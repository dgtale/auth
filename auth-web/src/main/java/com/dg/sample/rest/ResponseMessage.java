package com.dg.sample.rest;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ResponseMessage {

	private String error;

	private String errorDescription;

	private String additionalInformation;

	private List<ResponsePropertyDetail> properties;


	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @return the errorDescription
	 */
	public String getErrorDescription() {
		return errorDescription;
	}

	/**
	 * @param errorDescription the errorDescription to set
	 */
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}

	/**
	 * @return the additionalInformation
	 */
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	/**
	 * @param additionalInformation the additionalInformation to set
	 */
	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	/**
	 * @return the properties
	 */
	public List<ResponsePropertyDetail> getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void addProperty(ResponsePropertyDetail property) {
		if (this.properties == null) {
			this.properties = new ArrayList<>();
		}
		this.properties.add(property);
	}

}
