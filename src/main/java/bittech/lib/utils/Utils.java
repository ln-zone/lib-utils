package bittech.lib.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

import bittech.lib.utils.exceptions.StoredException;
import bittech.lib.utils.json.JsonBuilder;

public class Utils {

	public static boolean deepEquals(Object r1, Object r2) {
		if (r1 == null && r2 == null) {
			return true;
		}
		if (r1 == null || r2 == null) {
			return false;
		}
		Gson gson = new Gson();
		return StringUtils.equals(gson.toJson(r1), gson.toJson(r2));
	}

	public static <T> T deepCopy(Object obj, Class<T> classOfT) {
		Gson gson = new Gson();
		return gson.fromJson(gson.toJson(obj), classOfT);
	}

	public static String toJson(Object obj) {
		return JsonBuilder.build().toJson(obj);
	}

	public static long rand() {
		return (long) (Math.random() * Long.MAX_VALUE);
	}

	public static void prn(Object obj) {
		System.out.println(JsonBuilder.build().toJson(obj));
	}

	public static void sleep(int msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			throw new RuntimeException("Sleep failed", e);
		}
	}

	public static void prnList(List<?> list) {
		int i = 0;
		for (Object obj : list) {
			System.out.println("" + i + ": " + JsonBuilder.build().toJson(obj));
			i++;
		}
	}

	public static void prn(String title, Object obj) {
		System.out.println(title + ": " + JsonBuilder.build().toJson(obj));
	}

//	public static void delFileIfExists(String fileName) {
//		try {
//			if (new File(fileName).exists()) {
//				Files.delete(Path.of(fileName));
//			}
//		} catch (Exception ex) {
//			throw new StoredException("Failed on tryintg to delete file", ex);
//		}
//	}

}
