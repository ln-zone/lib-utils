package bittech.lib.utils.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import bittech.lib.utils.Config;
import bittech.lib.utils.Require;
import bittech.lib.utils.json.JsonBuilder;
import bittech.lib.utils.logs.Log;
import bittech.lib.utils.logs.Logs;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ExceptionManagerTest extends TestCase {

	public ExceptionManagerTest(String testName) {
		super(testName);

	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(ExceptionManagerTest.class);
	}

	@Override
	public void setUp() throws InterruptedException {
		Config.loadEmptyConfig();
		Config.getInstance().addEntry("saveLogs", false);
		Config.getInstance().addEntry("pushToLogs", true);

	}

	@Override
	public void tearDown() {
		Logs.getInstance().clear();
		ExceptionManager.getInstance().reset();
	}

	public void testBasic() {
		ExceptionManager exMan = ExceptionManager.getInstance();
		Exception ex0 = new StoredException("Nic nie działa!", null);
		StoredException ex = new StoredException("Nie jest dobrze", ex0);
		long id = exMan.add(ex);
		ExceptionInfo ex1 = new ExceptionInfo(ex);
		ExceptionInfo ex2 = exMan.get(id);
		Assert.assertEquals("Exceptions not equeal", JsonBuilder.build().toJson(ex1), JsonBuilder.build().toJson(ex2));
	}

	public static class ExManThread extends Thread {

		private Exception lastException = null;

		@Override
		public void run() {
			try {
				for (int i = 0; i < 5; i++) {
					ExceptionManager exMan = ExceptionManager.getInstance();
					Exception ex0 = new Exception("Nic nie działa!");
					StoredException ex = new StoredException("Nie jest dobrze", ex0);
					long id = exMan.add(ex);
					ExceptionInfo ex1 = new ExceptionInfo(ex);
					ExceptionInfo ex2 = exMan.get(id);
					Assert.assertEquals("Exceptions not equeal", (JsonBuilder.build()).toJson(ex1),
							(JsonBuilder.build()).toJson(ex2));
				}
			} catch (Exception ex) {
				lastException = ex;
			}
		}

		public void throwLastException() throws Exception {
			if (lastException != null) {
				throw lastException;
			}
		}
	}

	public void testMultithreading() throws Exception {
		List<ExManThread> threads = new ArrayList<ExManThread>();
		for (int i = 0; i < 100; i++) {
			threads.add(new ExManThread());
		}

		for (ExManThread thread : threads) {
			thread.start();
		}

		for (ExManThread thread : threads) {
			thread.join();
		}

		for (ExManThread thread : threads) {
			thread.throwLastException();
		}
	}

	public void testToLogDefaultConverter() throws InterruptedException {
		// Tworzymy wyjątek
		try {
			try {
				throw new StoredException("No such user: abc", null);
			} catch (Exception ex) {
				throw new StoredException("Login user failed: abc", ex);
			}
		} catch (Exception ex) {
			new StoredException("Cannot process command", ex);
		}

		Assert.assertEquals(0, Logs.getInstance().count()); // Na razie nie ma z tego loga
		
		Thread.sleep(2000); // Czekamy 2 sekundy
		
		System.out.println(Logs.getInstance().getAsJson()); 

		Assert.assertEquals(1, Logs.getInstance().count()); // Mamy nowego loga

		// Poniżej pobieramy tego loga i sprawdzamy czy zawiera dokłądnie to co potrzeba
		Log log = Logs.getInstance().getLog(0);

		JSONObject obj = new JSONObject(JsonBuilder.build().toJson(log));

		Assert.assertNotNull(obj.getString("time"));
		Assert.assertNotNull(obj.getLong("timeMillsec") > 1570000000000L);
		Assert.assertEquals(obj.getEnum(Log.Severity.class, "severity"), Log.Severity.Error);
		Assert.assertEquals(obj.getBoolean("inspectNeeded"), true);
		Assert.assertTrue(obj.getJSONObject("params").getLong("ID wyjątku") > 0);
		Assert.assertEquals("Cannot process command", obj.getJSONObject("params").getString("tekst wyjątku"));
		
		Assert.assertEquals("Cannot process command", obj.getString("event"));

		JSONArray reasons = obj.getJSONObject("params").getJSONArray("przyczyny");
		Assert.assertNotNull(reasons);
		Assert.assertEquals(2, reasons.length());
		Assert.assertEquals("Login user failed: abc", reasons.get(0));
		Assert.assertEquals("No such user: abc", reasons.get(1));

	}

	public void testToLogOwnConverter() throws InterruptedException {

		// rejestrujemy własnego convertera
		ExceptionManager.getInstance().registerConverter((exception) -> {

			try {
				Log log = Log.build();
				log.param("ID wyjątku", exception.getId());
				log.param("przyczyny", exception.listReasons());
				log.param("tekst wyjątku", exception.getMessage());

				if (exception.getCause() != null && exception.getCause().getMessage().contains("Login user failed")) {
					String userName = StringUtils.substringAfter(exception.getCause().getMessage(),
							"Login user failed: ");
					
					log.setSeverity(Log.Severity.Warning);
					log.setInspectNeeded(false);
					
					Require.notEmpty(userName, "userName");
					log.param("Nazwa użytkownika", userName);
					log.event("Błąd logowania użytkownika");
					return true;
				}
				
				return false;
			} catch (Exception ex) {
				throw new StoredException("Cannot convert exception with ID = " + exception.getId() + " to log", ex);
			}
		});

		// Tworzymy wyjątek
		try {
			try {
				throw new StoredException("No such user: abc", null);
			} catch (Exception ex) {
				throw new StoredException("Login user failed: abc", ex);
			}
		} catch (Exception ex) {
			new StoredException("Cannot process command", ex);
		}

		Assert.assertEquals(0, Logs.getInstance().count());
		Thread.sleep(2000);
		System.out.println(Logs.getInstance().getAsJson());

		Assert.assertEquals(1, Logs.getInstance().count());

		// Poniżej pobieramy tego loga i sprawdzamy czy zawiera dokłądnie to co potrzeba
		Log log = Logs.getInstance().getLog(0);

		JSONObject obj = new JSONObject(JsonBuilder.build().toJson(log));

		Assert.assertNotNull(obj.getString("time"));
		Assert.assertNotNull(obj.getLong("timeMillsec") > 1570000000000L);
		Assert.assertEquals(obj.getEnum(Log.Severity.class, "severity"), Log.Severity.Warning);
		Assert.assertEquals(obj.getBoolean("inspectNeeded"), false);
		
		Assert.assertTrue(obj.getJSONObject("params").getLong("ID wyjątku") > 0);
		Assert.assertEquals("Cannot process command", obj.getJSONObject("params").getString("tekst wyjątku"));
		
		Assert.assertEquals("Błąd logowania użytkownika", obj.getString("event"));	
		
		Assert.assertEquals("abc", obj.getJSONObject("params").getString("Nazwa użytkownika"));
		
		JSONArray reasons = obj.getJSONObject("params").getJSONArray("przyczyny");
		Assert.assertNotNull(reasons);
		Assert.assertEquals(2, reasons.length());
		Assert.assertEquals("Login user failed: abc", reasons.get(0));
		Assert.assertEquals("No such user: abc", reasons.get(1));

	}

}
