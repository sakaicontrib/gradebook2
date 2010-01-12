package org.sakaiproject.gradebook.gwt.client.gxt;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;

import org.sakaiproject.gradebook.gwt.client.model.ApplicationKey;
import org.sakaiproject.gradebook.gwt.client.model.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.ConfigurationModel;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumnKey;
import org.sakaiproject.gradebook.gwt.client.model.FixedColumnModel;
import org.sakaiproject.gradebook.gwt.client.model.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookKey;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemKey;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class JsonTranslater {

	private ModelType modelType;
	
	public JsonTranslater(ModelType modelType) {
		this.modelType = modelType;
	}
	
	public JsonTranslater(EnumSet<?> keyEnumSet) {
		this.modelType = new ModelType();
		for (Enum<?> e : keyEnumSet) {
			DataField field = new DataField(e.name());
			Class<?> type = null;
			
			if (e instanceof GradebookKey) {
				field.setType(((GradebookKey)e).getType());
			} else if (e instanceof ItemKey) {
				type = ((ItemKey)e).getType();
				
				field.setType(type);
				if (type != null) {
					if (type.equals(Date.class))
						field.setFormat("yyyy-MM-dd");
				}
			}
			
			modelType.addField(field);
		}
	}
	
	public ModelData translate(Object data) {
		JSONObject obj = null;
	    if (data instanceof JavaScriptObject) {
	    	obj = new JSONObject((JavaScriptObject) data);
	    } else {
	    	obj = (JSONObject) JSONParser.parse((String) data);
	    }
	    
		ModelData model = newModelInstance();
		for (int j = 0; j < modelType.getFieldCount(); j++) {
			DataField field = modelType.getField(j);
			String name = field.getName();
			Class type = field.getType();
			String map = field.getMap() != null ? field.getMap() : field.getName();
			JSONValue value = obj.get(map);

			if (value == null) continue;
			if (value.isArray() != null) {
				
				JSONArray jsonArray = value.isArray();
				
				ArrayList array = new ArrayList(jsonArray.size());
				for (int i=0;i<jsonArray.size();i++) {
					JSONValue elementValue = jsonArray.get(i);
					JSONObject elementObject = elementValue.isObject();
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
						} else if (name.equals(ApplicationKey.ENABLEDGRADETYPES.name())) {
							
							array.add(GradeType.valueOf(elementObject.toString()));
							
						} else if (name.equals(ItemKey.CHILDREN.name())) {
							JsonTranslater itemTranslater = new JsonTranslater(EnumSet.allOf(ItemKey.class)) {
								protected ModelData newModelInstance() {
									return new ItemModel();
								}
							};
							
							ItemModel childModel = (ItemModel)itemTranslater.translate(elementObject.toString());
							if (model instanceof ItemModel) {
								((ItemModel) model).add(childModel);
							}
							
							array.add(childModel);
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
					
					/*JsonTranslater itemTranslater = new JsonTranslater(EnumSet.allOf(ConfigurationKey.class)) {
						protected ModelData newModelInstance() {
							return new ConfigurationModel();
						}
					};*/
					
					ConfigurationModel configModel = new ConfigurationModel();// (ConfigurationModel)itemTranslater.translate(value.toString());
					
					for (String key : o.keySet()) {
						//if (!EnumSet.allOf(ConfigurationKey.class).contains(key)) {
							configModel.set(key, o.get(key));
						//}
					}
					
					model.set(name, configModel);
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
					} else if (type.equals(ItemModel.Type.class)) {
						model.set(name, ItemModel.Type.valueOf(s));
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
		return model;
	}

	protected ModelData newModelInstance() {
		return new BaseModelData();
	}
}
