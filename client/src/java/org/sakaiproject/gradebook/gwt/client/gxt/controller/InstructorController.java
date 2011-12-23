package org.sakaiproject.gradebook.gwt.client.gxt.controller;

import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.I18nConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.view.FinalGradeSubmissionView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.ImportExportView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.InstructorView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.MultigradeView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.PermissionsView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.SingleGradeView;
import org.sakaiproject.gradebook.gwt.client.gxt.view.TreeView;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationSetup;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

public class InstructorController extends Controller {

	private SingleGradeView singleView;
	private TreeView treeView;
	private ImportExportView importExportView;
	private MultigradeView multigradeView;
	private FinalGradeSubmissionView finalGradeSubmissionView;
	private InstructorView appView;
	private PermissionsView permissionsView;
	
	public InstructorController(I18nConstants i18n, boolean isUserAbleToEditItems, boolean isNewGradebook) {
		super();
		singleView = new SingleGradeView(this, false);
		treeView = new TreeView(this, isUserAbleToEditItems);
		multigradeView = new MultigradeView(this, i18n);
		permissionsView = new PermissionsView(this);
		appView = new InstructorView(this, treeView, multigradeView, singleView, permissionsView, isUserAbleToEditItems, isNewGradebook);
		
		registerEventTypes(GradebookEvents.BeginItemUpdates.getEventType());
		registerEventTypes(GradebookEvents.BrowseLearner.getEventType());
		registerEventTypes(GradebookEvents.ConfirmDeleteItem.getEventType());
		registerEventTypes(GradebookEvents.EndItemUpdates.getEventType());
		registerEventTypes(GradebookEvents.ExpandEastPanel.getEventType());
		registerEventTypes(GradebookEvents.FailedToUpdateItem.getEventType());
		registerEventTypes(GradebookEvents.GradeTypeUpdated.getEventType());
		registerEventTypes(GradebookEvents.HideColumn.getEventType());
		registerEventTypes(GradebookEvents.HideFormPanel.getEventType());
		registerEventTypes(GradebookEvents.HideEastPanel.getEventType());
		registerEventTypes(GradebookEvents.ItemCreated.getEventType());
		registerEventTypes(GradebookEvents.ItemDeleted.getEventType());
		registerEventTypes(GradebookEvents.ItemUpdated.getEventType());
		registerEventTypes(GradebookEvents.LearnerGradeRecordUpdated.getEventType());
		registerEventTypes(GradebookEvents.MaskItemTree.getEventType());
		registerEventTypes(GradebookEvents.NewCategory.getEventType());
		registerEventTypes(GradebookEvents.NewItem.getEventType());
		registerEventTypes(GradebookEvents.PermissionCreated.getEventType());
		registerEventTypes(GradebookEvents.PermissionDeleted.getEventType());
		registerEventTypes(GradebookEvents.RefreshCourseGrades.getEventType());
		registerEventTypes(GradebookEvents.RefreshGradebookItems.getEventType());
		registerEventTypes(GradebookEvents.RefreshGradebookSetup.getEventType());
		registerEventTypes(GradebookEvents.RefreshGradeScale.getEventType());
		registerEventTypes(GradebookEvents.RevertItem.getEventType());
		registerEventTypes(GradebookEvents.SelectDeleteItem.getEventType());
		registerEventTypes(GradebookEvents.SelectLearner.getEventType());
		registerEventTypes(GradebookEvents.SelectItem.getEventType());
		registerEventTypes(GradebookEvents.ShowColumns.getEventType());
		registerEventTypes(GradebookEvents.ShowGradeScale.getEventType());
		registerEventTypes(GradebookEvents.ShowHistory.getEventType());
		registerEventTypes(GradebookEvents.ShowStatistics.getEventType());
		registerEventTypes(GradebookEvents.StopStatistics.getEventType());
		registerEventTypes(GradebookEvents.SingleGrade.getEventType());
		registerEventTypes(GradebookEvents.SingleView.getEventType());
		registerEventTypes(GradebookEvents.StartEditItem.getEventType());
		registerEventTypes(GradebookEvents.StartExport.getEventType());
		registerEventTypes(GradebookEvents.StartImport.getEventType());
		registerEventTypes(GradebookEvents.Startup.getEventType());
		registerEventTypes(GradebookEvents.StopImport.getEventType());
		registerEventTypes(GradebookEvents.SwitchEditItem.getEventType());
		registerEventTypes(GradebookEvents.SwitchGradebook.getEventType());
		registerEventTypes(GradebookEvents.UnmaskItemTree.getEventType());
		registerEventTypes(GradebookEvents.UpdateLearnerGradeRecord.getEventType());
		registerEventTypes(GradebookEvents.UserChange.getEventType());
		registerEventTypes(GradebookEvents.StartFinalgrade.getEventType());
		registerEventTypes(GradebookEvents.StopGraderPermissionSettings.getEventType());
		registerEventTypes(GradebookEvents.FinishTreeItemDragAndDrop.getEventType());
		registerEventTypes(GradebookEvents.ShowUserFeedback.getEventType());
		registerEventTypes(GradebookEvents.HideUserFeedback.getEventType());
		registerEventTypes(GradebookEvents.GradeScaleUpdateError.getEventType());
		registerEventTypes(GradebookEvents.MaskMultiGradeGrid.getEventType());
		registerEventTypes(GradebookEvents.UnmaskMultiGradeGrid.getEventType());
		registerEventTypes(GradebookEvents.ShowFinalGradeSubmissionStatus.getEventType());
		registerEventTypes(GradebookEvents.LayoutItemTreePanel.getEventType());
	}
	
	@Override
	public void handleEvent(AppEvent event) {
		// Note: the 'missing' break statements in this switch are intentional, they
		// allow certain events to drop through to multiple views
		switch (GradebookEvents.getEvent(event.getType()).getEventKey()) {
			case SHOW_USER_FEEDBACK:
				forwardToView(appView, event);
				break;
			case HIDE_USER_FEEDBACK:
				forwardToView(appView, event);
				break;
			case BEGIN_ITEM_UPDATES:
			case END_ITEM_UPDATES:
				forwardToView(multigradeView, event);
				break;
			case HIDE_COLUMN:
				forwardToView(treeView, event);
				break;
			case BROWSE_LEARNER:
				forwardToView(multigradeView, event);
				break;
			case CONFIRM_DELETE_ITEM:
			case SELECT_DELETE_ITEM:
				// GRBK-943 - Give the treeview's form panel the multi grid
				if (treeView.getFormPanel().getMultiGradePanel() == null)
				{
					treeView.getFormPanel().setMultiGradePanel(this.multigradeView.getMultiGradeContentPanel());
				}
				forwardToView(treeView, event);
				break;
			case LEARNER_GRADE_RECORD_UPDATED:
				forwardToView(multigradeView, event);
				forwardToView(appView, event);
				if (singleView != null)
					forwardToView(singleView, event);
				break;
			case NEW_CATEGORY:
			case NEW_ITEM:
				// GRBK-943 - Give the treeview's form panel the multi grid
				if (treeView.getFormPanel().getMultiGradePanel() == null)
				{
					treeView.getFormPanel().setMultiGradePanel(this.multigradeView.getMultiGradeContentPanel());
				}
				forwardToView(appView, event);
				forwardToView(treeView, event);
				break;
			case REFRESH_GRADEBOOK_ITEMS:
			case REFRESH_GRADEBOOK_SETUP:
				forwardToView(multigradeView, event);
				forwardToView(treeView, event);
				forwardToView(appView, event);
				if (singleView != null && singleView.isDialogVisible())
					forwardToView(singleView, event);
				break;
			case REFRESH_GRADE_SCALE:
				forwardToView(multigradeView, event);
				forwardToView(appView, event);
				break;
			case GRADE_SCALE_UPDATE_ERROR:
				forwardToView(appView, event);
				break;
			case REFRESH_COURSE_GRADES:
				forwardToView(multigradeView, event);
				break;
			case SELECT_LEARNER:
				forwardToView(appView, event);
				if (singleView != null && singleView.isDialogVisible())
					forwardToView(singleView, event);
				break;
			case START_IMPORT:
				onStartImport(event);
				break;
			case START_EXPORT:
				onStartExport(event);
				break;
			case STOP_IMPORT:
				onStopImport(event);
				break;
			case SINGLE_GRADE:
				forwardToView(appView, event);

				if (treeView != null)
					forwardToView(treeView, event);

				if (singleView != null && singleView.isDialogVisible())
					forwardToView(singleView, event);
				break;
			case GRADE_TYPE_UPDATED:
				forwardToView(appView, event);
				break;
			case SINGLE_VIEW:
				if (singleView == null)
					singleView = new SingleGradeView(this, false);

				forwardToView(singleView, event);
				forwardToView(appView, event);
				break;
			case SWITCH_GRADEBOOK:
				forwardToView(appView, event);
				forwardToView(multigradeView, event);
				forwardToView(treeView, event);
				break;
			case USER_CHANGE:
				forwardToView(multigradeView, event);
				break;
			case LOAD_ITEM_TREE_MODEL:
				forwardToView(multigradeView, event);
				forwardToView(treeView, event);
				forwardToView(appView, event);
				break;
			case SHOW_COLUMNS:
				forwardToView(multigradeView, event);
				break;
			case SHOW_GRADE_SCALE:
			case SHOW_HISTORY:	
			case SHOW_STATISTICS:
			case STOP_STATISTICS:
				forwardToView(appView, event);
				break;
			case HIDE_FORM_PANEL:
				forwardToView(appView, event);
				break;
			case START_EDIT_ITEM:
			case HIDE_EAST_PANEL:
			case SELECT_ITEM:
			case SWITCH_EDIT_ITEM:
				// GRBK-943 - Give the treeview's form panel the multi grid
				if (treeView.getFormPanel().getMultiGradePanel() == null)
				{
					treeView.getFormPanel().setMultiGradePanel(this.multigradeView.getMultiGradeContentPanel());
				}
				forwardToView(treeView, event);
				forwardToView(appView, event);
				break;
			case EXPAND_EAST_PANEL:
				forwardToView(appView, event);
				break;
			case ITEM_UPDATED:
				forwardToView(multigradeView, event);
				forwardToView(treeView, event);
				if (singleView != null)
					forwardToView(singleView, event);
				break;
			case ITEM_CREATED:
				forwardToView(appView, event);
				forwardToView(multigradeView, event);
				forwardToView(treeView, event);
				break;
			case ITEM_DELETED:
				forwardToView(multigradeView, event);
				forwardToView(treeView, event);
				break;
			case FAILED_TO_UPDATE_ITEM:
				forwardToView(appView, event);
				break;
			case MASK_ITEM_TREE:
			case UNMASK_ITEM_TREE:
				forwardToView(treeView, event);
				break;
			case START_FINAL_GRADE:
				I18nConstants i18n = Registry.get(AppConstants.I18N);
				finalGradeSubmissionView = new FinalGradeSubmissionView(this, i18n);
				forwardToView(finalGradeSubmissionView, event);
				break;
			case START_GRADER_PERMISSION_SETTINGS:
			case STOP_GRADER_PERMISSION_SETTINGS:
				forwardToView(appView, event);
				break;
			case STARTUP:
				onStartup(event);
				break;
			case PERMISSION_CREATED:
			case PERMISSION_DELETED:
				forwardToView(permissionsView, event);
				break;
			case FINISH_TREE_ITEM_DRAG_AND_DROP:
				// GRBK-833
				forwardToView(treeView, event);
				break;
			case MASK_MULTI_GRADE_GRID:
				forwardToView(multigradeView, event);
				break;
			case UNMASK_MULTI_GRADE_GRID:
				forwardToView(multigradeView, event);
				break;
			case SHOW_FINAL_GRADE_SUBMISSION_STATUS:
				forwardToView(appView, event);
				break;
			case LAYOUT_ITEM_TREE_PANEL:
				forwardToView(treeView, event);
				break;
		}
	}
	
	private void onStartExport(AppEvent event) {
		if (importExportView == null) {
			importExportView = new ImportExportView(InstructorController.this);
		}
		forwardToView(importExportView, event);
		forwardToView(appView, event);
	}
	
	private void onStartImport(final AppEvent event) {
		if (importExportView == null) {
			importExportView = new ImportExportView(InstructorController.this);
		}
		appView.getViewport().add(importExportView.getImportDialog());
		appView.getViewportLayout().setActiveItem(importExportView.getImportDialog());
		forwardToView(importExportView, event);
		forwardToView(appView, event);
	}
	
	private void onStopImport(AppEvent event) {
		appView.getViewport().remove(importExportView.getImportDialog());
		forwardToView(importExportView, event);
		forwardToView(appView, event);
	}
	
	private void onStartup(AppEvent event) {
		ApplicationSetup model = (ApplicationSetup)event.getData();

		List<Gradebook> gradebookModels = model.getGradebookModels();

		Registry.register(AppConstants.HELP_URL, model.getHelpUrl());
		Registry.register(AppConstants.ENABLED_SEARCH_ROSTER_BY_FIELD, Boolean.valueOf(model.isSearchRosterByFieldEnabled()));
		Registry.register(AppConstants.ENABLED_GRADE_TYPES, model.getEnabledGradeTypes());
		Registry.register(AppConstants.SHOW_WEIGHTED_ENABLED, Boolean.toString(model.isShowWeightedEnabled()));

		// FIXME: Currently we only evaluate the first gradebook model to determine if we have
		// FIXME: an instructor or a student. This needs to be refined.
		for (Gradebook gbModel : gradebookModels) {
			Registry.register(gbModel.getGradebookUid(), gbModel);
			Registry.register(AppConstants.CURRENT, gbModel);
			Boolean isUserAbleToGradeBoolean = Registry.get(AppConstants.IS_ABLE_TO_GRADE);
			boolean isUserAbleToGrade = isUserAbleToGradeBoolean == null ? false : isUserAbleToGradeBoolean.booleanValue();

			if (isUserAbleToGrade) {
				forwardToView(treeView, event);
				forwardToView(multigradeView, event);
			} 

			if (appView != null)
				forwardToView(appView, event);

			return;
		}
	}
}
