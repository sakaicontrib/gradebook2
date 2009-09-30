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

package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.PersistentStore;
import org.sakaiproject.gradebook.gwt.client.gxt.a11y.AriaCheckMenuItem;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;

public class PreferencesMenu extends Menu {

	private CheckMenuItem enablePopupsMenuItem;

	private Listener<MenuEvent> menuListener;
	public PreferencesMenu(I18nConstants i18n, TreeView treeView) {
		initListeners(i18n);
		
		// Enable popups
		enablePopupsMenuItem = new AriaCheckMenuItem(i18n.prefMenuEnablePopups());
		enablePopupsMenuItem.setId(AppConstants.ID_ENABLE_POPUPS_MENUITEM);
		add(enablePopupsMenuItem);

		enablePopupsMenuItem.addListener(Events.CheckChange, menuListener);
	}

	
	protected void initListeners(final I18nConstants i18n) {
		
	}
	

	public void onSwitchGradebook(GradebookModel selectedGradebook) {
		// Initialize enable popups checkbox
		String storedEnableNotifications = PersistentStore.getPersistentField(selectedGradebook.getGradebookUid(), "enableNotifications", "checked");
		if (storedEnableNotifications != null) {
			Boolean isChecked = Boolean.valueOf(storedEnableNotifications);
			if (isChecked != null) {
				Registry.register(AppConstants.ENABLE_POPUPS, isChecked);
				enablePopupsMenuItem.setChecked(isChecked.booleanValue());
			}
		}
	}
}
