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
package org.sakaiproject.gradebook.gwt.client.gxt.custom.widget.grid;

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
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CustomColumnModel extends ColumnModel {

	private String gradebookUid;
	private String gridId;
	
	public CustomColumnModel(String gradebookUid, String gridId, List<ColumnConfig> columns) {
		super(columns);
		this.gradebookUid = gradebookUid;
		this.gridId = gridId;
	}

	@Override
	public void setHidden(int colIndex, boolean hidden) {
		super.setHidden(colIndex, hidden);
		ColumnConfig column = getColumn(colIndex);
		String columnId = column == null ? null : column.getId();
		
		/*GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
		
		ConfigurationModel configModel = selectedGradebook.getConfigurationModel();
		
		if (configModel.isColumnHidden(gridId, columnId) != hidden) {
			ConfigurationModel model = new ConfigurationModel(selectedGradebook.getGradebookId());
			model.setColumnHidden(gridId, columnId, Boolean.valueOf(hidden));
			
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
		}*/
	}
	
}
