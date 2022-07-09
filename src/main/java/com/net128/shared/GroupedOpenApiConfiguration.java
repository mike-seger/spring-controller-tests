package com.net128.shared;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
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
	private final String mainGroup;
	@SuppressWarnings("FieldCanBeLocal")
	private final String defaultMainGroupName = "main";
	private final static String sysPropUrlsPrimaryName =
		"springdoc.swagger-ui.urlsPrimaryName";

	public GroupedOpenApiConfiguration(
			ApplicationContext context,
			ConfigurableListableBeanFactory beanFactory,
			@Value("${springdoc.swagger-ui.urlsPrimaryName:}") String mainGroup) {
		this.beanFactory = beanFactory;
		this.mainGroup = mainGroup;
		this.context = context;
	}

	private void registerExtraGroupedOpenApis(List<String> packageNames, int namePosition) throws BeansException {
		packageNames.forEach(p -> beanFactory.registerSingleton(
			GroupedOpenApiConfiguration.class.getSimpleName()+
			"-" + p, apiGroup(p,namePosition)));
	}

	private GroupedOpenApi apiGroup(String packageName, int namePosition) {
		int lastDotPos = packageName.lastIndexOf('.');
		if(lastDotPos>=0) {
			if(lastDotPos+1<namePosition) namePosition=lastDotPos+1;
		} else namePosition=0;
		String groupName = packageName.substring(namePosition);
		log.info("Created GroupedOpenApi: {}", groupName);
		return GroupedOpenApi.builder().group(groupName)
			.packagesToScan(new String [] { packageName }).build();
	}

	private Class<?> getMainClass()	{
		List<Class<?>> applicationClasses = Stream.of(context.getBeanNamesForAnnotation(SpringBootApplication.class))
			.map(n -> context.getBean(n).getClass()).collect(Collectors.toList());
		if(applicationClasses.size()<1) {
			log.error("No SpringBootApplication annotated class found. Returning {}", getMainClass());
			return getClass();
		}
		log.info("Return first SpringBootApplication from: {}.", applicationClasses);
		return applicationClasses.get(0);
	}

	@Bean
	public GroupedOpenApi mainApi() {
		var controllerClasses = new Reflections(getMainClass().getPackageName())
				.getTypesAnnotatedWith(RestController.class);
		log.info("RestControllers found: {}", controllerClasses);

		List<String> packageNames = controllerClasses.stream().map(
			Class::getPackageName).collect(Collectors.toList());
		if(packageNames.size()==0) {
			log.error("No controller package classes found under: {}",
				getMainClass().getPackageName());
			return apiGroup(defaultMainGroupName, 0);
		}
		String commonParentPackage = StringUtils.getCommonPrefix(
			packageNames.toArray(new String []{}));
		String mainPackage = packageNames.remove(0);
		registerExtraGroupedOpenApis(packageNames, commonParentPackage.length());
		if(mainGroup.length()==0)
			System.setProperty(sysPropUrlsPrimaryName,
				mainPackage.substring(commonParentPackage.length()));
		return apiGroup(mainPackage, commonParentPackage.length());
	}
}
