package org.sakaiproject.gradebook.gwt.client.gxt.event;

import java.util.Set;

public class ShowColumnsEvent {

	public boolean selectAll;
	public Set<String> selectedItemModelIdSet;
	public Set<String> visibleStaticIdSet;
	public Set<String> fullStaticIdSet;
	
	public ShowColumnsEvent(boolean selectAll, Set<String> fullStaticIdSet, Set<String> visibleStaticIdSet,
			Set<String> selectedItemModelIdSet) {
		this.fullStaticIdSet = fullStaticIdSet;
		this.selectAll = selectAll;
		this.selectedItemModelIdSet = selectedItemModelIdSet;
		this.visibleStaticIdSet = visibleStaticIdSet;
	}
	
}
