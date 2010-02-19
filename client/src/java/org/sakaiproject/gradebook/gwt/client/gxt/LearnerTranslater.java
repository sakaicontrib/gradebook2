package org.sakaiproject.gradebook.gwt.client.gxt;

import java.util.EnumSet;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.gxt.model.LearnerModel;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;

public class LearnerTranslater extends JsonTranslater {

	public LearnerTranslater() {
		super();
	}
	
	/*public LearnerTranslater(Item gbItem, boolean isImportLearner) {
		super(generateLearnerModelType(gbItem, isImportLearner));
	}*/
	
	public LearnerTranslater(Item gbItem, boolean isImportLearner) {
		this.modelType = new ModelType();
		this.modelType.setRoot(AppConstants.LIST_ROOT);
		this.modelType.setTotalName(AppConstants.TOTAL);
		this.fieldSet = addModelTypeFields(modelType, EnumSet.allOf(LearnerKey.class), false); //, gbItem, isImportLearner);
	}
	
	/*
	public static Set<String> addModelTypeFields(final ModelType modelType, EnumSet<?> keyEnumSet, boolean ignoreType, Item gbItem, final boolean isImportLearner) {
		final Set<String> fieldSet = addModelTypeFields(modelType, keyEnumSet, ignoreType);
		
		if (isImportLearner) {
			DataField field = new DataField(AppConstants.IMPORT_CHANGES, AppConstants.IMPORT_CHANGES);
			field.setType(Boolean.class);
			modelType.addField(field);
			fieldSet.add(AppConstants.IMPORT_CHANGES);
		}
		
		ItemModelProcessor processor = new ItemModelProcessor(gbItem) {
			@Override
			public void doItem(Item itemModel) {
				String id = itemModel.getIdentifier();
				modelType.addField(id, id);
				String droppedKey = DataTypeConversionUtil.buildDroppedKey(id);
				modelType.addField(droppedKey, droppedKey);
				fieldSet.add(droppedKey);
				
				String commentedKey = DataTypeConversionUtil.buildCommentKey(id);
				modelType.addField(commentedKey, commentedKey);
				fieldSet.add(commentedKey);
				
				String commentTextKey = DataTypeConversionUtil.buildCommentTextKey(id);
				modelType.addField(commentTextKey, commentTextKey);
				fieldSet.add(commentTextKey);
				
				String excusedKey = DataTypeConversionUtil.buildExcusedKey(id);
				modelType.addField(excusedKey, excusedKey);
				fieldSet.add(excusedKey);
				
				String failedKey = DataTypeConversionUtil.buildFailedKey(id);
				modelType.addField(failedKey, failedKey);
				fieldSet.add(failedKey);
				
				if (isImportLearner) {
					String successKey = DataTypeConversionUtil.buildSuccessKey(id);
					modelType.addField(successKey, successKey);
					fieldSet.add(successKey);
				}
			}
		};
		
		processor.process();
		
		return fieldSet;
	}*/
	
	public static ModelType generateLearnerModelType() {
		ModelType modelType = new ModelType();
		modelType.setRoot(AppConstants.LIST_ROOT);
		modelType.setTotalName(AppConstants.TOTAL);
		addModelTypeFields(modelType, EnumSet.allOf(LearnerKey.class), false); //, gbItem, isImportLearner);
		
		return modelType;
		
		/*final ModelType t = new ModelType();  
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
		
		return t;*/
	}
	
	protected ModelData newModelInstance() {
		return new LearnerModel();
	}
	
}
