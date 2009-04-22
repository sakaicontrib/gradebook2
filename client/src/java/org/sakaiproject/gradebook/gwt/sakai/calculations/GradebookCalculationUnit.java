package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.sakai.calculations.Calculation.Mode;

public class GradebookCalculationUnit {

	
	
	
	
	public static void main(String[] args) {
		
		long start = System.nanoTime();
		CategoryCalculationUnit essayUnit = new CategoryCalculationUnit(new BigDecimal(".60"), Integer.valueOf(1));
		CategoryCalculationUnit hwUnit = new CategoryCalculationUnit(new BigDecimal(".40"), Integer.valueOf(0));
		
		List<GradeRecordCalculationUnit> essayUnits = new ArrayList<GradeRecordCalculationUnit>();
		essayUnits.add(new GradeRecordCalculationUnit(new BigDecimal("4.0"), 
				new BigDecimal("5.0"), new BigDecimal(".20")));
		essayUnits.add(new GradeRecordCalculationUnit(null, 
				new BigDecimal("9.0"), new BigDecimal(".20")));
		essayUnits.add(new GradeRecordCalculationUnit(new BigDecimal("6.0"), 
				new BigDecimal("10.0"), new BigDecimal(".10")));
		essayUnits.add(new GradeRecordCalculationUnit(new BigDecimal("8.0"), 
				new BigDecimal("10.0"), new BigDecimal(".10")));
		essayUnits.add(new GradeRecordCalculationUnit(new BigDecimal("19.0"), 
				new BigDecimal("20.0"), new BigDecimal(".40")));
		
		PrintWriter writer = null; //new PrintWriter(System.out);
		
		Mode mode = Mode.DO;
		
		
		BigDecimal essaysGrade = essayUnit.calculateGrade(essayUnits, mode, writer);
		
		if (writer != null) writer.flush();
		
		List<GradeRecordCalculationUnit> hwUnits = new ArrayList<GradeRecordCalculationUnit>();
		hwUnits.add(new GradeRecordCalculationUnit(new BigDecimal("10.0"), 
				new BigDecimal("10.0"), new BigDecimal(".30")));
		hwUnits.add(new GradeRecordCalculationUnit(new BigDecimal("10.0"), 
				new BigDecimal("10.0"), new BigDecimal(".30")));
		hwUnits.add(new GradeRecordCalculationUnit(new BigDecimal("10.0"), 
				new BigDecimal("10.0"), new BigDecimal(".40")));
		
		BigDecimal hwGrade = hwUnit.calculateGrade(hwUnits, mode, writer);
		
		long end = System.nanoTime();
		
		System.out.println("Elapsed: " + (end-start));
		
		if (writer != null) writer.flush();
		System.out.println("Essays: " + essaysGrade);
		System.out.println("Homework: " + hwGrade);
		
		BigDecimal total = essaysGrade.add(hwGrade);
		
		System.out.println("Total: " + total);
	
		if (writer != null) writer.close();
	}
	
}
