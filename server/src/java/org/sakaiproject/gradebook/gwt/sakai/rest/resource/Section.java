package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;

@Path("/gradebook/rest/section")
public class Section {

	private Gradebook2ComponentService service;
	
	@GET @Path("{uid}/{id}")
    @Produces("application/json")
    public String get(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId) {
    	
		List<Map<String,Object>> sections = service.getVisibleSections(gradebookUid, true, "All Viewable Sections");
		
		Map<String,Object> wrapper = new HashMap<String, Object>();
		wrapper.put("sections", sections);
		wrapper.put("total", sections.size());
		
		ObjectMapper mapper = new ObjectMapper();
		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, wrapper);
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
	}

	public Gradebook2ComponentService getService() {
		return service;
	}

	public void setService(Gradebook2ComponentService service) {
		this.service = service;
	}
}
