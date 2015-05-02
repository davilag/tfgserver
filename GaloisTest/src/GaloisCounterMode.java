import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;


public class GaloisCounterMode {

	public  String GCMEncrypt(String key,String iv,String input,String aad) 
			throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException{
		//GENERAR SALIDA
		Security.addProvider(new BouncyCastleProvider());
		byte[] keyBytes = Base64.decode(key);
		byte[] ivBytes = Base64.decode(iv);
		byte[] aadBytes = aad.getBytes("UTF-8");
		Cipher cipherEncrypt = Cipher.getInstance("AES/GCM/NoPadding",BouncyCastleProvider.PROVIDER_NAME);
		cipherEncrypt.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes,"AES"), new IvParameterSpec(ivBytes));
		cipherEncrypt.updateAAD(aadBytes);
		byte[] encrypted = cipherEncrypt.doFinal(input.getBytes("UTF-8"));
		//FIN GENERAR SALIDA
		return Base64.toBase64String(encrypted);
		
	}
	
	public  String GCMDecrypt(String key,String iv,String input,String aad)
			throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException{
		Security.addProvider(new BouncyCastleProvider());
		byte[] keyBytes = Base64.decode(key);
		byte[] ivBytes = Base64.decode(iv);
		byte[] aadBytes = aad.getBytes("UTF-8");
		Cipher cipherDecrypt = Cipher.getInstance("AES/GCM/NoPadding",BouncyCastleProvider.PROVIDER_NAME);
		cipherDecrypt.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes,"AES"), new IvParameterSpec(ivBytes));
		cipherDecrypt.updateAAD(aadBytes);
		byte[] decryptedBytes = Base64.decode(input);
		byte[] original = cipherDecrypt.doFinal(decryptedBytes);
		return new String(original,"UTF-8");
	}
	
	public String[] hashSHA256(String pass) throws UnsupportedEncodingException, NoSuchAlgorithmException{
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] passBytes = pass.getBytes("UTF-8");
		md.update(passBytes);
		byte[] resumen = md.digest();
		return new String[]{Base64.toBase64String(Arrays.copyOfRange(resumen, 0, resumen.length/2)),
				Base64.toBase64String(Arrays.copyOfRange(resumen, resumen.length/2, resumen.length))};
	}
}
