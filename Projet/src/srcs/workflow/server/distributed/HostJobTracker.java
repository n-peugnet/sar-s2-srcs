package srcs.workflow.server.distributed;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import srcs.workflow.executor.JobExecutorPluggable;
import srcs.workflow.executor.JobExecutorDistributed;
import srcs.workflow.job.Job;
import srcs.workflow.notifications.Notifiable;
import srcs.workflow.server.TaskHost;
import srcs.workflow.server.HostImpl;
import srcs.workflow.server.Master;

public class HostJobTracker extends HostImpl implements Master, TaskExecutorManager {

	protected BlockingQueue<TaskExecutor> taskExecutors = new LinkedBlockingQueue<>();
	protected Set<Submission> submissions = new HashSet<>();

	@Override
	public Map<String, Object> submitJob(Notifiable client, Job job) throws RemoteException {
		Submission submission = new Submission(client, job);
		if (submissions.contains(submission)) {
			throw new RemoteException("A job with the same name is being processed");
		}
		submissions.add(submission);
		JobExecutorPluggable executor = new JobExecutorDistributed(job, this);
		return executeJob(executor, client, job);
	}
	
	@Override
	public TaskExecutor getTaskExecutor() throws InterruptedException {
		TaskExecutor executor = taskExecutors.take();
		if (executor.use()) {
			taskExecutors.add(executor);
		}
		return executor;
	}

	@Override
	public void putTaskExecutor(TaskExecutor executor) {
		if (executor.put()) {
			taskExecutors.add(executor);
		}
	}

	@Override
	public void registerTaskTracker(TaskHost host, String name, int maxTask) throws RemoteException {
		taskExecutors.add(new TaskExecutor(host, name, maxTask));
	}
	
	/**
	 * Classe représentant une soumission. Elle est identifiée par son job,
	 * lui même identifié par son name.
	 */
	protected static class Submission {
		public Notifiable client;
		public Job job;
		public Submission(Notifiable client, Job job) {
			this.client = client;
			this.job = job;
		}

		@Override
		public int hashCode() {
			return job.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this.getClass().isInstance(obj)) {
				Submission submission = this.getClass().cast(obj);
				return job.equals(submission.job);
			}
			return false;
		}
	}
}
