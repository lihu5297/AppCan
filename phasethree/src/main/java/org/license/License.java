package org.license;

public class License {
	
	 private String beginDate = "-1";
    private String date = "-1";
    private String appcount = "-1";
    private String devcount = "-1";
    private String info = "";
    private String productversion="";
    private String entcount = "-1";
    
    public License(){
    	
    }
    
    public License(String beginDate,String date,String productversion,String appcount,
    				String devcount,String info,String entcount){
    	this.beginDate = beginDate;
    	this.date = date;
    	this.productversion= productversion;
    	this.appcount = appcount;
    	this.devcount = devcount;
    	this.info = info;
    	this.entcount = entcount;
    }
    
    public String getAppcount() {
		return appcount;
	}

	public void setAppcount(String appcount) {
		this.appcount = appcount;
	}

	public String getDevcount() {
		return devcount;
	}

	public void setDevcount(String devcount) {
		this.devcount = devcount;
	}

	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	public String getProductversion() {
		return productversion;
	}

	public void setProductversion(String productversion) {
		this.productversion = productversion;
	}
	

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public  String getJsonStr(){
		String jsonStr = "{";
		jsonStr += "\"DATE\":\""+ this.date +"\",";
		jsonStr += "\"BeginDate\":\""+ this.beginDate +"\",";
		jsonStr += "\"ProductVersion\":\""+ this.productversion +"\",";
		jsonStr += "\"APPCount\":\""+ this.appcount +"\",";
		jsonStr += "\"DevCount\":\""+ this.devcount +"\",";
		jsonStr += "\"Info\":\""+ this.info +"\",";
		jsonStr += "\"EntCount\":\""+ this.entcount +"\"";
		jsonStr += "}";
		return jsonStr;
	}
}
