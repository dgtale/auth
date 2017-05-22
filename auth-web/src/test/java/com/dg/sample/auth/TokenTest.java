package com.dg.sample.auth;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import io.jsonwebtoken.impl.crypto.MacProvider;

public class TokenTest {

	@Test
	public void generateKey() {
		// create new key
		try {
			SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();
			String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
			System.out.println(encodedKey);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// get base64 encoded version of the key
		
		
		Key key = MacProvider.generateKey();
		String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
		System.out.println(encodedKey);
	}
	
	@Test
	public void decodeKey() {
		String encodedKey = "ooJXPj7xFYjo0pLDY1Rthg==";
//		String keyStr = "NcJ777mdofILIyUOJkaMNygaqYgjq4gPBnHrOhWf9j/sNTpkqeWIpDNaJDpbcX23a9EXJJ+Kni3haUFcJyVTLQ==";
		
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES"); 
	}
}
