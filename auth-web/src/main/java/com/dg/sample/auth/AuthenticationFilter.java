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

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
	
	@Inject
	@AuthenticatedUser
	Event<String> userAuthenticatedEvent;

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

//			setUserPrincipal(requestContext);
		} catch (Exception e) {
			e.printStackTrace();
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

//	private void setUserPrincipal(ContainerRequestContext requestContext) {
//		final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
//		requestContext.setSecurityContext(new SecurityContext() {
//
//			@Override
//			public Principal getUserPrincipal() {
//
//				return new Principal() {
//
//					@Override
//					public String getName() {
//						return username;
//					}
//				};
//			}
//
//			@Override
//			public boolean isUserInRole(String role) {
//				return true;
//			}
//
//			@Override
//			public boolean isSecure() {
//				return currentSecurityContext.isSecure();
//			}
//
//			@Override
//			public String getAuthenticationScheme() {
//				return "Bearer";
//			}
//		});
//	}

	private void validateToken(String token) throws Exception {
		// Check if it was issued by the server and if it's not expired
		// Throw an Exception if the token is invalid

		try {

			Claims body = Jwts.parser().setSigningKey(getKey()).parseClaimsJws(token).getBody();
			String subject = body.getSubject();
			System.out.println(">>> subject "+subject);
			//OK, we can trust this JWT
			userAuthenticatedEvent.fire(subject);

		} catch (SignatureException e) {

			//don't trust the JWT!
		}
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
