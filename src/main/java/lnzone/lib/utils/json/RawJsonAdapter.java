package lnzone.lib.utils.json;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class RawJsonAdapter extends TypeAdapter<RawJson> {

    @Override
    public void write(final JsonWriter out, final RawJson value) throws IOException {
    	if(value != null) {
    		out.jsonValue(value.getJsonStr());
    	} else {
    		out.jsonValue(null);
    	}
    }

    @Override
    public RawJson read(final JsonReader in) throws IOException {
        return null; // Not supported
    }
}
