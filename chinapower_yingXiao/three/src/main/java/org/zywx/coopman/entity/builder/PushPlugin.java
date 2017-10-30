package org.zywx.coopman.entity.builder;

import java.io.File;

public class PushPlugin {

	private Plugin plugin;
	
	private PluginVersion pv;
	
	private File file;

	public Plugin getPlugin() {
		return plugin;
	}

	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	public PluginVersion getPv() {
		return pv;
	}

	public void setPv(PluginVersion pv) {
		this.pv = pv;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
}
