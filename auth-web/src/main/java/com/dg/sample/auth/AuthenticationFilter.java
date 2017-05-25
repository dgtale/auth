package com.dg.sample.auth;

import java.io.IOException;
import java.security.Key;
import java.util.Base64;

import javax.annotation.Priority;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.dg.sample.annotation.AuthenticatedUser;
import com.dg.sample.annotation.Secured;
import com.dg.sample.entity.user.Account;
import com.dg.sample.service.AccountService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Inject
	private AccountService accountService;

	@Inject
	@AuthenticatedUser
	Event<Account> userAuthenticatedEvent;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		// Get the HTTP Authorization header from the request
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

		// Check if the HTTP Authorization header is present and formatted
		// correctly
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new NotAuthorizedException("Authorization header must be provided");
		}

		// Extract the token from the HTTP Authorization header
		String token = authorizationHeader.substring("Bearer".length()).trim();

		try {
			// Validate the token
			validateToken(token);
		} catch (SignatureException|SecurityException e) {
			// Don't trust the JWT!
			e.printStackTrace();
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		} catch (Exception e) {
			requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
		}
	}

	private void validateToken(String token) throws SignatureException {
		// Check if it was issued by the server and if it's not expired
		// Throw an Exception if the token is invalid
		Claims body = Jwts.parser().setSigningKey(getKey()).parseClaimsJws(token).getBody();
		String username = body.getSubject();
		System.out.println(">>> subject " + username);

		// OK, we can trust this JWT

		// Check if the user is known to our system and if it's still active
		Account authenticatedUser = accountService.findByEmail(username);
		if (authenticatedUser == null || !authenticatedUser.isApproved() || !authenticatedUser.isReady()) {
			throw new SecurityException();
		}
		
		// OK, the user is authenticated
		userAuthenticatedEvent.fire(authenticatedUser);
	}

	public Key getKey() {
		String encodedKey = "ooJXPj7xFYjo0pLDY1Rthg==";

		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(encodedKey);

		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

		return originalKey;
	}
}
