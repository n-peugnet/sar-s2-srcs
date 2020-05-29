package srcs.workflow.executor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import srcs.workflow.graph.Graph;
import srcs.workflow.job.Job;
import srcs.workflow.job.JobValidator;
import srcs.workflow.job.ValidationException;

public class JobExecutorParallel extends JobExecutor {

	public JobExecutorParallel(Job job) {
		super(job);
	}

	@Override
	public Map<String, Object> execute() throws ValidationException, Exception {
		Map<String, Object> results = new ConcurrentHashMap<>();
		JobValidator jv = new JobValidator(job);
		Graph<String> taskGraph = jv.getTaskGraph();
		List<Thread> threads = new ArrayList<>();
		List<String> tasks = StreamSupport.stream(taskGraph.spliterator(), false).collect(Collectors.toList());
		while (!tasks.isEmpty()) {
			Iterator<String> i = tasks.iterator();
			while (i.hasNext()) {
				String task = i.next();
				if (taskGraph.getNeighborsIn(task).stream().allMatch(id -> results.containsKey(id))) {
					Thread t = new Thread(() -> {
						try {
							results.put(task, executeMethod(results, jv.getMethod(task)));
						} catch (ReflectiveOperationException e) {
							e.printStackTrace();
						}
					});
					threads.add(t);
					t.start();
					i.remove();
				}
			}
		}
		for (Thread t : threads) {
			t.join();
		}
		return results;
	}

}
