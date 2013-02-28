package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BigDecimalFactory {

	private static final Log log = LogFactory.getLog(BigDecimalFactory.class);
	
	/*
	 * Return BigDecimal objects corresponding to the BigDecimal constructors.  This wrapper approach allows 
	 * implementing a cache for commonly requested values.
	 * 
	 * This wraps all calls to the BigDecimal constructors, but it implements caching 
	 * only the most requested BigDecimal type in Gradebook2: STRING. If it turns out that additional types of
	 * constructors should have values cached it is probably best not to create new parallel 
	 * HashMap structures for each type.  Perhaps an inner class using Java generics would be appropriate.
	 *
	 * This implementation does NOT cache across threads.  The vast majority of the benefit from 
	 * caching should be within a thread.  If cross thread caching appears necessary then the standard 
	 * Sakai ehcache approach should be used.
	 * 
	 */

	// These enum entries correspond to the different BigDecimal constructors.
	enum BD_Types  {
		CHAR,STRING,DOUBLE,BIGINTEGER,INT,LONG,
		CHAR_MATHCONTEXT,STRING_MATHCONTEXT,DOUBLE_MATHCONTEXT,BIGINTEGER_MATHCONTEXT,
		BIGINTEGER_INT,
		INT_MATHCONTEXT,LONG_MATHCONTEXT,
		CHAR_INT_INT,
		BIGINTEGER_INT_MATHCONTEXT,
		CHAR_INT_INT_MATHCONTEXT,
		DOUBLE_TO_STRING
	};
	// DOUBLE_TO_STRING created to allow call with double that will be converted via toString if necessary.

	/// Count the requests for each constructor type.
	int[] bdCounts = new int[BD_Types.values().length];
	
	// Keep track of the overall cache performance.
	int cacheHits = 0;
	int factoryRequests = 0;

	/*
	* Some update statistics are maintained so performance can be tracked.
	*/

	Map<String, BigDecimal> cacheString = new HashMap<String, BigDecimal>(200);
	
	// Get a cached value if it exists.
	protected BigDecimal getCachedValueString(String key) {
		BigDecimal bd = cacheString.get(key);
		if (bd!=null) {
			cacheHits++;
		};
		return bd;
	}


	// Cache a key / value pair.
	protected BigDecimal setCachedValueString(String key, BigDecimal value) {
		cacheString.put(key,value);
		return value;
	}

	/*
	* Implement a DOUBLE only cache.
	* Some update statistics are maintained so performance can be tracked.
	*/
	Map<Double, BigDecimal> cacheDouble = new HashMap<Double, BigDecimal>(200);
	
	// Get a cached value if it exists.
	protected BigDecimal getCachedValueDouble(Double key) {
		BigDecimal bd = cacheDouble.get(key);
		if (bd != null) {
			cacheHits++;
		};
		return bd;
	}

	// Cache a key / value pair.
	protected BigDecimal setCachedValueDouble(Double key, BigDecimal value) {
		cacheDouble.put(key,value);
		return value;
	}
	
	/*
	 * Helper routines
	 */
	
	/* helper routine to update counts */
	protected void updateTypeCount(BD_Types type) {
		factoryRequests++;
		bdCounts[type.ordinal()]++;
	}

	// Summary statistic methods.  This information is vital to evaluating cache performance now
	// and in the future.  It prints the total number of factory requests, the number of
	// cache hits.  It also prints the number of requests by constructor type for constructors
	// that have been called at least once.

	// Determine how often to print an output summary
	int printInterval = 100000;
	
	// setter / getter very useful for testing.
	public int getPrintInterval() {
		return printInterval;
	}

	public void setPrintInterval(int printInterval) {
		this.printInterval = printInterval;
	}

	// return string with performance information.
	public String getFactoryStats () {
		StringBuilder r = new StringBuilder();

		r.append("BigDecimalFactory: requests: "+factoryRequests+" cacheHits: "+cacheHits);
		
		for(BD_Types  bdt : BD_Types.values()) {

			if (bdCounts[bdt.ordinal()] == 0)
				continue;
			
			r.append(" ")	
			.append(bdt.toString())
			.append(": ")
			.append(bdCounts[bdt.ordinal()]);
		}

		return r.toString();
	}
	
	/*
	 * Implement cached BigDecimal objects by wrapping BigDecimal constructors.
	 *	
	* The following methods map to BigDecimal constructors.  Most of them 
	* just update a count and call the BigDecimal constructor.  
	*
	* Caching is implemented on a type by type basis depending on 
	* the estimated number of calls so we can determine if it is 
	* worth addressing.
	* 
	*/
	
	// Return  Big Decimals based on string value
	public BigDecimal sameBigDecimal(String val) {
		
		// Print summary stats periodically.  By default this is every 
		if ((factoryRequests > 0) && (factoryRequests % printInterval) == 0) {
			log.info(getFactoryStats());
		}
		
		updateTypeCount(BD_Types.STRING);
		BigDecimal bd = getCachedValueString(val);
		
		if (bd == null) {
			bd = setCachedValueString(val,new BigDecimal(val));
		}
		
		return bd; 
	}
	
	public BigDecimal sameBigDecimal(char[] in) {
		updateTypeCount(BD_Types.CHAR);	
		return new BigDecimal(in);
	}

	public BigDecimal sameBigDecimal(double val) {
		updateTypeCount(BD_Types.DOUBLE);
		return new BigDecimal(val);
	}

	// Return Big Decimals based on value of Double
	// new BigDecimal(<some Double>.toString()).
	public BigDecimal sameBigDecimalToString(Double val) {
		if ((factoryRequests > 0) && (factoryRequests % printInterval) == 0) {
			log.info(getFactoryStats());
		}
		
		updateTypeCount(BD_Types.DOUBLE_TO_STRING);
		
		BigDecimal bd = getCachedValueDouble(val);
		if (bd == null) {
			bd = setCachedValueDouble(val,new BigDecimal(val.toString()));
		}

		return bd;
	}

	
	public BigDecimal sameBigDecimal(BigInteger val) {
		updateTypeCount(BD_Types.BIGINTEGER);
		return new BigDecimal(val);
	}

	public BigDecimal sameBigDecimal(int val) {
		updateTypeCount(BD_Types.INT);
		return new BigDecimal(val);
	}

	public BigDecimal sameBigDecimal(long val) {
		updateTypeCount(BD_Types.LONG);
		return new BigDecimal(val);
	}

	public BigDecimal sameBigDecimal(char[] in, MathContext mc) {
		updateTypeCount(BD_Types.CHAR_MATHCONTEXT);
		return new BigDecimal(in,mc);
	}

	public BigDecimal sameBigDecimal(String val, MathContext mc) {
		updateTypeCount(BD_Types.STRING_MATHCONTEXT);
		return new BigDecimal(val);
	}

	public BigDecimal sameBigDecimal(double val, MathContext mc) {
		updateTypeCount(BD_Types.DOUBLE_MATHCONTEXT);
		return new BigDecimal(val,mc);
	}

	public BigDecimal sameBigDecimal(BigInteger val, MathContext mc) {
		updateTypeCount(BD_Types.BIGINTEGER_MATHCONTEXT);
		return new BigDecimal(val,mc);
	}

	public BigDecimal sameBigDecimal(BigInteger unscaledVal, int scale) {
		updateTypeCount(BD_Types.BIGINTEGER_INT);
		return new BigDecimal(unscaledVal,scale);
	}

	public BigDecimal sameBigDecimal(int val, MathContext mc) {
		updateTypeCount(BD_Types.INT_MATHCONTEXT);
		return new BigDecimal(val);
	}

	public BigDecimal sameBigDecimal(long val, MathContext mc) {
		updateTypeCount(BD_Types.LONG_MATHCONTEXT);
		return new BigDecimal(val);
	}

	public BigDecimal sameBigDecimal(char[] in, int offset, int len) {
		updateTypeCount(BD_Types.CHAR_INT_INT);
		return new BigDecimal(in,offset,len);
	}

	public BigDecimal sameBigDecimal(BigInteger unscaledVal, int scale,
			MathContext mc) {
		updateTypeCount(BD_Types.BIGINTEGER_INT_MATHCONTEXT);
		return new BigDecimal(unscaledVal,scale,mc);
	}

	public BigDecimal sameBigDecimal(char[] in, int offset, int len, MathContext mc) {
		updateTypeCount(BD_Types.CHAR_INT_INT_MATHCONTEXT);
		return new BigDecimal(in,offset,len,mc);
	}

}
