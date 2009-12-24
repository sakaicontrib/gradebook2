package org.sakaiproject.gradebook.gwt.sakai.rest;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeLoadConfig;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2Service;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

@Path("/gradebook/rest/roster")
public class Roster {
    
	private static final Log log = LogFactory.getLog(Roster.class);
	
	private Gradebook2Service service;
	
    // The Java method will process HTTP GET requests
    @GET
    @Produces("application/json")
    public String get(@QueryParam("uid") String gradebookUid, @QueryParam("id") Long gradebookId,
    		@QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset,
    		@QueryParam("sortField") String sortField, @QueryParam("sortDir") String sortDir, 
    		@QueryParam("sectionUuid") String sectionUuid, @QueryParam("searchString") String searchString) {
    	
    	log.info("GET!");
    	
    	MultiGradeLoadConfig loadConfig = new MultiGradeLoadConfig();
    	loadConfig.setLimit(limit == null ? -1 : limit.intValue());
    	loadConfig.setOffset(offset == null ? -1 : offset.intValue());
    	loadConfig.setSearchString(searchString);
    	loadConfig.setSectionUuid(sectionUuid);
    	loadConfig.setSortDir(SortDir.findDir(sortDir));
    	loadConfig.setSortField(sortField);
    	
    	PagingLoadResult<BaseModel> result = service.getStudentRows(gradebookUid, gradebookId, loadConfig, false);
    	List<BaseModel> models = result.getData();
    	
    	JSONArray array = new JSONArray();
    	int index = 0;
    	for (BaseModel model : models) {
    		JSONObject object = new JSONObject();
    		for (String name : model.getPropertyNames()) {
    			Object value = model.get(name);
    			try {
					object.put(name, value);
				} catch (JSONException e) {
					e.printStackTrace();
				}
    		}
    		array.put(object);
    	}
    	
    	JSONObject jsonObject = new JSONObject();
    	try {
			jsonObject.put("learners", array);
			jsonObject.put("total", result.getTotalLength());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String str = jsonObject.toString();
    	
        return str;
    }

	public Gradebook2Service getService() {
		return service;
	}

	public void setService(Gradebook2Service service) {
		this.service = service;
	}
    
}
