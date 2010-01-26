package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.PermissionsPanel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;

public class PermissionsView extends View {

	private PermissionsPanel permissionsPanel;
	
	public PermissionsView(Controller controller) {
		super(controller);
	}
	
	public PermissionsPanel getPermissionsPanelInstance() {
		if (permissionsPanel == null) {
			permissionsPanel = new PermissionsPanel((I18nConstants)Registry.get(AppConstants.I18N));
		}
		return permissionsPanel;
	}

	@Override
	protected void handleEvent(AppEvent event) {
		switch(GradebookEvents.getEvent(event.getType()).getEventKey()) {
		case PERMISSION_CREATED:
			onPermissionCreated((ModelData)event.getData());
			break;
		case PERMISSION_DELETED:
			onPermissionDeleted((ModelData)event.getData());
			break;
		};
	}
	
	private void onPermissionCreated(ModelData model) {
		if (permissionsPanel != null)
			permissionsPanel.onPermissionCreated(model);
	}
	
	private void onPermissionDeleted(ModelData model) {
		if (permissionsPanel != null)
			permissionsPanel.onPermissionDeleted(model);
	}
	
}
