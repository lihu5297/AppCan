package org.zywx.cooldev.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class TimestampFormat {
	
	private static Logger log = Logger.getLogger(TimestampFormat.class.getName());
	/**
	 * 
	 * 功能描述：字符串日期转timestamp	<br>
	 * 创建人:刘杰雄	<br>
	 * 时间:2015年8月11日 下午2:04:23	<br>
	 * @param source
	 * @return
	 *
	 */
	public static Timestamp getTimestamp(String source){
		if(null==source||source.equals("")){
			return new Timestamp((new Date()).getTime());
		}
		Timestamp time = Timestamp.valueOf(source);
		return time;
		
	}
	
	/**
	 * 比较两个日期相差多少天,第一个参数是小的日期,第二个参数时大日期
	 * @user jingjian.wu
	 * @date 2015年10月17日 下午9:23:25
	 */
	public static int daysBetween(Date smdate,Date bdate) throws ParseException  
    {  
     SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
     smdate=sdf.parse(sdf.format(smdate));
     bdate=sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();  
        cal.setTime(smdate);  
        long time1 = cal.getTimeInMillis ();               
        cal.setTime(bdate);  
        long time2 = cal.getTimeInMillis ();       
        long between_days=(time2-time1)/ (1000*3600*24);

       return Integer.parseInt(String.valueOf (between_days));         
    }  
	
	public static void main(String[] args) throws ParseException{
		Timestamp time = getTimestamp("");
		log.info(time);
		
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = sf.parse("2010-01-11");
		Date d2 = sf.parse("2010-01-22");
		log.info(daysBetween(d1, d2));
	}

}
