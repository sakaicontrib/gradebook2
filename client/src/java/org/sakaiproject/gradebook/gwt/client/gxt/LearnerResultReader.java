package org.sakaiproject.gradebook.gwt.client.gxt;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.sakaiproject.gradebook.gwt.client.gxt.model.LearnerModel;

import com.extjs.gxt.ui.client.data.DataField;
import com.extjs.gxt.ui.client.data.JsonPagingLoadResultReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class LearnerResultReader<D> extends JsonPagingLoadResultReader<D> {

	private ModelType modelType;
	private Set<String> fieldSet;
	
	public LearnerResultReader(ModelType modelType) {
		super(modelType);
		this.modelType = modelType;
		this.fieldSet = new HashSet<String>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public D read(Object loadConfig, Object data) {
		JSONObject jsonRoot = null;
		if (data instanceof JavaScriptObject) {
			jsonRoot = new JSONObject((JavaScriptObject) data);
		} else {
			jsonRoot = (JSONObject) JSONParser.parse((String) data);
		}
		JSONArray root = (JSONArray) jsonRoot.get(modelType.getRoot());
		int size = root.size();
		ArrayList<ModelData> models = new ArrayList<ModelData>();
		for (int i = 0; i < size; i++) {
			JSONObject obj = (JSONObject) root.get(i);
			JsonTranslater.decorateModelType(modelType, fieldSet, obj);
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
			models.add(model);
		}
		int totalCount = models.size();
		if (modelType.getTotalName() != null) {
			totalCount = getTotalCount(jsonRoot);
		}
		return (D) createReturnData(loadConfig, models, totalCount);
	}
	
	protected int getTotalCount(JSONObject root) {
		if (modelType.getTotalName() != null) {
			JSONValue v = root.get(modelType.getTotalName());
			if (v != null) {
				if (v.isNumber() != null) {
					return (int) v.isNumber().doubleValue();
				} else if (v.isString() != null) {
					return Integer.parseInt(v.isString().stringValue());
				}
			}
		}
		return -1;
	}
	
	protected ModelData newModelInstance() {
	    return new LearnerModel();
	}
}
