package org.sakaiproject.gradebook.gwt.client;


import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.exceptions.SecurityException;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.RemoteService;

public interface Gradebook2RPCService extends RemoteService {

	<X extends BaseModel> X create(String entityUid, Long entityId, X model, EntityType type, String secureToken) 
	throws BusinessRuleException, FatalException, SecurityException;
	
	<X extends BaseModel> X get(String entityUid, Long entityId, EntityType type, String learnerUid, Boolean doShowAll, String secureToken) 
	throws FatalException, SecurityException;
	
	<X extends BaseModel, Y extends ListLoadResult<X>> Y getPage(String uid, Long id, EntityType type, PagingLoadConfig config, String secureToken) 
	throws FatalException, SecurityException;
	
	<X extends BaseModel> X update(X model, EntityType type, UserEntityUpdateAction<StudentModel> action, String secureToken) 
	throws InvalidInputException, FatalException, SecurityException;
	
	<X extends BaseModel> X delete(String entityUid, Long entityId, X model, EntityType type, String secureToken)
	throws FatalException, SecurityException;
	
}
