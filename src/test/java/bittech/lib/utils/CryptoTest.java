package bittech.lib.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import bittech.lib.utils.Crypto;
import bittech.lib.utils.RsaKeys;
import bittech.lib.utils.exceptions.StoredException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CryptoTest extends TestCase {

	public CryptoTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(CryptoTest.class);
	}

	public void testBasic() throws StoredException {
		RsaKeys keys = Crypto.generateKeys();

		String text = "po co mi Buty?";
		String ePrv = Crypto.encryptText(text, keys.getPrv());
		String ePub = Crypto.encryptText(text, keys.getPub());

		String dPrv = Crypto.decryptText(ePrv, keys.getPub());
		String dPub = Crypto.decryptText(ePub, keys.getPrv());

		Assert.assertEquals(dPrv, text);
		Assert.assertEquals(dPub, text);
	}

	public void testRandom() throws StoredException {

		for (int i = 0; i < 10; i++) {
			RsaKeys keys = Crypto.generateKeys();
			String text = "Rand: " + Math.random();
			String ePrv = Crypto.encryptText(text, keys.getPrv());
			String ePub = Crypto.encryptText(text, keys.getPub());

			String dPrv = Crypto.decryptText(ePrv, keys.getPub());
			String dPub = Crypto.decryptText(ePub, keys.getPrv());

			Assert.assertEquals(dPrv, text);
			Assert.assertEquals(dPub, text);
		}
	}

	public static class TestThread extends Thread {

		private Exception lastException = null;

		@Override
		public void run() {
			try {
				for (int i = 0; i < 5; i++) {
					RsaKeys keys = Crypto.generateKeys();
					String text = "Rand: " + Math.random();
					String ePrv = Crypto.encryptText(text, keys.getPrv());
					String ePub = Crypto.encryptText(text, keys.getPub());

					String dPrv = Crypto.decryptText(ePrv, keys.getPub());
					String dPub = Crypto.decryptText(ePub, keys.getPrv());

					Assert.assertEquals(dPrv, text);
					Assert.assertEquals(dPub, text);
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
		List<TestThread> threads = new ArrayList<TestThread>();
		for (int i = 0; i < 30; i++) {
			threads.add(new TestThread());
		}

		for (TestThread thread : threads) {
			thread.start();
		}

		for (TestThread thread : threads) {
			thread.join();
		}

		for (TestThread thread : threads) {
			thread.throwLastException();
		}
	}

}
