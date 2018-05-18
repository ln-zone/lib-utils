package lnzone.lib.utils.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lnzone.lib.utils.Btc;

public class JsonBuilder {

	public JsonBuilder() {
		// TODO Auto-generated constructor stub
	}

	public static final Gson build() {
		return (new GsonBuilder()).registerTypeAdapter(RawJson.class, new RawJsonAdapter())
				.registerTypeAdapter(Btc.class, new BtcAdapter()).serializeNulls().create();
	}

}
