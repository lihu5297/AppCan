package org.zywx.cooldev.vo;

import java.io.Serializable;
/**
 * 
    * @ClassName: TeamGroupVO
    * @Description:某个团队分组信息(及每个分组下面人员总数) 返回给前端数据,转json使用 
    * @author jingjian.wu
    * @date 2015年8月13日 下午2:29:10
    *
 */
public class TeamGroupVO implements Serializable {

	private int total;
	
	private long groupId;
	
	private String name;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
