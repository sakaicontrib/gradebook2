package org.sakaiproject.gradebook.gwt.sakai.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.archive.api.ArchiveService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.gradebook.gwt.sakai.mock.ArchiveServiceMock;
import org.sakaiproject.gradebook.gwt.sakai.mock.BaseGroupMock;

@Path("archive")
public class EntityArchiveTester extends Resource {
	
	private ArchiveService archiveService = null;
	private EntityManager entityManager;



	
	Log log = LogFactory.getLog(EntityArchiveTester.class);



	

	@GET @Path("{siteId}")
	@Produces("text/plain")
	public String archive(@PathParam("siteId") String siteId) {
		if(log.isDebugEnabled()) {
			log.debug("called --> archive(" + siteId + ")");
		}
		
		if(null == archiveService) {
			log.error("ArchiveService was not injected!");
			return "ArchiveService was not injected!";
		}
		
		return archiveService.archive(siteId);
		

		
	}
	
	@GET @Path("transfer/{from}/{to}")
    @Produces("text/plain")
    public String transfer(@PathParam("from") String fromContext, @PathParam("to") String toContext) {

		log.info("Gradebook2 transfer(" + fromContext + "," + toContext + ")");
		
		
		if (null == entityManager) {
			log.error("EntityManager was not injected!");
			return "EntityManager was not injected!";
		}
		for (Object e : entityManager.getEntityProducers()) {
			if (e!=null && e instanceof EntityTransferrer) {
				((EntityTransferrer)e).transferCopyEntities(ArchiveServiceMock.ANOTHER_SITE_CONTEXT, BaseGroupMock.testSite_ContextId, null);
			}
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
	
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	
	
}