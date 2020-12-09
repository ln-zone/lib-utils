package bittech.lib.utils.encryption;

import java.security.KeyPair;

import bittech.lib.utils.Bytes;
import bittech.lib.utils.Require;

public class AsymKeyPair {

	private final Bytes prv;
	private final Bytes pub;

	public AsymKeyPair(KeyPair pair) {
		Require.notNull(pair, "key pair");
		this.prv = Bytes.fromArray(pair.getPrivate().getEncoded());
		this.pub = Bytes.fromArray(pair.getPublic().getEncoded());
	}

	public AsymKeyPair(final Bytes prv, final Bytes pub) {
		this.prv = Require.notNull(prv, "prv");
		this.pub = Require.notNull(pub, "pub");
	}

	public Bytes getPrv() {
		return prv;
	}

	public Bytes getPub() {
		return pub;
	}

}
