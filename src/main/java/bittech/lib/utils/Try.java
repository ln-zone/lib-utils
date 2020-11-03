package bittech.lib.utils;

public class Try {
	public static void printIfThrown(Runnable runnable) {
		try {
			runnable.run();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
