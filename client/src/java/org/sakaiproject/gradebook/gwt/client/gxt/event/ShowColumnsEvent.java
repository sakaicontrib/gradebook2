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

package org.sakaiproject.gradebook.gwt.client.gxt.event;

import java.util.Set;

public class ShowColumnsEvent {

	public boolean isSingle;
	public boolean selectAll;
	public Set<String> selectedItemModelIdSet;
	public Set<String> visibleStaticIdSet;
	public Set<String> fullStaticIdSet;
	public String itemModelId;
	public boolean isHidden;
	
	public ShowColumnsEvent(String itemModelId, boolean isHidden) {
		this.itemModelId = itemModelId;
		this.isSingle = true;
		this.isHidden = isHidden;
	}
	
	public ShowColumnsEvent(boolean selectAll, Set<String> fullStaticIdSet, Set<String> visibleStaticIdSet,
			Set<String> selectedItemModelIdSet) {
		this.fullStaticIdSet = fullStaticIdSet;
		this.selectAll = selectAll;
		this.selectedItemModelIdSet = selectedItemModelIdSet;
		this.visibleStaticIdSet = visibleStaticIdSet;
		this.isSingle = false;
	}
	
}
