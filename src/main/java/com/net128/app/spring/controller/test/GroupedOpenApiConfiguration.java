package com.net128.app.spring.controller.test;

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
	final Set<Class<?>> controllerClasses;
	final String mainGroup;

	public GroupedOpenApiConfiguration(
			ConfigurableListableBeanFactory beanFactory,
			@Value("${springdoc.swagger-ui.urlsPrimaryName:}") String mainGroup) {
		this.beanFactory = beanFactory;
		this.mainGroup = mainGroup;
		this.controllerClasses = new Reflections(getClass().getPackageName())
			.getTypesAnnotatedWith(RestController.class);
		log.info("{}", controllerClasses);
	}

	private void registerExtraGroupedOpenApis(List<String> packageNames, int namePosition) throws BeansException {
		packageNames.forEach(p -> beanFactory.registerSingleton(
			"GroupedOpenApi-" + p, apiGroup(p,namePosition)));
	}

	public GroupedOpenApi apiGroup(String packageName, int namePosition) {
		return GroupedOpenApi.builder().group(packageName.substring(namePosition))
			.packagesToScan(new String [] { packageName }).build();
	}

	@Bean
	public GroupedOpenApi mainApi() {
		List<String> packageNames = controllerClasses.stream().map(
			Class::getPackageName).collect(Collectors.toList());
		String commonParentPackage = StringUtils.getCommonPrefix(
			packageNames.toArray(new String []{}));
		String mainPackage = packageNames.remove(0);
		registerExtraGroupedOpenApis(packageNames, commonParentPackage.length());
		if(mainGroup.length()==0)
			System.setProperty("springdoc.swagger-ui.urlsPrimaryName",
				mainPackage.substring(commonParentPackage.length()));
		return apiGroup(mainPackage, commonParentPackage.length());
	}
}
