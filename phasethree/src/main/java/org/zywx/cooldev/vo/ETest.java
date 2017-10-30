package org.zywx.cooldev.vo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ETest {
	
	private static Logger log = Logger.getLogger(ETest.class.getName());

	public static void main(String[] args) throws IOException {
		BufferedReader in=  new BufferedReader( new FileReader("d:\\a.txt"));
		String diffResult = "";
		String line = null;
		while((line = in.readLine()) != null) {
			diffResult += (line + "\n\r");
		}
		
		List<DiffItem> itemList = analysisDiff(diffResult);
		
		log.info(itemList);
		
		in.close();
	}
	
	
	
	public static List<DiffItem> analysisDiff(String deffResult) {
		
		List<DiffItem> itemList = new ArrayList<>();
		
		String[] diffLines = deffResult.split("\n\r");	// git diff 结果
		
		String diffAbstract = null;

		ArrayList<String> recLines = new ArrayList<>();	// 一个diff点
		
		// 解析diff结果
		for(int i = 0; i < diffLines.length; i++) {
			if(i <= 3) {
				continue; // 略过前四行
			}
			String line = diffLines[i];
			if(line.startsWith("@@")) {
				if(diffAbstract != null && recLines.size() > 0) {
					// 添加变化
					int rightIdx = diffAbstract.indexOf("+") + 1;
					int nextBlankIdx = diffAbstract.indexOf(" ", rightIdx);
					String info = diffAbstract.substring(rightIdx, nextBlankIdx);
					String[] numStrs = info.split(",");
					int startNumber = Integer.parseInt(numStrs[0]);
					int lineTotal  = Integer.parseInt(numStrs[1]);
					
				
					DiffItem item = new DiffItem();
					item.setLineTotal(lineTotal);
					item.setStartNumber(startNumber);
					item.setReplacement(recLines);
					
					itemList.add(item);
				}
				
				diffAbstract = line;
				recLines.clear();
				
			} else {
				recLines.add(line);
			
			}
			
		}
		
		if(diffAbstract != null && recLines.size() > 0) {
			// 添加变化
			int rightIdx = diffAbstract.indexOf("+") + 1;
			int nextBlankIdx = diffAbstract.indexOf(" ", rightIdx);
			String info = diffAbstract.substring(rightIdx, nextBlankIdx);
			String[] numStrs = info.split(",");
			int startNumber = Integer.parseInt(numStrs[0]);
			int lineTotal  = Integer.parseInt(numStrs[1]);
			
		
			DiffItem item = new DiffItem();
			item.setLineTotal(lineTotal);
			item.setStartNumber(startNumber);
			item.setReplacement(recLines);
			
			itemList.add(item);
		}
		
		return itemList;
	}

}



