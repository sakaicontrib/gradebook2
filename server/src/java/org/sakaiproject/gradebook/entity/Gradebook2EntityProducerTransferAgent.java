package org.sakaiproject.gradebook.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.entity.api.EntityProducer;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.gradebook.gwt.client.BusinessLogicCode;
import org.sakaiproject.gradebook.gwt.client.api.ImportSettings;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.gxt.type.FileFormat;
import org.sakaiproject.gradebook.gwt.client.model.Gradebook;
import org.sakaiproject.gradebook.gwt.client.model.Item;
import org.sakaiproject.gradebook.gwt.client.model.Upload;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;
import org.sakaiproject.gradebook.gwt.sakai.model.GradeItem;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility;
import org.sakaiproject.gradebook.gwt.server.ImportSettingsImpl;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility.FileType;
import org.sakaiproject.service.gradebook.shared.GradebookFrameworkService;
import org.sakaiproject.service.gradebook.shared.GradebookNotFoundException;
import org.sakaiproject.util.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class Gradebook2EntityProducerTransferAgent implements EntityProducer,
		EntityTransferrer {
	
	private EntityManager entityManager = null;
	private Log log = LogFactory.getLog(Gradebook2EntityProducerTransferAgent.class);
	private String label = null;
	private String[] myToolIds = null;
	private Gradebook2ComponentService componentService;
	private GradebookToolService toolService;
	// Set via IoC
	private ResourceLoader i18n;
	private ImportExportUtility importExportUtil = null;
	private GradebookFrameworkService frameworkService = null;
	
	
	

	public void setFrameworkService(GradebookFrameworkService frameworkService) {
		this.frameworkService = frameworkService;
	}

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

	@SuppressWarnings("unchecked")
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
			msg = "Unable to archive gradebook2 for site " + siteId +" because no gradebook exists.\n";
			log.info(msg);
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

	@SuppressWarnings("unchecked")
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

	/* 
	 * 
	 * in practice, this is a misnamed method since archiveService currently
	 * uses this to determine if a class will do arching operations.. not simply merge.
	 * (non-Javadoc)
	 * @see org.sakaiproject.entity.api.EntityProducer#willArchiveMerge()
	 */
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
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		try {
			importExportUtil.exportGradebook (FileType.CSV, "", result, componentService, from, true, false, null);
		} catch (FatalException e1) {
			e1.printStackTrace();
		} 

		log.debug(result.toString());
		
		/*
		 *  this could be going to a new site as part of the site creation process
		 *  So, check for the target gb, if not there, create one.
		 */

		if (!frameworkService.isGradebookDefined(to)) {

			frameworkService.addGradebook(to, i18n
					.getString("defaultGradebookName"));
		}
		
		ImportSettings settings= new ImportSettingsImpl();
		settings.setJustStructure(true);
		settings.setGradebookUid(to);
		settings.setFileFormatName(FileFormat.FULL.name());

		Upload importFile = null;
		try {
			importFile = importExportUtil.parseImportCSV(new InputStreamReader(new ByteArrayInputStream(result.toByteArray())), settings);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		} catch (FatalException e) {
			e.printStackTrace();
		}
		
		org.sakaiproject.gradebook.gwt.sakai.rest.resource.Upload uploadREST = new org.sakaiproject.gradebook.gwt.sakai.rest.resource.Upload();
		
		try {
			uploadREST.update(to, componentService.getGradebook(to).getGradebookId(), toJson(importFile), "true", "", componentService);
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
		
		return;

	}

	public void setImportExportUtil(ImportExportUtility importExportUtil) {
		this.importExportUtil = importExportUtil;
	}

	public void transferCopyEntities(String from, String to, List ids,
			boolean cleanup) {
		
		Gradebook toGB = componentService.getGradebook(to);
		
		Item im = toGB.getGradebookItemModel();
		
		for (GradeItem level1 : ((GradeItem) im).getChildren()) {
			for (GradeItem level2 : level1.getChildren()) {
				if (!level2.getRemoved()) {
					level2.setRemoved(true);
				}
				
			}
			if (!level1.getRemoved()) {
				level1.setRemoved(true);
			}
			
		}
		
		List<BusinessLogicCode> ignore = im.getIgnoredBusinessRules();
		ignore.add(BusinessLogicCode.CannotIncludeDeletedItemRule);
		ignore.add(BusinessLogicCode.CannotIncludeItemFromUnincludedCategoryRule);
		
		try {
			componentService.saveFullGradebookFromClientModel(toGB);
		} catch (FatalException e) {
			log.error("transferCopyEntities(migrate) - from: " + from + " - to: " + to);
			e.printStackTrace();
		} catch (InvalidInputException e) {
			log.error("transferCopyEntities(migrate) - from: " + from + " - to: " + to);
			e.printStackTrace();
		}
		transferCopyEntities(from, to, ids);
		
		
	}
	
	
	protected String toJson(Object o)
	{
		return toJson(o, false); 
	}
	protected String toJson(Object o, boolean prettyPrint) {
		ObjectMapper mapper = new ObjectMapper();
		if (prettyPrint)
		{
			mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true); 
		}
		StringWriter w = new StringWriter();
		try {
			mapper.writeValue(w, o);
		} catch (Exception e) {
			log.error("Caught an exception serializing to JSON: ", e);
		}
		
		return w.toString();
	}

	public void setI18n(ResourceLoader i18n) {
		this.i18n = i18n;
	}
	

}
