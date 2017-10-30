package org.zywx.cooldev.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.CRUD_TYPE;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DOC_CHAPTER_TYPE;
import org.zywx.cooldev.commons.Enums.DOC_MEMBER_TYPE;
import org.zywx.cooldev.commons.Enums.DOC_PUB_TYPE;
import org.zywx.cooldev.commons.Enums.ENTITY_TYPE;
import org.zywx.cooldev.commons.Enums.PROJECT_BIZ_LICENSE;
import org.zywx.cooldev.commons.Enums.ROLE_TYPE;
import org.zywx.cooldev.entity.auth.Permission;
import org.zywx.cooldev.entity.document.Document;
import org.zywx.cooldev.entity.document.DocumentChapter;
import org.zywx.cooldev.entity.project.ProjectAuth;
import org.zywx.cooldev.entity.project.ProjectMember;
import org.zywx.cooldev.entity.query.DOCQuery;
import org.zywx.cooldev.system.Cache;
import org.zywx.cooldev.util.ChineseToEnglish;
import org.zywx.cooldev.util.ZipUtil;

import com.petebevin.markdown.MarkdownProcessor;

@Service
public class DocumentService extends AuthService {
	
	@Autowired
	protected DocumentChapterService documentChapterService;

	private static final String DELETE_DOCUMENT_CHAPTER = "UPDATE T_DOCUMENT_CHAPTER TDC SET del = ? WHERE TDC.documentId = ? AND TDC.del = ?";
	
	private static final String EXPORT_DOCUMENT = "SELECT TD.id docId,TD.name docName,TDC.id docCId,TDC.parentId,TDC.name,TDC.type,TDC.sort,TDC.contentMD "
			+ " FROM T_DOCUMENT_CHAPTER TDC LEFT JOIN T_DOCUMENT TD on TDC.documentId = TD.id  "
			+ " WHERE TDC.documentId = ? AND TDC.del = ? ORDER BY TDC.parentId,TDC.sort DESC ";

	@Value("${document.upload}")
	private String upload;

	@Value("${file}")
	private String rootpath;
	@Value("${host}")
	private String host;
	@Value("${document.nginx.url}")
	private String nginxUrl;

	/**
	 * 
	 * @describe 创建document <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月12日 下午8:10:09 <br>
	 * @param doc
	 *
	 */
	public Document addDoc(Document doc) {
		//增加拼音字段
		doc.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(doc.getName()==null?"":doc.getName()));
		doc.setPinYinName(ChineseToEnglish.getPingYin(doc.getName()==null?"":doc.getName()));
		doc = this.documentDao.save(doc);
		doc.setPubUrl(nginxUrl+doc.getId());
		doc = this.documentDao.save(doc);
		
//		//文档创建者添加权限
//		List<ProjectMember> pm = this.projectMemberDao.findByProjectIdAndUserIdAndDel(doc.getProjectId(), doc.getUserId(), DELTYPE.NORMAL);
//		ProjectAuth pa = new ProjectAuth();
//		pa.setMemberId(pm.get(0).getId());
//		pa.setRoleId(Cache.getRole(ENTITY_TYPE.DOCUMENT+"_"+ROLE_TYPE.CREATOR).getId());
//		pa = this.projectAuthDao.save(pa);
//		//文档参与者添加权限
//		List<ProjectMember> pms = this.projectMemberDao.findByProjectIdAndDel(doc.getProjectId(),  DELTYPE.NORMAL);
//		for(ProjectMember pm1 : pms){
//			if(pm.get(0).getId().equals(pm1.getId())){
//				continue;
//			}
//			ProjectAuth pa1 = new ProjectAuth();
//			pa1.setMemberId(pm1.getId());
//			pa1.setRoleId(Cache.getRole(ENTITY_TYPE.DOCUMENT+"_"+ROLE_TYPE.MEMBER).getId());
//			pa1 = this.projectAuthDao.save(pa1);
//		}
		return doc;
	}

	/**
	 * 
	 * @describe 查询单个document实体 <br>
	 * @author jiexiong.liu <br>
	 * @param loginUserId
	 * @date 2015年8月13日 上午10:10:49 <br>
	 * @param id
	 * @return
	 *
	 */
	public Document getDoc(Long docId) {
		Document doc = this.documentDao.findByIdAndDel(docId, DELTYPE.NORMAL);
		if (null != doc && 0 != doc.getId()) {
			return doc;
		}
		return null;
	}

	/**
	 * 
	 * @describe 更新document <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月13日 上午10:12:09 <br>
	 * @param document
	 * @return
	 *
	 */
	public Document updateDoc(Document document) {
		//修改拼音字段
		document.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(document.getName()==null?"":document.getName()));
		document.setPinYinName(ChineseToEnglish.getPingYin(document.getName()==null?"":document.getName()));
		document = this.documentDao.save(document);
		return document;
	}

	/**
	 * 
	 * @describe 删除document <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月13日 上午10:23:05 <br>
	 * @param docId
	 * @param loginUserId
	 * @return
	 *
	 */
	public int deleteDoc(Long docId) {
		int a = 0;
		Document doc = this.documentDao.findByIdAndDel(docId, DELTYPE.NORMAL);
		if (null != doc && doc.getId() > 0) {
			doc.setDel(DELTYPE.DELETED);
			doc = this.documentDao.save(doc);
		}
		if (null != doc && doc.getDel().equals(DELTYPE.DELETED)) {
			a = 1;
			// 删除文档 章节
			this.jdbcTpl.update(DELETE_DOCUMENT_CHAPTER, DELTYPE.DELETED.ordinal(), doc.getId(), DELTYPE.NORMAL.ordinal());
		}
		return a;
	}

	/**
	 * 
	 * @describe 发布或者回收document <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月13日 下午2:06:00 <br>
	 * @param docId
	 * @param loginUserId
	 * @param opertion
	 * @return
	 *
	 */
	public int upgradePubOrRetDoc(Long docId, DOC_PUB_TYPE opertion) {
		Enums.DOC_PUB_TYPE opertionType = null;
		if (Enums.DOC_PUB_TYPE.PUBLISHED.compareTo(opertion)==0) {
			opertionType = Enums.DOC_PUB_TYPE.PUBLISHED;
		} else
			opertionType = Enums.DOC_PUB_TYPE.RETRIEVED;

		int a = 0;
		Document doc = this.documentDao.findByIdAndDel(docId, DELTYPE.NORMAL);
		if (null != doc && doc.getId() > 0) {
			doc.setPub(opertionType);
			doc.setPubUrl(nginxUrl+doc.getId());
			doc = this.documentDao.save(doc);
			
			String sql = "update T_DOCUMENT_CHAPTER set pub = "+opertionType.ordinal()+" where documentId = "+docId;
			this.jdbcTpl.update(sql);
			a=1;
		}
		return a;
	}

	
	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	public NamedParameterJdbcTemplate getNamedJdbcTemplate() {
		return namedJdbcTemplate;
	}

	@Autowired
	public void setNamedJdbcTemplate(JdbcTemplate namedJdbcTemplate) {
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(namedJdbcTemplate);
	}

	/**
	 * 
	 * @describe 获取document列表 <br>
	 * @author jiexiong.liu <br>
	 * @param loginUserId 
	 * @date 2015年8月13日 下午2:05:41 <br>
	 * @param loginUserId
	 * @param ipageSize
	 * @param ipageNo
	 * @param string
	 * @param projectId
	 * @param userId
	 * @return
	 *
	 */
	@SuppressWarnings("deprecation")
	public Map<String, Object> getDocList( Long loginUserId, List<Long> projectIds, List<DOC_MEMBER_TYPE> typeQ,DOCQuery query,Pageable pageable) {
		Map<String, Object> map = new HashMap<String, Object>();
		//分页
		int startNum = pageable.getPageNumber()*pageable.getPageSize();
		int number = pageable.getPageSize();
		// TODO 待优化文档列表查询
		List<Map<String, Object>> list = null;
		long count = 0;
		if(projectIds==null || projectIds.size()<1){
			return null;
		}
		String proIds = projectIds.toString().substring(1,projectIds.toString().length()-1);
	    String sql1 = " SELECT * FROM (SELECT TD.id id,TD.name docName,TD.describ describ,TD.createdAt,"
    			+ " TP.id projectId,TP.name projectName,TP.bizLicense,date_format(TD.updatedAt,'%Y-%m-%d %H:%i:%S') updatedAt, CASE WHEN TU.userName IS NOT NULL THEN "
    			+ " TU.userName ELSE TU.account END userName,TU.icon,TU.id userId FROM T_DOCUMENT TD "
				+ " LEFT JOIN T_PROJECT TP ON TD.projectId = TP.id LEFT JOIN T_USER TU ON TU.id = TD.userId "
    			+ " WHERE TD.projectId in ("+proIds+") AND TD.del = 0 ";
	    if(null!=query.getCreatedAtStart()){
			sql1 +=" AND DATE_FORMAT(TD.createdAt,'%Y-%m-%d') >= '"+query.getCreatedAtStart()+"' ";
		}
	    if(null!=query.getCreatedAtEnd()){
			sql1 +=" AND DATE_FORMAT(TD.createdAt,'%Y-%m-%d') <= '"+query.getCreatedAtEnd()+"' ";
		}
	    if(null!=query.getDocName() && !"%%".equals(query.getDocName()) && !"%null%".equals(query.getDocName() )){
	    	sql1 +=" and (TD.name like '"+query.getDocName()+"' or TD.pinYinHeadChar like '"+query.getPinYinName()+"' or TD.pinYinName like '"+query.getPinYinName()+"')";
	    }
		
	    String sql =" SELECT count(1) count FROM (SELECT DISTINCT TD.id FROM T_DOCUMENT TD "
				+ " LEFT JOIN T_PROJECT TP ON TD.projectId = TP.id LEFT JOIN T_USER TU ON TD.userId = TU.id "
				+ " WHERE TD.projectId in ("+proIds+") AND TD.del = 0 ";
		if(null!=query.getCreatedAtStart()){
			sql +=" AND DATE_FORMAT(TD.createdAt,'%Y-%m-%d') >= '"+query.getCreatedAtStart()+"' ";
		}
		if(null!=query.getCreatedAtEnd()){
			sql +=" AND DATE_FORMAT(TD.createdAt,'%Y-%m-%d') <= '"+query.getCreatedAtEnd()+"' ";
		}	
	    if(null!=query.getDocName() && !"%%".equals(query.getDocName()) && !"%null%".equals(query.getDocName() )){
	    	sql +=" and (TD.name like '"+query.getDocName()+"' or TD.pinYinHeadChar like '"+query.getPinYinName()+"' or TD.pinYinName like '"+query.getPinYinName()+"')";
	    }
	   /* if(typeQ.contains(DOC_MEMBER_TYPE.SPONSOR) && !typeQ.contains(DOC_MEMBER_TYPE.ACTOR)){
	    	sql1 += " AND TD.userId = ? and TD.projectId in (select pm.projectId from T_PROJECT_MEMBER pm LEFT JOIN T_USER u on u.id=pm.userId where u.userName like ? and (pm.type=1 or pm.type=0) and pm.projectId in ("+proIds+") AND pm.userId="+loginUserId+" AND pm.del = 0 ) "
	    			+ " and TD.projectId in (select pm.projectId from T_PROJECT_MEMBER pm LEFT JOIN T_USER u on u.id=pm.userId where u.userName like ? and pm.type=0 and pm.projectId in ("+proIds+") AND pm.del = 0 ) "
	    			+ "  ) TTT ORDER BY TTT.updatedAt DESC LIMIT " + startNum +","+ number;
	    	sql += " AND TD.userId = ? and TD.projectId in (select pm.projectId from T_PROJECT_MEMBER pm LEFT JOIN T_USER u on u.id=pm.userId where u.userName like ? and (pm.type=1 or pm.type=0)  and pm.projectId in ("+proIds+") AND pm.userId="+loginUserId+" AND pm.del = 0 ) "
	    			+ " and TD.projectId in (select pm.projectId from T_PROJECT_MEMBER pm LEFT JOIN T_USER u on u.id=pm.userId where u.userName like ? and pm.type=0 and pm.projectId in ("+proIds+") AND pm.del = 0 ) "
	    			+ "  ) TTT ";	    	
	    	log.info("JDBCTemple：" + sql1);
			list = this.jdbcTpl.queryForList(sql1, new Object[]{DELTYPE.NORMAL.ordinal(),loginUserId,query.getCreator(),query.getActor()});
			log.info("JDBCTemple：" + sql);
			count = this.jdbcTpl.queryForLong(sql, new Object[]{DELTYPE.NORMAL.ordinal(),loginUserId,query.getCreator(),query.getActor()});
	    }else if(typeQ.contains(DOC_MEMBER_TYPE.ACTOR) && !typeQ.contains(DOC_MEMBER_TYPE.SPONSOR)){
	    	sql1 += " AND TD.userId != ? and TD.projectId in (select pm.projectId from T_PROJECT_MEMBER pm LEFT JOIN T_USER u on u.id=pm.userId where u.userName like ? and (pm.type=1 or ) and pm.projectId in ("+proIds+") AND pm.userId="+loginUserId+" AND pm.del = 0 ) "
	    			+ " and TD.projectId in (select pm.projectId from T_PROJECT_MEMBER pm LEFT JOIN T_USER u on u.id=pm.userId where u.userName like ? and pm.type=0 and pm.projectId in ("+proIds+") AND pm.del = 0 ) "
	    			+ "  ) TTT ORDER BY TTT.updatedAt DESC LIMIT " + startNum +","+ number;
	    	sql += " AND TD.userId != ? and TD.projectId in (select pm.projectId from T_PROJECT_MEMBER pm LEFT JOIN T_USER u on u.id=pm.userId where u.userName like ? and (pm.type=1 or pm.type=0) and pm.projectId in ("+proIds+") AND pm.userId="+loginUserId+" AND pm.del = 0 ) "
	    			+ " and TD.projectId in (select pm.projectId from T_PROJECT_MEMBER pm LEFT JOIN T_USER u on u.id=pm.userId where u.userName like ? and pm.type=0 and pm.projectId in ("+proIds+") AND pm.del = 0 ) "
	    			+ "  ) TTT ";	
	    	log.info("JDBCTemple：" + sql1);
			list = this.jdbcTpl.queryForList(sql1, new Object[]{DELTYPE.NORMAL.ordinal(),loginUserId,query.getCreator(),query.getActor()});
			log.info("JDBCTemple：" + sql);
			count = this.jdbcTpl.queryForLong(sql, new Object[]{DELTYPE.NORMAL.ordinal(),loginUserId,query.getCreator(),query.getActor()});
	    }else{
	    	sql1 += " and TD.projectId in (select pm.projectId from T_PROJECT_MEMBER pm LEFT JOIN T_USER u on u.id=pm.userId where u.userName like ? and pm.type=0 and pm.projectId in ("+proIds+") AND pm.del = 0 ) "
	    			+ "and TD.projectId in (select pm.projectId from T_PROJECT_MEMBER pm LEFT JOIN T_USER u on u.id=pm.userId where u.userName like ? and pm.type=1 and pm.projectId in ("+proIds+") AND pm.del = 0 ) "
	    			+ " ) TTT ORDER BY TTT.updatedAt DESC LIMIT " + startNum +","+ number;
	    	sql += " and TD.projectId in (select pm.projectId from T_PROJECT_MEMBER pm LEFT JOIN T_USER u on u.id=pm.userId where u.userName like ? and pm.type=0 and pm.projectId in ("+proIds+") AND pm.del = 0 ) "
	    			+ " and TD.projectId in (select pm.projectId from T_PROJECT_MEMBER pm LEFT JOIN T_USER u on u.id=pm.userId where u.userName like ? and pm.type=1 and pm.projectId in ("+proIds+") AND pm.del = 0 ) "
	    			+ " ) TTT ";	 
	    	log.info("JDBCTemple：" + sql1);
			list = this.jdbcTpl.queryForList(sql1, new Object[]{DELTYPE.NORMAL.ordinal(),query.getCreator(),query.getActor()});
			log.info("JDBCTemple：" + sql);
			count = this.jdbcTpl.queryForLong(sql, new Object[]{DELTYPE.NORMAL.ordinal(),query.getCreator(),query.getActor()});
	    }*/
		 if(StringUtils.isNotBlank(query.getCreator())  && !"%%".equals(query.getCreator())){
			 sql1+=" and TU.userName like '"+query.getCreator()+"'";
			 sql+=" and TU.userName like '"+query.getCreator()+"'";
		 }
	    if(StringUtils.isNotBlank(query.getActor())  && !"%%".equals(query.getActor())){
//	    	sql1 += " and TU.userName not like '"+query.getActor()+"'";
//	    	sql += " and TU.userName not like '"+query.getActor()+"'";
	    	StringBuilder sqlActor = new StringBuilder("select id from T_PROJECT where del=0 and  id in (")
	    			.append("select projectId from T_PROJECT_MEMBER where del=0 and type in (0,1) and userId in (")
	    			.append("select id from T_USER where del=0 and userName like '").append(query.getActor()).append("'")
	    			.append("))");
	    	sql1+=" and TD.projectId in("+sqlActor.toString()+") and TU.userName !='"+query.getActor().replace("%", "")+"' ";
	    	sql+=" and TD.projectId in("+sqlActor.toString()+") and TU.userName !='"+query.getActor().replace("%", "")+"' ";
	    	
	    }
	    sql1 +=") TTT  order by TTT.createdAt desc LIMIT " + startNum +","+ number;
	    sql+=") TTT";
	    log.info("JDBCTemple：" + sql1);
		list = this.jdbcTpl.queryForList(sql1);
		log.info("JDBCTemple：" + sql);
		count = this.jdbcTpl.queryForLong(sql);
		if(null!=list&&list.size()>0){
			for(Map<String, Object> obj : list){
				int a = Integer.parseInt(null!=obj.get("bizLicense")?obj.get("bizLicense").toString():"1");
				PROJECT_BIZ_LICENSE[] bizLicense = PROJECT_BIZ_LICENSE.values();
				obj.put("bizLicense", bizLicense[a]);
			}
		}
		map.put("count", count);
		map.put("list", list);
		return map;
	}

	/**
	 * 
	 * @describe 导出markdown文档压缩包 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月18日 上午11:46:37 <br>
	 * @param docId
	 * @param docId2
	 * @return <br>
	 * @throws IOException
	 * @returnType Object
	 *
	 */
	public String exportDocument(Long loginUserId, Long docId) throws IOException {
		
		Long startsTime = Calendar.getInstance().getTimeInMillis();
		log.info("============output document starts============");
		// InputStream inputStream =
		// DocumentService.class.getResourceAsStream("/FileConfig.properties");
		// Properties p = new Properties();
		// p.load(inputStream);
		// inputStream.close();

		log.info("file path:" + rootpath + "\nhost:" + host );
		String docName = null;
		log.info("JDBCTEMPLE :" + EXPORT_DOCUMENT);
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(EXPORT_DOCUMENT, docId, DELTYPE.NORMAL.ordinal());

		if (list.size() > 0) {
			docName = list.get(0).get("docName").toString();
			for (int a = 0; a < list.size(); a++) {
				Map<String, Object> map = list.get(a);
				for (int b = 0; b < list.size(); b++) {
					Map<String, Object> map1 = list.get(b);
					// type=1 类型是markdown文件
					if ("1".equals(map1.get("type").toString())) {
						// 如果map是map1的父元素
						if (map.get("docCId").equals(map1.get("parentId"))) {
							// 将map的file路径添加到map1的file路径中
							map1.put("file", null != map.get("file") ? map.get("file") + "/"
									: rootpath + "/" + loginUserId + "/" + docId + "/" + map1.get("docName") + "/");

						} else if ("-1".equals(map1.get("parentId").toString())) {
							// 如果map1的parentId=-1 则该文件没有父节点了 将根路径添加到map1的file路径中
							map1.put("file",
									rootpath + "/" + loginUserId + "/" + docId + "/" + map1.get("docName") + "/");
						} else
							continue;// 否则忽略下述步骤 继续迭代

						// 创建map1的markdown文件
						File file1 = new File(map1.get("file").toString(), new String(map1.get("name").toString().getBytes(),"utf-8") + ".md");
						// 创建父目录
						File file2 = new File(map1.get("file").toString());
						if (!file2.exists()) {
							file2.mkdirs();
						}
						
						if (!file1.exists()) {
							file1.createNewFile();
							
						}
						// 获取markdown文件的输出流 保存内容到文件中
						FileOutputStream out = new FileOutputStream(file1.getPath());
						String contentMD = null != map1.get("contentMD") ? map1.get("contentMD").toString() : "";
						byte[] bb = contentMD.getBytes();
						out.write(bb);
						out.flush();
						out.close();

					} else {
						// type =0 类型是markdown目录
						// 如果map是map1的父元素
						if (map.get("docCId").equals(map1.get("parentId"))) {
							// 将map的file路径添加到map1的file路径中
							map1.put("file",
									null != map.get("file") ? map.get("file") + "/" + map1.get("name")
											: rootpath + "/" + loginUserId + "/" + docId + "/" + map1.get("docName")
													+ "/" + map1.get("name"));

						} else if ("-1".equals(map1.get("parentId").toString())) {
							// 如果map1的parentId=-1 则该文件没有父节点了 将根路径添加到map1的file路径中
							map1.put("file", rootpath + "/" + loginUserId + "/" + docId + "/" + map1.get("docName")
									+ "/" + map1.get("name"));
						} else
							continue;// 否则忽略下述步骤 继续迭代

						// 创建map1的markdown目录
						File file3 = new File(map1.get("file").toString());
						if (!file3.exists()) {
							file3.mkdirs();
						}
					}
				}
			}

		} else {
			String sql1 = "SELECT TD.id docId,TD.name docName FROM T_DOCUMENT TD  WHERE " + " TD.del = "
					+ Enums.DELTYPE.NORMAL.ordinal() + " AND TD.id=" + docId;
			log.info(" JDBCTEMPLE :" + sql1);
			list = this.jdbcTpl.queryForList(sql1);
			if (list.size() < 1) {
				return "the document does not exist";
			}
			docName = list.get(0).get("docName").toString();
			File file = new File(rootpath + "/" + loginUserId + "/" + docId + "/" + docName);
			if (!file.exists()) {
				file.mkdirs();
			}
		}

		// 将生成的目录和文件添加到压缩包
		ZipUtil.documentZip(rootpath + "/" + loginUserId + "/" + docId + "/" + docName,
				rootpath + "/" + loginUserId + "/" + docId + "/" + docName + ".zip");
		// 压缩完成之后 将生成的目录和文件删除
		org.zywx.cooldev.util.FileUtil
				.deleteDir(new File(rootpath + "/" + loginUserId + "/" + docId + "/" + docName + "/"));

		Long endsTime = Calendar.getInstance().getTimeInMillis();
		log.info("============output document total time : " + (endsTime - startsTime) + "ms============");

		return host  + "/" + +loginUserId + "/" + docId + "/" + docName + ".zip";
	}

	/**
	 * 
	 * @describe 获取对document相应的permissions <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月25日 下午3:30:13 <br>
	 * @param loginUserId
	 * @param documentId
	 * @return
	 *
	 */
	@Override
	public List<Permission> getPermissionList(long loginUserId, long documentId) {
//		List<Permission> retList = new ArrayList<>();
//		List<DocumentMember> memberList = this.documentMemberDao.findByDocumentIdAndUserIdAndDel(documentId,
//				loginUserId, DELTYPE.NORMAL);
//		if (memberList == null || memberList.size() == 0) {
//			return retList;
//		}
//		DocumentMember member = memberList.get(0);
//		List<DocumentAuth> authList = this.documentAuthDao.findByMemberIdAndDel(member.getId(), DELTYPE.NORMAL);
//		if (authList == null || authList.size() == 0) {
//			return retList;
//		}
//		HashMap<Long, Permission> permissionMap = new HashMap<>();
//		for (DocumentAuth auth : authList) {
//			long roleId = auth.getRoleId();
//			Role role = Cache.getRole(roleId);
//			List<Permission> permissions = role.getPermissions();
//			for (Permission p : permissions) {
//				// 去除重复的权限
//				permissionMap.put(p.getId(), p);
//			}
//		}
//
//		List<Permission> permissionList = new ArrayList<>();
//		Iterator<Long> it = permissionMap.keySet().iterator();
//		while (it.hasNext()) {
//			long permissionId = it.next();
//			Permission p = permissionMap.get(permissionId);
//			permissionList.add(p);
//		}
//
//		return permissionList;
		return null;
	}

	/**
	 * 
	 * @describe 导入markdown文档章节 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月26日 上午9:41:32 <br>
	 * @param loginUserId
	 * @param projectId
	 * @param docId 
	 * @param parentId 
	 * @param path
	 * @return <br>
	 * @throws IOException
	 * @returnType Document
	 *
	 */
	public DocumentChapter importDocumentChapter(Long loginUserId, Long projectId, String filename, Long docId, Long parentId) throws IOException {
		String name = filename.substring(0,filename.indexOf("."));
		DocumentChapter documentChapter =this.documentChapterService.ExistDocC(name,docId,parentId);
		
		Long startsTime = Calendar.getInstance().getTimeInMillis();
		log.info("============import document starts============");
		String filepath = upload + "/" + loginUserId + "/";
		String filepathroot = upload + "/" + loginUserId + "/" + docId+"/"+filename;

		File file = new File(filepathroot);
		DocumentChapter docC = loadDOCChapter(loginUserId,docId,file,parentId,documentChapter);
		// 导入完成之后 将文件删除
		org.zywx.cooldev.util.FileUtil.deleteDir(new File(filepath));

		Long endsTime = Calendar.getInstance().getTimeInMillis();
		log.info("============import document total time : " + (endsTime - startsTime) + "ms============");
		return docC;
	}

	/**
	 * 
	 * @describe 导入文档章节	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年9月6日 下午2:25:47	<br>
	 * @param loginUserId
	 * @param docId
	 * @param file
	 * @param parentId 
	 * @param documentChapter 
	 * @return
	 * @throws IOException  <br>
	 * @returnType DocumentChapter
	 *
	 */
	private DocumentChapter loadDOCChapter(Long loginUserId, Long docId, File file, Long parentId, DocumentChapter documentChapter) throws IOException {
		DocumentChapter docC = new DocumentChapter();
		if(null!=documentChapter){
			docC=documentChapter;
		}else{
			docC.setName(file.getName().substring(0, file.getName().lastIndexOf(".")));
			docC.setDocumentId(docId);
			docC.setUserId(loginUserId);
			docC.setType(DOC_CHAPTER_TYPE.PART);
			docC.setParentId(parentId);
			String sql = "SELECT CASE WHEN MAX(tdc.sort)  IS NULL  THEN -1 ELSE MAX(tdc.sort) END count FROM T_DOCUMENT_CHAPTER tdc WHERE tdc.documentId = ? AND tdc.parentId=? AND tdc.del = ? ";
			@SuppressWarnings("deprecation")
			int a = this.jdbcTpl.queryForInt(sql, new Object[]{docC.getDocumentId(),docC.getParentId(),DELTYPE.NORMAL.ordinal()});
			docC.setSort(a+1);
		}
		
		FileInputStream in;
		byte[] b;
		String contentMDAll = null;
		String contentHTMLALL = "";

		in = new FileInputStream(file.getPath());
		int len = 1000;
		b = new byte[in.available()]; // 新建一个字节数组
		in.read(b); // 将文件中的内容读取到字节数组中
		in.close();
		contentMDAll = new String(b); // 再将字节数组中的内容转化成字符串形式输出
		//循环转换成html 防止堆栈溢出
		for(int i = 1;i<=(contentMDAll.length()%len==0?contentMDAll.length()/len:contentMDAll.length()/len+1);i++){
			String contentMD = "";
			String contentHTML = "";
			contentMD = contentMDAll.substring((i-1)*len, i*len>contentMDAll.length()?contentMDAll.length():i*len);
			MarkdownProcessor processor = new MarkdownProcessor();
			contentHTML = String
					.format("<div style=\"padding: 20px;\" previewcontainer=\"true\" class=\"markdown-body editormd-preview-container\">%s</div>",
							processor.markdown(contentMD))
					.replaceAll("src=\"", "src=\"file:");
			contentHTMLALL += contentHTML;
		}
		
		
		docC.setContentMD(contentMDAll);
		docC.setContentHTML(contentHTMLALL);
		docC = this.documentChapterDao.save(docC);
		log.info(String.format("import document part newContent[%s]",docC ));
		return docC;
	}

	
	/**
	 * 
	 * @describe 导入markdown文档 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月26日 上午9:41:32 <br>
	 * @param loginUserId
	 * @param projectId
	 * @param docId 
	 * @param path
	 * @return <br>
	 * @throws IOException
	 * @returnType Document
	 *
	 */
	public Document importDocument(Long loginUserId, Long projectId, String filename) throws IOException {
		Long startsTime = Calendar.getInstance().getTimeInMillis();
		log.info("============import document starts============");
		String filepath = upload + "/" + loginUserId + "/";
		String filepathroot = upload + "/" + loginUserId + "/" + filename.substring(0, filename.lastIndexOf("."));
		String fileunzip = upload + "/" + loginUserId + "/" + filename;

		// 解压上传的压缩包
		ZipUtil.unzip(fileunzip, filepath);
		File file = new File(filepathroot);
		// 导入文档
		Document doc = loadDocumentFile(loginUserId, projectId, -1L, -1L, file, true, true);
		// 导入完成之后 将压缩包和解压文件删除
		org.zywx.cooldev.util.FileUtil.deleteDir(new File(filepath));

		Long endsTime = Calendar.getInstance().getTimeInMillis();
		log.info("============import document total time : " + (endsTime - startsTime) + "ms============");
		return doc;
	}
	/**
	 * 
	 * @describe <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月26日 上午10:35:55 <br>
	 * @param projectId
	 *            项目主键 <br>
	 * @param parentId
	 *            父节点主键 <br>
	 * @param documentId
	 *            文档主键 <br>
	 * @param file
	 *            文件名称 <br>
	 * @param isRoot
	 *            是否根目录 <br>
	 * @param isDirectory
	 *            是否目录 <br>
	 * @return <br>
	 * @throws IOException
	 * @returnType Document
	 *
	 */
	private Document loadDocumentFile(Long loginUserId, Long projectId, Long parentId, Long documentId, File file,
			boolean isRoot, boolean isDirectory) throws IOException {
		if (isRoot) {

			String name = file.getName();
			Document doc = new Document();
			doc.setName(name);
			doc.setDescrib(name);
			doc.setProjectId(projectId);
			doc.setUserId(loginUserId);
			doc = this.addDoc(doc);

			documentId = doc.getId();
			isRoot = false;
			isDirectory = true;
			loadDocumentFile(loginUserId, projectId, parentId, documentId, file, isRoot, isDirectory);
			return doc;
		} else {
			File[] files = file.listFiles();
			for (File file1 : files) {
				isDirectory = file1.isDirectory();
				if (isDirectory) {
					DocumentChapter docC = new DocumentChapter();
					docC.setName(file1.getName());
					docC.setDocumentId(documentId);
					docC.setParentId(parentId);
					docC.setUserId(loginUserId);
					docC.setType(DOC_CHAPTER_TYPE.CHAPTER);
					List<DocumentChapter> list = this.documentChapterDao.findByDocumentIdAndParentIdAndDel(
							docC.getDocumentId(), docC.getParentId(), DELTYPE.NORMAL);
					docC.setSort(list.size());
					docC = this.documentChapterDao.save(docC);

					parentId = docC.getId();
					loadDocumentFile(loginUserId, projectId, parentId, documentId, file1, isRoot, isDirectory);
				} else {
					DocumentChapter docC = new DocumentChapter();
					docC.setName(file1.getName());
					docC.setDocumentId(documentId);
					docC.setParentId(parentId);
					docC.setUserId(loginUserId);
					docC.setType(DOC_CHAPTER_TYPE.PART);

					FileInputStream in;
					byte[] b;
					String contentMD = null;

					in = new FileInputStream(file1.getPath());
					b = new byte[in.available()]; // 新建一个字节数组
					in.read(b); // 将文件中的内容读取到字节数组中
					in.close();
					contentMD = new String(b); // 再将字节数组中的内容转化成字符串形式输出

					docC.setContentMD(contentMD);

					MarkdownProcessor processor = new MarkdownProcessor();
					String contentHTML = String
							.format("<div style=\"padding: 20px;\" previewcontainer=\"true\" class=\"markdown-body editormd-preview-container\">%s</div>",
									processor.markdown(contentMD))
							.replaceAll("src=\"", "src=\"file:");
					docC.setContentHTML(contentHTML);
					String sql = "SELECT CASE WHEN MAX(tdc.sort)  IS NULL  THEN -1 ELSE MAX(tdc.sort) END count FROM T_DOCUMENT_CHAPTER tdc WHERE tdc.documentId = ? AND tdc.parentId=? AND tdc.del = ? ";
					@SuppressWarnings("deprecation")
					int a = this.jdbcTpl.queryForInt(sql, new Object[]{docC.getDocumentId(),docC.getParentId(),DELTYPE.NORMAL.ordinal()});
					docC.setSort(a+1);
					this.documentChapterDao.save(docC);
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @describe 获取单个document	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年8月28日 下午4:11:52	<br>
	 * @param docId
	 * @return  <br>
	 * @returnType Document
	 *
	 */
	public Document findDocument(Long docId) {
		Document doc = this.documentDao.findOne(docId);
		return doc;
	}

	public String getDocPath(DocumentChapter docC) {
		String path = docC.getName();
		Long parentId = docC.getParentId();
		if(parentId.intValue() == -1){
			return path;
		}
		List<DocumentChapter> docS = this.documentChapterDao.findByDocumentIdAndDel(docC.getDocumentId(), DELTYPE.NORMAL);
		log.info("get docPath:"+path);
		while(parentId.intValue()!=-1){
			boolean flag  = false;
			log.info("get docPath,parentId:"+parentId+",docS:"+docS);
			for(DocumentChapter docc : docS){
				log.info("get docChapter:"+docc);
				if(parentId.intValue() == docc.getId().intValue()){
					flag = true;
					parentId = docc.getParentId();
					path = docc.getName()+">"+path;
					break;
				}
			}
			if(docS.size()<1 || !flag){
				break;
			}
		}
		return path;
	}

	public int addPinYin() {
		List<Document> dList=this.documentDao.findByDel(DELTYPE.NORMAL);
		for(Document d:dList){
			d.setPinYinHeadChar(ChineseToEnglish.getPinYinHeadChar(d.getName()==null?"":d.getName()));
			d.setPinYinName(ChineseToEnglish.getPingYin(d.getName()==null?"":d.getName()));
		}
		documentDao.save(dList);
		return dList.size();
	}
}
