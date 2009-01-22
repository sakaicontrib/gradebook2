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
package org.sakaiproject.gradebook.gwt.client.gxt.settings;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.SummaryColumnConfig;

public class SummaryCheckColumnConfig extends SummaryColumnConfig implements ComponentPlugin {

	protected Grid grid;

	public SummaryCheckColumnConfig() {
		super();
		init();
	}

	public SummaryCheckColumnConfig(String id, String name, int width) {
		super(id, name, width);
		init();
	}

	public void init(Component component) {
		this.grid = (Grid) component;
		grid.addListener(Events.CellMouseDown, new Listener<GridEvent>() {
			public void handleEvent(GridEvent e) {
				onMouseDown(e);
			}
		});
	}

	protected void onMouseDown(GridEvent ge) {
		String cls = ge.getTarget().getClassName();
		if (cls != null && cls.indexOf("x-grid3-cc-" + getId()) != -1) {
			ge.stopEvent();
			int index = grid.getView().findRowIndex(ge.getTarget());
			ModelData m = grid.getStore().getAt(index);
			Record r = grid.getStore().getRecord(m);
			Object v = m.get(getDataIndex());
			boolean b = v == null ? false : ((Boolean)v).booleanValue();
			r.set(getDataIndex(), Boolean.valueOf(!b));
			r.setDirty(true);
			changeValue(r, getDataIndex(), Boolean.valueOf(!b), Boolean.valueOf(b));
		}
	}

	protected void init() {
		setRenderer(new GridCellRenderer() {
			public String render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore store) {
				Boolean v = model.get(property);
				String on = v != null && v.booleanValue() ? "-on" : "";
				config.css = "x-grid3-check-col-td";
				return "<div class='x-grid3-check-col" + on + " x-grid3-cc-"
						+ getId() + "'>&#160;</div>";
			}
		});
	}
	
	protected void changeValue(Record record, String property, Boolean value, Boolean startValue) {
		
	}
	
}
