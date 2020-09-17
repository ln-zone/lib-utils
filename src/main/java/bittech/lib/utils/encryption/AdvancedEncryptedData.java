package bittech.lib.utils.encryption;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class AdvancedEncryptedData {

	String data;
	List<String> keys;

	public AdvancedEncryptedData(byte[] data, List<byte[]> keys) {
		this.data = new BigInteger(data).toString(16);
		this.keys = new ArrayList<String>(keys.size());
		for(byte[] encKey : keys) {
			this.keys.add(new BigInteger(encKey).toString(16));
		}

	}

}
