package srcs.workflow.executor;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import srcs.workflow.graph.Graph;
import srcs.workflow.job.Context;
import srcs.workflow.job.Job;
import srcs.workflow.job.JobValidator;
import srcs.workflow.job.LinkFrom;
import srcs.workflow.job.ValidationException;

public class JobExecutorParallel extends JobExecutor {

	public JobExecutorParallel(Job job) throws ValidationException {
		super(job);
	}

	@Override
	public Map<String, Object> execute() throws ValidationException, Exception {
		Map<String, Future<Object>> futureResults = new ConcurrentHashMap<>();
		Map<String, Object> results = new HashMap<>();
		JobValidator jv = new JobValidator(job);
		Graph<String> taskGraph = jv.getTaskGraph();
		ExecutorService executor = Executors.newCachedThreadPool();
		List<String> tasks = StreamSupport.stream(taskGraph.spliterator(), false).collect(Collectors.toList());

		while (!tasks.isEmpty()) {
			Iterator<String> i = tasks.iterator();
			while (i.hasNext()) {
				String task = i.next();
				if (taskGraph.getNeighborsIn(task).stream().allMatch(id -> futureResults.containsKey(id))) {
					Callable<Object> callable = new ExecutorThread(futureResults, job, jv.getMethod(task));
					futureResults.put(task, executor.submit(callable));
					i.remove();
				}
			}
		}
		for (String key : futureResults.keySet()) {
			results.put(key, futureResults.get(key).get());
		}
		return results;
	}
	
	protected static class ExecutorThread implements Callable<Object> {
		
		Map<String, Future<Object>> results;
		Job job;
		Method m;
		
		public ExecutorThread(Map<String, Future<Object>> results, Job job, Method m) {
			this.results = results;
			this.job = job;
			this.m = m;
		}

		@Override
		public Object call() throws Exception {
			List<Object> args = new ArrayList<>();
			for(Parameter param : m.getParameters()) {
				if (param.isAnnotationPresent(Context.class)) {
					args.add(job.getContext().get(param.getAnnotation(Context.class).value()));
				} else if (param.isAnnotationPresent(LinkFrom.class)) {
					args.add(results.get(param.getAnnotation(LinkFrom.class).value()).get());
				}
			}
			return m.invoke(job, args.toArray());
		}
		
	}

}
