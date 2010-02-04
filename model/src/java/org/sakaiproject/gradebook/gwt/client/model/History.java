package org.sakaiproject.gradebook.gwt.client.model;

import java.util.List;

public interface History {

	public abstract List<HistoryRecord> getHistoryPage();
	
	public abstract void setHistoryPage(List<HistoryRecord> records);
	
	public abstract Integer getTotal();
	
	public abstract void setTotal(Integer total);
	
}
