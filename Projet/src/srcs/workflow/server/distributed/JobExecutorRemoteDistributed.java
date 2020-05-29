package srcs.workflow.server.distributed;


import srcs.workflow.job.Job;
import srcs.workflow.server.JobExecutorRemote;

public class JobExecutorRemoteDistributed extends JobExecutorRemote {

	public JobExecutorRemoteDistributed(Job job) {
		super(job);
	}

}