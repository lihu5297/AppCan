package org.zywx.coopman.service;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.zywx.coopman.entity.GitOperationLog;
import org.zywx.coopman.entity.QueryEntity;


@Service
public class GitStatisticService extends BaseService{

	public Page<GitOperationLog> findAllBySearch(QueryEntity queryEntity) {
		Pageable page = new PageRequest(queryEntity.getPageNo()-1,queryEntity.getPageSize(),Direction.DESC,"id");
		if(queryEntity.getEndTime()==null){
			return this.gitOperationLogDao.findByAccountLike("%"+queryEntity.getSearch()+"%",page);
		}else{
			return this.gitOperationLogDao.findByAccountLike("%"+queryEntity.getSearch()+"%",queryEntity.getStartTime(),queryEntity.getEndTime(),page);
		}
	}

	
	public void exportExcel(HttpServletResponse response, QueryEntity queryEntity) {
		List<GitOperationLog> pageList = null;
		if(queryEntity.getEndTime()==null){
			pageList = this.gitOperationLogDao.findByAccountLike("%"+queryEntity.getSearch()+"%");
		}else{
			pageList = this.gitOperationLogDao.findByAccountLike("%"+queryEntity.getSearch()+"%",queryEntity.getStartTime(),queryEntity.getEndTime());
		}
		
		this.buildExcel(response,pageList,queryEntity);
		
	}


	private void buildExcel(HttpServletResponse response, List<GitOperationLog> content,QueryEntity queryEntity) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("git操作统计");
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("账号");cell.setCellStyle(style);
		HSSFCell cell1 = row.createCell(1);
		cell1.setCellValue("提交地址");cell1.setCellStyle(style);
		HSSFCell cell2 = row.createCell(2);
		cell2.setCellValue("提交时间");cell2.setCellStyle(style);
		
		
		int i = 1;
		for(GitOperationLog git : content){
			row = sheet.createRow(i++);
			row.createCell(0).setCellValue(git.getAccount());
			row.createCell(1).setCellValue(git.getGitRemoteUrl());
			row.createCell(2).setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(git.getUpdatedAt()));
		}
		
		String filename = "git操作统计_";
		
		if(queryEntity.getEndTime()!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒");
			filename += sdf.format(queryEntity.getStartTime())+"_";
			filename += sdf.format(queryEntity.getEndTime());
		}else{
			filename += "全部";
		}
		
		
		try{
			filename  = new String(filename.getBytes("utf-8"), "ISO_8859_1");
			response.setContentType("application/x-msdownload;charset=utf-8");
			response.setHeader("content-disposition", "attachment;filename="+filename+".xls");
			ServletOutputStream out = response.getOutputStream();
			wb.write(out);
			out.flush();
			out.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
