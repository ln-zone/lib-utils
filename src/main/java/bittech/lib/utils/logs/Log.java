package bittech.lib.utils.logs;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import bittech.lib.utils.FormattedTime;
import bittech.lib.utils.FormattedTime.Precision;
import bittech.lib.utils.Require;

public class Log {

	public enum Severity {
		Info, Warning, Error
	}

	FormattedTime time;
	long timeMillsec;
	Severity severity;
	private boolean inspectNeeded;
	Map<String, Object> params = new LinkedHashMap<String, Object>();
	String event;

	public Log() {
		this.severity = Severity.Info;
		this.inspectNeeded = false;
	}

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

	public Log setSeverity(Severity severity) {
		this.severity = Require.notNull(severity, "severity");
		return this;
	}

	public boolean getInspectNeeded() {
		return inspectNeeded;
	}

	public Log setInspectNeeded(boolean inspectNeeded) {
		this.inspectNeeded = inspectNeeded;
		return this;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public FormattedTime getTime() {
		return time;
	}

	public void setTimeMilisec(long timeMillsec) {
		this.timeMillsec = timeMillsec;
	}

	public long getTimeMillsec() {
		return timeMillsec;
	}

	public Severity getSeverity() {
		return severity;
	}

	public boolean isInspectNeeded() {
		return inspectNeeded;
	}

	public String getEvent() {
		return event;
	}

	public static Log build() {
		return new Log();
	}

}
