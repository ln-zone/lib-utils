package lnzone.lib.utils.json;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class RawJsonAdapter extends TypeAdapter<RawJson> {

	@Override
	public void write(final JsonWriter out, final RawJson value) throws IOException {
		if (value != null) {
			out.jsonValue(value.getJsonStr());
		} else {
			out.jsonValue(null);
		}
	}

	private final static void readToken(final JsonReader in, final StringBuilder jsonBuilder) throws IOException {
		JsonToken t = in.peek();
		switch (t.toString()) {
		case "NAME":
			jsonBuilder.append("\"" + in.nextName() + "\":");
			break;
		case "BEGIN_OBJECT":
			readObject(in, jsonBuilder);
			jsonBuilder.append(",");
			break;
		case "END_OBJECT":
			throw new IOException("It looks it should neve happens");
		case "BEGIN_ARRAY":
			readArray(in, jsonBuilder);
			jsonBuilder.append(",");
			break;
		case "END_ARRAY":
			throw new IOException("It looks it should neve happens");
		case "STRING":
			jsonBuilder.append("\"" + in.nextString() + "\",");
			break;
		case "NUMBER":
			String num = Double.toString(in.nextDouble());
			if (num.endsWith(".0")) {
				num = num.substring(0, num.length() - 2);
			}
			jsonBuilder.append(num + ",");
			break;
		case "BOOLEAN":
			jsonBuilder.append("" + in.nextBoolean() + ",");
			break;
		case "NULL":
			jsonBuilder.append("null,");
			in.nextNull();
			break;
		}		
	}
	
	private static final void readElements(final JsonReader in, final StringBuilder jsonBuilder) throws IOException {

		while (in.hasNext()) {
			readToken(in, jsonBuilder);
		}
		removeLastColon(jsonBuilder);
	}

	private static final void removeLastColon(final StringBuilder jsonBuilder) {
		if (jsonBuilder.charAt(jsonBuilder.length() - 1) == ',') {
			jsonBuilder.delete(jsonBuilder.length() - 1, jsonBuilder.length());
		}
	}
	
	private static final void readObject(final JsonReader in, final StringBuilder jsonBuilder) throws IOException {
		jsonBuilder.append("{");
		in.beginObject();

		readElements(in, jsonBuilder);
		
		jsonBuilder.append("}");
		in.endObject();
	}
	
	private static final void readArray(final JsonReader in, final StringBuilder jsonBuilder) throws IOException {
		jsonBuilder.append("[");
		in.beginArray();

		readElements(in, jsonBuilder);
		
		jsonBuilder.append("]");
		in.endArray();
	}

	@Override
	public RawJson read(final JsonReader in) throws IOException {
		StringBuilder jsonBuilder = new StringBuilder();
		readToken(in, jsonBuilder);
		removeLastColon(jsonBuilder);
		String ret = jsonBuilder.toString();
		return new RawJson(ret);

	}

}
