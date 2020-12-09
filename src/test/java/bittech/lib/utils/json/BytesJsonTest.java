package bittech.lib.utils.json;

import java.util.Arrays;

import org.junit.Assert;

import com.google.gson.Gson;

import bittech.lib.utils.Bytes;
import bittech.lib.utils.UtilsException;
import junit.framework.TestCase;

public class BytesJsonTest extends TestCase {

	private static final class TestClass {
		public Bytes b;
	}

	public BytesJsonTest(String testName) {
		super(testName);
	}

	private static String toJson(Bytes bytes) {
		TestClass tc = new TestClass();
		tc.b = bytes;

		Gson g = JsonBuilder.build();

		return g.toJson(tc);
	}

	private static Bytes fromJson(String json) {
		Gson g = JsonBuilder.build();

		TestClass tc = g.fromJson(json, TestClass.class);
		return tc.b;
	}

	public void testBytesOnly() throws UtilsException {
		byte[] bytesArray = new byte[] { 0, 0, 1, -6, -120, 120, 100 };
		Bytes bytes = Bytes.fromArray(bytesArray);

		String base64 = bytes.asBase64();
		Bytes bytes2 = Bytes.fromBase64(base64);

		Arrays.equals(bytes.asByteArray(), bytes2.asByteArray());
	}

	public void testFromJson() throws UtilsException {
		Assert.assertEquals("{\"b\":\"AP8=\"}", toJson(Bytes.fromArray(new byte[] { 0, -1 })));
	}

	public void testToJson() {
		Bytes bytes = Bytes.fromArray(new byte[] { 0, -1 });
		Assert.assertEquals(bytes.toString(), fromJson("{\"b\":\"AP8=\"}").toString());
	}

}
