package lnzone.lib.utils;

public class Require {

	private Require() {
	}
	
	public static <T> T notNull(T obj, String name) {
		if(obj == null) {
			throw new RuntimeException("\"" + name + "\" cannot be null");
		}
		return obj;
	}
	
	public static int inRange(int value, int valFrom, int valTo, String name) {
		if((value >= valFrom) && (value <= valTo)) {
			return value;
		}
		throw new RuntimeException("\"" + name + "\" must be value between " + valFrom + " and " + valTo);
	}
	
	public static long inRange(long value, long valFrom, long valTo, String name) {
		if((value >= valFrom) && (value <= valTo)) {
			return value;
		}
		throw new RuntimeException("\"" + name + "\" must be value between " + valFrom + " and " + valTo);
	}

}
