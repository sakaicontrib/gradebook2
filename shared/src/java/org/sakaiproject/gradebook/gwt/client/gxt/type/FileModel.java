/**********************************************************************************
 *
 * Copyright (c) 2012 The Regents of the University of California
 *
 * Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.gradebook.gwt.client.gxt.type;

import org.sakaiproject.gradebook.gwt.client.I18nConstants;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ModelData;

public interface FileModel {
	
	public static String DISPLAY_NAME = "name";
	public static String DISPLAY_VALUE = "value";
	public static String DISPLAY_VALUE_STRING = "stringValue";
	
	

	public String getDisplayName(I18nConstants i18n);
	
	public String name();
	public static class Util {
		public static ModelData getFileModel(FileModel fileModel, I18nConstants i18n) {

			ModelData model = new BaseModel();
			model.set(DISPLAY_NAME, fileModel.getDisplayName(i18n));
			model.set(DISPLAY_VALUE, fileModel);
			model.set(DISPLAY_VALUE_STRING, fileModel.name());

			return model;
		}
	}
	public String getTypeName();
	

}
