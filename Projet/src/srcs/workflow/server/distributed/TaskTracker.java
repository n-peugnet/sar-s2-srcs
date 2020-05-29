package srcs.workflow.server.distributed;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import srcs.workflow.server.Host;
import srcs.workflow.server.HostImpl;
import srcs.workflow.server.Master;

public class TaskTracker {

	private static Host host;
	private static Registry registry;

	public static void main(String[] args) throws RemoteException, NotBoundException {
		host = new HostImpl();
		UnicastRemoteObject.exportObject(host, 0);
		registry = LocateRegistry.getRegistry();
		Master master = (Master) registry.lookup("host");
		master.registerTaskTracker(host);
	}
}
