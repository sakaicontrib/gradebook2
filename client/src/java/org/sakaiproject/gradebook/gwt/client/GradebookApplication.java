/**********************************************************************************
*
* Copyright (c) 2008, 2009, 2010, 2011 The Regents of the University of California
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

import org.sakaiproject.gradebook.gwt.client.gin.WidgetInjector;
import org.sakaiproject.gradebook.gwt.client.gxt.JsonUtil;
import org.sakaiproject.gradebook.gwt.client.gxt.controller.NotificationController;
import org.sakaiproject.gradebook.gwt.client.gxt.controller.StartupController;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityOverlay;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationSetup;
import org.sakaiproject.gradebook.gwt.client.model.AuthModel;
import org.sakaiproject.gradebook.gwt.client.resource.GradebookResources;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GradebookApplication implements EntryPoint  {

	private Dispatcher dispatcher;
	private GradebookResources resources;
	private I18nConstants i18n;
	private I18nMessages i18nTemplates;
	private int screenHeight = 580;
	private WidgetInjector injector = GWT.create(WidgetInjector.class);
	
    public GradebookApplication() {

    }
	
	public void onModuleLoad() {
		i18n = (I18nConstants) GWT.create(I18nConstants.class);
		i18nTemplates = (I18nMessages) GWT.create(I18nMessages.class);
		resources = GWT.create(GradebookResources.class);
		resources.css().ensureInjected();

		dispatcher = Dispatcher.get();
		
		dispatcher.addController(new StartupController());
		dispatcher.addController(new NotificationController());
				
		Registry.register(AppConstants.RESOURCES, resources);
		Registry.register(AppConstants.VERSION, getVersion());
		Registry.register(AppConstants.I18N, i18n);
		Registry.register(AppConstants.I18N_TEMPLATES, i18nTemplates);		
		

		getApplicationSetup();		
	}
	
	private String getVersion() {
		String version = Cookies.getCookie(AppConstants.VERSION_COOKIE_NAME);
		
		if (version == null)
			version = "";
		
		return version;
	}

	/*
	 * This method get both the AuthModel and ApplicationSetup objects
	 */
	private void getApplicationSetup() {

		RestBuilder builder = RestBuilder.getInstance(RestBuilder.Method.GET, 
				GWT.getModuleBaseURL(),
				AppConstants.REST_FRAGMENT,
				AppConstants.STARTUP_FRAGMENT);

		builder.sendRequest(200, 202, null, new RestCallback() {

			public void onError(Request request, Throwable exception) {
				
				warnUser(exception);
			}

			public void onFailure(Request request, Throwable exception) {

				warnUser(exception);
			}

			public void onSuccess(Request request, Response response) {

				EntityOverlay overlay = JsonUtil.toOverlay(response.getText());

				ApplicationSetup applicationSetup = new ApplicationModel(overlay);
				
				String authToken = applicationSetup.getAuthorizationDetails();
				
				AuthModel authModel = new AuthModel();
				authModel.parse(authToken);
				
				if (GWT.isScript()) {
					String placementId = authModel.getPlacementId();
					if (placementId != null) {
						String modifiedId = placementId.replace('-', 'x');
						resizeMainFrame("Main" + modifiedId, screenHeight);
					}
				}
				
				processApplicationSetup(authModel, applicationSetup);
			}
		});
	}
	
	private void warnUser(Throwable e) {
		
		GXT.hideLoadingPanel(AppConstants.LOADINGPANELID);
		RootPanel.get("mainapp").clear();
		RootPanel rootPanel = RootPanel.get("alert");
		rootPanel.clear();
		String warning = e == null ? i18n.gradebookApplicationWarnUser() : e.getMessage();
		rootPanel.add(new HTML(warning));
	}
	
	private void processApplicationSetup(AuthModel authModel, ApplicationSetup applicationSetup) {

		Registry.register(AppConstants.APP_MODEL, applicationSetup);
		
		Registry.register(AppConstants.WIDGET_INJECTOR, injector);
		
		dispatcher.dispatch(GradebookEvents.Load.getEventType(), authModel);
		GXT.hideLoadingPanel(AppConstants.LOADINGPANELID);
	}
	
	private void onApplicationModelFailure(int i, Throwable caught) {
		
		// If this is the first try, then give it another shot
		dispatcher.dispatch(GradebookEvents.Exception.getEventType(), new NotificationEvent(caught, i18n.gradebookApplicaitonFailure()));
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
