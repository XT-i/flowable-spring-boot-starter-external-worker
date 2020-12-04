package com.xti.flowable.client;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.xti.flowable.client.annotation.FlowableWorker;
import com.xti.flowable.client.config.FlowableConfig;

@Component
class FlowableWorkerPostProcessor implements BeanPostProcessor{

	private static final Log LOGGER = LogFactory.getLog(FlowableWorkerPostProcessor.class);
	
	@Autowired
	FlowableConfig flowableConfig;

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		Class<?> targetClass = bean.getClass();
		FlowableWorker[] annotations = targetClass.getAnnotationsByType(FlowableWorker.class);
		if(annotations.length>0) {
			FlowableWorker worker = annotations[0];
			Field[] fields = targetClass.getDeclaredFields();
			for(int i = 0;i<fields.length;i++) {
				if(fields[i].getType().isAssignableFrom(FlowableConnection.class)) {
					Field field = fields[i];
					try {
						field.setAccessible(true);
						Object o = field.get(bean);
											
						Method method = o.getClass().getDeclaredMethod("setClient", WorkerClient.class);
						
						if(method != null) {																				
							if(flowableConfig.getUrl() != null) {
								WorkerClient client = WorkerClientBuilder.create()
										.toUrl(flowableConfig.getUrl())
										.topic(worker.topic())
										.workerId(worker.workerId())
										.maxTasks(worker.maxTasks())
										.lockDuration(worker.lockDuration())
										.numberOfRetries(worker.numberOfRetries())
										.scopeType(worker.scopeType())
										.build();
								method.invoke(o,client);
								LOGGER.info("Worker " + beanName + " instantiated with WorkerClient");
							}else {
								LOGGER.warn("No Flowable url defined! Using the default.");
							}
						}
					} catch (Exception e) {	
						LOGGER.warn(e.getMessage());
					}
				}
			}
		}

		return bean;
	}
}
