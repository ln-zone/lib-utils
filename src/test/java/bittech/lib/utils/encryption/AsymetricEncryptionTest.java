package bittech.lib.utils.encryption;

import java.util.List;

import org.junit.Assert;

import junit.framework.TestCase;

public class AsymetricEncryptionTest extends TestCase {

	public void testBasic() {

		List<AsymKeyPair> asymKeys = AsymKeys.generate(3);

		byte[] data = new byte[] { -4 };
		AdvancedEcryptedData encrypted = AsymetricEncryption.encrypt(data, AsymKeys.getPubKeys(asymKeys));
		byte[] decrypted = AsymetricDecryption.decrypt(encrypted, AsymKeys.getPrvKeys(asymKeys));
		Assert.assertArrayEquals(data, decrypted);
	}

	public void testBigData() {

		List<AsymKeyPair> asymKeys = AsymKeys.generate(3);

		byte[] data = new byte[10000];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) ((Math.random() * 2 * Byte.MAX_VALUE) - Byte.MAX_VALUE);
		}
		AdvancedEcryptedData encrypted = AsymetricEncryption.encrypt(data, AsymKeys.getPubKeys(asymKeys));
		byte[] decrypted = AsymetricDecryption.decrypt(encrypted, AsymKeys.getPrvKeys(asymKeys));
		Assert.assertArrayEquals(data, decrypted);
	}

	/*
	 * TODO: Remove public void testAsym() { List<Keys> asymKeys =
	 * AsymKeys.generate(3);
	 * 
	 * byte[] data = new byte[] { -4 };
	 * 
	 * byte[] encrypted = AsymetricEncryption.encryptWithPub(data,
	 * asymKeys.get(0).getPub()); byte[] decrypted =
	 * AsymetricDecryption.decryptAsym(encrypted, asymKeys.get(0).getPrv());
	 * Assert.assertArrayEquals(data, decrypted); }
	 * 
	 * public void testSym() throws Exception {
	 * 
	 * byte[] symKey = SymKeys.generate(1).get(0);
	 * 
	 * byte[] data = new byte[] { -4 };
	 * 
	 * byte[] encrypted = AsymetricEncryption.encryptSym(data, symKey); byte[]
	 * decrypted = AsymetricDecryption.decryptSym(encrypted, symKey);
	 * Assert.assertArrayEquals(data, decrypted); }
	 */
}
