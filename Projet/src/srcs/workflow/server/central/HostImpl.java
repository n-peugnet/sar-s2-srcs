package srcs.workflow.server.central;

import java.rmi.RemoteException;
import java.util.Map;

import srcs.workflow.executor.JobExecutor;
import srcs.workflow.executor.JobExecutorParallel;
import srcs.workflow.job.Job;
import srcs.workflow.job.ValidationException;

public class HostImpl implements Host {

	@Override
	public Map<String, Object> submitJob(Job job) throws RemoteException {
		JobExecutor executor;
		try {
			executor = new JobExecutorParallel(job);
			return executor.execute();
		} catch (ValidationException e) {
			throw new RemoteException("Provided job is not valid", e);
		} catch (Exception e) {
			throw new RemoteException("The execution of this job failed", e);
		}
	}

}
