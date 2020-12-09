package bittech.lib.utils;

import java.util.Base64;

import org.apache.commons.codec.binary.Hex;

import bittech.lib.utils.exceptions.StoredException;

public class Bytes {

	private byte[] byteArray;

	public static Bytes fromArray(byte[] byteArray) {
		return new Bytes(byteArray);
	}

	public static Bytes fromBase64(String base64) {
		return new Bytes(Base64.getDecoder().decode(base64));
	}

	public static Bytes fromHex(String hex) {
		try {
			if(hex.startsWith("0x")) {
				hex = hex.substring(2);
			}
			return Bytes.fromArray(Hex.decodeHex(hex.toCharArray()));
		} catch (Exception ex) {
			throw new StoredException("Failed to create Bytes from Hex: " + hex, ex);
		}
	}

	private Bytes(byte[] byteArray) {
		this.byteArray = Require.notNull(byteArray, "byteArray");
	}

	public byte[] asByteArray() {
		return byteArray;
	}

	public String asBase64() {
		return Base64.getEncoder().encodeToString(byteArray);
	}

	public String asHex() {
		try {
			return new String(Hex.encodeHex(byteArray));
		} catch (Exception ex) {
			throw new StoredException("Failed to cretae Hex from Bytes", ex);
		}
	}

	public String toString() {
		return asBase64();
	}

}
