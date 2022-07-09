package com.net128.shared;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class GroupedOpenApiConfiguration {
	private final ConfigurableListableBeanFactory beanFactory;
	private final Set<Class<?>> controllerClasses;
	private final String mainGroup;
	@SuppressWarnings("FieldCanBeLocal")
	private final String defaultMainGroupName = "main";
	private final static String sysPropUrlsPrimaryName =
		"springdoc.swagger-ui.urlsPrimaryName";

	public GroupedOpenApiConfiguration(
			ConfigurableListableBeanFactory beanFactory,
			@Value("${springdoc.swagger-ui.urlsPrimaryName:}") String mainGroup) {
		this.beanFactory = beanFactory;
		this.mainGroup = mainGroup;
		this.controllerClasses = new Reflections(getMainClass().getPackageName())
			.getTypesAnnotatedWith(RestController.class);
		log.info("RestControllers found: {}", controllerClasses);
	}

	private void registerExtraGroupedOpenApis(List<String> packageNames, int namePosition) throws BeansException {
		packageNames.forEach(p -> beanFactory.registerSingleton(
			GroupedOpenApiConfiguration.class.getSimpleName()+
			"-" + p, apiGroup(p,namePosition)));
	}

	private GroupedOpenApi apiGroup(String packageName, int namePosition) {
		String groupName = packageName.substring(namePosition);
		log.info("Created GroupedOpenApi: {}", groupName);
		return GroupedOpenApi.builder().group(groupName)
			.packagesToScan(new String [] { packageName }).build();
	}

	private Class<?> getMainClass()	{
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		if (trace.length > 0) {
			try {
				return Class.forName(trace[trace.length - 1].getClassName());
			} catch(Exception e) {
				log.error("Failed to determine main class", e);
			}
		} else {
			log.error("Failed to determine main class");
		}
		return getClass();
	}

	@Bean
	public GroupedOpenApi mainApi() {
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
