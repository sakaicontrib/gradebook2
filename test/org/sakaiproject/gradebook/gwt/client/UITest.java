package org.sakaiproject.gradebook.gwt.client;

import org.sakaiproject.gradebook.gwt.client.custom.widget.grid.CustomColumnModel;
import org.sakaiproject.gradebook.gwt.client.gxt.GradebookContainer;
import org.sakaiproject.gradebook.gwt.client.gxt.multigrade.MultiGradeContentPanel;
import org.sakaiproject.gradebook.gwt.client.model.StudentModel;

import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class UITest extends GWTTestCase {

  /**
   * Must refer to a valid module that sources this class.
   */
  public String getModuleName() {
    return "org.sakaiproject.gradebook.gwt.GradebookApplication";
  }

  /**
   * Add as many tests as you like.
   */
  public void testSimple() {
	  final GradebookApplication module = new GradebookApplication();
	  
	  module.onModuleLoad();
	  
	  delayTestFinish(10000);
	  
	  GradebookContainerCallback callback = new GradebookContainerCallback() {
		  public void doAction(GradebookContainer gradebookContainer) {
			  MultiGradeContentPanel multigrade = gradebookContainer.getInstructorViewContainer().getMultigrade();
			  //changeMultiGradeCellValue(multigrade, "Essay 1", 0, Double.valueOf(0), true, false);
			  changeMultiGradeCellValue(multigrade, "Essay 1", 0, Double.valueOf(50), true, false);
			  changeMultiGradeCellValue(multigrade, "Essay 1", 0, Double.valueOf(100), true, false);
			  changeMultiGradeCellValue(multigrade, "Essay 1", 0, Double.valueOf(200), false, true);
		  }
	  };
	  
	  doGradebookContainer(module, callback);
	  
	  
  }
  
  
  private void changeMultiGradeCellValue(MultiGradeContentPanel multigrade, final String header, final int row, 
		  final Double newValue, final boolean expectSuccess, final boolean doFinish) {
	  final EditorGrid<StudentModel> grid = multigrade.getGrid();
	  final ListStore<StudentModel> store = multigrade.getStore();
	  
	  CustomColumnModel cm = multigrade.getColumnModel();
	  
	  int numberOfColumns = cm.getColumnCount();
	  
	  //int row = 0;
	  
	  for (int c = 0;c < numberOfColumns;c++) {
		  final ColumnConfig column = cm.getColumn(c);

		  if (column.getHeader().equals(header)) {
			  
			  final StudentModel model = store.getAt(row);
			  
			  final String property = column.getDataIndex();
			  final Record record = store.getRecord(model);

			  final Double startValue = (Double)record.get(property);
			  
			  
			  GridEvent ge = new GridEvent(multigrade.getGrid());
			  ge.colIndex = c;
			  ge.rowIndex = row;
			  
			  //System.out.println("Course grade is " + record.get(StudentModel.Key.COURSE_GRADE.name()));
			  
			  multigrade.editCell(null, record, property, newValue, startValue, ge);
	
			  Callback c2 = new Callback() {
				  public void doAction() {
					  System.out.println("Replaced " + startValue + " with " + newValue);
					  
					  Record r = store.getRecord(model);
					  
					  Double actualValue = (Double)r.get(property);
					  
					  System.out.println("Actual value is " + actualValue);
					  
					  if (expectSuccess)
						  assertTrue(newValue.equals(actualValue));
					  else 
						  assertFalse(newValue.equals(actualValue));
					  
					  //System.out.println("Course grade is " + r.get(StudentModel.Key.COURSE_GRADE.name()));
					  
					  if (doFinish) {
						  
						  finishTest();
					  }
				  }
			  };
			  
			  DelayAction d2 = new DelayAction(c2);
			  d2.schedule(500);
			  
		  }
	  }
  }
  
  private void doGradebookContainer(final GradebookApplication module, final GradebookContainerCallback callback) {
	  Callback c0 = new Callback() {
		  
		  public void doAction() {
			  final GradebookContainer gradebookContainer = module.getGradebookContainer();
			  Callback c1 = new Callback() {
				  
				  public void doAction() {
					  assertTrue(gradebookContainer != null);
						
					  gradebookContainer.layout();
					  
					  callback.doAction(gradebookContainer);
				  }
			  };
			  DelayAction d1 = new DelayAction(c1);
			  d1.schedule(500);
		  }
	  };
	  
	  DelayAction d0 = new DelayAction(c0);
	  d0.schedule(2000);
  }

  
  public abstract class GradebookContainerCallback {
	  
	  public abstract void doAction(GradebookContainer gradebookContainer);
	  
  }

  public abstract class Callback {
	  
	  public abstract void doAction();
	  
  }
  
  public class DelayAction extends Timer {
	  
	  private Callback callback;
	  
	  public DelayAction(Callback callback) {
		  super();
		  this.callback = callback;
	  }
	  
	  public void run() {
		  callback.doAction();
	  }
	  
  }
  
  

}
