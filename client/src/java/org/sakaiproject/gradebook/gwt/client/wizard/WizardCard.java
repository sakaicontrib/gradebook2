/*
 * Copyright 2008 Grant Slender <gslender@iinet.com.au>
 * 
 * Ideas/concepts borrowed from Thorsten Suckow-Homberg <ts@siteartwork.de>
 * http://www.siteartwork.de/wizardcomponent
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

/*
 * Significant modifications made by @author jpgorrono@ucdavis.edu
 * including:
 * 
 *  - modifications to work with 2.x
 *   -- such as, use of buttonbar and status objects
 *  - added cancel listeners
 *  - added method to add cards directly on wizard
 * 
 */

package org.sakaiproject.gradebook.gwt.client.wizard;

import java.util.ArrayList;

import org.sakaiproject.gradebook.gwt.client.api.Card;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Element;

/**
 * Part of wizard window, WizardCard is used to show & validate a step in the
 * wizard process.</br></br>
 * 
 * WizardCard wc = new WizardCard("Welcome");</br> wc.setHtmlText("Welcome to
 * the example for <strong>ext.ux.WizardWindow</strong>, "</br> + "a ExtGWT
 * user extension for creating wizards.<br/><br/>"</br> + "Please click the
 * \"next\"-button and fill out all form values.");</br> cards.add(wc);</br>
 * 
 */
public class WizardCard extends LayoutContainer implements Card {

	private ArrayList<Listener<BaseEvent>> finishListeners;
	private String cardTitle;
	private FormPanel panel;
	private LayoutContainer layoutContainer;
	
	private ArrayList<Listener<BaseEvent>> cardCloseListeners;

	/**
	 * Creates a new wizard card.
	 * 
	 * @param cardtitle
	 *            title string of this card
	 */
	public WizardCard(String cardtitle) {
		super();
		this.cardTitle = cardtitle;
		setLayout(new RowLayout(Orientation.VERTICAL));
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
	}

	public void notifyFinishListeners() {
		notifyListeners(finishListeners);
	}

	/**
	 * Returns the currently set title.
	 * 
	 * @returns the current title of this card
	 */
	public String getCardTitle() {
		return cardTitle;
	}

	public void setCardTitle(String cardTitle) {
		this.cardTitle = cardTitle;
	}

	/**
	 * Sets the HTML text associated with this card.
	 * 
	 * @param htmltext
	 *            HTML string to set
	 */
	public void setHtmlText(String htmltext) {
		add(new HtmlContainer(htmltext));
	}

	/**
	 * Sets the FormPanel associated with this card.</br></br> Note: this
	 * panel will set height = 300, and the following to false;
	 * frame,borders,bodyborder,headervisible
	 * 
	 * @param panel
	 *            FormPanel to set
	 */
	public void setFormPanel(FormPanel panel) {
		this.panel = panel;
		panel.setHeight(300);
		panel.setFrame(false);
		panel.setBorders(false);
		panel.setBodyBorder(false);
		panel.setHeaderVisible(false);
		add(panel);
	}

	/**
	 * Calls the isValid of the form (if set) and returns the result.
	 * 
	 * @returns the result of the form isValid(), or true if no form set
	 */
	public boolean isValid() {
		if (panel == null) return true;
		return panel.isValid();
	}

	/**
	 * Adds a Listener<BaseEvent> to the list of listeners that will be
	 * notified when the card is finished.</br> Note: this event is only called
	 * on the last card and when the finish button is clicked.
	 * 
	 * @param listener
	 *            the Listener<BaseEvent> to be added
	 */
	public void addFinishListener(Listener<BaseEvent> listener) {
		if (finishListeners == null) finishListeners = new ArrayList<Listener<BaseEvent>>();
		finishListeners.add(listener);
	}

	public FormPanel getFormPanel() {
		return panel;
	}

	
	
	public void setLayoutContainer(LayoutContainer container) {
		this.layoutContainer = container;
		add(container);
	}
	
	public LayoutContainer getLayoutContainer() {
		return this.layoutContainer;
	}

	@Override
	public void addCardCloseListener(Listener<BaseEvent> listener) {
		if (cardCloseListeners == null) cardCloseListeners = new ArrayList<Listener<BaseEvent>>();
		cardCloseListeners.add(listener);
		
	}

	@Override
	public void notifyCardCloseListeners() {
		notifyListeners(cardCloseListeners);
	}
	
	private void notifyListeners(ArrayList<Listener<BaseEvent>> listeners) {
		if (listeners != null) {
			for (Listener<BaseEvent> listener : listeners) {
				listener.handleEvent(new BaseEvent(this));
			}
		}
	}
	
}