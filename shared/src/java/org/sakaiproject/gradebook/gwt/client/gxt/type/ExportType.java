package org.sakaiproject.gradebook.gwt.client.gxt.type;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;

public enum ExportType implements AppConstants, FileModel { 
	
	CSV(FILE_TYPE_CSV, ".csv"), XLS97(FILE_TYPE_XLS, ".xls"), XLSX(FILE_TYPE_XLSX, ".xlsx");

	
	private String displayName;
	private String typeName;
	private String fileExtension;
	ExportType(String typeName) {
		
		this(typeName, null);
	}
	
	ExportType(String typeName, String fileExtension) {
		
		this.typeName = typeName;
		this.fileExtension = fileExtension;
	}
	
	public String getDisplayName(I18nConstants i18n) {
		
		if(null == displayName) {
			switch(this) {
			case CSV:
				displayName = i18n.exportTypeCSV();
				break;
			case XLS97:
				displayName = i18n.exportTypeXLS();
				break;
			case XLSX:
				displayName = i18n.exportTypeXLSX();
				break;
			}
		}
		
		return displayName;
	}
	
	public String  getTypeName() {
		return typeName;
	}

	public static ExportType getExportTypeFromFilename(String value) {
		if (null == value || value.lastIndexOf(".")<0)
			return null;
		String x = value.substring(value.lastIndexOf("."));
		for (ExportType type : values()) {
			if (x.equalsIgnoreCase(type.getFileExtension())) {
				return type;
			}
		}
		return null;
	}	
	
	public String getFileExtension() {
		return fileExtension;
	}
}
