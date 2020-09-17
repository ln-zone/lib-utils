package bittech.lib.utils.encryption;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SymKeys {
	
	private static final String SYM_ALGO = "AES";
	
	public static List<BigInteger> generate(int count) throws Exception {
		KeyGenerator keyGen = KeyGenerator.getInstance(SYM_ALGO);
		keyGen.init(256, SecureRandom.getInstanceStrong());
		List<BigInteger> symetricKeys = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			SecretKey sc = keyGen.generateKey();
//			System.out.println("Secret key: " + new BigInteger(sc.getEncoded()).toString(16));
			symetricKeys.add(new BigInteger(sc.getEncoded()));
		}
		return symetricKeys;
	}

}
