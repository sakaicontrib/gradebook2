package org.sakaiproject.gradebook.gwt.server;

import java.io.InputStreamReader;

import org.sakaiproject.gradebook.gwt.client.model.Upload;
import org.sakaiproject.gradebook.gwt.server.test.Gradebook2TestCase;
import org.springframework.core.io.Resource;


public class ImportTest extends Gradebook2TestCase {

	public ImportTest() {
		 
	}
	
	
	public void testImport() throws Exception
	{
		onSetup();
		Resource csvFile = applicationContext.getResource("classpath:test1.csv"); 
		InputStreamReader r = new InputStreamReader(csvFile.getInputStream());  
		NewImportExportUtility ieu =  new NewImportExportUtility();
		Upload u = ieu.parseImportCSV(getService(), "TESTSITECONTEXT", r);
		assertNotNull(u); 
		
	}
	
}
