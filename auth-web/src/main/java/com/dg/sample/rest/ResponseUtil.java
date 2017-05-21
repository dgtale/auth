package com.dg.sample.rest;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;

import com.dg.sample.i18.MessageCode;

@RequestScoped
public class ResponseUtil {

	@Inject
	private Logger log;

	/**
	 * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
	 * by clients to show violations.
	 * 
	 * @param violations A set of violations that needs to be reported
	 * @param locale
	 * @return the response message
	 */
	public ResponseMessage createViolationMessage(String messageCode, Set<ConstraintViolation<?>> violations, Locale locale) {
		log.fine("Validation completed. violations found: " + violations.size());

		ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);

		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setError(messageCode);
		responseMessage.setErrorDescription(resourceBundle.getString(messageCode));

		for (ConstraintViolation<?> violation : violations) {
			ResponsePropertyDetail messageDetail = new ResponsePropertyDetail();
			String message = resourceBundle.getString(violation.getMessage());

			messageDetail.setError(violation.getMessage());
			messageDetail.setErrorDescription(message);
			messageDetail.setProperty(violation.getPropertyPath().toString());

			responseMessage.addProperty(messageDetail);
		}

		return responseMessage;
	}

	/**
	 * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
	 * by clients to show violations.
	 * 
	 * @param message code
	 * @param locale
	 * @return JAX-RS response containing all violations
	 */
	public ResponseMessage createResponseMessage(String messageCode, Locale locale) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);

		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setError(messageCode);
		responseMessage.setErrorDescription(resourceBundle.getString(messageCode));

		return responseMessage;
	}

	/**
	 * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
	 * by clients to show violations.
	 * 
	 * @param message code
	 * @param additional information
	 * @param locale
	 * @return JAX-RS response containing all violations
	 */
	public ResponseMessage createResponseMessage(String messageCode, String additionalInformation, Locale locale) {
		ResponseMessage responseMessage = createResponseMessage(messageCode, locale);
		responseMessage.setAdditionalInformation(additionalInformation);

		return responseMessage;
	}

	//	@PostConstruct
	//	public void initResourceBundle() {
	//		resourceBundle = ResourceBundle.getBundle("messages", locale);
	//	}
}
