package srcs.workflow.server.central;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;

import srcs.workflow.executor.JobExecutor;
import srcs.workflow.job.Job;
import srcs.workflow.job.ValidationException;

public class JobExecutorRemoteCentral extends JobExecutor {

	public JobExecutorRemoteCentral(Job job) {
		super(job);
	}

	@Override
	public Map<String, Object> execute() throws ValidationException, Exception {
		Registry registry = LocateRegistry.getRegistry();
		Host host = (Host) registry.lookup("host");
		return host.submitJob(job);
	}

}
