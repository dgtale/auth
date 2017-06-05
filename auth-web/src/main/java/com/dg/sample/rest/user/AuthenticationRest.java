package com.dg.sample.rest.user;

import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dg.sample.dto.user.CredentialsDto;
import com.dg.sample.entity.user.Account;
import com.dg.sample.i18.MessageCode;
import com.dg.sample.i18.TextUtil;
import com.dg.sample.rest.ResponseMessage;
import com.dg.sample.rest.ResponseUtil;
import com.dg.sample.service.AccountService;
import com.dg.sample.util.AuthUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Path("/authentication")
public class AuthenticationRest {
	@Inject
	private Logger log;

	@Inject
	private Validator validator;

	@Inject
	private ResponseUtil responseUtil;

	@Inject
	private AccountService accountService;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticateUser(@FormParam("username") String username, @FormParam("password") String password, @Context HttpHeaders headers) {

		try {
			// Authenticate the user using the credentials provided
			Account account = authenticate(username, password);

			// Issue a token for the user
			String token = issueToken(account);

			// Return the token on the response
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("access_token", token);
			responseObj.put("token_type", "bearer");
//			responseObj.put("expires_in", "86399");
			responseObj.put("appName", "sample");
			responseObj.put("username", account.getEmail());
			responseObj.put("role", account.getRole().name());
			responseObj.put("firstName", account.getUser().getFirstname());
			responseObj.put("lastName", account.getUser().getLastname());
			responseObj.put("fullName", "a b");
			responseObj.put("accountId", account.getId().toString());

			Response.ResponseBuilder builder = Response.status(Response.Status.OK).entity(responseObj);

			return builder.build();
		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues, JAX-RS response containing all violations
			ResponseMessage responseMessage = responseUtil.createViolationMessage(MessageCode.BUS001, ce.getConstraintViolations(),
					TextUtil.getLocale(headers.getAcceptableLanguages()));
			return Response.status(Response.Status.BAD_REQUEST).entity(responseMessage).build();
		} catch (ValidationException e) {
			ResponseMessage responseMessage = responseUtil.createResponseMessage(e.getMessage(),
					TextUtil.getLocale(headers.getAcceptableLanguages()));
			return Response.status(Response.Status.UNAUTHORIZED).entity(responseMessage).build();
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception while authenticating a user", e);
			ResponseMessage responseMessage = responseUtil.createResponseMessage(MessageCode.SYS001, e.getMessage(),
					TextUtil.getLocale(headers.getAcceptableLanguages()));
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseMessage).build();
		}
	}

	private Account authenticate(String username, String password) throws ConstraintViolationException, ValidationException {
		// Validates account using bean validation
		CredentialsDto credentials = new CredentialsDto();
		credentials.setUsername(username);
		credentials.setPassword(password);

		// Create a bean validator and check for issues.
		Set<ConstraintViolation<CredentialsDto>> violations = validator.validate(credentials);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}

		// Check if the account exists
		Account entity = accountService.findByEmail(credentials.getUsername());

		boolean passwordVerified = false;
		if (entity != null) {
			// {"error":"invalid_grant","error_description":"The user is locked
			// because of too many wrong passwords attempts. please contact the
			// administrator."}
			// TODO
			passwordVerified = AuthUtil.verifyPassword(credentials.getPassword().toCharArray(), entity.getPassword());
		}

		// Check the uniqueness of the email address
		if (entity == null || !passwordVerified) {
			throw new ValidationException(MessageCode.USR030);
		}

		return entity;
	}

	private String issueToken(Account account) {
		//		Key key = MacProvider.generateKey();
		Key key = getKey();
		Calendar cal = GregorianCalendar.getInstance();
		long now = cal.getTimeInMillis();
		cal.add(Calendar.HOUR_OF_DAY, 24);

		String compactJws = Jwts.builder()
				.setIssuer("sample.com")
				.setIssuedAt(new Date(now))
				.setExpiration(cal.getTime())
				.setSubject(account.getEmail())
				.signWith(SignatureAlgorithm.HS512, key)
				.compact();

		// Issue a token (can be a random String persisted to a database or a JWT token)
		// The issued token must be associated to a user
		// Return the issued token
		//		Random random = new SecureRandom();
		//		String token = new BigInteger(130, random).toString(32);

		return compactJws;
	}

	private Key getKey() {
		String encodedKey = "ooJXPj7xFYjo0pLDY1Rthg==";

		// decode the base64 encoded string
		byte[] decodedKey = Base64.getDecoder().decode(encodedKey);

		// rebuild key using SecretKeySpec
		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

		return originalKey;
	}
}