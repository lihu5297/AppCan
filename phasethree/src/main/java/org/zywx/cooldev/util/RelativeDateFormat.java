package org.zywx.cooldev.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

/**
    * @Description:获取某个日期,是在当前日期的多少时间段之前 
    * 如果传入的时间比当前时间大,则返回传入时间
    * @author jingjian.wu
    * @date 2015年8月18日 上午10:15:16
    *
 */
public class RelativeDateFormat {
	
	private static Logger log = Logger.getLogger(RelativeDateFormat.class.getName());

	private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;//24小时
//    private static final long ONE_WEEK = 604800000L;

    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";
    static GregorianCalendar ca = new GregorianCalendar();

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:m:s");
        Date date = format.parse("2016-08-17 09:30:35");
        log.info(format(date));
    }

    public static String format(Date date) {
		/*boolean leapYear = ca.isLeapYear(Calendar.getInstance().get(Calendar.YEAR));
        long delta = new Date().getTime() - date.getTime();
        if(delta<=0){
        	return new SimpleDateFormat("yyyy-MM-dd HH:m:s").format(date); 
        }
        if (delta < 1L * ONE_MINUTE) {//小于一分钟
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (delta < 60L * ONE_MINUTE) {//小于一小时
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {//小于一天
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (delta < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        long leapYearValue = leapYear?366L:365L;
        if (delta < leapYearValue * ONE_DAY) {
            long months = toMonths(delta,date);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(delta,date);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }*/
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
    	return sdf.format(date);
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date,Date original) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(original);
    	int month = cal.get(Calendar.MONTH);
    	int year = cal.get(Calendar.YEAR);
    	if(month ==1 || month ==3 ||month ==5 ||month ==7 ||month ==8 ||month ==10 ||month ==12){
    		return toDays(date) / 31L;	
    	}else if(month==2){
    		if(ca.isLeapYear(year)){
    			return toDays(date) / 29L;	
    		}else{
    			return toDays(date) / 28L;
    		}
    	}else{
    		return toDays(date) / 30L;
    	}
    }

    private static long toYears(long date,Date original) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(original);
    	int year = cal.get(Calendar.YEAR);
    	if(ca.isLeapYear(year)){
    		 return toMonths(date,original) / 366L;
		}else{
			 return toMonths(date,original) / 365L;
		}
       
    }
    
}
