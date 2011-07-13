/**
 * 
 */
package org.sakaiproject.gradebook.gwt.client.gin;

import org.sakaiproject.gradebook.gwt.client.wizard.CardProvider;
import org.sakaiproject.gradebook.gwt.client.wizard.WizardProvider;
import org.sakaiproject.gradebook.gwt.client.wizard.WizardServiceProvider;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * @author jpgorrono@ucdavis.edu
 *
 */
@GinModules(WidgetBindModule.class)
public interface WidgetInjector extends Ginjector {
	WizardProvider getWizardProvider();
	WizardServiceProvider getWizardServiceProvider();
	CardProvider getWizardCardProvider();
	
}
