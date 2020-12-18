package com.xti.flowable.client.processor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.xti.flowable.client.FlowableHandler;
import com.xti.flowable.client.annotation.FlowableWorker;

@Component
class FlowableWorkerPostProcessor implements BeanPostProcessor{

	private static final Log LOGGER = LogFactory.getLog(FlowableWorkerPostProcessor.class);
	
	private Map<Method,Object> workerMethods = new HashMap<>();
	
	@Autowired
	private ApplicationContext context;

	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		Class<?> targetClass = bean.getClass();
		Method[] methods = targetClass.getMethods();
		for(Method method:methods) {
			FlowableWorker[] annotations = method.getAnnotationsByType(FlowableWorker.class);
			if(annotations.length>0) {
				LOGGER.info("Worker " + beanName + ":" + method.getName() + " Found!");
				workerMethods.put(method, bean);
				FlowableHandler handler = context.getBean(FlowableHandler.class);
				handler.setWorkerMethods(workerMethods);
			}
		}
		return bean;
	}
}
