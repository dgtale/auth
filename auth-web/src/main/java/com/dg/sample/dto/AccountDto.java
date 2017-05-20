package com.dg.sample.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.dg.sample.entity.user.Account;

@XmlRootElement(name = "Account")
public class AccountDto implements Serializable {

	/** Default value included to remove warning. Remove or modify at will. **/
	private static final long serialVersionUID = 1L;

	private String email;
	private String password;

	public AccountDto() {
	}

	public AccountDto(final Account entity) {
		if (entity != null) {
			this.email = entity.getEmail();
		}
	}

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
