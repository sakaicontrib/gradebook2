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

import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;


public class SpreadsheetModel extends EntityModel implements BeanModelTag {

	private static final long serialVersionUID = 1L;
	
	private String displayName;
	private List<ItemModel> headers;
	private List<StudentModel> rows;
	private boolean isPercentage;
	private List<String> results;
	private ItemModel gradebookItemModel;
	
	public SpreadsheetModel() {
		
	}

	public List<ItemModel> getHeaders() {
		return headers;
	}

	public void setHeaders(List<ItemModel> headers) {
		this.headers = headers;
	}

	public List<StudentModel> getRows() {
		return rows;
	}

	public void setRows(List<StudentModel> rows) {
		this.rows = rows;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public String getIdentifier() {
		return displayName;
	}

	public boolean isPercentage() {
		return isPercentage;
	}

	public void setPercentage(boolean isPercentage) {
		this.isPercentage = isPercentage;
	}

	public List<String> getResults() {
		return results;
	}

	public void setResults(List<String> results) {
		this.results = results;
	}

	public ItemModel getGradebookItemModel() {
		return gradebookItemModel;
	}

	public void setGradebookItemModel(ItemModel gradebookItemModel) {
		this.gradebookItemModel = gradebookItemModel;
	}
	
}
