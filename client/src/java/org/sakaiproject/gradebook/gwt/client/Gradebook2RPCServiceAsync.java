package org.sakaiproject.gradebook.gwt.client;

import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Gradebook2RPCServiceAsync {

	<X extends BaseModel> void create(String entityUid, Long entityId, X model, EntityType type, AsyncCallback<X> callback);
	
	<X extends BaseModel> void get(String entityUid, Long entityId, EntityType type, String learnerUid, Boolean doShowAll, AsyncCallback<X> callback);
	
	<X extends BaseModel, Y extends ListLoadResult<X>> void getPage(String uid, Long id, EntityType type, PagingLoadConfig config, AsyncCallback<Y> callback);
	
	<X extends BaseModel> void update(X model, EntityType type, UserEntityUpdateAction<StudentModel> action, AsyncCallback<X> callback);
	
	<X extends BaseModel> void delete(X model, AsyncCallback<X> callback);

}
