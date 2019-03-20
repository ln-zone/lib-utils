package bittech.lib.utils;

import org.junit.Assert;

import bittech.lib.utils.Config;
import bittech.lib.utils.FormattedTime;
import bittech.lib.utils.UtilsException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FormattedTimeTests extends TestCase {

	public FormattedTimeTests(String testName) {
		super(testName);
		Config.loadEmptyConfig();
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(FormattedTimeTests.class);
	}

	public void testCorrect() throws UtilsException {
		Assert.assertEquals("2016", new FormattedTime("2016").toString());
		Assert.assertEquals("2016-02", new FormattedTime("2016-02").toString());
		Assert.assertEquals("2016-11-04", new FormattedTime("2016-11-04").toString());
		Assert.assertEquals("2000-01-01 03", new FormattedTime("2000-01-01 03").toString());
		Assert.assertEquals("2000-01-01 03:02", new FormattedTime("2000-01-01 03:02").toString());
		Assert.assertEquals("2000-01-01 03:02:01", new FormattedTime("2000-01-01 03:02:01").toString());
		Assert.assertEquals("2000-01-01 03:02:01.002", new FormattedTime("2000-01-01 03:02:01.002").toString());

	}

	public void testNotCorrect() throws UtilsException {
		try {
			new FormattedTime(null).toString();
			Assert.fail("No exceptin thrown");
		} catch (Exception ex) {
		}

		try {
			new FormattedTime("").toString();
			Assert.fail("No exceptin thrown");
		} catch (Exception ex) {
		}

		try {
			new FormattedTime("201").toString();
			Assert.fail("No exceptin thrown");
		} catch (Exception ex) {
		}

		try {
			new FormattedTime("2000-01-01 03:02:01,002").toString();
			Assert.fail("No exceptin thrown");
		} catch (Exception ex) {
		}

	}

}
