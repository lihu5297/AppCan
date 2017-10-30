package org.zywx.cooldev.thread;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.util.FileSystemUtils;
import org.zywx.cooldev.entity.Resource;

public class ResourceTransferThread implements Runnable{

	
	Logger log = Logger.getLogger(this.getClass());
	
	
	private File srcFile;
	
	private File targetFile;
	
	private Resource srcRes;
	
	
	
	@Override
	public void run(){
		try {
			log.info("resource transfer -->srcFile:"+srcFile.toString()+",targetFile:"+targetFile+",srcRes:"+srcRes.toStr());
			FileSystemUtils.copyRecursively(srcFile, targetFile);
			String [] picture = new String[]{".JPEG",".JPG",".PNG",".SWF",".SVG",".PCX",".DXF",".WMF",".EMF",".TIFF",".PSD",".GIF",".BMP"};
			boolean isPic = false;//是否是图片
			for(String str : picture){
				if(str.toLowerCase().equals(srcRes.getType().toLowerCase())){
					isPic = true;
					break;
				}
			}
			if(isPic){//如果是文件,并且有缩略图的话,需要将对应的缩略图也移过去
				if(new File(srcFile.getParent()+File.separator+"abbr_"+srcFile.getName()).exists()){
					//如果文件名以expectFileName开头,并且去掉expectFileName的文件也存在,标识此文件为需要排除的文件,则不需要压缩
					FileSystemUtils.copyRecursively(new File(srcFile.getParent()+File.separator+"abbr_"+srcFile.getName()), new File(targetFile.getParent()+File.separator+"abbr_"+targetFile.getName()));
					FileSystemUtils.deleteRecursively(new File(srcFile.getParent()+File.separator+"abbr_"+srcFile.getName()));
				}
			}
			boolean flag =FileSystemUtils.deleteRecursively(srcFile);
			log.info("delete file "+srcFile +" result:"+flag);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("转移文件失败"+e.getMessage());
		}
	}
	
	public ResourceTransferThread() {
		super();
	}
	
	public ResourceTransferThread(File srcFile,File targetFile,Resource srcRes){
		this.srcFile = srcFile;
		this.targetFile = targetFile;
		this.srcRes = srcRes;
	}

	public File getSrcFile() {
		return srcFile;
	}

	public void setSrcFile(File srcFile) {
		this.srcFile = srcFile;
	}

	public File getTargetFile() {
		return targetFile;
	}

	public void setTargetFile(File targetFile) {
		this.targetFile = targetFile;
	}

	public Resource getSrcRes() {
		return srcRes;
	}

	public void setSrcRes(Resource srcRes) {
		this.srcRes = srcRes;
	}
	
	
}
