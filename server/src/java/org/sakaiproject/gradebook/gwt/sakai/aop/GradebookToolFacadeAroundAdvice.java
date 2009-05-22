package org.sakaiproject.gradebook.gwt.sakai.aop;

import java.util.UUID;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.sakaiproject.gradebook.gwt.client.action.PageRequestAction;


public class GradebookToolFacadeAroundAdvice implements MethodInterceptor {

	public Object invoke(MethodInvocation invocation) throws Throwable {

		String random = UUID.randomUUID().toString();
		String methodName = invocation.getMethod().getName();
		Object arguments [] = invocation.getArguments();
		String entityName = null;
		
		System.out.print("# : GTF start -> " + methodName + "(");
		for(Object obj : arguments) {
			if (obj != null && obj.getClass() != null) {
				System.out.print(obj.getClass().getName() + " ");
				if(obj instanceof PageRequestAction) {
					entityName = ((PageRequestAction) obj).getEntityType().name();
				}
			}
		}
		if(null == entityName) {
			
			System.out.println(")");
		}
		else {
		
			System.out.println(") : EntityName = " + entityName);
		}
		
		Object obj = null;
		long start = System.currentTimeMillis();
		
		obj = invocation.proceed();
		
		long end = System.currentTimeMillis();

		System.out.println("# : GTF end -> " + methodName + " : execution time for method (" + methodName + ") was "+(end-start)+" ms.");
		
		return obj;
	}

}
