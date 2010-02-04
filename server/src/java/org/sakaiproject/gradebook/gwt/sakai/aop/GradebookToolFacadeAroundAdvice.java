/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009 The Regents of the University of California
 *
 * Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.gradebook.gwt.sakai.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;


public class GradebookToolFacadeAroundAdvice implements MethodInterceptor {

	public Object invoke(MethodInvocation invocation) throws Throwable {

		String methodName = invocation.getMethod().getName();
		Object arguments [] = invocation.getArguments();
		String entityName = null;

		System.out.print("# : GTF start -> " + methodName + "(");
		for(Object obj : arguments) {
			if (obj != null && obj.getClass() != null) {
				System.out.print(obj.getClass().getName() + " ");
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
