package bittech.lib.utils;

import bittech.lib.utils.exceptions.StoredException;
import org.apache.commons.codec.binary.Base64;
import org.testcontainers.shaded.com.google.common.io.BaseEncoding;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Crypto {

	final static int keylength = 512;
	final static String algorithm = "RSA";

	public static RsaKeys generateKeys() throws StoredException {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
			keyGen.initialize(keylength);
			KeyPair pair = keyGen.generateKeyPair();
			return new RsaKeys(BaseEncoding.base32().lowerCase().encode(pair.getPrivate().getEncoded()),
					BaseEncoding.base32().lowerCase().encode(pair.getPublic().getEncoded()));
		} catch (Exception ex) {
			throw new StoredException("Cannot generate RSA keys", ex);
		}
	}

	private static PrivateKey getPrivateKey(final String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = BaseEncoding.base32().lowerCase().decode(key);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance(algorithm);
		return kf.generatePrivate(spec);
	}

	private static PublicKey getPublicKey(final String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] keyBytes = BaseEncoding.base32().lowerCase().decode(key);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance(algorithm);
		return kf.generatePublic(spec);
	}

	public static String encryptText(String msg, String key) throws StoredException {
		try {
			Require.notNull(msg, "msg");
			Require.notNull(key, "key");
			Cipher cipher = Cipher.getInstance(algorithm);
			if (key.length() > keylength) {
				cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(key));
			} else {
				cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(key));
			}
			return Base64.encodeBase64String(cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception ex) {
			throw new StoredException("Cannot encrypt text '" + msg + "' using key '" + key + "'", ex);
		}
	}

	public static String decryptText(String msg, String key) throws StoredException {
		try {
			Cipher cipher = Cipher.getInstance(algorithm);
			if (key.length() > keylength) {
				cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(key));
			} else {
				cipher.init(Cipher.DECRYPT_MODE, getPublicKey(key));
			}
			return new String(cipher.doFinal(Base64.decodeBase64(msg)), StandardCharsets.UTF_8);
		} catch (Exception ex) {
			throw new StoredException("Cannot decrypt text '" + msg + "' using key '" + key + "'", ex);
		}
	}

	private Crypto() {

	}

}