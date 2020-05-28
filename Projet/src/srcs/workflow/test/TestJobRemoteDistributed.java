package srcs.workflow.test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import srcs.workflow.executor.JobExecutor;
import srcs.workflow.server.distributed.JobExecutorRemoteDistributed;
import srcs.workflow.server.distributed.JobTrackerMaster;
import srcs.workflow.server.distributed.TaskTracker;

public class TestJobRemoteDistributed extends SystemDeployer{

	
	
	
	
	public TestJobRemoteDistributed() {
		this.name_class_jobtracker = JobTrackerMaster.class.getCanonicalName();
		this.name_class_tasktracker = TaskTracker.class.getCanonicalName();
		this.nb_tasktracker=3;
	}
	
		
	
	@Test
	public void test() throws Exception {
		
		for(JobTest jobtest: JobTests.jobtests()) {
			JobForTest job = jobtest.getJob();
			job.reset();
			JobExecutor je = new JobExecutorRemoteDistributed(job);
			Map<String,Object> res = je.execute();
			jobtest.check(res);
				
			Integer my_pid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
			//on s'assure que le nombre de processus impliqués dans l'exécution est > 1
			
			
			assertNotEquals(1,job.getMappingTaskPid().values().stream().distinct().count());
			//on s'assure que processus qui ont exécuté les taches sont bien les tasktrackers
			assertNotEquals(my_pid,job.getMappingTaskPid().get("A"));
			
			Set<Long> pidtask_trackers = Stream.of(processesTaskTracker)
					 .map( p -> Long.valueOf(getPidOfProcess(p))).collect(Collectors.toSet());
			
			for(Integer pid_task : job.getMappingTaskPid().values() ) {
				assertTrue(pidtask_trackers.contains(pid_task.longValue()));
			}
		}
		
	}
	
}
