package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.gxt.StudentViewContainer;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;

public class StudentView extends AppView {

	private StudentViewContainer studentViewContainer;
	
	public StudentView(Controller controller, NotificationView notificationView) {
		super(controller, notificationView);
	}
	
	@Override
	protected void initUI(ApplicationModel model) {
		GradebookModel gbModel = model.getGradebookModels().get(0);
		studentViewContainer = new StudentViewContainer(true);
		Dispatcher.forwardEvent(GradebookEvents.SingleView, gbModel.getUserAsStudent());
		viewport.add(studentViewContainer);
	}
	
}
