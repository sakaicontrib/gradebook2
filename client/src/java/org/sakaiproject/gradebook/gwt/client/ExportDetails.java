package org.sakaiproject.gradebook.gwt.client;

import org.sakaiproject.gradebook.gwt.client.gxt.type.FileFormat;


public class ExportDetails {

	private FileFormat fileType; 
	private boolean includeStructure;
	private String sectionUid;
	private Boolean includeComments = true;

	public String getSectionUid() {
		return sectionUid;
	}
	public void setSectionUid(String sectionUid) {
		this.sectionUid = sectionUid;
	}
	public ExportDetails(FileFormat fileType, boolean includeStructure) {
		super();
		this.fileType = fileType;
		this.includeStructure = includeStructure;
	}
	public ExportDetails() {
		super();
	}
	
	public FileFormat getFileType() {
		return fileType;
	}
	public void setFileType(FileFormat fileType) {
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
