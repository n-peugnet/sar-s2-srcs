package srcs.workflow.server.distributed;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import srcs.workflow.server.Master;
import srcs.workflow.server.TaskHost;

public class TaskTracker {

	private static TaskHost host;
	private static Registry registry;

	public static void main(String[] args) throws RemoteException, NotBoundException {
		String name = args[0];
		int maxTask = Integer.valueOf(args[1]);
		host = new HostTaskTracker();
		UnicastRemoteObject.exportObject(host, 0);
		registry = LocateRegistry.getRegistry();
		Master master = (Master) registry.lookup("host");
		master.registerTaskTracker(host, name, maxTask);
	}
}
