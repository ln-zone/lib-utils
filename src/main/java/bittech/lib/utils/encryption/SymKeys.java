package bittech.lib.utils.encryption;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import bittech.lib.utils.Bytes;
import bittech.lib.utils.Utils;

public class SymKeys {

	private static final String SYM_ALGO = "AES";

	public static List<Bytes> generate(int count) throws Exception {
		KeyGenerator keyGen = KeyGenerator.getInstance(SYM_ALGO);
		keyGen.init(256, SecureRandom.getInstanceStrong());
		List<Bytes> symetricKeys = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			SecretKey sc = keyGen.generateKey();
			Utils.prn(sc.getEncoded());
//			System.out.println("Secret key: " + new BigInteger(sc.getEncoded()).toString(16));
			symetricKeys.add(Bytes.fromArray(sc.getEncoded()));
		}
		return symetricKeys;
	}

}
