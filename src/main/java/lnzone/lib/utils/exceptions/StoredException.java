package lnzone.lib.utils.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lnzone.lib.utils.Config;

public class StoredException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(StoredException.class);
	
	private final static ExceptionManager exceptionManager = ExceptionManager.getInstance();
	
	private final long id;
	
	private final static boolean print = Config.getInstance().getEntryOrDefault("printExceptions", Boolean.class, true);

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
