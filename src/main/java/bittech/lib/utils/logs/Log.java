package bittech.lib.utils.logs;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import bittech.lib.utils.FormattedTime;
import bittech.lib.utils.FormattedTime.Precision;
import bittech.lib.utils.Require;

public class Log {
	
	FormattedTime time;
	long timeMillsec;
	Map<String, Object> params = new LinkedHashMap<String, Object>();
	@SuppressWarnings("unused")
	String event;
	
	public Log param(String name, Object value) {
		params.put(name, value);
		return this;
	}
	
	public Log param(String name, long value) {
		params.put(name, Long.toString(value));
		return this;
	}
	
	public void event(String message) {
		Date now = new Date();
		timeMillsec = now.getTime();
		time = new FormattedTime(now, Precision.MILLISECONDS);
		event = Require.notNull(message, "event");
		Logs.getInstance().addLog(this);
	}
	
	public static Log build() {
		return new Log();
	}

}
