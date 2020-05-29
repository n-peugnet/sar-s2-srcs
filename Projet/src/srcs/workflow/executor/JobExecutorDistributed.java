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
import srcs.workflow.notifications.NotifNull;
import srcs.workflow.notifications.Notifiable;
import srcs.workflow.server.TaskHost;
import srcs.workflow.server.distributed.TaskTrackerManager;

public class JobExecutorDistributed extends JobExecutor implements JobExecutorPluggable {

	TaskTrackerManager manager;

	public JobExecutorDistributed(Job job, TaskTrackerManager manager) throws ValidationException {
		super(job);
		this.manager = manager;
	}

	@Override
	public Map<String, Object> execute() throws ValidationException, Exception {
		return this.execute(new NotifNull());
	}

	public Map<String, Object> execute(Notifiable target) throws ValidationException, Exception {
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
					TaskHost host = manager.nextTaskTracker();
					Callable<Object> callable = new ExecutorThread(futureResults, job, jv.getMethod(task), host);
					futureResults.put(task, executor.submit(callable));
					i.remove();
				}
			}
		}
		int i = 0;
		for (String key : futureResults.keySet()) {
			i++;
			target.notify(i);
			results.put(key, futureResults.get(key).get());
		}
		return results;
	}
	
	protected static class ExecutorThread implements Callable<Object> {
		
		Map<String, Future<Object>> results;
		Job job;
		Method m;
		TaskHost host;
		
		public ExecutorThread(Map<String, Future<Object>> results, Job job, Method m, TaskHost host) {
			this.results = results;
			this.job = job;
			this.m = m;
			this.host = host;
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
			return host.submitTask(job, m.getName(), args.toArray());
		}
		
	}

}
