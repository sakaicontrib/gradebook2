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
package org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid;

import java.util.List;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

public class CustomColumnModel extends ColumnModel {

	private String gradebookUid;
	private String gridId;
	
	public CustomColumnModel(String gradebookUid, String gridId, List<ColumnConfig> columns) {
		super(columns);
		this.gradebookUid = gradebookUid;
		this.gridId = gridId;
	}

	@Override
	public void setHidden(int colIndex, boolean hidden) {
		super.setHidden(colIndex, hidden);
		ColumnConfig column = getColumn(colIndex);
		String columnId = column == null ? null : column.getId();
	}
	
}
