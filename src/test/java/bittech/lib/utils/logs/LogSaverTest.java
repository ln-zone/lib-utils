package bittech.lib.utils.logs;

import bittech.lib.utils.Config;
import bittech.lib.utils.Utils;
import bittech.lib.utils.db.Database;
import bittech.lib.utils.db.DbCollection;
import bittech.lib.utils.storage.InjectStorage;
import bittech.lib.utils.tests.Container;
import bittech.lib.utils.tests.SavedLogsTester;
import com.jayway.jsonpath.DocumentContext;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.List;

public class LogSaverTest extends TestCase {

    Container mongoContainer;

    protected void setUp() {
        mongoContainer = new Container("docker-mongo/Dockerfile");
        Utils.sleep(1000);

        InjectStorage storage = new InjectStorage();

        // @formatter:off
        storage.inject("config",
                "{\"entries\":{"
                        + "  \"saveLogs\": false,"
                        + "  \"logsSaver\": {"
                        + "    \"enabled\":true,"
                        + "    \"dbUri\":\"mongodb://tron:Cy%24%24%24132@" + mongoContainer.getIp() + ":27017/?authSource=admin&ssl=false\","
                        + "    \"dbName\":\"logs\","
                        + "    \"collectionName\":\"unitTests\""
                        + "  },"
                        + "  \"logsSaver2\": {"
                        + "    \"enabled\":true,"
                        + "    \"dbUri\":\"mongodb://tron:Cy%24%24%24132@" + mongoContainer.getIp() + ":27017/?authSource=admin&ssl=false\","
                        + "    \"dbName\":\"logs\","
                        + "    \"collectionName\":\"unitTests2\""
                        + "  }"
                        + "}}");

        Config.setConfig(storage.load("config", Config.class));
    }

    @Override
    protected void tearDown() throws Exception {
        mongoContainer.close();
    }

    public void testBasic() {
        try(LogsSaver ignored = new LogsSaver()) {
            for(int i=0; i<101; i++) {
                Log.build().param("counter", i).setSeverity(Log.Severity.Warning).setInspectNeeded(true).event("Sie dodao");
            }
        }

        SavedLogsTester slt = new SavedLogsTester("logsSaver");

        DocumentContext doc = slt.waitForLog("$.params.counter", "...");

        Assert.assertEquals("100", doc.read("$.params.counter"));
        Assert.assertEquals("Warning", doc.read("$.severity"));
        Assert.assertEquals(true, doc.read("$.inspectNeeded"));

        System.out.println("RET: " + doc.jsonString());
    }

    public void testTwoSavers() {
        try(LogsSaver ignored = new LogsSaver("logsSaver");LogsSaver ignored1 = new LogsSaver("logsSaver2")) {
            for(int i=0; i<1000; i++) {
                Log.build().param("counter", i).setSeverity(Log.Severity.Warning).setInspectNeeded(true).event("Sie dodao");
            }
        }

        {   // Chack first collection
            SaverConf conf = Config.getInstance().getEntry("logsSaver", SaverConf.class);
            Database db = new Database(conf.dbUri, conf.dbName);
            DbCollection<ReadOnlyLog> dbCol = new DbCollection<>(conf.collectionName, ReadOnlyLog.class, db);

            List<ReadOnlyLog> allLogs = dbCol.listAll();

            Assert.assertEquals(1000, allLogs.size());
            for(ReadOnlyLog log : allLogs) {
                Assert.assertEquals("Sie dodao", log.event);
            }
        }

        {// Chack second collection
            SaverConf conf = Config.getInstance().getEntry("logsSaver2", SaverConf.class);
            Database db = new Database(conf.dbUri, conf.dbName);
            DbCollection<ReadOnlyLog> dbCol = new DbCollection<>(conf.collectionName, ReadOnlyLog.class, db);

            List<ReadOnlyLog> allLogs = dbCol.listAll();

            Assert.assertEquals(1000, allLogs.size());
            for(ReadOnlyLog log : allLogs) {
                Assert.assertEquals("Sie dodao", log.event);
            }
        }
    }
}
