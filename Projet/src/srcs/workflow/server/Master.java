package srcs.workflow.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Master extends Remote {
	public void registerTaskTracker(TaskHost host, String name, int maxTask) throws RemoteException;
}
