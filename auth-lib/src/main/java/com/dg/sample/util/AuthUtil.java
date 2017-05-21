package com.dg.sample.util;

import java.nio.charset.Charset;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class AuthUtil {

	public static String passwordHash(char[] password) {
		// Create instance
		Argon2 argon2 = passwordHashingAlgo();
		try {
			// Hash password
			return argon2.hash(2, 65536, 1, password, Charset.forName("UTF-8"));
		} finally {
			// Wipe confidential data
			argon2.wipeArray(password);
		}
	}

	public static boolean verifyPassword(char[] password, String storedHash) {
		// Create instance
		Argon2 argon2 = passwordHashingAlgo();

		try {
			// Verify password
			return argon2.verify(storedHash, password);
		} finally {
			// Wipe confidential data
			argon2.wipeArray(password);
		}
	}

	private static Argon2 passwordHashingAlgo() {
		return Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id, 32, 64);
	}
}
