package org.zywx.coopman.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.zywx.coopman.entity.dynamicuser.UserDay;
import org.zywx.coopman.entity.dynamicuser.UserDynamic;
import org.zywx.coopman.entity.dynamicuser.UserHour;
import org.zywx.coopman.entity.dynamicuser.UserMonth;
import org.zywx.coopman.entity.dynamicuser.UserWeek;


@Service
public class DynamicUserService extends BaseService{

	private int getCount(String dynamicType,UserDynamic td){
		if("task".equals(dynamicType)){
			return td.getTaskdynamic();
		}else if("all".equals(dynamicType)){
			return td.getTotaldynamic();
		}
		System.out.println("error-----td->"+td.toString());
		return 0;
	}
	
	public List<UserHour> findHourList(String date,String dynamicType,String keyWords){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT userid, username,account FROM C_USER_DYNAMIC WHERE DATE_FORMAT(v_time,'%Y-%m-%d') = DATE_FORMAT('"+date+"','%Y-%m-%d')");
		if(StringUtils.isNotBlank(keyWords)){
			sb.append(" and ( username like '%").append(keyWords).append("%'  or account like '%").append(keyWords).append("%')");
		}
		sb.append(" GROUP BY userid ");
		final List<UserHour> volist = new ArrayList<UserHour>();
		this.jdbcTpl.query(sb.toString(), 
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						UserHour th = new UserHour();
						th.setUserid(rs.getLong("userid"));
						th.setUsername(rs.getString("username"));
						volist.add(th);
					}
				});
		
		sb.setLength(0);
		sb.append("SELECT id,userid, username,account,totaldynamic,taskdynamic,v_time  FROM  C_USER_DYNAMIC WHERE DATE_FORMAT(v_time,'%Y-%m-%d') = DATE_FORMAT('"+date+"','%Y-%m-%d')");
		
		final List<UserDynamic> projectlist = new ArrayList<UserDynamic>();
		this.jdbcTpl.query(sb.toString(), 
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						UserDynamic td = new UserDynamic();
						td.setId(rs.getLong("id"));
						td.setUserid(rs.getLong("userid"));
						td.setUsername(rs.getString("username"));
						td.setAccount(rs.getString("account"));
						td.setTaskdynamic(rs.getInt("taskdynamic"));
						td.setTotaldynamic(rs.getInt("totaldynamic"));
						td.setV_time(rs.getTimestamp("v_time"));
						projectlist.add(td);
					}
				});
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
		for(UserHour th:volist){
			for(UserDynamic td:projectlist){
				if(th.getUserid()==td.getUserid()){
					if(sdf.format(td.getV_time()).equals(date + " 00")){
						th.setHour0(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 01")){
						th.setHour1(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 02")){
						th.setHour2(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 03")){
						th.setHour3(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 04")){
						th.setHour4(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 05")){
						th.setHour5(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 06")){
						th.setHour6(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 07")){
						th.setHour7(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 08")){
						th.setHour8(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 09")){
						th.setHour9(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 10")){
						th.setHour10(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 11")){
						th.setHour11(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 12")){
						th.setHour12(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 13")){
						th.setHour13(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 14")){
						th.setHour14(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 15")){
						th.setHour15(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 16")){
						th.setHour16(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 17")){
						th.setHour17(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 18")){
						th.setHour18(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 19")){
						th.setHour19(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 20")){
						th.setHour20(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 21")){
						th.setHour21(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 22")){
						th.setHour22(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(date + " 23")){
						th.setHour23(getCount(dynamicType,  td));
					}
					
				}
			}
		}
		
		return volist;
	}
	
	
	
	public List<UserDay> findDayList(String begin,String end,String dynamicType,String keyWords) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT userid,username,account FROM C_USER_DYNAMIC WHERE DATE_FORMAT(v_time,'%Y-%m-%d') >=DATE_FORMAT('"+begin+"','%Y-%m-%d') AND DATE_FORMAT(v_time,'%Y-%m-%d') <=DATE_FORMAT('"+end+"','%Y-%m-%d')  ");
		if(StringUtils.isNotBlank(keyWords)){
			sb.append(" and ( username like '%").append(keyWords).append("%'  or account like '%").append(keyWords).append("%')");
		}
		sb.append(" GROUP BY userid");
		final List<UserDay> volist = new ArrayList<UserDay>();
		this.jdbcTpl.query(sb.toString(), 
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						UserDay td = new UserDay();
						td.setUserid(rs.getLong("userid"));
						td.setUsername(rs.getString("username"));
						volist.add(td);
					}
				});
		
		sb.setLength(0);
		sb.append("SELECT id,userid,username,account,SUM(totaldynamic) totaldynamic,SUM(taskdynamic) taskdynamic,DATE_FORMAT(v_time,'%Y-%m-%d') v_time  FROM C_USER_DYNAMIC WHERE DATE_FORMAT(v_time,'%Y-%m-%d') >=DATE_FORMAT('"+begin+"','%Y-%m-%d') AND DATE_FORMAT(v_time,'%Y-%m-%d') <=DATE_FORMAT('"+end+"','%Y-%m-%d') GROUP BY userid,DATE_FORMAT(v_time,'%Y-%m-%d') ORDER BY DATE_FORMAT(v_time,'%Y-%m-%d')");
		
		final List<UserDynamic> projectlist = new ArrayList<UserDynamic>();
		this.jdbcTpl.query(sb.toString(), 
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						UserDynamic td = new UserDynamic();
						td.setId(rs.getLong("id"));
						td.setUsername(rs.getString("username"));
						td.setAccount(rs.getString("account"));
						td.setUserid(rs.getLong("userid"));
						td.setTaskdynamic(rs.getInt("taskdynamic"));
						td.setTotaldynamic(rs.getInt("totaldynamic"));
						td.setV_time(rs.getTimestamp("v_time"));
						projectlist.add(td);
					}
				});
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdday = new SimpleDateFormat("yyyy-MM-");
		for(UserDay th:volist){
			for(UserDynamic td:projectlist){
				if(th.getUserid()==td.getUserid()){
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "01")){
						th.setDay1(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "02")){
						th.setDay2(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "03")){
						th.setDay3(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "04")){
						th.setDay4(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "05")){
						th.setDay5(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "06")){
						th.setDay6(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "07")){
						th.setDay7(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "08")){
						th.setDay8(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "09")){
						th.setDay9(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "10")){
						th.setDay10(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "11")){
						th.setDay11(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "12")){
						th.setDay12(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "13")){
						th.setDay13(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "14")){
						th.setDay14(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "15")){
						th.setDay15(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "16")){
						th.setDay16(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "17")){
						th.setDay17(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "18")){
						th.setDay18(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "19")){
						th.setDay19(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "20")){
						th.setDay20(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "21")){
						th.setDay21(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "22")){
						th.setDay22(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "23")){
						th.setDay23(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "24")){
						th.setDay24(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "25")){
						th.setDay25(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "26")){
						th.setDay26(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "27")){
						th.setDay27(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "28")){
						th.setDay28(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "29")){
						th.setDay29(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "30")){
						th.setDay30(getCount(dynamicType,  td));
					}
					if(sdf.format(td.getV_time()).equals(sdday.format(sdf.parse(begin)) + "31")){
						th.setDay31(getCount(dynamicType,  td));
					}
					
				}
			}
		}
		
		return volist;
	}
	
	public List<UserWeek> findWeekList(String begin,String end,String dynamicType,String keyWords) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT userid,username,account FROM C_USER_DYNAMIC WHERE DATE_FORMAT(v_time,'%Y-%m-%d') >=DATE_FORMAT('"+begin+"','%Y-%m-%d') AND DATE_FORMAT(v_time,'%Y-%m-%d') <=DATE_FORMAT('"+end+"','%Y-%m-%d')  ");
		if(StringUtils.isNotBlank(keyWords)){
			sb.append(" and ( username like '%").append(keyWords).append("%'  or account like '%").append(keyWords).append("%')");
		}
		sb.append(" GROUP BY userid");
		final List<UserWeek> volist = new ArrayList<UserWeek>();
		this.jdbcTpl.query(sb.toString(), 
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						UserWeek td = new UserWeek();
						td.setUserid(rs.getLong("userid"));
						td.setUsername(rs.getString("username"));
						td.setAccount(rs.getString("account"));
						volist.add(td);
					}
				});
		
		sb.setLength(0);
		sb.append("SELECT id,userid,username,account,SUM(totaldynamic) totaldynamic,SUM(taskdynamic) taskdynamic,DATE_FORMAT(v_time,'%u') v_time  FROM C_USER_DYNAMIC WHERE DATE_FORMAT(v_time,'%Y-%m-%d') >=DATE_FORMAT('"+begin+"','%Y-%m-%d') AND DATE_FORMAT(v_time,'%Y-%m-%d') <=DATE_FORMAT('"+end+"','%Y-%m-%d') GROUP BY userid,DATE_FORMAT(v_time,'%u') ORDER BY DATE_FORMAT(v_time,'%u')");
		
		final List<UserDynamic> projectlist = new ArrayList<UserDynamic>();
		this.jdbcTpl.query(sb.toString(), 
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						UserDynamic td = new UserDynamic();
						td.setId(rs.getLong("id"));
						td.setUsername(rs.getString("username"));
						td.setAccount(rs.getString("account"));
						td.setUserid(rs.getLong("userid"));
						td.setTaskdynamic(rs.getInt("taskdynamic"));
						td.setTotaldynamic(rs.getInt("totaldynamic"));
						td.setWeek(rs.getString("v_time"));
						projectlist.add(td);
					}
				});
		for(UserWeek tw:volist){
			for(UserDynamic td:projectlist){
				if(tw.getUserid()==td.getUserid()){
					if("01".equals(td.getWeek())){
						tw.setWeek1(getCount(dynamicType,  td));
					}
					if("02".equals(td.getWeek())){
						tw.setWeek2(getCount(dynamicType,  td));
					}
					if("03".equals(td.getWeek())){
						tw.setWeek3(getCount(dynamicType,  td));
					}
					if("04".equals(td.getWeek())){
						tw.setWeek4(getCount(dynamicType,  td));
					}
					if("05".equals(td.getWeek())){
						tw.setWeek5(getCount(dynamicType,  td));
					}
					if("06".equals(td.getWeek())){
						tw.setWeek6(getCount(dynamicType,  td));
					}
					if("07".equals(td.getWeek())){
						tw.setWeek7(getCount(dynamicType,  td));
					}
					if("08".equals(td.getWeek())){
						tw.setWeek8(getCount(dynamicType,  td));
					}
					if("09".equals(td.getWeek())){
						tw.setWeek9(getCount(dynamicType,  td));
					}
					if("10".equals(td.getWeek())){
						tw.setWeek10(getCount(dynamicType,  td));
					}
					if("11".equals(td.getWeek())){
						tw.setWeek11(getCount(dynamicType,  td));
					}
					if("12".equals(td.getWeek())){
						tw.setWeek12(getCount(dynamicType,  td));
					}
					if("13".equals(td.getWeek())){
						tw.setWeek13(getCount(dynamicType,  td));
					}
					if("14".equals(td.getWeek())){
						tw.setWeek14(getCount(dynamicType,  td));
					}
					if("15".equals(td.getWeek())){
						tw.setWeek15(getCount(dynamicType,  td));
					}
					if("16".equals(td.getWeek())){
						tw.setWeek16(getCount(dynamicType,  td));
					}
					if("17".equals(td.getWeek())){
						tw.setWeek17(getCount(dynamicType,  td));
					}
					if("18".equals(td.getWeek())){
						tw.setWeek18(getCount(dynamicType,  td));
					}
					if("19".equals(td.getWeek())){
						tw.setWeek19(getCount(dynamicType,  td));
					}
					if("20".equals(td.getWeek())){
						tw.setWeek20(getCount(dynamicType,  td));
					}if("21".equals(td.getWeek())){
						tw.setWeek21(getCount(dynamicType,  td));
					}
					if("22".equals(td.getWeek())){
						tw.setWeek22(getCount(dynamicType,  td));
					}
					if("23".equals(td.getWeek())){
						tw.setWeek23(getCount(dynamicType,  td));
					}
					if("24".equals(td.getWeek())){
						tw.setWeek24(getCount(dynamicType,  td));
					}
					if("25".equals(td.getWeek())){
						tw.setWeek25(getCount(dynamicType,  td));
					}
					if("26".equals(td.getWeek())){
						tw.setWeek26(getCount(dynamicType,  td));
					}
					if("27".equals(td.getWeek())){
						tw.setWeek27(getCount(dynamicType,  td));
					}
					if("28".equals(td.getWeek())){
						tw.setWeek28(getCount(dynamicType,  td));
					}
					if("29".equals(td.getWeek())){
						tw.setWeek29(getCount(dynamicType,  td));
					}
					if("30".equals(td.getWeek())){
						tw.setWeek30(getCount(dynamicType,  td));
					}
					if("31".equals(td.getWeek())){
						tw.setWeek31(getCount(dynamicType,  td));
					}
					if("32".equals(td.getWeek())){
						tw.setWeek32(getCount(dynamicType,  td));
					}
					if("33".equals(td.getWeek())){
						tw.setWeek33(getCount(dynamicType,  td));
					}
					if("34".equals(td.getWeek())){
						tw.setWeek34(getCount(dynamicType,  td));
					}
					if("35".equals(td.getWeek())){
						tw.setWeek35(getCount(dynamicType,  td));
					}
					if("36".equals(td.getWeek())){
						tw.setWeek36(getCount(dynamicType,  td));
					}
					if("37".equals(td.getWeek())){
						tw.setWeek37(getCount(dynamicType,  td));
					}
					if("38".equals(td.getWeek())){
						tw.setWeek38(getCount(dynamicType,  td));
					}
					if("39".equals(td.getWeek())){
						tw.setWeek39(getCount(dynamicType,  td));
					}
					if("40".equals(td.getWeek())){
						tw.setWeek40(getCount(dynamicType,  td));
					}
					if("41".equals(td.getWeek())){
						tw.setWeek41(getCount(dynamicType,  td));
					}
					if("42".equals(td.getWeek())){
						tw.setWeek42(getCount(dynamicType,  td));
					}
					if("43".equals(td.getWeek())){
						tw.setWeek43(getCount(dynamicType,  td));
					}
					if("44".equals(td.getWeek())){
						tw.setWeek44(getCount(dynamicType,  td));
					}
					if("45".equals(td.getWeek())){
						tw.setWeek45(getCount(dynamicType,  td));
					}
					if("46".equals(td.getWeek())){
						tw.setWeek46(getCount(dynamicType,  td));
					}
					if("47".equals(td.getWeek())){
						tw.setWeek47(getCount(dynamicType,  td));
					}
					if("48".equals(td.getWeek())){
						tw.setWeek48(getCount(dynamicType,  td));
					}
					if("49".equals(td.getWeek())){
						tw.setWeek49(getCount(dynamicType,  td));
					}
					if("50".equals(td.getWeek())){
						tw.setWeek50(getCount(dynamicType,  td));
					}
					if("51".equals(td.getWeek())){
						tw.setWeek51(getCount(dynamicType,  td));
					}
					if("52".equals(td.getWeek())){
						tw.setWeek52(getCount(dynamicType,  td));
					}
					if("53".equals(td.getWeek())){
						tw.setWeek53(getCount(dynamicType,  td));
					}
					
				}
			}
		}
		
		return volist;
	}
	
	
	
	public List<UserMonth> findMonthList(String begin,String end,String dynamicType,String keyWords) throws Exception{
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT userid,username,account FROM C_USER_DYNAMIC WHERE DATE_FORMAT(v_time,'%Y-%m-%d') >=DATE_FORMAT('"+begin+"','%Y-%m-%d') AND DATE_FORMAT(v_time,'%Y-%m-%d') <=DATE_FORMAT('"+end+"','%Y-%m-%d')  ");
		if(StringUtils.isNotBlank(keyWords)){
			sb.append(" and ( username like '%").append(keyWords).append("%'  or account like '%").append(keyWords).append("%')");
		}
		sb.append(" GROUP BY userid");
		final List<UserMonth> volist = new ArrayList<UserMonth>();
		this.jdbcTpl.query(sb.toString(), 
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						UserMonth td = new UserMonth();
						td.setUserid(rs.getLong("userid"));
						td.setUsername(rs.getString("username"));
						td.setAccount(rs.getString("account"));
						volist.add(td);
					}
				});
		
		sb.setLength(0);
		sb.append("SELECT id,userid,username,account,SUM(totaldynamic) totaldynamic,SUM(taskdynamic) taskdynamic,DATE_FORMAT(v_time,'%Y-%m') v_time  FROM C_USER_DYNAMIC WHERE DATE_FORMAT(v_time,'%Y-%m-%d') >=DATE_FORMAT('"+begin+"','%Y-%m-%d') AND DATE_FORMAT(v_time,'%Y-%m-%d') <=DATE_FORMAT('"+end+"','%Y-%m-%d') GROUP BY userid,DATE_FORMAT(v_time,'%Y-%m') ORDER BY DATE_FORMAT(v_time,'%Y-%m')");
		
		final List<UserDynamic> projectlist = new ArrayList<UserDynamic>();
		this.jdbcTpl.query(sb.toString(), 
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						UserDynamic td = new UserDynamic();
						td.setId(rs.getLong("id"));
						td.setUsername(rs.getString("username"));
						td.setAccount(rs.getString("account"));
						td.setUserid(rs.getLong("userid"));
						td.setTaskdynamic(rs.getInt("taskdynamic"));
						td.setTotaldynamic(rs.getInt("totaldynamic"));
						td.setV_timeStr(rs.getString("v_time"));
						projectlist.add(td);
					}
				});
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdmonth = new SimpleDateFormat("yyyy-");
		for(UserMonth tm:volist){
			for(UserDynamic td:projectlist){
				if(tm.getUserid()==td.getUserid()){
					if(td.getV_timeStr().equals(sdmonth.format(sdf.parse(begin)) + "01" )){
						tm.setMonth1(getCount(dynamicType,  td));
					}
					if(td.getV_timeStr().equals(sdmonth.format(sdf.parse(begin)) + "02")){
						tm.setMonth2(getCount(dynamicType,  td));
					}
					if(td.getV_timeStr().equals(sdmonth.format(sdf.parse(begin)) + "03")){
						tm.setMonth3(getCount(dynamicType,  td));
					}
					if(td.getV_timeStr().equals(sdmonth.format(sdf.parse(begin)) + "04")){
						tm.setMonth4(getCount(dynamicType,  td));
					}
					if(td.getV_timeStr().equals(sdmonth.format(sdf.parse(begin)) + "05")){
						tm.setMonth5(getCount(dynamicType,  td));
					}
					if(td.getV_timeStr().equals(sdmonth.format(sdf.parse(begin)) + "06")){
						tm.setMonth6(getCount(dynamicType,  td));
					}
					if(td.getV_timeStr().equals(sdmonth.format(sdf.parse(begin)) + "07")){
						tm.setMonth7(getCount(dynamicType,  td));
					}
					if(td.getV_timeStr().equals(sdmonth.format(sdf.parse(begin)) + "08")){
						tm.setMonth8(getCount(dynamicType,  td));
					}
					if(td.getV_timeStr().equals(sdmonth.format(sdf.parse(begin)) + "09")){
						tm.setMonth9(getCount(dynamicType,  td));
					}
					if(td.getV_timeStr().equals(sdmonth.format(sdf.parse(begin)) + "10")){
						tm.setMonth10(getCount(dynamicType,  td));
					}
					if(td.getV_timeStr().equals(sdmonth.format(sdf.parse(begin)) + "11")){
						tm.setMonth11(getCount(dynamicType,  td));
					}
					if(td.getV_timeStr().equals(sdmonth.format(sdf.parse(begin)) + "12")){
						tm.setMonth12(getCount(dynamicType,  td));
					}
					
				}
			}
		}
		
		return volist;
	}
}
