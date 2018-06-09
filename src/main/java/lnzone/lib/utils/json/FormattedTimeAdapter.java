package lnzone.lib.utils.json;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import lnzone.lib.utils.FormattedTime;

public class FormattedTimeAdapter extends TypeAdapter<FormattedTime> {

	@Override
	public void write(final JsonWriter out, final FormattedTime value) throws IOException {
		if (value != null) {
			out.jsonValue("\"" + value.toString() + "\"");
		} else {
			out.jsonValue(null);
		}
	}

	@Override
	public FormattedTime read(final JsonReader in) throws IOException {
		if (in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		} else {
			String str = in.nextString();
			return new FormattedTime(str);
		}
	}
}
