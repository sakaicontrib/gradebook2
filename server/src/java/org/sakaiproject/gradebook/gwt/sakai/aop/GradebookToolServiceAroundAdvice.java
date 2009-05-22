package org.sakaiproject.gradebook.gwt.sakai.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


public class GradebookToolServiceAroundAdvice implements MethodInterceptor {

	public Object invoke(MethodInvocation invocation) throws Throwable {
		
		Object obj = null;
				
		String methodName = invocation.getMethod().getName();
		
		long start = System.currentTimeMillis();
		
		obj = invocation.proceed();
			
		long end = System.currentTimeMillis();

		System.out.println("Execution time for method (" + methodName + ") was "+(end-start)+" ms.");
		
		return obj;
	}

}
