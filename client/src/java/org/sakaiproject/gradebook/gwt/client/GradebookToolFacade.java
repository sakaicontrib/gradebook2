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
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.CategoryModel;
import org.sakaiproject.gradebook.gwt.client.model.EntityModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;

public interface GradebookToolFacade extends RemoteService {
	
	public static final int VIEW_ALL_SECTIONS = 0;
	
	public static final int GRADING_DIRECTION_HORIZONTAL = 0;
	public static final int GRADING_DIRECTION_VERTICAL = 1;
	
	public static final int NAVIGATION_BEHAVIOR_PAGE = 0;
	public static final int NAVIGATION_BEHAVIOR_WRAP = 1;
	public static final int NAVIGATION_BEHAVIOR_NOWRAP = 2;
	
	
	<X extends EntityModel> X createEntity(UserEntityCreateAction<X> action) throws FatalException;
	
	<X extends ItemModel> X createItemEntity(UserEntityCreateAction<X> action) throws FatalException;
	
	<X extends EntityModel> X getEntity(UserEntityGetAction<X> action) throws FatalException;
	
	<X extends EntityModel> List<X> getEntityList(UserEntityGetAction<X> action) throws FatalException;
	
	<X extends EntityModel> PagingLoadResult<X> getEntityPage(PageRequestAction action, PagingLoadConfig config) throws FatalException;

	<X extends ItemModel> X getEntityTreeModel(String gradebookUid, X parent);
	
	List<CategoryModel> recalculateEqualWeightingCategories(String gradebookUid, Long gradebookId, Boolean isEqualWeighting);
	
	<X extends ItemModel> X updateItemEntity(UserEntityUpdateAction<X> action) throws InvalidInputException, FatalException;
	
	<X extends EntityModel> X updateEntity(UserEntityUpdateAction<X> action) throws InvalidInputException, FatalException;
	
	<X extends EntityModel> List<X> updateEntityList(UserEntityUpdateAction<X> action) throws InvalidInputException, FatalException;

}

