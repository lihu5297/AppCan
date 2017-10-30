package org.zywx.cooldev.vo;

/**
 * 打包完成之后,获取版本信息
 * 给插件信息排序
 * @author Administrator
 *
 */
public class PackagePluginInfo implements Comparable<PackagePluginInfo>{

	private long pluginVersionId;
	private String pluginVersionNo;
	private String pluginCnName;
	private String pluginEnName;
	private String pluginType;
	public long getPluginVersionId() {
		return pluginVersionId;
	}
	public void setPluginVersionId(long pluginVersionId) {
		this.pluginVersionId = pluginVersionId;
	}
	public String getPluginVersionNo() {
		return pluginVersionNo;
	}
	public void setPluginVersionNo(String pluginVersionNo) {
		this.pluginVersionNo = pluginVersionNo;
	}
	public String getPluginCnName() {
		return pluginCnName;
	}
	public void setPluginCnName(String pluginCnName) {
		this.pluginCnName = pluginCnName;
	}
	public String getPluginEnName() {
		return pluginEnName;
	}
	public void setPluginEnName(String pluginEnName) {
		this.pluginEnName = pluginEnName;
	}
	public String getPluginType() {
		return pluginType;
	}
	public void setPluginType(String pluginType) {
		this.pluginType = pluginType;
	}
	@Override
	public int compareTo(PackagePluginInfo o) {
		return this.getPluginEnName().compareTo(o.getPluginEnName());
	}
	
	
}
