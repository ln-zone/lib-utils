package bittech.lib.utils.json;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import bittech.lib.utils.exceptions.StoredException;

public class RawJson implements Serializable {

	private static final long serialVersionUID = 1L;

	private final String str;

	public RawJson(Object toJson) {
		this.str = JsonBuilder.build().toJson(toJson);
	}

	public RawJson(String str) {
		this.str = str;
	}

	public String getJsonStr() {
		return str;
	}

	public String toString() {
		return str;
	}

	public byte[] getBytes() {
		try {
			return str.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			throw new StoredException("Failed to get bytes of Json", e);
		}
	}

	public <T> T fromJson(Class<T> classOfT) {
		return JsonBuilder.build().fromJson(str, classOfT);
	}

}
