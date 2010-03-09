package org.sakaiproject.gradebook.gwt.client.gxt.model;

import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;

public class LearnerUtil {

	public static boolean isFixed(String property) {
		try {
			LearnerKey.valueOf(property);
			return true;
		} catch (IllegalArgumentException iae) {
			return false;
		}
	}
	
}
