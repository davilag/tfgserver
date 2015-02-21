import java.security.MessageDigest;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Security.addProvider(new BouncyCastleProvider());
		try {
			byte[] key = MessageDigest.getInstance("MD5").digest("som3C0o7p@s5".getBytes());
			byte[] iv = Hex.decode("EECE34808EF2A9ACE8DF72C9C475D751");
			String plainTextIn = "Hola mundo";
			
			Cipher cipherEncrypt = Cipher.getInstance("AES/GCM/NoPadding", BouncyCastleProvider.PROVIDER_NAME);
			cipherEncrypt.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key,"AES"), new IvParameterSpec(iv));
			byte[] encrypted = cipherEncrypt.doFinal(plainTextIn.getBytes());
			byte[] encryptedValue = Base64.encodeBase64(encrypted);
			
			System.out.println("La salida del encrypt es: "+encryptedValue);
			Cipher cipherDecrypt = Cipher.getInstance("AES/GCM/NoPadding", BouncyCastleProvider.PROVIDER_NAME);
			cipherDecrypt.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key,"AES"), new IvParameterSpec(iv));
			byte[] decryptedBytes = Base64.decodeBase64(encryptedValue);
			byte[] original = cipherDecrypt.doFinal(decryptedBytes);
			System.out.println(new String(original));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
