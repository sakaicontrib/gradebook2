package org.sakaiproject.gradebook.gwt.client.gxt.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.model.type.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.type.GradeType;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.RegExp;

public class EntityOverlay extends JavaScriptObject implements Serializable {

	private static final long serialVersionUID = 1L;
	
	static class EntityOverlayUtil$ {
		public static RegExp unixEpochPattern = RegExp.compile("\\d{13}");
	}
	

	protected EntityOverlay() {		
	}
		
	public final Object safeGet(EntityOverlayOwner owner, String property) {
		if (property == null)
			return null;
				
		
		if (isBoolean(property))
			return Boolean.valueOf(Boolean.parseBoolean(getString(property)));
		
		
		// Determine Java type from property
		char c = property.charAt(0);
		
		String s = null;
		
		switch (c) {
		case AppConstants.CATEGORYTYPE_PREFIX:
			s = getString(property);
			if (s == null)
				return null;
			return CategoryType.valueOf(s);
		case AppConstants.GRADETYPE_PREFIX:
			s = getString(property);
			if (s == null)
				return null;
			return GradeType.valueOf(s);
		case AppConstants.STRING_PREFIX:
			return getString(property);
		case AppConstants.BOOLEAN_PREFIX:
			s = getString(property);
			if (s == null)
				return null;
			return Boolean.valueOf(Boolean.parseBoolean(s));
		case AppConstants.INTEGER_PREFIX:
			s = getNumberAsString(property);
			if (s == null)
				return null;
			return Integer.valueOf(Integer.parseInt(s));
		case AppConstants.LONG_PREFIX:
			s = getNumberAsString(property);
			if (s == null)
				return null;
			return Long.valueOf(Long.parseLong(s));
		case AppConstants.DOUBLE_PREFIX:
			s = getNumberAsString(property);
			if (s == null)
				return null;
			return Double.valueOf(Double.parseDouble(s));
		case AppConstants.DATE_PREFIX:
		case AppConstants.ODD_DATE_PREFIX:
			Date d = null;
			s = getString(property);
			if (EntityOverlayUtil$.unixEpochPattern.test(s)) {
				long l = Long.parseLong(s);
				d = new Date(l);
				
			}else {
				try {
					
					if (s != null) {
						DateTimeFormat f = DateTimeFormat.getFormat(AppConstants.LONG_DATE);
						d = f.parse(s);
					}
				} catch (IllegalArgumentException iae) {
					d = DateTimeFormat.getFormat(AppConstants.SHORT_DATE).parse(s);
				}
			}
		
			return d;
		case AppConstants.MODEL_PREFIX:
			Object obj = getObject(property);
			if (obj instanceof EntityOverlay)
				return owner.newChildModel(property, (EntityOverlay)obj);
			else 
				return obj;
		case AppConstants.OBJECT_PREFIX:
			return getObject(property);
		case AppConstants.O_ARRAY_PREFIX:
			if (isArray(property)) {
			//if (owner.isChildString(property)) {
				JsArrayString jsStringObjects = getArrayString(property);
				if (owner.isGradeType(property)) {
					List<GradeType> gradeTypes = new ArrayList<GradeType>();
					for (int i=0;i<jsStringObjects.length();i++) {
						gradeTypes.add(GradeType.valueOf(jsStringObjects.get(i)));
					}
					return gradeTypes;
				}
				
				List<String> list = new ArrayList<String>();
				for (int i=0;i<jsStringObjects.length();i++) {
					list.add(jsStringObjects.get(i));
				}
				
				return list;
			}
			//}
			//break;
		case AppConstants.M_ARRAY_PREFIX:
			if (isArray(property)) {
				JsArray<EntityOverlay> jsObjects = getArray(property);
				List<ModelData> models = new ArrayList<ModelData>();
				
				if (jsObjects != null) {
					for (int i=0;i<jsObjects.length();i++) {
						ModelData model = owner.newChildModel(property, jsObjects.get(i));
						if (model != null)
							models.add(model);
					}
				}
				
				return models;
			} 
		}
		
		// Need to handle the case of item id keyed values that are numeric
		if (isNumber(property)) {
			s = getNumberAsString(property);
			if (s == null)
				return null;
			return Double.valueOf(Double.parseDouble(s));
		}
		
		
		return getObject(property);
	}
	
	public final void safeSet(EntityOverlayOwner owner, String property, Object value) {
		if (value == null) {
			setUndefined(property);
			return;
		} 
		
		char c = property.charAt(0);
		
		switch (c) {
		case AppConstants.CATEGORYTYPE_PREFIX:
			setString(property, ((CategoryType)value).name());
			break;
		case AppConstants.GRADETYPE_PREFIX:
			setString(property, ((GradeType)value).name());
			break;
		case AppConstants.STRING_PREFIX:
			if (value instanceof String)
				setString(property, (String)value);
			else
				setString(property, String.valueOf(value));
			break;
		case AppConstants.BOOLEAN_PREFIX:
			setBoolean(property, ((Boolean) value).booleanValue());
			break;
		case AppConstants.DOUBLE_PREFIX:
		case AppConstants.LONG_PREFIX:
		case AppConstants.INTEGER_PREFIX:
			if (value instanceof String)
				setDouble(property, Double.parseDouble((String)value));
			else
				setDouble(property, ((Number)value).doubleValue());
			break;
		case AppConstants.DATE_PREFIX:
		case AppConstants.ODD_DATE_PREFIX:
			DateTimeFormat f = DateTimeFormat.getFormat(AppConstants.LONG_DATE);
			String s = f.format((Date)value);
			setString(property, s);
			break;
		case AppConstants.MODEL_PREFIX:
			setNative(property, ((EntityOverlayOwner)value).getOverlay());
			break;
		case AppConstants.O_ARRAY_PREFIX:
			JsArrayString jsArrayString = getEmptyArrayString();
			
			if (owner.isGradeType(property)) {
				List<GradeType> gradeTypes = (List<GradeType>)value;
				for (int i=0;i<gradeTypes.size();i++) {
					GradeType gradeType = gradeTypes.get(i);
					jsArrayString.push(gradeType.name());
				}
			} else {
				List<String> list = (List<String>)value;
				for (int i=0;i<list.size();i++) {
					String str = list.get(i);
					jsArrayString.push(str);
				}
			}
			break;
		case AppConstants.M_ARRAY_PREFIX:
			JsArray<EntityOverlay> jsObjects = getEmptyArray();
			
			List<EntityOverlayOwner> models = (List<EntityOverlayOwner>)value;
			if (models != null) {
				for (int i=0;i<models.size();i++) {
					EntityOverlayOwner e = models.get(i);
					jsObjects.push(e.getOverlay());
				}
			}
			setArray(property, jsObjects);
			break;
		default:
			setNative(property, value);
			break;
		}
	}
	
	public final native boolean isNull(String key) /*-{
		var v = this[key];
		return !v || v === null;
	}-*/;
	
	public final native boolean isNumber(String key) /*-{
   		var v = this[key];
		return 'number' === typeof v && isFinite(v);
	}-*/;
	
	public final native boolean isNumberObject(String key) /*-{
		var v = this[key];
		return v instanceof Number;
	}-*/;
	
	public final native boolean isArray(String key) /*-{
		var v = this[key];
		return 'object' === typeof v && v instanceof Array;
	}-*/;
	
	public final native boolean isObject(String key) /*-{
		var v = this[key];
		return 'object' === typeof v;
	}-*/;
	
	public final native boolean isBoolean(String key) /*-{
		var v = this[key];
		return 'boolean' === typeof v;
	}-*/;
	
	public final native boolean isBooleanObject(String key) /*-{
		var v = this[key];
		return v instanceof Boolean;
	}-*/;
	
	public final native boolean isString(String key) /*-{
		var v = this[key];
		return 'string' === typeof v;
	}-*/;
	
	public final native boolean isStringObject(String key) /*-{
		var v = this[key];
		return v instanceof String;
	}-*/;
	
	public final native String getString(String key) /*-{
		var v = this[key];
		
		if (v)
			return "" + v;
		
		return null;
	}-*/;
	
	public final native String getNumberAsString(String key) /*-{
		var v = this[key];
		if (!v)
			return "0";
		if (v === null)
			return "-1";
			
		return "" + v;
	}-*/;
	
	public final native <X> X getNative(String key) /*-{
		var v = this[key];
		return v;
	}-*/;
	
	public final native EntityOverlay getEntityOverlay(String key) /*-{
		return this[key];
	}-*/;
	
	public final native void setArray(String key, JsArray<EntityOverlay> array) /*-{
		this[key] = array;
	}-*/;
	
	public final native JsArray<EntityOverlay> getArray(String key) /*-{
	 	return this[key];
	}-*/;
	
	public final native JsArray<EntityOverlay> getEmptyArray() /*-{
 		return [ ];
	}-*/;
	
	public final native JsArrayString getArrayString(String key) /*-{
 		return this[key];
	}-*/;
	
	public final native JsArrayString getEmptyArrayString() /*-{
		return [ ];
	}-*/;
	
	public final native Boolean getBooleanObject(String key) /*-{
		return this[key];
	}-*/;
	
	public final native String getStringObject(String key) /*-{
		return this[key];
	}-*/;
	
	public final native Number getNumberObject(String key) /*-{
		return this[key];
	}-*/;
	
	public final native Object getObject(String key) /*-{
 		return this[key];
	}-*/;
	
	public final native JsArray<EntityOverlay> getList() /*-{
		return this.list;
	}-*/;
	
	public final native JsArrayString getKeys() /*-{
		var keys = [];
	  	for(i in this) if (this.hasOwnProperty(i))
	  	{
	    	keys.push(i);
	  	}
	  	return keys;
	}-*/;
	
	public final native String getTotal() /*-{
		return this.total;
	}-*/;
	
	public final native void setUndefined(String key) /*-{
		this[key] = undefined;
	}-*/;
	
	public final native <X> void setNative(String key, X value) /*-{
		this[key] = value;
	}-*/;
	
	public final native void setBoolean(String key, boolean value) /*-{
		this[key] = value;
	}-*/;
	
	public final native void setString(String key, String value) /*-{
		this[key] = value;
	}-*/;
	
	public final native void setDouble(String key, double value) /*-{
		this[key] = value;
	}-*/;
	
}
