package srcs.workflow.server.central;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class JobTrackerCentral {

	private static Host host;
	private static Registry registry;

	public static void main(String[] args) throws RemoteException, AlreadyBoundException {
		host = new HostImpl();
		registry = LocateRegistry.createRegistry(1099);
		UnicastRemoteObject.exportObject(host, 0);
		registry.bind("host", host);
	}
}
