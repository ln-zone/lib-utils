package bittech.lib.utils.encryption;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class AdvancedEncryptedData {

	private final String data;
	private final List<String> keys;

	public AdvancedEncryptedData(byte[] data, List<byte[]> keys) {
		this.data = new BigInteger(data).toString(16);
		System.out.println("Decrypted data: " + this.data);
		this.keys = new ArrayList<String>(keys.size());
		for (byte[] encKey : keys) {
			this.keys.add(new BigInteger(encKey).toString(16));
		}
	}

	public String getData() {
		return data;
	}
	
	public List<String> getKeys() {
		return keys;
	}
	
	public boolean isFullyDecrypted() {
		return keys.size() == 0;
	}
	
	public byte[] toByteArray() {
		ByteBuffer bb = ByteBuffer.allocate(100000);
		
		byte[] bData = new BigInteger(data, 16).toByteArray();
		bb.putInt(bData.length);
		bb.put(bData);
		
		bb.putInt(keys.size());
		for(String strKey : keys) {
			byte[] bKey = new BigInteger(strKey, 16).toByteArray();
			bb.putInt(bKey.length);
			bb.put(bKey);
		}
		
		return bb.array();	
	}
	
	public static AdvancedEncryptedData fromByteArray(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		int dataSize = bb.getInt();
		byte[] bData = new byte[dataSize];
		bb.get(bData, 0, dataSize);
		
		int keysCount = bb.getInt();
		List<byte[]> keys = new ArrayList<>(keysCount);
		for(int i =0; i<keysCount; i++) {
			int keyLen = bb.getInt();
			byte[] bKey = new byte[keyLen];
			bb.get(bKey, 0, keyLen);
			keys.add(bKey);
		}
		
		return new AdvancedEncryptedData(bData, keys);
		
	}
	

}
