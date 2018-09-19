package lnzone.lib.utils;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

public class Utils {

	public static boolean deepEquals(Object r1, Object r2) {
		Gson gson = new Gson();
		return StringUtils.equals(gson.toJson(r1), gson.toJson(r2));
	}
	
	public static <T> T deepCopy(Object obj, Class<T> classOfT) {
		Gson gson = new Gson();
		return gson.fromJson(gson.toJson(obj), classOfT);
	}	
	
	public static long rand() {
		return (long)(Math.random()*Long.MAX_VALUE);
	}
	
}
