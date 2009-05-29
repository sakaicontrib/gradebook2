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

import java.util.Date;

import org.sakaiproject.gradebook.gwt.client.action.Action.EntityType;
import org.sakaiproject.gradebook.gwt.client.gxt.controller.AppController;
import org.sakaiproject.gradebook.gwt.client.gxt.controller.ServiceController;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.NotificationEvent;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.state.CookieProvider;
import com.extjs.gxt.ui.client.state.StateManager;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.StatusCodeException;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GradebookApplication implements EntryPoint {
	
	// One year in millisecond is 365 days x 24 hours x 60 minutes x 60 seconds x 1000 milliseconds
	private static long ONE_YEAR = 31536000000l;
	
	private Dispatcher dispatcher;
	private Gradebook2RPCServiceAsync dataService;
	private int screenHeight = 600;
	
    public GradebookApplication() {
    	//GXT.setDefaultTheme(Theme.GRAY, true);
    	
    	Date expiryDate = new Date(new Date().getTime() + ONE_YEAR);
    	CookieProvider provider = new CookieProvider("/", expiryDate, null, true);
        StateManager.get().setProvider(provider);
    }
	
	public void onModuleLoad() {
		dispatcher = Dispatcher.get();
		dispatcher.addController(new AppController());
		dispatcher.addController(new ServiceController());
		
		I18nConstants i18n = (I18nConstants) GWT.create(I18nConstants.class);
		
		if (dataService == null) {
			dataService = GWT.create(Gradebook2RPCService.class);
			EndpointUtil.setEndpoint((ServiceDefTarget) dataService);
		}
		
		if (dataService == null) {
			MessageBox box = new MessageBox();
			box.setButtons(MessageBox.OK);
			box.setIcon(MessageBox.INFO);
			box.setTitle("Information");
			box.setMessage("No service detected");
			box.show();
			return;
		}
		
		Registry.register(AppConstants.SERVICE, dataService);
		Registry.register(AppConstants.I18N, i18n);

		if (GWT.isScript())
			resizeMainFrame(screenHeight + 20);
		
		getApplicationModel(0);
		
	}
	
	private void getApplicationModel(final int i) {
		AsyncCallback<ApplicationModel> callback = 
			new AsyncCallback<ApplicationModel>() {

				public void onFailure(Throwable caught) {
					GXT.hideLoadingPanel("loading");
					
					if (caught instanceof StatusCodeException) {
						System.out.println("Status code exception!");
						caught.printStackTrace();
					} 
					
					// If this is the first try, then give it another shot
					if (i == 0)
						getApplicationModel(i+1);
					else
						dispatcher.dispatch(GradebookEvents.Exception.getEventType(), new NotificationEvent(caught));
				}

				public void onSuccess(ApplicationModel result) {
					GXT.hideLoadingPanel("loading");
					
					dispatcher.dispatch(GradebookEvents.Startup.getEventType(), result);
				}
			
		};
		
		dataService.get(null, null, EntityType.APPLICATION, null, null, SecureToken.get(), callback);

	}
	
	
	private native Document getWindowParentDocument() /*-{
	    	return $wnd.parent.document
	}-*/;
	
	private void resizeMainFrame(int setHeight) {
		Document doc = getWindowParentDocument();
		NodeList<Element> nodeList = doc.getElementsByTagName("iframe");
		for (int i = 0; i < nodeList.getLength(); i++) {
			IFrameElement iframe = (IFrameElement) nodeList.getItem(i);
			if (iframe.getId().startsWith("Main")) {
				iframe.setAttribute("style", "height: " + setHeight + "px;");
				break;
			}
		}
	}

}
