/**********************************************************************************
*
* $Id$
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

package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import org.sakaiproject.gradebook.gwt.client.model.PermissionEntryModel;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public class PermissionDeleteCellRenderer implements GridCellRenderer<PermissionEntryModel> {

	public String render(PermissionEntryModel model, String property, 
			ColumnData config, int rowIndex, int colIndex, 
			ListStore<PermissionEntryModel> store, Grid<PermissionEntryModel> grid) {
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<span class=\"gbCellClickable\">");
		stringBuilder.append(model.get(property));
		stringBuilder.append("</span>");
		return stringBuilder.toString();
	}
}
