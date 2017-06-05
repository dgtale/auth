package com.dg.sample.rest.user;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
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

import com.dg.sample.annotation.Secured;
import com.dg.sample.dto.user.CredentialsDto;
import com.dg.sample.entity.user.Account;
import com.dg.sample.entity.user.Role;
import com.dg.sample.i18.MessageCode;
import com.dg.sample.i18.TextUtil;
import com.dg.sample.rest.ResponseMessage;
import com.dg.sample.rest.ResponseUtil;
import com.dg.sample.service.AccountService;
import com.dg.sample.util.AuthUtil;

@Path("/accounts")
@RequestScoped
public class AccountRegistrationRest {
	@Inject
	private Logger log;

	@Inject
	private Validator validator;

	@Inject
	private ResponseUtil responseUtil;

	@Inject
	private AccountService accountService;

	@GET
	@Secured({Role.Admin})
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllAccounts(@Context HttpHeaders headers) {
		ResponseMessage responseMessage = null;
		Response.ResponseBuilder builder = null;
		try {
			List<Account> responseObj = accountService.findAllOrderedByEmail();
			builder = Response.status(Response.Status.OK).entity(responseObj);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception while getting all users", e);

			// Handle generic exceptions
			responseMessage = responseUtil.createResponseMessage(MessageCode.SYS001, e.getMessage(),
					TextUtil.getLocale(headers.getAcceptableLanguages()));
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseMessage);
		}
		return builder.build();
	}

	/**
	 * Creates a new account from the values provided. Performs validation, and
	 * will return a JAX-RS response with either 200 ok, or with a map of
	 * fields, and related errors.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAccount(@FormParam("username") String username, @FormParam("password") String password,
			@Context HttpHeaders headers) {

		ResponseMessage responseMessage = null;
		Response.ResponseBuilder builder = null;

		try {
			// Validates account using bean validation
			CredentialsDto credentials = new CredentialsDto();
			credentials.setUsername(username);
			credentials.setPassword(password);

			validateAccount(credentials);

			accountService.register(toEntity(credentials));

			responseMessage = responseUtil.createResponseMessage(MessageCode.USR111, TextUtil.getLocale(headers.getAcceptableLanguages()));
			builder = Response.status(Response.Status.OK).entity(responseMessage);
		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues, JAX-RS response containing all
			// violations
			responseMessage = responseUtil.createViolationMessage(MessageCode.USR100, ce.getConstraintViolations(),
					TextUtil.getLocale(headers.getAcceptableLanguages()));
			builder = Response.status(Response.Status.BAD_REQUEST).entity(responseMessage);
		} catch (ValidationException e) {
			// Handle the unique constrain violation
			responseMessage = responseUtil.createResponseMessage(e.getMessage(),
					TextUtil.getLocale(headers.getAcceptableLanguages()));
			builder = Response.status(Response.Status.CONFLICT).entity(responseMessage);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception while creating a new account", e);

			// Handle generic exceptions
			responseMessage = responseUtil.createResponseMessage(MessageCode.SYS001, e.getMessage(),
					TextUtil.getLocale(headers.getAcceptableLanguages()));
			builder = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseMessage);
		}

		return builder.build();
	}

	private Account toEntity(CredentialsDto credentials) {
		Account account = new Account();

		account.setEmail(credentials.getUsername());
		account.setPassword(AuthUtil.passwordHash(credentials.getPassword().toCharArray()));
		account.setRole(Role.User);
		// TODO remove this when the email verification is in place
		account.setApproved(Boolean.TRUE);
		account.setReady(Boolean.TRUE);

		return account;
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
	private void validateAccount(CredentialsDto credentials) throws ConstraintViolationException, ValidationException {
		// Create a bean validator and check for issues.
		Set<ConstraintViolation<CredentialsDto>> violations = validator.validate(credentials);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}

		// Check the uniqueness of the email address
		if (emailAlreadyExists(credentials.getUsername())) {
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
