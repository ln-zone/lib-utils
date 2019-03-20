package bittech.lib.utils.exceptions;

import java.io.Serializable;

public class ExceptionInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	long id = 0;
	String type;
	String message;
	ExceptionInfo cause;
	StackTraceElement[] stackTrace;

	public ExceptionInfo(StoredException ex) {
		this.id = ex.getId();
		this.type = ex.getClass().getCanonicalName();
		this.message = ex.getMessage();
		this.stackTrace = ex.getStackTrace();
		this.cause = convert(ex.getCause());
	}
	
	private ExceptionInfo() {
		
	}
	
	private static ExceptionInfo convert(Throwable cause) {
		if(cause == null) {
			return null;
		}
		ExceptionInfo inf = new ExceptionInfo();
		inf.type = cause.getClass().getCanonicalName();
		inf.message = cause.getMessage();
		inf.stackTrace = cause.getStackTrace();
		inf.cause = convert(cause.getCause());
		return inf;
	}

}
