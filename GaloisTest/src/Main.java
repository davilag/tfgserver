import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;



public class Main {
	/*
	public static byte[] encrypt(String key,String plainText, String aad, String iv, String ctext, String tag) throws Exception{
		byte[] byteKey = Hex.decode(key);
		byte[] byteIv = null;
		if(iv!=null){
			byteIv = Hex.decode(iv);
		}
		byte[] byteTag = Hex.decode(tag.toUpperCase());

		System.out.println("El tamaño de byteKey: "+byteKey.length);
		if(iv!=null)
			System.out.println("El tamaño de byteIv: "+byteIv.length);
		System.out.println("El texto plano es: "+plainText);
		System.out.println("El tamaño de tag es: "+byteTag.length);
		byte[] byteEncrypt = new byte[key.length()+byteTag.length];
		for(int i = 0; i<byteTag.length;i++){
			byteEncrypt[i] = byteTag[i];
		}
		for(int i = byteTag.length; i<byteEncrypt.length;i++){
			byteEncrypt[i] = byteTag[i-byteTag.length];
		}
		Cipher cipherEncrypt = Cipher.getInstance("AES/GCM/NoPadding", BouncyCastleProvider.PROVIDER_NAME);
		if(iv!=null){
			cipherEncrypt.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(byteEncrypt,"AES"), new IvParameterSpec(byteIv)k);
		}else{
			cipherEncrypt.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(byteEncrypt,"AES"));
		}
		byte[] encrypted = cipherEncrypt.doFinal(plainText.getBytes());
		byte[] encryptedValue = Base64.encodeBase64(encrypted);
		return encryptedValue;
	}
	
	public static String decrypt(byte[] encryptText,String key, String aad, String iv, String ctext){
		byte[] byteKey = Hex.decode(key);
		
	}
	*/
	
	public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, UnsupportedEncodingException {
//		byte[]key = Base64.decode("u/Gu5posvwDsXUnV5Zaq4g==");
		String key = "MTIzNDU2Nzg5MDk4NzY1NA==";
		String iv = "NzYzNDI1MTA5ODQ2MzgyNQ==";
		String input = "Hola esto es una prueba";
		String aad = "12457423556432ghd";
		GaloisCounterMode gcm = new GaloisCounterMode();
		try {
			String encrypt = gcm.GCMEncrypt(key, iv, input, aad);
			String decrypt = gcm.GCMDecrypt(key, iv, encrypt, aad);
			String[] hash = gcm.hashSHA256("d916099356.T7");
			System.out.println("Salida hash: "+hash[0] +"   "+ hash[1]);
			System.out.println("Salida encrypt: "+encrypt);
			System.out.println("Salida decrypt: "+decrypt);
			System.out.println("Salida: "+decrypt);
		} catch (InvalidKeyException | NoSuchAlgorithmException
				| NoSuchProviderException | NoSuchPaddingException
				| IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		
		
	}
}
