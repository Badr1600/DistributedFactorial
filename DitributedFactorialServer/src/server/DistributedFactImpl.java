package server;

import java.math.BigInteger;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import commons.DistributedFact;
import commons.NumberObject;

/**
 * @author Ahmed Badr
 * 
 *         Title: Distributed Factorial Implementation Date: 28.10.2018
 * 
 *         This class contains the implementation of the Distributed Factorial
 *         JAVA RMI interfaces.
 * 
 */
public class DistributedFactImpl extends UnicastRemoteObject implements DistributedFact {

	private static final long serialVersionUID = -4408845922252630444L;

	private static int CLINET_IDS = 0;

	private static Long startTime = System.nanoTime();

	/*
	 * HashMap<ClientID, TimeStamp> for keep tracking last time each client checked
	 * in.
	 */
	private HashMap<Integer, Long> clientsConnecions;
	/*
	 * HashMap<ClientID, Boolean> for keep tracking of clients status
	 * connected/disconnected
	 */
	private HashMap<Integer, Boolean> clientsAvailable;

	/*
	 * HashMap for tracking the number spaces assigned to each client.
	 */
	private ArrayList<RangeHelper> rangeBacketsHelper;

	private NumberObject numToBeFactored;
	private boolean jobFinished;
	private Timer priodicClientConnectionTimer;

	/**
	 * Class Constructor.
	 * 
	 * @param numToBeFactored
	 * @throws RemoteException
	 */
	public DistributedFactImpl(NumberObject numToBeFactored) throws RemoteException {
		super();
		this.numToBeFactored = numToBeFactored;
		this.clientsConnecions = new HashMap<>();
		this.clientsAvailable = new HashMap<>();
		this.rangeBacketsHelper = new ArrayList<>();
		this.jobFinished = false;
		this.priodicClientConnectionTimer = new Timer();
		rangeBacketsHelper.add(new RangeHelper(0, false, numToBeFactored.getNextRange()));

		/*
		 * Periodic timer to keep track of the online connected clients to the server
		 * and keep track of the subset ranges sent to the clients.
		 * 
		 * And also reduce memory usage by removing the used subset ranges (with out
		 * finding factors within)
		 */
		priodicClientConnectionTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				Long currecntTime = System.nanoTime();
				Collection<Integer> clientsConnecionsKey = clientsConnecions.keySet();
				Object[] keyObjects = clientsConnecionsKey.toArray();
				for (int i = 0; i < keyObjects.length; i++) {
					Long clientLastCheckIn = clientsConnecions.get((int) keyObjects[i]);
					Long timeElapsedLastCheck = (currecntTime - clientLastCheckIn) / 1000000; // time in millisecond
					if (timeElapsedLastCheck > 5000) {
						System.out.println("Client: " + (int) keyObjects[i] + " Disconnected!");
						System.out.println("Number of Connected Clients: " + keyObjects.length);
						clientsAvailable.remove((int) keyObjects[i]);
						clientsConnecions.remove((int) keyObjects[i]);
						for (int j = 0; j < rangeBacketsHelper.size(); j++) {
							RangeHelper rangeHelperObject = rangeBacketsHelper.get(j);
							int tempClientID = rangeHelperObject.getClientID();
							if (tempClientID == (int) keyObjects[i]) {
								rangeHelperObject.setClientID(0);
								rangeHelperObject.setAssigned(false);
								rangeHelperObject.setRangeComplete(false);
								rangeBacketsHelper.set(j, rangeHelperObject);
							}
						}
					}
				}
			}
		}, 0, 1000);
	}

	@Override
	public boolean checkConnection(int clientID) throws RemoteException {
		Long currecntTime = System.nanoTime();
		clientsConnecions.put(clientID, currecntTime);
		clientsAvailable.put(clientID, true);
		if (jobFinished) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void submitAnswer(ArrayList<String> answer) throws RemoteException {
		for (int i = 0; i < answer.size(); i++) {
			System.out.println(answer.get(i));
		}
		jobFinished = true;
		priodicClientConnectionTimer.cancel();
		Long endTime = System.nanoTime();
		System.out.println("Total Time Elabsed by all clients: " + (endTime - startTime) / 1000000000 + " sec");
	}

	@Override
	public int askForID() throws RemoteException {
		while (true) {
			CLINET_IDS++;
			int clientID = CLINET_IDS;
			if (clientsConnecions.get(clientID) == null) {
				clientsConnecions.put(clientID, System.nanoTime());
				clientsAvailable.put(clientID, true);
				BigInteger[] range = numToBeFactored.getNextRange();
				rangeBacketsHelper.add(new RangeHelper(0, false, range));
				System.out.println("Client: " + clientID + " is Connected!");
				return clientID;
			}
		}
	}

	@Override
	public BigInteger[] askForRange(int clientID) throws RemoteException {
		BigInteger[] range = numToBeFactored.getNextRange();
		rangeBacketsHelper.add(new RangeHelper(0, false, range));
		for (int i = 0; i < rangeBacketsHelper.size(); i++) {
			RangeHelper rangeHelperObject = rangeBacketsHelper.get(i);
			int tempClientID = rangeHelperObject.getClientID();
			if (tempClientID == 0) {
				rangeHelperObject.setClientID(clientID);
				rangeHelperObject.setAssigned(true);
				rangeBacketsHelper.set(i, rangeHelperObject);
				return rangeHelperObject.getRange();
			}
		}
		return null;
	}

	@Override
	public void factorialNotFound(int clientID) throws RemoteException {
		for (int i = 0; i < rangeBacketsHelper.size(); i++) {
			RangeHelper rangeHelperObject = rangeBacketsHelper.get(i);
			int tempClientID = rangeHelperObject.getClientID();
			if (tempClientID == clientID && rangeHelperObject.isAssigned()) {
				rangeHelperObject.setRangeComplete(true);
				rangeBacketsHelper.remove(i);
				break;
			}
		}
	}

	@Override
	public NumberObject askForTheNumber() throws RemoteException {
		return numToBeFactored;
	}

	/**
	 * @author Ahmed Badr
	 * 
	 *         Title: RangeHelper Date: 28.10.2018
	 * 
	 *         Private Helper Class for handling the ranges of the number space sent
	 *         to the clients.
	 * 
	 */
	private class RangeHelper {
		private int clientID;
		private boolean assigned; // to check if the range is assigned to any client yet.
		private boolean rangeComplete; // to check if the range is completed in search by the client.
		private BigInteger[] range; // subset of the number space

		/**
		 * Class Constructor.
		 * 
		 * @param clientID
		 * @param status
		 * @param range
		 */
		public RangeHelper(int clientID, boolean status, BigInteger[] range) {
			this.clientID = clientID;
			this.assigned = status;
			this.range = range;
			this.rangeComplete = false;
		}

		/**
		 * @return the clientID
		 */
		public int getClientID() {
			return clientID;
		}

		/**
		 * @return the range
		 */
		public BigInteger[] getRange() {
			return range;
		}

		/**
		 * @param clientID the clientID to set
		 */
		public void setClientID(int clientID) {
			this.clientID = clientID;
		}

		/**
		 * @return the status
		 */
		public boolean isAssigned() {
			return assigned;
		}

		/**
		 * @param status the status to set
		 */
		public void setAssigned(boolean status) {
			this.assigned = status;
		}

		/**
		 * @return the rangeComplete
		 */
		@SuppressWarnings("unused")
		public boolean isRangeComplete() {
			return rangeComplete;
		}

		/**
		 * @param rangeComplete the rangeComplete to set
		 */
		public void setRangeComplete(boolean rangeComplete) {
			this.rangeComplete = rangeComplete;
		}

	}
}