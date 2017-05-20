package com.dg.sample.service;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import com.dg.sample.dao.AccountDao;
import com.dg.sample.entity.user.Account;

@Stateless
public class AccountService {

	@Inject
	private Logger log;

	@EJB
	private AccountDao accountDao;

	public Account register(String email, String password) throws Exception {
		return accountDao.create(email, password);
	}

	public void register(Account account) throws Exception {
		accountDao.create(account);
	}

	public Account findByEmail(String email) {
		return accountDao.findByEmail(email);
	}

	public List<Account> findAllOrderedByEmail() {
		return accountDao.findAllOrderedByEmail();
	}

}
