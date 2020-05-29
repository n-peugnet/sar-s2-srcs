package srcs.workflow.server.central;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

import srcs.workflow.executor.JobExecutor;
import srcs.workflow.job.Job;
import srcs.workflow.job.ValidationException;
import srcs.workflow.notifications.NotifStdout;
import srcs.workflow.notifications.Notifiable;

public class JobExecutorRemoteCentral extends JobExecutor {

	public JobExecutorRemoteCentral(Job job) {
		super(job);
	}

	@Override
	public Map<String, Object> execute() throws ValidationException, Exception {
		Registry registry = LocateRegistry.getRegistry();
		Notifiable client = new NotifStdout();
		UnicastRemoteObject.exportObject(client, 0);
		Host host = (Host) registry.lookup("host");
		return host.submitJob(client, job);
	}

}
