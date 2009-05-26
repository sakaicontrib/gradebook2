package org.sakaiproject.gradebook.gwt.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.gxt.upload.ClientUploadUtility;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportFile;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportHeader;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.SpreadsheetModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class Gradebook2ServiceImportTest extends Gradebook2ServiceTest {

	
	public Gradebook2ServiceImportTest(String name) {
		super(name);
	}

	public void testImportExport() throws Exception {
		
		ApplicationModel applicationModel = service.getApplicationModel();
		
		List<GradebookModel> gbModels = applicationModel.getGradebookModels();
		
		GradebookModel model = gbModels.get(0);
		verifyCategoryPercentsForItems(model, Double.valueOf(25d), Double.valueOf(25d), Double.valueOf(25d), Double.valueOf(25d));
		
		// Export to a temp file
		File tempFile = File.createTempFile("imp", ".csv");
		PrintWriter writer = new PrintWriter(new FileOutputStream(tempFile));
		ImportExportUtility.exportGradebook(service, GRADEBOOK_UID, true, writer, null);
		writer.close();
		
		// Update the category weightings
		File modifiedTempFile = File.createTempFile("mod", ".csv");
		
		BufferedReader originalReader = new BufferedReader(new FileReader(tempFile));
		PrintWriter modifiedWriter = new PrintWriter(new FileOutputStream(modifiedTempFile));
		while (originalReader.ready()) {
			String line = originalReader.readLine();
			
			if (line.startsWith("\"\",\"Equal Weight Items:\"")) {
				line = "\"\",\"Equal Weight Items:\",\"false\",\"\",\"\",\"\"";
			}
			
			if (line.startsWith("\"\",\"% Category:\"")) {
				line = "\"\",\"% Category:\",\"50.0%\",\"10.0%\",\"10.0%\",\"30.0%\"";
			}
			
			modifiedWriter.println(line);
		}
		modifiedWriter.close();
		originalReader.close();
		
		// Import the temp file back into the gradebook
		FileReader reader = new FileReader(modifiedTempFile);
		ImportFile importFile = ImportExportUtility.parseImport(service, GRADEBOOK_UID, reader);
	
		
		List<ColumnConfig> previewColumns = new ArrayList<ColumnConfig>();
		List<StudentModel> students = new ArrayList<StudentModel>();
		
		for (ImportHeader header : importFile.getItems()) {
			if (header != null)
				previewColumns.add(new ColumnConfig(header.getId(), header.getValue(), 100));
		}
		
		List<ItemModel> items = ClientUploadUtility.convertHeadersToItemModels(importFile.getItems());
		
		SpreadsheetModel spreadsheetModel = ClientUploadUtility.composeSpreadsheetModel(items, students, previewColumns);
		service.createOrUpdateSpreadsheet(GRADEBOOK_UID, spreadsheetModel);
		
		applicationModel = service.getApplicationModel();
		
		gbModels = applicationModel.getGradebookModels();
		
		model = gbModels.get(0);
		verifyCategoryPercentsForItems(model, Double.valueOf(50d), Double.valueOf(10d), Double.valueOf(10d), Double.valueOf(30d));
		
	}
	
	
	private void verifyCategoryPercentsForItems(GradebookModel model, Double... values) {
		ItemModel gradebookItemModel = model.getGradebookItemModel();
		
		assertEquals(1, gradebookItemModel.getChildCount());
		
		ItemModel essaysItemModel = gradebookItemModel.getChildren().get(0);
		
		assertEquals(4, essaysItemModel.getChildCount());
		
		List<ItemModel> children = essaysItemModel.getChildren();
		ItemModel essay1 = children.get(0);
		ItemModel essay2 = children.get(1);
		ItemModel essay3 = children.get(2);
		ItemModel essay4 = children.get(3);
		
		assertEquals(values[0], essay1.getPercentCategory());
		assertEquals(values[1], essay2.getPercentCategory());
		assertEquals(values[2], essay3.getPercentCategory());
		assertEquals(values[3], essay4.getPercentCategory());
	}
	
}
