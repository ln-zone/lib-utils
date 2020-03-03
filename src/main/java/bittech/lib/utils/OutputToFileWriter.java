package bittech.lib.utils;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

import bittech.lib.utils.exceptions.StoredException;

public class OutputToFileWriter {

	public static String name;
	private static List<String> lines;

	public static void autoconfigName() {
		if (name != null) {
			throw new RuntimeException(
					"Name already assigned. Probably there is no OutputToFileWriter.close in tearDown");
		}
		StackTraceElement el = new Throwable().getStackTrace()[1]; // .getClassName() + "." +
		String[] subnames = el.getClassName().split("\\.");
		name = subnames[subnames.length - 1] + "." + el.getMethodName();
		lines = new LinkedList<String>();
	}

	public static void close() {
		saveLinesToFile();
		name = null;
		lines = null;
	}

	private synchronized static void saveLinesToFile() {
		try {
			FileWriter fileWriter = new FileWriter(new File("testLogs/" + name + ".txt"), false);
			for (String line : lines) {
				fileWriter.write(line);
				fileWriter.write("\n\n");
			}
			fileWriter.close();
		} catch (Exception ex) {
			throw new StoredException("Cannot write output to file", ex);
		}
	}

	public synchronized static void saveLine(String line) {
		System.out.println(line);
		if (name != null) {
			lines.add(line);
		}
	}

}
