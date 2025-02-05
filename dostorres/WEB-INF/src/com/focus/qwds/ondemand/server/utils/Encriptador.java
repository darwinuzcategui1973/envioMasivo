package com.focus.qwds.ondemand.server.utils;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Encriptador {
	   private static MessageDigest algorithm;

	   //Singlenton...
	   private static Encriptador instancia;
	   

	   static{
		   instancia = new Encriptador();
	   }
	   
	   /**
	    *  constructor Singlenton
	    */
	   private Encriptador() {
	      algorithm = null;

	      try {
	         algorithm = MessageDigest.getInstance("MD5");
	      } catch (NoSuchAlgorithmException e) {
	         //System.out.println(e);
	      }

	      algorithm.reset();
	   }

	   /**
	    *  Return the encrypted password
	    *  @param pw the password
	    *  @return password encrypted
	    */
	   public  String getEncryptedPassword(String pw) {
	      byte[] buf = new byte[pw.length()];
	      buf = pw.getBytes();

	      algorithm.update(buf);

	      byte[] digest1 = algorithm.digest();

	      BASE64Encoder encoder = new BASE64Encoder();
	      String encodedPW = encoder.encode(digest1);

	      return encodedPW;
	   }

	   /**
	    *  Checks whether the password Typed and the one in DB are equal
	    *  @param pwTyped the password typed
	    *  @param pwDB the password in the DB
	    *  @return true if equal, false, otherwise
	    */
	    public boolean isPasswordEqual(String pwTyped, String pwDB) {
	        try {
	            //convert key in value to byte[]
	            byte[] buf = new byte[pwTyped.length()];
	            buf = pwTyped.getBytes();
	            algorithm.update(buf);

	            byte[] keyInDigest = algorithm.digest();

	            //decode database value
	            BASE64Decoder decoder = new BASE64Decoder();
	            byte[] dbDigest = decoder.decodeBuffer(pwDB);
	            return MessageDigest.isEqual(keyInDigest, dbDigest);
	        } catch (Exception e) {
	            //System.out.println(e);
	            e.printStackTrace();
	        }
	        return false;
	    }

	    public  String encrypt(String keyGen,String source) {
	        try {
	            Key key = getKey(keyGen);
	            Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	            desCipher.init(Cipher.ENCRYPT_MODE, key);
	            byte[] cleartext = source.getBytes();
	            byte[] ciphertext = desCipher.doFinal(cleartext);
	            return getString( ciphertext );
	        } catch( Exception e ) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    public  String generateKey() {
	        try {
	            KeyGenerator keygen = KeyGenerator.getInstance("DES");
	            SecretKey desKey = keygen.generateKey();
	            byte[] bytes = desKey.getEncoded();
	            return getString( bytes );
	        } catch( Exception e ) {
	            e.printStackTrace();
	            return null;
	        }
	    }

	    public  String decrypt(String keyGen,String source ) {
	        try {
	            Key key = getKey(keyGen);
	            Cipher desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
	            byte[] ciphertext = getBytes( source );
	            desCipher.init(Cipher.DECRYPT_MODE, key);
	            byte[] cleartext = desCipher.doFinal(ciphertext);
	            return new String( cleartext );
	        }  catch( Exception e ) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    private  Key getKey(String key) {
	        try {
	            byte[] bytes = getBytes(key);
	            DESKeySpec pass = new DESKeySpec( bytes );
	            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
	            SecretKey s = skf.generateSecret(pass);
	            return s;
	        } catch( Exception e ) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    /**
	    * Returns true if the specified text is encrypted, false otherwise
	    */
	    public  boolean isEncrypted( String text ) {
	        if( text.indexOf( '-' ) == -1 ) {
	            return false;
	        }
	        StringTokenizer st = new StringTokenizer( text, "-", false );
	        while( st.hasMoreTokens() ) {
	            String token = st.nextToken();
	            if( token.length() > 3 ) {
	                return false;
	            }
	            for( int i=0; i < token.length(); i++ ) {
	                if( !Character.isDigit( token.charAt( i ) ) ) {
	                    return false;
	                }
	            }
	        }
	        return true;
	    }

	    private  String getString( byte[] bytes ) {
	        StringBuffer sb = new StringBuffer(20);
	        for( int i=0; i < bytes.length; i++ ) {
	            byte b = bytes[ i ];
	            int valor = ( int )( 0x00FF & b );
	            sb.append( valor );
	            if( i+1 < bytes.length ) {
	                sb.append( "-" );
	            }
	        }
	        return sb.toString();
	    }

	    private  byte[] getBytes( String str ) {
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        StringTokenizer st = new StringTokenizer( str, "-", false );
	        while( st.hasMoreTokens() ) {
	            int i = Integer.parseInt( st.nextToken() );
	            bos.write( ( byte )i );
	        }
	        return bos.toByteArray();
	    }
	    
	    public static Encriptador getInstancia(){
	    	return instancia;
	    }

}
