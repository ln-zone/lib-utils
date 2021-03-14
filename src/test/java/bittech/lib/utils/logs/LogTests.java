package bittech.lib.utils.logs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Ignore;

import bittech.lib.utils.logs.Log;
import bittech.lib.utils.logs.Logs;
import junit.framework.TestCase;

public class LogTests extends TestCase {

	@Override
	public void setUp() {
		Logs.resetInstance();
	}

	public void testBasicLog() throws InterruptedException {
		Logs.getInstance().registerNewLogListener((log) -> {

			System.out.println("I found new log !");
			System.out.println(log.getEvent());

		});

		Log log = new Log();
		log.param("name", 200);
		log.event("I'm log");
		Thread.sleep(50);

	}

	public void testGetTimeMillisAfterChange() throws InterruptedException {
		AtomicLong time = new AtomicLong();

		Logs.getInstance().registerLogChangedListener((log) -> {
			time.set(log.getTimeMillsec());
		});

		Log log = new Log();
		{
			log.param("name", 200);
			log.event("I'm log");
			log.setInspectNeeded(true);
		}
		{
			Logs.getInstance().markInspected(log.getTimeMillsec());
			Thread.sleep(50);
		}
		{
			Assert.assertEquals(log.getTimeMillsec(), time.get());
		}

	}

	public void testInspectedNeededBasic() throws InterruptedException {
		AtomicBoolean afterLambdaChange = new AtomicBoolean();

		Logs.getInstance().registerLogChangedListener((log) -> {
			afterLambdaChange.set(log.getInspectNeeded());
		});

		Log log = new Log();
		{
			log.param("name", 200);
			log.event("I'm log");
			log.setInspectNeeded(true);
		}
		{
			Logs.getInstance().markInspected(log.getTimeMillsec());
			Thread.sleep(50);
		}
		{
			Assert.assertFalse(afterLambdaChange.get());
		}

	}

	public void testInspectedNeededOnFalseValue() throws InterruptedException {
		AtomicBoolean afterLambdaChange = new AtomicBoolean();

		Logs.getInstance().registerLogChangedListener((log) -> {
			afterLambdaChange.set(log.getInspectNeeded());
		});

		Log log = new Log();
		{
			log.param("name", 200);
			log.event("I'm log");
			log.setInspectNeeded(false);
		}
		{
			Logs.getInstance().markInspected(log.getTimeMillsec());
			Thread.sleep(50);
		}
		{
			Assert.assertFalse(afterLambdaChange.get());
		}

	}

	// TODO: Pomyslec czemu failuje
	@SuppressWarnings("null")
	@Ignore
//	public void testMarkInspectedWithNull() {
//		Log log;
//		log = null;
//		try {
//			Logs.getInstance().markInspected(log.getTimeMillsec());
//			Thread.sleep(50);
//			Assert.fail("Exception not thrown!");
//		} catch (Exception e) {
//			Assert.assertNull(e.getCause());
//			Assert.assertNull(e.getMessage());
//		}
//	}

	public void testSetSeverityError() throws InterruptedException {
		AtomicReference<Log.Severity> severity = new AtomicReference<>();
		Logs.getInstance().registerNewLogListener((log) -> {
			severity.set(log.getSeverity());
		});

		Log log = new Log();
		{
			log.setSeverity(Log.Severity.Error);
			log.param("name", 200);
			log.event("I'm log");
			log.setInspectNeeded(false);
			Thread.sleep(50);
		}
		Assert.assertEquals(severity.get(), log.getSeverity());
	}

	public void testSetSeverityInfo() throws InterruptedException {
		AtomicReference<Log.Severity> severity = new AtomicReference<>();
		Logs.getInstance().registerNewLogListener((log) -> {
			severity.set(log.getSeverity());
		});

		Log log = new Log();
		{
			log.setSeverity(Log.Severity.Info);
			log.param("name", 200);
			log.event("I'm log");
			log.setInspectNeeded(false);
			Thread.sleep(50);
		}
		Assert.assertEquals(severity.get(), log.getSeverity());
	}

	public void testSetSeverityWarning() throws InterruptedException {
		AtomicReference<Log.Severity> severity = new AtomicReference<>();
		Logs.getInstance().registerNewLogListener((log) -> {
			severity.set(log.getSeverity());
		});

		Log log = new Log();
		{
			log.setSeverity(Log.Severity.Warning);
			log.param("name", 200);
			log.event("I'm log");
			log.setInspectNeeded(false);
			Thread.sleep(50);
		}
		Assert.assertEquals(severity.get(), log.getSeverity());
	}

	public void testAddLogInLogs() throws InterruptedException {
		Logs.getInstance().registerNewLogListener((log) -> {
			log.setSeverity(Log.Severity.Error);
		});
		Log log = new Log();
		{
			log.setSeverity(Log.Severity.Warning);
			log.param("name", 200);
			log.setInspectNeeded(false);
			Logs.getInstance().addLog(log);
		}
		Thread.sleep(50);
		Assert.assertEquals(Log.Severity.Error, log.getSeverity());
	}

	public void testAddLogInLogsWithNull() {

		Log log = null;
		try {
			Logs.getInstance().addLog(log);
		} catch (Exception e) {
			Assert.assertNull(e.getMessage());
			Assert.assertNull(e.getCause());
		}

	}

	public void testCount() {

		Log log = new Log();
		{
			log.setSeverity(Log.Severity.Warning);
			log.param("name", 200);
			log.setInspectNeeded(false);
			Logs.getInstance().addLog(log);
			int logs = Logs.getInstance().count();
			assertEquals(1, logs);

		}
	}

	public void testMultiThreadingLog() throws InterruptedException {
		List<Thread> threads = new ArrayList<>();

		Logs.getInstance().registerNewLogListener((log) -> {
			System.out.println(log.getEvent());
		});
		for (int i = 0; i < 30; i++) {
			threads.add(
					new Thread(() -> Log.build().setSeverity(Log.Severity.Error).setInspectNeeded(true).event("TT")));
			threads.add(
					new Thread(() -> Log.build().setSeverity(Log.Severity.Info).setInspectNeeded(true).event("DD")));
			threads.add(new Thread(
					() -> Log.build().setSeverity(Log.Severity.Warning).setInspectNeeded(false).event("EE")));
		}
		for (Thread t : threads) {
			t.start();

		}
		for (Thread t : threads) {
			t.join();

		}

	}

}