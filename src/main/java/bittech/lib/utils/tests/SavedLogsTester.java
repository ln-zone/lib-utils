package bittech.lib.utils.tests;

import bittech.lib.utils.Config;
import bittech.lib.utils.Utils;
import bittech.lib.utils.db.Database;
import bittech.lib.utils.db.DbCollection;
import bittech.lib.utils.json.JsonBuilder;
import bittech.lib.utils.logs.SaverConf;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.util.List;

public class SavedLogsTester {

    private final DbCollection<Object> dbCol;

    public SavedLogsTester(String configName) {
        SaverConf conf = Config.getInstance().getEntry(configName, SaverConf.class);
        Database db = new Database(conf.dbUri, conf.dbName);
        dbCol = new DbCollection<>(conf.collectionName, Object.class, db);
    }

    public void printLogs() {
        List<Object> allLogs = dbCol.listAll();

        for (Object log : allLogs) {
            DocumentContext doc = JsonPath.parse(JsonBuilder.build().toJson(log));
            String value = doc.read("$.event");

            Utils.prn(value + "   :   ", log);
//            Assert.assertEquals("Sie dodao", log.event);
        }
    }

    public DocumentContext waitForLog(String path, String regex) {

        for (int i = 0; i < 100; i++) {
            List<Object> allLogs = dbCol.listAll();
            for (Object log : allLogs) {
                DocumentContext doc = JsonPath.parse(JsonBuilder.build().toJson(log));
                String value = doc.read(path);
                if (value.matches(regex)) {
                    return doc;
//                System.out.println("Time: " + count);
                }
            }

            Utils.sleep(10);
        }

        throw new RuntimeException("No log maching criteria: path: " + path + " , regex: " + regex);

    }


}