package org.sakaiproject.gradebook.gwt.client.gxt.controller;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.view.AppView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.ImportExportView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.InstructorView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.MultigradeView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.NewItemView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.NotificationView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.SingleGradeView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.StudentView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.TreeView;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

public class AppController extends Controller {
	
	private AppView appView;
	private NewItemView newItemView;
	private SingleGradeView singleGrade;
	private SingleGradeView singleView;
	private TreeView treeView;
	private NotificationView notificationView;
	private ImportExportView importExportView;
	private MultigradeView multigradeView;
	
	public AppController() {
		registerEventTypes(GradebookEvents.BrowseLearner);
		registerEventTypes(GradebookEvents.CloseNotification);
		registerEventTypes(GradebookEvents.Confirmation);
		registerEventTypes(GradebookEvents.ConfirmDeleteItem);
		registerEventTypes(GradebookEvents.ExpandEastPanel);
		registerEventTypes(GradebookEvents.FullScreen);
		registerEventTypes(GradebookEvents.HideColumn);
		registerEventTypes(GradebookEvents.ItemCreated);
		registerEventTypes(GradebookEvents.ItemDeleted);
		registerEventTypes(GradebookEvents.ItemUpdated);
		registerEventTypes(GradebookEvents.LearnerGradeRecordUpdated);
		registerEventTypes(GradebookEvents.LoadItemTreeModel);
		registerEventTypes(GradebookEvents.NewCategory);
		registerEventTypes(GradebookEvents.NewItem);
		registerEventTypes(GradebookEvents.Notification);
		registerEventTypes(GradebookEvents.RefreshCourseGrades);
		registerEventTypes(GradebookEvents.RevertItem);
		registerEventTypes(GradebookEvents.SelectDeleteItem);
		registerEventTypes(GradebookEvents.SelectLearner);
		registerEventTypes(GradebookEvents.SelectItem);
		registerEventTypes(GradebookEvents.ShowColumns);
		registerEventTypes(GradebookEvents.SingleGrade);
		registerEventTypes(GradebookEvents.SingleView);
		registerEventTypes(GradebookEvents.StartEditItem);
		registerEventTypes(GradebookEvents.StartExport);
		registerEventTypes(GradebookEvents.StartImport);
		registerEventTypes(GradebookEvents.Startup);
		registerEventTypes(GradebookEvents.HideEastPanel);
		registerEventTypes(GradebookEvents.SwitchGradebook);
		registerEventTypes(GradebookEvents.UpdateLearnerGradeRecord);
		registerEventTypes(GradebookEvents.UserChange);
	}
	
	@Override
	public void handleEvent(AppEvent<?> event) {
		// Note: the 'missing' break statements in this switch are intentional, they
		// allow certain events to drop through to multiple views
		switch (event.type) {
		case GradebookEvents.Confirmation:
		case GradebookEvents.CloseNotification:
		case GradebookEvents.Notification:
			onNotification(event);
			break;
		case GradebookEvents.FullScreen:
			onFullScreen(event);
			break;
		case GradebookEvents.HideColumn:
			onHideColumn(event);
			break;
		case GradebookEvents.BrowseLearner:
			onBrowseLearner(event);
			break;
		case GradebookEvents.ConfirmDeleteItem:
		case GradebookEvents.SelectDeleteItem:
			onConfirmDeleteItem(event);
			break;
		case GradebookEvents.LearnerGradeRecordUpdated:
			onLearnerGradeRecordUpdated(event);
			break;
		case GradebookEvents.NewCategory:
		case GradebookEvents.NewItem:
			onNewItem(event);
			break;
		case GradebookEvents.RefreshCourseGrades:
			onRefreshCourseGrades(event);
			break;
		case GradebookEvents.SelectLearner:
			onSelectLearner(event);
			break;
		case GradebookEvents.StartImport:
		case GradebookEvents.StartExport:
			onStartImport(event);
			break;
		case GradebookEvents.Startup:
			onStartup(event);
			break;
		case GradebookEvents.SingleGrade:
			onSingleGrade(event);
			break;
		case GradebookEvents.SingleView:
			onSingleView(event);
			break;
		case GradebookEvents.SwitchGradebook:
			forwardToView(appView, event);
			forwardToView(multigradeView, event);
			forwardToView(treeView, event);
			break;
		case GradebookEvents.UserChange:
			if (singleGrade != null)
				forwardToView(singleGrade, event);
			forwardToView(multigradeView, event);
			break;
		case GradebookEvents.LoadItemTreeModel:
			onLoadItemTreeModel(event);
			break;
		case GradebookEvents.ShowColumns:
			forwardToView(multigradeView, event);
			break;
		case GradebookEvents.StartEditItem:
		case GradebookEvents.HideEastPanel:
		case GradebookEvents.SelectItem:
			forwardToView(treeView, event);
		case GradebookEvents.ExpandEastPanel:
			forwardToView(appView, event);
			break;
		case GradebookEvents.ItemUpdated:
			onItemUpdated(event);
			break;
		case GradebookEvents.ItemCreated:
			onItemCreated(event);
			break;
		case GradebookEvents.ItemDeleted:
			onItemDeleted(event);
			break;
		}
	}

	@Override
	public void initialize() {
		
	}
	
	private void onBrowseLearner(AppEvent<?> event) {
		forwardToView(multigradeView, event);
	}
	
	private void onConfirmDeleteItem(AppEvent<?> event) {
		forwardToView(treeView, event);
	}
	
	private void onFullScreen(AppEvent<?> event) {
		forwardToView(appView, event);
	}
	
	private void onHideColumn(AppEvent<?> event) {
		forwardToView(treeView, event);
	}
	
	private void onItemCreated(AppEvent<?> event) {
		forwardToView(appView, event);
		forwardToView(multigradeView, event);
		forwardToView(treeView, event);
	}
	
	private void onItemDeleted(AppEvent<?> event) {
		forwardToView(multigradeView, event);
	}
	
	private void onItemUpdated(AppEvent<?> event) {
		forwardToView(multigradeView, event);
		forwardToView(treeView, event);
		if (singleView != null)
			forwardToView(singleView, event);
	}
	
	private void onLearnerGradeRecordUpdated(AppEvent<?> event) {
		forwardToView(multigradeView, event);
		if (singleView != null && singleView.isDialogVisible())
			forwardToView(singleView, event);
	}
	
	private void onLoadItemTreeModel(AppEvent<?> event) {
		forwardToView(multigradeView, event);
		forwardToView(treeView, event);
	}
		
	private void onNewItem(AppEvent<?> event) {
		forwardToView(appView, event);
		forwardToView(treeView, event);
		/*if (newItemView == null)
			newItemView = new NewItemView(this);
		
		forwardToView(newItemView, event);
		*/
	}
	
	private void onNotification(AppEvent<?> event) {
		forwardToView(appView, event);
		forwardToView(notificationView, event);
	}
	
	private void onRefreshCourseGrades(AppEvent<?> event) {
		forwardToView(multigradeView, event);
	}
	
	private void onSelectLearner(AppEvent<?> event) {
		forwardToView(appView, event);
	}
	
	private void onSingleGrade(AppEvent<?> event) {
		//if (singleGrade == null)
		//	singleGrade = new SingleGradeView(this, true);
		
		//forwardToView(singleGrade, event);
		
		forwardToView(appView, event);
		
		if (treeView != null)
			forwardToView(treeView, event);
		
		if (singleView != null && singleView.isDialogVisible())
			forwardToView(singleView, event);
	}
	
	private void onSingleView(AppEvent<?> event) {
		if (singleView == null)
			singleView = new SingleGradeView(this, false);
		
		forwardToView(singleView, event);
	}
	
	private void onStartImport(AppEvent<?> event) {
		if (importExportView == null)
			importExportView = new ImportExportView(this);
		
		forwardToView(importExportView, event);
	}
	
	private void onStartup(AppEvent<?> event) {
		ApplicationModel model = (ApplicationModel)event.data;
		
		List<GradebookModel> gradebookModels = model.getGradebookModels();
		I18nConstants i18n = Registry.get(AppConstants.I18N);
		
		// FIXME: Currently we only evaluate the first gradebook model to determine if we have
		// FIXME: an instructor or a student. This needs to be refined.
		for (GradebookModel gbModel : gradebookModels) {
			Registry.register(gbModel.getGradebookUid(), gbModel);
			Registry.register(AppConstants.CURRENT, gbModel);
			boolean isUserAbleToGrade = gbModel.isUserAbleToGrade() == null ? false : gbModel.isUserAbleToGrade().booleanValue();
			boolean isUserAbleToViewOwnGrades = gbModel.isUserAbleToViewOwnGrades() == null ? false : gbModel.isUserAbleToViewOwnGrades().booleanValue();
			boolean isUserAbleToEditItems = DataTypeConversionUtil.checkBoolean(gbModel.isUserAbleToEditAssessments());
			
			this.notificationView = new NotificationView(this);
			if (isUserAbleToGrade) {
				this.treeView = new TreeView(this, i18n, isUserAbleToEditItems);
				this.multigradeView = new MultigradeView(this, i18n);
				this.appView = new InstructorView(this, treeView, multigradeView, notificationView, isUserAbleToEditItems);
				forwardToView(treeView, event);
				forwardToView(multigradeView, event);
			} else if (isUserAbleToViewOwnGrades) {
				this.appView = new StudentView(this, notificationView);
			}
			
			forwardToView(appView, event);

			return;
		}
	}
	
}
