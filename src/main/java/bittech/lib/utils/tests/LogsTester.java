package bittech.lib.utils.tests;

import bittech.lib.utils.Utils;
import bittech.lib.utils.logs.Log;
import bittech.lib.utils.logs.Logs;
import org.junit.Assert;

import java.util.LinkedList;
import java.util.List;

public class LogsTester {

    private final List<Log> logs = new LinkedList<>();

    public LogsTester() {
        Logs.getInstance().registerNewLogListener(this::addLog);
//        addLogs();
    }

    private synchronized void addLog(Log log) {
        Utils.prn("LOG", log);
        logs.add(log);
    }

    public synchronized Log consumeLog(String eventMsg) {
        for(Log log: logs) {
            if(log.getEvent().equals(eventMsg)) {
                logs.remove(log);
                return log;
            }
        }
        throw new RuntimeException("No such log: " + eventMsg);
    }


    public synchronized void assertNoMoreLogs() {
        if(logs.size() != 0) {
            System.err.println("Logs that were not consumed:");
            logs.forEach(Utils::prn);
            Assert.fail("No all logs were consumed");
        }
    }

    public synchronized void printAllLogs() {
        System.out.println("Logs amount: " + logs.size());
        logs.forEach(Utils::prn);
    }
}
