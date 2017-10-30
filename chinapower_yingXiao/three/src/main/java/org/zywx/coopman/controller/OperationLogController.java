package org.zywx.coopman.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.language.bm.Lang;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.zywx.coopman.entity.DailyLog.OperationLog;
import org.zywx.coopman.service.OperationLogService;
import org.zywx.coopman.util.ReflectUtil;

@Controller
@RequestMapping(value = "/operationlog")
public class OperationLogController extends BaseController {

	@Autowired
	private OperationLogService opertionLogService; 

	@RequestMapping
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "pageNo", required = false) Integer pageNo,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "startTime", required = false) Timestamp startTime,
			@RequestParam(value = "endTime", required = false) Timestamp endTime,
			@RequestParam(value = "queryKey", required = false) String queryKey) {
		ModelAndView mv = new ModelAndView("/operationLog/list");
		int ipageNo = 0;
		int ipageSize = 10;
		try {
			if (pageNo != null && pageNo > 0) {
				ipageNo = pageNo - 1;
				ipageSize = pageSize;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("").addObject("pageNo or pageSize is illegal");
		}
		PageRequest page = new PageRequest(ipageNo, ipageSize, Direction.DESC, "createdAt");
		Page<OperationLog> page1 = null;
		if(queryKey==null){
			queryKey = "";
		}else{
			try {
				queryKey = URLDecoder.decode(queryKey,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if (null != startTime && null != endTime ) {
			page1 = this.opertionLogService.getList(page, startTime, endTime, "%" + queryKey + "%");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			mv.addObject("startTime", sdf.format(startTime));
			mv.addObject("endTime", sdf.format(endTime));
			mv.addObject("queryKey", queryKey);
		}else if ("" != queryKey) {
			page1 = this.opertionLogService.getList(page, "%" + queryKey + "%");
			mv.addObject("queryKey", queryKey);
		} else
			page1 = this.opertionLogService.getList(page);
		if (page1 != null && page1.getContent() != null) {
			mv.addObject("list", page1.getContent());
			mv.addObject("total", page1.getTotalElements());
			mv.addObject("totalPage", page1.getTotalPages());
			mv.addObject("curPage", ipageNo + 1);
			mv.addObject("pageSize", ipageSize);
		} else {
			mv.addObject("list", null);
			mv.addObject("total", 0);
			mv.addObject("totalPage", 0);
			mv.addObject("curPage", 1);
			mv.addObject("pageSize", ipageSize);
		}
		return mv;
	}

	@RequestMapping(value = "/export")
	public void export(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "startTime", required = false) Timestamp startTime,
			@RequestParam(value = "endTime", required = false) Timestamp endTime,
			@RequestParam(value = "queryKey", required = false) String queryKey) {
		
		try {
			List<OperationLog> list = null;
			String filename = "操作日志_";
			if(queryKey==null){
				queryKey = "";
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			if (null != startTime && null != endTime) {
				list = this.opertionLogService.getList(startTime, endTime, "%" + queryKey + "%");
				filename += sdf.format(startTime) + "-" + sdf.format(endTime) + "(包含关键字：" + queryKey + ")";
			} else if ("" != queryKey) {
				list = this.opertionLogService.getList("%" + queryKey + "%");
				filename += "(包含关键字：" + queryKey + ")";
			} else {
				list = this.opertionLogService.getList();
				filename += "全部";
			}
			LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			map.put("account", "帐号");
			map.put("ip", "IP");
			map.put("operationLog", "操作日志");
			map.put("createdAt", "操作时间");
			excelExport(request, response, list, new OperationLog(), filename, map);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void excelExport(HttpServletRequest request, HttpServletResponse response, List<OperationLog> list,
			Object object, String filename, LinkedHashMap<String, String> map) throws InstantiationException, ClassNotFoundException {
		try {
			// 根据object获取属性和get、set方法
			ReflectUtil ru = new ReflectUtil();
			List<java.lang.reflect.Field> fields = ru.getField(object);
			List<java.lang.reflect.Method> methods = ru.getMethod(object);
			fields = removeUnused(fields, map);
			Set<String> set = map.keySet();
			// 创建新的Excel 工作簿
			@SuppressWarnings("resource")
			HSSFWorkbook hs = new HSSFWorkbook();
			// 创建sheet
			HSSFSheet sh = hs.createSheet(filename);
			HSSFCellStyle CellStyle = hs.createCellStyle();
			CellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
			for (int index = 0; index <= (list.size() == 0 ? 1 : list.size()); index++) {
				HSSFRow row = sh.createRow((short) index);
				Object obj= null;
				if (index == 0) {
					row.setHeightInPoints((short) 30);// 设置ex单元格的高度
				} else {
					row.setHeightInPoints((short) 20);// 设置ex单元格的高度
					obj = list.get(index-1);
				}
				Iterator<String> iterator = set.iterator();
				for (int i = 0; i < (list.size() == 0 ? fields.size() : set.size()); i++) {
					sh.setColumnWidth(i, 5000);
					@SuppressWarnings("deprecation")
					HSSFCell cell = row.createCell((short) i);
					String cellVal = "";
					if (index == 0) {
						cellVal = iterator.next();
						cellVal = map.get(cellVal);
					} else if (list.size() != 0) {
						/**
						 * list中Object[]的元素是根据fields中field的排列顺序进行数据库查询的
						 * 根据排列顺序取出对应field的结果
						 */
						Object value = null;
						String name = iterator.next();
						for(Method method : methods){
							if(method.getName().contains("get") && method.getName().toLowerCase().contains(name.toLowerCase())) {
								value = method.invoke(obj);
							}
						}
						cellVal = getTrueValue(value);
					}
					try {
						cell.setCellStyle(CellStyle);
						int intval = Integer.parseInt(cellVal);
						cell.setCellValue(intval);
					} catch (Exception e) {
						cell.setCellValue(cellVal);
					}
				}
			}
			// 下载文件名称:模块下载次数统计_2015年07月17日.xls
			// 转成ios_8859-1 避免下载文件名称乱码和名称不全
			
			filename = new String(filename.getBytes("utf-8"), "ISO_8859_1");
			
			response.setContentType("application/x-msdownload;charset=utf-8");
			response.setHeader("content-disposition", "attachment;filename=" + filename + ".xls");
			ServletOutputStream out = response.getOutputStream();
			hs.write(out);
			out.flush();
			out.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private String getTrueValue(Object value) {
		if(value.getClass().equals(Lang.class)){
			return value.toString();
		}else if(value.getClass().equals(Timestamp.class)){
			Timestamp time = (Timestamp) value;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(time);
		}else 
			return value.toString();
		
	}

	private List<java.lang.reflect.Field> removeUnused(List<java.lang.reflect.Field> fields,
			HashMap<String, String> map) {
		for (int i = 0; i < fields.size(); i++) {
			if (map.containsKey(fields.get(i).getName())) {
				continue;
			} else
				fields.remove(fields.get(i));
			i--;
		}
		return fields;

	}
}
