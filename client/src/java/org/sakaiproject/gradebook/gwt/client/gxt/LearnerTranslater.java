package org.sakaiproject.gradebook.gwt.client.gxt;

import java.util.EnumSet;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;

import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.ModelType;

public class LearnerTranslater extends JsonTranslater {

	public LearnerTranslater(Item gbItem, boolean isImportLearner) {
		super(generateLearnerModelType(gbItem, isImportLearner));
	}

	public static ModelType generateLearnerModelType(Item gbItem, final boolean isImportLearner) {
		final ModelType t = new ModelType();  
		t.setRoot(AppConstants.LIST_ROOT);
		t.setTotalName(AppConstants.TOTAL);
		
		for (LearnerKey key : EnumSet.allOf(LearnerKey.class)) {
			t.addField(key.name(), key.name()); 
		}
		
		if (isImportLearner) {
			DataField field = new DataField(AppConstants.IMPORT_CHANGES, AppConstants.IMPORT_CHANGES);
			field.setType(Boolean.class);
			t.addField(field);
		}
		
		ItemModelProcessor processor = new ItemModelProcessor(gbItem) {
			@Override
			public void doItem(Item itemModel) {
				String id = itemModel.getIdentifier();
				t.addField(id, id);
				String droppedKey = DataTypeConversionUtil.buildDroppedKey(id);
				t.addField(droppedKey, droppedKey);
				
				String commentedKey = DataTypeConversionUtil.buildCommentKey(id);
				t.addField(commentedKey, commentedKey);
				
				String commentTextKey = DataTypeConversionUtil.buildCommentTextKey(id);
				t.addField(commentTextKey, commentTextKey);
				
				String excusedKey = DataTypeConversionUtil.buildExcusedKey(id);
				t.addField(excusedKey, excusedKey);
				
				String failedKey = DataTypeConversionUtil.buildFailedKey(id);
				t.addField(failedKey, failedKey);
				
				if (isImportLearner) {
					String successKey = DataTypeConversionUtil.buildSuccessKey(id);
					t.addField(successKey, successKey);
				}
			}
		};
		
		processor.process();
		
		return t;
	}
	
}
