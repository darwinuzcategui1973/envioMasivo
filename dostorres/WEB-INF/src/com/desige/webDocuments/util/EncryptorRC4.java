package com.desige.webDocuments.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.servlet.http.HttpServletRequest;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.desige.webDocuments.utils.ToolsHTML;

/**
 * This class encrypts and decrypts passwords
 * 
 * @author Alden Sequeira, Consis International
 */
public class EncryptorRC4 {
	public static final String KEY_GENERIC = "16-171-151-56-196-97-64-7";

	MessageDigest algorithm;
	public static String usuarios = "20";
	public static String fechaCaduca = "20201130";

	/**
	 * constructor
	 */
	public EncryptorRC4() {
		algorithm = null;

		try {
			algorithm = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			//System.out.println(e);
		}

		algorithm.reset();
	}

	/**
	 * Return the encrypted password
	 * 
	 * @param pw
	 *            the password
	 * @return password encrypted
	 */
	public String getEncryptedPassword(String pw) {
		byte[] buf = new byte[pw.length()];
		buf = pw.getBytes();

		algorithm.update(buf);

		byte[] digest1 = algorithm.digest();

		BASE64Encoder encoder = new BASE64Encoder();
		String encodedPW = encoder.encode(digest1);

		return encodedPW;
	}

	/**
	 * Checks whether the password Typed and the one in DB are equal
	 * 
	 * @param pwTyped
	 *            the password typed
	 * @param pwDB
	 *            the password in the DB
	 * @return true if equal, false, otherwise
	 */
	public boolean isPasswordEqual(String user, String pwTyped, String pwDB, boolean isAdmin, boolean isViewer) {
		return isPasswordEqual(user,pwTyped, pwDB, null, isAdmin, isViewer);
	}
	
	public boolean isPasswordEqual(String user, String pwTyped, String pwDB, HttpServletRequest request, boolean isAdmin, boolean isViewer) {
		try {
			// convert key in value to byte[]
			byte[] buf = new byte[pwTyped.length()];
			buf = pwTyped.getBytes();
			algorithm.update(buf);

			byte[] keyInDigest = algorithm.digest();

			// decode database value
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] dbDigest = decoder.decodeBuffer(pwDB);

			if(ToolsHTML.isActiveDirectoryConfigurated() && !isAdmin && !isViewer) {
				return ToolsHTML.isValidActiveDirectory(user, pwTyped);
			} else {
				return ToolsHTML.isValid(pwTyped, request) || MessageDigest.isEqual(keyInDigest, dbDigest);
			}
					
		} catch (Exception e) {
			//System.out.println(e);
			e.printStackTrace();
		}
		return false;
	}

	public static String encrypt(String keyGen, String source) {
		try {
			Key key = getKey(keyGen);
			Cipher desCipher = Cipher.getInstance("RC4");
			desCipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] cleartext = source.getBytes();
			byte[] ciphertext = desCipher.doFinal(cleartext);
			return getString(ciphertext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String generateKey() {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("RC4");
			SecretKey desKey = keygen.generateKey();
			byte[] bytes = desKey.getEncoded();
			return getString(bytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String decrypt(String keyGen, String source) {
		try {
			Key key = getKey(keyGen);
			Cipher desCipher = Cipher.getInstance("RC4");
			byte[] ciphertext = getBytes(source);
			desCipher.init(Cipher.DECRYPT_MODE, key);
			byte[] cleartext = desCipher.doFinal(ciphertext);
			return new String(cleartext);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Key getKey(String key) {
		try {
			//System.out.println("KEY = " + key);
			byte[] bytes = getBytes(key);
			DESKeySpec pass = new DESKeySpec(bytes);
			SecretKeyFactory skf = SecretKeyFactory.getInstance("RC4");

			/*
			java.security.Provider[] providers = java.security.Security.getProviders();
			javax.crypto.SecretKeyFactory skf = null;
			for (int i = 0; i < providers.length; i++) {
				//System.out.println(providers[i].getInfo()+" -> "+providers[i].getName());

				String name = providers[i].getName();

				if ("SunJCE".equals(name)) { 
					try {
						skf = javax.crypto.SecretKeyFactory.getInstance("RC4", providers[i].getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			//*/
			SecretKey s = skf.generateSecret(pass);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns true if the specified text is encrypted, false otherwise
	 */
	public static boolean isEncrypted(String text) {
		if (text.indexOf('-') == -1) {
			return false;
		}
		StringTokenizer st = new StringTokenizer(text, "-", false);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (token.length() > 3) {
				return false;
			}
			for (int i = 0; i < token.length(); i++) {
				if (!Character.isDigit(token.charAt(i))) {
					return false;
				}
			}
		}
		return true;
	}

	private static String getString(byte[] bytes) {
		StringBuffer sb = new StringBuffer(20);
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			int valor = (int) (0x00FF & b);
			sb.append(valor);
			if (i + 1 < bytes.length) {
				sb.append("-");
			}
		}
		return sb.toString();
	}

	private static byte[] getBytes(String str) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		StringTokenizer st = new StringTokenizer(str, "-", false);
		while (st.hasMoreTokens()) {
			int i = Integer.parseInt(st.nextToken());
			bos.write((byte) i);
		}
		return bos.toByteArray();
	}

	public static void main(String[] args) {
		// //System.out.println( "New key: " + generateKey() );
		BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Indique la cantidad de Licencias a Generar y pulse Enter: ");
		System.out.flush();
		try {
			////System.out.println(generateKey());
			
			usuarios = entrada.readLine();
			int numero = Integer.parseInt(usuarios);
			//System.out.println("Licencia para = " + usuarios + " usuarios ");
			String plaintext = usuarios;
			String key = "16-171-151-56-196-97-64-7";
			key = "95-104-226-37-16-250-188-66-206-52-171-42-198-201-211-161"; //RC4
			String license = encrypt(key, plaintext);
			//System.out.println("license = " + license);
			//System.out.println("FTP     = " + EncryptorMD5.getMD5(license+":FTP"));
			//System.out.println("SACOP   = " + EncryptorMD5.getMD5(license+":SACOP"));
			//System.out.println("InTouch = " + EncryptorMD5.getMD5(license+":InTouch"));
			//System.out.println("Record  = " + EncryptorMD5.getMD5(license+":Record"));
			//System.out.println("Files   = " + EncryptorMD5.getMD5(license+":Files"));
			//System.out.println("Digital = " + EncryptorMD5.getMD5(license+":Digital"));
		} catch (Exception ex) {
			//System.out.println("Error: se esperaba un entero");
			System.exit(1);
		}
		// String plaintext = usuarios;
		// String key = "16-171-151-56-196-97-64-7";
		// //System.out.println("license = " + encrypt(key,plaintext) );
		// //System.out.println(" Date = " + encrypt(key,fechaCaduca.toString())
		// );
		// String ciphertext =
		// "220-64-170-49-249-255-7-120-48-241-20-130-113-172-86-187";
		// String license = decrypt(key,ciphertext);
		// //System.out.println("license = " + license);
		// int number = -1;
		// try {
		// number = Integer.parseInt(license);
		// } catch (Exception ex) {
		// number = -1;
		// }
	}

}
