package es.david.ptc.util;



import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Base64;




public class GaloisCounterMode {

	private static RandomString ivGenerator;
    public static String GCMEncrypt(String key,String iv,String input,String aad)
            throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException{
        //GENERAR SALIDA
    	Security.addProvider(new BouncyCastleProvider());
        byte[] keyBytes = Base64.decode(key);
        byte[] ivBytes = Base64.decode(iv);
        byte[] aadBytes = aad.getBytes("UTF-8");
        Cipher cipherEncrypt = Cipher.getInstance("AES/GCM/NoPadding");
        cipherEncrypt.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes,"AES"), new IvParameterSpec(ivBytes));
        cipherEncrypt.updateAAD(aadBytes);
        byte[] encrypted = cipherEncrypt.doFinal(input.getBytes("UTF-8"));
        //FIN GENERAR SALIDA
        return Base64.toBase64String(encrypted);

    }

    public static String GCMDecrypt(String key,String iv,String input,String aad)
            throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException{
    	Security.addProvider(new BouncyCastleProvider());
    	byte[] keyBytes = Base64.decode(key);
        byte[] ivBytes = Base64.decode(iv);
        byte[] aadBytes = aad.getBytes("UTF-8");
        Cipher cipherDecrypt = Cipher.getInstance("AES/GCM/NoPadding");
        cipherDecrypt.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes,"AES"), new IvParameterSpec(ivBytes));
        cipherDecrypt.updateAAD(aadBytes);
        byte[] decryptedBytes = Base64.decode(input);
        byte[] original = cipherDecrypt.doFinal(decryptedBytes);
        return new String(original,"UTF-8");
    }
    
    public static long getNonce(){
    	return (long) (Math.floor(Math.random()*999999999)+0);
    }
    
    public static String getIv(){
    	if(ivGenerator==null){
    		ivGenerator = new  RandomString(16);
    	}
    	try {
			return Base64.toBase64String(ivGenerator.nextString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    public static String[] getKeys(String pass){
    	try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(pass.getBytes("UTF-8"));
			byte[] resumen = md.digest();
			return new String[] {Base64.toBase64String(Arrays.copyOfRange(resumen, 0, resumen.length/2)),Base64.toBase64String(Arrays.copyOfRange(resumen,resumen.length/2,resumen.length ))};
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
}
