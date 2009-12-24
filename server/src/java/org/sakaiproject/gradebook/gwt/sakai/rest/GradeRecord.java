package org.sakaiproject.gradebook.gwt.sakai.rest;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.xml.bind.JAXBElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.sakaiproject.gradebook.gwt.client.DataTypeConversionUtil;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;

@Path("/gradebook/rest/graderecord")
public class GradeRecord {

	private static final Log log = LogFactory.getLog(GradeRecord.class);
	
	private Gradebook2ComponentService service;
	
	@PUT @Path("comment")
	@Consumes({"application/xml", "application/json"})
	public String assignComment(JAXBElement<GradeAction> jaxbAction/*@QueryParam("uid") String gradebookUid, 
			@QueryParam("id") Long gradebookId,
			@QueryParam("studentUid") String studentUid,
			@QueryParam("itemId") String itemId,
			@QueryParam("value") Double value,
			@QueryParam("oldValue") Double previousValue*/) throws InvalidInputException {
		
		GradeAction action = jaxbAction.getValue();
		Map<String,Object> map = null;
		
		map = service.assignComment(action.getItemId(), action.getStudentUid(), action.getStringValue());
		
		JSONObject object = new JSONObject();
		for (String n : map.keySet()) {
			Object v = map.get(n);
			try {
				object.put(n, v);
			} catch (JSONException e) {
				log.error(e);
			}
		}
		
		return object.toString();
	}
	
	@PUT @Path("numeric")
	@Consumes({"application/xml", "application/json"})
	public String assignNumericScore(JAXBElement<GradeAction> jaxbAction/*@QueryParam("uid") String gradebookUid, 
			@QueryParam("id") Long gradebookId,
			@QueryParam("studentUid") String studentUid,
			@QueryParam("itemId") String itemId,
			@QueryParam("value") Double value,
			@QueryParam("oldValue") Double previousValue*/) throws InvalidInputException {
		
		GradeAction action = jaxbAction.getValue();
		
		boolean isNumericGrade = DataTypeConversionUtil.checkBoolean(action.getNumeric());
		
		Map<String,Object> map = null;
		
		map = service.assignScore(action.getGradebookUid(), 
				action.getStudentUid(), action.getItemId(), action.getValue(), 
				action.getPreviousValue());

		JSONObject object = new JSONObject();
		for (String n : map.keySet()) {
			Object v = map.get(n);
			try {
				object.put(n, v);
			} catch (JSONException e) {
				log.error(e);
			}
		}
		
		return object.toString();
	}
	
	@PUT @Path("string")
	@Consumes({"application/xml", "application/json"})
	public String assignStringScore(JAXBElement<GradeAction> jaxbAction/*@QueryParam("uid") String gradebookUid, 
			@QueryParam("id") Long gradebookId,
			@QueryParam("studentUid") String studentUid,
			@QueryParam("itemId") String itemId,
			@QueryParam("value") Double value,
			@QueryParam("oldValue") Double previousValue*/) throws InvalidInputException {
		
		GradeAction action = jaxbAction.getValue();
		
		boolean isNumericGrade = DataTypeConversionUtil.checkBoolean(action.getNumeric());
		
		Map<String,Object> map = null;
		
		map = service.assignScore(action.getGradebookUid(), 
					action.getStudentUid(), action.getItemId(), action.getStringValue(), 
					action.getPreviousStringValue());
		
		JSONObject object = new JSONObject();
		for (String n : map.keySet()) {
			Object v = map.get(n);
			try {
				object.put(n, v);
			} catch (JSONException e) {
				log.error(e);
			}
		}
		
		return object.toString();
	}
	
	public Gradebook2ComponentService getService() {
		return service;
	}

	public void setService(Gradebook2ComponentService service) {
		this.service = service;
	}
	
	
	
}
