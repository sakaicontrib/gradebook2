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

package org.sakaiproject.gradebook.gwt.client.gxt;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.EditorSupport;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel.Callback;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel.Cell;

public class GbEditorGrid<M extends ModelData> extends EditorGrid<M> {

	public GbEditorGrid(ListStore<M> store, ColumnModel cm) {
		super(store, cm);
	}
	
	public Cell doWalkCells(int row, int col, int step, Callback callback, 
			boolean acceptNavs) {
		return this.walkCells(row, col, step, callback, acceptNavs);
	}
	
	public EditorSupport<M> getEditorSupport() {
	    return editSupport;
	}
	
}
