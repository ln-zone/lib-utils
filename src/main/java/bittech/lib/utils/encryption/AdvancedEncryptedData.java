package bittech.lib.utils.encryption;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bittech.lib.utils.Bytes;

public class AdvancedEncryptedData {

	private final Bytes data;
	private final List<Bytes> keys;

	public AdvancedEncryptedData(byte[] data, List<byte[]> keys) {
		this.data = Bytes.fromArray(data);
//		System.out.println("Decrypted data: " + this.data);
		this.keys = new ArrayList<Bytes>(keys.size());
		for (byte[] encKey : keys) {
			this.keys.add(Bytes.fromArray(encKey));
		}
	}

	public Bytes getData() {
		return data;
	}

	public List<Bytes> getKeys() {
		return keys;
	}

	public boolean isFullyDecrypted() {
		return keys.size() == 0;
	}

	public byte[] toByteArray() {

		ByteBuffer bb = ByteBuffer.allocate(100000);

		byte[] bData = data.asByteArray();
		bb.putInt(bData.length);
		bb.put(bData);

		bb.putInt(keys.size());
		for (Bytes strKey : keys) {
			byte[] bKey = strKey.asByteArray();
			bb.putInt(bKey.length);
			bb.put(bKey);
		}

		return Arrays.copyOf(bb.array(), bb.position());
	}

	public static AdvancedEncryptedData fromByteArray(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		int dataSize = bb.getInt();
		byte[] bData = new byte[dataSize];
		bb.get(bData, 0, dataSize);

		int keysCount = bb.getInt();
		List<byte[]> keys = new ArrayList<>(keysCount);
		for (int i = 0; i < keysCount; i++) {
			int keyLen = bb.getInt();
			byte[] bKey = new byte[keyLen];
			bb.get(bKey, 0, keyLen);
			keys.add(bKey);
		}

		return new AdvancedEncryptedData(bData, keys);

	}

}
