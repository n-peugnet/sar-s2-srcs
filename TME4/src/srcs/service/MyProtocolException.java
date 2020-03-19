package srcs.service;

public class MyProtocolException extends RuntimeException {

	/**
	 * Version
	 */
	private static final long serialVersionUID = 1L;

	public MyProtocolException() {
		super();
	}

	public MyProtocolException(Throwable e) {
		super(e);
	}
	
	public MyProtocolException(String msg) {
		super(msg);
	}
	
	public MyProtocolException(String msg, Throwable e) {
		super(msg, e);
	}

}
