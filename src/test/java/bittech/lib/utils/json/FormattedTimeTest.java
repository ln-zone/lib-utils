package bittech.lib.utils.json;

import org.junit.Assert;

import com.google.gson.Gson;

import bittech.lib.utils.FormattedTime;
import bittech.lib.utils.UtilsException;
import bittech.lib.utils.json.JsonBuilder;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FormattedTimeTest extends TestCase {

	private static final class TestClass {
		public FormattedTime time;
	}
	
	public FormattedTimeTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(FormattedTimeTest.class);
	}

	private static String toJson(FormattedTime time) {
		TestClass tc = new TestClass();
		tc.time = time;

		Gson g = JsonBuilder.build();
		
		return g.toJson(tc);
	}
	
	private static FormattedTime fromJson(String json) {
		Gson g = JsonBuilder.build();
		
		TestClass tc = g.fromJson(json, TestClass.class);
		return tc.time;
	}
	
	public void testToJson() throws UtilsException {
		Assert.assertEquals("{\"time\":\"2016\"}", toJson(new FormattedTime("2016")));
		Assert.assertEquals("{\"time\":null}", toJson(null));
		Assert.assertEquals("{\"time\":\"2016-01-23 14:23\"}", toJson(new FormattedTime("2016-01-23 14:23")));
		Assert.assertEquals("{\"time\":\"2016-01-23 14:23:00\"}", toJson(new FormattedTime("2016-01-23 14:23:00")));
		Assert.assertEquals("{\"time\":\"2016-01-23 14:23:00.000\"}", toJson(new FormattedTime("2016-01-23 14:23:00.000")));
	}
	
	public void testFromJson() throws UtilsException {
		Assert.assertEquals(new FormattedTime("2016").toString(), fromJson("{\"time\":\"2016\"}").toString());
		Assert.assertEquals(null, fromJson("{\"time\":null}"));
		Assert.assertEquals(new FormattedTime("2016-01-23 14:23").toString(), fromJson("{\"time\":\"2016-01-23 14:23\"}").toString());
		Assert.assertEquals(new FormattedTime("2016-01-23 14:23:00").toString(), fromJson("{\"time\":\"2016-01-23 14:23:00\"}").toString());
		Assert.assertEquals(new FormattedTime("2016-01-23 14:23:00.000").toString(),fromJson("{\"time\":\"2016-01-23 14:23:00.000\"}").toString());

//		Assert.assertEquals(new Btc(null).toString(), fromJson("{\"cash\":\"\"}").toString());
//		Assert.assertEquals(new Btc("123").toString(), fromJson("{\"cash\":\"0.00000123:000\"}").toString());
//		Assert.assertEquals(new Btc("0").toString(), fromJson("{\"cash\":\"0.00000000:000\"}").toString());
//		Assert.assertEquals(new Btc("0").toString(), fromJson("{\"cash\":\"0\"}").toString());
	}


}
