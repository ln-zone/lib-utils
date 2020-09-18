package bittech.lib.utils.encryption;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import bittech.lib.utils.Utils;
import bittech.lib.utils.exceptions.StoredException;

public class AsymetricDecryption {

	private static final String ASYM_ALGO = "RSA";
	private static final String SYM_ALGO = "AES";

	public static byte[] decrypt(AdvancedEncryptedData data, List<BigInteger> prvKeys) {

		List<BigInteger> symKeys = decyptKeys(data.getKeys(), prvKeys);
		Utils.prnList(symKeys);

		byte[] toDecrypt = new BigInteger(data.getData(), 16).toByteArray();
		for (BigInteger symKey : symKeys) {
			toDecrypt = decryptSym(toDecrypt, symKey);
		}

		return toDecrypt;
	}
	
	public static AdvancedEncryptedData decryptSingleLevel(AdvancedEncryptedData data, BigInteger prvKey) {
		if(data.getKeys().size() == 0) {
			throw new StoredException("Data is already encrypted", null);
		}
		BigInteger encryptedKey = new BigInteger(data.getKeys().get(0), 16);
		BigInteger decryptedKey = new BigInteger(decryptAsym(encryptedKey.toByteArray(), prvKey));
		
		byte[] encryptedData = new BigInteger(data.getData(), 16).toByteArray();
		byte[] decryptedData = decryptSym(encryptedData, decryptedKey);
		
		List<byte[]> keys = new ArrayList<>();
		if(data.getKeys().size() > 1) {
			for(int i=1; i<data.getKeys().size(); i++) {
				keys.add(new BigInteger(data.getKeys().get(i), 16).toByteArray());
			}
		}
		
		return new AdvancedEncryptedData(decryptedData, keys);
	}

	private static List<BigInteger> decyptKeys(List<String> encryptedKeys, List<BigInteger> prvKeys) {
		List<BigInteger> ret = new ArrayList<>(encryptedKeys.size());
		int pos = 0;
		for (BigInteger key : prvKeys) {
			BigInteger encryptedKey = new BigInteger(encryptedKeys.get(pos), 16);
			byte[] decryptedKey = decryptAsym(encryptedKey.toByteArray(), key);
			ret.add(new BigInteger(decryptedKey));
			pos++;
		}
		return ret;
	}

	private static byte[] decryptAsym(byte[] data, BigInteger prvKey) {
		try {
			PrivateKey prv = bigIntToPrivate(prvKey);
			Cipher cipher = Cipher.getInstance(ASYM_ALGO);
			cipher.init(Cipher.DECRYPT_MODE, prv);
			return cipher.doFinal(data);
		} catch (Exception ex) {
			throw new StoredException("Failed to decrypt data using private key", ex);
		}
	}

	private static PrivateKey bigIntToPrivate(BigInteger asBigInt) throws Exception {
		byte[] keyBytes = asBigInt.toByteArray();
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance(ASYM_ALGO);
		return kf.generatePrivate(spec);
	}

	private static byte[] decryptSym(byte[] data, BigInteger key) {
		try {
			Cipher c = Cipher.getInstance(SYM_ALGO);
			SecretKeySpec k = new SecretKeySpec(key.toByteArray(), SYM_ALGO);
			c.init(Cipher.DECRYPT_MODE, k);
			return c.doFinal(data);
		} catch (Exception ex) {
			throw new StoredException("Failed to decrypt data using sync key", ex);
		}
	}

}
