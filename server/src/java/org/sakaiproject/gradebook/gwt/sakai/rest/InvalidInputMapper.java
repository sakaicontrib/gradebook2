package org.sakaiproject.gradebook.gwt.sakai.rest;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.sakaiproject.gradebook.gwt.client.BusinessLogicCode;
import org.sakaiproject.gradebook.gwt.client.exceptions.BusinessRuleException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;

@Provider
public class InvalidInputMapper implements ExceptionMapper<InvalidInputException> {
    public Response toResponse(InvalidInputException ex) {
    	 int status = 400;
    	if (ex != null && ex instanceof BusinessRuleException) {
    		BusinessRuleException bre = (BusinessRuleException)ex;
    		if (bre.getCodes().contains(BusinessLogicCode.NoImportedDuplicateItemNamesWithinCategoryRule)
    				|| bre.getCodes().contains(BusinessLogicCode.NoImportedDuplicateItemNamesRule)) {
    			status = 401;
    		} else if(bre.getCodes().contains(BusinessLogicCode.ScanTronScoresMustBeNormalized)) {
    			status = 411; //more information needed... get it? :)
    		}
    	}
        return Response.status(status).
            entity(ex != null ? ex.getMessage() : "").
            type("text/plain").
            build();
    }
}
