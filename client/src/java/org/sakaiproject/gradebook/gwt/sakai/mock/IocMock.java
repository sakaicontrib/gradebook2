package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.util.HashMap;
import java.util.Map;

public class IocMock {

	private static IocMock instance = null;
	private Map<String, Object> lookup = new HashMap<String, Object>();

	protected IocMock() { }

	public static IocMock getInstance() {

		if (instance == null) {
			instance = new IocMock();
		}
		return instance;
	}
	
	public Object getClassInstance(String className) {
		return lookup.get(className);
	}
	
	public void registerClassInstance(String className, Object object) {
		lookup.put(className, object);
	}

}
