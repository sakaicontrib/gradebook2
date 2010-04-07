package org.sakaiproject.gradebook.gwt.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.Upload;
import org.sakaiproject.gradebook.gwt.client.model.key.UploadKey;
import org.sakaiproject.gradebook.gwt.sakai.Util;

public class UploadImpl extends BaseModel implements Upload {

	private static final long serialVersionUID = 1L;

	public UploadImpl(Map<String,Object> map) {
		super();
		
				
		List<Map<String, Object>> headers = 
			(List<Map<String, Object>>)map.get(UploadKey.A_HDRS.name());
		
		if (headers != null && !headers.isEmpty()) {
			List<Item> itemHeaders = new ArrayList<Item>(headers.size());
			for (Map<String, Object> submap : headers) {
				itemHeaders.add(new GradeItemImpl(submap));
			}
			setHeaders(itemHeaders);
		}
		
		setPercentage(Util.toBooleanPrimitive(map.get(UploadKey.B_PCT.name())));
		
		List<Map<String, Object>> rows = 
			(List<Map<String, Object>>)map.get(UploadKey.A_ROWS.name());
	
		if (rows != null && !rows.isEmpty()) {
			List<Learner> learners = new ArrayList<Learner>(rows.size());
			for (Map<String, Object> submap : rows) {
				learners.add(new LearnerImpl(submap));
			}
			setRows(learners);
		}
	}
	
	public Item getGradebookItemModel() {
		return get(UploadKey.M_GB_ITM.name());
	}

	public List<Item> getHeaders() {
		return get(UploadKey.A_HDRS.name());
	}

	public List<String> getResults() {
		return get(UploadKey.A_RSTS.name());
	}

	public List<Learner> getRows() {
		return get(UploadKey.A_ROWS.name());
	}

	public boolean isPercentage() {
		return Util.toBooleanPrimitive(get(UploadKey.B_PCT.name()));
	}

	public void setDisplayName(String displayName) {
		set(UploadKey.S_NM.name(), displayName);
	}

	public void setGradebookItemModel(Item gradebookItemModel) {
		set(UploadKey.M_GB_ITM.name(), gradebookItemModel);	
	}

	public void setHeaders(List<Item> headers) {
		set(UploadKey.A_HDRS.name(), headers);
	}

	public void setPercentage(boolean isPercentage) {
		set(UploadKey.B_PCT.name(), Boolean.valueOf(isPercentage));
	}

	public void setResults(List<String> results) {
		set(UploadKey.A_RSTS.name(), results);
	}

	public void setRows(List<Learner> rows) {
		set(UploadKey.A_ROWS.name(), rows);
	}

}
