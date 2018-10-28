package client;

import java.math.BigInteger;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import commons.DistributedFact;
import commons.NumberObject;

public class DistributedFactClient {

	private static int clientID;
	private static Long startTime = System.nanoTime();

	public static void main(String[] args) {
		System.out.print("Client Connected!");
		try {
			DistributedFact factRMIClient = (DistributedFact) Naming.lookup(DistributedFact.SERVICENAME);
			clientID = factRMIClient.askForID();
			NumberObject numToBeFactored = factRMIClient.askForTheNumber();
			System.out.println("Connection Established.. Client Connected");
			System.out.println("Client ID: " + clientID);
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

			while (factRMIClient.checkConnection(clientID)) {
				ArrayList<String> result = findFactors(factRMIClient.askForRange(clientID), numToBeFactored);
				if (result.get(1).equals("Number Not Found")) {
					factRMIClient.factorialNotFound(clientID);
				} else {
					Long endTime = System.nanoTime();
					result.add("Time Elabsed: " + (endTime - startTime) / 1000000000 + " sec");
					factRMIClient.submitAnswer(result);
					System.out.println(result.toString());
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

	private static ArrayList<String> findFactors(BigInteger[] bounds, NumberObject numToBeFactored) {
		ArrayList<String> answer = new ArrayList<>();
		answer.add("Client ID: " + clientID + " Found the Factors of: " + numToBeFactored.getTargetNumber().toString());
		BigInteger j = bounds[0];
		while (j.compareTo(bounds[1]) == -1) {
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
			if (j.mod(new BigInteger("3")).equals(BigInteger.ZERO)) {
				j = j.add(BigInteger.ONE);
				if (!numToBeFactored.isThreeEleminated()) {
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
			if (j.mod(new BigInteger("5")).equals(BigInteger.ZERO)) {
				j = j.add(BigInteger.ONE);
				if (!numToBeFactored.isFiveEleminated()) {
					continue;
				} else {
					if (numToBeFactored.getTargetNumber().remainder(j).equals(BigInteger.ZERO)) {
						String tempStr = "Factors: " + j + "*" + numToBeFactored.getTargetNumber().divide(j).toString();
						answer.add(tempStr);
						return answer;
					}
					continue;
				}
			} else {
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
