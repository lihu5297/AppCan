package org.zywx.cooldev.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProcessClearStream extends Thread{

	private Log log = LogFactory.getLog(this.getClass().getName());	 
	private InputStream inputStream;

	private String type;
	
	private StringBuffer result;

    public	ProcessClearStream(InputStream inputStream, String type) {
		this.inputStream = inputStream;
		this.type = type;
	}
    
    public	ProcessClearStream(InputStream inputStream, String type,StringBuffer result) {
		this.inputStream = inputStream;
		this.type = type;
		this.result = result;
	}

	public void run() {
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader br = new BufferedReader(inputStreamReader);
			// 打印信息
			String line = null;
			while ((line = br.readLine()) != null) {
				log.info(type +">"+ line);
				if(null!=result){
					result.append(line+"\n\r");
				}
			}
			inputStreamReader.close();
			br.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
