package org.sakaiproject.gradebook.gwt.sakai.mock;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.archive.impl.BasicArchiveService;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.time.api.Time;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.util.Xml;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ArchiveServiceMock extends BasicArchiveService {
	Log log = LogFactory.getLog(BasicArchiveService.class);
	
	private SiteService siteService = null;
	
	private TimeService timeService = null;
	
	public static String ANOTHER_SITE_CONTEXT = "ANOTHER_SITE_CONTEXT";
	

	

	public void init () {
		
		log.info("init(): storage path: " + m_storagePath);
	}
	
	public String archive(String siteId) {
		
		
		StringBuilder results = new StringBuilder();
		
		Site theSite = null;
		try
		{
			theSite = siteService.getSite(siteId);
		}
		catch (IdUnusedException e)
		{
			results.append("Site: " + siteId + " not found.\n");
			log.warn("archive(): site not found: " + siteId);
			return results.toString();
		}

		// collect all the attachments we need
		List attachments = m_entityManager.newReferenceList();

		Time now = timeService.newTime();

		// this is the folder we are writing files to
		String storagePath = m_storagePath + siteId + "-archive/";

		// create the directory for the archive
		File dir = new File(m_storagePath + siteId + "-archive/");
		boolean isCreated = dir.mkdirs();

		if(!isCreated) {
			log.error("Was not able to create directories = " + dir.getName());
		}
		// for each registered ResourceService, give it a chance to archve
		List services = m_entityManager.getEntityProducers();
		for (Iterator iServices = services.iterator(); iServices.hasNext();)
		{
			EntityProducer service = (EntityProducer) iServices.next();
			if (service == null) continue;
			if (!service.willArchiveMerge()) continue;

			Document doc = Xml.createDocument();
			Stack stack = new Stack();
			Element root = doc.createElement("archive");
			doc.appendChild(root);
			root.setAttribute("source", siteId);
			root.setAttribute("server", "No Server: GWT Devel Mode");
			root.setAttribute("date", now.toString());
			root.setAttribute("system", FROM_SAKAI);
			
			stack.push(root);

			try
			{
				String msg = service.archive(siteId, doc, stack, storagePath, attachments);
				results.append(msg);
			}
			catch (Throwable t)
			{
				results.append(t.toString() + "\n");
			}

			stack.pop();
			
			String fileName = storagePath + service.getLabel() + ".xml";
			Xml.writeDocument(doc, fileName);
		}

		// archive the collected attachments
		if (attachments.size() > 0)
		{
			log.error("There shouldn't be any attachments here");
		}

		// *** Site

		
		Document doc = Xml.createDocument();
		Stack stack = new Stack();
		Element root = doc.createElement("archive");
		doc.appendChild(root);
		root.setAttribute("site", siteId);
		root.setAttribute("date", now.toString());
		root.setAttribute("system", FROM_SAKAI);
		
		stack.push(root);

		String msg = archiveSite(theSite, doc, stack);
		results.append(msg);
		
		stack.pop();
		Xml.writeDocument(doc, m_storagePath + siteId + "-archive/site.xml");


		// *** Users
		doc = Xml.createDocument();
		stack = new Stack();
		root = doc.createElement("archive");
		doc.appendChild(root);
		root.setAttribute("site", siteId);
		root.setAttribute("date", now.toString());
		root.setAttribute("system", FROM_SAKAI);
		
		stack.push(root);
		
		msg = archiveUsers(theSite, doc, stack);
		results.append(msg);

		stack.pop();
		Xml.writeDocument(doc, m_storagePath + siteId + "-archive/user.xml");


		return results.toString();
		
		
	}
	
	//service setters and getters
	
	public SiteService getSiteService() {
		return siteService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
	
	public TimeService getTimeService() {
		return timeService;
	}

	public void setTimeService(TimeService timeService) {
		this.timeService = timeService;
	}
	

}
