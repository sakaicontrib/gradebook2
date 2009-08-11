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

package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.gxt.view.InstructorView.MenuSelector;

public class TabConfig {

	public String id;
	public String header;
	public boolean isClosable;
	public String menuItemId;
	public MenuSelector menuSelector;
	public String iconStyle;
	
	public TabConfig(String id, String header, String iconStyle, boolean isClosable, MenuSelector menuSelector) {
		this.id = id;
		this.header = header;
		this.iconStyle = iconStyle;
		this.isClosable = isClosable;
		this.menuSelector = menuSelector;
	}
	
}
