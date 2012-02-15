package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import java.util.EnumSet;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.RestBuilder;
import org.sakaiproject.gradebook.gwt.client.RestBuilder.Method;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityModelComparer;
import org.sakaiproject.gradebook.gwt.client.model.key.SectionKey;
import org.sakaiproject.gradebook.gwt.client.resource.GradebookResources;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.core.client.GWT;

public class SectionsComboBox<D extends ModelData> extends Composite {
	
	private ComboBox<D> sectionComboBox = null;
	private I18nConstants i18n = null;
	private GradebookResources resources;
	private ListLoader<ListLoadResult<D>> sectionsLoader;
	
	// The default constructor automatically loads all the sections
	public SectionsComboBox() {
		this(true);
	}
	
	// The boolean argument doLoad tells the component to load or not to load the data
	public SectionsComboBox(boolean doLoad) {
		
		this.i18n = Registry.get(AppConstants.I18N);
		this.resources = Registry.get(AppConstants.RESOURCES);
		
		sectionsLoader = RestBuilder.getDelayLoader(AppConstants.LIST_ROOT, 
													EnumSet.allOf(SectionKey.class),
													Method.GET,
													null,
													null,
													GWT.getModuleBaseURL(),
													AppConstants.REST_FRAGMENT,
													AppConstants.SECTION_FRAGMENT);
		sectionsLoader.setRemoteSort(true);
		ListStore<D> sectionStore = new ListStore<D>(sectionsLoader);
		sectionStore.setModelComparer(new EntityModelComparer<D>(SectionKey.S_ID.name()));
		
		sectionComboBox = new ComboBox<D>();
		sectionComboBox.setEmptyText(i18n.sectionsEmptyText());
		sectionComboBox.setDisplayField(SectionKey.S_NM.name());
		//sectionComboBox.addStyleName(resources.css().gbComboBoxWidth());
		//sectionComboBox.setAutoWidth(true);
		sectionComboBox.setStore(sectionStore);
		sectionComboBox.setTypeAhead(true);
		sectionComboBox.setTriggerAction(TriggerAction.ALL);
		sectionComboBox.setEditable(false);
		
		if(doLoad) {
			sectionsLoader.load();
		}
		
		initComponent(sectionComboBox);

	}
	
	// Add a selection change listener
	public void addSelectionChangedListener(SelectionChangedListener<D> selectionChangeListener) {
		
		sectionComboBox.addSelectionChangedListener(selectionChangeListener);
	}
	
	// Return ComboBox selection
	public List<D> getSelection() {
		
		if(null != sectionComboBox) {
			return sectionComboBox.getSelection();
		}
		else {
			return null;
		}
	}
	
	// Load data
	public void load() {
		
		if(null != sectionsLoader) {
			sectionsLoader.load();
		}
	}
	
	// Reset ComboBox
	public void reset() {
		
		if(null != sectionComboBox) {
			sectionComboBox.reset();
		}
	}
	
	public ListStore<D> getStore() {
		return sectionComboBox.getStore();
	}
}
