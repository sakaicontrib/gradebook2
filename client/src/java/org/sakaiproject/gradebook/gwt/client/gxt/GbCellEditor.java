package org.sakaiproject.gradebook.gwt.client.gxt;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TriggerField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;

public class GbCellEditor extends CellEditor {

	public GbCellEditor(Field<? extends Object> field) {
		super(field);
	}
	
	/*
	 * GRBK-900 
	 * We basically don't allow our editor to blur.  We do this because if we do allow it to blur it will cause
	 * fields to lose focus when we don't want them to do so.  
	 * 
	 * See: http://www.sencha.com/forum/showthread.php?132296-Grid-KEY_ENTER-handling-in-onEditorKey%28%29-doesn-t-the-cell-edit-anymore
	 * (non-Javadoc)
	 * @see com.extjs.gxt.ui.client.widget.Editor#triggerBlur(com.extjs.gxt.ui.client.widget.form.TriggerField)
	 */

	@Override
	protected void triggerBlur(TriggerField field) {
		return; 
	}
}
