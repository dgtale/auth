/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dg.sample.entity.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.dg.sample.constant.MessageCode;
import com.dg.sample.entity.AbstractEntity;

@Entity
@Table(name = Account.TABLE, uniqueConstraints = @UniqueConstraint(columnNames = Account.ALIAS + "email"))
@SuppressWarnings("serial")
public class Account extends AbstractEntity {

	static final String ALIAS = "acct_";

	public static final String TABLE = "ACCOUNTS";

	@Id
	@GeneratedValue
	@Column(name = ALIAS + "id")
	private Long id;

	@Column(name = ALIAS + "uuid")
	private String uuid;

	@NotNull
	@NotEmpty
	@Email(message = MessageCode.USR001)
	@Column(name = ALIAS + "email")
	private String email;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = ALIAS + "role")
	private Role role;

	@Column(name = ALIAS + "approved")
	private Boolean approved;

	@Column(name = ALIAS + "ready")
	private Boolean ready;

	@Column(name = ALIAS + "created")
	private Date created;

	@Column(name = ALIAS + "updated")
	private Date updated;

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
	@Size(min = 8, message = MessageCode.USR002)
	@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = MessageCode.USR003)
	@Column(name = ALIAS + "password")
	private String password;

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
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the created
	 */
	public Date getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}

	/**
	 * @return the updated
	 */
	public Date getUpdated() {
		return updated;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(Date updated) {
		this.updated = updated;
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
	 * @return the approved
	 */
	public Boolean isApproved() {
		return approved;
	}

	/**
	 * @param approved the approved to set
	 */
	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	/**
	 * @return the ready
	 */
	public Boolean isReady() {
		return ready;
	}

	/**
	 * @param ready the ready to set
	 */
	public void setReady(Boolean ready) {
		this.ready = ready;
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
