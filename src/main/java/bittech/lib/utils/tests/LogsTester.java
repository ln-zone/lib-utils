package bittech.lib.utils.tests;

import bittech.lib.utils.Utils;
import bittech.lib.utils.logs.Log;
import bittech.lib.utils.logs.Logs;
import org.junit.Assert;

import java.util.LinkedList;
import java.util.List;

public class LogsTester {

    List<Log> logs = new LinkedList<>();

    public LogsTester() {
        Logs.getInstance().consumeAll((newLog) -> {
            logs.add(newLog);
        });
    }

    public Log consumeLog(String eventMsg) {
        for(Log log: logs) {
            if(log.getEvent().equals(eventMsg)) {
                logs.remove(log);
                return log;
            }
        }
        throw new RuntimeException("No such log: " + eventMsg);
    }


    public void assertNoMoreLogs() {
        if(logs.size() != 0) {
            System.err.println("Logs that was not consumed:");
            logs.forEach((log) -> Utils.prn(log));
            Assert.fail("No all logs were consumed");
        }
    }

    public void printAllLogs() {
        System.out.println("Logs amount: " + logs.size());
        logs.forEach((log) -> Utils.prn(log));
    }
}
