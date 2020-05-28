package srcs.workflow.test;

import static org.junit.Assert.assertEquals;

import java.lang.management.ManagementFactory;
import java.util.Map;

import org.junit.Test;

import srcs.workflow.executor.JobExecutor;
import srcs.workflow.executor.JobExecutorSequential;

public class TestJobLocalSequential {

	@Test
	public void test() throws Exception {
		
		for(JobTest jobtest : JobTests.jobtests()) {
			JobForTest job = jobtest.getJob();
			job.reset();
			JobExecutor je = new JobExecutorSequential(job);
			Map<String,Object> res = je.execute();
			jobtest.check(res);
			
			
			Integer my_pid = Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
			assertEquals(1,job.getMappingTaskThread().values().stream().distinct().count());
			assertEquals(1,job.getMappingTaskPid().values().stream().distinct().count());
			assertEquals(my_pid, job.getMappingTaskPid().get("A"));
		}		
		
	}
}
