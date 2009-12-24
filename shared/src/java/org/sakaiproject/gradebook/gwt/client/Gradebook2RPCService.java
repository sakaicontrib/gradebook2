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


import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.exceptions.SecurityException;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;

public interface Gradebook2RPCService extends RemoteService {

	<X extends ModelData> X create(String entityUid, Long entityId, X model, EntityType type, String secureToken) 
	throws BusinessRuleException, FatalException, SecurityException;
	
	<X extends ModelData> X get(String entityUid, Long entityId, EntityType type, String learnerUid, Boolean doShowAll, String secureToken) 
	throws FatalException, SecurityException;
	
	<X extends ModelData, Y extends ListLoadResult<X>> Y getPage(String uid, Long id, EntityType type, PagingLoadConfig config, String secureToken) 
	throws FatalException, SecurityException;
	
	<X extends ModelData> X update(X model, EntityType type, UserEntityUpdateAction<ModelData> action, String secureToken) 
	throws InvalidInputException, FatalException, SecurityException;
	
	<X extends ModelData> X delete(String entityUid, Long entityId, X model, EntityType type, String secureToken)
	throws FatalException, SecurityException;
	
}
