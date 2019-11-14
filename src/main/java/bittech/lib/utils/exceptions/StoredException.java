package bittech.lib.utils.exceptions;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bittech.lib.utils.Config;

public class StoredException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(StoredException.class);
	
	private final static ExceptionManager exceptionManager = ExceptionManager.getInstance();
	
	private final long id;
	
	private final long timestamp;
	
	private final static boolean print = Config.getInstance().getEntryOrDefault("printExceptions", Boolean.class, true);
	
	public StoredException(String message, Throwable cause) {
		super(message, cause);
		id = exceptionManager.add(this);
		timestamp = System.currentTimeMillis();
		LOGGER.debug("Exception created with id " + id +" : " + message);
		if(print) {
			this.printStackTrace();
		}

	}
	
	public long getId() {
		return id;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public List<String> listReasons() {
		List<String> reasons = new LinkedList<String>();
		Throwable myEx = this.getCause();
		while(myEx != null) {
			reasons.add(myEx.getMessage());
			myEx = myEx.getCause();
		}
		return reasons;
	}



}
