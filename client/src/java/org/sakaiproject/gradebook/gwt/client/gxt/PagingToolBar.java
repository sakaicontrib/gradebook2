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
/*
 * This file is a very close copy of com.extjs.gxt.ui.client.widget.PagingToolBar from 
 * the GXT (Ext GWT) SDK. It is copied purely because the original java class did not easily
 * allow for extension in terms of where new items were added to the toolbar. The original 
 * class is licensed under the GPLv3 with the applicable FLOSS exemption for inclusion in
 * an ECL-2.0 project. 
 * 
 * Ext GWT - Ext for GWT
 * Copyright(c) 2007, 2008, Ext JS, LLC.
 * licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package org.sakaiproject.gradebook.gwt.client.gxt;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.messages.MyMessages;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.toolbar.AdapterToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.TextToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolItem;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class PagingToolBar extends Component implements Listener {

	public class PagingToolBarMessages {
	    private String afterPageText;
	    private String beforePageText;
	    private String displayMsg;
	    private String emptyMsg;
	    private String firstText;
	    private String lastText;
	    private String nextText;
	    private String prevText;
	    private String refreshText;

	    /**
	     * Returns the after page text.
	     * 
	     * @return the after page text
	     */
	    public String getAfterPageText() {
	      return afterPageText;
	    }

	    /**
	     * Returns the before page text.
	     * 
	     * @return the before page text
	     */
	    public String getBeforePageText() {
	      return beforePageText;
	    }

	    /**
	     * Returns the display message.
	     * 
	     * @return the display message.
	     */
	    public String getDisplayMsg() {
	      return displayMsg;
	    }

	    /**
	     * Returns the empty message.
	     * 
	     * @return the empty message
	     */
	    public String getEmptyMsg() {
	      return emptyMsg;
	    }

	    public String getFirstText() {
	      return firstText;
	    }

	    /**
	     * Returns the last text.
	     * 
	     * @return the last text
	     */
	    public String getLastText() {
	      return lastText;
	    }

	    /**
	     * Returns the next text.
	     * 
	     * @return the next ext
	     */
	    public String getNextText() {
	      return nextText;
	    }

	    /**
	     * Returns the previous text.
	     * 
	     * @return the previous text
	     */
	    public String getPrevText() {
	      return prevText;
	    }

	    /**
	     * Returns the refresh text.
	     * 
	     * @return the refresh text
	     */
	    public String getRefreshText() {
	      return refreshText;
	    }

	    /**
	     * Customizable piece of the default paging text (defaults to "of {0}").
	     * 
	     * @param afterPageText the after page text
	     */
	    public void setAfterPageText(String afterPageText) {
	      this.afterPageText = afterPageText;
	    }

	    /**
	     * Customizable piece of the default paging text (defaults to "Page").
	     * 
	     * @param beforePageText the before page text
	     */
	    public void setBeforePageText(String beforePageText) {
	      this.beforePageText = beforePageText;
	    }

	    /**
	     * The paging status message to display (defaults to "Displaying {0} - {1}
	     * of {2}"). Note that this string is formatted using the braced numbers 0-2
	     * as tokens that are replaced by the values for start, end and total
	     * respectively. These tokens should be preserved when overriding this
	     * string if showing those values is desired.
	     * 
	     * @param displayMsg the display message
	     */
	    public void setDisplayMsg(String displayMsg) {
	      this.displayMsg = displayMsg;
	    }

	    /**
	     * The message to display when no records are found (defaults to "No data to
	     * display").
	     * 
	     * @param emptyMsg the empty message
	     */
	    public void setEmptyMsg(String emptyMsg) {
	      this.emptyMsg = emptyMsg;
	    }

	    /**
	     * Customizable piece of the default paging text (defaults to "First Page").
	     * 
	     * @param firstText the first text
	     */
	    public void setFirstText(String firstText) {
	      this.firstText = firstText;
	    }

	    /**
	     * Customizable piece of the default paging text (defaults to "Last Page").
	     * 
	     * @param lastText the last text
	     */
	    public void setLastText(String lastText) {
	      this.lastText = lastText;
	    }

	    /**
	     * Customizable piece of the default paging text (defaults to "Next Page").
	     * 
	     * @param nextText the next text
	     */
	    public void setNextText(String nextText) {
	      this.nextText = nextText;
	    }

	    /**
	     * Customizable piece of the default paging text (defaults to "Previous
	     * Page").
	     * 
	     * @param prevText the prev text
	     */
	    public void setPrevText(String prevText) {
	      this.prevText = prevText;
	    }

	    /**
	     * Customizable piece of the default paging text (defaults to "Refresh").
	     * 
	     * @param refreshText the refresh text
	     */
	    public void setRefreshText(String refreshText) {
	      this.refreshText = refreshText;
	    }

	  }

	  protected PagingLoader loader;
	  protected PagingLoadConfig config;
	  protected int start, pageSize, totalLength;
	  protected int activePage = -1, pages;
	  protected ToolBar toolBar;
	  protected TextToolItem first, prev, next, last, refresh;
	  protected Label afterText;
	  protected Label displayText;
	  protected TextBox pageText;
	  protected PagingToolBarMessages msgs;
	  protected boolean showToolTips = true;

	  private boolean reuseConfig = true;
	  private LoadEvent<PagingLoadConfig, PagingLoadResult> renderEvent;
	  private List<ToolItem> items = new ArrayList<ToolItem>();

	  /**
	   * Creates a new paging tool bar with the given page size.
	   * 
	   * @param pageSize the page size
	   */
	  public PagingToolBar(final int pageSize) {
	    this.pageSize = pageSize;
	    msgs = new PagingToolBarMessages();
	  }

	  /**
	   * Adds an item to the end of the tool bar (pre-render). Can be used to add
	   * custom components to the tool bar. Use {@link AdapterToolItem} to adapt and
	   * component to be used in the tool bar.
	   * 
	   * @param item the item to add
	   */
	  public void add(ToolItem item) {
	    assertPreRender();
	    items.add(item);
	  }

	  /**
	   * Binds the toolbar to the loader.
	   * 
	   * @param loader the loader
	   */
	  public void bind(PagingLoader loader) {
	    if (this.loader != null) {
	      this.loader.removeListener(Loader.BeforeLoad, this);
	      this.loader.removeListener(Loader.Load, this);
	      this.loader.removeListener(Loader.LoadException, this);
	    }
	    this.loader = loader;
	    if (loader != null) {
	      loader.setLimit(pageSize);
	      loader.addListener(Loader.BeforeLoad, this);
	      loader.addListener(Loader.Load, this);
	      loader.addListener(Loader.LoadException, this);
	    }
	  }

	  /**
	   * Clears the current toolbar text.
	   */
	  public void clear() {
	    if (rendered) {
	      pageText.setText("");
	      afterText.setText("");
	      displayText.setText("");
	    }
	  }

	  /**
	   * Moves to the first page.
	   */
	  public void first() {
	    doLoadRequest(0, pageSize);
	  }

	  /**
	   * Returns the active page.
	   * 
	   * @return the active page
	   */
	  public int getActivePage() {
	    return activePage;
	  }

	  /**
	   * Returns the tool bar's messages.
	   * 
	   * @return the messages
	   */
	  public PagingToolBarMessages getMessages() {
	    return msgs;
	  }

	  /**
	   * Returns the current page size.
	   * 
	   * @return the page size
	   */
	  public int getPageSize() {
	    return pageSize;
	  }

	  /**
	   * Returns the total number of pages.
	   * 
	   * @return the
	   */
	  public int getTotalPages() {
	    return pages;
	  }

	  public void handleEvent(BaseEvent be) {
	    switch (be.type) {
	      case Loader.BeforeLoad:
	        disable();
	        break;
	      case Loader.Load:
	        onLoad((LoadEvent) be);
	        enable();
	        break;
	      case Loader.LoadException:
	        enable();
	        break;
	    }
	  }

	  /**
	   * Returns true if the previous load config is reused.
	   * 
	   * @return the reuse config state
	   */
	  public boolean isReuseConfig() {
	    return reuseConfig;
	  }

	  /**
	   * Returns true if tooltip are enabled.
	   * 
	   * @return the show tooltip state
	   */
	  public boolean isShowToolTips() {
	    return showToolTips;
	  }

	  /**
	   * Moves to the last page.
	   */
	  public void last() {
	    int extra = totalLength % pageSize;
	    int lastStart = extra > 0 ? (totalLength - extra) : totalLength - pageSize;
	    doLoadRequest(lastStart, pageSize);
	  }

	  /**
	   * Moves to the last page.
	   */
	  public void next() {
	    doLoadRequest(start + pageSize, pageSize);
	  }

	  /**
	   * Moves the the previos page.
	   */
	  public void previous() {
	    doLoadRequest(Math.max(0, start - pageSize), pageSize);
	  }

	  /**
	   * Refreshes the data using the current configuration.
	   */
	  public void refresh() {
	    loader.load(start, pageSize);
	  }

	  /**
	   * Removes the item from the toolbar (pre-render).
	   * 
	   * @param item the item to remove
	   */
	  public void remove(ToolItem item) {
	    assertPreRender();
	    items.remove(item);
	  }

	  /**
	   * Sets the active page (1 to page count inclusive).
	   * 
	   * @param page the page
	   */
	  public void setActivePage(int page) {
	    if (page > pages) {
	      last();
	      return;
	    }
	    if (page != activePage && page > 0 && page <= pages) {
	      loader.load(--page * pageSize, pageSize);
	    } else {
	      pageText.setText(String.valueOf((int) activePage));
	    }
	  }

	  /**
	   * Sets the tool bar's messages.
	   * 
	   * @param messages the messages
	   */
	  public void setMessages(PagingToolBarMessages messages) {
	    msgs = messages;
	  }

	  /**
	   * Sets the current page size. This method does not effect the data currently
	   * being displayed. The new page size will not be used until the next load
	   * request.
	   * 
	   * @param pageSize the new page size
	   */
	  public void setPageSize(int pageSize) {
	    this.pageSize = pageSize;
	  }

	  /**
	   * True to reuse the previous load config (defaults to true).
	   * 
	   * @param reuseConfig true to reuse the load config
	   */
	  public void setReuseConfig(boolean reuseConfig) {
	    this.reuseConfig = reuseConfig;
	  }

	  /**
	   * Sets if the button tool tips should be displayed (defaults to true,
	   * pre-render).
	   * 
	   * @param showToolTips true to show tool tips
	   */
	  public void setShowToolTips(boolean showToolTips) {
	    this.showToolTips = showToolTips;
	  }

	  protected void doAttachChildren() {
	    ComponentHelper.doAttach(toolBar);
	  }

	  protected void doDetachChildren() {
	    ComponentHelper.doDetach(toolBar);
	  }

	  protected void doLoadRequest(int offset, int limit) {
	    if (reuseConfig && config != null) {
	      config.setOffset(offset);
	      config.setLimit(pageSize);
	      loader.load(config);
	    } else {
	      loader.load(offset, limit);
	    }
	  }

	  protected void onLoad(LoadEvent<PagingLoadConfig, PagingLoadResult> event) {
	    if (!rendered) {
	      renderEvent = event;
	      return;
	    }
	    config = event.config;
	    PagingLoadResult result = event.data;
	    start = result.getOffset();
	    totalLength = result.getTotalLength();
	    activePage = (int) Math.ceil((double) (start + pageSize) / pageSize);
	    pageText.setText(String.valueOf((int) activePage));
	    pages = totalLength < pageSize ? 1 : (int) Math.ceil((double) totalLength / pageSize);

	    String after = null, display = null;
	    if (msgs.getAfterPageText() != null) {
	      after = Format.substitute(msgs.getAfterPageText(), "" + pages);
	    } else {
	      after = GXT.MESSAGES.pagingToolBar_afterPageText((int) pages);
	    }

	    afterText.setText(after);
	    first.setEnabled(activePage != 1);
	    prev.setEnabled(activePage != 1);
	    next.setEnabled(activePage != pages);
	    last.setEnabled(activePage != pages);
	    int temp = activePage == pages ? totalLength : start + pageSize;

	    if (msgs.getDisplayMsg() != null) {
	      String[] params = new String[] {"" + (start + 1), "" + temp, "" + totalLength};
	      display = Format.substitute(msgs.getDisplayMsg(), (Object[]) params);
	    } else {
	      display = GXT.MESSAGES.pagingToolBar_displayMsg(start + 1, (int) temp, (int) totalLength);
	    }

	    String msg = display;
	    if (totalLength == 0) {
	      msg = msgs.getEmptyMsg();
	    }
	    displayText.setText(msg);
	  }

	  protected void onPageChange() {
	    String value = pageText.getText();
	    if (value.equals("") || !Util.isInteger(value)) {
	      pageText.setText(String.valueOf((int) activePage));
	      return;
	    }
	    int p = Integer.parseInt(value);
	    setActivePage(p);
	  }

	
	@Override
	  protected void onRender(Element target, int index) {
	    MyMessages msg = GXT.MESSAGES;

	    msgs.setRefreshText(msgs.getRefreshText() == null ? msg.pagingToolBar_refreshText()
	        : msgs.getRefreshText());
	    msgs.setNextText(msgs.getNextText() == null ? msg.pagingToolBar_nextText() : msgs.getNextText());
	    msgs.setPrevText(msgs.getPrevText() == null ? msg.pagingToolBar_prevText() : msgs.getPrevText());
	    msgs.setFirstText(msgs.getFirstText() == null ? msg.pagingToolBar_firstText()
	        : msgs.getFirstText());
	    msgs.setLastText(msgs.getLastText() == null ? msg.pagingToolBar_lastText() : msgs.getLastText());
	    msgs.setBeforePageText(msgs.getBeforePageText() == null ? msg.pagingToolBar_beforePageText()
	        : msgs.getBeforePageText());
	    msgs.setEmptyMsg(msgs.getEmptyMsg() == null ? msg.pagingToolBar_emptyMsg() : msgs.getEmptyMsg());

	    toolBar = new ToolBar();

	    first = new TextToolItem();
	    first.setIconStyle("x-tbar-page-first");
	    if (showToolTips) first.setToolTip(msgs.getFirstText());
	    first.addSelectionListener(new SelectionListener<ComponentEvent>() {
	      public void componentSelected(ComponentEvent ce) {
	        first();
	      }
	    });

	    prev = new TextToolItem();
	    prev.setIconStyle("x-tbar-page-prev");
	    if (showToolTips) prev.setToolTip(msgs.getPrevText());
	    prev.addSelectionListener(new SelectionListener<ComponentEvent>() {
	      public void componentSelected(ComponentEvent ce) {
	        previous();
	      }
	    });

	    next = new TextToolItem();
	    next.setIconStyle("x-tbar-page-next");
	    if (showToolTips) next.setToolTip(msgs.getNextText());
	    next.addSelectionListener(new SelectionListener<ComponentEvent>() {
	      public void componentSelected(ComponentEvent ce) {
	        next();
	      }
	    });

	    last = new TextToolItem();
	    last.setIconStyle("x-tbar-page-last");
	    if (showToolTips) last.setToolTip(msgs.getLastText());
	    last.addSelectionListener(new SelectionListener<ComponentEvent>() {
	      public void componentSelected(ComponentEvent ce) {
	        last();
	      }
	    });

	    refresh = new TextToolItem();
	    refresh.setIconStyle("x-tbar-loading");
	    if (showToolTips) refresh.setToolTip(msgs.getRefreshText());
	    refresh.addSelectionListener(new SelectionListener<ComponentEvent>() {
	      public void componentSelected(ComponentEvent ce) {
	        refresh();
	      }
	    });

	    Label beforePage = new Label(msgs.getBeforePageText());
	    beforePage.setStyleName("my-paging-text");
	    afterText = new Label();
	    afterText.setStyleName("my-paging-text");
	    pageText = new TextBox();
	    if (!GXT.isGecko && !GXT.isSafari) {
	      pageText.addKeyboardListener(new KeyboardListenerAdapter() {
	        public void onKeyDown(Widget sender, char keyCode, int modifiers) {
	          if (keyCode == KeyboardListener.KEY_ENTER) {
	            onPageChange();
	          }
	        }
	      });
	    }
	    pageText.setWidth("30px");

	    pageText.addChangeListener(new ChangeListener() {
	      public void onChange(Widget sender) {
	        onPageChange();
	      }
	    });

	    displayText = new Label();
	    displayText.setStyleName("my-paging-display");

	    for (ToolItem item : items) {
		   toolBar.add(item);
		}
	    
	    toolBar.add(new FillToolItem());
	    
	    toolBar.add(new AdapterToolItem(displayText));
	    toolBar.add(new SeparatorToolItem());
	    toolBar.add(first);
	    toolBar.add(prev);
	    toolBar.add(new SeparatorToolItem());
	    toolBar.add(new AdapterToolItem(beforePage));
	    toolBar.add(new AdapterToolItem(pageText));
	    toolBar.add(new AdapterToolItem(afterText));
	    toolBar.add(new SeparatorToolItem());
	    toolBar.add(next);
	    toolBar.add(last);
	    toolBar.add(new SeparatorToolItem());
	    toolBar.add(refresh);

	    

	    toolBar.render(target, index);
	    setElement(toolBar.getElement());

	    if (renderEvent != null) {
	      onLoad(renderEvent);
	      renderEvent = null;
	    }
	}
}
