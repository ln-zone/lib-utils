package lnzone.lib.utils.json;

import org.junit.Assert;

import com.google.gson.Gson;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import lnzone.lib.utils.Btc;
import lnzone.lib.utils.UtilsException;
import lnzone.lib.utils.json.JsonBuilder;

public class BtcJsonTest extends TestCase {

	
	private static final class TestClass {
		public Btc cash;
	}

	
	public BtcJsonTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(BtcJsonTest.class);
	}

	private static String toJson(Btc btc) {
		TestClass tc = new TestClass();
		tc.cash = btc;

		Gson g = JsonBuilder.build();
		
		return g.toJson(tc);
	}
	
	private static Btc fromJson(String json) {
		Gson g = JsonBuilder.build();
		
		TestClass tc = g.fromJson(json, TestClass.class);
		return tc.cash;
	}
	
	public void testToJson() throws UtilsException {
		Assert.assertEquals("{\"cash\":\"1.01000000:000\"}", toJson(new Btc("1.01")));
		Assert.assertEquals("{\"cash\":null}", toJson(null));
		Assert.assertEquals("{\"cash\":\"\"}", toJson(new Btc(null)));
		Assert.assertEquals("{\"cash\":\"0.00000123:000\"}", toJson(new Btc("123")));
		Assert.assertEquals("{\"cash\":\"0.00000000:000\"}", toJson(new Btc("0")));
	}
	
	public void testFromJson() throws UtilsException {
		Assert.assertEquals(new Btc("1.01").toString(), fromJson("{\"cash\":\"1.01000000:000\"}").toString());
		Assert.assertEquals(new Btc(null).toString(), fromJson("{\"cash\":\"\"}").toString());
		Assert.assertEquals(null, fromJson("{\"cash\":null}"));
		Assert.assertEquals(new Btc("123").toString(), fromJson("{\"cash\":\"0.00000123:000\"}").toString());
		Assert.assertEquals(new Btc("0").toString(), fromJson("{\"cash\":\"0.00000000:000\"}").toString());
		Assert.assertEquals(new Btc("0").toString(), fromJson("{\"cash\":\"0\"}").toString());
	}


}
