package org.sakaiproject.gradebook.gwt.client.gin;



import org.sakaiproject.gradebook.gwt.client.api.Card;
import org.sakaiproject.gradebook.gwt.client.api.Wizard;
import org.sakaiproject.gradebook.gwt.client.wizard.WizardProvider;
import org.sakaiproject.gradebook.gwt.client.wizard.CardProvider;

import com.google.gwt.inject.client.AbstractGinModule;

public class WidgetBindModule extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(Wizard.class).toProvider(WizardProvider.class);
		bind(Card.class).toProvider(CardProvider.class);
	}

}