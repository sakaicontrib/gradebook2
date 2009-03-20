package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.gxt.StudentViewContainer;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class StudentView extends AppView {

	private StudentViewContainer studentViewContainer;
	
	public StudentView(Controller controller, NotificationView notificationView) {
		super(controller, notificationView);
	}
	
	@Override
	protected void initUI(ApplicationModel model) {
		GradebookModel gbModel = model.getGradebookModels().get(0);
		studentViewContainer = new StudentViewContainer(true);
		studentViewContainer.onChangeModel(gbModel, gbModel.getUserAsStudent());
		//Dispatcher.forwardEvent(GradebookEvents.SingleView, gbModel.getUserAsStudent());
		viewport.setLayout(new FitLayout());
		viewport.add(studentViewContainer);
		//viewport.setHeight(600);
	}
	
}
