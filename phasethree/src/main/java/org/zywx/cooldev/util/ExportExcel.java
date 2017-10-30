package org.zywx.cooldev.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.bson.Document;

import net.sf.json.JSONObject;

/**
 * 导出excel
 * 
 * @author "xingshen.zhao"
 *
 */
public class ExportExcel {
	protected static Log Logger = LogFactory.getLog(ExportExcel.class);

	/**
	 * 导出Excel文件
	 * 
	 * @param title
	 *            文件标题/字段英文名 格式如下：姓名|name,年龄|age,性别|gender
	 * @param fileName
	 *            保存文件名
	 * @param listContent
	 *            查询文件名称
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final static HSSFWorkbook exportExcel(String[] title, String fileName, List listContent) {
	
		Object obj ;
		// 创建新的Excel 工作簿
		HSSFWorkbook wb = new HSSFWorkbook();
		
		HSSFSheet sheet = wb.createSheet(fileName);

		HSSFCellStyle setBorder = wb.createCellStyle();
		setBorder.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
		for (int index = 0; index <= listContent.size(); index++) {
			HSSFRow row = sheet.createRow((short) index);
			if (index == 0) {
				row.setHeightInPoints((short) 30);// 设置ex单元格的高度
			} else {
				row.setHeightInPoints((short) 20);// 设置ex单元格的高度
			}
			for (int i = 0; i < title.length; i++) {
				sheet.setColumnWidth(i, 5000);
				HSSFCell cell = row.createCell((short) i);
				
				String cName = title[i].split("\\|")[0];
				String eName = title[i].split("\\|")[1];
				String cellVal = "";
				if (index == 0) {
					cellVal = cName;
				} else {
					if(listContent.get(index - 1) instanceof Map){
						obj =((Map<String,Object>) listContent.get(index - 1)).get(eName);
						cellVal = obj ==null ?"" :obj.toString();
					}else{
						
						cellVal =(null!=(JSONObject.fromObject(listContent.get(index - 1))).get(eName))?(JSONObject.fromObject(listContent.get(index - 1))).get(eName).toString():"";
					}
				}
				try {
					cell.setCellStyle(setBorder);
					cell.setCellValue(cellVal);
				} catch (Exception e) {
					Logger.error(e.getMessage());
				}
			}
		}
		return wb;
	}
}
