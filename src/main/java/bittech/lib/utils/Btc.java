package bittech.lib.utils;

import java.io.Serializable;
import java.math.BigDecimal;

public class Btc implements Serializable {

	private static final long serialVersionUID = -8508114241681334762L;

	private static final long maxSatoshis = 2099999997690000L;

	private final String value;

	private static final String regex = "(-?\\d{1,8}\\.\\d{8}\\:\\d{3}|-?\\d{1,8}\\:\\d{3}|-?\\d+\\.\\d{1,8}|-?\\d{1,8})";

	public Btc() {
		this.value = "";
	}

	public Btc(Btc btcToCopy) {
		Require.notNull(btcToCopy, "toCopy");
		this.value = btcToCopy.value;
	}

	public Btc(String value) {
		try {
			if (value == null || "".equals(value)) {
				this.value = "";
			} else {
				if (value.matches(regex) == false) {
					throw new RuntimeException("Incorrect Btc format: " + value
							+ ". Format should be x.xxxxxxxx:xxx. Or in other words have to mach regex: " + regex);
				}
				this.value = normalize(value);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Cannot parse Btc value: " + value, ex);
		}
	}

	public String toString() {
		return value;
	}

	public static Btc fromMsat(long millisatoshis) {
		try {
			String negativeChar = "";
			long msat = millisatoshis;
			if (millisatoshis < 0) {
				negativeChar = "-";
				msat = -millisatoshis;
			}

			if (msat > maxSatoshis * 1000L) {
				throw new RuntimeException("Millisatoshis amount too big. There will never be more than " + maxSatoshis
						+ "000 millisatoshis");
			}

			String formatted = String.format("%012d", msat);
			String mili = formatted.substring(formatted.length() - 3, formatted.length());
			String sato = formatted.substring(formatted.length() - 11, formatted.length() - 3);
			String btc = formatted.substring(0, formatted.length() - 11);

			return new Btc(negativeChar + btc + "." + sato + ":" + mili);
		} catch (Throwable ex) {
			throw new RuntimeException("Cannot convert milisatoshis to Btc: " + millisatoshis, ex);
		}
	}

	public static Btc fromSat(long satoshis) {
		if ((satoshis > maxSatoshis) || (satoshis < -maxSatoshis)) {
			throw new RuntimeException("Cannot convert satoshis to Btc: " + satoshis
					+ ". There will never be more than " + maxSatoshis + " satoshis");
		}
		return fromMsat(1000L * satoshis);
	}

	public static Btc fromBitcoins(BigDecimal bitcoins) {
		try {
			Require.notNull(bitcoins, "bitcoins");
			if ((bitcoins.intValue() > 21000000) || (bitcoins.intValue() < -21000000)) {
				throw new RuntimeException("Bitcoins amount too big. There will never be more than 21000000 bitcoins");
			}
			long msat = bitcoins.movePointRight(11).longValueExact();
			return fromMsat(msat);
		} catch (Exception ex) {
			throw new RuntimeException("Cannot convert bitcoins to milisatoshis: " + bitcoins, ex);
		}

	}

	public static String normalize(String amount) throws UtilsException {
		try {
			String amt = amount;
			boolean isNegative = false;
			if (amount.charAt(0) == '-') {
				amt = amount.substring(1);
				isNegative = true;
			}
			String bPart;
			String sPart;
			String mPart;

			int pointInd = amt.indexOf(".");
			if (pointInd != -1) {
				bPart = amt.substring(0, pointInd);
			} else {
				bPart = "0";
			}

			int colonInd = amt.indexOf(":");
			if (colonInd != -1) {
				mPart = amt.substring(colonInd + 1, amt.length());
			} else {
				mPart = "000";
			}

			if (pointInd != -1) {
				if (colonInd != -1) {
					sPart = amt.substring(pointInd + 1, colonInd);
				} else {
					sPart = amt.substring(pointInd + 1, amt.length());
					sPart = rightPadZeros(sPart, 8);
				}
			} else {
				if (colonInd != -1) {
					sPart = amt.substring(0, colonInd);
				} else {
					sPart = amt;
				}
			}
			sPart = leftPadZeros(sPart, 8);

			String ret = bPart + "." + sPart + ":" + mPart;
			if (isNegative) {
				if (parseMsat(ret) != 0) {
					ret = "-" + ret;
				}
			}
			return ret;
		} catch (Throwable ex) {
			throw new UtilsException("Cannot normalize bitcoin amount: " + amount, ex);
		}
	}

	private static String rightPadZeros(String str, int num) {
		return String.format("%1$-" + num + "s", str).replace(' ', '0');
	}

	private static String leftPadZeros(String str, int num) {
		return String.format("%08d", Integer.parseInt(str));
	}

	public BigDecimal toSat() {
		if ("".equals(value)) {
			throw new RuntimeException("Cannot convert to bitcoins. No value assigned to Btc class");
		}
		return new BigDecimal(toMsat()).divide(new BigDecimal(1000));
	}

	public int toSatRoundFloor() {
		if ("".equals(value)) {
			throw new RuntimeException("Cannot convert to satoshis. No value assigned to Btc class");
		}
		return (int) (toMsat() / 1000L);
	}

	public Btc add(Btc value) {
		Require.notNull(value, "value");
		if (!this.hasValue() || !value.hasValue()) {
			throw new RuntimeException("Cannot add Btc. No value assigned to Btc class");
		}
		return fromMsat(this.toMsat() + value.toMsat());
	}

	public Btc sub(Btc value) {
		Require.notNull(value, "value");
		if (!this.hasValue() || !value.hasValue()) {
			throw new RuntimeException("Cannot add Btc. No value assigned to Btc class");
		}
		return fromMsat(this.toMsat() - value.toMsat());
	}

	public double div(Btc value) {
		Require.notNull(value, "value");
		if (!this.hasValue() || !value.hasValue()) {
			throw new RuntimeException("Cannot div Btc. No value assigned to Btc class");
		}
		return (double) this.toMsat() / value.toMsat();
	}

	public Btc div(int value) {
		if (!this.hasValue()) {
			throw new RuntimeException("Cannot div Btc. No value assigned to Btc class");
		}
		return fromMsat(this.toMsat() / value);
	}

	public Btc multi(int value) {
		if (!this.hasValue()) {
			throw new RuntimeException("Cannot div Btc. No value assigned to Btc class");
		}
		return fromMsat(this.toMsat() * value);
	}

	private static long parseMsat(String value) {
		Require.notNull(value, "value");
		if ("".equals(value)) {
			throw new RuntimeException("Cannot convert to msat. No value provided");
		}
		try {
			String[] bigSplited = value.split("\\.");
			String[] smallSplited = bigSplited[1].split(":");
			long btc = Math.abs(Long.parseLong(bigSplited[0]));
			long sato = Long.parseLong(smallSplited[0]);
			long mili = Long.parseLong(smallSplited[1]);

			long msat = mili + sato * 1000L + btc * 100000000000L;
			if (value.charAt(0) == '-') {
				msat = -msat;
			}
			return msat;
		} catch (Throwable ex) {
			throw new RuntimeException("Cannot parse Btc value: " + value, ex);
		}
	}

	public long toMsat() {
		return parseMsat(value);
	}

	public BigDecimal toBitcoins() {
		if ("".equals(value)) {
			throw new RuntimeException("Cannot cnverto to bitcoins. No value assigned to Btc class");
		}
		try {
			BigDecimal msat = new BigDecimal(toMsat());
			return msat.divide(new BigDecimal(100000000000L));
		} catch (Throwable ex) {
			throw new RuntimeException("Cannot convert to Btc: " + value, ex);
		}
	}

	public boolean hasValue() {
		return !"".equals(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Btc == false) {
			return false;
		}
		Btc o = (Btc) obj;
		if (this.hasValue() != o.hasValue()) {
			return false;
		}
		if (this.hasValue() == false) {
			return true;
		}
		if (this.value.equals(o.value)) {
			return true;
		}
		return false;
	}

	public static Btc noValue() {
		return new Btc();
	}

	public static boolean HasValue(Btc btc) {
		return btc != null && btc.hasValue();
	}

}
