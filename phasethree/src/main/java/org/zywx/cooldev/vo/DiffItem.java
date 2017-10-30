package org.zywx.cooldev.vo;

import java.util.ArrayList;

public class DiffItem {
	private int startNumber;
	private int lineTotal;
	private ArrayList<String> replacement;

	public int getStartNumber() {
		return startNumber;
	}
	public void setStartNumber(int startNumber) {
		this.startNumber = startNumber;
	}
	public int getLineTotal() {
		return lineTotal;
	}
	public void setLineTotal(int lineTotal) {
		this.lineTotal = lineTotal;
	}
	public ArrayList<String> getReplacement() {
		return replacement;
	}
	public void setReplacement(ArrayList<String> replacement) {
		this.replacement = replacement;
	}
	@Override
	public String toString() {
		return "DiffItem [startNumber=" + startNumber + ", lineTotal=" + lineTotal + ", replacement=" + replacement
				+ "]";
	}
	
	
}
