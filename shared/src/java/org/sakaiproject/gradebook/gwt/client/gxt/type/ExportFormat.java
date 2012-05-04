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

public enum ExportFormat implements FileModel { 
	
	FULL("full"),
	NO_STRUCTURE("no-structure");

	private String displayName;
	private String exportMessage;
	private String typeName;

	ExportFormat(String typeName) {
		
		this.typeName = typeName;
		
	}

	public String getDisplayName(I18nConstants i18n) {
		if (null == displayName) {
			switch(this) {
			case FULL:
				displayName = i18n.fileFormatNameFull();
				break;
			case NO_STRUCTURE:
				displayName = i18n.fileFormatNameNoStructure();
			}
		}
		
		return displayName;
	}

	public String getExportMessage(I18nConstants i18n) {
		if (null == exportMessage) {
			switch(this) {
			case FULL:
				exportMessage = i18n.fileFormatExportMessageFull();
				break;
			case NO_STRUCTURE:
				exportMessage = i18n.fileFormatExportMessageNoStructure();
			}
		}

		return exportMessage;
	}
	
	public String  getTypeName() {
		return typeName;
	}

	public static ExportFormat getFormatByName(String name) {
		for (ExportFormat f : values()) {
			if (f.name().equals(name)) {
				return f;
			}
		}
		return null;
	}

	public Boolean hasStructure() {
	
		if(this == NO_STRUCTURE) {
			return Boolean.FALSE;
		}
		else {
			return Boolean.TRUE;
		}
	}
}
