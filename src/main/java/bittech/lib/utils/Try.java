package bittech.lib.utils;

public class Try {
	public static void printIfThrown(RunnableWithEx runnable) {
		try {
			runnable.run();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void dupa() {
		System.out.println("Costam");
	}
}
