package lnzone.lib.utils.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import lnzone.lib.utils.json.JsonBuilder;

public class ExceptionManagerTest extends TestCase {

	public ExceptionManagerTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(ExceptionManagerTest.class);
	}

	public void testBasic() {
		ExceptionManager exMan = ExceptionManager.getInstance();
		Exception ex0 = new Exception("Nic nie działa!");
		StoredException ex = new StoredException("Nie jest dobrze", ex0);
		long id = exMan.add(ex);
		ExceptionInfo ex1 = new ExceptionInfo(ex);
		ExceptionInfo ex2 = exMan.get(id);
		Assert.assertEquals("Exceptions not equeal", JsonBuilder.build().toJson(ex1), JsonBuilder.build().toJson(ex2));
	}

	public static class ExManThread extends Thread {

		private Exception lastException = null;

		@Override
		public void run() {
			try {
				for (int i = 0; i < 5; i++) {
					ExceptionManager exMan = ExceptionManager.getInstance();
					Exception ex0 = new Exception("Nic nie działa!");
					StoredException ex = new StoredException("Nie jest dobrze", ex0);
					long id = exMan.add(ex);
					ExceptionInfo ex1 = new ExceptionInfo(ex);
					ExceptionInfo ex2 = exMan.get(id);
					Assert.assertEquals("Exceptions not equeal", (JsonBuilder.build()).toJson(ex1), (JsonBuilder.build()).toJson(ex2));
				}
			} catch (Exception ex) {
				lastException = ex;
			}
		}

		public void throwLastException() throws Exception {
			if (lastException != null) {
				throw lastException;
			}
		}
	}

	public void testMultithreading() throws Exception {
		List<ExManThread> threads = new ArrayList<ExManThread>();
		for (int i = 0; i < 100; i++) {
			threads.add(new ExManThread());
		}

		for (ExManThread thread : threads) {
			thread.start();
		}
		
		for (ExManThread thread : threads) {
			thread.join();
		}
		
		for (ExManThread thread : threads) {
			thread.throwLastException();
		}
	}

}
