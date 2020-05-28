package srcs.workflow.job;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import srcs.workflow.graph.Graph;
import srcs.workflow.graph.GraphImpl;

public class JobValidator {

	protected Job job;
	protected Map<String, Method> tasks;
	protected Graph<String> taskGraph;

	public JobValidator(Job job) throws ValidationException {
		this.job = job;
		this.tasks = extractMethods(job);
		this.taskGraph = buildGraph(tasks, job);
		if (!taskGraph.isDAG()) {
			throw new ValidationException("Tasks graph must be acyclic");
		}
	}
	
	/**
	 * fabrique une Map de tasks à partir d'un Job.
	 * @param job le job dont on veut extrair les tasks.
	 * @return le Map de tasks.
	 * @throws ValidationException si le job n'est pas valide.
	 */
	protected Map<String, Method> extractMethods(Job job) throws ValidationException {
		Map<String, Method> tasks = new HashMap<>();
		int taskCount = 0;
		for (Method m : job.getClass().getDeclaredMethods()) {
			if (!m.isAnnotationPresent(Task.class)) {
				continue;
			}
			Task task = m.getAnnotation(Task.class);
			taskCount++;
			if ((m.getModifiers() & Modifier.STATIC) != 0) {
				throw new ValidationException("Tasks cannot be class methods");
			}
			if (m.getReturnType().equals(Void.TYPE)) {
				throw new ValidationException("Tasks cannot return void");
			}
			if (tasks.containsKey(task.value())) {
				throw new ValidationException("Two tasks cannot have the same id");
			}
			tasks.put(task.value(), m);
		}
		if (taskCount == 0) {
			throw new ValidationException("No tasks defined");
		}
		return tasks;
	}
	
	/**
	 * fabrique un Graph à partir d'une Map de tasks et d'un job.
	 * @param tasks
	 * @param job
	 * @return
	 * @throws ValidationException si la Map des tasks n'est pas cohérente.
	 */
	protected Graph<String> buildGraph(Map<String, Method> tasks, Job job) throws ValidationException {
		Graph<String> taskGraph = new GraphImpl<>();
		for (String id : tasks.keySet()) {
			taskGraph.addNode(id);
		}
		for (String id : tasks.keySet()) {
			Method m = tasks.get(id);
			for (Parameter p : m.getParameters()) {
				if (p.isAnnotationPresent(Context.class)) {
					String ref = p.getAnnotation(Context.class).value();
					if (!job.getContext().containsKey(ref)) {
						throw new ValidationException("No such context value " + ref);
					}
					if (!p.getType().isAssignableFrom(job.getContext().get(ref).getClass())) {
						throw new ValidationException("Incompatible type of field " + ref);
					}
				} else if (p.isAnnotationPresent(LinkFrom.class)) {
					String ref = p.getAnnotation(LinkFrom.class).value();
					if (!tasks.containsKey(ref)) {
						throw new ValidationException("No such task " + ref);
					}
					if (!p.getType().isAssignableFrom(tasks.get(ref).getReturnType())) {
						throw new ValidationException("Incompatible return type of task " + ref);
					}
					taskGraph.addEdge(ref, id);
				} else {
					throw new ValidationException("Context or LinkFrom annotation is not present");
				}
			}
		}
		return taskGraph;
	}

	public Graph<String> getTaskGraph() {
		return taskGraph;
	}
	
	public Method getMethod(String id) throws IllegalArgumentException {
		if (!tasks.containsKey(id) ) {
			throw new IllegalArgumentException("No such method " + id);
		}
		return tasks.get(id);
	}
	
	public Job getJob() {
		return job;
	}
}
