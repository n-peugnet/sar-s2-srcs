package srcs.workflow.server.central;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import srcs.workflow.job.Job;

public interface Host extends Remote {
	public Map<String, Object> submitJob(Job job) throws RemoteException;
}
