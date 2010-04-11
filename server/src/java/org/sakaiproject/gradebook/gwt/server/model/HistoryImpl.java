package org.sakaiproject.gradebook.gwt.server.model;

import java.util.HashMap;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.model.History;
import org.sakaiproject.gradebook.gwt.client.model.HistoryRecord;
import org.sakaiproject.gradebook.gwt.client.model.key.HistoryKey;
import org.sakaiproject.gradebook.gwt.server.Util;

public class HistoryImpl extends HashMap<String, Object> implements History {

	private static final long serialVersionUID = 1L;

	public HistoryImpl(List<HistoryRecord> records, Integer total) {
		super();
		setHistoryPage(records);
		setTotal(total);
	}
	
	@SuppressWarnings("unchecked")
	public List<HistoryRecord> getHistoryPage() {
		return (List<HistoryRecord>)get(HistoryKey.A_PAGE.name());
	}

	public Integer getTotal() {
		return Util.toInteger(get(HistoryKey.I_TOTAL.name()));
	}

	public void setHistoryPage(List<HistoryRecord> records) {
		put(HistoryKey.A_PAGE.name(), records);
	}

	public void setTotal(Integer total) {
		put(HistoryKey.I_TOTAL.name(), total);
	}

}
