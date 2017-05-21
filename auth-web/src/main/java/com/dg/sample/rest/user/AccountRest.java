package com.dg.sample.rest.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dg.sample.entity.user.Account;
import com.dg.sample.entity.user.Role;
import com.dg.sample.i18.MessageCode;
import com.dg.sample.i18.TextUtil;
import com.dg.sample.rest.ResponseMessage;
import com.dg.sample.rest.ResponseUtil;
import com.dg.sample.service.AccountService;
import com.dg.sample.util.AuthUtil;
import com.dg.sample.validator.NewAccount;

@Path("/accounts")
@RequestScoped
public class AccountRest {

	@Inject
	private Logger log;

	@Inject
	private Validator validator;

	@Inject
	private ResponseUtil responseUtil;

	@Inject
	private AccountService accountService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Account> test() {
		return accountService.findAllOrderedByEmail();
	}

	/**
	 * Creates a new account from the values provided. Performs validation, and
	 * will return a JAX-RS response with either 200 ok, or with a map of
	 * fields, and related errors.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAccount(@FormParam("email") String email, @FormParam("password") String password,
			@Context HttpHeaders headers) {

		Response.ResponseBuilder builder = null;

		try {
			// Validates account using bean validation
			NewAccount newAccount = new NewAccount();
			newAccount.setEmail(email);
			newAccount.setPassword(password);

			validateAccount(newAccount, true);

			accountService.register(toEntity(newAccount));

			builder = Response.ok();
		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues, JAX-RS response containing all
			// violations
			ResponseMessage responseMessage = responseUtil.createViolationMessage(MessageCode.USR100, ce.getConstraintViolations(),
					TextUtil.getLocale(headers.getAcceptableLanguages()));
			builder = Response.status(Response.Status.BAD_REQUEST).entity(responseMessage);
		} catch (ValidationException e) {
			// Handle the unique constrain violation
			ResponseMessage responseMessage = responseUtil.createResponseMessage(e.getMessage(),
					TextUtil.getLocale(headers.getAcceptableLanguages()));
			builder = Response.status(Response.Status.CONFLICT).entity(responseMessage);
		} catch (Exception e) {
			// Handle generic exceptions
			ResponseMessage responseMessage = responseUtil.createResponseMessage(MessageCode.SYS001, e.getMessage(),
					TextUtil.getLocale(headers.getAcceptableLanguages()));
			builder = Response.status(Response.Status.BAD_REQUEST).entity(responseMessage);
		}

		return builder.build();
	}

	private Account toEntity(NewAccount newAccount) {
		Account entity = new Account();
		entity.setEmail(newAccount.getEmail());
		entity.setPassword(AuthUtil.passwordHash(newAccount.getPassword().toCharArray()));
		entity.setRole(Role.User);
		return entity;
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@FormParam("email") String email, @FormParam("password") String password,
			@Context HttpHeaders headers) {
		Response.ResponseBuilder builder = null;

		try {
			// Validates account using bean validation
			NewAccount newAccount = new NewAccount();
			newAccount.setEmail(email);
			newAccount.setPassword(password);

			validateAccount(newAccount, false);
		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues, JAX-RS response containing all
			// violations
			ResponseMessage responseMessage = responseUtil.createViolationMessage(MessageCode.BUS001, ce.getConstraintViolations(),
					TextUtil.getLocale(headers.getAcceptableLanguages()));
			return Response.status(Response.Status.BAD_REQUEST).entity(responseMessage).build();
		}

		boolean passwordVerified = false;
		Account account = accountService.findByEmail(email);

		if (account != null) {
			passwordVerified = AuthUtil.verifyPassword(password.toCharArray(), account.getPassword());
		}

		// {"error":"invalid_grant","error_description":"The user is locked
		// because of too many wrong passwords attempts. please contact the
		// administrator."}
		Map<String, String> responseObj = new HashMap<>();
		if (account != null && passwordVerified) {
			responseObj.put("access_token", "KuAmYgLk6H1D");
			responseObj.put("token_type", "bearer");
			responseObj.put("expires_in", "86399");
			responseObj.put("appName", "sosa");
			responseObj.put("username", "test@gmail.com");
			responseObj.put("role", "User");
			responseObj.put("firstName", "a");
			responseObj.put("lastName", "b");
			responseObj.put("fullName", "a b");
			responseObj.put("accountId", "1");
			builder = Response.status(Response.Status.OK).entity(responseObj);
		} else {
			ResponseMessage responseMessage = responseUtil.createResponseMessage(MessageCode.USR030,
					TextUtil.getLocale(headers.getAcceptableLanguages()));
			builder = Response.status(Response.Status.FORBIDDEN).entity(responseMessage);
		}

		return builder.build();
	}

	/**
	 * <p>
	 * Validates the given Account variable and throws validation exceptions
	 * based on the type of error. If the error is standard bean validation
	 * errors then it will throw a ConstraintValidationException with the set of
	 * the constraints violated.
	 * </p>
	 * <p>
	 * If the error is caused because an existing account with the same email is
	 * registered it throws a regular validation exception so that it can be
	 * interpreted separately.
	 * </p>
	 * 
	 * @param account
	 *            Account to be validated
	 * @throws ConstraintViolationException
	 *             If Bean Validation errors exist
	 * @throws ValidationException
	 *             If account with the same email already exists
	 */
	private void validateAccount(NewAccount account, boolean isNewAccount) throws ConstraintViolationException, ValidationException {
		// Create a bean validator and check for issues.
		Set<ConstraintViolation<NewAccount>> violations = validator.validate(account);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}

		// Check the uniqueness of the email address
		if (isNewAccount && emailAlreadyExists(account.getEmail())) {
			throw new ValidationException(MessageCode.USR050);
		}
	}

	/**
	 * Checks if a account with the same email address is already registered.
	 * This is the only way to easily capture the "@UniqueConstraint(columnNames
	 * = "email")" constraint from the account class.
	 * 
	 * @param email
	 *            The email to check
	 * @return True if the email already exists, and false otherwise
	 */
	public boolean emailAlreadyExists(String email) {
		Account account = null;
		account = accountService.findByEmail(email);

		return account != null;
	}
}
