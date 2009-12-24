package org.sakaiproject.gradebook.gwt.client;

import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;

import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface Gradebook2RPCServiceAsync {

	<X extends ModelData> void create(String entityUid, Long entityId, X model, EntityType type, String secureToken, AsyncCallback<X> callback);
	
	<X extends ModelData> void get(String entityUid, Long entityId, EntityType type, String learnerUid, Boolean doShowAll, String secureToken, AsyncCallback<X> callback);
	
	<X extends ModelData, Y extends ListLoadResult<X>> void getPage(String uid, Long id, EntityType type, PagingLoadConfig config, String secureToken, AsyncCallback<Y> callback);
	
	<X extends ModelData> void update(X model, EntityType type, UserEntityUpdateAction<ModelData> action, String secureToken, AsyncCallback<X> callback);
	
	<X extends ModelData> void delete(String entityUid, Long entityId, X model, EntityType type, String secureToken, AsyncCallback<X> callback);

}
