package com.dg.sample.auth;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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
		String encodedKey = getKey();
//		String keyStr = "NcJ777mdofILIyUOJkaMNygaqYgjq4gPBnHrOhWf9j/sNTpkqeWIpDNaJDpbcX23a9EXJJ+Kni3haUFcJyVTLQ==";

		// rebuild key using SecretKeySpec
		SecretKey originalKey = decodeKey(encodedKey);
	}
	
	private String getKey() {
		return "ooJXPj7xFYjo0pLDY1Rthg==";
	}
	
	public SecretKey decodeKey(String encodedKey) {
		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
		return originalKey;
	}

	@Test
	public void generateTokenForAdmin() {
		Key key = decodeKey(getKey());
		Calendar cal = GregorianCalendar.getInstance();
		long now = cal.getTimeInMillis();
		cal.add(Calendar.HOUR_OF_DAY, 24);

		String compactJws = Jwts.builder()
				.setIssuer("sample.com")
				.setIssuedAt(new Date(now))
				.setExpiration(cal.getTime())
				.setSubject("dgtale@hotmail.com")
				.signWith(SignatureAlgorithm.HS512, key)
				.compact();

		System.out.println("Token: " + compactJws);
	}
}
