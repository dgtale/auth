package com.dg.sample.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import com.dg.sample.dao.AccountDao;
import com.dg.sample.data.AccountCache;
import com.dg.sample.entity.user.Account;

@Stateless
public class AccountService {

	@Inject
	private AccountCache accountCache;

	@EJB
	private AccountDao accountDao;

//	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Account findById(Long id) {
		return accountDao.findById(id);
	}

//	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Account findByEmail(String email) {

		System.out.println(">>>>> findByEmail");

		return accountCache.findByEmail(email);
	}

//	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Account> findAllOrderedByEmail() {
		return accountDao.findAllOrderedByEmail();
	}

	public Account register(String email, String password) throws Exception {
		Account account = accountDao.create(email, password);

		accountCache.put(account);

		return account;
	}

	public void register(Account account) throws Exception {
		accountDao.create(account);

		accountCache.put(account);
	}

}
