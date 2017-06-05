package com.dg.sample.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.dg.sample.entity.user.Account;
import com.dg.sample.entity.user.Role;
import com.dg.sample.entity.user.User;

@Stateless
public class AccountDao {

	@Inject
	private EntityManager em;

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Account findById(Long id) {
		return em.find(Account.class, id);
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public Account findByEmail(String email) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = cb.createQuery(Account.class);
		Root<Account> account = criteria.from(Account.class);
		// Swap criteria statements if you would like to try out type-safe criteria queries, a new
		// feature in JPA 2.0
		// criteria.select(member).where(cb.equal(member.get(Member_.name), email));
		criteria.select(account).where(cb.equal(account.get("email"), email));
		try {
			return em.createQuery(criteria).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public List<Account> findAllOrderedByEmail() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Account> criteria = cb.createQuery(Account.class);
		Root<Account> account = criteria.from(Account.class);
		// Swap criteria statements if you would like to try out type-safe criteria queries, a new
		// feature in JPA 2.0
		// criteria.select(account).orderBy(cb.asc(account.get(Account_.name)));
		criteria.select(account).orderBy(cb.asc(account.get("email")));
		return em.createQuery(criteria).getResultList();
	}

	public void create(Account account) throws Exception {
		em.persist(account);

		User user = new User();
		user.setId(account.getId());

		em.persist(user);
		
		account.setUser(user);
	}

	public Account create(String email, String password) throws Exception {
		Account account = new Account();
		account.setEmail(email);
		account.setPassword(password);
		account.setRole(Role.User);
		
		create(account);

		return account;
	}
}
