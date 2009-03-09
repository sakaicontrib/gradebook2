package org.sakaiproject.gradebook.gwt.client.gxt.view;

import org.sakaiproject.gradebook.gwt.client.gxt.event.GradebookEvents;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.AddAssignmentDialog;
import org.sakaiproject.gradebook.gwt.client.gxt.settings.AddCategoryDialog;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;

import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;

public class NewItemView extends View {

	private AddAssignmentDialog addAssignmentDialog;
	private AddCategoryDialog addCategoryDialog;
	
	public NewItemView(Controller controller) {
		super(controller);
	}
	
	@Override
	protected void handleEvent(AppEvent<?> event) {
		ItemModel itemModel = null;
		if (event != null) {
			itemModel = (ItemModel)event.data;
		}
		switch (event.type) {
		case GradebookEvents.NewCategory:
			onNewCategory(itemModel);
			break;
		case GradebookEvents.NewItem:
			onNewItem(itemModel);
			break;
		}
	}
	
	private void onNewCategory(ItemModel itemModel) {
		if (addCategoryDialog == null)
			addCategoryDialog = new AddCategoryDialog();
		
		//addCategoryDialog.setItemModel(itemModel);
		addCategoryDialog.show();
	}
	
	private void onNewItem(ItemModel itemModel) {
		if (addAssignmentDialog == null)
			addAssignmentDialog = new AddAssignmentDialog();
		
		addAssignmentDialog.setItemModel(itemModel);
		addAssignmentDialog.show();
	}

}
