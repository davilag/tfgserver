package es.david.httptest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class PruebaJson {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashMap<String,Usuario> users = new HashMap<String,Usuario>();
		for (int i = 0; i<10; i++){
			Usuario u = new Usuario();
			u.setUsername("usuario"+i);
			u.setServerKey("serverKey"+i);
			for(int j = 0; j<5; j++){
				u.addContainer("regId"+i+""+j);
			}
			u.addRequester("hostname"+i);
			users.put("usuario"+i, u);
		}
		ObjectWriter ow = new ObjectMapper().writer();
		ObjectWriter owm = ow.withDefaultPrettyPrinter();
		File f = new File("/tmp/prueba.json");
		try {
			if(!f.exists())
			f.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			System.out.println(owm.writeValueAsString(users));
			ow.writeValue(f, users);
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ObjectMapper or = new ObjectMapper();
		
		HashMap<String, Usuario> users1 = null;
		try {
			System.out.println("USERS 1");
			users1 = or.readValue(f, HashMap.class);
			System.out.println(owm.writeValueAsString(users1));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Prueba string aleatorios");
		for (int i=0; i<10;i ++){
			RandomString rs = new RandomString(10);
			System.out.println(rs.nextString());
		}
		
	}

}
