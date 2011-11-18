package org.sakaiproject.gradebook.gwt.server.model;

public class GradeRecord {

	private String gradebookUid;
	private Long gradebookId;
	private String studentUid;
	private String itemId;
	private Double value;
	private Double previousValue;
	private String stringValue;
	private String previousStringValue;
	private Boolean booleanValue;
	private Boolean previousBooleanValue;
	private Boolean numeric;
	
	public GradeRecord() { }
	
	public GradeRecord(String gradebookUid, Long gradebookId,
			String studentUid, String itemId, Double value, Double previousValue) {
		super();
		this.gradebookUid = gradebookUid;
		this.gradebookId = gradebookId;
		this.studentUid = studentUid;
		this.itemId = itemId;
		this.value = value;
		this.previousValue = previousValue;
	}
	
	public GradeRecord(String gradebookUid, Long gradebookId,
			String studentUid, String itemId, String stringValue, String previousStringValue) {
		super();
		this.gradebookUid = gradebookUid;
		this.gradebookId = gradebookId;
		this.studentUid = studentUid;
		this.itemId = itemId;
		this.stringValue = stringValue;
		this.previousStringValue = previousStringValue;
	}

	public GradeRecord(String gradebookUid, Long gradebookId,
			String studentUid, String itemId, Boolean booleanValue, Boolean previousBooleanValue) {
		super();
		this.gradebookUid = gradebookUid;
		this.gradebookId = gradebookId;
		this.studentUid = studentUid;
		this.itemId = itemId;
		this.booleanValue = booleanValue;
		this.previousBooleanValue = previousBooleanValue;
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

	public String getStudentUid() {
		return studentUid;
	}

	public void setStudentUid(String studentUid) {
		this.studentUid = studentUid;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Double getPreviousValue() {
		return previousValue;
	}

	public void setPreviousValue(Double previousValue) {
		this.previousValue = previousValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public String getPreviousStringValue() {
		return previousStringValue;
	}

	public void setPreviousStringValue(String previousStringValue) {
		this.previousStringValue = previousStringValue;
	}

	public Boolean getNumeric() {
		return numeric;
	}

	public void setNumeric(Boolean numeric) {
		this.numeric = numeric;
	}

	public Boolean getBooleanValue() {
		return booleanValue;
	}

	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}

	public Boolean getPreviousBooleanValue() {
		return previousBooleanValue;
	}

	public void setPreviousBooleanValue(Boolean previousBooleanValue) {
		this.previousBooleanValue = previousBooleanValue;
	}
}
