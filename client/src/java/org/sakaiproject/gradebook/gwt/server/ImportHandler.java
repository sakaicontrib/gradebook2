package org.sakaiproject.gradebook.gwt.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.sakaiproject.gradebook.gwt.client.GradebookToolFacade;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportFile;
import org.sakaiproject.gradebook.gwt.sakai.ExportAdvisor;
import org.sakaiproject.gradebook.gwt.sakai.SampleExportAdvisor;
import org.sakaiproject.gradebook.gwt.sakai.mock.DelegateFacadeMockImpl;
import org.sakaiproject.gradebook.gwt.sakai.mock.IocMock;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility.Delimiter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

public class ImportHandler extends HttpServlet {

	private static final String CONTENT_TYPE = "text/html";
	private static final long serialVersionUID = 1L;

	private IocMock iocMock = IocMock.getInstance();
	
	private GradebookToolFacade delegateFacade;
	private ExportAdvisor exportAdvisor;
	
	public void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		if (delegateFacade == null)
			delegateFacade = (GradebookToolFacade)iocMock.getClassInstance(DelegateFacadeMockImpl.class.getName());
		
		if (exportAdvisor == null)
			exportAdvisor = new SampleExportAdvisor();
		
		response.setContentType("application/ms-excel");
		response.setHeader("Content-Disposition", "attachment; filename=" + "gradebook.csv");
		
		PrintWriter writer = response.getWriter();
		
		String gradebookUid = req.getParameter("gradebookUid");
		String include = req.getParameter("include");
		try {
			boolean doIncludeStructure = include != null;
			ImportExportUtility.exportGradebook(delegateFacade, gradebookUid, exportAdvisor, doIncludeStructure, writer);
		} catch (FatalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		if (delegateFacade == null)
			delegateFacade = (GradebookToolFacade)iocMock.getClassInstance(DelegateFacadeMockImpl.class.getName());
		
		if (exportAdvisor == null)
			exportAdvisor = new SampleExportAdvisor();
		
		XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
		
		response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
		
		String feedback = "nothing";
		//JSONObject feedback = new JSONObject();
        boolean isMultipart = ServletFileUpload.isMultipartContent(req);
        if (isMultipart) {
                final DiskFileItemFactory factory = new DiskFileItemFactory();
                //factory.setRepository(new File("/tmp/gwt"));
                factory.setSizeThreshold(1000000);
                final ServletFileUpload upload = new ServletFileUpload(factory);
                try {
                        final List<FileItem> items = (List<FileItem>) upload.parseRequest(req);
                       
                        EnumSet<Delimiter> delimiterSet = EnumSet.noneOf(Delimiter.class);
                        String gradebookUid = null;
                        FileItem uploadedFile = null;
                        for (int i = 0; i < items.size(); i++) {
                                FileItem current = items.get(i);
                                
                                if (current.isFormField()) {
                                	
                                	String name = current.getFieldName();
                                	if (name != null) { 
                                		if (name.equals("gradebookUid"))
                                			gradebookUid = current.getString();
                                		else if (name.equals("delimiter:comma")) 
                                			delimiterSet.add(Delimiter.COMMA);
                                		else if (name.equals("delimiter:tab")) 
                                			delimiterSet.add(Delimiter.TAB);
                                		else if (name.equals("delimiter:space"))
                                			delimiterSet.add(Delimiter.SPACE);
                                		else if (name.equals("delimiter:colon"))
                                			delimiterSet.add(Delimiter.COLON);
                                	}
                                } else
                                	uploadedFile = current;
                                
                       }
                       
                       InputStreamReader reader = new InputStreamReader(uploadedFile.getInputStream());
                       
                       ImportFile importFile = ImportExportUtility.parseImportX(delegateFacade, exportAdvisor, gradebookUid, reader, delimiterSet);
                        
                       out.write(xstream.toXML(importFile)); 
                       

                } catch (FileUploadException e) {
                        System.out.println(e.getMessage());
                } catch (FatalException fe) {
                		System.out.println(fe.getMessage());
                }

                
                
                
        } else {
        	System.out.println("Not multipart");
        }
	}
	

	public GradebookToolFacade getDelegateFacade() {
		return delegateFacade;
	}

	public void setDelegateFacade(GradebookToolFacade delegateFacade) {
		this.delegateFacade = delegateFacade;
	}

	public ExportAdvisor getExportAdvisor() {
		return exportAdvisor;
	}

	public void setExportAdvisor(ExportAdvisor exportAdvisor) {
		this.exportAdvisor = exportAdvisor;
	}

}
