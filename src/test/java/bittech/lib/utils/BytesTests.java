package bittech.lib.utils;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;

import junit.framework.TestCase;

public class BytesTests extends TestCase {

	public void testHex() {
		for(int i = 0; i<10000; i++) {
		byte[] bytes = RandomUtils.nextBytes(RandomUtils.nextInt(3, 100));
		String str = Bytes.fromArray(bytes).asHex();
		Assert.assertArrayEquals(bytes, Bytes.fromHex(str).asByteArray());
		}
	}

	public void testBase64() {
		for(int i = 0; i<10000; i++) {
			RandomUtils.nextBytes(20);
		byte[] bytes = RandomUtils.nextBytes(RandomUtils.nextInt(3, 100));
		String str = Bytes.fromArray(bytes).asBase64();
		Assert.assertArrayEquals(bytes, Bytes.fromBase64(str).asByteArray());
		}
	}
	
	public void testBytes() {
		Bytes bytes = Bytes.fromArray("gowno".getBytes());
		System.out.println("Bytes: " + bytes);
		Assert.assertArrayEquals("gowno".getBytes(), bytes.asByteArray());
	}	

}
