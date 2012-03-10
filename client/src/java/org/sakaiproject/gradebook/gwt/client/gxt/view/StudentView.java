/***********************************************************************************
*
* Copyright (c) 2008, 2009, 2010, 2011, 2012 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.StudentPanel;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationSetup;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class StudentView extends AppView {

	private StudentPanel studentViewContainer;
	
	public StudentView(Controller controller) {
		super(controller);
	}
	
	@Override
	protected void initUI(ApplicationSetup applicationSetup) {
		
		I18nConstants i18n = Registry.get(AppConstants.I18N);
		Gradebook gbModel = applicationSetup.getGradebookModels().get(0);
		studentViewContainer = new StudentPanel(i18n, true, true);
		studentViewContainer.onChangeModel(gbModel, (ModelData)gbModel.getUserAsStudent());
		viewport.setLayout(new FitLayout());
		viewport.add(studentViewContainer);
		viewportLayout.setActiveItem(studentViewContainer);
		viewport.layout();
	}
}
