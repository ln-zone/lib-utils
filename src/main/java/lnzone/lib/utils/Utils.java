package lnzone.lib.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

public class Utils {

	public static boolean deepEquals(Object r1, Object r2) {
		return StringUtils.equals((new Gson()).toJson(r1), (new Gson()).toJson(r2));
	}
	
	public static String now() {
	    String pattern = "yyyy-MM-dd HH:mm:ss";
	    SimpleDateFormat format = new SimpleDateFormat(pattern);
	    return format.format(new Date());
	}

}
