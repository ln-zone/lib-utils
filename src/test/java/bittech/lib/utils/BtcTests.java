package bittech.lib.utils;

import java.math.BigDecimal;

import org.junit.Assert;

import bittech.lib.utils.Btc;
import bittech.lib.utils.UtilsException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BtcTests extends TestCase {

	public BtcTests(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(BtcTests.class);
	}
	
	public void testToBitcoins() throws UtilsException {
		Assert.assertEquals("0", new Btc("0.00000000:000").toBitcoins().toPlainString());
		Assert.assertEquals("0", new Btc("0:000").toBitcoins().toPlainString());
		Assert.assertEquals("0.0000002", new Btc("0.00000020:000").toBitcoins().toPlainString());
		Assert.assertEquals("0.00123456789", new Btc("0.00123456:789").toBitcoins().toPlainString());
		Assert.assertEquals("1.00000000001", new Btc("1.00000000:001").toBitcoins().toPlainString());
		Assert.assertEquals("1", new Btc("1.00000000").toBitcoins().toPlainString());
		Assert.assertEquals("1", new Btc("1.0").toBitcoins().toPlainString());
		Assert.assertEquals("1.00000001", new Btc("1.00000001").toBitcoins().toPlainString());
		Assert.assertEquals("0.10000001", new Btc("10000001").toBitcoins().toPlainString());
		Assert.assertEquals("0.00000002", new Btc("2").toBitcoins().toPlainString());
		Assert.assertEquals("0.00000002123", new Btc("2:123").toBitcoins().toPlainString());
		Assert.assertEquals("123.4", new Btc("123.4").toBitcoins().toPlainString());
		Assert.assertEquals("0.001", new Btc("0.001").toBitcoins().toPlainString());
		
		Assert.assertEquals("0", new Btc("-0.00000000:000").toBitcoins().toPlainString());
		Assert.assertEquals("0", new Btc("-0:000").toBitcoins().toPlainString());
		Assert.assertEquals("-0.0000002", new Btc("-0.00000020:000").toBitcoins().toPlainString());
		Assert.assertEquals("-0.00123456789", new Btc("-0.00123456:789").toBitcoins().toPlainString());
		Assert.assertEquals("-1.00000000001", new Btc("-1.00000000:001").toBitcoins().toPlainString());
		Assert.assertEquals("-1", new Btc("-1.00000000").toBitcoins().toPlainString());
		Assert.assertEquals("-1", new Btc("-1.0").toBitcoins().toPlainString());
		Assert.assertEquals("-1.00000001", new Btc("-1.00000001").toBitcoins().toPlainString());
		Assert.assertEquals("-0.10000001", new Btc("-10000001").toBitcoins().toPlainString());
		Assert.assertEquals("-0.00000002", new Btc("-2").toBitcoins().toPlainString());
		Assert.assertEquals("-0.00000002123", new Btc("-2:123").toBitcoins().toPlainString());
		Assert.assertEquals("-123.4", new Btc("-123.4").toBitcoins().toPlainString());
		Assert.assertEquals("-0.001", new Btc("-0.001").toBitcoins().toPlainString());
		
		try {
			new Btc().toBitcoins();
			Assert.fail("No exceptin thrown");
		} catch(Exception ex) {}
	}
	
	public void testToMsat() throws UtilsException {
		Assert.assertEquals(0L, new Btc("0.00000000:000").toMsat());
		Assert.assertEquals(0L, new Btc("0:000").toMsat());
		Assert.assertEquals(20000L, new Btc("0.00000020:000").toMsat());
		Assert.assertEquals(123456789L, new Btc("0.00123456:789").toMsat());
		Assert.assertEquals(100000000001L, new Btc("1.00000000:001").toMsat());
		Assert.assertEquals(100000000000L, new Btc("1.00000000").toMsat());
		Assert.assertEquals(100000000000L, new Btc("1.0").toMsat());
		Assert.assertEquals(100000001000L, new Btc("1.00000001").toMsat());
		Assert.assertEquals(10000001000L, new Btc("10000001").toMsat());
		Assert.assertEquals(2000L, new Btc("2").toMsat());
		Assert.assertEquals(2123L, new Btc("2:123").toMsat());
		Assert.assertEquals(12340000000000L, new Btc("123.4").toMsat());
		Assert.assertEquals(100000000L, new Btc("0.001").toMsat());
		
		Assert.assertEquals(0L, new Btc("-0.00000000:000").toMsat());
		Assert.assertEquals(0L, new Btc("-0:000").toMsat());
		Assert.assertEquals(-20000L, new Btc("-0.00000020:000").toMsat());
		Assert.assertEquals(-123456789L, new Btc("-0.00123456:789").toMsat());
		Assert.assertEquals(-100000000001L, new Btc("-1.00000000:001").toMsat());
		Assert.assertEquals(-100000000000L, new Btc("-1.00000000").toMsat());
		Assert.assertEquals(-100000000000L, new Btc("-1.0").toMsat());
		Assert.assertEquals(-100000001000L, new Btc("-1.00000001").toMsat());
		Assert.assertEquals(-10000001000L, new Btc("-10000001").toMsat());
		Assert.assertEquals(-2000L, new Btc("-2").toMsat());
		Assert.assertEquals(-2123L, new Btc("-2:123").toMsat());
		Assert.assertEquals(-12340000000000L, new Btc("-123.4").toMsat());
		Assert.assertEquals(-100000000L, new Btc("-0.001").toMsat());
		
		try {
			new Btc().toMsat();
			Assert.fail("No exceptin thrown");
		} catch(Exception ex) {}
	}
	
	public void testToSat() throws UtilsException {
		Assert.assertEquals("0", new Btc("0.00000000:000").toSat().toPlainString());
		Assert.assertEquals("0", new Btc("0.0").toSat().toPlainString());
		Assert.assertEquals("20", new Btc("0.00000020:000").toSat().toPlainString());
		Assert.assertEquals("123456.789", new Btc("0.00123456:789").toSat().toPlainString());
		Assert.assertEquals("100000000.001", new Btc("1.00000000:001").toSat().toPlainString());
		Assert.assertEquals("100000000", new Btc("1.00000000").toSat().toPlainString());
		Assert.assertEquals("100000000", new Btc("1.0").toSat().toPlainString());
		Assert.assertEquals("100000001", new Btc("1.00000001").toSat().toPlainString());
		Assert.assertEquals("10000001", new Btc("10000001").toSat().toPlainString());
		Assert.assertEquals("2", new Btc("2").toSat().toPlainString());
		Assert.assertEquals("2.123", new Btc("2:123").toSat().toPlainString());
		Assert.assertEquals("12340000000", new Btc("123.4").toSat().toPlainString());
		Assert.assertEquals("100000", new Btc("0.001").toSat().toPlainString());
		
		Assert.assertEquals("0", new Btc("-0.00000000:000").toSat().toPlainString());
		Assert.assertEquals("0", new Btc("-0.0").toSat().toPlainString());
		Assert.assertEquals("-20", new Btc("-0.00000020:000").toSat().toPlainString());
		Assert.assertEquals("-123456.789", new Btc("-0.00123456:789").toSat().toPlainString());
		Assert.assertEquals("-100000000.001", new Btc("-1.00000000:001").toSat().toPlainString());
		Assert.assertEquals("-100000000", new Btc("-1.00000000").toSat().toPlainString());
		Assert.assertEquals("-100000000", new Btc("-1.0").toSat().toPlainString());
		Assert.assertEquals("-100000001", new Btc("-1.00000001").toSat().toPlainString());
		Assert.assertEquals("-10000001", new Btc("-10000001").toSat().toPlainString());
		Assert.assertEquals("-2", new Btc("-2").toSat().toPlainString());
		Assert.assertEquals("-2.123", new Btc("-2:123").toSat().toPlainString());
		Assert.assertEquals("-12340000000", new Btc("-123.4").toSat().toPlainString());
		
		try {
			new Btc().toSat();
			Assert.fail("No exceptin thrown");
		} catch(Exception ex) {}
	}

	
	public void testParseValid() throws UtilsException {
		Assert.assertEquals("0.00000000:000", new Btc("0.00000000:000").toString());
		Assert.assertEquals("0.00000000:000", new Btc("0:000").toString());
		Assert.assertEquals("0.00000000:000", new Btc("0").toString());
		Assert.assertEquals("0.00000000:000", new Btc("0.0").toString());
		Assert.assertEquals("0.00000020:000", new Btc("0.00000020:000").toString());
		Assert.assertEquals("0.00123456:789", new Btc("0.00123456:789").toString());
		Assert.assertEquals("1.00000000:001", new Btc("1.00000000:001").toString());
		Assert.assertEquals("1.00000000:000", new Btc("1.00000000").toString());
		Assert.assertEquals("1.00000000:000", new Btc("1.0").toString());
		Assert.assertEquals("1.00000001:000", new Btc("1.00000001").toString());
		Assert.assertEquals("0.10000001:000", new Btc("10000001").toString());
		Assert.assertEquals("0.00000002:000", new Btc("2").toString());
		Assert.assertEquals("0.00000002:123", new Btc("2:123").toString());
		Assert.assertEquals("123.40000000:000", new Btc("123.4").toString());
		Assert.assertEquals("0.00100000:000", new Btc("0.001").toString());
		
		Assert.assertEquals("0.00000000:000", new Btc("-0.00000000:000").toString());
		Assert.assertEquals("0.00000000:000", new Btc("-0:000").toString());
		Assert.assertEquals("0.00000000:000", new Btc("-0").toString());
		Assert.assertEquals("0.00000000:000", new Btc("-0.0").toString());
		Assert.assertEquals("-0.00000020:000", new Btc("-0.00000020:000").toString());
		Assert.assertEquals("-0.00123456:789", new Btc("-0.00123456:789").toString());
		Assert.assertEquals("-1.00000000:001", new Btc("-1.00000000:001").toString());
		Assert.assertEquals("-1.00000000:000", new Btc("-1.00000000").toString());
		Assert.assertEquals("-1.00000000:000", new Btc("-1.0").toString());
		Assert.assertEquals("-1.00000001:000", new Btc("-1.00000001").toString());
		Assert.assertEquals("-0.10000001:000", new Btc("-10000001").toString());
		Assert.assertEquals("-0.00000002:000", new Btc("-2").toString());
		Assert.assertEquals("-0.00000002:123", new Btc("-2:123").toString());
		Assert.assertEquals("-123.40000000:000", new Btc("-123.4").toString());
		Assert.assertEquals("-0.00100000:000", new Btc("-0.001").toString());
		
		Assert.assertEquals("", new Btc("").toString());
	}
	
	public void testFromBitcoins() throws UtilsException {
		Assert.assertEquals("0.00000000:000", Btc.fromBitcoins(new BigDecimal("0")).toString());
		Assert.assertEquals("0.00000000:000", Btc.fromBitcoins(new BigDecimal("-0")).toString());
		Assert.assertEquals("0.00123456:789", Btc.fromBitcoins(new BigDecimal("0.00123456789")).toString());
		Assert.assertEquals("1.00000000:001", Btc.fromBitcoins(new BigDecimal("1.00000000001")).toString());
		Assert.assertEquals("1.00000001:000", Btc.fromBitcoins(new BigDecimal("1.00000001")).toString());
		Assert.assertEquals("-1.00000001:000", Btc.fromBitcoins(new BigDecimal("-1.00000001")).toString());
		Assert.assertEquals("0.10000001:000", Btc.fromBitcoins(new BigDecimal("0.10000001")).toString());
		Assert.assertEquals("0.00000002:123", Btc.fromBitcoins(new BigDecimal("0.00000002123")).toString());
		Assert.assertEquals("123.40000000:000", Btc.fromBitcoins(new BigDecimal("123.4")).toString());
		Assert.assertEquals("0.00100000:000", Btc.fromBitcoins(new BigDecimal("0.001")).toString());
		Assert.assertEquals("-0.00100000:000", Btc.fromBitcoins(new BigDecimal("-0.001")).toString());
		
		try {
			Btc.fromBitcoins(new BigDecimal("0.001234567891")); // To many digits after point
			Assert.fail("Exception not thrown");
		} catch(Exception ex) {
			// Exception thrown - it is OK
		}
		
		try {
			Btc.fromBitcoins(new BigDecimal("1234567890")); // To many digits (too big number)
			Assert.fail("Exception not thrown");
		} catch(Exception ex) {
			// Exception thrown - it is OK
		}
	}
	
	public void testFromMsat() throws UtilsException {
		Assert.assertEquals("0.00000000:000", Btc.fromMsat(0L).toString());
		Assert.assertEquals("0.00123456:789", Btc.fromMsat(123456789L).toString());
		Assert.assertEquals("1.00000000:001", Btc.fromMsat(100000000001L).toString());
		Assert.assertEquals("123.40000000:000", Btc.fromMsat(12340000000000L).toString());
		Assert.assertEquals("-123.40000000:000", Btc.fromMsat(-12340000000000L).toString());
		Assert.assertEquals("0.00000000:001", Btc.fromMsat(1L).toString());
		Assert.assertEquals("-0.00000000:001", Btc.fromMsat(-1L).toString());
		
		try {
			Btc.fromMsat(22000000L*100000000L*1000L); // 100 mln bitcoins - To many digits (too big number)
			Assert.fail("Exception not thrown");
		} catch(Exception ex) {
			// Exception thrown - it is OK
		}
		
		try {
			Btc.fromMsat(-22000000L*100000000L*1000L); // 100 mln bitcoins - To many digits (too big number)
			Assert.fail("Exception not thrown");
		} catch(Exception ex) {
			// Exception thrown - it is OK
		}
	}
	
	public void testFromSat() throws UtilsException {
		Assert.assertEquals("0.00000000:000", Btc.fromSat(0L).toString());
		Assert.assertEquals("0.00123456:000", Btc.fromSat(123456L).toString());
		Assert.assertEquals("-0.00123456:000", Btc.fromSat(-123456L).toString());
		Assert.assertEquals("1.00000000:000", Btc.fromSat(100000000L).toString());
		Assert.assertEquals("123.40000000:000", Btc.fromSat(12340000000L).toString());
		Assert.assertEquals("0.00000001:000", Btc.fromSat(1).toString());
		Assert.assertEquals("-0.00000001:000", Btc.fromSat(-1).toString());
		
		try {
			Btc.fromSat(22000000L*100000000L); // 100 mln bitcoins - To many digits (too big number)
			Assert.fail("Exception not thrown");
		} catch(Exception ex) {
			// Exception thrown - it is OK
		}
		
		try {
			Btc.fromSat(-22000000L*100000000L); // 100 mln bitcoins - To many digits (too big number)
			Assert.fail("Exception not thrown");
		} catch(Exception ex) {
			// Exception thrown - it is OK
		}

	}

	public void testParseInvalid() throws UtilsException {
		try {
			new Btc("0.00000000:00");
			Assert.fail("No exception thrown");
		} catch (Exception ex) {
		}

		try {
			new Btc("0.0000000:000");
			Assert.fail("No exception thrown");
		} catch (Exception ex) {
		}

		try {
			new Btc("234234723478782347823873240.00000000:00");
			Assert.fail("No exception thrown");
		} catch (Exception ex) {
		}

		try {
			new Btc(".00000000:000");
			Assert.fail("No exception thrown");
		} catch (Exception ex) {
		}

		try {
			new Btc("0.00000000:");
			Assert.fail("No exception thrown");
		} catch (Exception ex) {
		}

		try {
			new Btc("123456789");
			Assert.fail("No exception thrown");
		} catch (Exception ex) {
		}
		
		try {
			new Btc("1:12");
			Assert.fail("No exception thrown");
		} catch (Exception ex) {
		}
		try {
			new Btc("0:0");
			Assert.fail("No exception thrown");
		} catch (Exception ex) {
		}
		
		try {
			new Btc("1:3");
			Assert.fail("No exception thrown");
		} catch (Exception ex) {
		}
		
		try {
			new Btc("0.0000a000:000");
			Assert.fail("No exception thrown");
		} catch (Exception ex) {
		}
		try {
			new Btc("0.0000b000:000");
			Assert.fail("No exception thrown");
		} catch (Exception ex) {
		}
		
	}

}
