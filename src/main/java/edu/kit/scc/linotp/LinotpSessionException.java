package edu.kit.scc.linotp;

public class LinotpSessionException extends Exception {

	private static final long serialVersionUID = 1L;

	public LinotpSessionException() {
		super();
	}

	public LinotpSessionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public LinotpSessionException(String message) {
		super(message);
	}

	public LinotpSessionException(Throwable cause) {
		super(cause);
	}
}
