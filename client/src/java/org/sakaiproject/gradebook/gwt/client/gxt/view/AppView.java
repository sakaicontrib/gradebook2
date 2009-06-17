package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityAction;
import org.sakaiproject.gradebook.gwt.client.action.UserEntityUpdateAction;
import org.sakaiproject.gradebook.gwt.client.gxt.event.FullScreen;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradeRecordUpdate;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class AppView extends View {

	public enum EastCard { DELETE_CATEGORY, DELETE_ITEM, EDIT_CATEGORY, EDIT_GRADEBOOK, EDIT_ITEM, 
		GRADE_SCALE, HELP, HISTORY, LEARNER_SUMMARY, NEW_CATEGORY, NEW_ITEM, GRADER_PERMISSION_SETTINGS };
	
	private static final int screenHeight = 600;
	
	protected NotificationView notificationView;
	protected Viewport realViewport;
	protected CardLayout viewportLayout;
	
	protected LayoutContainer viewport;

	
	public AppView(Controller controller, NotificationView notificationView) {
		super(controller);
		this.notificationView = notificationView;
		this.viewportLayout = new CardLayout();
		this.realViewport = new Viewport() {
			protected void onRender(Element parent, int pos) {
			    super.onRender(parent, pos);
			    Accessibility.setRole(el().dom, "application");
			}
		};
		realViewport.setLayout(new FillLayout());
		realViewport.setLoadingPanelId("loading");
		
		viewport = new LayoutContainer();
		realViewport.add(viewport);
		
		viewport.setPosition(0, 0);
		viewport.setHeight(screenHeight);
		viewport.setLayout(viewportLayout);
		realViewport.setHeight(screenHeight);
		realViewport.setDelay(400);
	}

	@Override
	protected void handleEvent(AppEvent<?> event) {
		switch(GradebookEvents.getEvent(event.type).getEventKey()) {
		case CONFIRMATION:
		case NOTIFICATION:
			onOpenNotification();
			break;
		case CLOSE_NOTIFICATION:
			onCloseNotification();
			break;
		case LOAD_ITEM_TREE_MODEL:
			onLoadItemTreeModel((GradebookModel)event.data);
			break;
		case LEARNER_GRADE_RECORD_UPDATED:
			onLearnerGradeRecordUpdated((UserEntityUpdateAction)event.data);
			break;
		case NEW_CATEGORY:
			onNewCategory((ItemModel)event.data);
			break;
		case NEW_ITEM:
			onNewItem((ItemModel)event.data);
			break;
		case SELECT_LEARNER:
			onSelectLearner((StudentModel)event.data);
			break;
		case SHOW_GRADE_SCALE:
			onShowGradeScale((Boolean)event.data);
			break;
		case SHOW_GRADER_PERMISSION_SETTINGS:
			onShowGraderPermissionSettings((Boolean)event.data);
			break;
		case SHOW_HISTORY:
			onShowHistory((String)event.data);
			break;
		case SINGLE_VIEW:
			onSingleView((StudentModel)event.data);
			break;
		case START_IMPORT:
			onStartImport();
			break;
		case START_EDIT_ITEM:
			onStartEditItem((ItemModel)event.data);
			break;
		case STOP_IMPORT:
			onStopImport();
			break;
		case HIDE_EAST_PANEL:
			onHideEastPanel((Boolean)event.data);
			break;
		case HIDE_FORM_PANEL:
			onHideFormPanel();
			break;
		case EXPAND_EAST_PANEL:
			onExpandEastPanel((EastCard)event.data);
			break;
		case ITEM_CREATED:
			onItemCreated((ItemModel)event.data);
			break;
		case SINGLE_GRADE:
			onSingleGrade((StudentModel)event.data);
			break;
		case STARTUP:
			RootPanel.get().add(realViewport);
			ApplicationModel applicationModel = (ApplicationModel)event.data;
			initUI(applicationModel);
			GradebookModel selectedGradebook = Registry.get(AppConstants.CURRENT);
			onSwitchGradebook(selectedGradebook);
			break;
		case SWITCH_GRADEBOOK:
			onSwitchGradebook((GradebookModel)event.data);
			break;
		case UPDATE_LEARNER_GRADE_RECORD:
			onGradeStudent((GradeRecordUpdate)event.data);
			break;
		case USER_CHANGE:
			onUserChange((UserEntityAction<?>)event.data);
			break;
		}
	}
	
	@Override
	protected void initialize() {
		
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
	
	protected void onHideEastPanel(Boolean doCommit) {
		
	}
	
	protected void onHideFormPanel() {
		
	}
	
	protected void onItemCreated(ItemModel itemModel) {
		
	}
	
	protected void onLearnerGradeRecordUpdated(UserEntityUpdateAction action) {
		
	}
	
	protected void onLoadItemTreeModel(GradebookModel selectedGradebook) {
		
	}
		
	protected void onOpenNotification() {
		
	}
	
	protected void onNewCategory(ItemModel itemModel) {
		
	}
	
	protected void onNewItem(ItemModel itemModel) {
		
	}
	
	protected void onSelectLearner(StudentModel learner) {
		
	}
	
	protected void onSingleView(StudentModel learner) {
		
	}
	
	protected void onShowGradeScale(Boolean show) {
		
	}
	
	protected void onShowHistory(String identifier) {
		
	}
	
	protected void onSingleGrade(StudentModel student) {
		
	}
	
	protected void onStartEditItem(ItemModel itemModel) {
		
	}
	
	protected void onStartImport() {
		
	}
	
	protected void onStopImport() {
		
	}
		
	protected void onSwitchGradebook(GradebookModel selectedGradebook) {
		
	}
	
	protected void onUserChange(UserEntityAction<?> action) {
		
	}
	
	protected void onShowGraderPermissionSettings(Boolean show) {
		
	}
}
