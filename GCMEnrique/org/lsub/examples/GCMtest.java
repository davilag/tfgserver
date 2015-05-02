package org.lsub.examples;

import static org.junit.Assert.*;
import org.junit.Test;

public class GCMtest {

	//128-bit key
	private byte key[] = {	
			0x01, 0x01, 0x01, 0x01, 
			0x01, 0x01, 0x01, 0x01,
			0x01, 0x01, 0x01, 0x01, 
			0x01, 0x01, 0x01, 0x01};

	//128-bit IV
	private byte iv[] = {	
			0x0f, 0x0f, 0x01, 0x01, 
			0x01, 0x01, 0x01, 0x01,
			0x01, 0x0e, 0x01, 0x01, 
			0x01, 0x09, 0x01, 0x01}; 

	@Test
	public void testString() throws Exception {
		String tocipher = "0123456789";
		String aad = "verify this!";
		
		GCM gcm = new GCM(key);
		byte[] ciphered = gcm.encrypt(tocipher.getBytes(), iv, aad.getBytes());
		byte[] deciphered = gcm.decrypt(ciphered, iv, aad.getBytes());
		
		String str = new String(deciphered);
		if(! tocipher.equals(str)){
			System.err.println("tocipher: " + tocipher);
			System.err.println("str: " + str);
			fail("test failed, string is not equal");
		}
	}

	@Test(expected = javax.crypto.AEADBadTagException.class)
	public void testAAD() throws Exception {
		String tocipher = "very secret stuff here!!!! beware!!!!!";
		String aad1 = "verify this!";
		String aad2 = "verify that!";
		
		GCM gcm = new GCM(key);
		byte[] cipher = gcm.encrypt(tocipher.getBytes(), iv, aad1.getBytes());
		gcm.decrypt(cipher, iv, aad2.getBytes());
		fail("exception expected!");
	}
}
