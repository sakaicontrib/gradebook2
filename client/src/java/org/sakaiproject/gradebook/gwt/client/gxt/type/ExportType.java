package org.sakaiproject.gradebook.gwt.client.gxt.type;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;

import com.extjs.gxt.ui.client.Registry;

public enum ExportType { 
	
	CSV("csv"), XLS97("xls97"), XLSX("xlsx"), TEMPLATE("template");
	
	private I18nConstants i18n;
	private String displayName;
	private String typeName;
	
	ExportType(String typeName) {
		
		this.i18n = Registry.get(AppConstants.I18N);
		this.typeName = typeName;
	}
	
	public String getDisplayName() {
		
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
		case TEMPLATE:
			displayName = i18n.exportTypeTemplate();
			break;
		}
		
		return displayName;
	}
	
	public String  getTypeName() {
		return typeName;
	}
}
