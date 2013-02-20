package jp.thisptr.optimizer;

public class NotConvergedException extends RuntimeException {
	private static final long serialVersionUID = -1130752441276033236L;

	public NotConvergedException() {
		super();
	}

	public NotConvergedException(final String message) {
		super(message);
	}

	public NotConvergedException(final Throwable cause) {
		super(cause);
	}
}
