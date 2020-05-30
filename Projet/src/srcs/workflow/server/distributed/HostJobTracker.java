package srcs.workflow.server.distributed;

import java.rmi.RemoteException;
import java.util.Map;
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

	@Override
	public Map<String, Object> submitJob(Notifiable target, Job job) throws RemoteException {
		JobExecutorPluggable executor = new JobExecutorDistributed(job, this);
		return executeJob(executor, target, job);
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
	public void reportTaskExecutor(TaskExecutor executor) {
		boolean remained = true;
		while (remained == true) {
			remained = taskExecutors.remove(executor);
		}
	}

	@Override
	public void registerTaskTracker(TaskHost host, String name, int maxTask) throws RemoteException {
		taskExecutors.add(new TaskExecutor(host, name, maxTask));
	}
}
