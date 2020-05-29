package srcs.workflow.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

import srcs.workflow.job.Job;

public interface TaskHost extends Remote {
	public Object submitTask(Job job, String method, Object[] args) throws RemoteException;
}
