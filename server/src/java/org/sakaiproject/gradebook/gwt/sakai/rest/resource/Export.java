package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.server.BrowserDetect;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility;
import org.sakaiproject.gradebook.gwt.server.UserAgent;

@Path("export")
public class Export extends Resource {

	private static Log log = LogFactory.getLog(Export.class);
	
	@GET @Path("{uid}{structure:(/structure/[^/]+?)?}{filetype:(/filetype/[^/]+?)?}")
	@Produces("application/json")
	public String export(@PathParam("uid") String gradebookUid, 
			@PathParam("structure") String includeStructureFlag, @PathParam("filetype") String format,
			@Context HttpServletResponse response, @Context HttpServletRequest request) {
		
		boolean includeStructure = !"".equals(includeStructureFlag)
					&& includeStructureFlag.indexOf("/") > -1
					&& "true".equals(includeStructureFlag.split("/")[2].toLowerCase());
		
		String fileType = "".equals(format) || format.indexOf("/") == -1 ? ImportExportUtility.FileType.CSV.getExtension() : format.split("/")[2].toLowerCase();
		
		ImportExportUtility utility = new ImportExportUtility();
		
		// GRBK-665
		if (request.getScheme().equals("https") &&
				BrowserDetect.atLeast(request, UserAgent.IE, 8)) {
			response.setHeader("Pragma", "");
			response.setHeader("Cache-Control", "");
		}

		try {
			if ( ! ImportExportUtility.SUPPORTED_FILE_TYPES.contains(fileType)) {
				throw new FatalException("Unsupported file type: " + fileType);
			}
			utility.exportGradebook(service, gradebookUid, includeStructure, true, null, response, fileType);
		} catch (FatalException e) {
			log.error("EXCEPTION: Wasn't able to export gradebook: " + gradebookUid, e);
			// 500 Internal Server Error
			response.setStatus(500);
		}
		return null;
	}
}
