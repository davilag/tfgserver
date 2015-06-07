import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;



public class Main {
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, IOException{
		String key = "ZV54ZnTZ0+d7wF7R3je0tg==";
		String iv = "emFKaDJtVUh0YWUzdDFsdg==";
		String aad = "d.avilag23@gmail.com";

		PayloadRequest payload = new PayloadRequest();
		payload.setDominio("twitter");
		payload.setNonce(GaloisCounterMode.getNonce());
		payload.setTs(System.currentTimeMillis());
		System.out.println(payload.getNonce());
		ObjectWriter ow = new ObjectMapper().writer();
		String payloadStr = ow.writeValueAsString(payload);
		System.out.println(payloadStr);
		String payloadCif = GaloisCounterMode.GCMEncrypt(key, iv, "{\"dominio\":\"twitter\",\"nonce\":72825886,\"ts\":1431191892448}", aad);
		System.out.println("payload importante :"+payloadCif);
		RequestMessage rm = new RequestMessage();
		rm.setAad(aad);
		rm.setIv(iv);
		rm.setPayload(payloadCif);
		String message = ow.writeValueAsString(rm);
		System.out.println(message);
		
		//ENVIO EL MENSAJE
		
		//RECIBO EL MENSAJE
		ObjectMapper om = new ObjectMapper();
		RequestMessage rm2 = om.readValue(message, RequestMessage.class);
		System.out.println("iv: "+rm2.getIv());
		System.out.println("aad: "+rm2.getAad());
		System.out.println("payload: "+rm2.getPayload());
		String payloadDescif = GaloisCounterMode.GCMDecrypt(key, iv, "eM28hWne2DLYDonvv91pihqzgTX2kWNrEO2zjLGP6sh6rW7rHuHB7pvMdLTvVr8RPQDxe4DwF8HgssQaCLWdUPaMIl8IyhzxSA==" , aad);
		System.out.println(payloadDescif);
		PayloadRequest prequest = om.readValue(payloadDescif, PayloadRequest.class);
		System.out.println(prequest.getDominio());
		System.out.println(prequest.getNonce());
		System.out.println(prequest.getTs());
		System.out.println("PRUEBA DE PASS: ");
		String[] keys = GaloisCounterMode.getKeys("prueba");
		System.out.println("KS: "+keys[0]);
		System.out.println("KP: "+keys[1]);
		
	}
}
