package com.dg.sample.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@MappedSuperclass
@SuppressWarnings("serial")
public abstract class AbstractEntity implements Serializable {

	public abstract Date getCreated();

	public abstract void setCreated(Date created);

	public abstract Date getUpdated();

	public abstract void setUpdated(Date updated);

	public abstract String getUuid();

	public abstract void setUuid(String uuid);

	@PrePersist
	void preInsert() {
		Date date = new Date();
		if (getCreated() == null) {
			setCreated(date);
		}
		if (getUpdated() == null) {
			setUpdated(date);
		}
		if (getUuid() == null) {
			setUuid(UUID.randomUUID().toString());
		}
	}

	@PreUpdate
	void preUpdate() {
		setUpdated(new Date());
	}

}
