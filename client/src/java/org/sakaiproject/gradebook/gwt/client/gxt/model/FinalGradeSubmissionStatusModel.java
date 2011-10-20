/**********************************************************************************
 *
 * Copyright (c) 2008, 2009, 2010, 2011 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.client.gxt.model;

import org.sakaiproject.gradebook.gwt.client.model.FinalGradeSubmissionStatus;
import org.sakaiproject.gradebook.gwt.client.model.key.FinalGradeSubmissionStatusKey;

public class FinalGradeSubmissionStatusModel extends EntityModel implements FinalGradeSubmissionStatus {

	private static final long serialVersionUID = 1L;
	
	public FinalGradeSubmissionStatusModel() {
		super();
	}
	
	public FinalGradeSubmissionStatusModel(EntityOverlay overlay) {
		super(overlay);
	}

	@Override
	public String getDialogNotificationMessage() {
		
		return get(FinalGradeSubmissionStatusKey.S_DIALOG.name());
	}

	@Override
	public void setDialogNotificationMessage(final String message) {
		
		set(FinalGradeSubmissionStatusKey.S_DIALOG.name(), message);
	}

	@Override
	public String getBannerNotificationMessage() {
		
		return get(FinalGradeSubmissionStatusKey.S_BANNER.name());
	}

	@Override
	public void setBannerNotificationMessage(final String message) {

		set(FinalGradeSubmissionStatusKey.S_BANNER.name(), message);
	}
}
