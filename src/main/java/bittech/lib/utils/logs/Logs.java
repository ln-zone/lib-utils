package bittech.lib.utils.logs;

import bittech.lib.utils.*;
import bittech.lib.utils.json.JsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Logs implements AutoCloseable {

	private final static Logger LOGGER = LoggerFactory.getLogger(Logs.class);

	private boolean initialized = false;

	private final static Logs instance = new Logs();

	private boolean printLogs;

	private Timer deleteOldTimer;

	private long logLifeTimeMillisec = 60 * 60 * 1000;

	public static synchronized Logs getInstance() {
		if(!instance.initialized) {
			instance.init();
		}
		return instance;
	}

	private Notificator<NewLogEvent> notificator;
	private Notificator<LogChangedEvent> notificatorForMark;

	private final List<Log> list = new LinkedList<>();

	public Logs() {
		printLogs = Config.getInstance().getEntryOrDefault("printLogs", Boolean.class, false);
	}

	private synchronized void init() {
		notificator = new Notificator<>();
		notificatorForMark = new Notificator<>();

		TimerTask task = new TimerTask() {
			public void run() {
				deleteOld();
			}
		};
		deleteOldTimer = new Timer("DeleteOldLogsTimer");
		deleteOldTimer.schedule(task, 100L, 100L);
		initialized = true;
	}

	public synchronized void setLogLifetimeMillisec(long newLifetime) {
		this.logLifeTimeMillisec = Require.inRange(newLifetime, 0, Long.MAX_VALUE, "newLifetime");
	}

	public synchronized void setPrintLogs(boolean printLogs) {
		this.printLogs = printLogs;
	}

	public synchronized void registerNewLogListener(NewLogEvent newLogListener) {
		notificator.register(newLogListener);
	}

	public synchronized void registerLogChangedListener(LogChangedEvent onLogChange) {
		notificatorForMark.register(onLogChange);
	}

	public synchronized Log getLog(int index) {
		return list.get(index);
	}

	public synchronized void event(String message) {
		Log log = new Log();
		log.event(message);
	}

	public synchronized void addLog(Log log) {
		if (printLogs) {
			Utils.prn("LOG: " + log.event,log);
		}
		LOGGER.debug("Log added");
		Log copied = JsonBuilder.build().fromJson(JsonBuilder.build().toJson(log), Log.class);
		list.add(copied);
		notificator.notifyThem((m) -> m.onNewLog(log));
	}

	public synchronized String getAsJson() {
		return JsonBuilder.build().toJson(list);
	}

	private synchronized void deleteOld() {
		long nowMillisec = new Date().getTime();
		long oldestTime = nowMillisec - logLifeTimeMillisec;
		list.removeIf(log -> log.timeMillsec < oldestTime);
	}

	public synchronized int count() {
		return list.size();
	}

	public synchronized void markInspected(long timeMilisec) {
		for (Log log : list) {
			if (log.timeMillsec == timeMilisec) {
				log.setInspectNeeded(false);
				notificatorForMark.notifyThem((m) -> m.onLogChanged(log));
				return;
			}
		}
	}

	@Override
	public synchronized void close() {
		initialized = false;
		Try.printIfThrown(deleteOldTimer::cancel);
		Try.printIfThrown(notificator::close);
		Try.printIfThrown(notificatorForMark::close);
		list.clear();
	}

}
