package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.BusinessLogicCode;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.sakaiproject.gradebook.gwt.server.ImportSettingsImpl;
import org.sakaiproject.gradebook.gwt.server.model.UploadImpl;

@Path(AppConstants.UPLOAD_FRAGMENT)
public class Upload extends Resource {
	
	private static final Log log = LogFactory.getLog(Upload.class);
	
	@PUT
	@Path("{uid}/{id}{force:(/" + AppConstants.OVERWRITE_FRAGMENT + "/[^/]+?)?}{maxpnts:(/" + AppConstants.MAXPNTS_FRAGMENT + "/[^/]+?)?}")
	@Consumes("application/json")
	@Produces("application/json")
	public String update(@PathParam("uid") String gradebookUid,
			@PathParam("id") Long gradebookId, String model,
			@PathParam("force") String forceFlag,
			@PathParam("maxpnts") String maxPoints) throws InvalidInputException {

		boolean force = !"".equals(forceFlag) && forceFlag.indexOf("/") > -1
				&& "true".equals(forceFlag.split("/")[2].toLowerCase());
		
		boolean hasSantronPoints = !"".equals(maxPoints) && maxPoints.indexOf("/") > -1;
		

		org.sakaiproject.gradebook.gwt.client.model.Upload result = null;
		Map<String, Object> map = fromJson(model, Map.class);
		
		List<BusinessLogicCode> ignoredBusinessRules = 
			new ArrayList<BusinessLogicCode>();
		
		ImportSettingsImpl importSettings = new ImportSettingsImpl();
		importSettings.setForceOverwriteAssignments(force);
		importSettings.setScantron(hasSantronPoints);
		
		
		if(force) {
			ignoredBusinessRules.add(BusinessLogicCode.NoImportedDuplicateItemNamesWithinCategoryRule);
			ignoredBusinessRules.add(BusinessLogicCode.NoImportedDuplicateItemNamesRule);
		} 
		if(hasSantronPoints) {
			String value = maxPoints.split("/")[2];
			try {
				Integer max = Integer.valueOf(value);/// in case the client-side validation isn't catching it
				importSettings.setScantronMaxPoints(value); //set the string value
				ignoredBusinessRules.add(BusinessLogicCode.ScanTronScoresMustBeNormalized);
				
			} catch (NumberFormatException e) { 
				//TODO: throw invalidinputexception
				log.error("url param ["+AppConstants.MAXPNTS_FRAGMENT+"] not an integer value: " + maxPoints);
			}
		} 
		
		importSettings.setIgnoredBusinessRules(ignoredBusinessRules);
		
		UploadImpl uploadData = new UploadImpl(map);

		uploadData.setImportSettings(importSettings);
		
		result = service
				.upload(gradebookUid, gradebookId, uploadData, false);

		return toJson(result);
	}
	
	/*
	 * for non-REST access to import process
	 */
	public String update(String gradebookUid,
			Long gradebookId, String model,
			String forceFlag, String maxPoints, Gradebook2ComponentService serviceInstance) throws InvalidInputException {
		service = serviceInstance;
				return update(gradebookUid, gradebookId, model, forceFlag, maxPoints);
		
	}
}
