package pl.media4u.bonprix.memcached.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class HashUtils {

	public static String md5(String stringToDigest) {
		try {
			/* egemplarz klasy ktora wykonuje obliczenia */
			MessageDigest md = MessageDigest.getInstance("MD5");
			/* transformacja podanego stringa na tablice bytow */
			byte[] toDigestBytes = stringToDigest.getBytes();
			/* wyczyszczenie klasy liczacej */
			md.reset();
			StringBuilder result = new StringBuilder();
			/* obliczanie skrotu i odpowiednie formatowanie poszczegolnych byte'ow */
			for (byte b : md.digest(toDigestBytes)) {
				result.append(String.format("%02x", 0xff & b));
			}
			return result.toString();
		}
		catch (NoSuchAlgorithmException e) {
			/* nie powinno się wydarzyć */
			throw new RuntimeException(e);
		}
	}

}
