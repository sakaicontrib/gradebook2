package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.server.BrowserDetect;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtilityImpl;
import org.sakaiproject.gradebook.gwt.server.UserAgent;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility.FileType;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;

@Path("export")
public class Export extends Resource {

	private static Log log = LogFactory.getLog(Export.class);
	
	private ImportExportUtility importExportUtility = null;
	
	private SiteService siteService = null;
	
	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public void setImportExportUtility(ImportExportUtility importExportUtility) {
		this.importExportUtility = importExportUtility;
	}

	@SuppressWarnings("unchecked")
	@GET @Path("{uid}{structure:(/structure/[^/]+?)?}{filetype:(/filetype/[^/]+?)?}{section:(/section/[^/]+?)?}")
	@Produces("application/json")
	public String export(@PathParam("uid") String gradebookUid, 
			@PathParam("structure") String includeStructureFlag, @PathParam("filetype") String format,
			@PathParam("section") String section,
			@Context HttpServletResponse response, @Context HttpServletRequest request) throws IOException {
		
		boolean includeStructure = !"".equals(includeStructureFlag)
					&& includeStructureFlag.indexOf("/") > -1
					&& "true".equals(includeStructureFlag.split("/")[2].toLowerCase());
		
		String fileType = "".equals(format) || format.indexOf("/") == -1 ? 
				ImportExportUtilityImpl.FileType.CSV.getName() : 
					format.split("/")[2].toLowerCase();
		
		String sectionIndexes = "".equals(section) || section.indexOf("/") == -1 ? 
				null : 
					section.substring("/section/".length());
			
		
		List<String> sectionList = new ArrayList<String>();
		
		boolean error = false;
		

		if (sectionIndexes != null) {
			// need to decode the indexed values: get groups sort and extract from given index locations
			String[] sectionNumbers = sectionIndexes.split(",");
			
			Site targetSite = null;
			try {
				targetSite = siteService.getSite(gradebookUid); // all the usual caveats about this assumption apply
				
				if (null == targetSite) {
					response.setStatus(400);
					error = true;
				} else {
					Collection groups = targetSite.getGroups();
					if (null != groups && groups.size() != 0
							&& (groups.size() >= sectionNumbers.length )) {
						List<String> ids = new ArrayList<String>(groups.size());
						Object o = null;
						for (Iterator i=groups.iterator();i.hasNext();) {
							o = i.next();
							if (o != null) {
								ids.add(((Entity)o).getId());
							}
						}
						Collections.sort(ids);
						
						for (int i=0;i<sectionNumbers.length;++i) {
							String fullRef = "/site/" + gradebookUid + "/group/" + ids.get(i);
							sectionList.add(fullRef);
						}
					} else {error = true;}
				}
				
				
			} catch (IdUnusedException e) {
				response.setStatus(400);
				error = true;
			}
			
			
		}

		if(!error) {
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
				
					importExportUtility.exportGradebook (type, filename.toString(), out, service, gradebookUid, includeStructure, true, sectionList); 
							
				
			} catch (FatalException e) {
				log.error("EXCEPTION: Wasn't able to export gradebook: " + gradebookUid, e);
				// 500 Internal Server Error
				response.setStatus(500);
			}
		}
		return null;
	}
}
