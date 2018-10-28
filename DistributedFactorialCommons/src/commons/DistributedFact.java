package commons;

import java.math.BigInteger;
import java.rmi.*;
import java.util.ArrayList;

/**
 * @author Ahmed Badr
 * 
 *         Title: Distributed Factorial JAVA RMI Server Interfaces.
 *         Date: 28.10.2018
 * 
 *         This Interface contains all the methods offered by the Distributed
 *         Factorial JAVA RMI Server to the participating Clients.
 */
public interface DistributedFact extends Remote {

	public final static String SERVICENAME = "DistFactService";

	/**
	 * Method used by the clients to verify their availability for receiving jobs
	 * from the server.
	 * 
	 * @param clientID
	 * @return Connection Status
	 * @throws RemoteException
	 */
	public boolean checkConnection(int clientID) throws RemoteException;

	/**
	 * Method used by the clients to request unique ID from the Server.
	 * 
	 * @return Client ID
	 * @throws RemoteException
	 */
	public int askForID() throws RemoteException;

	/**
	 * Method used by the clients to request the number to be factored from the
	 * Server.
	 * 
	 * @return BigInteger Target Number to be Factored
	 * @throws RemoteException
	 */
	public NumberObject askForTheNumber() throws RemoteException;

	/**
	 * Method used by the clients to ask the Server for a new number space.
	 * 
	 * @param clientID
	 * @return The next range in form of BigInteger[] of two cells
	 *         [lowerBound,upperBound].
	 * @throws RemoteException
	 */
	public BigInteger[] askForRange(int clientID) throws RemoteException;

	/**
	 * Method used by the clients to inform the Server that the factors can't be
	 * found in the currently assigned number space.
	 * 
	 * @param clientID
	 * @throws RemoteException
	 */
	public void factorialNotFound(int clientID) throws RemoteException;

	/**
	 * Method used by the clients to submit the results of finding the factors of
	 * the target number.
	 * 
	 * @param Results {Factors found + Time Elapsed or Factors not Found}
	 * @throws RemoteException
	 */
	public void submitAnswer(ArrayList<String> results) throws RemoteException;
}