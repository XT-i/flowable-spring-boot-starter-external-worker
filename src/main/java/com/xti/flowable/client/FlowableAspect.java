package com.xti.flowable.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.xti.flowable.client.annotation.FlowableWorker;
import com.xti.flowable.client.config.FlowableConfig;


@Aspect
@Component
public class FlowableAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(FlowableAspect.class);
	
	@Autowired
	private FlowableConfig flowableConfig;
	
	@Autowired
	private ApplicationContext context;
	
	@Around("@annotation(com.xti.flowable.client.annotation.FlowableWorker)")
	public Object logMethodCall(ProceedingJoinPoint jp) throws Throwable {
		String methodName = jp.getSignature().getName();
		LOGGER.info("Before " + methodName);

		MethodSignature signature = (MethodSignature) jp.getSignature();
		Method method = signature.getMethod();

		FlowableWorker worker = method.getAnnotation(FlowableWorker.class);
				
		//define place of flowableconnection arg
		Integer connectionPlace = determinePlaceOfFlowableConnection(method);
		
		Object[] args =jp.getArgs();
		for(int i=0;i<args.length;i++) {
			if(connectionPlace == i) {
				Object arg = args[i];
				if(arg == null)
					arg = new FlowableConnection();
				if(arg instanceof FlowableConnection) {
					FlowableConnection connection = (FlowableConnection)arg;

					connection.setClient(createClient(worker));
					Field restField = connection.getClass().getDeclaredField("restTemplate");
					restField.setAccessible(true);
					
					restField.set(arg, context.getBean(RestTemplate.class));
				}
				args[i] = arg;
			}
		}
		
		Object proceed = jp.proceed(args);
		return proceed;
	}
	
	private Integer determinePlaceOfFlowableConnection(Method method){
		Integer result = -1;
		Class<?>[] types = method.getParameterTypes();
		for(int j=0;j<types.length;j++) {
			if(types[j].equals(FlowableConnection.class)) {
				result = j;
			}
		}
		return result;
	}

	private WorkerClient createClient(FlowableWorker worker) {
		return WorkerClientBuilder.create()
								  .toUrl(flowableConfig.getUrl())
								  .topic(worker.topic())
								  .workerId(flowableConfig.getWorkerId())
								  .maxTasks(worker.maxTasks())
								  .lockDuration(worker.lockDuration())
								  .numberOfRetries(worker.numberOfRetries())
								  .scopeType(worker.scopeType())
								  .build();
	}
}
