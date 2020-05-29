package srcs.workflow.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

import srcs.workflow.job.Job;
import srcs.workflow.notifications.Notifiable;

public interface JobHost extends Remote {
	public Map<String, Object> submitJob(Notifiable client, Job job) throws RemoteException;
}
