package com.xti.flowable.client;

import org.springframework.stereotype.Component;

@Component
class WorkerClientBuilder {

	String url;
	String topic;
	String workerId;
	Integer maxTasks;
	String lockDuration;
	Integer numberOfRetries;
	String scopeType;

	public static WorkerClientBuilder create() {
		return new WorkerClientBuilder();
	}

	public WorkerClientBuilder topic(String topic) {
		this.topic = topic;
		return this;
	}

	public WorkerClientBuilder workerId(String workerId) {
		this.workerId = workerId;
		return this;
	}

	public WorkerClientBuilder toUrl(String url) {
		this.url = url;
		return this;
	}

	public WorkerClientBuilder maxTasks(Integer maxTasks) {
		this.maxTasks = maxTasks;
		return this;
	}

	public WorkerClientBuilder lockDuration(String lockDuration) {
		this.lockDuration = lockDuration;
		return this;
	}

	public WorkerClientBuilder numberOfRetries(Integer numberOfRetries) {
		this.numberOfRetries = numberOfRetries;
		return this;
	}
	
	public WorkerClientBuilder scopeType(String scopeType) {
		this.scopeType = scopeType;
		return this;
	}
	public WorkerClient build() {
		WorkerClient client = new WorkerClient(this);
		return client;
	}
}
