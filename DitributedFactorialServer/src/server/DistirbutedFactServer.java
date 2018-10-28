package server;

import java.math.BigInteger;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import commons.DistributedFact;
import commons.NumberObject;

/**
 * @author Ahmed Badr
 * 
 *         Title: Distributed Factorial Server 
 *         Date: 28.10.2018
 * 
 *         This class represents the Distributed Factorial JAVA RMI Server.
 */
public class DistirbutedFactServer extends UnicastRemoteObject {

	private static final long serialVersionUID = -4219545147136911429L;

	protected DistirbutedFactServer() throws RemoteException {
		super();
	}

	public static void main(String[] args) {
		System.setSecurityManager(new SecurityManager());
		try {
			Scanner inputFromUser = new Scanner(System.in); // Get input from the user "Target Number to be Factored"
			BigInteger number = new BigInteger(inputFromUser.nextLine());

			// Use the received string to construct a NumberObject.
			NumberObject numTobeFactored = new NumberObject(number);

			if (numTobeFactored.isTwoEleminated()) { // Check to find if the target number is multiple of 2,3 or 5.
				System.out.println("Number is Factored by 2");
				System.out.println("Result: 2 * " + (numTobeFactored.getTargetNumber().divide(new BigInteger("2"))));
			} else if (numTobeFactored.isThreeEleminated()) {
				System.out.println("Number is Factored by 3");
				System.out.println("Result: 2 * " + (numTobeFactored.getTargetNumber().divide(new BigInteger("3"))));
			} else if (numTobeFactored.isFiveEleminated()) {
				System.out.println("Number is Factored by 5");
				System.out.println("Result: 2 * " + (numTobeFactored.getTargetNumber().divide(new BigInteger("5"))));
			} else {
				DistributedFactImpl fi = new DistributedFactImpl(numTobeFactored);
				Naming.rebind(DistributedFact.SERVICENAME, fi);

				System.out.println("Published in RMI registery, ready...");
				System.out.println("Number to be Factored is: " + numTobeFactored.getTargetNumber().toString());
				System.out.println("Clients Can be Connected to the Server.");

				inputFromUser.close();
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
