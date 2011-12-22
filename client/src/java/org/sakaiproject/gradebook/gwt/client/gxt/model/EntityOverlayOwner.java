package org.sakaiproject.gradebook.gwt.client.gxt.model;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.i18n.client.DateTimeFormat;

public interface EntityOverlayOwner {

	public EntityOverlay getOverlay();
	
	public boolean isChildString(String property);
	 
	public boolean isGradeType(String property);
	 
	public boolean isCategoryType(String property);
	
	public DateTimeFormat getDateTimeFormat(String property);
	
	public boolean isChildModel(String property);
	
	public ModelData newChildModel(String property, EntityOverlay overlay);
	
}
