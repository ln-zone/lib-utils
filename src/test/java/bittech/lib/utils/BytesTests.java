package bittech.lib.utils;

import org.junit.Assert;

import junit.framework.TestCase;

public class BytesTests extends TestCase {

	public void testHex() {
		for(int i = 0; i<10000; i++) {
		byte[] bytes = randBytes();
		String str = Bytes.fromArray(bytes).asHex();
		Assert.assertArrayEquals(bytes, Bytes.fromHex(str).asByteArray());
		}
	}

	public void testBase64() {
		for(int i = 0; i<10000; i++) {
		byte[] bytes = randBytes();
		String str = Bytes.fromArray(bytes).asBase64();
		Assert.assertArrayEquals(bytes, Bytes.fromBase64(str).asByteArray());
		}
	}
	
	private byte[] randBytes() {
		int len = (int) (Math.random() * 100);
		byte[] newBytes = new byte[len];
		for (int i = 0; i < newBytes.length; i++) {
			newBytes[i] = (byte) ((Math.random() * 250) - 127);
		}
		return newBytes;
	};

}
