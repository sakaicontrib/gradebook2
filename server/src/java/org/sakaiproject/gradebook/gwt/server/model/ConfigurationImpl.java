package org.sakaiproject.gradebook.gwt.server.model;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.ConfigUtil;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.model.Configuration;
import org.sakaiproject.gradebook.gwt.client.model.key.ConfigurationKey;
import org.sakaiproject.gradebook.gwt.server.Util;


public class ConfigurationImpl extends BaseModel implements Configuration {

	private static final long serialVersionUID = 1L;

	public ConfigurationImpl() {
		super();
	}
	
	public ConfigurationImpl(Long gradebookId) {
		this();
		setGradebookId(gradebookId);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#getColumnWidth(java.lang.String, java.lang.String, java.lang.String)
	 */
	public int getColumnWidth(String gridId, String columnId, String name) {
		String columnWidth = Util.toString(get(ConfigUtil.getColumnWidthId(gridId, columnId)));
		
		int cw = 200;
			
		if (columnWidth != null)
			cw = Integer.parseInt(columnWidth);
		else {
			if (name != null) {
				cw = name.length() * 7;
			}
			
			if (cw < 100)
				cw = 100;
		}
			
		return cw;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#getGradebookId()
	 */
	public Long getGradebookId() {
		return Util.toLong(get(ConfigurationKey.L_GB_ID.name()));
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#getPageSize(java.lang.String)
	 */
	public int getPageSize(String gridId) {
		String pageSize = Util.toString(get(ConfigUtil.getPageSizeId(gridId)));
		
		if (pageSize == null)
			return -1;
		
		return Integer.parseInt(pageSize);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#getSelectedMultigradeColumns()
	 */
	public List<String> getSelectedMultigradeColumns() {
		String value = Util.toString(get(AppConstants.SELECTED_COLUMNS));
		
		List<String> columnIds = new ArrayList<String>();
		
		if (value != null) {
			String[] tokens = value.split(":");
			for (int i=0;i<tokens.length;i++) {
				columnIds.add(tokens[i]);
			}
		}
		
		return columnIds;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#getSortField(java.lang.String)
	 */
	public String getSortField(String gridId) {
		return Util.toString(get(ConfigUtil.getSortFieldId(gridId)));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#getUserUid()
	 */
	public String getUserUid() {
		return Util.toString(get(ConfigurationKey.S_USER_UID.name()));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#isAscending(java.lang.String)
	 */
	public boolean isAscending(String gridId) {
		String isAsc = Util.toString(get(ConfigUtil.getAscendingId(gridId)));
		
		return DataTypeConversionUtil.checkBoolean(Boolean.valueOf(isAsc));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#isColumnHidden(java.lang.String, java.lang.String)
	 */
	public boolean isColumnHidden(String gridId, String columnId) {
		return isColumnHidden(gridId, columnId, true);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#isColumnHidden(java.lang.String, java.lang.String, boolean)
	 */
	public boolean isColumnHidden(String gridId, String columnId, boolean valueForNull) {
		String hidden = Util.toString(get(ConfigUtil.getColumnHiddenId(gridId, columnId)));
		
		if (hidden == null)
			return valueForNull;
		
		return Boolean.valueOf(hidden).booleanValue();
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#setColumnHidden(java.lang.String, java.lang.String, java.lang.Boolean)
	 */
	public void setColumnHidden(String gridId, String columnId, Boolean isHidden) {
		put(ConfigUtil.getColumnHiddenId(gridId, columnId), String.valueOf(isHidden));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#setColumnWidth(java.lang.String, java.lang.String, java.lang.Integer)
	 */
	public void setColumnWidth(String gridId, String columnId, Integer width) {
		put(ConfigUtil.getColumnWidthId(gridId, columnId), String.valueOf(width));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#setGradebookId(java.lang.Long)
	 */
	public void setGradebookId(Long gradebookId) {
		put(ConfigurationKey.L_GB_ID.name(), gradebookId);
	}
	
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#setPageSize(java.lang.String, java.lang.Integer)
	 */
	public void setPageSize(String gridId, Integer pageSize) {
		put(ConfigUtil.getPageSizeId(gridId), String.valueOf(pageSize));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#setSortDirection(java.lang.String, java.lang.Boolean)
	 */
	public void setSortDirection(String gridId, Boolean isAscending) {
		put(ConfigUtil.getAscendingId(gridId), String.valueOf(isAscending));
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#setSortField(java.lang.String, java.lang.String)
	 */
	public void setSortField(String gridId, String sortField) {
		put(ConfigUtil.getSortFieldId(gridId), sortField);
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.gradebook.gwt.client.model.Configuration#setUserUid(java.lang.String)
	 */
	public void setUserUid(String userUid) {
		put(ConfigurationKey.S_USER_UID.name(), userUid);
	}
	
	public boolean isClassicNavigation() {
		return Util.toBooleanPrimitive(get(ConfigurationKey.B_CLASSIC_NAV.name()));
	}

	public void setClassicNavigation(Boolean useClassicNavigation) {
		put(ConfigurationKey.B_CLASSIC_NAV.name(), useClassicNavigation);
	}	
}
