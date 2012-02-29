package org.sakaiproject.gradebook.gwt.client.gxt.type;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;

public enum FileFormat implements FileModel { 
	
	FULL("full", false),
	SCANTRON("scantron", false), 
	CLICKER("clicker", false), 
	TEMPLATE("template", true);

	private String typeName;
	private String displayName;
	private boolean exportable;


	
	FileFormat(String typeName, boolean canExport) {
		
		this.typeName = typeName;
		this.exportable = canExport;
	}
	

	public String getDisplayName() {
		if (null == displayName) {
			switch(this) {
			case SCANTRON:
				displayName = i18n.importFormatSCANTRON();
				break;
			case CLICKER:
				displayName = i18n.importFormatCLICKER();
				break;
			case TEMPLATE:
				displayName = i18n.exportFormatTemplate();
				break;
			case FULL:
				displayName = i18n.importFullGradebook();
				break;
			}
		}
		
		return displayName;
	}
	
	public String  getTypeName() {
		return typeName;
	}
	
	public static ModelData getExportFormatModel(FileFormat exportFormat) {

		ModelData model = new BaseModel();
		model.set(DISPLAY_NAME, exportFormat.getDisplayName());
		model.set(DISPLAY_VALUE, exportFormat);
		model.set(DISPLAY_VALUE_STRING, exportFormat.name());

		return model;
	}
	
	public static ModelData getFileModel(FileModel fileModel) {
		return FileModel.Util.getFileModel(fileModel);
	}
	
	public boolean isExportable() {
		return exportable;
	}

	
	

	
}
