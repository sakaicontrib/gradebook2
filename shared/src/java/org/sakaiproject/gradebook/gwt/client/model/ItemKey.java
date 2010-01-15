/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model;

import java.util.Date;
import java.util.List;

public enum ItemKey {
	ID(String.class), NAME(String.class), WEIGHT(Double.class), EQUAL_WEIGHT(Boolean.class), 
	EXTRA_CREDIT(Boolean.class), 
	INCLUDED(Boolean.class), REMOVED(Boolean.class), GRADEBOOK(String.class), 
	DROP_LOWEST(Integer.class), 
	CATEGORY_NAME(String.class), CATEGORY_ID(Long.class), DUE_DATE(Date.class), 
	POINTS(Double.class), POINTS_STRING(String.class), 
	RELEASED(Boolean.class), NULLSASZEROS(Boolean.class), SOURCE(String.class), ITEM_TYPE(String.class), 
	PERCENT_COURSE_GRADE(Double.class), PERCENT_COURSE_GRADE_STRING(String.class),
	PERCENT_CATEGORY(Double.class), PERCENT_CATEGORY_STRING(String.class), 
	IS_PERCENTAGE(Boolean.class), 
	STUDENT_MODEL_KEY,
	ASSIGNMENT_ID(Long.class), DATA_TYPE, CATEGORYTYPE(CategoryType.class),
	GRADETYPE(GradeType.class), RELEASEGRADES(Boolean.class), 
	RELEASEITEMS(Boolean.class),
	ITEM_ORDER(Integer.class), GRADESCALEID(Long.class), 
	EXTRA_CREDIT_SCALED(Boolean.class),
	DO_RECALCULATE_POINTS(Boolean.class),
	ENFORCE_POINT_WEIGHTING(Boolean.class), SHOWMEAN(Boolean.class),
	SHOWMEDIAN(Boolean.class), SHOWMODE(Boolean.class), SHOWRANK(Boolean.class),
	SHOWITEMSTATS(Boolean.class),CHILDREN(List.class), IS_ACTIVE(Boolean.class),
	IS_EDITABLE(Boolean.class), IS_CHECKED(Boolean.class);
	
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