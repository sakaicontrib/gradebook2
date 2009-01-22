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

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.action.RemoteCommand;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGetAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.model.GradeEventModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeRecordModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;

public class LogColumnConfig extends ColumnConfig implements ComponentPlugin {

	protected Grid<GradeRecordModel> grid;
	private ContentPanel contentPanel;
	private String columnId;
	private StudentModel student;

	public LogColumnConfig() {
		super();
		init();
	}
	
	public LogColumnConfig(String id, String name, int width) {
		super(id, name, width);
		init();
		this.contentPanel = new ContentPanel();
		contentPanel.setSize(200, 140);
		this.columnId = id;
	}

	public void init(Component component) {
		this.grid = (Grid<GradeRecordModel>) component;
		
		grid.addListener(Events.CellClick, new Listener<GridEvent>() {

			public void handleEvent(GridEvent be) {
				ColumnModel cm = grid.getColumnModel();
				ColumnConfig column = cm.getColumn(be.colIndex);
				
				if (column.getId() == columnId) {
					GradeRecordModel model = grid.getStore().getAt(be.rowIndex);
					
					com.google.gwt.dom.client.Element row = grid.getView().getRow(be.rowIndex);
					com.google.gwt.dom.client.Element cell = grid.getView().getCell(be.rowIndex, be.colIndex);
					
					final Window logDialog = new Window();
					logDialog.setAutoHide(true);
					logDialog.setLayout(new FitLayout());
					final FlexTable logTable = new FlexTable();
					logDialog.add(logTable);
					logDialog.setPosition(cell.getAbsoluteLeft(), row.getAbsoluteTop());
					
					UserEntityGetAction<GradeEventModel> action = 
						new UserEntityGetAction<GradeEventModel>(EntityType.GRADE_EVENT, 
								String.valueOf(model.getAssignmentId()));
					action.setStudentUid(student.getIdentifier());
					
					RemoteCommand<GradeEventModel> remoteCommand = 
						new RemoteCommand<GradeEventModel>() {

							@Override
							public void onCommandListSuccess(UserEntityAction<GradeEventModel> action, List<GradeEventModel> events) {
								logDialog.setHeading("Grade Log: " + student.getStudentName());
								
								if (events != null && !events.isEmpty()) {
									int i = 0;
									logTable.clear();
									for (GradeEventModel event : events) {
										logTable.setText(i, 0, event.getDateGraded());
										logTable.setText(i, 1, "Grade set to " + event.getGrade() + " by " + event.getGraderName());
										i++;
									}
								}
								
								logDialog.show();
							}
						
					};
					
					remoteCommand.executeList(action);
				}
			}
			
		});
	}

	protected void onMouseDown(GridEvent ge) {
		String cls = ge.getTarget().getClassName();
		if (cls != null && cls.indexOf("x-grid3-cc-" + getId()) != -1) {
			ge.stopEvent();
			int index = grid.getView().findRowIndex(ge.getTarget());
			ModelData m = grid.getStore().getAt(index);
			/*Record r = grid.getStore().getRecord(m);
			Object v = m.get(getDataIndex());
			boolean b = v == null ? false : ((Boolean)v).booleanValue();
			r.set(getDataIndex(), !b);
			r.setDirty(true);
			changeValue(r, getDataIndex());*/
			//logDialog.show();
		}
	}

	protected void init() {
		setRenderer(new GridCellRenderer() {
			public String render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore store) {
				Boolean v = model.get(property);
				String on = v != null && v.booleanValue() ? "-on" : "";
				//config.css = "x-grid3-cell-inner";
				return "<div class='gbLog-col" + on + " x-grid3-cc-"
						+ getId() + "'>&#160;</div>";
			}
		});
	}
	
	protected void changeValue(Record record, String property) {
		
	}

	public StudentModel getStudent() {
		return student;
	}

	public void setStudent(StudentModel student) {
		this.student = student;
	}
	
}
