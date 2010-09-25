package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.ConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.gradebook.gwt.sakai.model.StudentScore;

public class GradeDataLoader {
	
	public static String INPUT_KEY_MEAN = "mean";
	public static String INPUT_KEY_STDEV = "stdev";
	public static String INPUT_KEY_STDEVP = "stdevp";
	public static String INPUT_KEY_MEDIAN = "median";
	public static String INPUT_KEY_MODE = "mode"; // comma separated
	public static String INPUT_KEY_USE_DEPRECATED = "deprecated";
	private static List<String> statsKeys = new ArrayList<String> ();
	
	// tests can be threaded in parallel: use concurrency-safe hashmap
	private Map<String, Object> testStatsByKey = new ConcurrentHashMap<String, Object> (5);
	
	/* read this value to decide whether to use old calculations units or new */
	
	private boolean useDeprecatedCalculations = false;
	
	private static Log log = LogFactory.getLog(GradeDataLoader.class);
	
	private File dataFile = null;
	private List<StudentScore> scores = null;
	
	public GradeDataLoader() {}
	
	public GradeDataLoader(String dataFileName) {
		Collections.addAll(statsKeys, 
				INPUT_KEY_MEAN, /* not currently supported -> INPUT_KEY_STDEV, */ 
				INPUT_KEY_STDEVP, INPUT_KEY_MEDIAN,
				INPUT_KEY_MODE);
		
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
	
	public boolean isAllTestStatsKeysPresent() {
		
		/// make sure we have all the stats for testing
		
		for (String key : statsKeys) {
			if (!testStatsByKey.containsKey(key)) {
				System.err.println("Missing stats key: " + key);
				return false;
			}
		}
		return true;
		
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
				String label = null;
				
				while ((line = br.readLine()) != null)   {
					// check for stats keys
					String[] parts = line.split(":");
					label = parts[0].trim();
					if (parts.length > 0 && statsKeys.contains(label)
							&& !testStatsByKey.containsKey(label)) { // uses the first value given for a key
						if (label.equalsIgnoreCase(INPUT_KEY_USE_DEPRECATED)) {
							boolean choice = Boolean.valueOf(parts[1].trim());
							useDeprecatedCalculations = choice;
							continue;
						}
						try {
							if(label.equalsIgnoreCase(INPUT_KEY_MODE) && parts[1].split(",").length>0) {
								String[] list = parts[1].split(",");
								ArrayList<BigDecimal> result = new ArrayList<BigDecimal>(list.length);
								for (int i=0;i<list.length;++i) {
									BigDecimal bd = new BigDecimal(list[i].trim());
									result.add(bd);
								}
								testStatsByKey.put(label, result);
							} else
								testStatsByKey.put(label, (new BigDecimal(parts[1].trim())).toString());
							continue;
						
						} catch (NumberFormatException e) {
							System.err.println("stats value NumberFormatException: " + line);
							continue;
						}
					} else 
					try {
						scores.add(new StudentScore("" + id, new BigDecimal(line)));
					} catch (NumberFormatException e) {
						System.err.println("(ignored)NumberFormatException: " + line);
						continue;
					}
					id++;
					
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

	public boolean isUseDeprecatedCalculations() {
		return useDeprecatedCalculations;
	}

	public Map<String, Object> getTestStatsByKey() {
		return testStatsByKey;
	}

}
