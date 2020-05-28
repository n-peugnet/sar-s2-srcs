package srcs.workflow.job;

public class ValidationException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;
	
	public ValidationException(String message) {
		super(message);
	}

	public ValidationException(String message, Throwable causedBy) {
		super(message, causedBy);
	}

	public ValidationException(Throwable causedBy) {
		super(causedBy);
	}
}
