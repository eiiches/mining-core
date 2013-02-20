package jp.thisptr.structure.dataframe;

public class MissingValueException extends RuntimeException {
	private static final long serialVersionUID = 4061812400536592955L;

	public MissingValueException() {
		super();
	}

	public MissingValueException(final String message) {
		super(message);
	}
}
