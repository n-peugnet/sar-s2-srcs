package srcs.workflow.executor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import srcs.workflow.graph.Graph;
import srcs.workflow.job.Job;
import srcs.workflow.job.JobValidator;

public class JobExecutorSequential extends JobExecutor {

	public JobExecutorSequential(Job job) {
		super(job);
	}

	@Override
	public Map<String, Object> execute() throws Exception {
		Map<String, Object> results = new HashMap<>();
		JobValidator jv = new JobValidator(job);
		Graph<String> taskGraph = jv.getTaskGraph();
		List<String> tasks = StreamSupport.stream(taskGraph.spliterator(), false).collect(Collectors.toList());
		while (!tasks.isEmpty()) {
			Iterator<String> i = tasks.iterator();
			while (i.hasNext()) {
				String task = i.next();
				if (taskGraph.getNeighborsIn(task).stream().allMatch(id -> results.containsKey(id))) {
					results.put(task, executeMethod(results, jv.getMethod(task)));
					i.remove();
				}
			}
		}
		return results;
	}

}
