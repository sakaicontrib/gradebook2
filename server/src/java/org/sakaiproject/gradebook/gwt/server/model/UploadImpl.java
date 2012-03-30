package org.sakaiproject.gradebook.gwt.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sakaiproject.gradebook.gwt.client.api.ImportSettings;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.Upload;
import org.sakaiproject.gradebook.gwt.client.model.key.UploadKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;

import org.sakaiproject.gradebook.gwt.server.ImportSettingsImpl;
import org.sakaiproject.gradebook.gwt.server.Util;

public class UploadImpl extends BaseModel implements Upload {

	private static final long serialVersionUID = 1L;
	
	
	public UploadImpl() {	
		super();
	}
	
	public UploadImpl(Map<String,Object> map) {
		super(map);
		
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
		
		String categoryTypeStr = (String)map.get(UploadKey.C_CTGRY_TYPE.name());
		if (categoryTypeStr != null)	
			setCategoryType(CategoryType.valueOf(categoryTypeStr));
		
		String gradeTypeStr = (String)map.get(UploadKey.G_GRD_TYPE.name());
		if (gradeTypeStr != null)
			setGradeType(GradeType.valueOf(gradeTypeStr));
		
		Map<String, Object> gradebookItemMap = (Map<String, Object>)map.get(UploadKey.M_GB_ITM.name());
		if (gradebookItemMap != null) { 
			GradeItem gradebookItem = new GradeItemImpl(gradebookItemMap);
			setGradebookItemModel(gradebookItem);
		}
		
		setNotifyAssignmentName(Util.toBooleanPrimitive(map.get(UploadKey.B_NTFY_ITM_NM.name())));	
	}
	
	public void addNotes(String notes) {
		
		StringBuilder allNotes = new StringBuilder();
		
		if (getNotes() != null) {
			allNotes.append(getNotes());
		}
		allNotes.append(notes).append("<br>");
		
		setNotes(allNotes.toString());
	}
	
	public Item getGradebookItemModel() {
		return get(UploadKey.M_GB_ITM.name());
	}

	public List<Item> getHeaders() {
		return get(UploadKey.A_HDRS.name());
	}

	public List<String> getResults() {
		return get(UploadKey.V_RSTS.name());
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
		set(UploadKey.V_RSTS.name(), results);
	}

	public void setRows(List<Learner> rows) {
		set(UploadKey.A_ROWS.name(), rows);
	}

	public CategoryType getCategoryType() {
		return Util.toCategoryType(get(UploadKey.C_CTGRY_TYPE.name()));
	}

	public GradeType getGradeType() {
		return Util.toGradeType(get(UploadKey.G_GRD_TYPE.name()));
	}

	public String getNotes() {
		return get(UploadKey.S_NOTES.name());
	}

	public boolean hasErrors() {
		return Util.toBooleanPrimitive(get(UploadKey.B_HAS_ERRS.name()));
	}

	public boolean isNotifyAssignmentName() {
		return Util.toBooleanPrimitive(get(UploadKey.B_NTFY_ITM_NM.name()));
	}

	public void setCategoryType(CategoryType categoryType) {
		put(UploadKey.C_CTGRY_TYPE.name(), categoryType.name());
	}

	public void setErrors(boolean hasErrors) {
		put(UploadKey.B_HAS_ERRS.name(), Boolean.valueOf(hasErrors));
	}

	public void setGradeType(GradeType gradeType) {
		put(UploadKey.G_GRD_TYPE.name(), gradeType.name());
	}

	public void setNotes(String notes) {
		put(UploadKey.S_NOTES.name(), notes);
	}

	public void setNotifyAssignmentName(boolean doNotify) {
		put(UploadKey.B_NTFY_ITM_NM.name(), Boolean.valueOf(doNotify));
	}
	
	public ImportSettings getImportSettings() {
		if (null == get(UploadKey.M_IMPRTSETGS.name())) set(UploadKey.M_IMPRTSETGS.name(), new ImportSettingsImpl());
		return (ImportSettings) get(UploadKey.M_IMPRTSETGS.name());
	}

	public void setImportSettings(ImportSettings importSettings) {
		set(UploadKey.M_IMPRTSETGS.name(), importSettings);
	}

	

}
