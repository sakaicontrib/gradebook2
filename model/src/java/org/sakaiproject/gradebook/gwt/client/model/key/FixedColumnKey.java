/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;

public enum FixedColumnKey {
	S_ID("id"), 
	S_NAME("name"), 
	L_ITEM_ID("itemId"), 
	L_CTGRY_ID("categoryId"), 
	S_CTGRY_NM("categoryName"), 
	I_WIDTH("width"), 
	D_PNTS("points"), 
	B_UNWGHTD("isUnweighted"),
	B_HDN("isHidden"), 
	B_EDIT("isEditable"), 
	O_LRNR_KEY("learnerKey"), 
	B_X_CRDT("isExtraCredit"), 
	B_CHCKD("isChecked");
	
	private String property;

	private FixedColumnKey(String property) {
		this.property = property;
	}
	
	public String getProperty() {
		return property;
	}
}