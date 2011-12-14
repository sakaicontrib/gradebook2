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

package org.sakaiproject.gradebook.gwt.server.model;

import org.sakaiproject.gradebook.gwt.client.model.FinalGradeSubmissionResult;

public class FinalGradeSubmissionResultImpl implements FinalGradeSubmissionResult {

	Integer status = Integer.valueOf(0);
	String data = null;
	
	public Integer getStatus() {

		return status;
	}

	@Override
	public void setStatus(Integer status) {
		
		this.status = status;
	}

	@Override
	public String getData() {
		
		return data;
	}

	@Override
	public void setData(String data) {
		
		this.data = data;
	}
}
