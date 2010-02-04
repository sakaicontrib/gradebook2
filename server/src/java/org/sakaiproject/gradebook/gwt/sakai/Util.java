package org.sakaiproject.gradebook.gwt.sakai;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;
import org.sakaiproject.gradebook.gwt.server.DataTypeConversionUtil;

public class Util {

	private static final Log log = LogFactory.getLog(Util.class);
	
	public static Map<String, Object> buildItemMap(String id, String name, CategoryType categoryType, GradeType gradeType, Long gradeScaleId,
			Boolean doReleaseGrades, Boolean doReleaseItems, Boolean doScaleExtraCredit, Boolean doShowMean, Boolean doShowMedian,
			Boolean doShowMode, Boolean doShowRank, Boolean doShowItemStatistics) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(ItemKey.ID.name(), id);
		map.put(ItemKey.NAME.name(), name);
		map.put(ItemKey.ITEM_TYPE.name(), ItemType.GRADEBOOK);
		map.put(ItemKey.CATEGORYTYPE.name(), categoryType);
		map.put(ItemKey.GRADETYPE.name(), gradeType);
		map.put(ItemKey.RELEASEGRADES.name(), doReleaseGrades);
		map.put(ItemKey.RELEASEITEMS.name(), doReleaseItems);
		map.put(ItemKey.GRADESCALEID.name(), gradeScaleId);
		map.put(ItemKey.EXTRA_CREDIT_SCALED.name(), doScaleExtraCredit);
		map.put(ItemKey.SHOWMEAN.name(), doShowMean);
		map.put(ItemKey.SHOWMEDIAN.name(), doShowMedian);
		map.put(ItemKey.SHOWMODE.name(), doShowMode);
		map.put(ItemKey.SHOWRANK.name(), doShowRank);
		map.put(ItemKey.SHOWITEMSTATS.name(), doShowItemStatistics);
		
		return map;
	}
	
	public static Map<String, Object> buildItemMap(String id, String name, String gradebookName, Long categoryId, Double percentCourseGrade,
			Integer dropLowest, Integer sortOrder, Boolean doEqualWeight, Boolean doExtraCredit, Boolean doInclude, Boolean doRemove,
			Boolean doRelease, Boolean isEditable, Boolean doPointWeighting) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(ItemKey.ID.name(), id);
		map.put(ItemKey.NAME.name(), name);
		map.put(ItemKey.ITEM_TYPE.name(), ItemType.CATEGORY);
		map.put(ItemKey.GRADEBOOK.name(), gradebookName);
		map.put(ItemKey.CATEGORY_ID.name(), categoryId);
		map.put(ItemKey.WEIGHT.name(), percentCourseGrade);
		map.put(ItemKey.EQUAL_WEIGHT.name(), doEqualWeight);
		map.put(ItemKey.EXTRA_CREDIT.name(), doExtraCredit);
		map.put(ItemKey.INCLUDED.name(), doInclude);
		map.put(ItemKey.DROP_LOWEST.name(), dropLowest);
		map.put(ItemKey.REMOVED.name(), doRemove);
		map.put(ItemKey.RELEASED.name(), doRelease);
		map.put(ItemKey.PERCENT_COURSE_GRADE.name(), percentCourseGrade);	
		map.put(ItemKey.IS_EDITABLE.name(), isEditable);
		map.put(ItemKey.ITEM_ORDER.name(), sortOrder);
		map.put(ItemKey.ENFORCE_POINT_WEIGHTING.name(), doPointWeighting);
		
		return map;
	}
	
	public static Map<String, Object> buildItemMap(String id, String name, String categoryName, Long categoryId, Long itemId,
			Double points, Double percentCategory, Double percentCourseGrade, Double itemWeight, Boolean doRelease, Boolean doInclude,
			Date dueDate, Boolean doExtraCredit, Boolean doRemove, String source, String dataType, String learnerKey, Integer itemOrder, 
			Boolean doNullsAsZeros) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(ItemKey.ID.name(), id);
		map.put(ItemKey.NAME.name(), name);
		map.put(ItemKey.ITEM_TYPE.name(), ItemType.ITEM);
		map.put(ItemKey.CATEGORY_NAME.name(), categoryName);
		map.put(ItemKey.CATEGORY_ID.name(), categoryId);
		map.put(ItemKey.ASSIGNMENT_ID.name(), itemId);
		map.put(ItemKey.WEIGHT.name(), itemWeight);
		map.put(ItemKey.RELEASED.name(), doRelease);
		map.put(ItemKey.INCLUDED.name(), doInclude);
		map.put(ItemKey.DUE_DATE.name(), dueDate);
		map.put(ItemKey.POINTS.name(), points);
		map.put(ItemKey.EXTRA_CREDIT.name(), doExtraCredit);
		map.put(ItemKey.REMOVED.name(), doRemove);
		map.put(ItemKey.SOURCE.name(), source);
		map.put(ItemKey.DATA_TYPE.name(), dataType);
		map.put(ItemKey.STUDENT_MODEL_KEY.name(), learnerKey);
		map.put(ItemKey.ITEM_ORDER.name(), itemOrder);
		map.put(ItemKey.PERCENT_CATEGORY.name(), percentCategory);
		map.put(ItemKey.PERCENT_COURSE_GRADE.name(), percentCourseGrade);
		map.put(ItemKey.NULLSASZEROS.name(), doNullsAsZeros);

		return map;
	}
	
	public static void makeChild(Map<String, Object> parent, Map<String, Object> child) {
		
		assert(parent != null);
		assert(child != null);
		
		List<Map<String,Object>> childrenList = (List<Map<String,Object>>)parent.get(ItemKey.CHILDREN.name());
		
		if (childrenList == null)
			childrenList = new ArrayList<Map<String,Object>>();
		
		if (!childrenList.contains(child))
			childrenList.add(child);
		
		parent.put(ItemKey.CHILDREN.name(), childrenList);
	}
	
	public static void sanitize(Map<String, Object> map) {

		for (ItemKey key : EnumSet.allOf(ItemKey.class)) {
			if (key.getType() != null) {
				try {
					Object rawValue = map.get(key.name());
					Object value = rawValue;
					
					if (rawValue != null) {
						if (key.getType().equals(Long.class)) 
							value = Long.valueOf(rawValue.toString());
						else if (key.getType().equals(Double.class))
							value = Double.valueOf(rawValue.toString());
						else if (key.getType().equals(Date.class))
							value = new Date((Long)rawValue);
					}
					
					map.put(key.name(), value);
				} catch (ClassCastException cce) {
					log.info("Unable to cast value for " + key.name() + " as " + key.getType().getCanonicalName());
				}
			} 
		}
	}
	
	public static void setPoints(Map<String, Object> map, Double points) {
		map.put(ItemKey.POINTS.name(), points);
		if (points != null)
			map.put(ItemKey.POINTS_STRING.name(), String.valueOf(points));
	}
	
	public static void setPercentCategory(Map<String, Object> map, Double percent) {
		map.put(ItemKey.PERCENT_CATEGORY.name(), percent);
		if (percent != null)
			map.put(ItemKey.PERCENT_CATEGORY_STRING.name(), String.valueOf(percent));
	}
	
	public static Boolean toBoolean(Object object) {
		Boolean b = null;
		
		if (object != null) {
			if (object instanceof Boolean)
				b = (Boolean)object;
		}
		
		return b;
	}
	
	public static boolean toBooleanPrimitive(Object object) {
		assert(object instanceof Boolean);
		
		boolean b = DataTypeConversionUtil.checkBoolean((Boolean)object);
		return b;
	}
	
	public static CategoryType toCategoryType(Object object) {
		CategoryType t = null;
		
		if (object != null) {
			if (object instanceof CategoryType)
				t = (CategoryType)object;
			else if (object instanceof String)
				t = CategoryType.valueOf((String)object);
		}
		
		return t;
	}
	
	public static Date toDate(Object object) {
		Date d = null;
		
		if (object != null) {
			if (object instanceof Date)
				d = (Date)object;
			else if (object instanceof Long)
				d = new Date((Long)object);
		}
		
		return d;
	}
	
	public static Double toDouble(Object object) {
		assert(object instanceof Number);
		
		Double d = null;
		if (object instanceof Integer)
			d = object == null ? null : Double.valueOf(((Integer)object).doubleValue());
		else if (object instanceof Long)
			d = object == null ? null : Double.valueOf(((Long)object).doubleValue());
		else if (object instanceof Double)
			d = (Double)object;
		
		return d;
	}
	
	public static double toDouble(Map<String, Object> map, String property) {
		double d = 0d;
		
		if (map != null) {
			Double value = (Double)map.get(property);
			if (value != null)
				d = value.doubleValue();
		}
	
		return d;
	}
	
	public static GradeType toGradeType(Object object) {
		GradeType t = null;
		
		if (object != null) {
			if (object instanceof GradeType)
				t = (GradeType)object;
			else if (object instanceof String)
				t = GradeType.valueOf((String)object);
		}
		
		return t;
	}
	
	public static Integer toInteger(Object object) {
		Integer l = null;

		if (object != null) {
			if (object instanceof Integer)
				l = (Integer)object;
			else if (object instanceof String)
				l = Integer.valueOf((String)object);
		}
		
		return l;
	}
	
	public static ItemType toItemType(Object object) {
		ItemType t = null;
		
		if (object != null) {
			if (object instanceof ItemType)
				t = (ItemType)object;
			else if (object instanceof String)
				t = ItemType.valueOf((String)object);
		}
		
		return t;
	}
	
	public static Long toLong(Object object) {
		Long l = null;

		if (object != null) {
			if (object instanceof Integer)
				l = object == null ? null : Long.valueOf(((Integer)object).longValue());
			else if (object instanceof Long)
				l = (Long)object;
			else if (object instanceof String)
				l = Long.valueOf((String)object);
		}
		
		return l;
	}
	
	public static String toString(Object object) {
		String s = null;
		
		if (object != null) {
			if (object instanceof String)
				s = (String)object;
			else
				s = String.valueOf(object);
		}
		
		return s;
	}
}
