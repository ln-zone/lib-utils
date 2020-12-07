package bittech.lib.utils.encryption;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import bittech.lib.utils.Utils;
import junit.framework.TestCase;

public class AsymetricEncryptionTest extends TestCase {

	public void testBasic() {

		List<AsymKeyPair> asymKeys = AsymKeys.generate(3);

		byte[] data = new byte[] { -4 };
		AdvancedEncryptedData encrypted = AsymetricEncryption.encrypt(data, AsymKeys.getPubKeys(asymKeys));
		byte[] decrypted = AsymetricDecryption.decrypt(encrypted, AsymKeys.getPrvKeys(asymKeys));
		Assert.assertArrayEquals(data, decrypted);
	}

	public void testBigData() {

		List<AsymKeyPair> asymKeys = AsymKeys.generate(3);

		byte[] data = new byte[10000];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) ((Math.random() * 2 * Byte.MAX_VALUE) - Byte.MAX_VALUE);
		}
		AdvancedEncryptedData encrypted = AsymetricEncryption.encrypt(data, AsymKeys.getPubKeys(asymKeys));
		byte[] decrypted = AsymetricDecryption.decrypt(encrypted, AsymKeys.getPrvKeys(asymKeys));
		Assert.assertArrayEquals(data, decrypted);
	}

	public void testDecryptByLevels() {

		List<AsymKeyPair> asymKeys = AsymKeys.generate(3);

		byte[] data = new byte[129];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) ((Math.random() * 2 * Byte.MAX_VALUE) - Byte.MAX_VALUE);
		}
		AdvancedEncryptedData encrypted = AsymetricEncryption.encrypt(data, AsymKeys.getPubKeys(asymKeys));

		AdvancedEncryptedData decryptedAED = encrypted;
		for (int i = 0; i < 3; i++) {
			decryptedAED = AsymetricDecryption.decryptSingleLevel(decryptedAED, asymKeys.get(i).getPrv());
		}

		byte[] decrypted = new BigInteger(decryptedAED.getData(), 16).toByteArray();
		Assert.assertArrayEquals(data, decrypted);
	}

	public void testAdvancedEncryptedDatatoBinary() {
		byte[] data = { 1, 5, 8, 123, -76 };

		List<byte[]> keys = new ArrayList<byte[]>(2);
		keys.add(new byte[] { 1, 5, 9, -2, 0 });
		keys.add(new byte[] { 0, 5, -100, 3, 0 });

		AdvancedEncryptedData encrypted = new AdvancedEncryptedData(data, keys);
		byte[] bytes = encrypted.toByteArray();
		AdvancedEncryptedData encrypted2 = AdvancedEncryptedData.fromByteArray(bytes);
		Assert.assertTrue(Utils.deepEquals(encrypted, encrypted2));
	}

}
