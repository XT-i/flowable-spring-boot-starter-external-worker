package com.xti.flowable.client;

import org.springframework.stereotype.Component;

@Component
class WorkerClient {

	String url;
	String topic;
	String workerId;
	Integer maxTasks;
	String lockDuration;
	Integer numberOfRetries;
	String scopeType;


	protected WorkerClient(WorkerClientBuilder builder) {
		this.url = builder.url;
		this.topic = builder.topic;
		this.workerId = builder.workerId;
		this.maxTasks = builder.maxTasks;
		this.lockDuration = builder.lockDuration;
		this.numberOfRetries = builder.numberOfRetries;
		this.scopeType = builder.scopeType;
	}
	
}
