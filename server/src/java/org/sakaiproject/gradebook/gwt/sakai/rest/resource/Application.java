package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;

@Path("/gradebook/rest/application")
public class Application {

	private Gradebook2ComponentService service;
	
	@GET
    @Produces("application/json")
    public String get() {
		
		Map<String, Object> map = service.getApplicationMap();
		
		//HashMap<String, Object> map = new HashMap<String, Object>();
		//map.put("test", "Testing");
		
		ObjectMapper mapper = new ObjectMapper();
		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, map);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return w.toString();
		
		/*
		JSONObject jsonObject = new JSONObject();
    	try {
    		
    		
    		
    		jsonObject.put(ApplicationKey.PLACEMENTID.name(), applicationModel.getPlacementId());
    		
    	//	JSONArray
    		
    		
    		//for (Enum<?> en : EnumSet.allOf(ApplicationKey.class)) {
    		//	jsonObject.put(en.name(), applicationModel.get(en.name()));
    		//}
 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String str = jsonObject.toString();
    	
        return str;*/
	}

	public Gradebook2ComponentService getService() {
		return service;
	}

	public void setService(Gradebook2ComponentService service) {
		this.service = service;
	}
	
}
