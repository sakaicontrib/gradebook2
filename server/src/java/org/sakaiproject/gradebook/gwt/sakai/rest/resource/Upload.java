package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.BusinessLogicCode;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.sakaiproject.gradebook.gwt.server.model.UploadImpl;

@Path(AppConstants.UPLOAD_FRAGMENT)
public class Upload extends Resource {

	@PUT
	@Path("{uid}/{id}{force:(/" + AppConstants.FORCE_FRAGMENT + "/[^/]+?)?}")
	@Consumes( { "application/json" })
	public String update(@PathParam("uid") String gradebookUid,
			@PathParam("id") Long gradebookId, String model,
			@PathParam("force") String forceFlag) throws InvalidInputException {

		boolean force = !"".equals(forceFlag) && forceFlag.indexOf("/") > -1
				&& "true".equals(forceFlag.split("/")[2].toLowerCase());

		org.sakaiproject.gradebook.gwt.client.model.Upload result = null;
		Map<String, Object> map = fromJson(model, Map.class);
		
		if(force) {
			List<BusinessLogicCode> ignoredBusinessRules = 
				new ArrayList<BusinessLogicCode>();
			ignoredBusinessRules.add(BusinessLogicCode.NoDuplicateItemNamesWithinCategoryRule);
				
			result = service
				.upload(gradebookUid, gradebookId, new UploadImpl(map), false, ignoredBusinessRules);
		} else {
			result = service
				.upload(gradebookUid, gradebookId, new UploadImpl(map), false);
		}

		return toJson(result);
	}
	
	/*
	 * for non-REST access to import process
	 */
	public String update(String gradebookUid,
			Long gradebookId, String model,
			String forceFlag, Gradebook2ComponentService serviceInstance) throws InvalidInputException {
		service = serviceInstance;
				return update(gradebookUid, gradebookId, model, forceFlag);
		
	}
	
	

}
