package edu.kit.scc.linotp;

public class LinotpLoginException extends Exception {

	private static final long serialVersionUID = 1L;

	public LinotpLoginException() {
		super();
	}

	public LinotpLoginException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public LinotpLoginException(String message) {
		super(message);
	}

	public LinotpLoginException(Throwable cause) {
		super(cause);
	}
}
