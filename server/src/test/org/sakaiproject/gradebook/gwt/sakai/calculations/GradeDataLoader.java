package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.sakai.model.StudentScore;

public class GradeDataLoader {
	private static Log log = LogFactory.getLog(GradeDataLoader.class);
	
	private File dataFile = null;
	private List<StudentScore> scores = null;
	
	public GradeDataLoader() {}
	
	public GradeDataLoader(File dataFile) {
		if(dataFile.exists() && dataFile.canRead()) {
			this.dataFile = dataFile;
			try {
				loadDataFile();
			} catch (Exception e) {
				this.dataFile = null;
				this.scores = null;
			}
			
		}
		
	}

	private void loadDataFile() {
		if(dataFile.exists() && dataFile.canRead()) {
			int id = 1;
			if (null == scores) {
				scores = new ArrayList<StudentScore>();
			}
			try {
				BufferedReader br = new BufferedReader(new FileReader(dataFile));
				
				String line = null;
				
				while ((line = br.readLine()) != null)   {
				      scores.add(new StudentScore("" + id, new BigDecimal(line)));
				      id++;
				    }
				System.out.println (--id + " lines read");
				
				for (StudentScore s : scores) {
					System.out.println(s);
				}
			} catch (FileNotFoundException e) {
				log.error("File not found: " + dataFile.getPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error("error (line " + id + ") reading file: " + dataFile.getPath());
			}
			
		} else {
			log.error("Filed to read file: " + dataFile.getPath());
		}
		
	}

}
