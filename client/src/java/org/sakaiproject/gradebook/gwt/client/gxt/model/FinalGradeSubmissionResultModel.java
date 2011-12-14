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

import org.sakaiproject.gradebook.gwt.client.model.FinalGradeSubmissionResult;
import org.sakaiproject.gradebook.gwt.client.model.key.FinalGradeSubmissionResultKey;

public class FinalGradeSubmissionResultModel extends EntityModel implements FinalGradeSubmissionResult {

	private static final long serialVersionUID = 1L;

	public FinalGradeSubmissionResultModel() {
		super();
	}
	
	public FinalGradeSubmissionResultModel(EntityOverlay overlay) {
		super(overlay);
	}
	
	@Override
	public Integer getStatus() {
		
		return get(FinalGradeSubmissionResultKey.I_STATUS.name());
	}

	@Override
	public void setStatus(Integer result) {

		set(FinalGradeSubmissionResultKey.I_STATUS.name(), result);
	}

	@Override
	public String getData() {
		
		return get(FinalGradeSubmissionResultKey.S_DATA.name());
	}

	@Override
	public void setData(String data) {

		set(FinalGradeSubmissionResultKey.S_DATA.name(), data);
	}
}
