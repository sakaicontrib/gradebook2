package org.sakaiproject.gradebook.gwt.client;

public class ConfigUtil {

	public static String getAscendingId(String gridId) {
		return new StringBuilder().append(gridId).append(AppConstants.SORT_DIRECTION).toString();
	}

	public static String getColumnHiddenId(String gridId, String columnId) {
		return new StringBuilder().append(gridId).append(":").append(columnId).append(AppConstants.HIDDEN_SUFFIX).toString();
	}

	public static String getColumnWidthId(String gridId, String columnId) {
		return new StringBuilder().append(gridId).append(":").append(columnId).append(AppConstants.WIDTH_SUFFIX).toString();
	}

	public static String getPageSizeId(String gridId) {
		return new StringBuilder().append(gridId).append(AppConstants.PAGE_SIZE).toString();
	}

	public static String getSortFieldId(String gridId) {
		return new StringBuilder().append(gridId).append(AppConstants.SORT_FIELD).toString();
	}

}
