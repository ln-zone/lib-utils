package bittech.lib.utils.db;

import bittech.lib.utils.Require;
import bittech.lib.utils.json.RawJson;

public class Wrapper {
    RawJson value;

    public Wrapper(RawJson value) {
        this.value = Require.notNull(value, "value");
    }
}
