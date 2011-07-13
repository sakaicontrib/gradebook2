package org.sakaiproject.gradebook.gwt.client.wizard;

import javax.inject.Provider;

import org.sakaiproject.gradebook.gwt.client.api.Card;
import org.sakaiproject.gradebook.gwt.client.gin.SimpleCard;


public class CardProvider implements Provider<Card> {

	
	public CardProvider() {}
	
	public Card get() {
		return new SimpleCard();
		
	}

}
