package com.dg.sample.entity.user;

public enum Role {
	Admin(1),
	PowerUser(2),
	Audit(3),
	User(4),
	ReadOnly(5),
	Public(6);

	int id;

	Role(int id) {
		this.id = id;
	}
}
