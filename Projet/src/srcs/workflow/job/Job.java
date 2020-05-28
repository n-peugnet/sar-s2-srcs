package srcs.workflow.job;

import java.util.Map;

public abstract class Job {

	protected final String name;
	protected final Map<String, Object> context;
	
	public Job(String name, Map<String, Object> context) {
		this.name = name;
		this.context = context;
	}
	
	public String getName() {
		return name;
	}
	
	public Map<String, Object> getContext() {
		return context;
	}
}
