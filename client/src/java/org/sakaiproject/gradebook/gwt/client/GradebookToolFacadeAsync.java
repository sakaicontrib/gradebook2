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
package org.sakaiproject.gradebook.gwt.client;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.action.PageRequestAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityCreateAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityGetAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.model.EntityModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GradebookToolFacadeAsync {
		
	<X extends EntityModel> void createEntity(UserEntityCreateAction<X> action, AsyncCallback<X> callback);

	<X extends ItemModel> void createItemEntity(UserEntityCreateAction<X> action, AsyncCallback<X> callback);
	
	<X extends EntityModel> void getEntity(UserEntityGetAction<X> action, AsyncCallback<X> callback);
	
	<X extends EntityModel> void getEntityList(UserEntityGetAction<X> action, AsyncCallback<List<X>> callback);
	
	<X extends EntityModel> void getEntityPage(PageRequestAction action, PagingLoadConfig config, AsyncCallback<PagingLoadResult<X>> callback);
	
	<X extends ItemModel> void getEntityTreeModel(String gradebookUid, X parent, AsyncCallback<X> callback);
	
	//void recalculateEqualWeightingCategories(String gradebookUid, Long gradebookId, Boolean isEqualWeighting, AsyncCallback<List<CategoryModel>> callback);

	<X extends ItemModel> void updateItemEntity(UserEntityUpdateAction<X> action, AsyncCallback<X> callback);
	
	<X extends EntityModel> void updateEntity(UserEntityUpdateAction<X> action, AsyncCallback<X> callback);
	
	<X extends EntityModel> void updateEntityList(UserEntityUpdateAction<X> action, AsyncCallback<List<X>> callback);

}
