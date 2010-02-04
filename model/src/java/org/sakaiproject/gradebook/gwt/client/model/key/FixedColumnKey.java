/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.model.key;

public enum FixedColumnKey {
	ID("id"), NAME("name"), ASSIGNMENT_ID("itemId"), CATEGORY_ID("categoryId"), 
	CATEGORY_NAME("categoryName"), WIDTH("width"), POINTS("points"), 
	UNWEIGHTED("unweighted"),
	HIDDEN("hidden"), EDITABLE("editable"), 
	STUDENT_MODEL_KEY("studentModelKey"), EXTRA_CREDIT("extraCredit"), 
	IS_CHECKED("isChecked");
	
	private String property;
	
	private FixedColumnKey(String property) {
		this.property = property;
	}

	public String toString() {
		return property;
	}
	
}