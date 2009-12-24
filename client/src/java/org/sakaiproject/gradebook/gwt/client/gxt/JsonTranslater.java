package org.sakaiproject.gradebook.gwt.client.gxt;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class JsonTranslater {

	private ModelType modelType;
	
	public JsonTranslater(ModelType modelType) {
		this.modelType = modelType;
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
				// nothing
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
				// nothing
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
