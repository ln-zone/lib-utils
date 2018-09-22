package lnzone.lib.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class NotificatorTests extends TestCase {

	class ToNotifyNumber {

		Random rand = new Random();
		
		public AtomicInteger lastNum = new AtomicInteger(0);
		public boolean wrongOrder = false;
		public boolean interrupted = false;

		synchronized void numberGenerated(int number) {
			try {
				Thread.sleep(rand.nextInt(10));

				if (lastNum.get() != number) {
					wrongOrder = true;
				}
				lastNum.incrementAndGet();
				
				Thread.sleep(rand.nextInt(10));
			} catch (InterruptedException e) {
				interrupted = true;
			}
		}
	}

	public NotificatorTests(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(NotificatorTests.class);
	}

	public void testMassive() throws Exception {

		int countNum = 100;
		
		List<ToNotifyNumber> listeners = new LinkedList<ToNotifyNumber>();
		for (int i = 0; i < 10; i++) {
			listeners.add(new ToNotifyNumber());
		}
		
		try (Notificator<ToNotifyNumber> notificator = new Notificator<ToNotifyNumber>()) {

			for (ToNotifyNumber el : listeners) {
				notificator.register(el);
			}

			for (int i = 0; i < countNum; i++) {
				final int num = i;
				notificator.notifyThem((toNotify) -> toNotify.numberGenerated(num));
			}
		}
	
		for (ToNotifyNumber el : listeners) {
			Assert.assertEquals(false, el.wrongOrder);
			Assert.assertEquals(false, el.interrupted);
			Assert.assertEquals(countNum, el.lastNum.get());
		}

	}

}
