package org.sakaiproject.gradebook.gwt.client.gxt.model;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.api.ImportSettings;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Learner;
import org.sakaiproject.gradebook.gwt.client.model.Upload;
import org.sakaiproject.gradebook.gwt.client.model.key.UploadKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;

import com.extjs.gxt.ui.client.data.ModelData;

public class UploadModel extends EntityModel implements Upload {

	private static final long serialVersionUID = 1L;

	public UploadModel(EntityOverlay overlay) {
		super(overlay);
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
		return DataTypeConversionUtil.checkBoolean((Boolean)get(UploadKey.B_PCT.name()));
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
		Object obj = get(UploadKey.C_CTGRY_TYPE.name());
		
		if (obj == null)
			return null;
		
		if (obj instanceof CategoryType)
			return (CategoryType)obj;
		
		return CategoryType.valueOf((String)obj);
	}

	public GradeType getGradeType() {
	 	Object obj = get(UploadKey.G_GRD_TYPE.name());
		
		if (obj == null)
			return null;
		
		if (obj instanceof GradeType)
			return (GradeType)obj;
		
		return GradeType.valueOf((String)obj);
	}

	public String getNotes() {
		return get(UploadKey.S_NOTES.name());
	}

	public boolean hasErrors() {
		return DataTypeConversionUtil.checkBoolean((Boolean)get(UploadKey.B_HAS_ERRS.name()));
	}

	public boolean isNotifyAssignmentName() {
		return DataTypeConversionUtil.checkBoolean((Boolean)get(UploadKey.B_NTFY_ITM_NM.name()));
	}

	public void setCategoryType(CategoryType categoryType) {
		set(UploadKey.C_CTGRY_TYPE.name(), categoryType.name());
	}

	public void setErrors(boolean hasErrors) {
		set(UploadKey.B_HAS_ERRS.name(), Boolean.valueOf(hasErrors));
	}

	public void setGradeType(GradeType gradeType) {
		set(UploadKey.G_GRD_TYPE.name(), gradeType.name());
	}

	public void setNotes(String notes) {
		set(UploadKey.S_NOTES.name(), notes);
	}

	public void setNotifyAssignmentName(boolean doNotify) {
		set(UploadKey.B_NTFY_ITM_NM.name(), Boolean.valueOf(doNotify));
	}

	
	public ModelData newChildModel(String property, EntityOverlay overlay) {
		
		if (property.equals(UploadKey.M_GB_ITM.name()))
			return new ItemModel(overlay);
		else if (property.equals(UploadKey.A_ROWS.name()))
			return new LearnerModel(overlay);
		
		
		return new EntityModel(overlay);
	}

	public void setImportSettings(ImportSettings importSettings) {
		set(UploadKey.M_IMPRTSETGS.name(), importSettings);
	}

	public ImportSettings getImportSettings() {
		if (null == get(UploadKey.M_IMPRTSETGS.name())) set(UploadKey.M_IMPRTSETGS.name(), new ImportSettingsModel());
		EntityModel e = get(UploadKey.M_IMPRTSETGS.name());
		ImportSettings s = new ImportSettingsModel(e);
		return s;
	}

}
