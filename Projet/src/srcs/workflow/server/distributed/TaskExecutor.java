package srcs.workflow.server.distributed;

import java.rmi.RemoteException;

import srcs.workflow.job.Job;
import srcs.workflow.server.TaskHost;

/**
 * Classe représentant un executeur de task. Elle est identifiée par son name.
 */
public class TaskExecutor implements TaskHost {

	protected TaskHost host;
	protected String name;
	protected int maxTask;
	protected Integer nbTask = 0;

	public TaskExecutor(TaskHost host, String name, int maxTask) {
		this.host = host;
		this.name = name;
		this.maxTask = maxTask;
	}
	
	public String getName() {
		return name;
	}

	public int getMaxTask() {
		return maxTask;
	}
	
	/**
	 * Utilise une des tasks disponibles de ce TaskExecutor.
	 * @return vrai si il reste des tasks disponibles.
	 */
	boolean use() {
		synchronized (nbTask) {
			nbTask++;
			return nbTask < maxTask;
		}
	}

	/**
	 * Rend une des tasks de ce TaskExecutor disponible.
	 * @return vrai si aucune task n'était disponible avant cette opération.
	 */
	boolean put() {
		synchronized (nbTask) {
			nbTask--;
			return nbTask == maxTask - 1;
		}
	}

	@Override
	public Object submitTask(Job job, String method, Object[] args) throws RemoteException {
		return host.submitTask(job, method, args);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this.getClass().isInstance(obj)) {
			TaskExecutor host = this.getClass().cast(obj);
			return name.equals(host.name);
		}
		return false;
	}
}