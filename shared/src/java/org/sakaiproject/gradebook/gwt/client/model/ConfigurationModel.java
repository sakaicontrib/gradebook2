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

package org.sakaiproject.gradebook.gwt.client.model;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;

import com.extjs.gxt.ui.client.data.BaseModel;

public class ConfigurationModel extends BaseModel {

	private static final long serialVersionUID = 1L;

	private Long gradebookId;
	private String userUid;
	
	public ConfigurationModel() {
		
	}
	
	public ConfigurationModel(Long gradebookId) {
		this.gradebookId = gradebookId;
	}
	
	public Long getGradebookId() {
		return gradebookId;
	}

	public void setGradebookId(Long gradebookId) {
		this.gradebookId = gradebookId;
	}

	public String getUserUid() {
		return userUid;
	}

	public void setUserUid(String userUid) {
		this.userUid = userUid;
	}

	public boolean isAscending(String gridId) {
		String isAsc = get(getAscendingId(gridId));
		
		return DataTypeConversionUtil.checkBoolean(Boolean.valueOf(isAsc));
	}
	
	public boolean isColumnHidden(String gridId, String columnId) {
		return isColumnHidden(gridId, columnId, true);
	}
	
	public boolean isColumnHidden(String gridId, String columnId, boolean valueForNull) {
		String hidden = get(getColumnHiddenId(gridId, columnId));
		
		if (hidden == null)
			return valueForNull;
		
		return Boolean.valueOf(hidden).booleanValue();
	}
	
	public int getColumnWidth(String gridId, String columnId, String name) {
		String columnWidth = get(getColumnWidthId(gridId, columnId));
		
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
	
	public int getPageSize(String gridId) {
		String pageSize = get(getPageSizeId(gridId));
		
		if (pageSize == null)
			return -1;
		
		return Integer.parseInt(pageSize);
	}
	
	public void setPageSize(String gridId, Integer pageSize) {
		set(getPageSizeId(gridId), String.valueOf(pageSize));
	}
	
	public List<String> getSelectedMultigradeColumns() {
		String value = get(AppConstants.SELECTED_COLUMNS);
		
		List<String> columnIds = new ArrayList<String>();
		
		if (value != null) {
			String[] tokens = value.split(":");
			for (int i=0;i<tokens.length;i++) {
				columnIds.add(tokens[i]);
			}
		}
		
		return columnIds;
	}
	
	public String getSortField(String gridId) {
		return get(getSortFieldId(gridId));
	}
	
	
	public void setColumnHidden(String gridId, String columnId, Boolean isHidden) {
		set(getColumnHiddenId(gridId, columnId), String.valueOf(isHidden));
	}
	
	public void setColumnWidth(String gridId, String columnId, Integer width) {
		set(getColumnWidthId(gridId, columnId), String.valueOf(width));
	}
	
	public void setSortDirection(String gridId, Boolean isAscending) {
		set(getAscendingId(gridId), String.valueOf(isAscending));
	}
	
	public void setSortField(String gridId, String sortField) {
		set(getSortFieldId(gridId), sortField);
	}
	
	private String getAscendingId(String gridId) {
		return new StringBuilder().append(gridId).append(AppConstants.SORT_DIRECTION).toString();
	}
	
	public static String getColumnHiddenId(String gridId, String columnId) {
		return new StringBuilder().append(gridId).append(":").append(columnId).append(AppConstants.HIDDEN_SUFFIX).toString();
	}
	
	private String getColumnWidthId(String gridId, String columnId) {
		return new StringBuilder().append(gridId).append(":").append(columnId).append(AppConstants.WIDTH_SUFFIX).toString();
	}
	
	private String getPageSizeId(String gridId) {
		return new StringBuilder().append(gridId).append(AppConstants.PAGE_SIZE).toString();
	}
	
	private String getSortFieldId(String gridId) {
		return new StringBuilder().append(gridId).append(AppConstants.SORT_FIELD).toString();
	}
	
	
}
