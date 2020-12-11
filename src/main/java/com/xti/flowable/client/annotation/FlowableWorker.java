package com.xti.flowable.client.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ TYPE, METHOD })
@Inherited
public @interface FlowableWorker {

	public String topic();

	public String workerId();

	public int maxTasks() default 10;

	public String lockDuration() default "PT10M";
	
	public int numberOfRetries() default 10;
	
	public String scopeType() default "";
}
