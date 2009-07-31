package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.gxt.view.panel.StudentPanel;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class StudentView extends AppView {

	private StudentPanel studentViewContainer;
	
	public StudentView(Controller controller, NotificationView notificationView) {
		super(controller, notificationView);
	}
	
	@Override
	protected void initUI(ApplicationModel model) {
		GradebookModel gbModel = model.getGradebookModels().get(0);
		studentViewContainer = new StudentPanel(true);
		studentViewContainer.onChangeModel(gbModel, gbModel.getUserAsStudent());
		//Dispatcher.forwardEvent(GradebookEvents.SingleView, gbModel.getUserAsStudent());
		viewport.setLayout(new FitLayout());
		viewport.add(studentViewContainer);
		viewportLayout.setActiveItem(studentViewContainer);
		//viewport.setHeight(600);
	}
	
}
