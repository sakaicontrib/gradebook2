package org.sakaiproject.gradebook.gwt.client.wizard.formpanel;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.gxt.type.ExportType;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;


public class ImportExportTypeComboBox extends ComboBox<ModelData> {
	
	private static I18nConstants i18n = Registry.get(AppConstants.I18N);
	
	
	public ImportExportTypeComboBox() {
		ListStore<ModelData> exportTypeStore = new ListStore<ModelData>();
		exportTypeStore.setModelComparer(new EntityModelComparer<ModelData>(ExportType.DISPLAY_NAME));
		for (ExportType type : ExportType.values()){
			exportTypeStore.add(ExportType.getExportTypeModel(type));
		}

		setStore(exportTypeStore);
		setDisplayField(ExportType.DISPLAY_NAME);
		setFieldLabel(i18n.exportFormPanelLabelExportType());
		setEmptyText(i18n.exportFormPanelExportTypeEmptyText());
		setTypeAhead(true);
		setEditable(false);
		setTriggerAction(TriggerAction.ALL);
	}
	
	public void setSelectionByExportType (ExportType type) {
		
		if( null != type ) {
			for (ModelData item : store.getModels()) {
				if (type.getDisplayName().equals(item.get(ExportType.DISPLAY_NAME))) {
					setValue(item);
					return;
				}
			}
		}
		
		setValue(null);
	}
}
