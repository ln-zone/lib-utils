package bittech.lib.utils.exceptions;

import java.io.Serializable;

public class ExceptionInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private long id = 0;
	private String type;
	private String message;
	private ExceptionInfo cause;
	private StackTraceElement[] stackTrace;

	public ExceptionInfo(StoredException ex) {
		this.setId(ex.getId());
		this.setType(ex.getClass().getCanonicalName());
		this.setMessage(ex.getMessage());
		this.setStackTrace(ex.getStackTrace());
		this.setCause(convert(ex.getCause()));
	}

	private ExceptionInfo() {

	}

	private static ExceptionInfo convert(Throwable cause) {
		if (cause == null) {
			return null;
		}
		ExceptionInfo inf = new ExceptionInfo();
		inf.setType(cause.getClass().getCanonicalName());
		inf.setMessage(cause.getMessage());
		inf.setStackTrace(cause.getStackTrace());
		inf.setCause(convert(cause.getCause()));
		return inf;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public ExceptionInfo getCause() {
		return cause;
	}

	public void setCause(ExceptionInfo cause) {
		this.cause = cause;
	}

	public StackTraceElement[] getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(StackTraceElement[] stackTrace) {
		this.stackTrace = stackTrace;
	}

}
