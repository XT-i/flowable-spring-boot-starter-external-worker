package com.xti.flowable.client;

import java.util.List;

import com.xti.flowable.client.model.EngineVariable;

public class AcquiredJobBuilder {

	FlowableConnection connection;
	String id;
	List<EngineVariable> variables;
	
	static AcquiredJobBuilder create() {
		return new AcquiredJobBuilder();
	}
	
	protected AcquiredJobBuilder connection(FlowableConnection connection) {
		this.connection = connection;
		return this;
	}
	
	protected AcquiredJobBuilder id(String id) {
		this.id = id;
		return this;
	}
	
	protected AcquiredJobBuilder variables(List<EngineVariable> variables) {
		this.variables = variables;
		return this;
	}
	
	public AcquiredJob build() {
		return new AcquiredJob(this); 
	}

}
