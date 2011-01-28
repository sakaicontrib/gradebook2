package org.sakaiproject.gradebook.gwt.sakai.calculations;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class wraps all the BigDecimal calculation methods that are needed by
 * Gradebook2. The gradebook2 code needs to use the methods provided by this class
 * instead of using the BigDecimal classes directly. We want to perform all calculations
 * using full precision. Also, in case we need to make some changes, we can do this easily
 * in this file instead of changing all the cases of using the BigDecimal methods directly.
 * We only apply a scale and rounding mode in cases where the divide 
 * method encounters an ArithmeticException.
 *
 */
public class BigDecimalCalculationsWrapper {

	private static final Log log = LogFactory.getLog(BigDecimalCalculationsWrapper.class);

	/*
	 * The scale can be overwritten either by the gradebook2 specific spring 
	 * bean definitions in:
	 * - applicationContext.xml
	 * - gbService.xml
	 * 
	 * ... or via the constructor
	 * 
	 * The scale is only used in cases where the divide method encounters an ArithmeticException
	 * 
	 */
	private int scale = 50;
	
	/*
	 * Gradebook2 uses the HALF_EVEN rounding method only in one specific case. It is
	 * used when the divide method encounters an ArithmeticException.
	 */
	private RoundingMode roundingMode = RoundingMode.HALF_EVEN;

	// Default Constructor
	public BigDecimalCalculationsWrapper() { }

	/* 
	 * Constructor that set the scale for cases where the divide method encounters
	 * an ArithmeticException
	 */
	public BigDecimalCalculationsWrapper(int scale) {

		this.scale = scale;
	}

	/** 
	 * Wrapping the BigDecimal add method
	 * 
	 * @see BigDecimal.add()
	 */
	public BigDecimal add(BigDecimal addend, BigDecimal augend) {

		return addend.add(augend);
	}

	/**
	 * Wrapping the BigDecimal subtract method
	 * 
	 * @see BigDecimal.subtract()
	 */
	public BigDecimal subtract(BigDecimal minuend, BigDecimal subtrahend) {

		return minuend.subtract(subtrahend);
	}

	/**
	 * Wrapping the BigDecimal multiply method 
	 * 
	 * @see BigDecimal.multiply()
	 */
	public BigDecimal multiply(BigDecimal multiplier, BigDecimal multiplicand) {

		return multiplier.multiply(multiplicand);
	}

	/**
	 * Wrapping the BigDecimal divide method. In case the method encounters
	 * an ArithmeticException exception, we execute the divide method a second
	 * time applying a scale and rounding mode.
	 * 
	 * @see BigDecimal.divide()
	 */
	public BigDecimal divide(BigDecimal dividend, BigDecimal divisor) {

		BigDecimal result = null;

		try {

			result = dividend.divide(divisor);

		} catch(ArithmeticException ae) {

			/*
			 * We are not handling divide by zero case here because the following call to divide
			 * will generate the exception again. The calling code should handle that case.
			 */
			result = dividend.divide(divisor, scale, roundingMode);
		}

		return result;
	}


	/**
	 * Getter method
	 * 
	 * @return roundingMode
	 */
	public RoundingMode getRoundingMode() {
		return roundingMode;
	}

	/**
	 * Setter method
	 * 
	 * @param roundingMode
	 */
	public void setRoundingMode(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
	}

	/**
	 * Getter method
	 * 
	 * @return scale
	 */
	public int getScale() {
		return scale;
	}

	/**
	 * Setter method
	 * 
	 * @param scale
	 */
	public void setScale(int scale) {

		log.info(" ## GB2 ##: Setting scale to " + scale + " : BigDecimalCalculationsWrapper's subclass is " + this.getClass().getName());
		this.scale = scale;
	}

	/**
	 * Calculates the square root
	 * 
	 * @param operand
	 * @return the square root of operand
	 */
	public BigDecimal sqrt(BigDecimal operand) {

		BigSquareRoot sqrtHelper = new BigSquareRoot();

		return sqrtHelper.get(operand);

	}
	
	/**
	 * This method used to reside in the NumericUtils class as a static method. In order to
	 * keep all the calculation methods centralized, and to make sure that we use a consistent
	 * math context/rounding mode, we moved it in here.
	 * @param dend
	 * @param dor
	 * @return result of the division
	 */
	public double divideWithPrecision(double dend, double dor) {
		double ret = 0.0; 
		String send = Double.toString(dend); 
		String sor = Double.toString(dor); 
		BigDecimal val = new BigDecimal(send); 
		BigDecimal divisor = new BigDecimal(sor); 
		BigDecimal result = divide(val, divisor);
		ret = result.doubleValue(); 
		return ret; 
	}

	/**
	 * 
	 * Inner class definition of BigSquareRoot
	 *
	 */
	public class BigSquareRoot {

		private final BigDecimal TWO = new BigDecimal("2");
		public final int DEFAULT_MAX_ITERATIONS = 50;


		private BigDecimal error;
		private int iterations;
		private int maxIterations = DEFAULT_MAX_ITERATIONS;

		//---------------------------------------
		// The error is the original number minus
		// (sqrt * sqrt). If the original number
		// was a perfect square, the error is 0.
		//---------------------------------------

		public BigDecimal getError() {
			return error;
		}

		//-------------------------------------------------------------
		// Number of iterations performed when square root was computed
		//-------------------------------------------------------------

		public int getIterations() {
			return iterations;
		}

		//-------------------
		// Maximum iterations
		//-------------------

		public int getMaxIterations() {
			return maxIterations;
		}

		public void setMaxIterations(int maxIterations) {
			this.maxIterations = maxIterations;
		}

		//--------------------------
		// Get initial approximation
		//--------------------------

		private BigDecimal getInitialApproximation(BigDecimal n) {
			BigInteger integerPart = n.toBigInteger();
			int length = integerPart.toString().length();
			if ((length % 2) == 0) {
				length--;
			}
			length /= 2;
			BigDecimal guess = BigDecimal.ONE.movePointRight(length);
			return guess;
		}

		//----------------
		// Get square root
		//----------------
		public BigDecimal get(BigDecimal n) {

			// Make sure n is a positive number

			if (n.compareTo(BigDecimal.ZERO) < 0) {
				throw new IllegalArgumentException();
			}

			BigDecimal initialGuess = getInitialApproximation(n);
			BigDecimal lastGuess = BigDecimal.ZERO;
			BigDecimal guess = new BigDecimal(initialGuess.toString());

			// Iterate

			iterations = 0;
			boolean more = true;
			while (more) {
				lastGuess = guess;
				guess = divide(n, guess);
				guess = add(guess, lastGuess);
				guess = divide(guess, TWO);
				error = subtract(n, guess.multiply(guess));
				if (++iterations >= maxIterations) {
					more = false;
				} else if (lastGuess.equals(guess)) {
					more = error.abs().compareTo(BigDecimal.ONE) >= 0;
				}
			}
			return guess;

		}	
	}
}
