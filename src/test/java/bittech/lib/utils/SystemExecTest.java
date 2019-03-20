package bittech.lib.utils;

import org.junit.Assert;

import bittech.lib.utils.Config;
import bittech.lib.utils.ExecResponse;
import bittech.lib.utils.SystemExec;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SystemExecTest extends TestCase {

	public SystemExecTest(String testName) {
		super(testName);
		Config.loadEmptyConfig();
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(SystemExecTest.class);
	}

	public void testBasic() throws Exception {
		ExecResponse resp = SystemExec.exec("more -V", 1000);

		Assert.assertNotEquals("System exec response is null", null, resp);
		Assert.assertEquals("Exit code not zero", 0, resp.exitCode);
		Assert.assertEquals("Errout not null", null, resp.errout);
		Assert.assertNotEquals("Output is null", null, resp.output);
		Assert.assertTrue("Invalid output", resp.output.contains("more from util-linux"));
	}

	// TODO: Test under not passed. Why?
	// public void testErrOutput() throws Exception {
	// SystemExecResponse resp = SystemExec.exec("java", 1000);
	//
	// Assert.assertNotEquals("System exec response is null", null, resp);
	// Assert.assertEquals("Exit code not 1", 1, resp.exitCode);
	// Assert.assertNotEquals("Errout is null", null, resp.errout);
	// Assert.assertEquals("Output is not null", null, resp.output);
	// Assert.assertTrue("Invalid errout", resp.errout.contains("Usage: java"));
	// Assert.assertTrue("Invalid errout", resp.errout.contains("<name> <value>."));
	// }

	// TODO: Test under not passed. Why?
	// public void testThreadNotStopped() throws Exception {
	// try {
	// SystemExec.exec("sleep 3", 1000);
	// Assert.fail("Exception not thrown");
	// } catch (StoredException ex) {
	// // It is OK, exception thrown
	// }
	// }

}
