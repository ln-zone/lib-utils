package lnzone.lib.utils.json;

import java.io.Serializable;

public class RawJson implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String str;
	
	public RawJson(Object toJson) {
		str = JsonBuilder.build().toJson(toJson);
	}
	
	public String getJsonStr() {
		return str;
	}
	
	public String toString() {
		return str;
	}

}
