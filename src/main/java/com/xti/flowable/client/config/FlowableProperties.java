package com.xti.flowable.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "flowable.external")
public class FlowableProperties {

	private String url = "http://localhost:8080";

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		if(url.endsWith("/")) {
			url = url.substring(0,url.length()-1);
		}
		this.url = url;
	}

}
