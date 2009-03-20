package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.gxt.event.BrowseLearner;
import org.sakaiproject.gradebook.gwt.client.gxt.event.FullScreen;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeRecordUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.event.ShowColumnsEvent;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class AppView extends View {

	public enum EastCard { DELETE_ITEM, EDIT_ITEM, GRADE_SCALE, HELP, HISTORY, LEARNER_SUMMARY, 
		NEW_CATEGORY, NEW_ITEM };
	
	private static final int screenHeight = 600;
	
	protected NotificationView notificationView;
	protected Viewport viewport;

	
	public AppView(Controller controller, NotificationView notificationView) {
		super(controller);
		this.notificationView = notificationView;
	}

	@Override
	protected void handleEvent(AppEvent<?> event) {
		switch(event.type) {
		case GradebookEvents.Confirmation:
		case GradebookEvents.Notification:
			onOpenNotification();
			break;
		case GradebookEvents.CloseNotification:
			onCloseNotification();
			break;
		case GradebookEvents.FullScreen:
			onFullScreen((FullScreen)event.data);
			break;
		case GradebookEvents.NewCategory:
			onNewCategory((ItemModel)event.data);
			break;
		case GradebookEvents.NewItem:
			onNewItem((ItemModel)event.data);
			break;
		case GradebookEvents.SelectLearner:
			onSelectLearner((StudentModel)event.data);
			break;
		case GradebookEvents.StartEditItem:
			onStartEditItem((ItemModel)event.data);
			break;
		case GradebookEvents.HideEastPanel:
			onHideEastPanel((Boolean)event.data);
			break;
		case GradebookEvents.ExpandEastPanel:
			onExpandEastPanel((EastCard)event.data);
			break;
		case GradebookEvents.ItemCreated:
			onItemCreated((ItemModel)event.data);
			break;
		case GradebookEvents.SingleGrade:
			onSingleGrade((StudentModel)event.data);
			break;
		case GradebookEvents.Startup:
			ApplicationModel applicationModel = (ApplicationModel)event.data;
			initUI(applicationModel);
			GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
			onSwitchGradebook(selectedGradebook);
			break;
		case GradebookEvents.SwitchGradebook:
			onSwitchGradebook((GradebookModel)event.data);
			break;
		case GradebookEvents.UpdateLearnerGradeRecord:
			onGradeStudent((GradeRecordUpdate)event.data);
			break;
		case GradebookEvents.UserChange:
			onUserChange((UserEntityAction<?>)event.data);
			break;
		}
	}
	
	@Override
	protected void initialize() {
		this.viewport = new Viewport() {
			protected void onRender(Element parent, int pos) {
			    super.onRender(parent, pos);
			    Accessibility.setRole(el().dom, "application");
			}
		};
		viewport.setHeight(screenHeight);
		viewport.setLayout(new FitLayout());
		RootPanel.get().add(viewport);
	}
	
	protected abstract void initUI(ApplicationModel model);

		
	protected void onCloseNotification() {
		
	}
	
	protected void onExpandEastPanel(EastCard activeCard) {
		
	}

	protected void onFullScreen(FullScreen fullscreen) {
		
	}
	
	protected void onGradeStudent(GradeRecordUpdate event) {
		
	}
	
	protected void onItemCreated(ItemModel itemModel) {
		
	}
		
	protected void onOpenNotification() {
		
	}
	
	protected void onNewCategory(ItemModel itemModel) {
		
	}
	
	protected void onNewItem(ItemModel itemModel) {
		
	}
	
	protected void onSelectLearner(StudentModel learner) {
		
	}
	
	protected void onSingleGrade(StudentModel student) {
		
	}
	
	protected void onStartEditItem(ItemModel itemModel) {
		
	}
	
	protected void onHideEastPanel(Boolean doCommit) {
		
	}
	
	protected void onSwitchGradebook(GradebookModel selectedGradebook) {
		
	}
	
	protected void onUserChange(UserEntityAction<?> action) {
		
	}
	
}
