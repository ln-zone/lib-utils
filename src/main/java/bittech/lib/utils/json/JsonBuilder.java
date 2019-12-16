package bittech.lib.utils.json;

import java.math.BigDecimal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import bittech.lib.utils.Btc;
import bittech.lib.utils.FormattedTime;

public class JsonBuilder {

	public JsonBuilder() {
		// TODO Auto-generated constructor stub
	}

	public static final Gson build() {
		return (new GsonBuilder()).registerTypeAdapter(RawJson.class, new RawJsonAdapter())
				.registerTypeAdapter(Btc.class, new BtcAdapter())
				.registerTypeAdapter(BigDecimal.class, new BigDecimalAdapter())
				.registerTypeAdapter(FormattedTime.class, new FormattedTimeAdapter()).serializeNulls().create();
	}

	public static boolean isValid(String Json) {
		Gson gson = new Gson();
		try {
			gson.fromJson(Json, Object.class);
			return true;
		} catch (com.google.gson.JsonSyntaxException ex) {
			return false;
		}
	}

}
