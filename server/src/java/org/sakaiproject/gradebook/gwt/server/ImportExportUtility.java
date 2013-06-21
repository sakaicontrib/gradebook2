package org.sakaiproject.gradebook.gwt.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.List;

import org.sakaiproject.gradebook.gwt.client.AppConstants;
import org.sakaiproject.gradebook.gwt.client.api.ImportSettings;
import org.sakaiproject.gradebook.gwt.client.exceptions.FatalException;
import org.sakaiproject.gradebook.gwt.client.exceptions.InvalidInputException;
import org.sakaiproject.gradebook.gwt.client.model.Upload;
import org.sakaiproject.gradebook.gwt.sakai.Gradebook2ComponentService;
import org.springframework.web.multipart.MultipartFile;

public interface ImportExportUtility {
	
	public static String DETECTOR_OOXML_CONTAINER_MIMETYPE = "application/zip";
	public static String DETECTOR_CSV_MIMETYPE = "text/plain";
	public static String DETECTOR_MS_OFFICE_GENERIC_MIMETYPE = "application/x-tika-msoffice";
	public static String DETECTOR_MS_EXCEL_MIMETYPE = "application/vnd.ms-excel";
	public static String DETECTOR_OOXML_SPREADSHEET_MIME_TYPE="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	
	/*
	 * public enums
	 * 
	 */
	public static enum Delimiter {
		TAB, COMMA, SPACE, COLON
	};
	
	public static enum OptionState { NULL, TRUE, FALSE}; 

	
	public static enum FileType implements AppConstants {
		CSV(FILE_TYPE_CSV, ".csv", "application/ms-excel"), 
		XLS97(FILE_TYPE_XLS, ".xls", "application/ms-excel"),
		XLSX(FILE_TYPE_XLSX, ".xlsx", "application/ms-excel"),
		TEMPLATE(FILE_TYPE_TEMPLATE, ".csv", "application/ms-excel");
		private String ext = "";
		private String mimeType = "";
		private String name = "";
		
		FileType(String name, String extension, String mimeType) {
			this.name  = name;
			this.ext = extension;
			this.mimeType = mimeType;
		}
		
		
		/*
		 * methods
		 */
		
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
					if (f.getExtension().equals(extension.toLowerCase())) {
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
	 * (deprecated)only used in test class. test class is not run in default build
	 */
	public Upload parseImportXLS(MultipartFile file, ImportSettings settings)
	throws InvalidInputException, FatalException, IOException;
	
	/*
	 * (deprecated)only used in test class. test class is not run in default build
	 */
	public Upload parseImportCSV(String gradebookUid, Reader reader)
	throws InvalidInputException, FatalException;
	
	public Upload parseImportCSV(Reader reader, ImportSettings settings)
	throws InvalidInputException, FatalException;
	
	
	
	public void exportGradebook(FileType fileType, String filename, OutputStream outStream,
			Gradebook2ComponentService service, String gradebookUid,
			final boolean includeStructure, final boolean includeComments, List<String> SectionUid) throws FatalException;

	public Upload getImportFile(MultipartFile file, ImportSettings importSettings);

			
}
