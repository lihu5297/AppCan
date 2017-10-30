package org.zywx.cooldev.entity.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

/**
 * 项目分类实体
 * @author yang.li
 * @date 2015-08-10
 *
 */
@Entity
@Table(name = "T_PROJECT_CATEGORY")
public class ProjectCategory extends BaseEntity {
	
	private static final long serialVersionUID = 2912469374093963046L;
	
	@Column(name = "name")
	private String name;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ProjectCategory [name=" + name + "]";
	}

}
