package org.sakaiproject.gradebook.gwt.sakai.calculations2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BigDecimalCalculationsWrapper {

	private static final Log log = LogFactory.getLog(BigDecimalCalculationsWrapper.class);

	private int scale = 50;
	private RoundingMode roundingMode = RoundingMode.HALF_EVEN;

	public BigDecimalCalculationsWrapper() {
		//log.info("BigDecimalCalculationsWrapper default constructor called.");
	}

	public BigDecimalCalculationsWrapper(int scale) {

		//log.info("#### TEST #### BigDecimalCalculationsWrapper(int scale) constructor called. This should only occure during JUnit tests");
		//log.info("#### TEST #### Setting MathContext scale to " + scale);
		this.scale = scale;

	}

	public BigDecimal add(BigDecimal addend, BigDecimal augend) {

		return addend.add(augend);
	}

	public BigDecimal subtract(BigDecimal minuend, BigDecimal subtrahend) {

		return minuend.subtract(subtrahend);
	}

	public BigDecimal multiply(BigDecimal multiplier, BigDecimal multiplicand) {

		return multiplier.multiply(multiplicand);
	}

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


	public RoundingMode getRoundingMode() {
		return roundingMode;
	}

	public void setRoundingMode(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {

		log.info(" ## GB2 ##: Setting scale to " + scale + " : BigDecimalCalculationsWrapper's subclass is " + this.getClass().getName());
		this.scale = scale;
	}

	public BigDecimal sqrt(BigDecimal operand) {

		BigSquareRoot sqrtHelper = new BigSquareRoot();

		return sqrtHelper.get(operand);

	}

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
