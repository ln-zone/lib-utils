package bittech.lib.utils.logs;

import bittech.lib.utils.Require;
import bittech.lib.utils.exceptions.StoredException;

public class SaverConf {

    public boolean enabled;
    public String dbUri;
    public String dbName;
    public String collectionName;

    public void verify() {
        try {
            Require.notNull(dbUri, "dbUri");
            Require.notNull(dbName, "dbName");
            Require.notNull(collectionName, "collectionName");
        } catch(Exception ex) {
            throw new StoredException("Incorrect logSaver config", ex);
        }
    }

}
