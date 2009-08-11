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
package org.sakaiproject.gradebook.gwt.client.gxt.view.panel;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseLearner;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseLearner.BrowseType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class ViewAsStudentPanel extends ContentPanel {

	public enum RefreshAction { NONE, REFRESHDATA, REFRESHCOLUMNS };

	private StudentPanel container;
	private RefreshAction refreshAction = RefreshAction.NONE;

	private boolean isStudentView;

	public ViewAsStudentPanel(boolean isStudentView) {
		this.isStudentView = isStudentView;
		setBodyBorder(true);
		setHeaderVisible(true);
		setLayout(new FitLayout());

		I18nConstants i18n = Registry.get(AppConstants.I18N);
		if (isStudentView)
			setHeading(i18n.singleViewHeader());
		else 
			setHeading(i18n.singleGradeHeader());

		container = new StudentPanel(isStudentView);
		add(container);

		setupNavigation(i18n);

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

	}

	public void onRefreshGradebookSetup(GradebookModel selectedGradebook) {
		container.onRefreshGradebookSetup(selectedGradebook);
	}

	public void onChangeModel(GradebookModel selectedGradebook, StudentModel learnerGradeRecordCollection) {
		container.onChangeModel(selectedGradebook, learnerGradeRecordCollection);
	}

	public void onItemUpdated(ItemModel itemModel) {
		container.onItemUpdated(itemModel);
	}

	public void onUserChange(UserEntityAction<?> action) {
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

	protected void queueDeferredRefresh(RefreshAction refreshAction) {
		switch (this.refreshAction) {
			// We don't want to 'demote' a refresh columns action to a refresh data action
			case NONE:
			case REFRESHDATA:
				this.refreshAction = refreshAction;
				break;
		}
	}

	private void setupNavigation(I18nConstants i18n) {

		getButtonBar().removeAll();
		Button next = new Button(i18n.nextLearner(), new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				BrowseLearner event = new BrowseLearner(container.getStudentRow(), BrowseType.NEXT);
				Dispatcher.forwardEvent(GradebookEvents.BrowseLearner.getEventType(), event);
			}
		}); 
		Button prev = new Button(i18n.prevLearner(), new SelectionListener<ComponentEvent>() {
			@Override
			public void componentSelected(ComponentEvent ce) {
				BrowseLearner event = new BrowseLearner(container.getStudentRow(), BrowseType.PREV);
				Dispatcher.forwardEvent(GradebookEvents.BrowseLearner.getEventType(), event);
			}
		}); 

		Button close = new Button(i18n.close(), new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				Dispatcher.forwardEvent(GradebookEvents.StopImport.getEventType());
			}

		});

		// FIXME: This needs to be integrated into MVC
		Button studentView = new Button(i18n.viewAsLearner(), new SelectionListener<ComponentEvent>() { 
			@Override
			public void componentSelected(ComponentEvent ce) {
				Dispatcher.forwardEvent(GradebookEvents.SingleView.getEventType(), container.getStudentModel());
			}
		});

		if (!isStudentView)
			addButton(studentView);
		addButton(prev);
		addButton(next);
		addButton(close);

	}

}
