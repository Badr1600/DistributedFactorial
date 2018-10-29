package client;

import java.math.BigInteger;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import commons.DistributedFact;
import commons.NumberObject;

/**
 * @author Ahmed Badr
 * 
 *         Title: Distributed Factorial JAVA RMI Client. Date: 28.10.2018
 * 
 *         This Interface contains the logic of the clients connecting to the
 *         JAVA RMI server starting by requesting unique ID from the server then
 *         ask for the target number to be factored. Then start the factoring
 *         process by asking the server for the number space available to be
 *         used.
 */
public class DistributedFactClient {

	private static int clientID;
	private static Long startTime = System.nanoTime(); // Client Job Start time stamp.
	private static int numberOfUsedRanges;

	/**
	 * Main Method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.print("Client Connected!");
		numberOfUsedRanges = 0;
		try {

			DistributedFact factRMIClient = (DistributedFact) Naming.lookup(DistributedFact.SERVICENAME);
			/*
			 * get unique id from the java RMI server, facilitates the communication with
			 * the server.
			 */
			clientID = factRMIClient.askForID();
			// get the target number to be factored.
			NumberObject numToBeFactored = factRMIClient.askForTheNumber();
			System.out.println("Connection Established.. Client Connected");
			System.out.println("Client ID: " + clientID);
			/*
			 * Timer to send periodic notification to the server indicating that the client
			 * is online and available for finding the factorial.
			 */
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						factRMIClient.checkConnection(clientID);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}, 0, 100);

			/*
			 * This loop maintain the searching routine for finding the factors of the
			 * target number by searching in different number spaces. The client gets one
			 * number space at a time: 1)if the factors are found within that space the loop
			 * will terminate and send the server the factors. 2) if not found in the
			 * current number space the client will ask for a new number space and continue
			 * the search routine.
			 */
			while (factRMIClient.checkConnection(clientID)) {
				numberOfUsedRanges++;
				ArrayList<String> result = findFactors(factRMIClient.askForRange(clientID), numToBeFactored);
				if (result.get(1).equals("Number Not Found")) {
					factRMIClient.factorialNotFound(clientID);
				} else {
					Long endTime = System.nanoTime();
					result.add("Time Elabsed: " + (endTime - startTime) / 1000000000 + " sec");
					factRMIClient.submitAnswer(result);
					System.out.println(result.toString());
					System.out.println("Number of used Packets: " + numberOfUsedRanges);
					System.out.println("Time Elabsed: " + (endTime - startTime) / 1000000000 + " sec");

					break;
				}
			}
			timer.cancel();
		} catch (Exception e) {
			System.err.println("Remote exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * The core logic function of the client process for finding the factorial of
	 * the target number.
	 * 
	 * The Function takes as input the number space getting the factorial of the
	 * number then initiates a loop searching the whole space. The loop eliminates
	 * 2,3 and 5 multiples given that the target number is not divisor of these
	 * numbers.
	 * 
	 * Note: logic can be enhanced.
	 * 
	 * @param numberSpace
	 * @param numToBeFactored
	 * @return
	 */
	private static ArrayList<String> findFactors(BigInteger[] numberSpace, NumberObject numToBeFactored) {
		ArrayList<String> answer = new ArrayList<>();
		answer.add("Client ID: " + clientID + " Found the Factors of: " + numToBeFactored.getTargetNumber().toString());
		BigInteger j = numberSpace[0];
		while (j.compareTo(numberSpace[1]) == -1) {
			/*
			 * This Part is responsible for checking & eliminating number "2" multiples
			 */
			if (j.mod(new BigInteger("2")).equals(BigInteger.ZERO)) {
				j = j.add(BigInteger.ONE);
				if (!numToBeFactored.isTwoEleminated()) {
					continue;
				} else {
					if (numToBeFactored.getTargetNumber().remainder(j).equals(BigInteger.ZERO)) {
						String tempStr = "Factors: " + j + "*" + numToBeFactored.getTargetNumber().divide(j).toString();
						answer.add(tempStr);
						return answer;
					}
					continue;
				}
			}
			/*
			 * This Part is responsible for checking & eliminating number "3" multiples
			 */
			if (j.mod(new BigInteger("3")).equals(BigInteger.ZERO)) {
				j = j.add(BigInteger.ONE);
				if (!numToBeFactored.isThreeEleminated()) {
					continue;
				} else {
					/*
					 * Return the factors if found.
					 */
					if (numToBeFactored.getTargetNumber().remainder(j).equals(BigInteger.ZERO)) {
						String tempStr = "Factors: " + j + "*" + numToBeFactored.getTargetNumber().divide(j).toString();
						answer.add(tempStr);
						return answer;
					}
					continue;
				}
			}
			/*
			 * This Part is responsible for checking & eliminating number "5" multiples
			 */
			if (j.mod(new BigInteger("5")).equals(BigInteger.ZERO)) {
				j = j.add(BigInteger.ONE);
				if (!numToBeFactored.isFiveEleminated()) {
					continue;
				} else {
					/*
					 * Return the factors if found.
					 */
					if (numToBeFactored.getTargetNumber().remainder(j).equals(BigInteger.ZERO)) {
						String tempStr = "Factors: " + j + "*" + numToBeFactored.getTargetNumber().divide(j).toString();
						answer.add(tempStr);
						return answer;
					}
					continue;
				}
			} else {
				/*
				 * Return the factors if found.
				 */
				if (numToBeFactored.getTargetNumber().remainder(j).equals(BigInteger.ZERO)) {
					String tempStr = "Factors: " + j + "*" + numToBeFactored.getTargetNumber().divide(j).toString();
					answer.add(tempStr);
					return answer;
				}
			}
			j = j.add(BigInteger.ONE);
		}
		answer.add("Number Not Found");
		return answer;
	}
}
