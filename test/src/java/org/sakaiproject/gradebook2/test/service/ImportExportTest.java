/**********************************************************************************
 *
 * $Id:$
 *
 ***********************************************************************************
 *
 * Copyright (c) 2008, 2009 The Regents of the University of California
 *
 * Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * 
 * http://www.osedu.org/licenses/ECL-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.gradebook2.test.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.gxt.upload.ClientUploadUtility;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportFile;
import org.sakaiproject.gradebook.gwt.client.gxt.upload.ImportHeader;
import org.sakaiproject.gradebook.gwt.client.model.ApplicationModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel;
import org.sakaiproject.gradebook.gwt.client.model.SpreadsheetModel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.CategoryType;
import org.sakaiproject.gradebook.gwt.client.model.GradebookModel.GradeType;
import org.sakaiproject.gradebook.gwt.client.model.ItemModel.Type;
import org.sakaiproject.gradebook.gwt.server.ImportExportUtility;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class ImportExportTest extends AbstractServiceTest {


	public ImportExportTest(String name) {
		super(name);
	}


	public void testImportExport() throws Exception {

		onSetup(GradeType.PERCENTAGES, CategoryType.WEIGHTED_CATEGORIES);

		ApplicationModel applicationModel = service.getApplicationModel(getName());

		List<GradebookModel> gbModels = applicationModel.getGradebookModels();

		GradebookModel model = gbModels.get(0);
		verifyCategoryPercentsForItems(model, Double.valueOf(25d), Double.valueOf(25d), Double.valueOf(25d), Double.valueOf(25d), Double.valueOf(20d));

		// Export to a temp file
		File tempFile = File.createTempFile("imp", ".csv");
		PrintWriter writer = new PrintWriter(new FileOutputStream(tempFile));
		ImportExportUtility.exportGradebook(service, getName(), true, false, writer, null, "csv");
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
				line = "\"\",\"% Category:\",\"50.0%\",\"10.0%\",\"10.0%\",\"30.0%\",\"20.0%\"";
			}

			modifiedWriter.println(line);
		}
		modifiedWriter.close();
		originalReader.close();

		String newGradebookUid = "TEST-IMPORT";

		// Import the temp file back into the gradebook
		FileReader reader = new FileReader(modifiedTempFile);
		ImportFile importFile = ImportExportUtility.parseImportCSV(service, newGradebookUid, reader);


		List<ColumnConfig> previewColumns = new ArrayList<ColumnConfig>();
		List<StudentModel> students = new ArrayList<StudentModel>();

		for (ImportHeader header : importFile.getItems()) {
			if (header != null)
				previewColumns.add(new ColumnConfig(header.getId(), header.getValue(), 100));
		}

		ArrayList<ItemModel> items = ClientUploadUtility.convertHeadersToItemModels(importFile.getItems());

		SpreadsheetModel spreadsheetModel = ClientUploadUtility.composeSpreadsheetModel(items, students, previewColumns);
		service.createOrUpdateSpreadsheet(newGradebookUid, spreadsheetModel);

		applicationModel = service.getApplicationModel(newGradebookUid);

		gbModels = applicationModel.getGradebookModels();

		model = gbModels.get(0);

		ItemModel newGradebookItemModel = model.getGradebookItemModel();

		assertNotNull(newGradebookItemModel);
		assertEquals("My Test Gradebook", newGradebookItemModel.getName());
		assertEquals(GradeType.PERCENTAGES, newGradebookItemModel.getGradeType());
		assertEquals(CategoryType.WEIGHTED_CATEGORIES, newGradebookItemModel.getCategoryType());
		assertEquals(1, newGradebookItemModel.getChildCount());
		assertEquals(Double.valueOf(60d), newGradebookItemModel.getPercentCourseGrade());
		verifyCategoryPercentsForItems(model, Double.valueOf(50d), Double.valueOf(10d), Double.valueOf(10d), Double.valueOf(30d), Double.valueOf(20d));

	}

	public void testImportExportEmptyCategories() throws Exception {

		onSetupEmptyCategories(GradeType.POINTS, CategoryType.WEIGHTED_CATEGORIES);

		ApplicationModel applicationModel = service.getApplicationModel(getName());

		List<GradebookModel> gbModels = applicationModel.getGradebookModels();

		GradebookModel model = gbModels.get(0);
		verifyCategoryAttributesForItemsEmptyCategories(model, Double.valueOf(25d), Double.valueOf(25d), Double.valueOf(25d), Double.valueOf(25d), Double.valueOf(20d));

		ItemModel gradebookItemModel = model.getGradebookItemModel();

		ItemModel firstItem = null;
		for (ModelData m : gradebookItemModel.getChildren()) {
			ItemModel categoryModel = (ItemModel)m;
			if (categoryModel.getChildCount() > 0) {
				for (ModelData itemModel : categoryModel.getChildren()) {
					firstItem = (ItemModel) itemModel;
					break;
				}

			}
		}

		PagingLoadResult<StudentModel> result = service.getStudentRows(model.getGradebookUid(), model.getGradebookId(), null, Boolean.FALSE);

		StudentModel firstStudent = result.getData().get(0);
		service.scoreNumericItem(model.getGradebookUid(), firstStudent, firstItem.getIdentifier(), Double.valueOf(200), null);


		// Export to a temp file
		File tempFile = File.createTempFile("imp", ".csv");
		PrintWriter writer = new PrintWriter(new FileOutputStream(tempFile));
		ImportExportUtility.exportGradebook(service, getName(), true, false, writer, null, "csv");
		writer.close();

		System.out.println("Writing to temp file " + tempFile.getName());

		// Update the category weightings
		File modifiedTempFile = File.createTempFile("mod", ".csv");

		BufferedReader originalReader = new BufferedReader(new FileReader(tempFile));
		PrintWriter modifiedWriter = new PrintWriter(new FileOutputStream(modifiedTempFile));
		while (originalReader.ready()) {
			String line = originalReader.readLine();

			if (line.startsWith("\"\",\"Gradebook:\"")) {
				line = "\"\",\"Gradebook:\",\"My Test Gradebook\",\"Weighted Categories\",\"Percentages\"";
			}

			if (line.startsWith("\"\",\"Equal Weight Items:\"")) {
				line = "\"\",\"Equal Weight Items:\",\"false\",\"false\",\"false\",\"false\",\"false\",\"\",\"\",\"\"";
			}

			if (line.startsWith("\"\",\"% Category:\"")) {
				line = "\"\",\"% Category:\",\"0.0%\",\"0.0%\",\"0.0%\",\"0.0%\",\"50.0%\",\"10.0%\",\"10.0%\",\"30.0%\",\"20.0%\"";
			}

			modifiedWriter.println(line);
		}
		modifiedWriter.close();
		originalReader.close();

		String newGradebookUid = "TEST-IMPORT";

		// Import the temp file back into the gradebook
		FileReader reader = new FileReader(modifiedTempFile);
		ImportFile importFile = ImportExportUtility.parseImportCSV(service, newGradebookUid, reader);


		List<ColumnConfig> previewColumns = new ArrayList<ColumnConfig>();
		List<StudentModel> students = new ArrayList<StudentModel>();

		for (ImportHeader header : importFile.getItems()) {
			if (header != null)
				previewColumns.add(new ColumnConfig(header.getId(), header.getValue(), 100));
		}

		ArrayList<ItemModel> items = ClientUploadUtility.convertHeadersToItemModels(importFile.getItems());

		SpreadsheetModel spreadsheetModel = ClientUploadUtility.composeSpreadsheetModel(items, students, previewColumns);
		service.createOrUpdateSpreadsheet(newGradebookUid, spreadsheetModel);

		applicationModel = service.getApplicationModel(newGradebookUid);

		gbModels = applicationModel.getGradebookModels();

		model = gbModels.get(0);

		ItemModel newGradebookItemModel = model.getGradebookItemModel();

		assertNotNull(newGradebookItemModel);
		assertEquals("My Test Gradebook", newGradebookItemModel.getName());
		assertEquals(GradeType.PERCENTAGES, newGradebookItemModel.getGradeType());
		assertEquals(CategoryType.WEIGHTED_CATEGORIES, newGradebookItemModel.getCategoryType());
		assertEquals(5, newGradebookItemModel.getChildCount());
		assertEquals(Double.valueOf(60d), newGradebookItemModel.getPercentCourseGrade());
		verifyCategoryAttributesForItemsEmptyCategories(model, Double.valueOf(50d), Double.valueOf(10d), Double.valueOf(10d), Double.valueOf(30d), Double.valueOf(20d));

	}


	protected void onSetupEmptyCategories(GradeType gradeType, CategoryType categoryType) throws Exception {

		gbModel = getGradebookModel(getName());

		ItemModel gradebookItemModel = gbModel.getGradebookItemModel();
		gradebookItemModel.setName("My Test Gradebook");
		gradebookItemModel.setGradeType(gradeType);
		gradebookItemModel.setCategoryType(categoryType);
		service.updateItemModel(gradebookItemModel);

		String gradebookUid = gbModel.getGradebookUid();
		Long gradebookId = gbModel.getGradebookId();

		ItemModel emptyCategory1 = new ItemModel();
		emptyCategory1.setName("Empty 1");
		emptyCategory1.setPercentCourseGrade(Double.valueOf(0d));
		emptyCategory1.setDropLowest(Integer.valueOf(0));
		emptyCategory1.setEqualWeightAssignments(Boolean.TRUE);
		emptyCategory1.setItemType(Type.CATEGORY);
		emptyCategory1.setIncluded(Boolean.FALSE);
		emptyCategory1 = getActiveItem(service.addItemCategory(gradebookUid, gradebookId,
				emptyCategory1));

		ItemModel emptyCategory2 = new ItemModel();
		emptyCategory2.setName("Empty 2");
		emptyCategory2.setPercentCourseGrade(Double.valueOf(0d));
		emptyCategory2.setDropLowest(Integer.valueOf(0));
		emptyCategory2.setEqualWeightAssignments(Boolean.TRUE);
		emptyCategory2.setItemType(Type.CATEGORY);
		emptyCategory2.setIncluded(Boolean.FALSE);
		emptyCategory2 = getActiveItem(service.addItemCategory(gradebookUid, gradebookId,
				emptyCategory2));

		ItemModel emptyCategory3 = new ItemModel();
		emptyCategory3.setName("Empty 3");
		emptyCategory3.setPercentCourseGrade(Double.valueOf(0d));
		emptyCategory3.setDropLowest(Integer.valueOf(0));
		emptyCategory3.setEqualWeightAssignments(Boolean.TRUE);
		emptyCategory3.setItemType(Type.CATEGORY);
		emptyCategory3.setIncluded(Boolean.FALSE);
		emptyCategory3 = getActiveItem(service.addItemCategory(gradebookUid, gradebookId,
				emptyCategory3));

		ItemModel emptyCategory4 = new ItemModel();
		emptyCategory4.setName("Empty 4");
		emptyCategory4.setPercentCourseGrade(Double.valueOf(0d));
		emptyCategory4.setDropLowest(Integer.valueOf(0));
		emptyCategory4.setEqualWeightAssignments(Boolean.TRUE);
		emptyCategory4.setItemType(Type.CATEGORY);
		emptyCategory4.setIncluded(Boolean.FALSE);
		emptyCategory4 = getActiveItem(service.addItemCategory(gradebookUid, gradebookId,
				emptyCategory4));

		ItemModel essaysCategory = new ItemModel();
		essaysCategory.setName("My Essays");
		essaysCategory.setPercentCourseGrade(Double.valueOf(60d));
		essaysCategory.setDropLowest(Integer.valueOf(0));
		essaysCategory.setEqualWeightAssignments(Boolean.TRUE);
		essaysCategory.setItemType(Type.CATEGORY);
		essaysCategory.setIncluded(Boolean.TRUE);
		essaysCategory = getActiveItem(service.addItemCategory(gradebookUid, gradebookId,
				essaysCategory));

		ItemModel essay1 = new ItemModel();
		essay1.setName("Essay 1");
		essay1.setPoints(Double.valueOf(200d));
		essay1.setDueDate(new Date());
		essay1.setCategoryId(essaysCategory.getCategoryId());
		essay1.setReleased(Boolean.TRUE);
		essay1.setItemType(Type.ITEM);
		essay1.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay1, true);

		ItemModel essay2 = new ItemModel();
		essay2.setName("Essay 2");
		essay2.setPoints(Double.valueOf(20d));
		essay2.setDueDate(new Date());
		essay2.setCategoryId(essaysCategory.getCategoryId());
		essay2.setReleased(Boolean.TRUE);
		essay2.setItemType(Type.ITEM);
		essay2.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay2, true);

		ItemModel essay3 = new ItemModel();
		essay3.setName("Essay 3");
		essay3.setPoints(Double.valueOf(20d));
		essay3.setDueDate(new Date());
		essay3.setCategoryId(essaysCategory.getCategoryId());
		essay3.setReleased(Boolean.TRUE);
		essay3.setItemType(Type.ITEM);
		essay3.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay3, true);

		ItemModel essay4 = new ItemModel();
		essay4.setName("Essay 4");
		essay4.setPoints(Double.valueOf(20d));
		essay4.setDueDate(new Date());
		essay4.setCategoryId(essaysCategory.getCategoryId());
		essay4.setReleased(Boolean.TRUE);
		essay4.setItemType(Type.ITEM);
		essay4.setIncluded(Boolean.TRUE);
		service.createItem(gradebookUid, gradebookId, essay4, true);

		ItemModel ec1 = new ItemModel();
		ec1.setName("Extra Credit");
		ec1.setPoints(Double.valueOf(20d));
		ec1.setDueDate(new Date());
		ec1.setCategoryId(essaysCategory.getCategoryId());
		ec1.setReleased(Boolean.TRUE);
		ec1.setItemType(Type.ITEM);
		ec1.setIncluded(Boolean.FALSE);
		ec1.setExtraCredit(Boolean.TRUE);
		category = service.createItem(gradebookUid, gradebookId, ec1, true);

		for (ModelData m : category.getChildren()) {
			ItemModel child = (ItemModel)m;
			Double percentCategory = child.getPercentCategory();
			BigDecimal pC = BigDecimal.valueOf(percentCategory.doubleValue());

		}

	}

	private void verifyCategoryPercentsForItems(GradebookModel model, Double... values) {
		ItemModel gradebookItemModel = model.getGradebookItemModel();

		assertEquals(1, gradebookItemModel.getChildCount());

		ItemModel essaysItemModel = (ItemModel) gradebookItemModel.getChildren().get(0);

		assertEquals(5, essaysItemModel.getChildCount());

		List<ModelData> children = essaysItemModel.getChildren();
		ItemModel essay1 = (ItemModel) children.get(0);
		ItemModel essay2 = (ItemModel) children.get(1);
		ItemModel essay3 = (ItemModel) children.get(2);
		ItemModel essay4 = (ItemModel) children.get(3);
		ItemModel ec = (ItemModel) children.get(4);

		assertEquals(values[0], essay1.getPercentCategory());
		assertEquals(values[1], essay2.getPercentCategory());
		assertEquals(values[2], essay3.getPercentCategory());
		assertEquals(values[3], essay4.getPercentCategory());
		assertEquals(values[4], ec.getPercentCategory());

		assertTrue(essay1.getIncluded());
		assertTrue(essay2.getIncluded());
		assertTrue(essay3.getIncluded());
		assertTrue(essay4.getIncluded());
		assertFalse(ec.getIncluded());
	}

	private void verifyCategoryAttributesForItemsEmptyCategories(GradebookModel model, Double... values) {
		ItemModel gradebookItemModel = model.getGradebookItemModel();

		assertEquals(5, gradebookItemModel.getChildCount());

		ItemModel emptyItemModel = (ItemModel) gradebookItemModel.getChildren().get(0);
		assertEquals(0, emptyItemModel.getChildCount());

		ItemModel essaysItemModel = (ItemModel) gradebookItemModel.getChildren().get(4);

		assertEquals(5, essaysItemModel.getChildCount());

		List<ModelData> children = essaysItemModel.getChildren();
		ItemModel essay1 = (ItemModel) children.get(0);
		ItemModel essay2 = (ItemModel) children.get(1);
		ItemModel essay3 = (ItemModel) children.get(2);
		ItemModel essay4 = (ItemModel) children.get(3);
		ItemModel ec = (ItemModel) children.get(4);

		assertEquals(values[0], essay1.getPercentCategory());
		assertEquals(values[1], essay2.getPercentCategory());
		assertEquals(values[2], essay3.getPercentCategory());
		assertEquals(values[3], essay4.getPercentCategory());
		assertEquals(values[4], ec.getPercentCategory());

		assertEquals(Double.valueOf(200d), essay1.getPoints());
		assertEquals(Double.valueOf(20d), essay2.getPoints());
		assertEquals(Double.valueOf(20d), essay3.getPoints());
		assertEquals(Double.valueOf(20d), essay4.getPoints());
		assertEquals(Double.valueOf(20d), ec.getPoints());

		assertTrue(essay1.getIncluded());
		assertTrue(essay2.getIncluded());
		assertTrue(essay3.getIncluded());
		assertTrue(essay4.getIncluded());
		assertFalse(ec.getIncluded());
	}

}
