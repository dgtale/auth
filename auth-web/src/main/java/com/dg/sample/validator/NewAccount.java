package com.dg.sample.validator;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;

import com.dg.sample.i18.MessageCode;

public class NewAccount {

	@NotNull
	@Email(message = MessageCode.USR001)
	private String email;

	/**
	 * Password hash
	 * 
	 * <pre>
	 * ^                 # start-of-string
	 * (?=.*[0-9])       # a digit must occur at least once
	 * (?=.*[a-z])       # a lower case letter must occur at least once
	 * (?=.*[A-Z])       # an upper case letter must occur at least once
	 * (?=.*[@#$%^&+=])  # a special character must occur at least once
	 * (?=\S+$)          # no whitespace allowed in the entire string
	 * .{8,}             # anything, at least eight places though
	 * $                 # end-of-string
	 * </pre>
	 */
	@NotNull
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = MessageCode.USR003)
	private String password;

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

}
