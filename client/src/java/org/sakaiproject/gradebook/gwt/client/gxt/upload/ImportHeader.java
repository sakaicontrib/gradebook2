package org.sakaiproject.gradebook.gwt.client.gxt.upload;

import java.io.Serializable;

public class ImportHeader implements Serializable {

	public enum Field { ID, NAME, ITEM }
	public enum Mode { POINTS, PERCENTAGES, LETTERGRADES };
	
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String field;
	private String assignmentId;
	private String headerName;
	private Mode mode;
	private String value;
	private Double numericValue;
	private String categoryName;
	private Double points;
	private boolean isPercentage;
	
	private boolean checker;
	
	public ImportHeader() {
		
	}
	
	public ImportHeader(Field field, String value) {
		this.field = field.name();
		this.value = value;
	}
	
	public ImportHeader(String header) {
		this.headerName = header;
	}
	
	public ImportHeader(String assignmentId, String headerName, Mode mode, Double numericValue) {
		this.assignmentId = assignmentId;
		this.headerName = headerName;
		this.mode = mode;
		this.numericValue = numericValue;
	}

	public String getAssignmentId() {
		return assignmentId;
	}

	public void setAssignmentId(String assignmentId) {
		this.assignmentId = assignmentId;
	}

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Double getNumericValue() {
		return numericValue;
	}

	public void setNumericValue(Double numericValue) {
		this.numericValue = numericValue;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getPoints() {
		return points;
	}

	public void setPoints(Double points) {
		this.points = points;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public boolean getChecker() {
		return checker;
	}

	public void setChecker(boolean isHidden) {
		this.checker = isHidden;
	}

	public boolean isPercentage() {
		return isPercentage;
	}

	public void setPercentage(boolean isPercentage) {
		this.isPercentage = isPercentage;
	}
	
	
}
