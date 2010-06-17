package org.sakaiproject.gradebook.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.type.ItemType;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;
import org.sakaiproject.service.gradebook.shared.GradebookNotFoundException;
import org.sakaiproject.tool.gradebook.Assignment;
import org.sakaiproject.tool.gradebook.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Gradebook2EntityProducerTransferAgent implements EntityProducer,
		EntityTransferrer {
	
	private EntityManager entityManager;
	private Log log = LogFactory.getLog(Gradebook2EntityProducerTransferAgent.class);
	private String label;
	private String[] myToolIds;
	private Gradebook2ComponentService componentService;
	private GradebookToolService toolService;
	
	

	public Gradebook2ComponentService getComponentService() {
		return componentService;
	}

	public void setComponentService(Gradebook2ComponentService componentService) {
		this.componentService = componentService;
	}

	public GradebookToolService getToolService() {
		return toolService;
	}

	public void setToolService(GradebookToolService toolService) {
		this.toolService = toolService;
	}

	public void init() {
		entityManager.registerEntityProducer(this, getLabel());
		
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public String archive(String siteId, Document doc, Stack stack,
			String archivePath, List attachments) {
		log.info("-------gradebook2 -------- archive('"
				+ StringUtils.join(new Object[] { siteId, doc, stack,
						archivePath, attachments }, "','") + "')");

		// stealing many ideas from BaseContentService

		// prepare the buffer for the results log
		StringBuilder results = new StringBuilder();

		// start with an element with our very own name
		Element element = doc.createElement(Gradebook2ComponentService.class.getName());
		((Element) stack.peek()).appendChild(element);
		stack.push(element);
		
		String msg = null;
		try {
			// TODO: the one gradebook for the site, someday there will be more possible
			Gradebook gradebook = componentService.getGradebook(siteId);
			
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			String asXML = gradebook.toXml();
			
			log.info(asXML);
			
			InputStream is = new ByteArrayInputStream(asXML.getBytes());
			Document gbDoc = docBuilder.parse(is);
			
			Node newNode = doc.adoptNode(gbDoc.getFirstChild());
			doc.getFirstChild().insertBefore(newNode, null);
			
			results.append("Gradebook2 archive of gradebook complete: "+ gradebook.getGradebookUid() + "\n");
			
			

			
		
		} catch (GradebookNotFoundException noGB) {
			msg = "Error archiving gradebook2 from site (no gradebook found): " + siteId + " " + noGB.toString() + "\n";
			log.error(noGB);
			noGB.printStackTrace();
			results.append(msg);
		} catch (IOException io ){
			msg = "IO Error archiving gradebook2 from site: " + siteId + " " + io.toString() + "\n";
			log.error(msg);
			io.printStackTrace();
			results.append(msg);
		} catch (SAXException sax) {
			msg = "XML Parsing Error archiving gradebook2 from site: " + siteId + " " + sax.toString() + "\n";
			log.error(msg);
			sax.printStackTrace();
			results.append(msg);
		} catch (Exception any) {
			msg = "General Error archiving gradebook2 from site: " + siteId + " " + any.toString() + "\n";
			log.error(msg);
			any.printStackTrace();
			results.append(msg);
		} 

		stack.pop();

		return results.toString();
	}

	public Entity getEntity(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection getEntityAuthzGroups(Reference arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityDescription(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResourceProperties getEntityResourceProperties(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getEntityUrl(Reference arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpAccess getHttpAccess() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel() {
		
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
		log.info("setting entityproducer label: " + label);
	}

	public String merge(String arg0, Element arg1, String arg2, String arg3,
			Map arg4, Map arg5, Set arg6) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean parseEntityReference(String arg0, Reference arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean willArchiveMerge() {
		return true;
	}

	public String[] myToolIds() {
		return myToolIds;
	}

	public String[] getMyToolIds() {
		return myToolIds;
	}

	public void setMyToolIds(String[] myToolIds) {
		this.myToolIds = myToolIds;
	}

	public void transferCopyEntities(String from, String to, List ids) {
		/*
		 *  TODO: we don't have unique item id's and so can't filter with the incoming param 'ids'
		 *  This could be a list of item *names* perhaps?
		 */


		Gradebook fromDB = componentService.getGradebook(from);
		
		

		Item im = fromDB.getGradebookItemModel();
		
		try {
			
			componentService.saveAllGradebookItems((GradeItem) im, to);
			
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FatalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void transferCopyEntities(String from, String to, List ids,
			boolean cleanup) {
		
		transferCopyEntities(from, to, ids);
		
		Gradebook fromDB = componentService.getGradebook(from);
		
		Item im = fromDB.getGradebookItemModel();
		
		for (GradeItem level1 : ((GradeItem) im).getChildren()) {
			for (GradeItem level2 : level1.getChildren()) {
				level2.setRemoved(true);
				updateGradeItem(level2);
			}
			level1.setRemoved(true);
			updateGradeItem(level1);
		}
		
		
	}

	private void updateGradeItem(GradeItem item) {
		if (item.getItemType() == ItemType.CATEGORY) {
			toolService.updateCategory((Category)item);
		} else if (item.getItemType() == ItemType.ITEM) {
			toolService.updateAssignment((Assignment)item);
		}
		
		
	}
	


}
