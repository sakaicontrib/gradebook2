package org.sakaiproject.gradebook.gwt.server;

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.api.ImportSettings;

public class ImportExportDataFile {

	private String fileType; 
	private String messages; 
	private boolean errorsFound; 
	private boolean newAssignment; 
	private boolean scantronFile;
	private List<String[]> allRows; 
	private int curRow; 
	private boolean isJustStructure = false;
	private ImportSettings importSettings = null;
	
	public ImportExportDataFile()
	{
		this.errorsFound = false; 
		this.newAssignment = false; 
		this.scantronFile = false; 
		allRows = new ArrayList<String[]>(); 
	}

	public List<String[]> getAllRows() {
		return allRows;
	}

	public void setAllRows(List<String[]> allRows) {
		this.allRows = allRows;
	}

	public boolean isNewAssignment() {
		return newAssignment;
	}

	public void setNewAssignment(boolean newAssignment) {
		this.newAssignment = newAssignment;
	}

	public boolean isScantronFile() {
		return scantronFile;
	}

	public void setScantronFile(boolean scantronFile) {
		this.scantronFile = scantronFile;
	}


	public void goToRow(int row)
	{
		if (row > 0 && row < allRows.size())
		{
			curRow = row; 
		}
		else
		{
			curRow = 0; 
		}
	}

	public int getCurrentRowNumber()
	{
		return curRow; 
	}

	public void close() 
	{
		this.allRows = null; 
		this.curRow = -2; 
	}
	public void startReading() 
	{
		this.curRow = -1; 
	}

	public String[] readNext() 
	{
		if (curRow == -2)
		{
			return null; 
		}

		this.curRow++; 
		if (curRow >= allRows.size())
		{
			return null; 
		}
		else
		{
			return allRows.get(curRow); 
		}

	}

	public void addRow(String[] rowData)
	{
		if (allRows != null)
		{
			allRows.add(rowData);
		}
	}

	public String[] getRow(int idx)
	{
		if (allRows != null)
		{
			if (idx < allRows.size())
			{
				return allRows.get(idx);
			}
			else
			{
				return null; 
			}
		}
		else
		{
			return null; 
		}
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getMessages() {
		return messages;
	}

	public void setMessages(String messages) {
		this.messages = messages;
	}

	public boolean isErrorsFound() {
		return errorsFound;
	}

	public void setErrorsFound(boolean errorsFound) {
		this.errorsFound = errorsFound;
	}

	public boolean isJustStructure() {
		return isJustStructure;
	}

	public void setJustStructure(boolean isJustStructure) {
		this.isJustStructure = isJustStructure;
	}

	public void setImportSettings(ImportSettings importSettings) {
		this.importSettings = importSettings;
	}

	public ImportSettings getImportSettings() {
		return importSettings;
	}

}
