package com.net128.shared;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@Slf4j
public class GroupedOpenApiConfiguration {
	private final ConfigurableListableBeanFactory beanFactory;
	private final ApplicationContext context;
	private String mainGroup;
	@SuppressWarnings("FieldCanBeLocal")
	private final String defaultMainGroupName = "main";
	private final static String propertyUrlsPrimaryName =
		"springdoc.swagger-ui.urlsPrimaryName";

	public GroupedOpenApiConfiguration(
		ApplicationContext context,
		ConfigurableListableBeanFactory beanFactory
	) {
		this.beanFactory = beanFactory;
		this.mainGroup = context.getEnvironment().getProperty(propertyUrlsPrimaryName);
		this.context = context;
	}

	private void registerExtraGroupedOpenApis(List<String> packageNames, int prefixLength) throws BeansException {
		packageNames.forEach(p -> beanFactory.registerSingleton(
			GroupedOpenApiConfiguration.class.getSimpleName()+
				"-" + p, apiGroup(p,prefixLength)));
	}

	private GroupedOpenApi apiGroup(String packageName, int prefixLength) {
		var lastDotPos = packageName.lastIndexOf('.');
		if(lastDotPos>=0) {
			if(lastDotPos+1<prefixLength) prefixLength=lastDotPos+1;
		} else prefixLength=0;
		var groupName = packageName.substring(prefixLength);
		log.info("Created GroupedOpenApi: {}", groupName);
		return GroupedOpenApi.builder().group(groupName)
			.packagesToScan(new String [] { packageName }).build();
	}

	private Class<?> getMainClass()	{
		var applicationClasses = Stream.of(context.getBeanNamesForAnnotation(SpringBootApplication.class))
			.map(n -> context.getBean(n).getClass()).collect(Collectors.toList());
		if(applicationClasses.size()<1) {
			log.error("No SpringBootApplication annotated class found. Returning {}", getMainClass());
			return getClass();
		}
		log.info("Return first SpringBootApplication from: {}.", applicationClasses);
		return applicationClasses.get(0);
	}

	@Bean
	public SwaggerUiConfigProperties swaggerUiConfig(SwaggerUiConfigProperties config) {
		config.setUrlsPrimaryName(System.getProperty(propertyUrlsPrimaryName));
		return config;
	}

	@Bean
	public GroupedOpenApi mainApi() {
		var packageNames = getPackageName();
		if(packageNames.size()==0) {
			log.error("No controller package classes found under: {}",
				getMainClass().getPackageName());
			return apiGroup(defaultMainGroupName, 0);
		}
		var commonPackagePrefix = StringUtils.getCommonPrefix(
			packageNames.toArray(new String []{}));
		var mainPackage = packageNames.remove(0);
		registerExtraGroupedOpenApis(packageNames, commonPackagePrefix.length());
		if(mainGroup==null || mainGroup.length()==0) mainGroup = mainPackage.substring(commonPackagePrefix.length());
		return apiGroup(mainPackage, commonPackagePrefix.length());
	}

	//@Bean
	//TODO this should work, but does not get used by swagger-ui
	@SuppressWarnings("unused")
	public List<GroupedOpenApi> groupedOpenApis() {
		var packageNames = getPackageName();
		if(packageNames.size()==0) {
			log.error("No controller package classes found under: {}",
					getMainClass().getPackageName());
			return List.of(apiGroup(defaultMainGroupName, 0));
		}
		var commonPackagePrefix = StringUtils.getCommonPrefix(
			packageNames.toArray(new String []{}));
		var mainPackage = packageNames.get(0);
		if(mainGroup==null || mainGroup.length()==0) mainGroup = mainPackage.substring(commonPackagePrefix.length());
		return packageNames.stream().map(p -> apiGroup(p,commonPackagePrefix.length())).collect(Collectors.toList());
	}

	private List<String> getPackageName() {
		var controllerClasses = new Reflections(getMainClass().getPackageName())
			.getTypesAnnotatedWith(RestController.class);
		log.info("RestControllers found: {}", controllerClasses);
		return controllerClasses.stream().map(
			Class::getPackageName).collect(Collectors.toList());
	}
}
