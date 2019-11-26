package bittech.lib.utils;

/**
 * Keys encoded base 32
 *
 */
public class RsaKeys {

	private final String prv;
	private final String pub;

	public RsaKeys(final String prv, final String pub) {
		this.prv = prv;
		this.pub = pub;
	}

	public String getPrv() {
		return prv;
	}

	public String getPub() {
		return pub;
	}

}
