package lnzone.lib.utils.json;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import lnzone.lib.utils.Btc;

public class BtcAdapter extends TypeAdapter<Btc> {

	@Override
	public void write(final JsonWriter out, final Btc value) throws IOException {
		if (value != null) {
			out.jsonValue("\"" + value.toString() + "\"");
		} else {
			out.jsonValue(null);
		}
	}

	@Override
	public Btc read(final JsonReader in) throws IOException {
		if (!in.hasNext()) {
			return null;
		} else {
			String str = in.nextString();
			return new Btc(str);
		}
	}
}
