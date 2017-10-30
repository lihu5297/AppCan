package org.zywx.cooldev.entity.builder;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.zywx.cooldev.entity.BaseEntity;

/**
 * @describe 	<br>
 * @author jiexiong.liu	<br>
 * @date 2015年9月16日 上午10:18:39	<br>
 * 
 */
@Entity
@Table(name="T_PLUGIN_CATEGORY")
public class PluginCategory extends BaseEntity{

	private static final long serialVersionUID = 5695053891771980409L;
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
