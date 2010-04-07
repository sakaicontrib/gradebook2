package org.sakaiproject.gradebook.gwt.sakai;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
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
		map.put(ItemKey.S_ID.name(), id);
		map.put(ItemKey.S_NM.name(), name);
		map.put(ItemKey.S_ITM_TYPE.name(), ItemType.GRADEBOOK);
		map.put(ItemKey.C_CTGRY_TYPE.name(), categoryType);
		map.put(ItemKey.G_GRD_TYPE.name(), gradeType);
		map.put(ItemKey.B_REL_GRDS.name(), doReleaseGrades);
		map.put(ItemKey.B_REL_ITMS.name(), doReleaseItems);
		map.put(ItemKey.L_GRD_SCL_ID.name(), gradeScaleId);
		map.put(ItemKey.B_SCL_X_CRDT.name(), doScaleExtraCredit);
		map.put(ItemKey.B_SHW_MEAN.name(), doShowMean);
		map.put(ItemKey.B_SHW_MEDIAN.name(), doShowMedian);
		map.put(ItemKey.B_SHW_MODE.name(), doShowMode);
		map.put(ItemKey.B_SHW_RANK.name(), doShowRank);
		map.put(ItemKey.B_SHW_ITM_STATS.name(), doShowItemStatistics);
		
		return map;
	}
	
	public static Map<String, Object> buildItemMap(String id, String name, String gradebookName, Long categoryId, Double percentCourseGrade,
			Integer dropLowest, Integer sortOrder, Boolean doEqualWeight, Boolean doExtraCredit, Boolean doInclude, Boolean doRemove,
			Boolean doRelease, Boolean isEditable, Boolean doPointWeighting) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(ItemKey.S_ID.name(), id);
		map.put(ItemKey.S_NM.name(), name);
		map.put(ItemKey.S_ITM_TYPE.name(), ItemType.CATEGORY);
		map.put(ItemKey.S_GB_NAME.name(), gradebookName);
		map.put(ItemKey.L_CTGRY_ID.name(), categoryId);
		map.put(ItemKey.D_WGHT.name(), percentCourseGrade);
		map.put(ItemKey.B_EQL_WGHT.name(), doEqualWeight);
		map.put(ItemKey.B_X_CRDT.name(), doExtraCredit);
		map.put(ItemKey.B_INCLD.name(), doInclude);
		map.put(ItemKey.I_DRP_LWST.name(), dropLowest);
		map.put(ItemKey.B_RMVD.name(), doRemove);
		map.put(ItemKey.B_RLSD.name(), doRelease);
		map.put(ItemKey.D_PCT_GRD.name(), percentCourseGrade);	
		map.put(ItemKey.B_EDITABLE.name(), isEditable);
		map.put(ItemKey.I_SRT_ORDR.name(), sortOrder);
		map.put(ItemKey.B_WT_BY_PTS.name(), doPointWeighting);
		
		return map;
	}
	
	public static Map<String, Object> buildItemMap(String id, String name, String categoryName, Long categoryId, Long itemId,
			Double points, Double percentCategory, Double percentCourseGrade, Double itemWeight, Boolean doRelease, Boolean doInclude,
			Date dueDate, Boolean doExtraCredit, Boolean doRemove, String source, String dataType, String learnerKey, Integer itemOrder, 
			Boolean doNullsAsZeros) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put(ItemKey.S_ID.name(), id);
		map.put(ItemKey.S_NM.name(), name);
		map.put(ItemKey.S_ITM_TYPE.name(), ItemType.ITEM);
		map.put(ItemKey.S_CTGRY_NAME.name(), categoryName);
		map.put(ItemKey.L_CTGRY_ID.name(), categoryId);
		map.put(ItemKey.L_ITM_ID.name(), itemId);
		map.put(ItemKey.D_WGHT.name(), itemWeight);
		map.put(ItemKey.B_RLSD.name(), doRelease);
		map.put(ItemKey.B_INCLD.name(), doInclude);
		map.put(ItemKey.W_DUE.name(), dueDate);
		map.put(ItemKey.D_PNTS.name(), points);
		map.put(ItemKey.B_X_CRDT.name(), doExtraCredit);
		map.put(ItemKey.B_RMVD.name(), doRemove);
		map.put(ItemKey.S_SOURCE.name(), source);
		map.put(ItemKey.S_DATA_TYPE.name(), dataType);
		map.put(ItemKey.O_LRNR_KEY.name(), learnerKey);
		map.put(ItemKey.I_SRT_ORDR.name(), itemOrder);
		map.put(ItemKey.D_PCT_CTGRY.name(), percentCategory);
		map.put(ItemKey.D_PCT_GRD.name(), percentCourseGrade);
		map.put(ItemKey.B_NLLS_ZEROS.name(), doNullsAsZeros);

		return map;
	}
	
	public static void makeChild(Map<String, Object> parent, Map<String, Object> child) {
		
		assert(parent != null);
		assert(child != null);
		
		List<Map<String,Object>> childrenList = (List<Map<String,Object>>)parent.get(ItemKey.A_CHILDREN.name());
		
		if (childrenList == null)
			childrenList = new ArrayList<Map<String,Object>>();
		
		if (!childrenList.contains(child))
			childrenList.add(child);
		
		parent.put(ItemKey.A_CHILDREN.name(), childrenList);
	}
	
	
	public static void setPoints(Map<String, Object> map, Double points) {
		map.put(ItemKey.D_PNTS.name(), points);
		if (points != null)
			map.put(ItemKey.S_PNTS.name(), String.valueOf(points));
	}
	
	public static void setPercentCategory(Map<String, Object> map, Double percent) {
		map.put(ItemKey.D_PCT_CTGRY.name(), percent);
		if (percent != null)
			map.put(ItemKey.S_PCT_CTGRY.name(), String.valueOf(percent));
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
	
	public static String buildCommentKey(String itemId) {
		return new StringBuilder(AppConstants.COMMENTED_FLAG).append(itemId).toString();
	}
	
	public static String buildCommentTextKey(String itemId) {
		return new StringBuilder(AppConstants.COMMENT_TEXT_FLAG).append(itemId).toString();
	}
	
	public static String buildDroppedKey(String itemId) {
		return new StringBuilder(AppConstants.DROP_FLAG).append(itemId).toString();
	}
	
	public static String buildExcusedKey(String itemId) {
		return new StringBuilder(AppConstants.EXCUSE_FLAG).append(itemId).toString();
	}
	
	public static String buildFailedKey(String itemId) {
		return new StringBuilder(AppConstants.FAILED_FLAG).append(itemId).toString();
	}
	
	public static String buildSuccessKey(String itemId) {
		return new StringBuilder(AppConstants.SUCCESS_FLAG).append(itemId).toString();
	}
	
	public static String unpackItemIdFromKey(String key) {
		if (key == null || key.length() < 5)
			return null;
		return key.substring(4);
	}
}
