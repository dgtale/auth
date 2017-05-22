package com.dg.sample.dto.user;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.dg.sample.entity.user.Account;

@XmlRootElement(name = "Account")
@SuppressWarnings("serial")
public class AccountDto implements Serializable {

	private String email;
	private UserDto user;

	public AccountDto() {
	}

	public AccountDto(final Account entity) {
		if (entity != null) {
			this.email = entity.getEmail();
			this.user = new UserDto(entity.getUser());
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
	 * @return the user
	 */
	public UserDto getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(UserDto user) {
		this.user = user;
	}

}
