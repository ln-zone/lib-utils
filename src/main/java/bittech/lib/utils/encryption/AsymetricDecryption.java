package bittech.lib.utils.encryption;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import bittech.lib.utils.Bytes;
import bittech.lib.utils.exceptions.StoredException;

public class AsymetricDecryption {

	private static final String ASYM_ALGO = "RSA";
	private static final String SYM_ALGO = "AES";

	public static byte[] decrypt(AdvancedEncryptedData data, List<Bytes> prvKeys) {

		AdvancedEncryptedData decryptedAED = data;
		for (Bytes prvKey : prvKeys) {
			decryptedAED = AsymetricDecryption.decryptSingleLevel(decryptedAED, prvKey);
			System.out.println("Decrypted: " + decryptedAED.getData());
		}

		byte[] decrypted = decryptedAED.getData().asByteArray();

		return decrypted;
	}

	public static AdvancedEncryptedData decryptSingleLevel(AdvancedEncryptedData data, Bytes prvKey) {
		if (data.getKeys().size() == 0) {
			throw new StoredException("Data is already decrypted", null);
		}
		Bytes encryptedKey = data.getKeys().get(0);
		Bytes decryptedKey = Bytes.fromArray(decryptAsym(encryptedKey.asByteArray(), prvKey));

		byte[] encryptedData = data.getData().asByteArray();
		byte[] decryptedData = decryptSym(encryptedData, decryptedKey);

		List<byte[]> keys = new ArrayList<>();
		if (data.getKeys().size() > 1) {
			for (int i = 1; i < data.getKeys().size(); i++) {
				keys.add(data.getKeys().get(i).asByteArray());
			}
		}

		return new AdvancedEncryptedData(decryptedData, keys);
	}

	private static byte[] decryptAsym(byte[] data, Bytes prvKey) {
		try {
			PrivateKey prv = tooPrivate(prvKey);
			Cipher cipher = Cipher.getInstance(ASYM_ALGO);
			cipher.init(Cipher.DECRYPT_MODE, prv);
			return cipher.doFinal(data);
		} catch (Exception ex) {
			throw new StoredException("Failed to decrypt data using private key", ex);
		}
	}

	private static PrivateKey tooPrivate(Bytes bytes) throws Exception {
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes.asByteArray());
		KeyFactory kf = KeyFactory.getInstance(ASYM_ALGO);
		return kf.generatePrivate(spec);
	}

	private static byte[] decryptSym(byte[] data, Bytes key) {
		try {
			Cipher c = Cipher.getInstance(SYM_ALGO);
			SecretKeySpec k = new SecretKeySpec(key.asByteArray(), SYM_ALGO);
			c.init(Cipher.DECRYPT_MODE, k);
			return c.doFinal(data);
		} catch (Exception ex) {
			throw new StoredException("Failed to decrypt data using sync key", ex);
		}
	}

}
