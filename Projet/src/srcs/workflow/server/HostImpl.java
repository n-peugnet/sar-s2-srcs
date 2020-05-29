package srcs.workflow.server;

import java.rmi.RemoteException;
import java.util.Map;

import srcs.workflow.executor.JobExecutorParallel;
import srcs.workflow.executor.JobExecutorPluggable;
import srcs.workflow.job.Job;
import srcs.workflow.job.ValidationException;
import srcs.workflow.notifications.Notifiable;

public class HostImpl implements JobHost {

	@Override
	public Map<String, Object> submitJob(Notifiable client, Job job) throws RemoteException {
		JobExecutorPluggable executor = new JobExecutorParallel(job);
		return executeJob(executor, client, job);
	}

	protected Map<String, Object> executeJob(JobExecutorPluggable executor, Notifiable client, Job job) throws RemoteException {
		try {
			return executor.execute(client);
		} catch (ValidationException e) {
			throw new RemoteException("Provided job is not valid", e);
		} catch (Exception e) {
			throw new RemoteException("The execution of this job failed", e);
		}
	}
}
