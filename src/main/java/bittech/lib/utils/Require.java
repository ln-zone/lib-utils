package bittech.lib.utils;

import bittech.lib.utils.exceptions.StoredException;

public class Require {

	private Require() {
	}

	public static <T> T notNull(T obj, String name) {
		if (obj == null) {
			throw new StoredException("\"" + name + "\" cannot be null", null);
		}
		return obj;
	}

	public static int inRange(int value, int valFrom, int valTo, String name) {
		if ((value >= valFrom) && (value <= valTo)) {
			return value;
		}
		throw new StoredException("\"" + name + "\" must be value between " + valFrom + " and " + valTo, null);
	}
	
	public static float inRange(float value, float valFrom, float valTo, String name) {
		if ((value >= valFrom) && (value <= valTo)) {
			return value;
		}
		throw new StoredException("\"" + name + "\" must be value between " + valFrom + " and " + valTo, null);
	}

	public static long inRange(long value, long valFrom, long valTo, String name) {
		if ((value >= valFrom) && (value <= valTo)) {
			return value;
		}
		throw new StoredException("\"" + name + "\" must be value between " + valFrom + " and " + valTo, null);
	}

	public static String notEmpty(String value, String name) {
		if (value != null && !value.equals("")) {
			return value;
		}
		throw new StoredException("\"" + name + "\" cannot be empty string", null);
	}
	
	public static String equals(String value, String expected, String name) {
		Require.notEmpty(value, name);
		if(value.equals(expected)) {
			return value;
		}
		throw new StoredException("\"" + name + "\" have to be " +  expected + " but it is " + value, null);
	}

}
