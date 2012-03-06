package org.sakaiproject.gradebook.gwt.client.gxt.type;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;

public interface FileModel {
	
	static I18nConstants i18n = Registry.get(AppConstants.I18N);
	public static String DISPLAY_NAME = "name";
	public static String DISPLAY_VALUE = "value";
	public static String DISPLAY_VALUE_STRING = "stringValue";
	
	

	public String getDisplayName();

	public String name();
	public static class Util {
		public static ModelData getFileModel(FileModel fileModel){

			ModelData model = new BaseModel();
			model.set(DISPLAY_NAME, fileModel.getDisplayName());
			model.set(DISPLAY_VALUE, fileModel);
			model.set(DISPLAY_VALUE_STRING, fileModel.name());

			return model;
		}
	}
	public String getTypeName();
	

}
