package org.zywx.cooldev.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.zywx.cooldev.commons.Enums;
import org.zywx.cooldev.commons.Enums.DELTYPE;
import org.zywx.cooldev.commons.Enums.DOC_CHAPTER_TYPE;
import org.zywx.cooldev.commons.Enums.DOC_PUB_TYPE;
import org.zywx.cooldev.entity.document.Document;
import org.zywx.cooldev.entity.document.DocumentChapter;

@Service
public class DocumentChapterService extends BaseService {

	private static final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
	private static final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
	private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
	private static final String regEx_space = "\\s*|\t|\r|\n";// 定义空格回车换行符

	/**
	 * 
	 * @describe 创建文档 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午2:30:47 <br>
	 * @param docC
	 * @return
	 *
	 */
	public DocumentChapter addDocC(DocumentChapter docC) {
		String sql = "SELECT CASE WHEN MAX(tdc.sort)  IS NULL  THEN -1 ELSE MAX(tdc.sort) END count FROM T_DOCUMENT_CHAPTER tdc WHERE tdc.documentId = ? AND tdc.parentId=? AND tdc.del = ? ";
		@SuppressWarnings("deprecation")
		int a = this.jdbcTpl.queryForInt(sql,
				new Object[] { docC.getDocumentId(), docC.getParentId(), DELTYPE.NORMAL.ordinal() });
		docC.setSort(a + 1);
		docC = this.documentChapterDao.save(docC);
		if(docC.getParentId()!=-1){
			final Map<String,String> map = new HashMap<>();
			StringBuffer idPathSql=new StringBuffer("SELECT paths AS idPath FROM ( SELECT id,parentId, @le:= IF (parentId = -1 ,0, IF( LOCATE( CONCAT('|',parentId,':'),@pathlevel)  > 0 ,");
			idPathSql.append(" SUBSTRING_INDEX( SUBSTRING_INDEX(@pathlevel,CONCAT('|',parentId,':'),-1),'|',1) +1,@le+1) ) levels, @pathlevel:= CONCAT(@pathlevel,'|',id,':', @le ,'|') pathlevel");
			idPathSql.append(" , @pathnodes:= IF( parentId =-1,'',  CONCAT_WS(',',IF( LOCATE( CONCAT('|',parentId,':'),@pathall) > 0 , ");
			idPathSql.append(" SUBSTRING_INDEX( SUBSTRING_INDEX(@pathall,CONCAT('|',parentId,':'),-1),'|',1),@pathnodes ) ,parentId ) )paths,@pathall:=CONCAT(@pathall,'|',id,':', @pathnodes ,'|') pathall ");
			idPathSql.append(" FROM T_DOCUMENT_CHAPTER, (SELECT @le:=0,@pathlevel:='', @pathall:='',@pathnodes:='') vv ) src where id = ").append(docC.getId());
			log.info("==========>idPathSql:"+idPathSql);
			this.jdbcTpl.query(idPathSql.toString(), 
					new RowCallbackHandler() {
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					map.put("idPath", rs.getString("idPath"));
				}  
			});
			String idPath="-1"+map.get("idPath");
			docC.setIdPath(idPath);
		}else{
			docC.setIdPath("-1");
		}
		return docC;
	}

	/**
	 * 
	 * @describe 获取单个文档 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午2:30:59 <br>
	 * @param id
	 * @return
	 *
	 */
	public DocumentChapter findOne(Long id) {
		DocumentChapter docC = this.documentChapterDao.findByIdAndDel(id, DELTYPE.NORMAL);
		if(docC.getParentId()!=-1){
			final Map<String,String> map = new HashMap<>();
			StringBuffer idPathSql=new StringBuffer("SELECT paths AS idPath FROM ( SELECT id,parentId, @le:= IF (parentId = -1 ,0, IF( LOCATE( CONCAT('|',parentId,':'),@pathlevel)  > 0 ,");
			idPathSql.append(" SUBSTRING_INDEX( SUBSTRING_INDEX(@pathlevel,CONCAT('|',parentId,':'),-1),'|',1) +1,@le+1) ) levels, @pathlevel:= CONCAT(@pathlevel,'|',id,':', @le ,'|') pathlevel");
			idPathSql.append(" , @pathnodes:= IF( parentId =-1,'',  CONCAT_WS(',',IF( LOCATE( CONCAT('|',parentId,':'),@pathall) > 0 , ");
			idPathSql.append(" SUBSTRING_INDEX( SUBSTRING_INDEX(@pathall,CONCAT('|',parentId,':'),-1),'|',1),@pathnodes ) ,parentId ) )paths,@pathall:=CONCAT(@pathall,'|',id,':', @pathnodes ,'|') pathall ");
			idPathSql.append(" FROM T_DOCUMENT_CHAPTER, (SELECT @le:=0,@pathlevel:='', @pathall:='',@pathnodes:='') vv ) src where id = ").append(docC.getId());
			log.info("==========>idPathSql:"+idPathSql);
			this.jdbcTpl.query(idPathSql.toString(), 
					new RowCallbackHandler() {
				@Override
				public void processRow(ResultSet rs) throws SQLException {
					map.put("idPath", rs.getString("idPath"));
				}  
			});
			String idPath="-1"+map.get("idPath");
			docC.setIdPath(idPath);
		}else{
			docC.setIdPath("-1");
		}
		return docC;
	}

	/**
	 * 
	 * @describe 根据类型获取文档 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月26日 下午5:27:47 <br>
	 * @param docCId
	 * @param part
	 * @return <br>
	 * @returnType DocumentChapter
	 *
	 */
	public DocumentChapter findOneByType(Long docCId, DOC_CHAPTER_TYPE part) {
		DocumentChapter docC = this.documentChapterDao.findByIdAndTypeAndDel(docCId, part, DELTYPE.NORMAL);
		return docC;
	}

	/**
	 * 
	 * @describe 更新文档 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午2:31:12 <br>
	 * @param docC
	 * @return
	 *
	 */
	public DocumentChapter updateDocC(DocumentChapter docC) {
		docC = this.documentChapterDao.save(docC);
		return docC;
	}

	/**
	 * 
	 * @describe 删除文档 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午2:35:03 <br>
	 * @param docCId
	 * @return
	 *
	 */
	public int deleteDocC(Long docCId) {
		DocumentChapter docC = this.documentChapterDao.findByIdAndDel(docCId, DELTYPE.NORMAL);
		if (null != docC) {
			docC.setDel(DELTYPE.DELETED);
			docC = this.documentChapterDao.save(docC);
			List<DocumentChapter> list = this.documentChapterDao.findByDocumentIdAndParentIdAndDel(docC.getDocumentId(),
					docC.getId(), DELTYPE.NORMAL);
			if (list != null) {
				for (DocumentChapter docC1 : list) {
					deleteDocC(docC1.getId());
				}
			}
			return 1;
		}
		return 0;
	}

	/**
	 * 
	 * @describe 发布章节 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午3:37:59 <br>
	 * @param docCId
	 * @param opertion
	 * @return
	 *
	 */
	public int upgradePubOrRetDocC(Long docCId, DOC_PUB_TYPE opertion) {
		DocumentChapter docC = this.documentChapterDao.findByIdAndDel(docCId, DELTYPE.NORMAL);
		Enums.DOC_PUB_TYPE opertionType = null;
		if (Enums.DOC_PUB_TYPE.PUBLISHED.compareTo(opertion)==0) {

			opertionType = Enums.DOC_PUB_TYPE.PUBLISHED;
			Long parentId = docC.getParentId();
			while (parentId != -1) {
				DocumentChapter documentChapter = this.documentChapterDao.findByIdAndDel(parentId, DELTYPE.NORMAL);
				if (null != documentChapter) {
					parentId = documentChapter.getParentId();
					documentChapter.setPub(opertionType);
					this.documentChapterDao.save(documentChapter);
				}
			}

			Document doc = this.documentDao.findByIdAndDel(docC.getDocumentId(), DELTYPE.NORMAL);
			if (null != doc) {
				doc.setPub(opertionType);
				this.documentDao.save(doc);
			}

			this.publishOrRetrieveAllChild(docC,opertionType);
		} else{
			opertionType = Enums.DOC_PUB_TYPE.RETRIEVED;
			this.publishOrRetrieveAllChild(docC,opertionType);
		}

		int a = 0;
		if (null != docC && docC.getId() > 0) {
			docC.setPub(opertionType);
			docC = this.documentChapterDao.save(docC);
			a = 1;
		}
		return a;
	}

	private void publishOrRetrieveAllChild(DocumentChapter docC,Enums.DOC_PUB_TYPE operation) {
		List<DocumentChapter> docCs = this.documentChapterDao.findByParentIdAndDel(docC.getId(), DELTYPE.NORMAL);
		for(DocumentChapter docc : docCs){
			docc.setPub(operation);
			this.documentChapterDao.save(docc);
			if(docc.getType().compareTo(DOC_CHAPTER_TYPE.CHAPTER)==0){
				publishOrRetrieveAllChild(docc,operation);
			}
		}
	}
	

	/**
	 * 
	 * @describe 获取文档下所有章节list <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午3:47:22 <br>
	 * @param docId
	 * @return
	 *
	 */
	public List<Map<String, Object>> getAllChapterAndPart(Long docId) {

		String sql = "SELECT TDC.id,TDC.parentId,TDC.name,TDC.type,TDC.pub,TDC.sort FROM T_DOCUMENT_CHAPTER TDC WHERE TDC.documentId = "
				+ docId + " AND TDC.del = " + Enums.DELTYPE.NORMAL.ordinal() + " ORDER BY TDC.parentId,TDC.sort ASC";
		log.info(" JDBCTEMPLE :" + sql);
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(sql);
		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
		for (int a = 0; a < list.size(); a++) {
			Map<String, Object> map = list.get(a);
			List<Map<?, ?>> obj = new ArrayList<Map<?, ?>>();
			for (int b = 0; b < list.size(); b++) {
				Map<String, Object> map1 = list.get(b);
				if (map.get("id").equals(map1.get("parentId"))) {
					int level = Integer.parseInt(null != map.get("level") ? map.get("level").toString() : "0");
					map1.put("level", level + 1);
					String namePath = null != map.get("namePath") ? map.get("namePath").toString()
							: map.get("name").toString();
					String idPath = null != map.get("idPath") ? map.get("idPath").toString() : map.get("id").toString();
					map1.put("namePath", namePath + "+@_@+" + map1.get("name"));
					map1.put("idPath", idPath + "+@_@+" + map1.get("id"));
					map1.put("type", map1.get("type").equals(Enums.DOC_CHAPTER_TYPE.CHAPTER.ordinal())
							? Enums.DOC_CHAPTER_TYPE.CHAPTER.name() : Enums.DOC_CHAPTER_TYPE.PART.name());
					map1.put("pub", map1.get("pub").equals(Enums.DOC_PUB_TYPE.RETRIEVED.ordinal())
							? Enums.DOC_PUB_TYPE.RETRIEVED.name() : Enums.DOC_PUB_TYPE.PUBLISHED.name());
					obj.add(map1);
				}
			}
			map.put("childs", obj);
			if (map.get("parentId").toString().equals("-1")) {
				map.put("level", 0);
				map.put("namePath", map.get("name"));
				map.put("idPath", map.get("id"));
				map.put("type", map.get("type").equals(Enums.DOC_CHAPTER_TYPE.CHAPTER.ordinal())
						? Enums.DOC_CHAPTER_TYPE.CHAPTER.name() : Enums.DOC_CHAPTER_TYPE.PART.name());
				map.put("pub", map.get("pub").equals(Enums.DOC_PUB_TYPE.RETRIEVED.ordinal())
						? Enums.DOC_PUB_TYPE.RETRIEVED.name() : Enums.DOC_PUB_TYPE.PUBLISHED.name());
				list1.add(map);
			}
		}
		
		for(int a = 0; a < list.size(); a++){
			Map<String, Object> map = list.get(a);
			setlevel(map);
		}
		return list1;
	}

	/**
	 * 
	 * @describe 获取文档下所有目录 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午3:47:22 <br>
	 * @param docId
	 * @return
	 *
	 */
	public List<Map<String, Object>> getAllChapter(Long docId) {

		String sql = "SELECT TDC.id,TDC.parentId,TDC.name,TDC.sort FROM T_DOCUMENT_CHAPTER TDC WHERE TDC.documentId = "
				+ docId + " AND TDC.del = " + Enums.DELTYPE.NORMAL.ordinal() + " AND TDC.type = "
				+ Enums.DOC_CHAPTER_TYPE.CHAPTER.ordinal() + " ORDER BY TDC.parentId,TDC.sort ASC";
		log.info(" JDBCTEMPLE :" + sql);
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(sql);
		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
		for (int a = 0; a < list.size(); a++) {
			Map<String, Object> map = list.get(a);
			List<Map<?, ?>> obj = new ArrayList<Map<?, ?>>();
			for (int b = 0; b < list.size(); b++) {
				Map<String, Object> map1 = list.get(b);
				if (map.get("id").equals(map1.get("parentId"))) {
					int level = Integer.parseInt(null != map.get("level") ? map.get("level").toString() : "0");
					map1.put("level", level + 1);
					obj.add(map1);
				}
			}
			map.put("childs", obj);
			if (map.get("parentId").toString().equals("-1")) {
				map.put("level", 0);
				list1.add(map);
			}
		}
		return list1;
	}

	/**
	 * 
	 * @describe 验证level <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月24日 上午11:03:05 <br>
	 * @param map
	 *            <br>
	 * @returnType void
	 *
	 */
	@SuppressWarnings("unused")
	private void setlevel(Map<String, Object> map) {
		if (null != map.get("childs")) {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("childs");
			for (Map<String, Object> map1 : list) {
				map1.put("level", Integer.parseInt(map.get("level").toString()) + 1);
				setlevel(map1);
			}
		}

	}

	/**
	 * 
	 * @describe 获取文档下所有章节list <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月15日 下午3:47:22 <br>
	 * @param docId
	 * @return
	 *
	 */
	public List<Map<String, Object>> getAllChapterAndPart2(Long docId) {

		String sql = "SELECT TDC.id,TDC.parentId,TDC.name,TDC.sort FROM T_DOCUMENT_CHAPTER TDC WHERE TDC.documentId = "
				+ docId + " AND TDC.del = " + Enums.DELTYPE.NORMAL.ordinal() + " AND TDC.pub = "
				+ Enums.DOC_PUB_TYPE.PUBLISHED.ordinal() + " ORDER BY TDC.parentId,TDC.sort DESC";
		log.info(" JDBCTEMPLE :" + sql);
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(sql);

		return list;
	}

	/**
	 * 
	 * @describe 章节排序 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月17日 下午12:00:46 <br>
	 * @param docCId1
	 * @param sort1
	 * @param docCId2
	 * @param sort2
	 * @return
	 *
	 */
	public int upgradeSortChapter(Long docCId1, Long docCId2) {
		DocumentChapter docC1 = this.documentChapterDao.findOne(docCId1);
		DocumentChapter docC2 = this.documentChapterDao.findOne(docCId2);

		int sortT = docC1.getSort();
		docC1.setSort(docC2.getSort());
		docC2.setSort(sortT);

		this.documentChapterDao.save(docC1);
		this.documentChapterDao.save(docC2);

		// String sql = "UPDATE T_DOCUMENT_CHAPTER TDC SET TDC.sort = ? WHERE
		// TDC.id = ?";
		// int a = this.jdbcTpl.update(sql, new Object[]{sort1,docCId1});
		// int b = this.jdbcTpl.update(sql, new Object[]{sort2,docCId2});

		return 1;
	}

	/**
	 * 
	 * @describe 文档搜索 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月22日 下午4:42:41 <br>
	 * @param query
	 *            <br>
	 * @param docId
	 * @param ipageSize
	 * @param ipageNo
	 * @returnType void
	 *
	 */
	public Page<DocumentChapter> SearchDocumentPart(String query, Long docId, int ipageNo, int ipageSize) {
		PageRequest page = new PageRequest(ipageNo - 1, ipageSize);
		Page<DocumentChapter> docCs = this.documentChapterDao
				.findByDocumentIdAndContentMDLikeAndDelOrderByCreatedAtDesc(docId, query, DELTYPE.NORMAL,
						DOC_CHAPTER_TYPE.PART, page);
		return docCs;
	}

	public int SearchDocumentPartCount(String query, Long docId) {
		int count = this.documentChapterDao.findByContentMDLikeAndDocumentIdAndDel(query, docId, DELTYPE.NORMAL,
				DOC_CHAPTER_TYPE.PART);
		return count;
	}

	/**
	 * 
	 * @describe 文档中心查询 组装数据 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月24日 上午9:10:24 <br>
	 * @param docCs
	 * @param query
	 * @return <br>
	 * @returnType List<Map<String,String>>
	 *
	 */
	public List<Map<String, String>> getWrapResult(List<DocumentChapter> docCs, String query) {
//		Pattern pattern = Pattern.compile("(?i)"+query);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (DocumentChapter docC : docCs) {
			String contentHTML = docC.getContentHTML();
			contentHTML = delHTMLTag(contentHTML);
			contentHTML = get350Bytes(query, contentHTML);
//			contentHTML = contentHTML.replace("(?i)"+query, "<span style=\"color:#d94139\">" + query + "</span>");
			Long parentId = docC.getParentId();
			Long documentId = docC.getDocumentId();
//			String namePath = docC.getName().replace("(?i)"+query, "<span style=\"color:#d94139\">" + query + "</span>");
			String namePath = docC.getName();
			String idPath = docC.getId().toString();
			while (parentId != null) {
				if (parentId != -1L) {
					DocumentChapter docC1 = this.documentChapterDao.findByIdAndDel(parentId, DELTYPE.NORMAL);
					parentId = docC1.getParentId();
					namePath = docC1.getName() + "@+_+@" + namePath;
					idPath = docC1.getId() + "@+_+@" + idPath;
				} else if (parentId.intValue() == -1) {
					Document doc = this.documentDao.findByIdAndDel(documentId, DELTYPE.NORMAL);
					namePath = doc.getName() + "@+_+@" + namePath;
					idPath = doc.getId() + "@+_+@" + idPath;
					parentId = null;
				}
			}
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("namePath", namePath);
			map.put("idPath", idPath);
			map.put("content", contentHTML);
			map.put("id", docC.getId().toString());
			list.add(map);
		}
		return list;
	}

	/**
	 * 
	 * @describe 获取包含关键字的350字节字符串 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月22日 下午5:41:14 <br>
	 * @param query
	 * @param queryBL
	 * @param contentHTML
	 * @return <br>
	 * @returnType String
	 *
	 */
	private String get350Bytes(String query, String contentHTML) {
		int len = getWordCount(contentHTML);
		if (len <= 350) {
			return contentHTML;
		}
		int queryBL = getWordCount(query);
		int queryL = query.length();
		String str = contentHTML.toLowerCase();
		int loc = str.indexOf(query.toLowerCase());//找出关键字首次出现的位置
		// 只有名称中有关键字的 直接返回内容
		if (loc == -1) {
			int g = 0;
			int gg = 0;
			for (; g < 350;) {
				gg++;
				if (getWordCount(contentHTML.substring(gg, gg + 1)) == 1) {
					g++;
				} else
					g += 2;
			}
			contentHTML = contentHTML.substring(0, gg);
			return contentHTML;
		}

		loc = loc == 1 ? 2 : loc;
		int leftMove = (350 - queryBL) / 2;
		int toleft = 0, toright = 0;
		// 左偏移
		int i = 0;
		for (; i <= loc - 1 && toleft < leftMove; i++) {
			toleft = getWordCount(contentHTML.substring(loc - 1 - i, loc - i)) == 1 ? ++toleft : toleft + 2;
		}
		toright = toleft < leftMove ? 350 - queryBL - toleft : (350 - queryBL) / 2;
		log.info("move to the left:" + toleft);
		log.info("move to the right:" + toright);
		// 右偏移
		int j = 0;
		int loc1 = loc + queryL;
		for (; j <= 350 - toleft - queryBL && toright >= 0; j++) {
			if(loc1 + j + 1 > contentHTML.length()){
				break;
			}
			toright = getWordCount(contentHTML.substring(loc1 + j, loc1 + j + 1)) == 1 ? --toright
					: toright - 2 < 0 ? toright - 1 : toright - 2;
		}
		contentHTML = contentHTML.substring(loc - i - 1 < 0 ? 0 : loc - i - 1, loc + j);
		log.info(getWordCount(contentHTML));
		return contentHTML;
	}

	/**
	 * 
	 * @describe 删除Html标签 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月22日 下午5:31:47 <br>
	 * @param htmlStr
	 * @return <br>
	 * @returnType String
	 *
	 */
	public static String delHTMLTag(String htmlStr) {
		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签

		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签

		Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
		Matcher m_space = p_space.matcher(htmlStr);
		htmlStr = m_space.replaceAll(""); // 过滤空格回车标签
		return htmlStr.trim(); // 返回文本字符串
	}

	/**
	 * 
	 * @describe 获取字符串的字节长度 <br>
	 * @author jiexiong.liu <br>
	 * @date 2015年8月22日 下午5:29:39 <br>
	 * @param s
	 * @return <br>
	 * @returnType int
	 *
	 */
	private static int getWordCount(String s) {
		int length = 0;
		int len = s.length();
		for (int i = 0; i < len; i++) {
			int ascii = Character.codePointAt(s, i);
			if (ascii >= 0 && ascii <= 255)
				length++;
			else
				length += 2;

		}
		return length;

	}

	public static void main1(String[] args) {
		String str = "创建项目是看到的’phone’文件App夹），和一个AppCan平台中App间件组成的。通常的app情况下，一个应用app是由一个WidgetappCan构成，那么，有没有可能说’n个WidgetAppCan’的机制呢，答应是肯定";
		String str1 = "效果，窗口被关闭销毁时，指定一个从右到左的切出效果等等。基本达到了与系统级别UI界面切换效果的一致。4.2、Widget插件扩展机制通过AppCan平台生成的应用，可以理解为一个Widget包（即在";
		System.out.println(delHTMLTag(str));
		System.out.println(getWordCount(str));
		System.out.println(getWordCount(str1));
		
		Pattern pattern = Pattern.compile("(?i)app");
		Matcher matcher = pattern.matcher(str);
		if(matcher.find()){
			str = matcher.replaceAll("abc"+matcher.group(0)+"abc");
			System.out.println(str);
		}
	}

	public Map<String, Object> getPubChapterAndPart(Long docId) {
		String sql = "SELECT TDC.id,TDC.parentId,TDC.name,TDC.type,TDC.pub,TDC.sort FROM T_DOCUMENT_CHAPTER TDC WHERE TDC.documentId = "
				+ docId + " AND TDC.pub = " + DOC_PUB_TYPE.PUBLISHED.ordinal() + " AND TDC.del = "
				+ Enums.DELTYPE.NORMAL.ordinal() + " ORDER BY TDC.parentId,TDC.sort ASC";
		log.info(" JDBCTEMPLE :" + sql);
		List<Map<String, Object>> list = this.jdbcTpl.queryForList(sql);
		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
		Map<String, Object> objM = new HashMap<>();
		for (int a = 0; a < list.size(); a++) {
			Map<String, Object> map = list.get(a);
			List<Map<?, ?>> obj = new ArrayList<Map<?, ?>>();
			for (int b = 0; b < list.size(); b++) {
				Map<String, Object> map1 = list.get(b);
				if (map.get("id").equals(map1.get("parentId"))) {
					int level = Integer.parseInt(null != map.get("level") ? map.get("level").toString() : "0");
					map1.put("level", level + 1);
					String namePath = null != map.get("namePath") ? map.get("namePath").toString()
							: map.get("name").toString();
					String idPath = null != map.get("idPath") ? map.get("idPath").toString() : map.get("id").toString();
					map1.put("namePath", namePath + "+@_@+" + map1.get("name"));
					map1.put("idPath", idPath + "+@_@+" + map1.get("id"));
					map1.put("type", map1.get("type").equals(Enums.DOC_CHAPTER_TYPE.CHAPTER.ordinal())
							? Enums.DOC_CHAPTER_TYPE.CHAPTER.name() : Enums.DOC_CHAPTER_TYPE.PART.name());
					map1.put("pub", map1.get("pub").equals(Enums.DOC_PUB_TYPE.RETRIEVED.ordinal())
							? Enums.DOC_PUB_TYPE.RETRIEVED.name() : Enums.DOC_PUB_TYPE.PUBLISHED.name());
					obj.add(map1);
				}
			}
			map.put("childs", obj);
			if (map.get("parentId").toString().equals("-1")) {
				map.put("level", 0);
				map.put("namePath", map.get("name"));
				map.put("idPath", map.get("id"));
				map.put("type", map.get("type").equals(Enums.DOC_CHAPTER_TYPE.CHAPTER.ordinal())
						? Enums.DOC_CHAPTER_TYPE.CHAPTER.name() : Enums.DOC_CHAPTER_TYPE.PART.name());
				map.put("pub", map.get("pub").equals(Enums.DOC_PUB_TYPE.RETRIEVED.ordinal())
						? Enums.DOC_PUB_TYPE.RETRIEVED.name() : Enums.DOC_PUB_TYPE.PUBLISHED.name());
				list1.add(map);
			}
		}
		objM.put("object", list1);
		Document doc = this.documentDao.findOne(docId);
		objM.put("document", doc);
		return objM;
	}

	@Value("${download.file}")
	private String rootpath;

	public void downloadDocumentChapter(Long docCId, String type, HttpServletResponse response) throws IOException {
		DocumentChapter docC = this.findOne(docCId);
		if (null != docC && docC.getPub().compareTo(DOC_PUB_TYPE.PUBLISHED) == 0
				&& docC.getType().compareTo(DOC_CHAPTER_TYPE.PART) == 0) {
			File file = null;
			String contentMD = "";
			if (type.equals("md")) {
				file = new File(rootpath, docC.getName() + ".md");
				contentMD = docC.getContentMD();
			} else {
				file = new File(rootpath, docC.getName() + ".html");
				contentMD ="<html>\n<head>\n<meta http-equiv=\"Content-Type\" content=\"textml; charset=utf-8\" />\n<link rel=\"stylesheet\" href=\"http://newdocx.appcan.cn/editor.md-master/css/editormd.css\" />\n</head>\n<body>\n" 
							+ docC.getContentHTML()
							+"\n</body>\n</html>";
			}

			if (!file.exists()) {
				file.createNewFile();
			}
			// 获取markdown文件的输出流 保存内容到文件中
			FileOutputStream out = new FileOutputStream(file.getPath());
			
			byte[] bb = contentMD.getBytes("UTF-8");
			out.write(bb);
			out.flush();
			out.close();

			OutputStream os = response.getOutputStream();
			response.reset();
			String filename = file.getName();
			filename = new String(filename.getBytes("utf-8"), "ISO_8859_1");
			response.setHeader("Content-Disposition", "attachment; filename=" + filename);
			response.setContentType("application/octet-stream; charset=utf-8");

			os.write(FileUtils.readFileToByteArray(file));
			os.flush();
		}
	}

	public Page<DocumentChapter> PubSearchDocumentPart(String query, Long docId, int ipageNo, int ipageSize) {
		PageRequest page = new PageRequest(ipageNo - 1, ipageSize);
		Page<DocumentChapter> docCs = this.documentChapterDao
				.findByDocumentIdAndNameLikeOrContentMDLikeAndPubAndDelOrderByCreatedAtDesc(docId, query,
						DOC_PUB_TYPE.PUBLISHED, DELTYPE.NORMAL, DOC_CHAPTER_TYPE.PART, page);
		return docCs;
	}

	public int PubSearchDocumentPartCount(String query, Long docId) {
		int count = this.documentChapterDao.findByContentMDLikeAndPubAndDocumentIdAndDel(query, DOC_PUB_TYPE.PUBLISHED,
				docId, DELTYPE.NORMAL, DOC_CHAPTER_TYPE.PART);
		return count;
	}

	public Boolean isExist(DocumentChapter docC) {
		DocumentChapter docc = this.documentChapterDao.findByNameAndParentIdAndDocumentIdAndDel(docC.getName(),
				docC.getParentId(), docC.getDocumentId(), DELTYPE.NORMAL);
		if (null != docc) {
			if(docc.getType().compareTo(docC.getType())==0){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	public String getSpecialCharQuery(String query) {
		query = query.replace("%", "\\%");
		return query;
	}

	
	public boolean isExistDocC(String filename, Long docId, Long parentId) {
		DocumentChapter docc = this.documentChapterDao.findByNameAndParentIdAndDocumentIdAndDel(filename,parentId,docId,DELTYPE.NORMAL);
		if(null != docc){
			if(docc.getType().compareTo(DOC_CHAPTER_TYPE.PART)==0){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	
	public DocumentChapter ExistDocC(String filename, Long docId, Long parentId) {
		DocumentChapter docc = this.documentChapterDao.findByNameAndParentIdAndDocumentIdAndDel(filename,parentId,docId,DELTYPE.NORMAL);
		if(null != docc){
			if(docc.getType().compareTo(DOC_CHAPTER_TYPE.PART)==0){
				return docc;
			}else{
				return null;
			}
		}
		return null;
	}
//	public static void main(String[] args) {
//		String str = "<html><head><meta http-equiv=\"Content-Type\" content=\"textml; charset=utf-8\" /><link rel=\"stylesheet\" href=\"http://newdocx.appcan.cn/editor.md-master/css/editormd.css\" /></head><body><div class=\"markdown-body editormd-preview-container\" previewcontainer=\"true\" style=\"padding: 20px;\"><p>exCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCalluexCall方圣诞节客户高价收购就赶紧<br>和大家很健康色号口角生风看见到很健康防护上健康哈萨克凤凰卡萨丁好<br>返回健康刷卡建行卡</p></div>/n</body>/n</html>";
//		str=delHTMLTag(str);
//		System.out.println(str);
//		str = get350Bytes("建行", str);
//		System.out.println(str);
//	}
}
