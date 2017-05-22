package com.dg.sample.auth;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.dg.sample.annotation.AuthenticatedUser;
import com.dg.sample.dto.user.AccountDto;
import com.dg.sample.entity.user.Account;
import com.dg.sample.service.AccountService;

@RequestScoped
public class AuthenticatedUserProducer {

	@Inject
	private AccountService accountService;

	@Produces
	@RequestScoped
	@AuthenticatedUser
	private AccountDto authenticatedUser;

	public void handleAuthenticationEvent(@Observes @AuthenticatedUser String username) {
		this.authenticatedUser = findUser(username);
	}

	private AccountDto findUser(String username) {
		// Hit the the database or a service to find a user by its username and return it
		// Return the User instance
		Account account = accountService.findByEmail(username);
		return new AccountDto(account);
	}
}