
public class GaloisCounterMode {
	
	private  byte[] xor(byte[] b1, byte[] b2){
		if(b1.length == b2.length){
			byte[] b3 = new byte[b1.length];
			int i = 0;
			for(byte b: b1){
				b3[i] = (byte) (b ^ b2[i++]);
			}
			return b3;
		}
		return null;
	}
	
	private byte[] intToList(int number, int listSize){
		byte[] byteReturn = new byte[listSize];
		for(int i = 0; i<listSize; i++){
			byteReturn[i] = (byte) ((number>>i) & 0xff);
		}
		
		return byteReturn;
	}
	private void GCMcrypt(int keySize, String key, String iv, String ptext, String aad){
		
	}
	public void GCMEncrypt(int keySize, String key, String iv, String ptext, String aad){
		GCMcrypt(keySize,key,iv,ptext,aad);
	}
}
