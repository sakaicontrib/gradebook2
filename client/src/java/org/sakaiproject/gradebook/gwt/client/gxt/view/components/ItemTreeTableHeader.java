/**********************************************************************************
*
* $Id:$
*
***********************************************************************************
*
* Copyright (c) 2008, 2009 The Regents of the University of California
*
* Licensed under the
* Educational Community License, Version 2.0 (the "License"); you may
* not use this file except in compliance with the License. You may
* obtain a copy of the License at
* 
* http://www.osedu.org/licenses/ECL-2.0
* 
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an "AS IS"
* BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
* or implied. See the License for the specific language governing
* permissions and limitations under the License.
*
**********************************************************************************/

package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.Gradebook2RPCServiceAsync;
import org.sakaiproject.gradebook.gwt.client.SecureToken;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.model.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemKey;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.table.TableColumn;
import com.extjs.gxt.ui.client.widget.treetable.TreeTable;
import com.extjs.gxt.ui.client.widget.treetable.TreeTableHeader;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ItemTreeTableHeader extends TreeTableHeader {

	private boolean isPropagatingChanges;
	
	public ItemTreeTableHeader(TreeTable treeTable) {
		super(treeTable);
		this.isPropagatingChanges = false;
	}
	
	public void hideColumn(int index) {
		super.showColumn(index, false);
		if (isPropagatingChanges) {
			TableColumn column = columnModel.getColumn(index);
			if (column != null)
				saveChanges(column.getId(), true);
		}
	}
	
	public void showColumn(int index) {
		super.showColumn(index, true);
		if (isPropagatingChanges) {
			TableColumn column = columnModel.getColumn(index);
			if (column != null)
				saveChanges(column.getId(), false);
		}
	}
	
	protected Menu onShowContextMenu(final TableColumn column) {
	    Menu menu = super.onShowContextMenu(column);

	    return menu;
	  }
	
	private void saveChanges(String columnId, boolean hidden) {
		GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		
		ConfigurationModel configModel = selectedGradebook.getConfigurationModel();
		
		String field = lookupFieldByColumnId(selectedGradebook, columnId);
		
		if (field == null)
			return;
		
		
		if (configModel.isColumnHidden(AppConstants.ITEMTREE_HEADER, field, !hidden) != hidden) {
			ConfigurationModel model = new ConfigurationModel(selectedGradebook.getGradebookId());
			model.setColumnHidden(AppConstants.ITEMTREE_HEADER, field, Boolean.valueOf(hidden));
			
			Dispatcher.forwardEvent(GradebookEvents.Configuration.getEventType(), configModel);
			
			/*
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
			
			service.update(model, EntityType.CONFIGURATION, null, SecureToken.get(), callback);*/
		}
	}

	public boolean isPropagatingChanges() {
		return isPropagatingChanges;
	}

	public void setPropagatingChanges(boolean isPropagatingChanges) {
		this.isPropagatingChanges = isPropagatingChanges;
	}
	
	private String lookupFieldByColumnId(GradebookModel selectedGradebook, String columnId) {
		if (columnId.equals(ItemKey.PERCENT_COURSE_GRADE.name()))
			return AppConstants.ITEMTREE_PERCENT_GRADE;
		else if (columnId.equals(ItemKey.PERCENT_CATEGORY.name()))
			return AppConstants.ITEMTREE_PERCENT_CATEGORY;
		
		CategoryType categoryType = selectedGradebook.getGradebookItemModel().getCategoryType();
		
		switch (categoryType) {
		case NO_CATEGORIES:
		case SIMPLE_CATEGORIES:
			return AppConstants.ITEMTREE_POINTS_NOWEIGHTS;
		case WEIGHTED_CATEGORIES:
			return AppConstants.ITEMTREE_POINTS_WEIGHTS;
		}
		
		return null;
	}
}
