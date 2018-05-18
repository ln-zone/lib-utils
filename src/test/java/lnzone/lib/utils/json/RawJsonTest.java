package lnzone.lib.utils.json;

import org.junit.Assert;

import com.google.gson.Gson;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import lnzone.lib.utils.UtilsException;
import lnzone.lib.utils.json.JsonBuilder;
import lnzone.lib.utils.json.RawJson;

public class RawJsonTest extends TestCase {

	
	private final class TestClass {
		@SuppressWarnings("unused")
		public String name;
		@SuppressWarnings("unused")
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
	
	
	public RawJsonTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(RawJsonTest.class);
	}

	public void testBasic() throws UtilsException {
		TestClass tc = new TestClass();
		tc.name = "Juzek";
		
		RawData rd = new RawData();
		rd.profession = "spawacz";
		rd.experience = 3;
		tc.rawJson = new RawJson(rd);
		
		tc.age = 38;
		
		Gson g = JsonBuilder.build();
		
		String json = g.toJson(tc);
		Assert.assertEquals("{\"name\":\"Juzek\",\"rawJson\":{\"profession\":\"spawacz\",\"experience\":3},\"age\":38}", json);

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
		Assert.assertEquals("{\"name\":null,\"rawJson\":{\"profession\":\"spawacz\",\"experience\":3},\"age\":38}", json);
		
	}
	
	public void testNullRawData() throws UtilsException {
		TestClass tc = new TestClass();
		tc.name = "Juzek";
		
		tc.rawJson = new RawJson(null);
		
		tc.age = 38;
		
		Gson g = JsonBuilder.build();
		
		String json = g.toJson(tc);
		Assert.assertEquals("{\"name\":\"Juzek\",\"rawJson\":null,\"age\":38}", json);
	}

	public void testNullRawJson() throws UtilsException {
		TestClass tc = new TestClass();
		tc.name = "Juzek";
		
		tc.rawJson = null;
		
		tc.age = 38;
		
		Gson g = JsonBuilder.build();
		
		String json = g.toJson(tc);
		Assert.assertEquals("{\"name\":\"Juzek\",\"rawJson\":null,\"age\":38}", json);
	}
	
	public void testRawJsonString() throws UtilsException {
		TestClass tc = new TestClass();
		tc.name = "Juzek";
		
		tc.rawJson = new RawJson("Ala");
		
		tc.age = 38;
		
		Gson g = JsonBuilder.build();
		
		String json = g.toJson(tc);
		Assert.assertEquals("{\"name\":\"Juzek\",\"rawJson\":\"Ala\",\"age\":38}", json);
	}
}
