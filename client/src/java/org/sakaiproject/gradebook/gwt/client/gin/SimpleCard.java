package org.sakaiproject.gradebook.gwt.client.gin;

import org.sakaiproject.gradebook.gwt.client.api.Card;
import org.sakaiproject.gradebook.gwt.client.wizard.WizardCard;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.user.client.Element;

public class SimpleCard extends WizardCard implements Card {

	public SimpleCard(String cardtitle) {
		super(cardtitle);
	}
	
	public SimpleCard() {
		super("default title");
	}

	public void setHtmlText(String html) {
		super.setHtmlText(html);
	}
	
	public void setTitle(String title) {
		super.setCardTitle(title);
	}
	
	public void setFormPanel(FormPanel formpanel) {
		super.setFormPanel(formpanel);
	}
	
	public void addFinishListener(Listener<BaseEvent> listener) {
		super.addFinishListener(listener);
	}
	
	public void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
	}

	public void notifyFinishListeners() {
		super.notifyFinishListeners();
	}

	public String getCardTitle() {
		return super.getCardTitle();
	}

	public boolean isValid() {
		return super.isValid();
	}
	
	public void addCardCloseListener(Listener<BaseEvent> listener) {
		super.addCardCloseListener(listener);
	}
	public void notifyCardCloseListeners() {
		super.notifyCardCloseListeners();
	}
	
}
