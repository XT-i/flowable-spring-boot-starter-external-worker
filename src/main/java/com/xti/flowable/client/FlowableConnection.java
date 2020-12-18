package com.xti.flowable.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.xti.flowable.client.model.AcquireJobsRequest;
import com.xti.flowable.client.model.AcquiredJobsRequest;
import com.xti.flowable.client.model.BpmnErrorRequest;
import com.xti.flowable.client.model.EngineVariable;
import com.xti.flowable.client.model.FailRequest;
import com.xti.flowable.client.model.JobCompleteRequest;

@Component
@Scope(scopeName=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FlowableConnection {

	private static final Logger LOGGER = LoggerFactory.getLogger(FlowableConnection.class);
	
	@Autowired
	private RestTemplate restTemplate;
	
	private WorkerClient client;
	
	void setClient(WorkerClient client) {
		this.client = client;
	}
	
	List<AcquiredJobsRequest> acquireJobs() {
		LOGGER.info("acquiring jobs for " + client.topic + ". Connecting to " + client.url);
		AcquireJobsRequest request = new AcquireJobsRequest();
		request.setTopic(client.topic);
		request.setLockDuration(client.lockDuration);
		request.setNumberOfTasks(client.maxTasks);
		request.setWorkerId(client.workerId);
		request.setNumberOfRetries(client.numberOfRetries);
		if(!client.scopeType.equals("")) {
			request.setScopeType(client.scopeType);	
		}
		ResponseEntity<AcquiredJobsRequest[]> response = restTemplate.postForEntity(client.url, request, AcquiredJobsRequest[].class);
		
		if(response.getStatusCodeValue() == 200) {
			return Arrays.asList(response.getBody());
		}else {
			LOGGER.warn("Error acquiring jobs " + response.getStatusCode().getReasonPhrase());
			return new ArrayList<AcquiredJobsRequest>();
		}
		
	}
	
	public void complete(String jobId) {
		complete(jobId, null);
	}

	public void complete(String jobId, List<EngineVariable> variables) {
		JobCompleteRequest completeRequest = new JobCompleteRequest(client.workerId);
		completeRequest.setVariables(variables);

		ResponseEntity<String> response = restTemplate.postForEntity(client.url + "/" + jobId + "/complete", completeRequest, String.class);
		if(response.getStatusCodeValue() == 204) {
			LOGGER.info("Worker " + client.workerId + " completed job " + jobId);
		}else{
			LOGGER.warn("Unable to mark job as complete " + response.getStatusCode().getReasonPhrase());
		}
		
	}
	
	public void bpmnError(String jobId) {
		bpmnError(jobId,null, null);
	}
	
	public void bpmnError(String jobId, List<EngineVariable> variables,String errorCode) {
		BpmnErrorRequest bpmnErrorRequest = new BpmnErrorRequest(client.workerId);
		bpmnErrorRequest.setVariables(variables);
		bpmnErrorRequest.setErrorCode(errorCode);

		ResponseEntity<String> response = restTemplate.postForEntity(client.url + "/" + jobId + "/bpmnError", bpmnErrorRequest, String.class);
		if(response.getStatusCodeValue() == 204) {
			LOGGER.info("Worker " + client.workerId + " finished job " + jobId + " with bpmnError");
		}else {
			LOGGER.warn("Unable to finish job with bpmnError " + response.getStatusCode().getReasonPhrase());
		}
	}
	
	public void cmmnTerminate(String jobId) {
		cmmnTerminate(jobId, null);
	}
	
	public void cmmnTerminate(String jobId, List<EngineVariable> variables) {
		JobCompleteRequest completeRequest = new JobCompleteRequest(client.workerId);
		completeRequest.setVariables(variables);

		ResponseEntity<String> response = restTemplate.postForEntity(client.url + "/" + jobId + "/cmmnTerminate", completeRequest, String.class);
		if(response.getStatusCodeValue() == 204) {
			LOGGER.info("Worker " + client.workerId + " terminated cmmn job " + jobId);
		}else {
			LOGGER.warn("Unable to terminate cmmn job " + response.getStatusCode().getReasonPhrase());
		}
	}
	
	public void fail(String jobId) {
		fail(jobId, null, null, null, null);
	}
	
	public void fail(String jobId,String errorMessage, String errorDetails, Integer retries, String retryTimeout) {
		FailRequest failRequest = new FailRequest(client.workerId);
		failRequest.setErrorDetails(errorDetails);
		failRequest.setErrorMessage(errorMessage);
		failRequest.setRetries(retries);
		failRequest.setRetryTimeout(retryTimeout);
		
		ResponseEntity<String> response = restTemplate.postForEntity(client.url + "/" + jobId + "/fail", failRequest, String.class);
		if(response.getStatusCodeValue() == 204) {
			LOGGER.info("Worker " + client.workerId + " finished job " + jobId + " with failure");
		}else {
			LOGGER.warn("Unable to mark job as failed " + response.getStatusCode().getReasonPhrase());
		}

	}
}
