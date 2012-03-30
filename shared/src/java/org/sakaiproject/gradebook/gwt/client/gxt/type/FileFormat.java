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

import com.extjs.gxt.ui.client.data.ModelData;

public enum FileFormat implements FileModel { 
	
	FULL("full"),
	SCANTRON("scantron"), 
	CLICKER("clicker"), 
	TEMPLATE("template");

	private String typeName;
	private String displayName;


	
	FileFormat(String typeName) {
		
		this.typeName = typeName;
		
	}
	

	public String getDisplayName(I18nConstants i18n) {
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
	
	public static ModelData getFileModel(FileModel fileModel, I18nConstants i18n) {
		return FileModel.Util.getFileModel(fileModel, i18n);
	}

	public static FileFormat getFormatByName(String name) {
		for (FileFormat f : values()) {
			if (f.name().equals(name)) {
				return f;
			}
		}
		return null;
	}
	

	
}
