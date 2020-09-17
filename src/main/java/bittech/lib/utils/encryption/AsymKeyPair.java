package bittech.lib.utils.encryption;

import java.math.BigInteger;
import java.security.KeyPair;

import bittech.lib.utils.Require;

public class AsymKeyPair {

	private final BigInteger prv;
	private final BigInteger pub;

	public AsymKeyPair(KeyPair pair) {
		Require.notNull(pair, "key pair");
		this.prv = new BigInteger(pair.getPrivate().getEncoded());
		this.pub = new BigInteger(pair.getPublic().getEncoded());
	}
	
	public AsymKeyPair(final BigInteger prv, final BigInteger pub) {
		this.prv = Require.notNull(prv, "prv");
		this.pub = Require.notNull(pub, "pub");
	}

	public BigInteger getPrv() {
		return prv;
	}

	public BigInteger getPub() {
		return pub;
	}

}
