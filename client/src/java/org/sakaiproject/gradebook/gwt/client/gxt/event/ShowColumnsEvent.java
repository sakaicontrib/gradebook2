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
