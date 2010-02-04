package org.sakaiproject.gradebook.gwt.server.model;

import java.util.HashMap;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.model.History;
import org.sakaiproject.gradebook.gwt.client.model.HistoryRecord;
import org.sakaiproject.gradebook.gwt.client.model.key.HistoryKey;
import org.sakaiproject.gradebook.gwt.sakai.Util;

public class HistoryImpl extends HashMap<String, Object> implements History {

	private static final long serialVersionUID = 1L;

	public HistoryImpl(List<HistoryRecord> records, Integer total) {
		super();
		setHistoryPage(records);
		setTotal(total);
	}
	
	@SuppressWarnings("unchecked")
	public List<HistoryRecord> getHistoryPage() {
		return (List<HistoryRecord>)get(HistoryKey.HISTORY_PAGE.toString());
	}

	public Integer getTotal() {
		return Util.toInteger(get(HistoryKey.TOTAL.toString()));
	}

	public void setHistoryPage(List<HistoryRecord> records) {
		put(HistoryKey.HISTORY_PAGE.toString(), records);
	}

	public void setTotal(Integer total) {
		put(HistoryKey.TOTAL.toString(), total);
	}

}
