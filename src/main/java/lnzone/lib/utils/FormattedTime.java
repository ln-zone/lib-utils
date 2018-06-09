package lnzone.lib.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import lnzone.lib.utils.exceptions.StoredException;

public class FormattedTime {

	public static enum Precision {
		YEARS, MONTHS, DAYS, HOURS, MINUTES, SECONDS, MILLISECONDS
	};

	private static String precisionToFormat(Precision precision) {
		switch (precision) {
		case YEARS:
			return "yyyy";
		case MONTHS:
			return "yyyy-MM";
		case DAYS:
			return "yyyy-MM-dd";
		case HOURS:
			return "yyyy-MM-dd HH";
		case MINUTES:
			return "yyyy-MM-dd HH:mm";
		case SECONDS:
			return "yyyy-MM-dd HH:mm:ss";
		case MILLISECONDS:
			return "yyyy-MM-dd HH:mm:ss.SSS";
		default:
			throw new StoredException("Usupported precision: " + precision, null);
		}
	}

	private static Precision calcPrecision(String time) {
		int len = Require.notNull(time, "time").length();
		switch (len) {
		case 4:
			return Precision.YEARS;
		case 7:
			return Precision.MONTHS;
		case 10:
			return Precision.DAYS;
		case 13:
			return Precision.HOURS;
		case 16:
			return Precision.MINUTES;
		case 19:
			return Precision.SECONDS;
		case 23:
			return Precision.MILLISECONDS;
		default:
			throw new StoredException("Cannot calc precision from time: " + time, null);
		}
	}

	private static Date parseTime(String time, Precision precision) {
		try {
			String format = precisionToFormat(precision);
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.parse(time);
		} catch (Exception ex) {
			throw new StoredException("Cannot parse time: " + time + " with precision " + precision, ex);
		}
	}

	private static String formatTime(Date date, Precision precision) {
		try {
			String format = precisionToFormat(precision);
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		} catch (Exception ex) {
			throw new StoredException("Cannot format time: " + date + " with precision " + precision, ex);
		}
	}
	
	private Date date;
	private Precision precision;

	public Date getDate() {
		return date;
	}

	public Precision getPrecision() {
		return precision;
	}

	public void setPrecision(Precision precision) {
		this.precision = Require.notNull(precision, "precision");
	}

	public FormattedTime(long time, Precision precision) {
		this.date = new Date(Require.inRange(time, 1, Long.MAX_VALUE, "time"));
		this.precision = Require.notNull(precision, "precision");
	}
	
	public FormattedTime(Date date, Precision precision) {
		this.date = Require.notNull(date, "date");
		this.precision = Require.notNull(precision, "precision");
	}
	
	public static FormattedTime now(Precision precision) {
		return new FormattedTime(new Date(), precision);
	}
	
	public FormattedTime(String time) {
		Require.notNull(time, "time");
		this.precision = calcPrecision(time);
		this.date = parseTime(time, precision);
	}

	@Override
	public String toString() {
		return formatTime(date, precision);
	}

}
