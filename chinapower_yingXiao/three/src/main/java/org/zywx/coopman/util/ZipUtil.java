package org.zywx.coopman.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
 
import java.util.List;
import java.util.Set;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtil {
	 
	/**
	 * 
	 * @describe 压缩文件夹和文件	<br>
	 * @author jiexiong.liu	<br>
	 * @date 2015年8月19日 下午5:48:37	<br>
	 * @param src	源文件或者目录
	 * @param dest	压缩文件路径
	 * @throws IOException  <br>
	 * @returnType void
	 *
	 */
    public static void zip(String src, String dest) throws IOException {
        ZipOutputStream out = null;
        try {
            File outFile = new File(dest);
            out = new ZipOutputStream(outFile);
            File fileOrDirectory = new File(src);
 
            if (fileOrDirectory.isFile()) {
            	//目标路劲是文件
                zipFileOrDirectory(out, fileOrDirectory, "");
            } else {
            	//目标路径是目录
                File[] entries = fileOrDirectory.listFiles();
                for (int i = 0; i < entries.length; i++) {
                    // 递归压缩，更新curPaths
                    zipFileOrDirectory(out, entries[i], "");
                }
            }
 
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                }
            }
        }
    }
 
    /**
     * 
     * @describe 递归压缩文件或目录	<br>
     * @author jiexiong.liu	<br>
     * @date 2015年8月19日 下午5:49:28	<br>
     * @param out	压缩输出流对象
     * @param fileOrDirectory	要压缩的文件或目录对象
     * @param curPath	当前压缩条目的路径，用于指定条目名称的前缀
     * @throws IOException  <br>
     * @returnType void
     *
     */
    private static void zipFileOrDirectory(ZipOutputStream out, File fileOrDirectory, String curPath) throws IOException {
        FileInputStream in = null;
        try {
            if (!fileOrDirectory.isDirectory()) {
                // 压缩文件
                byte[] buffer = new byte[4096];
                int bytes_read;
                in = new FileInputStream(fileOrDirectory);
 
                ZipEntry entry = new ZipEntry(curPath + fileOrDirectory.getName());
                out.putNextEntry(entry);
 
                while ((bytes_read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytes_read);
                }
                out.closeEntry();
            } else {
                // 压缩目录
                File[] entries = fileOrDirectory.listFiles();
                if(entries.length==0){
                	// 压缩空目录
                    ZipEntry entry = new ZipEntry(curPath + fileOrDirectory.getName()+"/");
                    out.putNextEntry(entry);
                    out.closeEntry();
                }else{
	                for (int i = 0; i < entries.length; i++) {
	                    // 递归压缩，更新curPaths
	                    zipFileOrDirectory(out, entries[i], curPath + fileOrDirectory.getName() + "/");
	                }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
        }
    }
 
    /**
     * 
     * @describe 解压缩	<br>
     * @author jiexiong.liu	<br>
     * @date 2015年8月19日 下午5:50:40	<br>
     * @param zipFileName	源文件
     * @param outputDirectory	解压缩后文件存放的目录
     * @throws IOException  <br>
     * @returnType void
     *
     */
    public static void unzip(String zipFileName, String outputDirectory) throws IOException {
 
        ZipFile zipFile = null;
 
        try {
            zipFile = new ZipFile(zipFileName);
            Enumeration<?> e = zipFile.getEntries();
 
            ZipEntry zipEntry = null;
 
            File dest = new File(outputDirectory);
            dest.mkdirs();
 
            while (e.hasMoreElements()) {
                zipEntry = (ZipEntry) e.nextElement();
 
                String entryName = zipEntry.getName();
 
                InputStream in = null;
                FileOutputStream out = null;
 
                try {
                    if (zipEntry.isDirectory()) {
                        String name = zipEntry.getName();
                        name = name.substring(0, name.length() - 1);
 
                        File f = new File(outputDirectory + File.separator + name);
                        f.mkdirs();
                    } else {
                        int index = entryName.lastIndexOf("\\");
                        if (index != -1) {
                            File df = new File(outputDirectory + File.separator + entryName.substring(0, index));
                            df.mkdirs();
                        }
                        index = entryName.lastIndexOf("/");
                        if (index != -1) {
                            File df = new File(outputDirectory + File.separator + entryName.substring(0, index));
                            df.mkdirs();
                        }
 
                        File f = new File(outputDirectory + File.separator + zipEntry.getName());
                        // f.createNewFile();
                        in = zipFile.getInputStream(zipEntry);
                        out = new FileOutputStream(f);
 
                        int c;
                        byte[] by = new byte[1024];
 
                        while ((c = in.read(by)) != -1) {
                            out.write(by, 0, c);
                        }
                        out.flush();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    throw new IOException("解压失败：" + ex.toString());
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException ex) {
                        }
                    }
                }
            }
 
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IOException("解压失败：" + ex.toString());
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException ex) {
                }
            }
        }
 
    }
 
    /**
     * 压缩文件夹，排除部分子文件
     */
    public static void zipExcept(String src, String dest, Set<String> exceptList) {
        ZipOutputStream out = null;
        try {
            File outFile = new File(dest);
            out = new ZipOutputStream(outFile);
            File fileOrDirectory = new File(src);
 
            if (fileOrDirectory.isFile()) {
            	//目标路劲是文件
                zipFileOrDirectory(out, fileOrDirectory, "");
            } else {
            	//目标路径是目录
                File[] entries = fileOrDirectory.listFiles();
                for (int i = 0; i < entries.length; i++) {
                	File entry = entries[i];
                	if(exceptList.contains(entry.getAbsolutePath())) {
                		continue;
                	}
                    // 递归压缩，更新curPaths
                    zipFileOrDirectory(out, entry, "");
                }
            }
 
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                }
            }
        }    	
    }
    
    public static void main(String[] args) {
        try {
            ZipUtil.zip("E:\\SVN\\coopDevelopment\\mas\\coopDevelopment", "F:\\test.zip");
            
            ZipUtil.unzip("F:\\test.zip", "F:\\test");
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
 
}
