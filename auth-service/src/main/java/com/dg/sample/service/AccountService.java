package com.dg.sample.service;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.dg.sample.dao.AccountDao;
import com.dg.sample.entity.user.Account;

@Stateless
public class AccountService {

	@EJB
	private AccountDao accountDao;

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Account findById(Long id) {
		return accountDao.findById(id);
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Account findByEmail(String email) {
		return accountDao.findByEmail(email);
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Account> findAllOrderedByEmail() {
		return accountDao.findAllOrderedByEmail();
	}

	public Account register(String email, String password) throws Exception {
		return accountDao.create(email, password);
	}

	public void register(Account account) throws Exception {
		accountDao.create(account);
	}

}
