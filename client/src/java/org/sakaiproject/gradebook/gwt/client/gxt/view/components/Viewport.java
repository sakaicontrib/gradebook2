package org.sakaiproject.gradebook.gwt.client.gxt.view.components;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Document;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;

public class Viewport extends LayoutContainer {

	  private String loadingPanelId = "loading";
	  private boolean enableScroll;
	  private boolean iniframe = true;

	  public Viewport() {
		  
	    monitorWindowResize = true;
	  }

	  /**
	   * Returns the window resizing state.
	   * 
	   * @return true if window scrolling is enabled
	   */
	  public boolean getEnableScroll() {
	    return enableScroll;
	  }

	  /**
	   * The loading panel id.
	   * 
	   * @return the id
	   */
	  public String getLoadingPanelId() {
	    return loadingPanelId;
	  }

	  public void onAttach() {
	    super.onAttach();
	    GXT.hideLoadingPanel(loadingPanelId);
	    // see if we're in an iframe. 
	    Element elt = DOM.getElementById("container");
	    if (elt != null) {
		String className = elt.getClassName();
		if (className != null && className.indexOf("Mrphs") >= 0)
		    iniframe = false;
	    }
	    elt = DOM.getElementById("mainapp");
	    // if not in iframe, always want scroll enabled
	    // I'm guessing they disable it in an iframe to avoid double scrollbar
	    if (!iniframe) {
		this.enableScroll = true;
		Window.enableScrolling(true);
           } else
              setEnableScroll(enableScroll);

	    setSize(elt.getClientWidth(), Window.getClientHeight());
	  }

	  /**
	   * Sets whether window scrolling is enabled.
	   * 
	   * @param enableScroll the window scroll state
	   */
	  public void setEnableScroll(boolean enableScroll) {
	    // Only disable scroll in iframe
	    if (!iniframe)
		return;
	    this.enableScroll = enableScroll;
	    Window.enableScrolling(enableScroll);
	  }

	  /**
	   * The element id of the loading panel which will be hidden when the viewport
	   * is attached (defaults to 'loading').
	   * 
	   * @param loadingPanelId the loading panel element id
	   */
	  public void setLoadingPanelId(String loadingPanelId) {
	    this.loadingPanelId = loadingPanelId;
	  }

	  @Override
	  protected void onRender(Element parent, int index) {
	    super.onRender(parent, 0);
	  }

	  @Override
	  protected void onWindowResize(final int width, final int height) {
	    Element elt = DOM.getElementById("mainapp");
	    setSize(elt.getClientWidth(), Window.getClientHeight());
	  }
}
