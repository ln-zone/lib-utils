package bittech.lib.utils.json;

import java.io.IOException;
import java.math.BigDecimal;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class BigDecimalAdapter extends TypeAdapter<BigDecimal> {

	@Override
	public void write(final JsonWriter out, final BigDecimal value) throws IOException {
		if (value != null) {
			out.jsonValue("" + value.stripTrailingZeros().toPlainString());
		} else {
			out.jsonValue(null);
		}
	}

	@Override
	public BigDecimal read(final JsonReader in) throws IOException {
		if (in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		} else {
			String str = "" + in.nextDouble();
			return new BigDecimal(str).stripTrailingZeros();
		}
	}
}
