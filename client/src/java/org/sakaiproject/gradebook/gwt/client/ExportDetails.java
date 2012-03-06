package org.sakaiproject.gradebook.gwt.client;

import org.sakaiproject.gradebook.gwt.client.gxt.type.FileModel;


public class ExportDetails {

	private FileModel fileType; 
	private boolean includeStructure;
	private String sectionUid;
	private Boolean includeComments = true;

	public String getSectionUid() {
		return sectionUid;
	}
	public void setSectionUid(String sectionUid) {
		this.sectionUid = sectionUid;
	}
	public ExportDetails(FileModel fileType, boolean includeStructure) {
		super();
		this.fileType = fileType;
		this.includeStructure = includeStructure;
	}
	public ExportDetails() {
		super();
	}
	
	public FileModel getFileType() {
		return fileType;
	}
	public void setFileType(FileModel fileType) {
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
