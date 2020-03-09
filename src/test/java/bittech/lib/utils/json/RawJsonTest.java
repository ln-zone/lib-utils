package bittech.lib.utils.json;

import org.junit.Assert;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bittech.lib.utils.UtilsException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class RawJsonTest extends TestCase {

	private final class TestClass {
		@SuppressWarnings("unused")
		public String name;
		public RawJson rawJson;
		@SuppressWarnings("unused")
		public int age;
	}

	private final class RawData {
		@SuppressWarnings("unused")
		public String profession;
		@SuppressWarnings("unused")
		public int experience;
	}

	private final class Empty {
	}

	public void testBasic() throws UtilsException {
		TestClass tc = new TestClass();
		tc.name = "Juzek";

		RawData rd = new RawData();
		rd.profession = "spawacz";
		rd.experience = 3;
		tc.rawJson = new RawJson(rd);

		tc.age = 38;

		Gson g = (new GsonBuilder()).registerTypeAdapter(RawJson.class, new RawJsonAdapter()).serializeNulls().create();

		String json = g.toJson(tc);
		Assert.assertEquals("{\"name\":\"Juzek\",\"rawJson\":{\"profession\":\"spawacz\",\"experience\":3},\"age\":38}",
				json);

		TestClass tc2 = g.fromJson(json, TestClass.class);
		String str = tc2.rawJson.getJsonStr();
		Assert.assertEquals("{\"profession\":\"spawacz\",\"experience\":3}", str);
	}

	public RawJsonTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(RawJsonTest.class);
	}

	public void testNullString() throws UtilsException {
		TestClass tc = new TestClass();
		tc.name = null;

		RawData rd = new RawData();
		rd.profession = "spawacz";
		rd.experience = 3;
		tc.rawJson = new RawJson(rd);

		tc.age = 38;

		Gson g = JsonBuilder.build();

		String json = g.toJson(tc);
		Assert.assertEquals("{\"name\":null,\"rawJson\":{\"profession\":\"spawacz\",\"experience\":3},\"age\":38}",
				json);

		g.fromJson(json, TestClass.class);

	}

	public void testNullRawData() throws UtilsException {
		TestClass tc = new TestClass();
		tc.name = "Juzek";

		tc.rawJson = new RawJson(null);

		tc.age = 38;

		Gson g = JsonBuilder.build();

		String json = g.toJson(tc);
		Assert.assertEquals("{\"name\":\"Juzek\",\"rawJson\":null,\"age\":38}", json);

		g.fromJson(json, TestClass.class);
	}

	public void testNullRawJson() throws UtilsException {
		TestClass tc = new TestClass();
		tc.name = "Juzek";

		tc.rawJson = null;

		tc.age = 38;

		Gson g = JsonBuilder.build();

		String json = g.toJson(tc);
		Assert.assertEquals("{\"name\":\"Juzek\",\"rawJson\":null,\"age\":38}", json);

		g.fromJson(json, TestClass.class);
	}

	public void testRawJsonEmpty() throws UtilsException {
		TestClass tc = new TestClass();
		tc.name = "Juzek";

		tc.rawJson = new RawJson(new Empty());

		tc.age = 38;

		Gson g = JsonBuilder.build();

		String json = g.toJson(tc);
		Assert.assertEquals("{\"name\":\"Juzek\",\"rawJson\":{},\"age\":38}", json);

		g.fromJson(json, TestClass.class);
	}

	// Not sure if this is needed
//	public void testRawJsonString() throws UtilsException {
//		TestClass tc = new TestClass();
//		tc.name = "Juzek";
//		
//		tc.rawJson = new RawJson("Ala");
//		
//		tc.age = 38;
//		
//		Gson g = JsonBuilder.build();
//		
//		String json = g.toJson(tc);
//		Assert.assertEquals("{\"name\":\"Juzek\",\"rawJson\":\"Ala\",\"age\":38}", json);
//	}
}
