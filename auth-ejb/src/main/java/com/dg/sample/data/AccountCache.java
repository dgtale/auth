package com.dg.sample.data;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.cache2k.integration.CacheLoader;

import com.dg.sample.dao.AccountDao;
import com.dg.sample.entity.user.Account;

@ApplicationScoped
public class AccountCache {

	@Inject
	private AccountDao accountDao;

	private Cache<String, Account> accountCache;

	@PostConstruct
	public void init() {
		accountCache = new Cache2kBuilder<String, Account>() { }
		.name("accounts")
		.loader(new CacheLoader<String, Account>() {
			@Override
			public Account load(final String key) throws Exception {
				System.out.println(">>>>> findByEmail via cache loader");
				return accountDao.findByEmail(key);
			}
		})
		.permitNullValues(true)
		.disableStatistics(true)
		.build();
	}

	public Account findByEmail(final String username) {
		return accountCache.get(username);
	}

	public void put(Account account) {
		accountCache.put(account.getEmail(), account);
	}
}
