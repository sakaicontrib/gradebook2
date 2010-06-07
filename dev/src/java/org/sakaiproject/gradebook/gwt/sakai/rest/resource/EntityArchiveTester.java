package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.archive.api.ArchiveService;
import org.sakaiproject.entity.api.EntityManager;

@Path("archive")
public class EntityArchiveTester extends Resource {
	
	ArchiveService archiveService = null;



	Log log = LogFactory.getLog(EntityArchiveTester.class);

	@GET @Path("{siteId}")
	@Produces("text/plain")
	public String archive(@PathParam("siteId") String siteId) {
		if(log.isDebugEnabled()) {
			log.debug("called --> archive(" + siteId + ")");
		}
		
		if(null == archiveService) {
			log.error("EntityManager was not injected!");
			return "EntityManager was not injected!";
		}
		
		return archiveService.archive(siteId);
		

		
	}
	
	@GET @Path("transfer/{from}/{to}")
    @Produces("text/plain")
    public String transfer(@PathParam("from") String fromContext, @PathParam("to") String toContext) {
		if(log.isDebugEnabled()) {
			log.debug("called --> transfer(" + fromContext + "," + toContext + ")");
		}
		
		if(null == archiveService) {
			log.error("EntityManager was not injected!");
			return "EntityManager was not injected!";
		}
		
		
		return "done";
	}
	
	
	@GET @Path("merge/{from}/{to}")
    @Produces("text/plain")
    public String merge(@PathParam("from") String fromContext, @PathParam("to") String toContext) {
		if(log.isDebugEnabled()) {
			log.debug("called --> merge(" + fromContext + "," + toContext + ")");
		}
		
		if(null == archiveService) {
			log.error("EntityManager was not injected!");
			return "EntityManager was not injected!";
		}
		
		
		return "done";
	}
	
	
	
	// (s/g)etters
	public ArchiveService getArchiveService() {
		return archiveService;
	}

	public void setArchiveService(ArchiveService archiveService) {
		this.archiveService = archiveService;
	}
	
}
