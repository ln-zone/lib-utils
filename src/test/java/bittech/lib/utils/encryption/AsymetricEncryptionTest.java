package bittech.lib.utils.encryption;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
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

		System.out.println("PUB: " + asymKeys.get(0).getPub().asBase64());
		System.out.println("PRV: " + asymKeys.get(0).getPrv().asBase64());

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

		byte[] decrypted = decryptedAED.getData().asByteArray();
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

	public void testNoKeys() {
		byte[] data = { 1, 5, 8, 123, -76 };

		List<byte[]> keys = new ArrayList<byte[]>();

		AdvancedEncryptedData encrypted = new AdvancedEncryptedData(data, keys);
		byte[] bytes = encrypted.toByteArray();
		AdvancedEncryptedData encrypted2 = AdvancedEncryptedData.fromByteArray(bytes);
		Assert.assertTrue(Utils.deepEquals(encrypted, encrypted2));
	}

	public void testBigInteger() {
		byte[] bytes = new byte[] { 0, 1, 2, 3 };
		BigInteger bigInt = new BigInteger(bytes);
		System.out.println(bigInt.toString(16));
		byte[] retBytes = bigInt.toByteArray();
		Utils.prn(retBytes);
	}

	public void testMasive1() {
		List<AsymKeyPair> asymKeys = AsymKeys.generate(1);
		
		for (int i = 0; i < 1000; i++) {
			byte[] data = RandomUtils.nextBytes(RandomUtils.nextInt(3, 100));

			AdvancedEncryptedData encrypted = AsymetricEncryption.encrypt(data, AsymKeys.getPubKeys(asymKeys));

			byte[] decrypted = AsymetricDecryption.decrypt(encrypted, AsymKeys.getPrvKeys(asymKeys));

			Assert.assertArrayEquals(data, decrypted);
		}
	}
	
	public void testMasive2() {
		List<AsymKeyPair> asymKeys = AsymKeys.generate(2);
		
		for (int i = 0; i < 1000; i++) {
			byte[] data = RandomUtils.nextBytes(RandomUtils.nextInt(3, 100));

			AdvancedEncryptedData encrypted = AsymetricEncryption.encrypt(data, AsymKeys.getPubKeys(asymKeys));

			byte[] decrypted = AsymetricDecryption.decrypt(encrypted, AsymKeys.getPrvKeys(asymKeys));

			Assert.assertArrayEquals(data, decrypted);
		}
	}
	
	public void testMasive3() {
		List<AsymKeyPair> asymKeys = AsymKeys.generate(3);
		
		for (int i = 0; i < 1000; i++) {
			byte[] data = RandomUtils.nextBytes(RandomUtils.nextInt(3, 100));

			AdvancedEncryptedData encrypted = AsymetricEncryption.encrypt(data, AsymKeys.getPubKeys(asymKeys));

			byte[] decrypted = AsymetricDecryption.decrypt(encrypted, AsymKeys.getPrvKeys(asymKeys));

			Assert.assertArrayEquals(data, decrypted);
		}
	}

}
