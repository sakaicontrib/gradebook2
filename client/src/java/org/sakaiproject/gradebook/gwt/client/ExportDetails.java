package org.sakaiproject.gradebook.gwt.client;

import java.util.List;

public class ExportDetails {
	public enum ExportType { XLS97, CSV }; 

	private ExportType fileType; 
	private boolean includeStructure;
	private String sectionUid;
	private List<String> allSections;
	
	public List<String> getAllSections() {
		return allSections;
	}
	public void setAllSections(List<String> allSections) {
		this.allSections = allSections;
	}
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

}
