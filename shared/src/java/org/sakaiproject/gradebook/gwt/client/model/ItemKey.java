/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model;

import java.util.Date;

public enum ItemKey {
	ID, NAME, WEIGHT, EQUAL_WEIGHT, 
	EXTRA_CREDIT, 
	INCLUDED, REMOVED, GRADEBOOK, 
	DROP_LOWEST, 
	CATEGORY_NAME, CATEGORY_ID(Long.class), DUE_DATE(Date.class), 
	POINTS, POINTS_STRING, 
	RELEASED, NULLSASZEROS, SOURCE, ITEM_TYPE, 
	PERCENT_COURSE_GRADE, PERCENT_COURSE_GRADE_STRING,
	PERCENT_CATEGORY, PERCENT_CATEGORY_STRING, 
	IS_PERCENTAGE, 
	STUDENT_MODEL_KEY,
	ASSIGNMENT_ID(Long.class), DATA_TYPE, CATEGORYTYPE(CategoryType.class),
	GRADETYPE(GradeType.class), RELEASEGRADES, 
	RELEASEITEMS,
	ITEM_ORDER, GRADESCALEID, 
	EXTRA_CREDIT_SCALED,
	DO_RECALCULATE_POINTS,
	ENFORCE_POINT_WEIGHTING, SHOWMEAN,
	SHOWMEDIAN, SHOWMODE, SHOWRANK,
	SHOWITEMSTATS,CHILDREN;
	
	private Class<?> type;
	
	private ItemKey() {
		
	}
	
	private ItemKey(Class<?> type) {
		this.type = type;
	}
	
	public Class<?> getType() {
		return type;
	}
}