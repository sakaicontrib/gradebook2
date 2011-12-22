/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.core.client.GWT;

public class SearchFieldListBox extends Composite {
	
	
	private ComboBox<SearchFieldData> searchFieldComboBox;
	private I18nConstants i18n = null;
	private List<SearchFieldData> searchFieldList = null;
	
	public SearchFieldListBox() {
		this(true);
	}
	
	public SearchFieldListBox(boolean doLoad) {
		i18n = (I18nConstants) GWT.create(I18nConstants.class);

		searchFieldList = new ArrayList<SearchFieldData>();
		searchFieldList.add(new SearchFieldData(AppConstants.USER_FIELD_DISPLAY_NAME, i18n.searchByDisplayName()));
		searchFieldList.add(new SearchFieldData(AppConstants.USER_FIELD_LAST_NAME_FIRST, i18n.searchByLastNameFirst()));
		searchFieldList.add(new SearchFieldData(AppConstants.USER_FIELD_DISPLAY_ID, i18n.searchByDisplayId()));
		searchFieldList.add(new SearchFieldData(AppConstants.USER_FIELD_EMAIL, i18n.searchByEmail()));

		ListStore<SearchFieldData> listStore = new ListStore<SearchFieldData>();
		listStore.add(searchFieldList);

		searchFieldComboBox = new ComboBox<SearchFieldData>();
		searchFieldComboBox.setValue(searchFieldList.get(0));
		searchFieldComboBox.setDisplayField(SearchFieldData.LABEL);  
		searchFieldComboBox.setWidth(150);  
		searchFieldComboBox.setStore(listStore);
		searchFieldComboBox.setTriggerAction(TriggerAction.ALL); 
		
		searchFieldComboBox.setEditable(false);
		searchFieldComboBox.setAllowBlank(false);
		searchFieldComboBox.setSelectOnFocus(true);
			
		initComponent(searchFieldComboBox);

	}
	
	public ModelData getValue() {
		return this.searchFieldComboBox.getValue();
	}
	
	public void reset() {
		if(searchFieldList == null || searchFieldList.isEmpty()) {
			this.searchFieldComboBox.clear();
		} else {
			this.searchFieldComboBox.setValue(searchFieldList.get(0));
		}
			
	}
	
	public class SearchFieldData extends BaseModelData implements ModelData 
	{
		
		private static final long serialVersionUID = 1L;
		public static final String ID = "id";
		public static final String LABEL = "label";
		
		public SearchFieldData() {
			
		}

		public SearchFieldData(String id, String label) {
			set(SearchFieldData.ID, id);
			set(SearchFieldData.LABEL, label);
		}
		
		/**
		 * @return the id
		 */
		public String getId() {
			return get(SearchFieldData.ID);
		}
		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			set(SearchFieldData.ID, id);
		}
		/**
		 * @return the label
		 */
		public String getLabel() {
			return get(SearchFieldData.LABEL);
		}
		/**
		 * @param label the label to set
		 */
		public void setLabel(String label) {
			set(SearchFieldData.LABEL, label);
		}
	}
	
}
