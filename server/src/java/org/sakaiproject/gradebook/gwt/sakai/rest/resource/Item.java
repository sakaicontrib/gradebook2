package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.sakaiproject.gradebook.gwt.client.BusinessLogicCode;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;
import org.sakaiproject.gradebook.gwt.server.model.GradeItemImpl;

@Path("item")
public class Item extends Resource {
	
	@POST @Path("{uid}/{id}")
	@Consumes("application/json")
	@Produces("application/json")
	public String create(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId, 
			String model) throws InvalidInputException {
		
		Map<String,Object> map = fromJson(model, Map.class);
		org.sakaiproject.gradebook.gwt.client.model.Item result = 
			service.createItem(gradebookUid, gradebookId, new GradeItemImpl(map), true);
		
		return toJson(result.getProperties());
	}
	
	@GET @Path("{uid}/{id}")
	@Produces("application/json")
	public String getList(@PathParam("uid") String gradebookUid, @PathParam("id") Long gradebookId, 
			@QueryParam("type") String itemType) {
		return toJson(service.getItem(gradebookUid, gradebookId, null));
	}
	
	@DELETE
	@Consumes("application/json")
	@Produces("application/json")
	public String remove(String model) throws InvalidInputException {
		Map<String,Object> map = fromJson(model, Map.class);
		
		/*
		 * GRBK-414 : During a delete, we shouldn't check any business rules.
		 * Thus we add all the existing rules to be ignored during a delete
		 */
		GradeItem gradeItem = new GradeItemImpl(map);
		List<BusinessLogicCode> ignoredBusinessRules = gradeItem.getIgnoredBusinessRules();
		
		for(BusinessLogicCode businessLogicCode :BusinessLogicCode.values()) {
			
			ignoredBusinessRules.add(businessLogicCode);
		}

		org.sakaiproject.gradebook.gwt.client.model.Item result = service.updateItem(gradeItem);
		
		return toJson(result);
	}
	
	@PUT
	@Consumes("application/json")
	@Produces("application/json")
	public String update(String model) throws InvalidInputException {
		Map<String,Object> map = fromJson(model, Map.class);
		org.sakaiproject.gradebook.gwt.client.model.Item result = 
			service.updateItem(new GradeItemImpl(map));
		
		return toJson(result);
	}

}
