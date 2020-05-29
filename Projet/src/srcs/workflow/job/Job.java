package srcs.workflow.job;

import java.io.Serializable;
import java.util.Map;


public abstract class Job implements Serializable {

	/** Version */
	private static final long serialVersionUID = 1L;

	private final String name;
	private final Map<String, Object> context;
	
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
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this.getClass().isInstance(obj)) {
			Job job = this.getClass().cast(obj);
			return name.equals(job.name);
		}
		return false;
	}
}
