package org.sakaiproject.gradebook.gwt.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.Upload;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.sakaiproject.gradebook.gwt.sakai.GradebookToolService;

public interface ImportExportUtility {
	
	/*
	 * public enums
	 * 
	 */
	public static enum Delimiter {
		TAB, COMMA, SPACE, COLON
	};
	
	public static enum OptionState { NULL, TRUE, FALSE}; 

	
	public static enum FileType {
		CSV("csv", ".csv", "application/ms-excel"), 
		XLS97("xls97", ".xls", "application/ms-excel"),
		XLSX("xlsx", ".xlsx", "application/ms-excel");
		private String ext = "";
		private String mimeType = "";
		private String name = "";
		
		FileType(String name, String extension, String mimeType) {
			this.name  = name;
			this.ext = extension;
			this.mimeType = mimeType;
		}
		
		public String getExtension() {
			return ext;
		}
		
		public String getMimeType() {
			return mimeType;
		}
		
		public String getName() {
			return name;
		}
		
		public static FileType getTypeFromExtension(String extension) {
			FileType rv = CSV;
			if( extension != null) {
				for (FileType f : values()) {
					if (f.getExtension().equals(extension)) {
						rv = f;
						break;
					}
				}
			}
			return rv;
		}

		public static FileType getType(String fileType) {
			FileType rv = CSV;
			if( fileType != null) {
				for (FileType f : values()) {
					if (f.getName().equals(fileType)) {
						rv = f;
						break;
					}
				}
			}
			return rv;
		}
		
		public boolean isExcelNative() {
			return this.equals(XLS97) || this.equals(XLSX);
		}
	}

	/*
	 * methods
	 */
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

	public ImportExportDataFile exportGradebook(Gradebook2ComponentService service, String gradebookUid, 
			final boolean includeStructure, final boolean includeComments, List<String> sectionUidList) 
	throws FatalException;
	
	public void exportGradebook(FileType fileType, String filename, OutputStream outStream,
			Gradebook2ComponentService service, String gradebookUid,
			final boolean includeStructure, final boolean includeComments, List<String> SectionUid) throws FatalException;
			
}
