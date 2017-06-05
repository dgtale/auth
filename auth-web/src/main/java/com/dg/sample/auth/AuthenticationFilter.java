package com.dg.sample.auth;

import java.io.IOException;
import java.security.Key;
import java.util.Base64;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.dg.sample.annotation.AuthenticatedUser;
import com.dg.sample.annotation.Secured;
import com.dg.sample.entity.user.Account;
import com.dg.sample.i18.MessageCode;
import com.dg.sample.rest.ResponseMessage;
import com.dg.sample.rest.ResponseUtil;
import com.dg.sample.service.AccountService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
	@Inject
	private Logger log;

	@Inject
	private ResponseUtil responseUtil;

	@Inject
	private AccountService accountService;

	@Inject
	@AuthenticatedUser
	Event<Account> userAuthenticatedEvent;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		try {
			// Get the HTTP Authorization header from the request
			String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
			validateHeader(authorizationHeader);

			// Extract the token from the HTTP Authorization header
			String token = authorizationHeader.substring("Bearer".length()).trim();

			// Validate the token
			validateToken(token);
		} catch (SignatureException | SecurityException e) {
			// Don't trust the JWT!
			ResponseMessage responseMessage =
					responseUtil.createResponseMessage(MessageCode.SEC001, e.getMessage(), Locale.ENGLISH);
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(responseMessage).build());
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception while authentication check", e);
			ResponseMessage responseMessage =
					responseUtil.createResponseMessage(MessageCode.SYS001, e.getMessage(), Locale.ENGLISH);
			requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseMessage).build());
		}
	}

	private void validateHeader(String authorizationHeader) {
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new SecurityException("Authorization header must be provided");
		}
	}

	private void validateToken(String token) throws SignatureException {
		// Check if it was issued by the server and if it's not expired
		// Throw an Exception if the token is invalid
		Claims body = Jwts.parser().setSigningKey(getKey()).parseClaimsJws(token).getBody();
		if (body.getExpiration() == null || body.getExpiration().getTime() < System.currentTimeMillis()) {
			throw new SecurityException("Token expired");
		}

		String username = body.getSubject();
		System.out.println(">>> subject " + username);

		// OK, we can trust this JWT

		// Check if the user is known to our system and if it's still active
		Account authenticatedUser = accountService.findByEmail(username);
		if (authenticatedUser == null || !authenticatedUser.isApproved() || !authenticatedUser.isReady()) {
			throw new SecurityException("Unknown user or user not allowed to use the application");
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
