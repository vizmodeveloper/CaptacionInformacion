package carloslobaton.pe.captacioninformacion;

import org.apache.commons.codec.binary.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class AESCrypt {
	private static final String KEY_ENCRYPTATION = "lV9O6iEHSassL08z";
	
	
	public static String decryptText(String textToDecrypt) {
	    try {
	        byte[] decodedValue = Base64.decodeBase64(textToDecrypt.getBytes("UTF-8"));
	        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	        IvParameterSpec ips = new IvParameterSpec(iv);
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
	        Key aesKey = new SecretKeySpec(KEY_ENCRYPTATION.getBytes("UTF-8"), "AES");
	        cipher.init(Cipher.DECRYPT_MODE, aesKey, ips);
	        byte[] plainText = cipher.doFinal(decodedValue);

	        return new String(plainText);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return "";
	}
	
	public static String encryptText(String textToEncrypt) {
	    try {
	        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	        IvParameterSpec ips = new IvParameterSpec(iv);
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
	        Key aesKey = new SecretKeySpec(KEY_ENCRYPTATION.getBytes(), "AES");
	        cipher.init(Cipher.ENCRYPT_MODE, aesKey, ips);

	        byte[] cipherText = cipher.doFinal(textToEncrypt.getBytes());
	        byte[] base64encodedSecretData = Base64.encodeBase64(cipherText);
	        String secretString = new String(base64encodedSecretData);
	        return secretString;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return "";
	}

}