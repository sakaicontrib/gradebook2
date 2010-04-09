/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;


public enum ActionKey { 
	S_ID("id"), 
	S_GB_UID("gradebookUid"), 
	L_GB_ID("gradebookId"), 
	S_ACTION("actionDate"), 
	S_RECORD("recordedDate"),
	O_ENTY_TYPE("entityType"), 
	S_ENTY_NM("entityName"), 
	S_LRNR_UID("learnerUid"), 
	S_LRNR_NM("learnerName"), 
	S_ENTY_ID("entityId"), 
	B_INCL_ALL("includeAll"), 
	S_PROP("property"), 
	L_PRNT_ID("parentId"), 
	O_ACTION_TYPE("actionType"), 
	M_MDL("model"), 
	M_LRNR_MDL("learnerModel"),
	O_VALUE("value"), 
	O_OLD_VALUE("startValue"), 
	S_NAME("name"), 
	D_WGHT("weight"), 
	B_EQL_WGHT("isEqualWeight"), 
	I_DRP_LOW("dropLowestX"), 
	D_PTS("points"), 
	T_DUE("dueDate"), 
	O_STATUS("status"), 
	S_GRDR_NM("graderName"), 
	S_DESC("description"), 
	S_TEXT("text"),
	S_PROP_NM("propertyName");

	private String property;

	private ActionKey(String property) {
		this.property = property;
	}
	
	public String getProperty() {
		return property;
	}
}
