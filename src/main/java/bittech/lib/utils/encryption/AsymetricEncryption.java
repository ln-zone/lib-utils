package bittech.lib.utils.encryption;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import bittech.lib.utils.exceptions.StoredException;

public class AsymetricEncryption {

	private static final String ASYM_ALGO = "RSA";
	private static final String SYM_ALGO = "AES";

	public static AdvancedEcryptedData encrypt(byte[] data, List<BigInteger> pubKeys) {
		try {
			List<BigInteger> symetricKeys = SymKeys.generate(pubKeys.size());
			byte[] ecnryptedData = encryptSymetric(data, symetricKeys);
			List<byte[]> encryptedKeys = encryptKeys(symetricKeys, pubKeys);
			return new AdvancedEcryptedData(ecnryptedData, encryptedKeys);
		} catch (Exception ex) {
			throw new StoredException("Failed to encrypt data", ex);
		}
	}

	public static byte[] encryptSym(byte[] data, BigInteger key) throws Exception {
		Cipher c = Cipher.getInstance(SYM_ALGO);
		SecretKeySpec k = new SecretKeySpec(key.toByteArray(), SYM_ALGO);
		c.init(Cipher.ENCRYPT_MODE, k);
		byte[] encryptedData = c.doFinal(data);
		System.out.println(encryptedData.length);
		return encryptedData;
	}

	private static byte[] encryptSymetric(byte[] data, List<BigInteger> keys) {
		try {
			byte[] toEncrypt = data;
			for (int i = keys.size() - 1; i >= 0; i--) {
				toEncrypt = encryptSym(toEncrypt, keys.get(i));
			}
			return toEncrypt;
		} catch (Exception ex) {
			throw new StoredException("Failed to encrypt symetric with all keys", ex);
		}
	}

	public static byte[] encryptWithPub(byte[] data, BigInteger pubKey) {
		try {
			Cipher cipher = Cipher.getInstance(ASYM_ALGO);
			X509EncodedKeySpec spec = new X509EncodedKeySpec(pubKey.toByteArray());
			KeyFactory kf = KeyFactory.getInstance(ASYM_ALGO);
			PublicKey pk = kf.generatePublic(spec);
			cipher.init(Cipher.ENCRYPT_MODE, pk);

			return cipher.doFinal(data);
		} catch (Exception ex) {
			throw new StoredException("Failed to encrypt data with pub key", ex);
		}
	}

	private static List<byte[]> encryptKeys(List<BigInteger> symKeys, List<BigInteger> pubKeys) {
		try {
			List<byte[]> encrypted = new ArrayList<byte[]>();
			for (int i = 0; i < symKeys.size(); i++) {
				byte[] toEncrypt = symKeys.get(i).toByteArray();
				BigInteger pubKey = pubKeys.get(i);
				System.out.println("" + i + ": " + pubKey);
				encrypted.add(encryptWithPub(toEncrypt, pubKey));
			}
			return encrypted;
		} catch (Exception ex) {
			throw new StoredException("Failed to encrypt symetric with all keys", ex);
		}
	}

}
