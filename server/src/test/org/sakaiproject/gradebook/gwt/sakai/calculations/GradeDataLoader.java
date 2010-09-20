package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
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
	
	public GradeDataLoader(String dataFileName) {
		URL u = ClassLoader.getSystemResource(dataFileName);
		if(u != null) {
			File dataFile = new File(u.getPath());
			if(dataFile.exists() && dataFile.canRead()) {
				this.dataFile = dataFile;
				try {
					loadDataFile();
				} catch (Exception e) {
					this.dataFile = null;
					this.scores = null;
					System.out.println("General Exception reading: " + dataFileName);
				}

			} else {
				System.out.println("Unable to read data file: " + dataFile.getPath());
			}
		} else {
			System.out.println("Unable find data file: " + dataFileName);
		}

	}

	public List<StudentScore> getScores() {
		return scores;
	}

	public void setScores(List<StudentScore> scores) {
		this.scores = scores;
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
					try {
						scores.add(new StudentScore("" + id, new BigDecimal(line)));
					} catch (NumberFormatException e) {
						System.err.println("(ignored)NumberFormatException: " + line);
						continue;
					}
					id++;
				}
				System.out.println (--id + " lines read");

				for (StudentScore s : scores) {
					System.out.println(s);
				}
			} catch (FileNotFoundException e) {
				System.out.println("File not found: " + dataFile.getPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("error (line " + id + ") reading file: " + dataFile.getPath());
			}
			
		} else {
			System.out.println("Filed to read file: " + dataFile.getPath());
		}
		
	}

}
