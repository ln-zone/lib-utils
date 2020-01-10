package bittech.lib.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;

import bittech.lib.utils.Notificator;
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

	public void testSimple() throws Exception {

		ToNotifyNumber observer = new ToNotifyNumber();

		try (Notificator<ToNotifyNumber> notificator = new Notificator<ToNotifyNumber>()) {

			notificator.register(observer);
			notificator.notifyThem((toNotify) -> toNotify.numberGenerated(0));

		}

		Assert.assertEquals(false, observer.wrongOrder);
		Assert.assertEquals(false, observer.interrupted);
		Assert.assertEquals(1, observer.lastNum.get());

	}

	public void testWithThread() throws Exception {

		ToNotifyNumber observer = new ToNotifyNumber();

		try (Notificator<ToNotifyNumber> notificator = new Notificator<ToNotifyNumber>()) {

			notificator.register(observer);
			final int num = 0;
			Thread th = new Thread(() -> {
				notificator.notifyThem((toNotify) -> toNotify.numberGenerated(num));
			});
			th.start();
			th.join();
		}

		Assert.assertEquals(false, observer.wrongOrder);
		Assert.assertEquals(false, observer.interrupted);
		Assert.assertEquals(1, observer.lastNum.get());

	}

	public void testMultithread() throws Exception {

		int numThreads = 10;
		int numObservers = 30;

		List<ToNotifyNumber> observers = new LinkedList<ToNotifyNumber>();

		for (int i = 0; i < numObservers; i++) {
			observers.add(new ToNotifyNumber());
		}

		try (Notificator<ToNotifyNumber> notificator = new Notificator<ToNotifyNumber>()) {

			for (ToNotifyNumber el : observers) {
				notificator.register(el);
			}

			List<Thread> threads = new ArrayList<Thread>();

			for (int i = 0; i < numThreads; i++) {
				final int num = i;
				threads.add(new Thread(() -> {
					notificator.notifyThem((toNotify) -> toNotify.numberGenerated(num));
				}));
			}

			for (Thread th : threads) {
				th.start();
			}

			for (Thread th : threads) {
				th.join();
			}
		}

		for (ToNotifyNumber el : observers) {
//			Assert.assertEquals(false, el.wrongOrder);
			Assert.assertEquals(false, el.interrupted);
			Assert.assertEquals(numThreads, el.lastNum.get());
		}

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

	public void testStopAll() {
		{
			ToNotifyNumber observer = new ToNotifyNumber();

			try (Notificator<ToNotifyNumber> notificator = new Notificator<ToNotifyNumber>()) {

				notificator.register(observer);
				notificator.notifyThem((toNotify) -> toNotify.numberGenerated(0));

			}

			Assert.assertEquals(false, observer.wrongOrder);
			Assert.assertEquals(false, observer.interrupted);
			Assert.assertEquals(1, observer.lastNum.get());
		}
		Notificator.stopAll();
		{
			ToNotifyNumber observer = new ToNotifyNumber();

			try (Notificator<ToNotifyNumber> notificator = new Notificator<ToNotifyNumber>()) {

				notificator.register(observer);
				notificator.notifyThem((toNotify) -> toNotify.numberGenerated(0));
			}

			Assert.assertEquals(false, observer.wrongOrder);
			Assert.assertEquals(false, observer.interrupted);
			Assert.assertEquals(1, observer.lastNum.get());
		}

	}
	
	public void testStopAllWait() {
		{
			ToNotifyNumber observer = new ToNotifyNumber();

			try (Notificator<ToNotifyNumber> notificator = new Notificator<ToNotifyNumber>()) {

				notificator.register(observer);
				notificator.notifyThem((toNotify) -> {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}); 
				System.out.println("Notified");
			}

			System.out.println("Teraz1");
			Assert.assertEquals(false, observer.wrongOrder);
			Assert.assertEquals(false, observer.interrupted);
		}
		System.out.println("Teraz");
//		Notificator.stopAll();
		{
			ToNotifyNumber observer = new ToNotifyNumber();

			try (Notificator<ToNotifyNumber> notificator = new Notificator<ToNotifyNumber>()) {

				notificator.register(observer);
				notificator.notifyThem((toNotify) -> toNotify.numberGenerated(0));
			}

			Assert.assertEquals(false, observer.wrongOrder);
			Assert.assertEquals(false, observer.interrupted);
			Assert.assertEquals(1, observer.lastNum.get());
		}

	}

}
