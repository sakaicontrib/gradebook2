package org.sakaiproject.gradebook.gwt.client.wizard;


import org.sakaiproject.gradebook.gwt.client.api.WizardService;

import com.google.inject.Provider;

public class WizardServiceProvider implements Provider<WizardService>{

	public WizardService get() {
		return new SimpleWizardService();
	}

}
