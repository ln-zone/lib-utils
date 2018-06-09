package lnzone.lib.utils;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

public class Utils {

	public static boolean deepEquals(Object r1, Object r2) {
		return StringUtils.equals((new Gson()).toJson(r1), (new Gson()).toJson(r2));
	}
	
}
