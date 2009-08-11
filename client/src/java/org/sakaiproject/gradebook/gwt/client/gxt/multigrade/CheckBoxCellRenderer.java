/**********************************************************************************
*
* $Id:$
*
***********************************************************************************
*
* Copyright (c) 2008, 2009 The Regents of the University of California
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

package org.sakaiproject.gradebook.gwt.client.gxt.multigrade;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.table.CellRenderer;

public class CheckBoxCellRenderer implements CellRenderer {
	
	public String render(Component item, String property, Object value) {
		if (value == null)
			return "";
				
		Boolean v = (Boolean)value;
		String on = v != null && v.booleanValue() ? "-on" : "";
		
		return new StringBuilder().append("<div class='x-grid3-check-col").append(on).append(" x-grid3-cc-").append(property).append("'>&#160;</div>").toString();
	}

}
