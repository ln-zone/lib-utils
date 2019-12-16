package bittech.lib.utils.json;

import java.math.BigDecimal;

import org.junit.Assert;

import com.google.gson.Gson;

import bittech.lib.utils.UtilsException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BigDecimalJsonTest extends TestCase {

	private static final class TestClass {
		public BigDecimal value;
	}

	public BigDecimalJsonTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(BigDecimalJsonTest.class);
	}

	private static String toJson(BigDecimal value) {
		TestClass tc = new TestClass();
		tc.value = value;

		Gson g = JsonBuilder.build();

		return g.toJson(tc);
	}

	private static BigDecimal fromJson(String json) {
		Gson g = JsonBuilder.build();

		TestClass tc = g.fromJson(json, TestClass.class);
		return tc.value;
	}

	public void testToJson() throws UtilsException {
		Assert.assertEquals("{\"value\":1.01}", toJson(new BigDecimal("1.01")));
		Assert.assertEquals("{\"value\":null}", toJson(null));
		Assert.assertEquals("{\"value\":0}", toJson(new BigDecimal("0")));
		Assert.assertEquals("{\"value\":0.00000000000001}", toJson(new BigDecimal("0.00000000000001")));
		Assert.assertEquals("{\"value\":1000000000000}", toJson(new BigDecimal("1000000000000")));
	}

	public void testFromJson() throws UtilsException {
		Assert.assertEquals("1.01", fromJson("{\"value\":1.01}").toPlainString());
		Assert.assertEquals(null, fromJson("{\"value\":null}"));
		Assert.assertEquals("0", fromJson("{\"value\":0}").toPlainString());
		Assert.assertEquals("0.00000000000001", fromJson("{\"value\":0.00000000000001}").toPlainString()); // TODO: Dlaczego to zero?
		Assert.assertEquals("1000000000000", fromJson("{\"value\":1000000000000}").toPlainString());
	}

}
