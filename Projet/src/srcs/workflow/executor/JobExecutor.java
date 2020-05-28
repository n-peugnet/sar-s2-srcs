package srcs.workflow.executor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import srcs.workflow.job.Context;
import srcs.workflow.job.Job;
import srcs.workflow.job.LinkFrom;
import srcs.workflow.job.ValidationException;

public abstract class JobExecutor {
	
	protected Job job;
	
	public JobExecutor(Job job) {
		this.job = job;
	}
	
	/**
	 * l’appel à cette méthode exécute le job et renvoie une map qui associe pour chaque tâche son résultat.
	 * @return
	 * @throws ValidationException si le job n'est pas valide.
	 * @throws Exception si l'execution a échoué.
	 */
	public abstract Map<String, Object> execute() throws ValidationException, Exception;
	
	/**
	 * execute un method selon une Map de resultats.
	 * @param results
	 * @param m
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	protected Object executeMethod(Map<String, Object> results, Method m) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<Object> args = new ArrayList<>();
		for(Parameter param : m.getParameters()) {
			if (param.isAnnotationPresent(Context.class)) {
				args.add(job.getContext().get(param.getAnnotation(Context.class).value()));
			} else if (param.isAnnotationPresent(LinkFrom.class)) {
				args.add(results.get(param.getAnnotation(LinkFrom.class).value()));
			}
		}
		return m.invoke(job, args.toArray());
	}

}
