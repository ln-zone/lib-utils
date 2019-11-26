package bittech.lib.utils.exceptions;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import bittech.lib.utils.LoopThread;
import bittech.lib.utils.logs.Log;

public class ExceptionsToLogsConverters implements AutoCloseable {

	private final List<StoredException> propositions = new LinkedList<StoredException>();
	private final LoopThread LoopThread;

	private final List<ExceptionToLogConverter> converters = new LinkedList<ExceptionToLogConverter>();

	public ExceptionsToLogsConverters() {
		LoopThread = new LoopThread(100, 3000) {

			@Override
			public void action() {
				review();
			}

		};
	}

	public synchronized void review() {
		long currentTime = System.currentTimeMillis();
		Iterator<StoredException> it = propositions.iterator();
		while (it.hasNext()) {
			StoredException ex = it.next();
			if (ex.getTimestamp() + 1000 < currentTime) {
				it.remove();
				if (ExceptionManager.getInstance().contains(ex.getId())) {
					exceptionToLog(ex);
				}
			}
		}
	}

	public void exceptionToLog(StoredException storedException) {
		for (ExceptionToLogConverter exToLog : converters) {
			if (exToLog.convert(storedException)) {
				return;
			}
		}

		Log log = Log.build();
		log.setSeverity(Log.Severity.Error);
		log.setInspectNeeded(true);
		log.param("ID wyjątku", storedException.getId());
		log.param("przyczyny", storedException.listReasons());
		log.param("tekst wyjątku", storedException.getMessage());
		log.event(storedException.getMessage());
	}

	public synchronized void push(StoredException ex) {
		propositions.add(ex);
	}

	@Override
	public void close() {
		LoopThread.close();
	}

	public void registerConverter(ExceptionToLogConverter converter) {
		converters.add(converter);
	}

}
