package srcs.workflow.server.distributed;

import srcs.workflow.server.TaskHost;

public interface TaskTrackerManager {
	public TaskHost nextTaskTracker();
}