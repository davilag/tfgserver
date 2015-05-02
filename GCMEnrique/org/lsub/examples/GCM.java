package org.lsub.examples;

import java.security.Security;
import javax.crypto.Cipher; 
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

public class GCM {
	private final static int ENCRYPT=0;
	private final static int DECRYPT=1;
	
	private byte[] key;
	private boolean debug = true;
	
	public GCM(byte[] k) {
		if(Security.getProvider("BC") == null)
			Security.addProvider(new BouncyCastleProvider());
		key = k;
	}

	private byte[] dojob(int mode, byte[] in, byte[] iv, byte[] aad) throws Exception {
		SecretKeySpec   kspec = new SecretKeySpec(key, "AES");
		IvParameterSpec ivspec = new IvParameterSpec(iv);
		Cipher  cipher = Cipher.getInstance("AES/GCM/NoPadding", "BC"); 
		
		switch(mode){
		case ENCRYPT:
			cipher.init(Cipher.ENCRYPT_MODE, kspec, ivspec);
			break;
		case DECRYPT:
			cipher.init(Cipher.DECRYPT_MODE, kspec, ivspec);
			break;
		default:
			throw new RuntimeException("bug");
		}
		if(aad != null){
			if(debug){
				System.err.println("AAD included: ");
				Hex.encode(aad, System.err);
				System.err.println();
			}
			cipher.updateAAD(aad);
		}
		return cipher.doFinal(in);
 	}
	
	public byte[] decrypt(byte[] ciphertext, byte[] iv, byte[] aad) throws Exception {
		return dojob(DECRYPT, ciphertext, iv, aad);
	}
	
	public byte[] encrypt(byte[] cleartext, byte[] iv, byte[] aad) throws Exception {
		return dojob(ENCRYPT, cleartext, iv, aad);
	}
}
