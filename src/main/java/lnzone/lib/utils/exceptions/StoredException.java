package lnzone.lib.utils.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StoredException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(StoredException.class);
	
	private final static ExceptionManager exceptionManager = ExceptionManager.getInstance();
	
	private final long id;
	
	private final boolean print = false;

	public StoredException(String message) {
		super(message);
		id = exceptionManager.add(this);
		LOGGER.debug("Exception created with id " + id +" : " + message);
		if(print) {
			this.printStackTrace();
		}
	}

	public StoredException(String message, Throwable cause) {
		super(message, cause);
		id = exceptionManager.add(this);
		LOGGER.debug("Exception created with id " + id +" : " + message);
		if(print) {
			this.printStackTrace();
		}
	}
	
	public long getId() {
		return id;
	}



}
