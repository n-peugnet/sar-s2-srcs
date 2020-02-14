package srcs.persistance;

@SuppressWarnings("serial")
public class PersitanceException extends RuntimeException {
	public PersitanceException(Throwable e) {
		initCause(e);
	}
}
