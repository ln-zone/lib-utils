package bittech.lib.utils.encryption;

import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import bittech.lib.utils.exceptions.StoredException;

public class AsymKeys {

	public static String encAlgo = "RSA";

	public static List<AsymKeyPair> generate(int amount) {
		try {
			KeyPairGenerator keyGen;
			keyGen = KeyPairGenerator.getInstance(encAlgo);
			keyGen.initialize(1024, SecureRandom.getInstanceStrong());

			List<AsymKeyPair> keysList = new ArrayList<AsymKeyPair>(amount);

			for (int i = 0; i < amount; i++) {
				keysList.add(new AsymKeyPair(keyGen.generateKeyPair()));
			}

			return keysList;
		} catch (Exception ex) {
			throw new StoredException("Failed to generate " + amount + " asym keys", ex);
		}
	}

	public static List<BigInteger> getPubKeys(List<AsymKeyPair> asymKeys) {
		List<BigInteger> pubKeys = new ArrayList<>(asymKeys.size());
		for (AsymKeyPair keys : asymKeys) {
			pubKeys.add(keys.getPub());
		}
		return pubKeys;
	}

	public static List<BigInteger> getPrvKeys(List<AsymKeyPair> asymKeys) {
		List<BigInteger> pubKeys = new ArrayList<>(asymKeys.size());
		for (AsymKeyPair keys : asymKeys) {
			pubKeys.add(keys.getPrv());
		}
		return pubKeys;
	}

}
