package srcs.workflow.server.distributed;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import srcs.workflow.executor.JobExecutorPluggable;
import srcs.workflow.executor.JobExecutorDistributed;
import srcs.workflow.job.Job;
import srcs.workflow.notifications.Notifiable;
import srcs.workflow.server.TaskHost;
import srcs.workflow.server.HostImpl;
import srcs.workflow.server.Master;

public class HostJobTracker extends HostImpl implements Master, TaskTrackerManager {
	
	protected List<TaskHost> taskTrackers = new ArrayList<>();
	protected Set<Submission> submissions = new HashSet<>();
	private int next = 0;

	@Override
	public Map<String, Object> submitJob(Notifiable client, Job job) throws RemoteException {
		if (taskTrackers.isEmpty()) {
			throw new RemoteException("No task tracker to exexcute this job");
		}
		Submission submission = new Submission(client, job);
		if (submissions.contains(submission)) {
			throw new RemoteException("A job with the same name is being processed");
		}
		submissions.add(submission);
		JobExecutorPluggable executor = new JobExecutorDistributed(job, this);
		return executeJob(executor, client, job);
	}
	
	public TaskHost nextTaskTracker() {
		synchronized (taskTrackers) {
			next = (next + 1) % taskTrackers.size();
			return taskTrackers.get(next);
		}
	}

	@Override
	public void registerTaskTracker(TaskHost newHost, String name, int maxTask) throws RemoteException {
		synchronized (taskTrackers) {
			// TODO: save name and maxTask
			taskTrackers.add(newHost);
		}
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
