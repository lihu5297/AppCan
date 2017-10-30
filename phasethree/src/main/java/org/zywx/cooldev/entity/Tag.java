package org.zywx.cooldev.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="T_TAG")
public class Tag extends BaseEntity{

	private static final long serialVersionUID = 4759704266782910094L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
