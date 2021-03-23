package bittech.lib.utils;

import bittech.lib.utils.db.Database;
import bittech.lib.utils.exceptions.ExceptionManager;
import bittech.lib.utils.exceptions.StoredException;
import bittech.lib.utils.logs.Logs;
import bittech.lib.utils.tests.Container;
import junit.framework.TestCase;
import org.bson.Document;
import org.junit.Assert;

public class DatabaseTest extends TestCase {

	private Container mongoContainer;

	private Database database;

	public static class C1 {
		public C2 c2;
	}

	public static class C2 {
		int a = 123;
	}

	public void setUp() throws InterruptedException {
		mongoContainer = new Container("docker-mongo/Dockerfile");
		database = new Database("mongodb://tron:Cy%24%24%24132@" + mongoContainer.getIp() + ":27017/?authSource=admin&ssl=false", "app");
	}
	
	@Override
	protected void tearDown() throws Exception {
		database.close();
		mongoContainer.close();
		ExceptionManager.getInstance().deleteAll();
		Logs.getInstance().close();
		super.tearDown();
	}

	public void testObj() {
		database.applyWriteAccess();
		C1 c1 = new C1();
		c1.c2 = new C2();
		database.addEntry("obj", c1,"mix");
	}

	public void testNoRights() {
		try {
			database.saveToDataBase(new Document(), "non_existing");
			Assert.fail("Exception not thrown");
		} catch(StoredException ex) {
			Assert.assertNotNull(ex.findReasonContains("No rights to write to DB"));
		}
	}

//	public void testNonExistingCollection() {
//		try {
//
//			Assert.fail("Exception not thrown");
//		} catch (Exception ex) {
//			// OK, we have exception
//		}
//	}

	public void testSecondDb() {
		try(Database database2 = new Database("mongodb://tron:Cy%24%24%24132@" + mongoContainer.getIp() + ":27017/?authSource=admin&ssl=false", "app")) {

			database.applyWriteAccess();
			database.addEntry("noweEntry", "abcd", "mix");

			database2.applyWriteAccess();
			try {
				database.addEntry("noweEntry2", "wwa", "mix");
				Assert.fail("Exception not thrown");
			} catch (StoredException ex) {
				Assert.assertNotNull(ex.findReasonContains("No rights to write to DB"));
			}

			{
				String en = database.getEntry("noweEntry", String.class);
				Assert.assertEquals("abcd", en);
			}

			{
				String en = database2.getEntry("noweEntry", String.class);
				Assert.assertEquals("abcd", en);
			}

			database2.updateEntry("noweEntry", "xyz");

			Assert.assertEquals("xyz", database.getEntry("noweEntry", String.class));

			try {
				Assert.assertEquals("wwa", database.getEntry("noweEntry2", String.class));
				Assert.fail("Exception not thrown");
			} catch (StoredException ex) {
				Assert.assertNotNull(ex.findReasonContains("No entry in mix collection for _id = noweEntry2"));
			}

		}

	}

	public void testUpdateNotExistingEntry() {
		try {
			database.applyWriteAccess();
			Test1 entry = new Test1();
			entry.name = "Ziutek";
			entry.num = 123;
			database.updateEntry("user", entry);
			Assert.fail("Exception not thrown");
		} catch (Exception ex) {
			// OK, we have exception
		}
	}

	public void testAddEntry() {
		database.applyWriteAccess();
		Test1 entry = new Test1();
		entry.name = "Ziutek";
		entry.num = 123;
		database.addEntry("user", entry,"mix");

		Test1 testRet = database.getEntry("user", Test1.class);
		Assert.assertTrue(Utils.deepEquals(entry, testRet));
	}

	public void testFindOneCreated() {
		database.applyWriteAccess();
		{
			Test1 entry = new Test1();
			entry.name = "Ziutek";
			entry.num = 123;
			database.addEntry("user", entry, "mix");
		}
		{
			Test1 entry = new Test1();
			entry.name = "Ziutek2";
			entry.num = 124;
			database.addEntry("user2", entry, "mix");
		}

		Test1 testRet = database.findOne("value.name", "Ziutek", "mix", Test1.class);

//		Assert.assertEquals(123, testRet.num); TODO: Uncomment dla poprawionej wersji
	}

	public void testSearchSpeed() {
		database.applyWriteAccess();
		for(int i=0; i<10000; i++){
			Test1 entry = new Test1();
			entry.name = "Ziutek"+i;
			entry.num = i;
			database.addEntry("user"+i, entry, "mix");
		}

		{ // Pierwsze wyszukanie (bez indexu) powyżej 10 ms
			long timeMillis = System.currentTimeMillis();
			Test1 testRet = database.findOne("value.name", "Ziutek1256", "mix", Test1.class);
			long time = (System.currentTimeMillis() - timeMillis);
			Assert.assertTrue(time > 10);
		}

		// Uwaga: Może jak zamuli to test nie przejdzie
		{ // Drugie wyszukanie (już z indexem) poniżej 10 ms
			long timeMillis = System.currentTimeMillis();
			Test1 testRet = database.findOne("value.name", "Ziutek1257", "mix", Test1.class);
			long time = (System.currentTimeMillis() - timeMillis);
			Assert.assertTrue(time < 10);
		}

		{// Trzecie wyszukanie (już z indexem) poniżej 10 ms
			long timeMillis = System.currentTimeMillis();
			Test1 testRet = database.findOne("value.name", "Ziutek1258", "mix", Test1.class);
			long time = (System.currentTimeMillis() - timeMillis);
			Assert.assertTrue(time < 10);
		}
	}

	public void testAddEntryInt() {
		database.applyWriteAccess();
		database.addEntry("int", 0,"mix");

		int ret = database.getEntry("int", Integer.class);
		Assert.assertEquals(0, ret);
	}

	public void testAddExisting() {
		database.applyWriteAccess();

		{
			Test1 entry = new Test1();
			entry.name = "Ziutek";
			entry.num = 123;
			database.addEntry("user", entry,"mix");
		}

		try {
			Test1 entry = new Test1();
			entry.name = "Mietek";
			entry.num = 564;
			database.addEntry("user", entry,"mix");
			Assert.fail("Exception not thrown");
		} catch (Exception ex) {
			// OK
		}

	}

	public void testUpdate() throws Exception {
		database.applyWriteAccess();

		Test1 entry = new Test1();
		entry.name = "Ziutek";
		entry.num = 123;
		database.addEntry("user", entry,"mix");

		entry.num = 234;

		database.updateEntry("user", entry);

		Test1 testRet = database.getEntry("user", Test1.class);
		Assert.assertEquals(234, testRet.num);
	}

	public static class Test1 {
		int num;
		String name;
	}

}
