package bittech.lib.utils.logs;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.reflect.TypeToken;

import bittech.lib.utils.Config;
import bittech.lib.utils.Notificator;
import bittech.lib.utils.exceptions.StoredException;
import bittech.lib.utils.json.JsonBuilder;

public class Logs {

	private final static Logger LOGGER = LoggerFactory.getLogger(Logs.class);

	static final Logs instance = new Logs();

	final boolean printLogs;

	public static synchronized Logs getInstance() {
		return instance;
	}

	private long logLifeTimeMillisec = 60 * 60 * 1000;

	Notificator<NewLogEvent> notificator = new Notificator<NewLogEvent>();
	Notificator<LogChangedEvent> notificatorForMark = new Notificator<>();

	private List<Log> list = new LinkedList<Log>();
	// private List<Log> listCopy = new LinkedList<Log>();

	public Logs() {
		printLogs = Config.getInstance().getEntryOrDefault("printLogs", Boolean.class, false);
		boolean saveLogs = Config.getInstance().getEntryOrDefault("saveLogs", Boolean.class, true);
		if (saveLogs) {
			load();
			new SavingThread().start();
		} else {
			LOGGER.info("Logs will not be loaded and saved");
		}
	}

	public void registerNewLogListener(NewLogEvent newLogListener) {
		notificator.register(newLogListener);
	}

	public void registerLogChangedListener(LogChangedEvent onLogChange) {
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
			System.out.println("LOG: " + log.event);
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

		Iterator<Log> i = list.iterator();
		while (i.hasNext()) {
			Log log = i.next(); // must be called before you can call i.remove()
			if (log.timeMillsec < oldestTime) {
				i.remove();
			} else {
				return;
			}
		}

	}

	private synchronized void save() throws StoredException {
		try {
			try (PrintWriter out = new PrintWriter("logs.json")) {
				JsonBuilder.build().toJson(list, out);
			}
		} catch (Exception ex) {
			throw new StoredException("Cannot save logs", ex);
		}
	}

	private synchronized void load() {
		try {
			File file = new File("logs.json");
			LOGGER.debug("Loading logs");
			if (file.exists()) {
				try (FileReader reader = new FileReader(file)) {
					Type listType = new TypeToken<LinkedList<Log>>() {
					}.getType();
					list.clear();
					list.addAll(JsonBuilder.build().fromJson(reader, listType));
				}
				LOGGER.debug("Loaded logs: " + list.size());
			}
		} catch (Exception ex) {
			new StoredException("Cannot load logs", ex);
		}
	}

	private class SavingThread extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					deleteOld();
					Thread.sleep(1000);
					save();
					Thread.sleep(9000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized int count() {
		return list.size();
	}

	public synchronized void clear() {
		list.clear();
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

	public static void main(String[] args) {

		Logs.getInstance().registerNewLogListener((log) -> System.out.println(log));

		Logs.getInstance().markInspected(1234);

	}

}
