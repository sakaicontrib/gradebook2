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

import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.controller.StartupController;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.model.AuthModel;
import org.sakaiproject.gradebook.gwt.client.resource.GradebookResources;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GradebookApplication implements EntryPoint {

	private Dispatcher dispatcher;
	private Gradebook2RPCServiceAsync dataService;
	private GradebookResources resources;
	private I18nConstants i18n;
	private int screenHeight = 580;
	
    public GradebookApplication() {

    }
	
	public void onModuleLoad() {
		i18n = (I18nConstants) GWT.create(I18nConstants.class);
		resources = GWT.create(GradebookResources.class);
		resources.css().ensureInjected();

		dispatcher = Dispatcher.get();
		
		dispatcher.addController(new StartupController());
				
		if (dataService == null) {
			dataService = GWT.create(Gradebook2RPCService.class);
			EndpointUtil.setEndpoint((ServiceDefTarget) dataService);
		}
		
		if (dataService == null) {
			GXT.hideLoadingPanel("loading");
			RootPanel.get("alert").add(new Html(i18n.serviceException()));
			return;
		}
		
		Registry.register(AppConstants.RESOURCES, resources);
		Registry.register(AppConstants.VERSION, getVersion());
		Registry.register(AppConstants.SERVICE, dataService);
		Registry.register(AppConstants.I18N, i18n);

		String paramString = getParamString();
		
		AuthModel authModel = null;
		
		if (paramString != null && paramString.length() > 0) {			
			authModel = new AuthModel();
			authModel.parse(paramString);
		}
		
		if (authModel != null && authModel.getPlacementId() != null) {
			readAuthorization(authModel);
		} else {
			getAuthorization(0);
		}
	}
	
	private native String getVersion() /*-{
		return $wnd.gb2_version;
	}-*/;
	
	private native String getParamString() /*-{
    	return $wnd.location.search;
	}-*/;
	
	private void getAuthorization(final int i) {
		AsyncCallback<AuthModel> callback = 
			new AsyncCallback<AuthModel>() {

				public void onFailure(Throwable caught) {
					// If this is the first try, then give it another shot
					if (i == 0)
						getAuthorization(i+1);
					else {
						GXT.hideLoadingPanel("loading");
						RootPanel.get("alert").add(new Html(new StringBuilder().append(i18n.serviceException()).append(": ").append(caught.getMessage()).toString()));
						//dispatcher.dispatch(GradebookEvents.Exception.getEventType(), new NotificationEvent(caught));
					}
				}

				public void onSuccess(AuthModel result) {
					readAuthorization(result);
				}
			
		};
		
		dataService.get(null, null, EntityType.AUTH, null, null, SecureToken.get(), callback);

	}
	
	private void readAuthorization(AuthModel authModel) {
		if (GWT.isScript()) {
			String placementId = authModel.getPlacementId();
			if (placementId != null) {
				String modifiedId = placementId.replace('-', 'x');
				resizeMainFrame("Main" + modifiedId, screenHeight);
			}
		}
		
		dispatcher.dispatch(GradebookEvents.Load.getEventType(), authModel);
		GXT.hideLoadingPanel("loading");
	}
	
	
	
	public native void resizeMainFrame(String placementId, int setHeight) /*-{	
		
			var frame = $wnd.parent.document.getElementById(placementId);

			if (frame)
		   	{
	
		       var objToResize = (frame.style) ? frame.style : frame;
		       var height;                
		       var offsetH = $wnd.parent.document.body.offsetHeight;
		     
		       var innerDocScrollH = null;

		       if (typeof(frame.contentDocument) != 'undefined' || typeof(frame.contentWindow) != 'undefined')
		       {
		           // very special way to get the height from IE on Windows!
		           // note that the above special way of testing for undefined variables is necessary for older browsers
		           // (IE 5.5 Mac) to not choke on the undefined variables.
		           var innerDoc = (frame.contentDocument) ? frame.contentDocument : frame.contentWindow.document;
		           innerDocScrollH = (innerDoc != null) ? innerDoc.body.scrollHeight : null;
		       }
		       
		       if ($wnd.parent.document.all && innerDocScrollH != null)
		       {
		           // IE on Windows only
		           height = innerDocScrollH;
		       }
		       else
		       {
		           // every other browser!
		           height = offsetH;
		       }
	
		       // here we fudge to get a little bigger
		       var newHeight = setHeight;
		     
		       // but not too big!
		       if (newHeight > 32760) newHeight = 32760;
	
		       // resize parent frame (this resets the scroll as well)
		       objToResize.height=newHeight + "px";
	
		    } 
		    
	 }-*/;

}
