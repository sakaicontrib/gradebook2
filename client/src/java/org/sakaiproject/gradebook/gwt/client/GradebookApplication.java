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
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.state.CookieProvider;
import com.extjs.gxt.ui.client.state.StateManager;
import com.extjs.gxt.ui.client.util.Theme;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GradebookApplication implements EntryPoint {
	
	// One year in millisecond is 365 days x 24 hours x 60 minutes x 60 seconds x 1000 milliseconds
	private static long ONE_YEAR = 31536000000l;
	
	private Gradebook2RPCServiceAsync dataService;
	private int screenHeight = 600;
	
    public GradebookApplication() {
    	GXT.setDefaultTheme(Theme.GRAY, true);
    	
    	Date expiryDate = new Date(new Date().getTime() + ONE_YEAR);
    	CookieProvider provider = new CookieProvider("/", expiryDate, null, true);
        StateManager.get().setProvider(provider);
    }
	
	public void onModuleLoad() {
		final Dispatcher dispatcher = Dispatcher.get();
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

		
		AsyncCallback<ApplicationModel> callback = 
			new AsyncCallback<ApplicationModel>() {

				public void onFailure(Throwable caught) {
					GXT.hideLoadingPanel("loading");
					dispatcher.dispatch(GradebookEvents.Exception.getEventType(), caught);
				}

				public void onSuccess(ApplicationModel result) {
					GXT.hideLoadingPanel("loading");
					if (GWT.isScript())
						resizeMainFrame(screenHeight + 20);
					
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
	 		for(int i = 0; i < nodeList.getLength(); i++) {
	 		    IFrameElement iframe = (IFrameElement) nodeList.getItem(i);
	 		    if(iframe.getId().startsWith("Main")) {
	 		        iframe.setAttribute("style", "height: " + setHeight + "px;");
	 		        break;
	 		    }
	 		}
	 	}

	/*
	// FIXME: This needs to be cleaned up
	public native void resizeMainFrame(String placementId, int setHeight) /--*-{
		
		//	$wnd.alert("Is " + placementId + " equal to Mainff1e8b82x01e4x4d00x9a17x3982e11d5bd1 ? ");
	
		
			var frame = $wnd.parent.document.getElementById(placementId);

			if (frame)
		   	{
		       // reset the scroll
		 //      $wnd.parent.window.scrollTo(0,0);
	
		       var objToResize = (frame.style) ? frame.style : frame;
		       var height;                
		       var offsetH = $wnd.parent.document.body.offsetHeight;
		       
		       //$wnd.alert($doc.body.offsetHeight + " and " + offsetH);
		       
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
	
		       // capture my current scroll position
	//	       var scroll = findScroll();
	
		       // resize parent frame (this resets the scroll as well)
		       objToResize.height=newHeight + "px";
	
		       // reset the scroll, unless it was y=0)
	//	       if (scroll[1] > 0)
	//	       {
	//	           var position = findPosition(frame);
	//	           $wnd.parent.window.scrollTo(position[0]+scroll[0], position[1]+scroll[1]);
	//	       }
		       
		       //objToResize.height=offsetH + "px";
		       
		    } 
		    
	 }-*--/;


	public native void findScroll() /--*-{
	 	var x = 0;
	 	var y = 0;
	 	if (self.pageYOffset)
	 	{
	 		x = self.pageXOffset;
	 		y = self.pageYOffset;
	 	}
	 	else if ($doc.documentElement && $doc.documentElement.scrollTop)
	 	{
	 		x = $doc.documentElement.scrollLeft;
	 		y = $doc.documentElement.scrollTop;
	 	}
	 	else if ($doc.body)
	 	{
	 		x = $doc.body.scrollLeft;
	 		y = $doc.body.scrollTop;
	 	}
	 	
	 	return [x,y];
	 }-*--/;
	*/
}
