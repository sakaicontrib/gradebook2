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

package org.sakaiproject.gradebook.gwt.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;

public class GradeScaleRecordMapModel extends BaseModel {

	private static final long serialVersionUID = 1L;
	
	private String gradebookUid;
	private Long gradebookId;
	private List<GradeScaleRecordModel> records;
	private GradeScaleRecordModel updatedRecord;
	private boolean hardReset; 
	
	public GradeScaleRecordMapModel() {
		
		hardReset = false; 
	}
	
	public GradeScaleRecordMapModel(String gradebookUid, Long gradebookId, GradeScaleRecordModel updatedRecord) {
		this.gradebookUid = gradebookUid;
		this.gradebookId = gradebookId;
		this.updatedRecord = updatedRecord;
	}
	
	public GradeScaleRecordMapModel(List<GradeScaleRecordModel> records) {
		this.records = records;
	}

	public List<GradeScaleRecordModel> getRecords() {
		return records;
	}

	public void setRecords(List<GradeScaleRecordModel> records) {
		this.records = records;
	}

	public GradeScaleRecordModel getUpdatedRecord() {
		return updatedRecord;
	}

	public void setUpdatedRecord(GradeScaleRecordModel updatedRecord) {
		this.updatedRecord = updatedRecord;
	}

	public String getGradebookUid() {
		return gradebookUid;
	}

	public void setGradebookUid(String gradebookUid) {
		this.gradebookUid = gradebookUid;
	}

	public Long getGradebookId() {
		return gradebookId;
	}

	public void setGradebookId(Long gradebookId) {
		this.gradebookId = gradebookId;
	}

	public boolean isHardReset() {
		return hardReset;
	}

	public void setHardReset(boolean hardReset) {
		this.hardReset = hardReset;
	}
	
}
