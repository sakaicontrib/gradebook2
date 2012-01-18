package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.server.BrowserDetect;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility.FileType;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtilityImpl;
import org.sakaiproject.gradebook.gwt.server.UserAgent;
import org.sakaiproject.site.api.Site;

@Path("export")
public class Export extends Resource {

	private static Log log = LogFactory.getLog(Export.class);
	
	private ImportExportUtility importExportUtility = null;

	public void setImportExportUtility(ImportExportUtility importExportUtility) {
		this.importExportUtility = importExportUtility;
	}

	@POST @Path("{uid}{structure:(/structure/[^/]+?)?}{filetype:(/filetype/[^/]+?)?}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("application/json")
	public String export(@PathParam("uid") String gradebookUid, 
			@PathParam("structure") String includeStructureFlag, @PathParam("filetype") String format,
			@FormParam(AppConstants.SECTIONS_FIELD) List<String> sections,
			@FormParam(AppConstants.INCLUDE_COMMENTS_FIELD) boolean includeComments,
			@Context HttpServletResponse response, @Context HttpServletRequest request) throws IOException {
		
		boolean includeStructure = !"".equals(includeStructureFlag)
					&& includeStructureFlag.indexOf("/") > -1
					&& "true".equals(includeStructureFlag.split("/")[2].toLowerCase());
		
		String fileType = "".equals(format) || format.indexOf("/") == -1 ? 
				ImportExportUtilityImpl.FileType.CSV.getName() : 
					format.split("/")[2].toLowerCase();
	
		List<String> sectionList = null;
				
		if (sections != null && sections.size()>0) {
			/*
			 *  this may not be necessary: jersey should be setting to null and not
			 *  creating zero-length lists... but just in case
			 */
			sectionList = sections;
			
		}

		try {
			if ( ! ImportExportUtilityImpl.SUPPORTED_FILE_TYPES.contains(fileType)) {
				throw new FatalException("Unsupported file type: " + fileType);
			}
			
			FileType type = FileType.getType(fileType);
			OutputStream out = response.getOutputStream();
			StringBuilder filename = new StringBuilder();
			Site site = service.getSite();
			
			if (site == null)
				filename.append("gradebook");
			else {
				String name = site.getTitle();
				name = name.replaceAll(ImportExportUtilityImpl.UNSAFE_FILENAME_CHAR_REGEX , "_");

				filename.append(name);
			}
			filename.append(type.getExtension());
			
			if (response != null) {
				
				// GRBK-665
				if (request.getScheme().equals("https") &&
						BrowserDetect.atLeast(request, UserAgent.IE, 7)) {
					response.setHeader("Pragma", "");
					response.setHeader("Cache-Control", "");
				}
				
				response.setContentType(type.getMimeType());
				response.setHeader(
						ImportExportUtilityImpl.CONTENT_DISPOSITION_HEADER_NAME,
						ImportExportUtilityImpl.CONTENT_DISPOSITION_HEADER_ATTACHMENT
										+ filename.toString());
			}
			
				importExportUtility.exportGradebook (type, filename.toString(), out, service, gradebookUid, includeStructure, includeComments, sectionList); 
						
			
		} catch (FatalException e) {
			log.error("EXCEPTION: Wasn't able to export gradebook: " + gradebookUid, e);
			// 500 Internal Server Error
			response.setStatus(500);
		}
	
		return null;
	}
}
