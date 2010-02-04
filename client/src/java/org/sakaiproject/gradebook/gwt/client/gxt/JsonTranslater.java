package org.sakaiproject.gradebook.gwt.client.gxt;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;

import org.sakaiproject.gradebook.gwt.client.gxt.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.FixedColumnModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.gxt.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.key.ActionKey;
import org.sakaiproject.gradebook.gwt.client.model.key.ApplicationKey;
import org.sakaiproject.gradebook.gwt.client.model.key.FixedColumnKey;
import org.sakaiproject.gradebook.gwt.client.model.key.GradeFormatKey;
import org.sakaiproject.gradebook.gwt.client.model.key.GradebookKey;
import org.sakaiproject.gradebook.gwt.client.model.key.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.key.LearnerKey;
import org.sakaiproject.gradebook.gwt.client.model.key.StatisticsKey;
import org.sakaiproject.gradebook.gwt.client.model.key.UploadKey;
import org.sakaiproject.gradebook.gwt.client.model.key.VerificationKey;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class JsonTranslater {

	private ModelType modelType;
	
	public JsonTranslater(ModelType modelType) {
		this.modelType = modelType;
	}
	
	public JsonTranslater(EnumSet<?> keyEnumSet) {
		this.modelType = new ModelType();
		addModelTypeFields(modelType, keyEnumSet, false);
	}
	
	public static void addModelTypeFields(ModelType modelType, EnumSet<?> keyEnumSet, boolean ignoreType) {
		for (Enum<?> e : keyEnumSet) {
			DataField field = new DataField(e.name(), e.name());
			Class<?> type = null;
			
			String format = "yyyy-MM-dd";
			
			if (!ignoreType) {
				if (e instanceof GradebookKey) 
					type = ((GradebookKey)e).getType();
				else if (e instanceof ItemKey) 
					type = ((ItemKey)e).getType();
				else if (e instanceof GradeFormatKey) 
					type = ((GradeFormatKey)e).getType();
				else if (e instanceof ActionKey) {
					type = ((ActionKey)e).getType();
					format = DateTimeFormat.getMediumDateFormat().getPattern();
				} else if (e instanceof VerificationKey) 
					type = ((VerificationKey)e).getType();
				
				if (type != null) {
					if (type.equals(String.class))
						type = null;
					else if (type.equals(Date.class)) {
						if (ignoreType)
							type = null;
						else
							field.setFormat(format);
					}
				}
			}

			field.setType(type);
			modelType.addField(field);
		}
	}
	
	public ModelData translate(Object data) {
		JSONObject obj = null;
	    if (data instanceof JavaScriptObject) {
	    	obj = new JSONObject((JavaScriptObject) data);
	    } else if (data instanceof JSONObject) {
	    	obj = (JSONObject)data;
	    } else {
	    	obj = (JSONObject) JSONParser.parse((String) data);
	    }
	    
		ModelData model = newModelInstance();
		for (int j = 0; j < modelType.getFieldCount(); j++) {
			DataField field = modelType.getField(j);
			String map = field.getMap() != null ? field.getMap() : field.getName();
			JSONValue value = obj.get(map);

			if (value == null) continue;
			setValue(model, value, field);
		}
		return model;
	}
	
	private void setValue(ModelData model, JSONValue value, DataField field) {

		String name = field.getName();
		Class type = field.getType();
		
		if (value.isArray() != null) {
			
			JSONArray jsonArray = value.isArray();
			
			ArrayList array = new ArrayList(jsonArray.size());
			for (int i=0;i<jsonArray.size();i++) {
				JSONValue elementValue = jsonArray.get(i);
				JSONObject elementObject = elementValue.isObject();
				JSONString elementString = elementValue.isString();
				if (elementObject != null) {
			
					if (name.equals(ApplicationKey.GRADEBOOKMODELS.name())) {
						JsonTranslater gbTranslater = new JsonTranslater(EnumSet.allOf(GradebookKey.class)) {
							protected ModelData newModelInstance() {
								return new GradebookModel();
							}
						};
						
						array.add(gbTranslater.translate(elementValue.toString()));
					} else if (name.equals(GradebookKey.COLUMNS.name())) {
						
						JsonTranslater colTranslater = new JsonTranslater(EnumSet.allOf(FixedColumnKey.class)) {
							protected ModelData newModelInstance() {
								return new FixedColumnModel();
							}
						};
						
						array.add(colTranslater.translate(elementValue.toString()));
					} else if (name.equals(ItemKey.CHILDREN.name())) {
						JsonTranslater itemTranslater = new JsonTranslater(EnumSet.allOf(ItemKey.class)) {
							protected ModelData newModelInstance() {
								return new ItemModel();
							}
						};
						
						ItemModel childModel = (ItemModel)itemTranslater.translate(elementObject.toString());
						if (model instanceof Item) {
							((ItemModel) model).add(childModel);
						}
						
						array.add(childModel);
					} else if (name.equals(GradebookKey.STATSMODELS.name())) {
						
						JsonTranslater statsTranslater = new JsonTranslater(EnumSet.allOf(StatisticsKey.class)) {
							protected ModelData newModelInstance() {
								return new BaseModel();
							}
						};
						array.add(statsTranslater.translate(elementValue.toString()));
					} else if (name.equals(UploadKey.ROWS.name())) {
						
						JsonTranslater rowsTranslater = new JsonTranslater(EnumSet.allOf(LearnerKey.class));
						
						array.add(rowsTranslater.translate(elementValue.toString()));
					}
					
				} else if (elementString != null) {
					if (name.equals(ApplicationKey.ENABLEDGRADETYPES.name())) {
						array.add(GradeType.valueOf(elementString.stringValue()));
					} else if (name.equals(UploadKey.RESULTS.name())) {
						array.add(elementString.stringValue());
					}
				}
			}

			model.set(name, array);
			
		} else if (value.isBoolean() != null) {
			model.set(name, value.isBoolean().booleanValue());
		} else if (value.isNumber() != null) {
			if (type != null) {
				Double d = value.isNumber().doubleValue();
				if (type.equals(Integer.class)) {
					model.set(name, d.intValue());
				} else if (type.equals(Long.class)) {
					model.set(name, d.longValue());
				} else if (type.equals(Float.class)) {
					model.set(name, d.floatValue());
				} else {
					model.set(name, d);
				}
			} else {
				model.set(name, value.isNumber().doubleValue());
			}
		} else if (value.isObject() != null) {
			
			if (name.equals(GradebookKey.GRADEBOOKITEMMODEL.name())) {
				
				JsonTranslater itemTranslater = new JsonTranslater(EnumSet.allOf(ItemKey.class)) {
					protected ModelData newModelInstance() {
						return new ItemModel();
					}
				};
				
				model.set(name, itemTranslater.translate(value.toString()));
			} else if (name.equals(GradebookKey.CONFIGURATIONMODEL.name())) {
				
				JSONObject o = value.isObject();
				
				ConfigurationModel configModel = new ConfigurationModel();// (ConfigurationModel)itemTranslater.translate(value.toString());
				
				for (String key : o.keySet()) {
					//configModel.set(key, o.get(key));
					DataField f = new DataField(key);
					f.setType(String.class);
					setValue(configModel, o.get(key), f);
				}
				
				model.set(name, configModel);
			} else if (name.equals(GradebookKey.USERASSTUDENT.name())) {
				
				JsonTranslater learnerTranslater = new JsonTranslater(EnumSet.allOf(LearnerKey.class)) {
					protected ModelData newModelInstance() {
						return new BaseModel();
					}
				};
				
				model.set(name, learnerTranslater.translate(value.toString()));
			} else if (name.equals(UploadKey.GRADEBOOK_ITEM_MODEL.name())) {
				JsonTranslater itemTranslater = new JsonTranslater(EnumSet.allOf(ItemKey.class)) {
					protected ModelData newModelInstance() {
						return new ItemModel();
					}
				};
				
				model.set(name, itemTranslater.translate(value.toString()));
			}
			
		} else if (value.isString() != null) {
			String s = value.isString().stringValue();
			if (type != null) {
				if (type.equals(Date.class)) {
					if ("timestamp".equals(field.getFormat())) {
						Date d = new Date(Long.parseLong(s) * 1000);
						model.set(name, d);
					} else {
						DateTimeFormat format = DateTimeFormat.getFormat(field.getFormat());
						Date d = format.parse(s);
						model.set(name, d);
					}
				} else if (type.equals(GradeType.class)) {
					model.set(name, GradeType.valueOf(s));
				} else if (type.equals(CategoryType.class)) {
					model.set(name, CategoryType.valueOf(s));
				} else if (type.equals(ItemType.class)) {
					model.set(name, ItemType.valueOf(s));
				} else if (type.equals(String.class)) {
					model.set(name, s);
				} else {
					model.set(name, s);
				}
			} else {
				model.set(name, s);
			}
		} else if (value.isNull() != null) {
			model.set(name, null);
		}
	}

	protected ModelData newModelInstance() {
		return new BaseModel();
	}
}
