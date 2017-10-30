package org.zywx.coopman.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.zywx.appdo.facade.user.entity.organization.Personnel;
import org.zywx.appdo.facade.user.service.organization.PersonnelFacade;
import org.zywx.coopman.commons.Enums.DELTYPE;
import org.zywx.coopman.commons.Enums.EMAIL_STATUS;
import org.zywx.coopman.commons.Enums.USER_LEVEL;
import org.zywx.coopman.commons.Enums.USER_STATUS;
import org.zywx.coopman.commons.Enums.UserGender;
import org.zywx.coopman.entity.Setting;
import org.zywx.coopman.entity.User;
import org.zywx.coopman.entity.UserAuth;
import org.zywx.coopman.entity.filialeInfo.FilialeInfo;
import org.zywx.coopman.system.Cache;
import org.zywx.coopman.system.InitBean;
import org.zywx.coopman.util.HttpTools;
import org.zywx.coopman.util.HttpUtil;
import org.zywx.coopman.util.MD5Util;
import org.zywx.coopman.util.mail.MailUtil;
import org.zywx.coopman.util.mail.base.MailSenderInfo;
import org.zywx.coopman.util.mail.base.SendMailTools;

import com.alibaba.dubbo.common.json.JSON;
import com.alibaba.dubbo.common.json.JSONArray;
import com.alibaba.dubbo.common.json.ParseException;

import net.sf.json.JSONObject;


@Service
public class UserService extends BaseService{

	@Value("${tenantId}")
	private String tenantId;
	
	@Value("${sso.regUrl}")
	private String ssoHost;
	
	@Value("${sso.host}")
	private String ssoUserHost;
	
	
	@Value("${sso.inseruser.creator}")
	private String ssoCreator;
	
	@Value("${headImgDir}")
	private String headImgDir;
	
	@Autowired
	private MailUtil mailUtil;
	
	
	@Value("${mail.username}")
	private String mailUserName;
	
	@Value("${mail.subject}")
	private String mailSubject;
	
	@Autowired(required=false)
	private PersonnelFacade personelFacade;
	
	@Autowired
	private SendMailTools sendMailTool;
	
	@Value("${emm3Url}")
	private String emm3Url;
	
	@Value("${emm3TestUrl}")
	private String emm3TestUrl;
	
	@Value("${serviceFlag}")
	private String serviceFlag;
	
    @Value("${mas.fobiddenUrl}")
	private String fobiddenUrl;
    
	public Page<User> findUserList(Long filialeId, int pageNumber,int pageSize,String sortType,String search){
		PageRequest pageRequest = buildPageRequest(pageNumber, pageSize, sortType);
		if(null==search || "".equals(search.trim())){
			List<USER_STATUS> status = new ArrayList<USER_STATUS>();
			status.add(USER_STATUS.NORMAL);
			status.add(USER_STATUS.FORBIDDEN);
			Page<User> list=userDao.findByStatusInAndFilialeIdAndDelOrderByCreatedAtDesc(status,filialeId,DELTYPE.NORMAL, pageRequest);
			return list;
		}else{
			List<USER_STATUS> status = new ArrayList<USER_STATUS>();
			status.add(USER_STATUS.NORMAL);
			status.add(USER_STATUS.FORBIDDEN);
			Page<User> list=userDao.findByUserNameLikeOrAccountLikeAndFilialeIdAndStatusInAndDel( "%"+search.trim()+"%", "%"+search.trim()+"%",filialeId,status,DELTYPE.NORMAL, pageRequest);
			return list;
		}
	}
	
	public Page<User> findUserList(int pageNumber,int pageSize,String sortType,String search,List filialeIdList){
		PageRequest pageRequest = buildPageRequest(pageNumber, pageSize, sortType);
		if(null==search || "".equals(search.trim())){
			List<USER_STATUS> status = new ArrayList<USER_STATUS>();
			status.add(USER_STATUS.NORMAL);
			status.add(USER_STATUS.FORBIDDEN);
			Page<User> list=userDao.findByStatusInAndDelOrderByCreatedAtDesc(status,DELTYPE.NORMAL, pageRequest);
			return list;
		}else{
			List<USER_STATUS> status = new ArrayList<USER_STATUS>();
			status.add(USER_STATUS.NORMAL);
			status.add(USER_STATUS.FORBIDDEN);
			
			Page<User> list = null ;
			//如果filialeIdList有值，查询了所属单位，则进行匹配查询，负责不传该参数。
			if(null!=filialeIdList&&0!=filialeIdList.size()){
				list=userDao.findByUserNameLikeOrAccountLikeOrEmailAndStatusInAndDel( "%"+search.trim()+"%", status,DELTYPE.NORMAL, pageRequest,filialeIdList);
			}else{
				list=userDao.findByUserNameLikeOrAccountLikeOrEmailAndStatusInAndDel( "%"+search.trim()+"%", status,DELTYPE.NORMAL, pageRequest);
			}
			return list;
		}
	}
	
	private PageRequest buildPageRequest(int pageNumber,int pageSize,String sortType){
		Sort sort = null;
        if ("id".equals(sortType)) {
            sort = new Sort(Direction.DESC, "id");
        } else if ("userName".equals(sortType)) {
        	sort = new Sort(Direction.DESC, "userName");
        }else if("account".equals(sortType))
        	sort = new Sort(Direction.DESC, "account");
        else if("createdAt".equals(sortType))
        	sort = new Sort(Direction.DESC, "createdAt");
        return new PageRequest(pageNumber-1, pageSize, sort);
	}

	/**
	 * @user jingjian.wu
	 * @date 2015年9月18日 下午4:28:14
	 */
	    
	public HSSFWorkbook export() {
		// 创建新的Excel 工作簿
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("userInfo");
		HSSFCellStyle setBorder = wb.createCellStyle();
	    setBorder.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
//	    List<User> listUser = userDao.findByDel(DELTYPE.NORMAL);
	    List<USER_STATUS> status = new ArrayList<USER_STATUS>();
		status.add(USER_STATUS.NORMAL);
		status.add(USER_STATUS.FORBIDDEN);
		List<User> listUser =userDao.findByStatusInAndDel(status, DELTYPE.NORMAL);
		for (User user : listUser) {
			FilialeInfo filialeInfo = filialeInfoDao.findOne(user.getFilialeId());
			user.setFilialeName(filialeInfo!=null||filialeInfo.getFilialeName()!=null?filialeInfo.getFilialeName():"");
		}
	    for(int index =0 ;index<=listUser.size();index++){
	    	HSSFRow row = sheet.createRow((short) index);
			if (index == 0) {
				row.setHeightInPoints((short) 30);// 设置ex单元格的高度
			} else {
				row.setHeightInPoints((short) 20);// 设置ex单元格的高度
			}
			if(serviceFlag.equals("efficient")){
				for(int i =0;i<12;i++){
					sheet.setColumnWidth(i, 5000);
					HSSFCell cell = row.createCell((short) i);
					String cellVal = "";
					if(index==0){
						if(i==0){
							cellVal = "账号";
						}else if(i==1){
							cellVal = "等级";
						}else if(i==2){
							cellVal = "姓名";
						}else if(i==3){
							cellVal = "昵称";
						}else if(i==4){
							cellVal = "接入平台";
						}else if(i==5){
							cellVal = "邮箱";
						}else if(i==6){
							cellVal = "性别";
						}else if(i==7){
							cellVal = "手机号";
						}else if(i==8){
							cellVal = "QQ";
						}else if(i==9){
							cellVal = "所在地";
						}else if(i==10){
							cellVal = "备注";
						}else if(i==11){
							cellVal = "启用/停用";
						}
					}else{
						User usr = listUser.get(index-1);
						if(i==0){
							cellVal = usr.getAccount();
						}else if(i==1){
							if(null!=usr.getUserlevel()){
								cellVal = usr.getUserlevel().name().equals("NORMAL")?"普通用户":"高级用户";
							}
						}else if(i==2){
							cellVal = usr.getUserName();
						}else if(i==3){
							cellVal = usr.getNickName();
						}else if(i==4){
							if(null!=usr.getJoinPlat()){
								cellVal = usr.getJoinPlat().name().equals("INNER")?"公司内部":"AppCan";
							}
						}else if(i==5){
							cellVal = usr.getEmail();
						}else if(i==6){
							if(null!=usr.getGender()){
								cellVal = usr.getGender().name().equals("MALE")?"男":"女";
							}
						}else if(i==7){
							cellVal = usr.getCellphone();
						}else if(i==8){
							cellVal = usr.getQq();
						}else if(i==9){
							cellVal = usr.getAddress();
						}else if(i==10){
							cellVal = usr.getRemark();
						}else if(i==11){
							if(null!=usr.getStatus()){
								cellVal = usr.getStatus().name().equals("NORMAL")?"启用":"停用";
							}
						}
					}
					try{
						cell.setCellStyle(setBorder);
						int intval = Integer.parseInt(cellVal);
						cell.setCellValue(intval);
					}catch(Exception e){
						cell.setCellValue(cellVal);
					}
				}
			}else{
				for(int i =0;i<9;i++){
					sheet.setColumnWidth(i, 5000);
					HSSFCell cell = row.createCell((short) i);
					String cellVal = "";
					if(index==0){
						if(i==0){
							cellVal = "用户名";
						}else if(i==1){
							cellVal = "邮箱";
						}else if(i==2){
							cellVal = "姓名";
						}else if(i==3){
							cellVal = "所属单位";
						}else if(i==4){
							cellVal = "性别";
						}else if(i==5){
							cellVal = "手机号";
						}else if(i==6){
							cellVal = "地址";
						}else if(i==7){
							cellVal = "备注";
						}else if(i==8){
							cellVal = "启用/停用";
						} 
					}else{
						User usr = listUser.get(index-1);
						if(i==0){
							cellVal = usr.getAccount();
						}else if(i==1){
							cellVal = usr.getEmail();
						}else if(i==2){
							cellVal = usr.getUserName();
						}else if(i==3){
							cellVal = usr.getFilialeName();
						}else if(i==4){
							if(null!=usr.getGender()){
								cellVal = usr.getGender().name().equals("MALE")?"男":"女";
							}
						}else if(i==5){
							cellVal = usr.getCellphone();
						}else if(i==6){
							cellVal = usr.getAddress();
						}else if(i==7){
							cellVal = usr.getRemark();
						}else if(i==8){
							if(null!=usr.getStatus()){
								cellVal = usr.getStatus().name().equals("NORMAL")?"启用":"停用";
							}
						}
					}
					try{
						cell.setCellStyle(setBorder);
						int intval = Integer.parseInt(cellVal);
						cell.setCellValue(intval);
					}catch(Exception e){
						cell.setCellValue(cellVal);
					}
				}
			}
	    }
		return wb;
	}

	/**
	 * @user jingjian.wu
	 * @date 2015年9月18日 下午8:21:31
	 */
	    
	public User save(User user) {
		if(user.getId() != null && user.getId() > 0){
			//更新用户时先清空用户的初始化权限
			List<UserAuth> uaList = userAuthDao.findByUserId(user.getId());
			if(uaList != null && !uaList.isEmpty()){
				userAuthDao.delete(uaList);
			}
		}
		FilialeInfo filiale = filialeInfoDao.findOne(user.getFilialeId());
		if(filiale != null){
			if(filiale.getId() == 1){
				user.setUserlevel(USER_LEVEL.ADVANCE);
			}else{
				user.setUserlevel(USER_LEVEL.NORMAL);
			}
		}else{
			user.setUserlevel(USER_LEVEL.NORMAL);
		}
		User userNew = userDao.save(user);
		
		//保存用户初始化权限
		List<Long> initPer = user.getInitPer();
		if(initPer != null && !initPer.isEmpty()){
			UserAuth ua = null;
			List<UserAuth> uaList = new ArrayList<UserAuth>();
			for(Long perId : initPer){
				ua = new UserAuth();
				ua.setUserId(userNew.getId());
				ua.setPermissionId(perId);
				uaList.add(ua);
			}
			this.userAuthDao.save(uaList);
		}
		
		return user;
	}

	/**
	 * @user jingjian.wu
	 * @date 2015年9月18日 下午9:29:12
	 */
	    
	public User updateOrFindOne(long userId) {
		User localUser = userDao.findOne(userId);
		String str = HttpTools.sendGet(ssoUserHost,"loginName="+localUser.getAccount());
		log.info("sso Info-->"+str);
		if(!StringUtils.isBlank(str)){
			str = str.replace("(", "");
			str = str.replace(")", "");
			str = str.replace("/n", "");
		}
		JSONObject sinupJson = JSONObject.fromObject(str);
		//SSO注册用户失败
		if ("fail".equals(sinupJson.getString("retCode"))) {
			log.info("reg sso return ==> "+sinupJson.getString("retMsg"));
			return null;
		}else{
			String userStr = sinupJson.getString("retData");
			JSONObject userObj = JSONObject.fromObject(userStr);
			String qq = null;
			if(userObj.containsKey("qq")){
				qq = userObj.getString("qq");
			}
			String mobile_phone =null;
			if(userObj.containsKey("mobile_phone")){
				mobile_phone = userObj.getString("mobile_phone");
			}
			String user_name = null;
			if(userObj.containsKey("user_name")){
				user_name = userObj.getString("user_name");
			}
			String user_pic = null;
			if(userObj.containsKey("user_pic")){
				user_pic = userObj.getString("user_pic");
			}
			//"province":"北京","city":"市辖区","area":"西城区","street":"第三段"
			String province = null;
			if(userObj.containsKey("province")){
				province = userObj.getString("province");
			}
			String city = null;
			if(userObj.containsKey("city")){
				city = userObj.getString("city");
			}
			String area = null;
			if(userObj.containsKey("area")){
				area = userObj.getString("area");
			}
			String street = null;
			if(userObj.containsKey("street")){
				street = userObj.getString("street");
			}
			
			String address =  (province==null?"":province+"-") + (city==null?"":city+"-") + (area==null?"":area+"-") + (street==null?"":street);
			
			User user = localUser;
//			user.setAccount(localUser.getAccount());
			if(null!=mobile_phone && !"".equals(mobile_phone)){
				user.setCellphone(mobile_phone);
			}
//			user.setEmail(localUser.getAccount());
			if(null!=user_pic && !"".equals(user_pic)){
				user.setIcon(user_pic);
			}
			if(null!=user_name && !"".equals(user_name)){
				user.setUserName(user_name);
			}
			if(null!=qq && !"".equals(qq)){
				user.setQq(qq);
			}
			user.setAddress(address);
//			user.setJoinPlat(USER_JOINPLAT.APPCAN);
//			user.setStatus(USER_STATUS.NORMAL);//审核通过
//			user.setUserlevel(USER_LEVEL.ADVANCE);
//			user.setType(USER_TYPE.AUTHENTICATION);
			if(userObj.containsKey("nickname")){
				user.setNickName(userObj.getString("nickname"));
			}
			this.userDao.save(user);
			return user;
		}
		
	}
	
	public User findOne(long userId){
		User u = userDao.findOne(userId);
		if(u != null){
			//查询用户初始化权限
			List<UserAuth> uaList = userAuthDao.findByUserId(u.getId());
			if(uaList != null){
				List<Long> uaIdList = new ArrayList<Long>();
				for(UserAuth ua : uaList){
					uaIdList.add(ua.getPermissionId());
				}
				u.setInitPer(uaIdList);
			}
		}
		return u;
	}
	
	public String saveUsersFromExcel(File file) throws IOException{
		Map<String,Object> userMap = new HashMap<String,Object>();
		if(serviceFlag.equals("enterpriseEmm3")||serviceFlag.equals("efficient")){
			userMap=this.readXlsEmmInvoke(file);
		}else{
			userMap=this.readXls(file);
		}
		@SuppressWarnings("unchecked")
		List<User> list = (List<User>) userMap.get("userInfo");
		String importXlsInfo =userMap.get("failedEmails").toString();
		String faileRows =userMap.get("faileRows").toString();
		userDao.save(list);
		if(StringUtils.isNotBlank(faileRows)){
			return faileRows;
		}
		if(StringUtils.isBlank(importXlsInfo)){
			return "ok";
		}else{
			return importXlsInfo.substring(0,importXlsInfo.length()-1)+"导入失败！";
		}
		
	}

	/**
     * 读取xls文件内容
     * 
     * @return List<XlsDto>对象
     * @throws IOException
     *             输入/输出(i/o)异常
     */
    private Map<String,Object> readXls(File xlxFile) throws IOException {
        InputStream is = new FileInputStream(xlxFile);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        User user = null;
        int count=0;
        Map<String,Object> userInfo = new HashMap<String,Object>();
        List<User> list = new ArrayList<User>();
        Map<String,Long> filiMap = new HashMap<String,Long>();
        if(hssfWorkbook.getNumberOfSheets() > 1){
        	Iterator<FilialeInfo> filialeIter = filialeInfoDao.findAll().iterator();
        	FilialeInfo fili = null;
        	while(filialeIter.hasNext()){
        		fili = filialeIter.next();
        		filiMap.put(fili.getFilialeName(), fili.getId());
        	}
        }
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 循环行Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow == null) {
                    continue;
                }
                user = new User();
                // 循环列Cell
                // for (int cellNum = 0; cellNum <=4; cellNum++) {
                HSSFCell account = hssfRow.getCell(0);
                user.setAccount(getValue(account));
                
                HSSFCell email = hssfRow.getCell(1);
                if (email == null) {
                	userInfo.put("failedEmails", "用户邮箱不能为空");
                	hssfWorkbook.close();
                	return userInfo;
                }
                user.setEmail(getValue(email));
                if(!user.getEmail().matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")){
                	userInfo.put("failedEmails", "用户邮箱:"+user.getEmail()+" 不符合邮箱规则");
                	hssfWorkbook.close();
                	return userInfo;
                }
                if(StringUtils.isBlank(user.getAccount())){//如果用户名为空,则取邮箱前缀
                	user.setAccount(user.getEmail().substring(0,user.getEmail().indexOf('@')));
                }
                if(existByProperty("account", user.getAccount())  || existByProperty("email", user.getEmail())){
                	count++;
                	//如果用户名或者邮箱已经存在,则忽略该用户
                	continue;
                }
                HSSFCell userName = hssfRow.getCell(2);
                if (userName != null) {
                	user.setUserName(getValue(userName));
                }
                HSSFCell userFiliale = hssfRow.getCell(3);
                if(userFiliale == null){
                	userInfo.put("failedEmails", "用户:"+user.getAccount()+" 所属单位不能为空");
                	hssfWorkbook.close();
                	return userInfo;
                }
                if(filiMap.get(getValue(userFiliale)) == null){
                	userInfo.put("failedEmails", "用户:"+user.getAccount()+" 所属单位不正确");
                	hssfWorkbook.close();
                	return userInfo;
                }
                user.setFilialeId(filiMap.get(getValue(userFiliale)));
                
                HSSFCell userSex = hssfRow.getCell(4);
                if (StringUtils.isBlank(userSex.toString())) {
               	 	user.setGender(UserGender.MALE);
                }else{
               	 	user.setGender(getValue(userSex).equals("男") ? UserGender.MALE : UserGender.FEMALE);
                }
                
                HSSFCell cellphone = hssfRow.getCell(5);
                if(cellphone!=null){
                	BigDecimal phone= new BigDecimal(cellphone.toString());//将科学计数的电话号码转成BigDecimal类型，再转成字符串判断是否符合手机号格式
                	if(!phone.toPlainString().matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$")){
                        count++;//不符合格式count加1
                    	continue;	
                	}
                }
                if (cellphone == null) {
                    count++;//若电话号码为空
                	continue;
                }
                BigDecimal phone= new BigDecimal(cellphone.toString());
                user.setCellphone(phone.toPlainString());
                
              
               
                HSSFCell userAddr = hssfRow.getCell(6);
                if (userAddr != null) {
                	user.setAddress(getValue(userAddr));
                }
                
                user.setIcon(headImgDir);
                
                list.add(user);
                
            }
        }
        userInfo.put("userInfo",list);
        userInfo.put("count", count);
        hssfWorkbook.close();
        return userInfo;
    }
    /**
     * Emm3.3读取xls文件内容
     * 
     * @return List<XlsDto>对象
     * @throws IOException
     *             输入/输出(i/o)异常
     */
    private Map<String,Object> readXlsEmmInvoke(File xlxFile) throws IOException {
        InputStream is = new FileInputStream(xlxFile);
        @SuppressWarnings("resource")
        Workbook workbook=null;
       if(xlxFile.getName().toLowerCase().endsWith("xls")){
    	   workbook = new HSSFWorkbook(is);
       }else if(xlxFile.getName().toLowerCase().endsWith("xlsx")){
    	   workbook = new XSSFWorkbook(is); 
       }
         
//        XSSFWorkbook xssfworkbook = new XSSFWorkbook(is);
        User user = null;
        String failedEmails="";
        String faileRows="";
        Map<String,Object> userInfo = new HashMap<String,Object>();
        List<User> list = new ArrayList<User>();
        // 循环工作表Sheet
        for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
        	if(xlxFile.getName().toLowerCase().endsWith("xls")){
        		 HSSFSheet hssfSheet = (HSSFSheet) workbook.getSheetAt(numSheet);
        		 if (hssfSheet == null) {
                     continue;
                 }
                 // 循环行Row
                 for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                     HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                     if (hssfRow == null||isBlankRow(hssfRow)) {
                         continue;
                     }
                    
                     user = new User();
                     // 循环列Cell
                     //用户名
                     HSSFCell account = hssfRow.getCell(0);
                     if (account==null||StringUtils.isBlank(account.toString())) {
                    	 faileRows+=rowNum+" ";
                         continue;
                     }
                     user.setAccount(getValue(account));
                     if(existByProperty("account", user.getAccount())){
                     	//如果用户名或者邮箱已经存在,则忽略该用户
                    	failedEmails=failedEmails+account+"、";
                    	 faileRows+=rowNum+" ";
                     	continue;
                     }
                     //邮箱
                     HSSFCell email = hssfRow.getCell(1);
                     if (email==null||StringUtils.isBlank(email.toString())) {
                    	 faileRows+=rowNum+" ";
                         continue;
                     }
                    // user.setAccount(getValue(email));//如果是EMM3.3则将帐号赋值邮箱
                     user.setEmail(getValue(email));
                     if(!user.getEmail().matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")){
                    	failedEmails=failedEmails+email+"、";
                    	 faileRows+=rowNum+" ";
                     	continue;
                     }
                     if(existByProperty("email", user.getEmail())){
                     	//如果用户名或者邮箱已经存在,则忽略该用户
                    	failedEmails=failedEmails+email+"、";
                    	 faileRows+=rowNum+" ";
                     	continue;
                     }
                     
					// 姓名
					HSSFCell usrename = hssfRow.getCell(2);
					if (usrename == null
							|| StringUtils.isBlank(usrename.toString())) {
						 faileRows+=rowNum+" ";
						continue;
					}
					user.setUserName(getValue(usrename));

					// 所属单位
					HSSFCell userFiliale = hssfRow.getCell(3);
					if (userFiliale == null
							|| StringUtils.isBlank(userFiliale.toString())) {
						 faileRows+=rowNum+" ";
						continue;
					}
					FilialeInfo filialeInfo = filialeInfoDao.findByFilialeName(getValue(userFiliale));
					if (filialeInfo == null || filialeInfo.getId() < 1) {
						 faileRows+=rowNum+" ";
						continue;
					}
					user.setFilialeId(filialeInfo.getId());
					
					//性别
					HSSFCell userSex = hssfRow.getCell(4);
                    if (userSex==null||StringUtils.isBlank(userSex.toString())) {
                   	 user.setGender(UserGender.MALE);
                    }else{
                   	 user.setGender(getValue(userSex).equals("男")?UserGender.MALE:UserGender.FEMALE);
                    }
					//手机号
                     HSSFCell cellphone = hssfRow.getCell(5);
                     if (cellphone==null||StringUtils.isBlank(cellphone.toString())) {
                         user.setCellphone("");
                     }
                     if(cellphone!=null){
                     	/*BigDecimal phone= new BigDecimal(cellphone.toString());//将科学计数的电话号码转成BigDecimal类型，再转成字符串判断是否符合手机号格式
                     	if(!phone.toPlainString().matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$")){
                     		failedEmails=failedEmails+email+"、";
                         	continue;	
                     	}*/
                        user.setCellphone(getValue(cellphone ));
                     }
                     //地址
                     HSSFCell address = hssfRow.getCell(6);
                     if (address==null||StringUtils.isBlank(address.toString())) {
                    	 user.setAddress("");
                     }else{
                    	 user.setAddress(getValue(address));
                     }
                     //地址
                     HSSFCell remark = hssfRow.getCell(7);
                     if (remark==null||StringUtils.isBlank(remark.toString())) {
                    	 user.setRemark ("");
                     }else{
                    	 user.setRemark(getValue(remark));
                     }
                    /* HSSFCell qq = hssfRow.getCell(4);
                     if (qq==null||StringUtils.isBlank(qq.toString())) {
                     	user.setQq("");
                     }else{
                     	 BigDecimal qqNum= new BigDecimal(qq.toString());
                     	 if(qqNum.toPlainString().matches("/^[0-9]*$/")){
                     		 failedEmails=failedEmails+email+"、";
                     		 continue;
                     	 }
                          if(qqNum.toPlainString().indexOf(".")>-1){
                          	 user.setQq(qqNum.toPlainString().substring(0,qqNum.toPlainString().lastIndexOf(".")));
                          }else{
                          	 user.setQq(qqNum.toPlainString());
                          }
                     }*/
                     
                     user.setIcon(headImgDir);
                     user.setPassword(MD5Util.MD5("123456").toLowerCase());
                     list.add(user);
                     
                 }
        	}else if(xlxFile.getName().toLowerCase().endsWith("xlsx")){
        		 XSSFSheet xssfSheet = (XSSFSheet) workbook.getSheetAt(numSheet);
        		 if (xssfSheet == null) {
                     continue;
                 }
                 // 循环行Row
                 for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                     XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                     if (xssfRow == null) {
                         continue;
                     }
                     user = new User();
                     // 循环列Cell
                     XSSFCell email = xssfRow.getCell(1);
                     if (email==null||StringUtils.isBlank(email.toString())) {
                    	 failedEmails=failedEmails+email+"、";
                         continue;
                     }
                     user.setEmail(getValue(email));
                     user.setAccount(getValue(email));//如果是EMM3.3则将帐号赋值邮箱
                     if(!user.getEmail().matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")){
                    	failedEmails=failedEmails+email+"、";
                     	continue;
                     }
                    
                     if(existByProperty("account", user.getAccount())  || existByProperty("email", user.getEmail())){
                     	//如果用户名或者邮箱已经存在,则忽略该用户
                    	failedEmails=failedEmails+email+"、"; 
                     	continue;
                     }
                     XSSFCell userLevel = xssfRow.getCell(2);
                     if (userLevel==null||StringUtils.isBlank(userLevel.toString())) {
                     	user.setUserlevel(USER_LEVEL.NORMAL);//用户等级是空设置为普通用户
                     }else{
                    	 user.setUserlevel(USER_LEVEL.valueOf(getValue(userLevel).equals("高级用户")?"ADVANCE":"NORMAL"));
                     }
                     XSSFCell cellphone = xssfRow.getCell(3);
                     if (cellphone==null||StringUtils.isBlank(cellphone.toString())) {
                    	failedEmails=failedEmails+email+"、"; 
                     	continue;
                     }
                     if(cellphone!=null){
                     	BigDecimal phone= new BigDecimal(cellphone.toString());//将科学计数的电话号码转成BigDecimal类型，再转成字符串判断是否符合手机号格式
                     	if(!phone.toPlainString().matches("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$")){
                     		failedEmails=failedEmails+email+"、";
                         	continue;	
                     	}
                     }
                     BigDecimal phone= new BigDecimal(cellphone.toString());
                     user.setCellphone(phone.toPlainString());
                     
                     XSSFCell qq = xssfRow.getCell(4);
                     if (qq==null||StringUtils.isBlank(qq.toString())) {
                     	user.setQq("");
                     }else{
                     	 BigDecimal qqNum= new BigDecimal(qq.toString());
                     	 if(qqNum.toPlainString().matches("/^[0-9]*$/")){
                     		 failedEmails=failedEmails+email+"、";
                     		 continue;
                     	 }
                          if(qqNum.toPlainString().indexOf(".")>-1){
                          	 user.setQq(qqNum.toPlainString().substring(0,qqNum.toPlainString().lastIndexOf(".")));
                          }else{
                          	 user.setQq(qqNum.toPlainString());
                          }
                     }
                    
                     XSSFCell usrename = xssfRow.getCell(0);
                     if (usrename==null||StringUtils.isBlank(usrename.toString())) {
                    	failedEmails=failedEmails+email+"、";
                     	continue;
                     }
                     user.setUserName(getValue(usrename));
                     
                     XSSFCell userSex = xssfRow.getCell(5);
                     if (userSex==null||StringUtils.isBlank(userSex.toString())) {
                    	 user.setGender(null);
                     }else{
                    	 user.setGender(getValue(usrename).equals("男")?UserGender.MALE:UserGender.FEMALE);
                     }
                     
                     if(serviceFlag.equals("efficient")){
                    	 XSSFCell userNickname = xssfRow.getCell(6);
                         if (userNickname==null||StringUtils.isBlank(userNickname.toString())) {
                         	failedEmails=failedEmails+email+"、";
                         	continue;
                         }else{
                        	 user.setNickName(getValue(userNickname));
                         }
                     }
                     user.setIcon(headImgDir);
                     user.setPassword(MD5Util.MD5("123456").toLowerCase());
                     list.add(user);
                     
                 }
        	}
           
            
        }
        userInfo.put("userInfo",list);
        userInfo.put("failedEmails", failedEmails);
        userInfo.put("faileRows", faileRows);
        return userInfo;
    }
    /**
     * 检测excel是否空行
     * @param row
     * @return
     */
    private  boolean isBlankRow(HSSFRow row){
        if(row == null) return true;
        boolean result = true;
        for(int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++){
            HSSFCell cell = row.getCell(i, HSSFRow.RETURN_BLANK_AS_NULL);
            String value = "";
            if(cell != null){
                switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    value = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    value = String.valueOf((int) cell.getNumericCellValue());
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    value = String.valueOf(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    value = String.valueOf(cell.getCellFormula());
                    break;
                //case Cell.CELL_TYPE_BLANK:
                //    break;
                default:
                    break;
                }
                 
                if(!value.trim().equals("")){
                    result = false;
                    break;
                }
            }
        }
         
        return result;
    }
    /**
     * 得到Excel表中的值
     * 
     * @param hssfCell
     *            Excel中的每一个格子
     * @return Excel中每一个格子中的值
     */
    @SuppressWarnings("static-access")
    private String getValue(HSSFCell hssfCell) {
//    	 return String.valueOf(hssfCell.getStringCellValue());
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            // 返回字符串类型的值
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }
    private String getValue(XSSFCell hssfCell) {
//   	 return String.valueOf(hssfCell.getStringCellValue());
       if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
           // 返回数值类型的值
           return String.valueOf(hssfCell.getNumericCellValue());
       } else {
           // 返回字符串类型的值
           return String.valueOf(hssfCell.getStringCellValue());
       }
   }
    /**
     * 从EMM选人插入到本地及SSO
     * @throws IOException 
     * @throws ClientProtocolException 
     * @user jingjian.wu
     * @date 2015年9月24日 下午4:06:55
     */
    public  List<List<String>> addUserFromEMM(String token,List<Long> personIds,List<String> uniqueFields,HttpServletRequest request) throws ClientProtocolException, IOException{
    	List<List<String>> result = new ArrayList<List<String>>();
    	List<String> emailList = new ArrayList<String>();
    	List<String> alreadyemail = new ArrayList<String>();
    	
    	if(null!=personelFacade && !serviceFlag.equals("enterpriseEmm3")){//emm4.0
    		List<Personnel> listPerson = personelFacade.getByIds(token, personIds);
        	
        	List<String> nickExist = new ArrayList<String>();
        	String loginUserName =request.getSession(true).getAttribute("userName").toString();
    		
        	for(Personnel p:listPerson){
        		//插入本地数据库
        		User u = userDao.findByAccountAndDel(p.getEmail(), DELTYPE.NORMAL);
        		
        		String randomCode =MD5Util.generateCode();
    	    	String md5Pass = MD5Util.MD5(randomCode).toLowerCase();
    	    	boolean sendMailpwd = true;//发送邮件时候，是否要发送密码,
        		if(u==null){
        			User user = new User();
                	user.setAccount(p.getEmail().toLowerCase());
                	user.setEmail(p.getEmail());
                	user.setUserName(p.getName());
                	user.setCellphone(p.getMobileNo());
                	user.setDel(DELTYPE.NORMAL);
                	user.setGender(UserGender.valueOf(p.getUserSex().toUpperCase()));
                	user.setPassword(md5Pass);
                	user.setIcon(headImgDir);
                	user.setNickName(p.getUniqueField());
                	userDao.save(user);
                	
                	//插入SSO
                	
        			StringBuffer sb = new StringBuffer();
        			sb.append("loginName=");
        			sb.append(p.getEmail());
        			sb.append("&tenantId=");
        			sb.append(tenantId);
        			sb.append("&tenantName=");
        			sb.append(ssoCreator);
        			sb.append("&nickName=");
        			sb.append(p.getUniqueField());
        			sb.append("&captcha=");
        			sb.append("");
        			sb.append("&loginPassword=");
        			sb.append(randomCode);
        			sb.append("&regSource=");
        			sb.append(Cache.getSetting("SETTING").getWebAddr());
        			sb.append("&regRefer=''");
        			sb.append("&regFrom=xt");
        			sb.append("&isAdmin=0&isUse=1&creator=");
        			sb.append(ssoCreator);
        			sb.append("&userEmail=");
        			sb.append(p.getEmail());
        			log.info("password====================================================================>"+randomCode);
        			log.info("send post ===========> "+sb.toString());
            		String postResult = HttpTools.sendPost(ssoHost+"signup", sb.toString());
            		log.info(postResult);
            		if(!StringUtils.isBlank(postResult)){
            			postResult = postResult.replace("(", "");
            			postResult = postResult.replace(")", "");
        			}
        			JSONObject sinupJson = JSONObject.fromObject(postResult);
        			//SSO注册用户失败
        			if ("fail".equals(sinupJson.getString("retCode"))) {
        				log.info("reg sso return ==> "+sinupJson.getString("retMsg"));
        				if(!sinupJson.getString("extraInfo").contains("userExist")){
        					throw new RuntimeException(sinupJson.getString("retMsg"));
        				}else{//用户已存在SSO中
        					log.info("user already in sso -->"+p.getEmail());
        					sendMailpwd = false;
        					String params = "propName=name&propValue="+p.getEmail();
        					log.info("params=====>"+params);
        					List<NameValuePair> parameters = new ArrayList<>();
        					parameters.add( new BasicNameValuePair("propName", "name") );
        					parameters.add( new BasicNameValuePair("propValue", p.getEmail() ) );
        					String getResult = HttpUtil.httpPost(ssoHost+"userCheck", parameters);
//        					String getResult = HttpTools.sendGet(ssoHost+"userCheck",params);
        					log.info("==================判断邮箱还是昵称已存在"+getResult);
        	        		if(!StringUtils.isBlank(getResult)){
        	        			getResult = getResult.replace("(", "");
        	        			getResult = getResult.replace(")", "");
        	    			}
        	    			JSONObject existStr = JSONObject.fromObject(getResult);
        	    			if ("fail".equals(existStr.getString("retCode"))) {//邮箱已存在
        	    				alreadyemail.add(p.getEmail());
        	    				emailList.add(p.getEmail());
        	    			}else{
        	    				nickExist.add(p.getEmail());
        	    				throw new RuntimeException("用户"+p.getEmail()+"的昵称已存在,请修改昵称之后再添加");
        	    			}
        				}
        			}else{
        				emailList.add(p.getEmail());
        			}
        			MailSenderInfo mailInfo = new MailSenderInfo();
        			mailInfo.setContent("Hi,"+p.getEmail()+"</br>"+loginUserName+"通知您加入协同开发啦！</br>地址:"+"<a href='"+Cache.getSetting("SETTING").getWebAddr()+"'>"+Cache.getSetting("SETTING").getWebAddr()+"</a>"+"</br>用户名:"+p.getEmail()+(sendMailpwd?("</br>密码:"+randomCode):"")+"</br>祝您使用愉快!");
        			mailInfo.setToAddress(p.getEmail());
        			log.info("mail Subject-->"+mailSubject);
        			sendMailTool.sendMailByAsynchronousMode(mailInfo);
        		}
            	
        	}
    	}else{//emm3.3
        	
        	String loginUserName =request.getSession(true).getAttribute("userName").toString();
    		
        	for(String uniqueField:uniqueFields){
        		//插入本地数据库
        		User u = userDao.findByAccountAndDel(uniqueField, DELTYPE.NORMAL);
        		
        		Map<String,String> map = new HashMap<String,String>();
        		map.put("uniqueField", uniqueField);
        		String personStr = HttpTools.doPost(emm3Url+"/mum/personnel/getPersonelByUnique", map, "utf-8");
        		log.info("get personel info for uniqueFiled:"+uniqueField+",return -> "+personStr);
        		JSONObject json = JSONObject.fromObject(personStr);
        		
        		String randomCode =MD5Util.generateCode();
    	    	String md5Pass = MD5Util.MD5(randomCode).toLowerCase();
//    	    	boolean sendMailpwd = true;//发送邮件时候，是否要发送密码,
        		if(u==null){
        			User user = new User();
                	user.setAccount(uniqueField);
                	user.setEmail(getValueFromJSONObject("email", json));
                	user.setUserName(getValueFromJSONObject("name", json));
                	user.setCellphone(getValueFromJSONObject("mobileNo", json));
                	user.setDel(DELTYPE.NORMAL);
                	//TODO gender
//                	user.setGender(UserGender.valueOf(getValueFromJSONObject("userSex", json).toUpperCase()));
                	user.setPassword(md5Pass);
                	user.setIcon(headImgDir);
                	userDao.save(user);
                	
                	//插入EMM
            		personStr = HttpTools.doPost(emm3Url+"/mum/personnel/savePersonelEMM", map, "utf-8");
            		log.info("sign up personel to Emm :"+uniqueField+",return -> "+personStr);
            		json = JSONObject.fromObject(personStr);
                	if("ok".equals(json.getString("status"))){
                		emailList.add(uniqueField);
                	}else{
                		throw new RuntimeException(uniqueField+" 注册为EMM登录人员失败.");
                	}
                	
                	//邮件由EMM发送,如果后期需要修改,则需要EMM给协同返回初始化密码,然后协同也发一份邮件.
        			/*if(StringUtils.isNotBlank(user.getEmail())){
        				
        				MailSenderInfo mailInfo = new MailSenderInfo();
        				mailInfo.setContent("Hi,"+uniqueField+"</br>"+loginUserName+"通知您加入协同开发啦！</br>地址:"+"<a href='"+Cache.getSetting("SETTING").getWebAddr()+"'>"+Cache.getSetting("SETTING").getWebAddr()+"</a>"+"</br>用户名:"+uniqueField+(sendMailpwd?("</br>密码:"+randomCode):"")+"</br>祝您使用愉快!");
        				mailInfo.setToAddress(user.getEmail());
        				log.info("mail Subject-->"+mailSubject);
        				sendMailTool.sendMailByAsynchronousMode(mailInfo);
        			}*/
        		}
            	
        	}
    	}
    	result.add(emailList);
    	result.add(alreadyemail);
    	return result;
    }
    
    
    public void updatePwd(Long userId,String pwd){
    	String randomCode =(null==pwd||"".equals(pwd))?MD5Util.generateCode():pwd;
    	String md5Pass = MD5Util.MD5(randomCode).toLowerCase();

    	User user = this.userDao.findOne(userId);
    	if(serviceFlag.equals("enterpriseEmm3")){
    		user.setPassword(md5Pass);
    		this.userDao.save(user);
    	}else{
    		//调用sso接口修改密码
        	StringBuffer sb = new StringBuffer();
        	sb.append("name=")
        	.append(user.getAccount());
        	String postResult = HttpTools.sendPost(ssoHost+"pwdToken", sb.toString());
        	log.info("invoke sso update pwd get token: "+postResult);
    		if(!StringUtils.isBlank(postResult)){
    			postResult = postResult.replace("(", "");
    			postResult = postResult.replace(")", "");
    		}
    		JSONObject result = JSONObject.fromObject(postResult);
    		//SSO注册用户失败
    		if ("fail".equals(result.getString("retCode"))) {
    			throw new RuntimeException("调用SSO修改密码获取token失败");
    		}else{
    			String token = result.getString("retData");
    			StringBuffer sb1 = new StringBuffer();
    	    	sb1.append("name=")
    	    	.append(user.getAccount())
    	    	.append("&newPwd=")
    	    	.append(randomCode)
    	    	.append("&token=")
    	    	.append(token);
    	    	String updateResult = HttpTools.sendPost(ssoHost+"pwdReset", sb1.toString());
    	    	log.info("invoke sso update pwd  : "+updateResult);
    			if(!StringUtils.isBlank(updateResult)){
    				updateResult = postResult.replace("(", "");
    				updateResult = postResult.replace(")", "");
    			}
    			JSONObject resultPwd = JSONObject.fromObject(updateResult);
    			if ("fail".equals(resultPwd.getString("retCode"))) {
    				throw new RuntimeException("调用SSO修改密码失败");
    			}
    		}
    	}
		if(null==pwd||"".equals(pwd)){
			MailSenderInfo mailInfo = new MailSenderInfo();
			mailInfo.setContent("Hi,"+user.getAccount()+"</br>"+"管理员已重置您的密码,您的密码是:"+randomCode+","+"</br>协同网址: "+"<a href='"+Cache.getSetting("SETTING").getWebAddr()+"'>"+Cache.getSetting("SETTING").getWebAddr()+"</a>"+"</br>祝您使用愉快!");
			mailInfo.setToAddress(user.getEmail());
			sendMailTool.sendMailByAsynchronousMode(mailInfo);
		}
		
    }
    public static void main(String[] args) throws IOException {
		/*List<User> listUser = new UserService().readXls(new File("d:\\userInfo.xls"));
		for(User user:listUser){
			System.out.println(user.getUserName());
			System.out.println(user.getAccount());
			System.out.println(user.getUserlevel());
			System.out.println(user.getCellphone());
			System.out.println(user.getQq());
			System.out.println("===============================");
		}*/
    	List<NameValuePair> parameters = new ArrayList<>();
		parameters.add( new BasicNameValuePair("propName", "name") );
		parameters.add( new BasicNameValuePair("propValue", "111111@qq.com" ) );
		String getResult = HttpUtil.httpPost("http://192.168.4.168:8080/"+"userCheck", parameters);
//		String getResult = HttpTools.sendGet(ssoHost+"userCheck",params);
		System.out.println("==================判断邮箱还是昵称已存在"+getResult);
    }

	public Page<User> findUserListByAuth(Long filialeId, Integer pageNumber, Integer pageSize, String sortType, String search,
			USER_STATUS status) {
		PageRequest pageRequest = buildPageRequest(pageNumber, pageSize, sortType);
		if(null==search || "".equals(search.trim())){
			Page<User> list=userDao.findByStatusAndFilialeIdAndDelOrderByCreatedAtDesc(status, filialeId, DELTYPE.NORMAL, pageRequest);
			return list;
		}else{
			Page<User> list=userDao.findByUserNameLikeOrAccountLikeAndFilialeIdAndStatusAndDel("%"+search.trim()+"%", "%"+search.trim()+"%",filialeId, status,DELTYPE.NORMAL, pageRequest);
			return list;
		}
	}
	
	public Page<User> findUserListByAuth(Integer pageNumber, Integer pageSize, String sortType, String search,
			USER_STATUS status,List filialeIdList) {
		PageRequest pageRequest = buildPageRequest(pageNumber, pageSize, sortType);
		if(null==search || "".equals(search.trim())){
			Page<User> list=userDao.findByStatusAndDelOrderByCreatedAtDesc(status, DELTYPE.NORMAL, pageRequest);
			return list;
		}else{
			Page<User> list=null;
			if(null!=filialeIdList&&0!=filialeIdList.size()){
				list=userDao.findByUserNameLikeOrAccountLikeOrEmainLikeAndStatusAndDel("%"+search.trim()+"%", status,DELTYPE.NORMAL, pageRequest,filialeIdList);
			}else{
				list=userDao.findByUserNameLikeOrAccountLikeOrEmainLikeAndStatusAndDel("%"+search.trim()+"%", status,DELTYPE.NORMAL, pageRequest);
			}
			return list;
		}
	}

	public void updateUserStatus(Long id, USER_STATUS normal) throws ClientProtocolException, IOException, ParseException {
		User user = this.userDao.findOne(id);
		//调用mas接口，修改用户redis记录
		if(normal.equals(USER_STATUS.NORMAL)){
			String enableString = HttpUtil.httpGet(fobiddenUrl+"?type=enable&username="+user.getAccount());
			JSONObject enableJSON = JSONObject.fromObject(enableString);
			if(enableJSON.get("status").equals("success")){
				log.info("调用mas接口成功，启用成功");
			}else{
				log.info("调用mas接口失败！");
			}
			
		}
		//调用mas接口，修改用户redis记录为禁用
		if(normal.equals(USER_STATUS.FORBIDDEN)){
			String forbiddenString = HttpUtil.httpGet(fobiddenUrl+"?type=forbidden&username="+user.getAccount());
			JSONObject forbiddenJSON = JSONObject.fromObject(forbiddenString);
			if(forbiddenJSON.get("status").equals("success")){
				log.info("调用mas接口成功，禁用成功");
			}else{
				log.info("调用mas接口失败！");
			}
		}
		user.setStatus(normal);
		this.userDao.save(user);
	}

	public void deleteUserStatus(Long id) {
		this.userDao.delete(id);
	}
	
	/**
	 * 删除用户
	 * @user jingjian.wu
	 * @date 2015年10月8日 下午8:25:35
	 */
	public int deleteUsers(List<Long> userIds){
		int count = 0;//标识删除了几个
		String deleteUserId="";
		for(Long id:userIds){
			String sql = "select sum(a) from ( "+
"select count(1)  a from T_PROJECT_MEMBER WHERE del=0 and  userId =  " +id+
" union  ALL" + 
" select count(1) a  from T_TEAM_MEMBER WHERE del=0 and  userId = "+id+ 
" union  ALL" + 
" select count(1) a from T_TASK_MEMBER WHERE del=0 and  userId =  " +id+
" ) t ";
			log.info("deleteUsers query "+sql);	
			int result = jdbcTpl.queryForInt(sql);
			if(result>0){
				continue;
			}
			userDao.delete(id);//此用户在协同中没有与之相关联的项目，团队，任务等等
			count++;
			deleteUserId=deleteUserId+String.valueOf(id)+",";
		}
//		if(serviceFlag.equals("enterpriseEmm3")&&StringUtils.isNotBlank(deleteUserId)){
//			deleteUserId=deleteUserId.substring(0,deleteUserId.length()-1);	//去掉最后一个逗号
//			String flag=deleteEmm3Users(deleteUserId);
//			if(!flag.equals("ok")){
//				throw new RuntimeException("删除用户失败");
//			}
//		}
		return count;
	}
	
	/**
	 * 根据邮箱或者账号判断用户是否存在
	 * @user jingjian.wu
	 * @date 2015年10月13日 上午11:56:32
	 */
	public boolean existByProperty(String property,String value){
		if("account".equals(property)){
			List<User> list = userDao.findByAccount(value);
			if(null!=list && list.size()>0){
				return true;
			}
		}else if("email".equals(property)){
			List<User> list = userDao.findByEmail(value);
			if(null!=list && list.size()>0){
				return true;
			}
		}
		return false;
	}
	
	public String getValueFromJSONObject(String key,JSONObject json){
		if(json.containsKey(key)){
			return json.getString(key);
		}
		return "";
	}
	/**
	 * 删除EMM管理员
	 * @user haijun.cheng
	 * @date 2016年06月22日 下午8:25:35
	 */
	public String deleteEmm3Users(String userIds){
		List<Map<String, Object>> email=new ArrayList<Map<String,Object>>();
		String sql="select email from T_USER where id in ("+userIds.toString().replace("[", "").replace("]", "")+")";
		email=this.jdbcTpl.queryForList(sql);
		for(int i=0;i<email.size();i++){
			Map<String,String> parameters=new HashMap<String,String>();
			parameters.put("email", email.get(i).get("email").toString());
			String resultStr="";
			if(StringUtils.isNotBlank(emm3Url)){
				resultStr=HttpUtil.httpsPost(emm3Url+"/mum/personnel/deletePersonnelXieTong", parameters,"UTF-8");
			}else if(StringUtils.isNotBlank(emm3TestUrl)){
				resultStr=HttpUtil.httpsPost(emm3TestUrl+"/mum/personnel/deletePersonnelXieTong", parameters,"UTF-8");
			}
			JSONObject obj=JSONObject.fromObject(resultStr);
			if(!obj.get("status").equals("ok")&&!obj.get("info").equals("管理员不存在")){
				return obj.get("info").toString();
			}
		}
		return "ok";
	}

	public void addUserToSSO(User user) throws ClientProtocolException, IOException {
		// TODO Auto-generated method stub
		String randomCode =MD5Util.generateCode();
    	String md5Pass = MD5Util.MD5(randomCode).toLowerCase();
		StringBuffer sb = new StringBuffer();
		sb.append("loginName=");
		sb.append(user.getEmail());
		sb.append("&tenantId=");
		sb.append(tenantId);
		sb.append("&tenantName=");
		sb.append(ssoCreator);
		sb.append("&nickName=");
		sb.append(user.getNickName());
		sb.append("&captcha=");
		sb.append("");
		sb.append("&loginPassword=");
		sb.append(randomCode);
		sb.append("&regSource=");
		sb.append(Cache.getSetting("SETTING").getWebAddr());
		sb.append("&regRefer=''");
		sb.append("&regFrom=xt");
		sb.append("&isAdmin=0&isUse=1&creator=");
		sb.append(ssoCreator);
		sb.append("&userEmail=");
		sb.append(user.getEmail());
		log.info("password====================================================================>"+randomCode);
		log.info("send post ===========> "+sb.toString());
		String postResult = HttpTools.sendPost(ssoHost+"signup", sb.toString());
		log.info(postResult);
		if(!StringUtils.isBlank(postResult)){
			postResult = postResult.replace("(", "");
			postResult = postResult.replace(")", "");
		}
		JSONObject sinupJson = JSONObject.fromObject(postResult);
		//SSO注册用户失败
		if ("fail".equals(sinupJson.getString("retCode"))) {
			log.info("reg sso return ==> "+sinupJson.getString("retMsg"));
			if(!sinupJson.getString("extraInfo").contains("userExist")){
				throw new RuntimeException(sinupJson.getString("retMsg"));
			}else{//用户已存在SSO中
				log.info("user already in sso -->"+user.getEmail());
				String params = "propName=name&propValue="+user.getEmail();
				log.info("params=====>"+params);
				List<NameValuePair> parameters = new ArrayList<>();
				parameters.add( new BasicNameValuePair("propName", "name") );
				parameters.add( new BasicNameValuePair("propValue", user.getEmail() ) );
				String getResult = HttpUtil.httpPost(ssoHost+"userCheck", parameters);
//				String getResult = HttpTools.sendGet(ssoHost+"userCheck",params);
				log.info("==================判断邮箱还是昵称已存在"+getResult);
        		if(!StringUtils.isBlank(getResult)){
        			getResult = getResult.replace("(", "");
        			getResult = getResult.replace(")", "");
    			}
    			JSONObject existStr = JSONObject.fromObject(getResult);
    			if ("fail".equals(existStr.getString("retCode"))) {//邮箱已存在
    				throw new RuntimeException("用户"+user.getEmail()+"的邮箱已存在,请修改昵称之后再添加");
    			}else{
    				throw new RuntimeException("用户"+user.getEmail()+"的昵称已存在,请修改昵称之后再添加");
    			}
			}
		}
	}
}
