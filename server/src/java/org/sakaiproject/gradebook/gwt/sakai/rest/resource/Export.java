package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

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

	@POST @Path("/{uid}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces("application/json")
	public String export(@PathParam("uid") String gradebookUid, 
			@Context HttpServletResponse response, @Context HttpServletRequest request,
			@FormParam(AppConstants.EXPORT_DATA_FIELD) String jsonData) throws IOException {

		Map<String, Object> exportData = fromJson(jsonData, Map.class);

		boolean includeStructure = (Boolean) exportData.get(AppConstants.EXPORT_DATA_STRUCTURE);
		boolean includeComment = (Boolean) exportData.get(AppConstants.EXPORT_DATA_COMMENTS);
		List<String> sections = (List<String>) exportData.get(AppConstants.EXPORT_DATA_SECTIONS);

		String fileType = (String) exportData.get(AppConstants.EXPORT_DATA_TYPE);

		/*
		 * FIXME:
		 * Further down in the service, we call getRoster(), which expects a null
		 * sections list if the user selected ALL sections. At some point we need to 
		 * fix this to pass in the AppConstants.ALL string instead, as we do in other places.
		 */
		if (sections != null && sections.size() == 0) {

			sections = null;
		}

		try {
			
			if ( ! ImportExportUtilityImpl.SUPPORTED_FILE_TYPES.contains(fileType)) {
			
				throw new FatalException("Unsupported file type: " + fileType);
			}

			FileType type = FileType.getType(fileType);
			OutputStream out = response.getOutputStream();
			StringBuilder filename = new StringBuilder();
			Site site = service.getSite();

			if (site == null) {
				filename.append("gradebook");
			}
			else {
			
				String name = site.getTitle();
				name = name.replaceAll(ImportExportUtilityImpl.UNSAFE_FILENAME_CHAR_REGEX , "_");
				filename.append(name);
			}
			
			filename.append(type.getExtension());

			if (response != null) {

				// GRBK-665
				if (request.getScheme().equals("https") && BrowserDetect.atLeast(request, UserAgent.IE, 7)) {
			
					response.setHeader("Pragma", "");
					response.setHeader("Cache-Control", "");
				}

				response.setContentType(type.getMimeType());
				response.setHeader(ImportExportUtilityImpl.CONTENT_DISPOSITION_HEADER_NAME, ImportExportUtilityImpl.CONTENT_DISPOSITION_HEADER_ATTACHMENT + filename.toString());
			}

			importExportUtility.exportGradebook(type, filename.toString(), out, service, gradebookUid, includeStructure, includeComment, sections); 


		} catch (FatalException e) {

			log.error("EXCEPTION: Wasn't able to export gradebook: " + gradebookUid, e);
			// 500 Internal Server Error
			response.setStatus(500);
		}

		return null;
	}
}
