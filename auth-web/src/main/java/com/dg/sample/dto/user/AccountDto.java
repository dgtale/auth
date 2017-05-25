package com.dg.sample.dto.user;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.dg.sample.entity.user.Account;
import com.dg.sample.entity.user.Role;

@XmlRootElement(name = "Account")
@SuppressWarnings("serial")
public class AccountDto implements Serializable {

	private Long id;
	private String email;
	private Role role;
	private UserDto user;

	public AccountDto() {
	}

	public AccountDto(final Account entity) {
		if (entity != null) {
			this.id = entity.getId();
			this.email = entity.getEmail();
			this.role = entity.getRole();
			this.user = new UserDto(entity.getUser());
		}
	}


	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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
	 * @return the role
	 */
	public Role getRole() {
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(Role role) {
		this.role = role;
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
