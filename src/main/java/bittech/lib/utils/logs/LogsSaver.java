package bittech.lib.utils.logs;

import bittech.lib.utils.Config;
import bittech.lib.utils.Require;
import bittech.lib.utils.Try;
import bittech.lib.utils.Utils;
import bittech.lib.utils.db.Database;
import bittech.lib.utils.db.DbCollection;
import bittech.lib.utils.exceptions.StoredException;

import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class LogsSaver implements AutoCloseable {

    Database database;
    DbCollection<Log> dbCollection;

    AtomicLong lastLogTime;

    public LogsSaver() {
        this("logsSaver");
    }

    public LogsSaver(String configEntryName) {
        try {
            SaverConf conf = Config.getInstance().getEntry(configEntryName, SaverConf.class);

            lastLogTime = new AtomicLong(0);

            if(conf.enabled == false) {
                System.out.println("!!! Log saver is disabled !!!");
                return;
            }

            database = new Database(conf.dbUri, conf.dbName);
            database.disableWriteAccessCheck();

            dbCollection = new DbCollection<>(conf.collectionName, Log.class, database);

            dbCollection.setAutoRecreateCollection(true);

            Logs.getInstance().registerNewLogListener(this::onNewLog);

        } catch (Exception ex) {
            throw new StoredException("Failed to crate LogsSaver", ex);
        }
    }

    private void waitForLoggingFinished() {
        try {
            int delta = 1000;
            int i = 0;
            while (System.currentTimeMillis() - lastLogTime.get() < delta) {
                Utils.sleep(100);
                i++;
                if (i > 50) {
                    throw new Exception("We are constantly receiving new logs");
                }
            }
        } catch(Exception ex) {
            throw new StoredException("Waiting for logging finish failed", ex);
        }
    }

    private void onNewLog(Log log) {
        if(database.isOpened()) {
            lastLogTime.set(System.currentTimeMillis());
            dbCollection.add(log);
        }
    }

    @Override
    public synchronized void close() {
        Try.printIfThrown(this::waitForLoggingFinished);
        if(database != null) {
            database.close();
        }
    }
}
