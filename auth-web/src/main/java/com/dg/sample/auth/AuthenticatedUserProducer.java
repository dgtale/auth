package com.dg.sample.auth;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

import com.dg.sample.annotation.AuthenticatedUser;
import com.dg.sample.entity.user.Account;

@RequestScoped
public class AuthenticatedUserProducer {

	@Produces
	@RequestScoped
	@AuthenticatedUser
	private Account authenticatedUser;

	public void handleAuthenticationEvent(@Observes @AuthenticatedUser Account account) {
		this.authenticatedUser = account;
	}
}