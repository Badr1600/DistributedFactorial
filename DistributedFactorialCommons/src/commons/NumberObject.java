package commons;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author Ahmed Badr
 * 
 *         Title: NumberObject Date: 28.10.2018
 * 
 *         This class represents the target number to find its factors.
 */
public class NumberObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private BigInteger targetNumber; // Number to be factored
	private boolean twoEleminated; // Helper to identify the number is multiple of 2
	private boolean threeEleminated; // Helper to identify the number is multiple of 3
	private boolean fiveEleminated; // Helper to identify the number is multiple of 5
	private BigInteger rangeIndex; // Index to track the number space sent to each user
	private BigInteger rangeSize; // Range packet size, subset of the number space

	/**
	 * Class Constructor.
	 * 
	 * @param number
	 */
	public NumberObject(BigInteger number) {
		this.targetNumber = number;
		this.twoEleminated = false;
		this.threeEleminated = false;
		this.fiveEleminated = false;
		this.rangeIndex = BigInteger.ONE;
		eliminateMultiples235(number);
		setRangeSize(calculateRangeBackets());
	}

	/**
	 * Helper function to identify if the target number is multiple of any 2,3 or 5.
	 * 
	 * @param bigNumber
	 */
	private void eliminateMultiples235(BigInteger bigNumber) {
		String numberStr = bigNumber.toString();
		int digitsSum = 0;
		for (int i = 0; i < numberStr.length(); i++) {
			digitsSum = digitsSum + Character.getNumericValue(numberStr.charAt(i));
		}
		if (bigNumber.mod(new BigInteger("2")).equals(BigInteger.ZERO)) {
			setTwoEleminated(true);
		}
		if (digitsSum % 3 == 0) {
			setThreeEleminated(true);
		}
		if (bigNumber.mod(new BigInteger("5")).equals(BigInteger.ZERO)) {
			setFiveEleminated(true);
		}
	}

	/*
	 * public ArrayList<BigInteger[]> calcualteRanges() { ArrayList<BigInteger[]>
	 * rangeBackets = new ArrayList<>(); BigInteger divident =
	 * calculateRangeBackets(); BigInteger backetSize =
	 * getTargetNumber().divide(calculateRangeBackets());
	 * 
	 * for (BigInteger i = BigInteger.ZERO; i.compareTo(divident) == -1; i =
	 * i.add(BigInteger.ONE)) { BigInteger[] range = new BigInteger[2]; if
	 * (i.equals(BigInteger.ZERO)) { range[0] = new BigInteger("2"); range[1] =
	 * backetSize; } else { range[0] = backetSize.multiply(new BigInteger("" + i));
	 * BigInteger tmpNumber = i.add(BigInteger.ONE); range[1] =
	 * backetSize.multiply(tmpNumber); } rangeBackets.add(range); } return
	 * rangeBackets; }
	 */

	/**
	 * Function to get the a small range of numbers from the whole number space to
	 * try to find the factors from.
	 * 
	 * @return The next range in form of BigInteger[] of two cells
	 *         [lowerBound,upperBound].
	 * 
	 */
	public synchronized BigInteger[] getNextRange() {
		BigInteger backetSize = getRangeSize();
		BigInteger[] range = new BigInteger[2];
		if (rangeIndex.equals(BigInteger.ONE)) {
			range[0] = new BigInteger("2");
			range[1] = backetSize;
			rangeIndex = rangeIndex.add(BigInteger.ONE);
		} else {
			range[0] = backetSize.multiply(rangeIndex);
			rangeIndex = rangeIndex.add(BigInteger.ONE);
			range[1] = backetSize.multiply(rangeIndex);
		}
		return range;
	}

	/**
	 * Function to calculate the size of each numbers range sent from the numbers
	 * space to the client.
	 * 
	 * @return BigInteger
	 */
	private BigInteger calculateRangeBackets() {
		BigInteger factor = getTargetNumber()
				.divide(new BigInteger("" + (getTargetNumber().toString().length())).pow(10));
		return (factor);
	}

	////////////////////////////////////////////////////////////////////////
	//
	// Basic GETTERs and SETTERs
	//
	////////////////////////////////////////////////////////////////////////

	/**
	 * @return the rangeSize
	 */
	public BigInteger getRangeSize() {
		return rangeSize;
	}

	/**
	 * @param rangeSize the rangeSize to set
	 */
	public void setRangeSize(BigInteger rangeSize) {
		this.rangeSize = rangeSize;
	}

	/**
	 * @return the targetNumber
	 */
	public BigInteger getTargetNumber() {
		return targetNumber;
	}

	/**
	 * @return the twoEleminated
	 */
	public boolean isTwoEleminated() {
		return twoEleminated;
	}

	/**
	 * @param twoEleminated the twoEleminated to set
	 */
	public void setTwoEleminated(boolean twoEleminated) {
		this.twoEleminated = twoEleminated;
	}

	/**
	 * @return the threeEleminated
	 */
	public boolean isThreeEleminated() {
		return threeEleminated;
	}

	/**
	 * @param threeEleminated the threeEleminated to set
	 */
	public void setThreeEleminated(boolean threeEleminated) {
		this.threeEleminated = threeEleminated;
	}

	/**
	 * @return the fiveEleminated
	 */
	public boolean isFiveEleminated() {
		return fiveEleminated;
	}

	/**
	 * @param fiveEleminated the fiveEleminated to set
	 */
	public void setFiveEleminated(boolean fiveEleminated) {
		this.fiveEleminated = fiveEleminated;
	}

	/**
	 * @return the rangeIndex
	 */
	public BigInteger getRangeIndex() {
		return rangeIndex;
	}
}
