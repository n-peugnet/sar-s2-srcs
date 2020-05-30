package srcs.workflow.server.distributed;

public interface TaskExecutorManager {
	public TaskExecutor getTaskExecutor() throws InterruptedException;
	public void putTaskExecutor(TaskExecutor executor);
}