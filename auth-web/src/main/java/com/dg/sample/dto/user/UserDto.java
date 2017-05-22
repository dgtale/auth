package com.dg.sample.dto.user;

import com.dg.sample.entity.user.User;

public class UserDto {

	private String firstname;
	private String lastname;


	public UserDto(User user) {
		if (user != null) {
			this.firstname = user.getFirstname();
			this.lastname = user.getLastname();
		}
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}


	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}


	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}


	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}


}
