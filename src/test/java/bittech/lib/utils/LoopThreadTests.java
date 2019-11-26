package bittech.lib.utils;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LoopThreadTests extends TestCase {

	public LoopThreadTests(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(LoopThreadTests.class);
	}

	public void testBasic() throws Exception {

		AtomicInteger executions = new AtomicInteger();

		LoopThread loopThread = new LoopThread() {

			@Override
			public void action() {
				executions.addAndGet(1);
			}

		};

		loopThread.start(100, 1000);

		Thread.sleep(990);

		loopThread.close();

		Assert.assertEquals(10, executions.get());

	}

	public void testInfiniteSleep() throws Exception {

		LoopThread loopThread = new LoopThread() {

			@Override
			public void action() {
				try {
					Thread.sleep(100000);
				} catch (Exception ex) {

				}
			}

		};

		loopThread.start(100, 100);

		Thread.sleep(990);

		loopThread.close();

	}

	public void testNeverFinished() throws Exception {

		LoopThread loopThread = new LoopThread() {

			@Override
			public void action() {
				try {
					while (true) {
						System.out.println("Dupa");
					}
				} catch (Exception ex) {

				}
			}

		};

		loopThread.start(100, 100);

		Thread.sleep(990);

		loopThread.close();

	}

}
