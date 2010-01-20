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
import com.extjs.gxt.ui.client.data.ModelData;

public class GradeScaleRecordMapModel extends BaseModel {

	private static final long serialVersionUID = 1L;
	
	private String gradebookUid;
	private Long gradebookId;
	private List<ModelData> records;
	private ModelData updatedRecord;
	private boolean hardReset; 
	
	public GradeScaleRecordMapModel() {
		
		hardReset = false; 
	}
	
	public GradeScaleRecordMapModel(String gradebookUid, Long gradebookId, ModelData updatedRecord) {
		this.gradebookUid = gradebookUid;
		this.gradebookId = gradebookId;
		this.updatedRecord = updatedRecord;
	}
	
	public GradeScaleRecordMapModel(List<ModelData> records) {
		this.records = records;
	}

	public List<ModelData> getRecords() {
		return records;
	}

	public void setRecords(List<ModelData> records) {
		this.records = records;
	}

	public ModelData getUpdatedRecord() {
		return updatedRecord;
	}

	public void setUpdatedRecord(ModelData updatedRecord) {
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
