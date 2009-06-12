package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCServiceAsync;
import org.sakaiproject.gradebook.gwt.client.SecureToken;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.treetable.TreeTable;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableHeader;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ItemTreeTableHeader extends TreeTableHeader {

	public ItemTreeTableHeader(TreeTable treeTable) {
		super(treeTable);
	}
	
	public void hideColumn(int index) {
		super.showColumn(index, false);
		saveChanges(String.valueOf(index), true);
	}
	
	public void showColumn(int index) {
		super.showColumn(index, true);
		saveChanges(String.valueOf(index), false);
	}
	
	private void saveChanges(String columnId, boolean hidden) {
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		
		ConfigurationModel configModel = selectedGradebook.getConfigurationModel();
		
		if (configModel.isColumnHidden(AppConstants.ITEMTREE, columnId, !hidden) != hidden) {
			ConfigurationModel model = new ConfigurationModel(selectedGradebook.getGradebookId());
			model.setColumnHidden(AppConstants.ITEMTREE, columnId, Boolean.valueOf(hidden));
			
			Gradebook2RPCServiceAsync service = Registry.get(AppConstants.SERVICE);
			
			AsyncCallback<ConfigurationModel> callback = new AsyncCallback<ConfigurationModel>() {
	
				public void onFailure(Throwable caught) {
					// FIXME: Should we notify the user when this fails?
				}
	
				public void onSuccess(ConfigurationModel result) {
					GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
					ConfigurationModel configModel = selectedGradebook.getConfigurationModel();
					
					Collection<String> propertyNames = result.getPropertyNames();
					if (propertyNames != null) {
						List<String> names = new ArrayList<String>(propertyNames);
						
						for (int i=0;i<names.size();i++) {
							String name = names.get(i);
							String value = result.get(name);
							configModel.set(name, value);
						}
					}
				}
				
			};
			
			service.update(model, EntityType.CONFIGURATION, null, SecureToken.get(), callback);
		}
	}
	

}
