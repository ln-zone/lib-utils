package lnzone.lib.utils;

import org.junit.Assert;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ConfigTests extends TestCase {

	public ConfigTests(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(ConfigTests.class);
	}
	
	public void testBasic() throws UtilsException {
		Config.loadFromJson("{\r\n    \"connectionKeys\": {\r\n        \"prv\":\"prv_key\",\r\n        \"pub\":\"pub_key\"\r\n    },\r\n    \"supportWebSocket\":true\r\n}");
		Assert.assertEquals(true, Config.getInstance().getEntry("supportWebSocket", Boolean.class).booleanValue());
	}
	
}
