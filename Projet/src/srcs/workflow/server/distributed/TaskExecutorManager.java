package srcs.workflow.server.distributed;

public interface TaskExecutorManager {

	/**
	 * Demande au manager un TaskExecutor disponible.
	 * 
	 * Cette demande est bloquante tant qu'uncun n'est disponible.
	 * @return un executeur de Task.
	 * @throws InterruptedException
	 */
	public TaskExecutor getTaskExecutor() throws InterruptedException;

	/**
	 * Rend un TaskExecutor au manager.
	 */
	public void putTaskExecutor(TaskExecutor executor);
	

	/**
	 * Reporte un TaskExecutor comme étant fautif.
	 */
	public void reportTaskExecutor(TaskExecutor executor);
}