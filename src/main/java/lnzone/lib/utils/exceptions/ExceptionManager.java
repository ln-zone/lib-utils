package lnzone.lib.utils.exceptions;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionManager {

	private final Logger LOGGER = LoggerFactory.getLogger(ExceptionManager.class);

	private static final ExceptionManager instance = new ExceptionManager();

	private Map<Long, ExceptionInfo> exceptions = new ConcurrentHashMap<Long, ExceptionInfo>();

	private ExceptionManager() {
		// TODO Auto-generated constructor stub
	}

	public static ExceptionManager getInstance() {
		return instance;
	}

	public synchronized void deleteAll() {
		exceptions.clear();
	}
	
	public Collection<Long> getExceptionIds() {
		return exceptions.keySet();
	}
	
	public synchronized long add(StoredException exception) {
		long exceptionId;
		if (exception.getCause() instanceof StoredException) {
			exceptionId = ((StoredException) exception.getCause()).getId();
		} else {
			do {
				exceptionId = (long) (Math.random() * Long.MAX_VALUE);
			} while (exceptions.containsKey(exceptionId));
		}
		exceptions.put(exceptionId, new ExceptionInfo(exception));
		LOGGER.debug("Exception with id " + exceptionId + " created");
		return exceptionId;
	}

	public synchronized ExceptionInfo get(long exceptionId) {
		return exceptions.get(exceptionId);
	}

//	public String getAsString(long exceptionId) {
//		Exception ex = exceptions.get(exceptionId);
//		if (ex == null) {
//			return null;
//		}
//		StringWriter sw = new StringWriter();
//		ex.printStackTrace(new PrintWriter(sw));
//		return sw.toString();
//	}

}
