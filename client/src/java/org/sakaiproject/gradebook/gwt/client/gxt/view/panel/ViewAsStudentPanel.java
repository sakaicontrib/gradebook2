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
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseLearner;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseLearner.BrowseType;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
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
		setFrame(true);
		setHeaderVisible(true);
		setLayout(new FitLayout());

		I18nConstants i18n = Registry.get(AppConstants.I18N);
		if (isStudentView)
			setHeading(i18n.singleViewHeader());
		else 
			setHeading(i18n.singleGradeHeader());

		container = new StudentPanel(i18n, isStudentView, false);
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

	public void onRefreshGradebookSetup(Gradebook selectedGradebook) {
		container.onRefreshGradebookSetup(selectedGradebook);
	}

	public void onChangeModel(Gradebook selectedGradebook, ModelData learnerGradeRecordCollection) {
		container.onChangeModel(selectedGradebook, learnerGradeRecordCollection);
	}

	public void onItemUpdated(Item itemModel) {
		container.onItemUpdated(itemModel);
	}
	
	public void onLearnerGradeRecordUpdated(ModelData learnerGradeRecordModel) {
		container.onLearnerGradeRecordUpdated(learnerGradeRecordModel);
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
		Button next = new Button(i18n.nextLearner(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				BrowseLearner event = new BrowseLearner(container.getStudentRow(), BrowseType.NEXT);
				Dispatcher.forwardEvent(GradebookEvents.BrowseLearner.getEventType(), event);
			}
		}); 
		Button prev = new Button(i18n.prevLearner(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				BrowseLearner event = new BrowseLearner(container.getStudentRow(), BrowseType.PREV);
				Dispatcher.forwardEvent(GradebookEvents.BrowseLearner.getEventType(), event);
			}
		}); 

		Button close = new Button(i18n.close(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				container.onClose();
				Dispatcher.forwardEvent(GradebookEvents.StopStatistics.getEventType());
			}

		});

		// FIXME: This needs to be integrated into MVC
		Button studentView = new Button(i18n.viewAsLearner(), new SelectionListener<ButtonEvent>() { 
			@Override
			public void componentSelected(ButtonEvent ce) {
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
