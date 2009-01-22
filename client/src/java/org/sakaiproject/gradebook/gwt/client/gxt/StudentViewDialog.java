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

import org.sakaiproject.gradebook.gwt.client.GradebookToolFacadeAsync;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseStudentEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.IndividualStudentEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.UserChangeEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseStudentEvent.BrowseType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.XDOM;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class StudentViewDialog extends Dialog {

	public enum RefreshAction { NONE, REFRESHDATA, REFRESHCOLUMNS };
	
	private StudentViewContainer container;
	private RefreshAction refreshAction = RefreshAction.NONE;
	private String gradebookUid;
	
	public StudentViewDialog(final String gradebookUid, final GradebookToolFacadeAsync toolSrv) {
		this.gradebookUid = gradebookUid;
		
		setBodyBorder(true);
		setButtons(Dialog.OK);
		//setIconStyle("icon-app-side");
		setHeaderVisible(true);
		setHeading("Individual Grade Summary");
		setResizable(false);
		setDraggable(false);
		//setFrame(true);
		setCloseAction(CloseAction.CLOSE);
		setHideOnButtonClick(false); 
		setLayout(new FitLayout());
		//setModal(true);
		setPlain(false);
		
	    container = new StudentViewContainer(gradebookUid, toolSrv, false);
		add(container);
		
		setupNavigation();
		
		container.addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {

			public void handleEvent(UserChangeEvent uce) {
				StudentViewDialog.this.fireEvent(GradebookEvents.UserChange, uce);
			}
			
		});
		
		addListener(GradebookEvents.SingleView, new Listener<IndividualStudentEvent>() 
		{

			public void handleEvent(IndividualStudentEvent be) {
				if (container.fireEvent(GradebookEvents.SingleView, be)) {
					//Point pos = getParent().getPosition(false);
					//StudentViewDialog.this.setPosition(pos.x, pos.y);
					//StudentViewDialog.this.setSize(XDOM.getViewportSize().width, XDOM.getViewportSize().height - 35);
					//StudentViewDialog.this.show();
					be.doit = true;
				}
			}
		});
		
		addListener(Events.BeforeShow, new Listener() {

			public void handleEvent(BaseEvent be) {
				switch (refreshAction) {
				case REFRESHDATA:
					container.refreshData();
					break;
				case REFRESHCOLUMNS:
					container.refreshColumns();
					break;
				}
				refreshAction = RefreshAction.NONE;
			}
		
		});
		
		addListener(GradebookEvents.UserChange, new Listener<UserChangeEvent>() {

			public void handleEvent(UserChangeEvent uce) {
				if (uce.getAction() instanceof UserEntityAction) {
					UserChangeEvent event = (UserChangeEvent)uce;
					UserEntityAction action = (UserEntityAction)event.getAction();
					
					// FIXME: Ideally we want to ensure that these methods are only called once at the end of a series of operations
					switch (action.getEntityType()) {
					case GRADEBOOK:
						switch (action.getActionType()) {
						case UPDATE:
							// Update actions will (always?) result from user changes on the setup 
							// screens, so they should be deferred to the "onShow" method
							GradebookModel.Key gradebookModelKey = GradebookModel.Key.valueOf(((UserEntityUpdateAction)action).getKey());
							switch (gradebookModelKey) {
							case GRADETYPE:
								queueDeferredRefresh(RefreshAction.REFRESHCOLUMNS);
								break;
							}
							break;
						}
					}
				}
			}
			
		});
	}
	
	protected void queueDeferredRefresh(RefreshAction refreshAction) {
		switch (this.refreshAction) {
		// We don't want to 'demote' a refresh columns action to a refresh data action
		case NONE:
		case REFRESHDATA:
			this.refreshAction = refreshAction;
			break;
		}
	}
	
	private void setupNavigation() {
		
		getButtonBar().removeAll();
		Button next = new Button("Next", new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				BrowseStudentEvent event = new BrowseStudentEvent(container.getStudentRow(), BrowseType.NEXT);
				StudentViewDialog.this.fireEvent(GradebookEvents.BrowseStudent, event);
				//el().mask(GXT.MESSAGES.loadMask_msg());
			}
		}); 
		Button prev = new Button("Previous", new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				BrowseStudentEvent event = new BrowseStudentEvent(container.getStudentRow(), BrowseType.PREV);
				StudentViewDialog.this.fireEvent(GradebookEvents.BrowseStudent, event);
				//el().mask(GXT.MESSAGES.loadMask_msg());
			}
		}); 
		
		/*Button close = new Button("Close", new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				close();
				
			}
			
		});*/
		
		final GradebookToolFacadeAsync service = Registry.get("service");
		Button studentView = new Button("View as Student", new SelectionListener<ComponentEvent>() { 
			@Override
			public void componentSelected(ComponentEvent ce) {
				StudentViewContainer studentView = new StudentViewContainer(gradebookUid, service, true);
				IndividualStudentEvent event = new IndividualStudentEvent(container.getStudentModel());
				studentView.fireEvent(GradebookEvents.SingleView, event);
				Window window = new Window();
				window.setHeading("Student View");
				window.setLayout(new FitLayout());
				window.add(studentView);
				
				Point pos = StudentViewDialog.this.getPosition(false);
				window.setPosition(pos.x, pos.y);
				window.setSize(XDOM.getViewportSize().width, XDOM.getViewportSize().height - pos.y);
				
				window.show();
			}
		});
		
		addButton(prev);
		addButton(studentView);
		addButton(next);
		
	}
	
}
