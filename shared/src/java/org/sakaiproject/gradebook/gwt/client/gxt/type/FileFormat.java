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

public enum FileFormat implements FileModel { 

	CLICKER("clicker"),
	FULL("full"),
	TEMPLATE("template"),
	NO_STRUCTURE("no-structure"),
	SCANTRON("scantron");

	private String displayName;
	private String importMessage;
	private String typeName;

	FileFormat(String typeName) {
		
		this.typeName = typeName;
		
	}

	public String getDisplayName(I18nConstants i18n) {
		if (null == displayName) {
			switch(this) {
			case CLICKER:
				displayName = i18n.fileFormatNameClicker();
				break;
			case FULL:
				displayName = i18n.fileFormatNameFull();
				break;
			case SCANTRON:
				displayName = i18n.fileFormatNameScantron();
				break;
			case TEMPLATE:
				displayName = i18n.fileFormatNameTemplate();
				break;
			case NO_STRUCTURE:
				displayName = i18n.fileFormatNameNoStructure();
			}
		}
		
		return displayName;
	}

	public String getImportMessage(I18nConstants i18n) {
		if (null == importMessage) {
			switch(this) {
			case CLICKER:
				importMessage = i18n.fileFormatImportMessageClicker();
				break;
			case FULL:
				importMessage = i18n.fileFormatImportMessageFull();
				break;
			case SCANTRON:
				importMessage = i18n.fileFormatImportMessageScantron();
				break;
			case TEMPLATE:
				importMessage = i18n.fileFormatImportMessageTemplate();
				break;
			case NO_STRUCTURE:
				importMessage = i18n.fileFormatImportMessageNoStructure();
			}
		}

		return importMessage;
	}
	
	public String  getTypeName() {
		return typeName;
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
