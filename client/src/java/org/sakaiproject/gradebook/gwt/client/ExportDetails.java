package org.sakaiproject.gradebook.gwt.client;


public class ExportDetails {
	public enum ExportType { XLS97, CSV }; 

	private ExportType fileType; 
	private boolean includeStructure;
	private String sectionUid;
	private Boolean includeComments = true;

	public String getSectionUid() {
		return sectionUid;
	}
	public void setSectionUid(String sectionUid) {
		this.sectionUid = sectionUid;
	}
	public ExportDetails(ExportType fileType, boolean includeStructure) {
		super();
		this.fileType = fileType;
		this.includeStructure = includeStructure;
	}
	public ExportDetails() {
		super();
	}
	
	public ExportType getFileType() {
		return fileType;
	}
	public void setFileType(ExportType fileType) {
		this.fileType = fileType;
	}
	public boolean isIncludeStructure() {
		return includeStructure;
	}
	public void setIncludeStructure(boolean includeStructure) {
		this.includeStructure = includeStructure;
	}
	public void setIncludeComments(Boolean value) {
		includeComments  = value;
	}
	public boolean includeComments() {
		return includeComments;
	}

}
