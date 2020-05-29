package srcs.workflow.executor;

import java.util.Map;

import srcs.workflow.job.ValidationException;
import srcs.workflow.notifications.Notifiable;

public interface JobExecutorPluggable {
	public Map<String, Object> execute(Notifiable target) throws ValidationException, Exception;
}
