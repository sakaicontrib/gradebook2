package org.sakaiproject.gradebook.gwt.client.gxt;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.gxt.model.EntityOverlay;

import com.extjs.gxt.ui.client.data.JsonPagingLoadResultReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelType;
import com.google.gwt.core.client.JsArray;

public abstract class EntityOverlayResultReader<D> extends JsonPagingLoadResultReader<D> {

	private ModelType modelType;
	
	public EntityOverlayResultReader(ModelType modelType) {
		super(modelType);
		this.modelType = modelType;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public D read(Object loadConfig, Object data) {
		EntityOverlay overlay = JsonUtil.toOverlay((String)data);
		JsArray<EntityOverlay> jsObjects = overlay.getList();
		
		List<ModelData> models = new ArrayList<ModelData>();
		
		for (int i=0;i<jsObjects.length();i++) {
			models.add(newModelInstance(jsObjects.get(i)));
		}
		
		int totalCount = models.size();
		if (modelType.getTotalName() != null) {
			String totalString = overlay.getTotal();
			if (totalString != null) 
				totalCount = Integer.parseInt(totalString);
		}
		return (D) createReturnData(loadConfig, models, totalCount);
	}
	
	protected abstract ModelData newModelInstance(EntityOverlay overlay);

}
