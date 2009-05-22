package org.sakaiproject.gradebook.gwt.client.model;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;

public class GradeScaleRecordMapModel extends BaseModel {

	private static final long serialVersionUID = 1L;
	
	private String gradebookUid;
	private Long gradebookId;
	private List<GradeScaleRecordModel> records;
	private GradeScaleRecordModel updatedRecord;
	
	public GradeScaleRecordMapModel() {
		
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
	
}
