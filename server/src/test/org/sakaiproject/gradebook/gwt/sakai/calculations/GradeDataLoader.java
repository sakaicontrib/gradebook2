package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.sakaiproject.gradebook.gwt.sakai.model.StudentScore;

public class GradeDataLoader {
	
	public static String INPUT_KEY_ERROR = "error";
	public static String INPUT_KEY_MEAN = "mean";
	public static String INPUT_KEY_STDEV = "stdev";
	public static String INPUT_KEY_STDEVP = "stdevp";
	public static String INPUT_KEY_MEDIAN = "median";
	public static String INPUT_KEY_MODE = "mode"; // comma separated
	public static String INPUT_KEY_USE_DEPRECATED = "deprecated";
	public static String INPUT_KEY_SCALE = "scale";
	private static List<String> statsKeys = new ArrayList<String> ();
	
	// tests can be threaded in parallel: use concurrency-safe hashmap
	private Map<String, Object> testStatsByKey = new ConcurrentHashMap<String, Object> (5);
	
	/* read this value to decide whether to use old calculations units or new */
	
	private boolean useDeprecatedCalculations = false;
	private int scale = (new BigDecimalCalculationsWrapper()).getScale();
	
	private File dataFile = null;
	private List<StudentScore> scores = null;
	private BigDecimal acceptableError = BigDecimal.ONE.movePointLeft(52);
	
	public GradeDataLoader() {}
	
	public GradeDataLoader(String dataFileName) {
		Collections.addAll(statsKeys, 
				INPUT_KEY_MEAN, /* not currently supported -> INPUT_KEY_STDEV, */ 
				INPUT_KEY_STDEVP, INPUT_KEY_MEDIAN,
				INPUT_KEY_MODE, INPUT_KEY_USE_DEPRECATED,
				INPUT_KEY_SCALE, INPUT_KEY_ERROR);
		
		File dataFile = null;
			
		URL u = ClassLoader.getSystemResource(dataFileName); // search the classpath first
		if (null == u) {
			dataFile = new File(dataFileName);
		} else {
			dataFile = new File(u.getPath());
		}
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
				if(key.equals(INPUT_KEY_MODE)) {
					System.err.println("Test will be expecting no mode value");
					testStatsByKey.put(INPUT_KEY_MODE, new ArrayList<BigDecimal>());
				} else if (key.equals(INPUT_KEY_SCALE)){
					System.err.println("no scale key found... using default (" + scale + ")...probably not what you want");
				} else if (key.equals(INPUT_KEY_ERROR)){
					System.err.println("no 'error' key found... using default (" + acceptableError + ")...probably not what you want");
				} else {
					System.err.println("Missing stats key: " + key);
					return false;
				}
			}
		}
		return true;
		
	}

	private void loadDataFile() {
		
		System.out.println("Reading test data from: " + dataFile);
		
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
					// check for stats keysnewChar
					String[] parts = line.replace("\"","").replace("'","").split(":");
					label = parts[0].trim();
					if (parts.length > 0 && statsKeys.contains(label)
							&& !testStatsByKey.containsKey(label)) { // uses the first value given for a key
						if (label.equalsIgnoreCase(INPUT_KEY_USE_DEPRECATED)) {
							useDeprecatedCalculations = Boolean.valueOf(parts[1].replace(",","").trim());
							System.out.println(INPUT_KEY_USE_DEPRECATED + ": " + useDeprecatedCalculations);
							continue;
						}
						if (label.equalsIgnoreCase(INPUT_KEY_SCALE)) {
							scale = Integer.valueOf(parts[1].replace(",","").trim());
							testStatsByKey.put(INPUT_KEY_SCALE, scale);
							System.out.println(INPUT_KEY_SCALE + ": " + scale);
							continue;
						}
						if (label.equalsIgnoreCase(INPUT_KEY_ERROR)) {
							acceptableError = new BigDecimal(parts[1].replace(",","").trim());
							System.out.println(INPUT_KEY_ERROR + ": " + acceptableError);
							testStatsByKey.put(INPUT_KEY_ERROR, acceptableError);
							continue;
						}
						try {
							String[] list = null;
							if (parts[1].trim().charAt(0) ==',') {
								list = parts[1].trim().substring(1).split(",");
							} else {
								list = parts[1].split(",");
							}
							if(label.equalsIgnoreCase(INPUT_KEY_MODE) && list.length>0) {
								ArrayList<BigDecimal> result = new ArrayList<BigDecimal>(list.length);
								System.out.println(INPUT_KEY_MODE + ":");
								for (int i=0;i<list.length;++i) {
									BigDecimal bd = new BigDecimal(list[i].trim());
									result.add(bd);
									System.out.println("--> " + bd);
								}
								testStatsByKey.put(label, result);
							} else {
								testStatsByKey.put(label, (new BigDecimal(parts[1].replace(",","").trim())).toString());
								System.out.println(label + ": " + testStatsByKey.get(label));
							}
							
							
							continue;
						
						} catch (NumberFormatException e) {
							System.err.println("stats value NumberFormatException: " + line);
							continue;
						}
					} else 
						try {
							scores.add(new StudentScore("" + id, new BigDecimal(line.replace("\"","").replace("'","").replace(",", ""))));
							
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
		
		/*
		 *  these aren't required values
		 *  remove them so that isAllTestStatsKeysPresent() won't send false negatives
		 */
		statsKeys.remove(INPUT_KEY_USE_DEPRECATED); 
		//statsKeys.remove(INPUT_KEY_ERROR);
		
		// make sure all the input statistics have the same requested scale

		for(String key : testStatsByKey.keySet()) {
			Object o = testStatsByKey.get(key);
			if(o != null && key.equals(INPUT_KEY_MEAN) || key.equals(INPUT_KEY_STDEVP)) {
				BigDecimal fromFile = new BigDecimal((String)o);
				BigDecimal toScale = fromFile.setScale(scale, RoundingMode.HALF_UP);
				if (!fromFile.equals(toScale)) {
					System.err.println("Adjusting scale of input for key '" + key + "' to scale: " + scale);
					testStatsByKey.put(key, toScale.toString());
				}
			}
		}
		
		
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public boolean isUseDeprecatedCalculations() {
		return useDeprecatedCalculations;
	}

	public Map<String, Object> getTestStatsByKey() {
		return testStatsByKey;
	}

	public BigDecimal getAcceptableError() {
		return acceptableError ;
	}

}
