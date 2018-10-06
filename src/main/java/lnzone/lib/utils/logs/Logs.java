package lnzone.lib.utils.logs;

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

import lnzone.lib.utils.Config;
import lnzone.lib.utils.exceptions.StoredException;
import lnzone.lib.utils.json.JsonBuilder;

public class Logs {

	private final static Logger LOGGER = LoggerFactory.getLogger(Logs.class);

	static final Logs instance = new Logs();

	public static synchronized Logs getInstance() {
		return instance;
	}

	private long logLifeTimeMillisec = 60 * 60 * 1000;

	private List<Log> list = new LinkedList<Log>();
	// private List<Log> listCopy = new LinkedList<Log>();

	public Logs() {
		boolean saveLogs = Config.getInstance().getEntryOrDefault("saveLogs", Boolean.class, true);
		if (saveLogs) {
			load();
			new SavingThread().start();
		} else {
			LOGGER.info("Logs will not be loaded and saved");
		}
	}

	public synchronized void event(String message) {
		Log log = new Log();
		log.event(message);
	}

	public synchronized void addLog(Log log) {
		LOGGER.debug("Log added");
		Log copied = JsonBuilder.build().fromJson(JsonBuilder.build().toJson(log), Log.class);
		list.add(copied);
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
			try (PrintWriter out = new PrintWriter("/root/ln/logs.json")) {
				JsonBuilder.build().toJson(list, out);
			}
		} catch (Exception ex) {
			throw new StoredException("Cannot save logs", ex);
		}
	}

	private synchronized void load() {
		try {
			File file = new File("/root/ln/logs.json");
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

}
