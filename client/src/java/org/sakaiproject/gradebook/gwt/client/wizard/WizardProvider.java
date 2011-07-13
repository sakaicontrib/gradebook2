package org.sakaiproject.gradebook.gwt.client.wizard;


import javax.inject.Provider;

import org.sakaiproject.gradebook.gwt.client.api.Wizard;


public class WizardProvider implements Provider<Wizard>{
	
	WizardProvider(){}

	public Wizard get() {
		return new SimpleWizard();
	}

}
