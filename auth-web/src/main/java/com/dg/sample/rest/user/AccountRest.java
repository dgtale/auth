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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.dg.sample.entity.user.Account;
import com.dg.sample.entity.user.Role;
import com.dg.sample.service.AccountService;

@Path("/accounts")
@RequestScoped
public class AccountRest {

	@Inject
	private Logger log;

	@Inject
	private Validator validator;

	@Inject
	private AccountService accountService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Account> test() {
		return accountService.findAllOrderedByEmail();
	}


	/**
	 * Creates a new account from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
	 * or with a map of fields, and related errors.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAccount(@FormParam("email") String email, @FormParam("password") String password) {

		Response.ResponseBuilder builder = null;

		try {
			// Validates account using bean validation
			Account entity = new Account();
			entity.setEmail(email);
			entity.setPassword(password);
			entity.setRole(Role.User);

			validateAccount(entity);

			accountService.register(entity);

			// Create an "ok" response
			builder = Response.ok();
		} catch (ConstraintViolationException ce) {
			// Handle bean validation issues
			builder = createViolationResponse(ce.getConstraintViolations());
		} catch (ValidationException e) {
			// Handle the unique constrain violation
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("error", e.getMessage());
			//			responseObj.put("error_description", "Email taken");
			builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
		} catch (Exception e) {
			// Handle generic exceptions
			Map<String, String> responseObj = new HashMap<>();
			responseObj.put("error", e.getMessage());
			builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
		}

		return builder.build();
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login() {
		Response.ResponseBuilder builder = null;
		//		builder = Response.ok();

		//		{"error":"invalid_grant","error_description":"The user is locked because of too many wrong passwords attempts. please contact the administrator."}
		Map<String, String> responseObj = new HashMap<>();
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

		return builder.build();
	}

	/**
	 * <p>
	 * Validates the given Account variable and throws validation exceptions based on the type of error. If the error is standard
	 * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.
	 * </p>
	 * <p>
	 * If the error is caused because an existing account with the same email is registered it throws a regular validation
	 * exception so that it can be interpreted separately.
	 * </p>
	 * 
	 * @param account Account to be validated
	 * @throws ConstraintViolationException If Bean Validation errors exist
	 * @throws ValidationException If account with the same email already exists
	 */
	private void validateAccount(Account account) throws ConstraintViolationException, ValidationException {
		// Create a bean validator and check for issues.
		Set<ConstraintViolation<Account>> violations = validator.validate(account);

		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>(violations));
		}

		// Check the uniqueness of the email address
		if (emailAlreadyExists(account.getEmail())) {
			throw new ValidationException("Unique Email Violation");
		}
	}

	/**
	 * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
	 * by clients to show violations.
	 * 
	 * @param violations A set of violations that needs to be reported
	 * @return JAX-RS response containing all violations
	 */
	private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
		log.fine("Validation completed. violations found: " + violations.size());

		Map<String, String> responseObj = new HashMap<>();

		for (ConstraintViolation<?> violation : violations) {
			responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
		}

		return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
	}

	/**
	 * Checks if a account with the same email address is already registered. This is the only way to easily capture the
	 * "@UniqueConstraint(columnNames = "email")" constraint from the account class.
	 * 
	 * @param email The email to check
	 * @return True if the email already exists, and false otherwise
	 */
	public boolean emailAlreadyExists(String email) {
		Account account = null;
//		try {
			account = accountService.findByEmail(email);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		} catch (NoResultException e) {
//			// ok
//		} catch (EJBException e) {
//			System.out.println(">>>>>> catch");
//			Exception ne = (Exception) e.getCause();
//			System.out.println(">>>>>> "+ne.getMessage());
//			if (ne.getClass().getName().equals("NoResultException")) {
//				// ok
//			} else {
//				throw new ValidationException(ne);
//			}
//		}
		return account != null;
	}
}
