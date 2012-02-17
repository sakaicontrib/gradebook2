package org.sakaiproject.gradebook.gwt.client.gxt.type;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;

public enum ExportType { 
	
	CSV("csv", ".csv"), XLS97("xls97", ".xls"), XLSX("xlsx", ".xlsx"), TEMPLATE("template");
	
	public static String DISPLAY_NAME = "name", DISPLAY_VALUE = "value";
	
	private I18nConstants i18n;
	private String displayName;
	private String typeName;
	private String fileExtension;

	
	ExportType(String typeName) {
		
		this(typeName, null);
	}
	
	ExportType(String typeName, String fileExtension) {
		
		this.i18n = Registry.get(AppConstants.I18N);
		this.typeName = typeName;
		this.fileExtension = fileExtension;
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
	
	public static ModelData getExportTypeModel(ExportType exportType) {

		ModelData model = new BaseModel();
		model.set(DISPLAY_NAME, exportType.getDisplayName());
		model.set(DISPLAY_VALUE, exportType);

		return model;
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
