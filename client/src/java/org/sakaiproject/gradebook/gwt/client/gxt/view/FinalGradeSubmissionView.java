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

package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.view.components.GradeSubmissionDialog;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;

public class FinalGradeSubmissionView extends View {

	private GradeSubmissionDialog dialog;
	
	public FinalGradeSubmissionView(Controller controller, I18nConstants i18n) {
		super(controller);
		this.dialog = new GradeSubmissionDialog(i18n);
	}

	@Override
	protected void initialize() {
		super.initialize();
	}
	
	@Override
	protected void handleEvent(AppEvent<?> event) {

		dialog.verify();
	}
}
