package org.sakaiproject.gradebook.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.Upload;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;

public interface ImportExportUtility {
	
	public Upload parseImportXLS(Gradebook2ComponentService service,
			String gradebookUid,
			InputStream is,
			String fileName,
			GradebookToolService gbToolService, 
			boolean doPreventOverwrite)
	throws InvalidInputException, FatalException, IOException;
	
	public Upload parseImportCSV(Gradebook2ComponentService service,
			String gradebookUid,
			Reader reader)
	throws InvalidInputException, FatalException;

}
