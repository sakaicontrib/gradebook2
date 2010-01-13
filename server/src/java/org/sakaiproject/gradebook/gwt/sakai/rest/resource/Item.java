package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;

@Path("/gradebook/rest/item")
public class Item {
	
	private Gradebook2ComponentService service;
	
	@POST @Path("{uid}/{id}")
	@Consumes({"application/xml", "application/json"})
	public String create(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId, 
			String model) throws InvalidInputException {
		
		StringWriter w = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map map = mapper.readValue(model, Map.class);
			Map<String,Object> result = service.createItem(gradebookUid, gradebookId, map);
			mapper.writeValue(w, result);
		} catch (JsonParseException e) {
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
	
	@DELETE
	@Consumes({"application/xml", "application/json"})
	public String remove(String model) throws InvalidInputException {
	
		StringWriter w = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map map = mapper.readValue(model, Map.class);
			Map<String,Object> result = service.updateItem(map);
			mapper.writeValue(w, result);
		} catch (JsonParseException e) {
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
	
	@PUT
	@Consumes({"application/xml", "application/json"})
	public String update(String model) throws InvalidInputException {
	
		StringWriter w = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map map = mapper.readValue(model, Map.class);
			Map<String,Object> result = service.updateItem(map);
			mapper.writeValue(w, result);
		} catch (JsonParseException e) {
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
