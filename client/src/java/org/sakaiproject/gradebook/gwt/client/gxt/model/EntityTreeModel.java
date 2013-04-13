package org.sakaiproject.gradebook.gwt.client.gxt.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.sakaiproject.gradebook.gwt.client.gxt.JsonUtil;

import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.core.FastSet;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;

public class EntityTreeModel extends BaseTreeModel implements EntityOverlayOwner {

	private static final long serialVersionUID = 1L;

	private EntityOverlay overlay = null;

	public EntityTreeModel() {
		super();
		this.overlay = JsonUtil.toOverlay("{ }");
	}

	public EntityTreeModel(Map<String, Object> properties) {
		super(properties);
		this.overlay = JsonUtil.toOverlay("{ }");
	}

	public EntityTreeModel(EntityOverlay overlay) {
		super();
		this.overlay = overlay;
	}

	public EntityOverlay getOverlay() {
		return overlay;
	}
	
	public String getJSON() {
		if (overlay != null) {
			JSONObject jso = new JSONObject(overlay) {
				public JSONValue get(String key) {
				    if (key == null) {
				      throw new NullPointerException();
				    }
				    
				    if (key.startsWith("M_")) {
				    	return new JSONObject(overlay.getEntityOverlay(key));
				    } else if (key.startsWith("A_")) {
				    	return new JSONArray(overlay.getArray(key));
				    }
				    
				    return super.get(key);
				}
			};
			return jso.toString();
		}
			
		return null;
	}
	
	@Override
	public <X> X get(String name) {

		if (null != overlay) 
			return (X)overlay.safeGet(this, name);
		
		return null;
	}

	public Long getLong(String name) {

		if (null != overlay) {
			return (Long)overlay.safeGet(this, name);
		}
		else {
			return super.get(name);
		}

	}

	public Map<String, Object> getProperties() {
		if (null == overlay)
			return super.getProperties();

		Map<String, Object> newMap = new FastMap<Object>();
		Collection<String> keys = getPropertyNames();
		if (keys != null) {
			for (String key : keys) {
				newMap.put(key, overlay.safeGet(this, key));
			}
		}
		return newMap;
	}

	public Collection<String> getPropertyNames() {
		if (null == overlay)
			return super.getPropertyNames();

		Set<String> set = new FastSet();
		JsArrayString array = overlay.getKeys();

		if (array != null) {
			for (int i=0;i<array.length();i++) {
				set.add(array.get(i));
			}
		}

		return set;
	}

	@Override
	public <X> X set(String name, X value) {

		if (null!= overlay) {
			X oldValue = (X)overlay.safeGet(this, name);
			overlay.safeSet(this, name, value);
			notifyPropertyChanged(name, value, oldValue);
			return oldValue;
		} else {
			return super.set(name, value);
		}
	}

	public boolean isChildString(String property) {
		return isGradeType(property) || isCategoryType(property);
	}

	public boolean isGradeType(String property) {
		return false;
	}

	public boolean isCategoryType(String property) {
		return false;
	}

	public boolean isChildModel(String property) {
		 return false;
	}

	public DateTimeFormat getDateTimeFormat(String property) {
		return DateTimeFormat.getMediumDateFormat();
	}

	public ModelData newChildModel(String property, EntityOverlay overlay) {
		return new BaseModel();
	}
	
	public String toString() {
		
		return getJSON();
		
	}

}
