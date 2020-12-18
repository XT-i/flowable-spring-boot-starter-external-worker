package com.xti.flowable.client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(FlowableProperties.class)
@EnableScheduling
@ComponentScan(basePackages = {"com.xti.flowable.client"})
public class FlowableAutoConfiguration {

	@Autowired
	private FlowableProperties flowableProperties;
	
	private static final String ENGINE_API_PATH = "/acquire/jobs";

	@Bean
	public FlowableConfig flowableConfig() {
		FlowableConfig flowableConfig = new FlowableConfig();
		flowableConfig.setUrl(flowableProperties.getUrl() + ENGINE_API_PATH);
		flowableConfig.setWorkerId(flowableProperties.getWorkerId());
		return flowableConfig;
	}
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
}
