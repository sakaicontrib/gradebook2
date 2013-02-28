package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.sakaiproject.gradebook.gwt.sakai.calculations.BigDecimalFactory;
//import java.util.regex.Pattern;
////import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.CoreMatchers.*;
////import static org.hamcrest.Matcher.;
////import static org.hamcrest.CoreMatchers.*;
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.Assert.assertThat;
//
//import org.hamcrest.Description;
//import org.hamcrest.Matcher;


//boolean b = Pattern.matches("a*b", "aaaaab"); // example of skipping explicit matcher.

import junit.framework.TestCase;

public class BigDecimalFactoryTest extends TestCase {

	BigDecimalFactory bdf;

	public BigDecimalFactoryTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		bdf = new BigDecimalFactory();
		bdf.setPrintInterval(1000);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}


	// utility function
	public boolean checkForString(String msg, String s, String p) {

		if (s.matches(p)) {
			assertTrue(msg,s.matches(p));
			return true;
		}
		fail("msg: ["+msg+"] failed to find ["+p+"] in ["+s+"]");	
		return false;
	}

	// Tests are not comprehensive over all types.  They are targeted on STRING caching and also
	// include spot checks that other argument types work.

	// Numbers in regex patterns below are followed by \\b.* to make sure they don't just
	// match a number beginning with the desired number.

	public void testMultipleTypesNotSameObject() {
			
		BigDecimal a = bdf.sameBigDecimal(1.0);
		assertNotNull(a);

		BigDecimal b = bdf.sameBigDecimal("1.1");
		assertNotNull(b);

		BigDecimal c = bdf.sameBigDecimal("1.2");
		assertNotNull(c);

		BigDecimal d =bdf.sameBigDecimal(1.3);
		assertNotNull(d);

		BigDecimal e = bdf.sameBigDecimal(BigInteger.valueOf(4));
		assertNotNull(e);

		// make sure that there are different objects for different arguments.
		assertNotSame(a,b);
		assertNotSame(b,c);
		assertNotSame(b,e);

	}

	public void testMultipleTypes() {

		bdf.sameBigDecimal(1.0);
		bdf.sameBigDecimal("1.1");
		bdf.sameBigDecimal("1.2");
		bdf.sameBigDecimal(1.3);
		bdf.sameBigDecimal(BigInteger.valueOf(4));

		String counts =	bdf.getFactoryStats();

		checkForString("check counts", counts, ".*requests: 5\\b.*");
		checkForString("check counts", counts, ".*cacheHits: 0\\b.*");
		checkForString("check counts", counts, ".*STRING: 2\\b.*");
		checkForString("check counts", counts, ".*DOUBLE: 2\\b.*");
		checkForString("check counts", counts, ".*BIGINTEGER: 1\\b.*");

	}

	public void testBigInteger() {
		bdf.sameBigDecimal(BigInteger.valueOf(4));

		String counts = bdf.getFactoryStats();
		checkForString("check counts", counts, ".*requests: 1\\b.*");
		checkForString("check counts", counts, ".*cacheHits: 0\\b.*");
		checkForString("check counts", counts, ".*BIGINTEGER: 1\\b.*");
	}


	public void testLong() {
		bdf.sameBigDecimal(1L);
		bdf.sameBigDecimal(0L);
		bdf.sameBigDecimal(2L);

		String counts = bdf.getFactoryStats();
		checkForString("check counts", counts, ".*requests: 3\\b.*");
		checkForString("check counts", counts, ".*cacheHits: 0\\b.*");
		checkForString("check counts", counts, ".*LONG: 3\\b.*");	
	}

	public void testDoubleToString() {
		BigDecimal a = bdf.sameBigDecimalToString(new Double(1));
		BigDecimal b = bdf.sameBigDecimalToString(new Double(1));
		BigDecimal cA = bdf.sameBigDecimalToString(new Double(0));
		BigDecimal cB = bdf.sameBigDecimalToString(new Double(0));
		BigDecimal d = bdf.sameBigDecimalToString(new Double(3.0));
		BigDecimal e = bdf.sameBigDecimalToString(new Double(0.1));
		BigDecimal f = bdf.sameBigDecimalToString(new Double("0.1"));

		assertEquals("same value for 1",a,b);
		assertEquals("same value for 0",cA,cB);
		assertEquals("same value for 0.1 double and 0.1 String",e,f);
		
		assertSame("cached Double(1)",a,b);
		assertSame("cached Double(0)",cA,cB);
		assertSame("cached 0.1",e,f);
		
		//assertNotEqual("different values for 1 and 0",a,cA);
		
		String counts = bdf.getFactoryStats();
		checkForString("check counts", counts, ".*requests: 7\\b.*");
		checkForString("check counts", counts, ".*cacheHits: 3\\b.*");
	}
	
	public void testStringBasicCaching() {
		BigDecimal a = bdf.sameBigDecimal("100.1");
		assertNotNull(a);
		BigDecimal b = bdf.sameBigDecimal("100.1");
		assertNotNull(b);
		
		String counts = bdf.getFactoryStats();
		checkForString("check counts", counts, ".*requests: 2\\b.*");
		checkForString("check counts", counts, ".*cacheHits: 1\\b.*");
		checkForString("check counts", counts, ".*STRING: 2\\b.*");
		
		assertEquals("verify that the value of the BigDecimal objects are the same",a,b);
		assertSame("verify that the BigDecimal objects returned are identical",a,b);
		
	}
	
	public void testStringCachingMultipleDifferentStringValues() {
		BigDecimal a = bdf.sameBigDecimal("100.1");
		assertNotNull(a);
		BigDecimal b = bdf.sameBigDecimal("100.1");
		assertNotNull(b);
		
		BigDecimal c = bdf.sameBigDecimal("102");
		assertNotNull(c);
		BigDecimal d = bdf.sameBigDecimal("102");
		assertNotNull(d);
		BigDecimal e = bdf.sameBigDecimal("102");
		assertNotNull(e);
		
		String counts = bdf.getFactoryStats();
		checkForString("check counts", counts, ".*requests: 5\\b.*");
		checkForString("check counts", counts, ".*cacheHits: 3\\b.*");
		checkForString("check counts", counts, ".*STRING: 5\\b.*");
		
		assertEquals("verify that the value of the BigDecimal objects are the same",a,b);
		
		assertSame("verify that the BigDecimal objects returned are identical",a,b);
		
		assertEquals("verify that the value of the BigDecimal objects are the same",c,d);
		assertEquals("verify that the value of the BigDecimal objects are the same",d,e);
		
		assertSame("verify that the BigDecimal objects returned are identical",c,d);
		assertSame("verify that the BigDecimal objects returned are identical",d,e);

		
	}
	
	// Used to verify printInterval.  Seldom needed.
//	public void testStringManyAllocations() {
//		for(int i = 1; i<=10;i++) {
//				bdf.sameBigDecimal("100.1");
//		}
//		
//	}
	
}
