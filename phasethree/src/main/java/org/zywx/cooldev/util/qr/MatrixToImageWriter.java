package org.zywx.cooldev.util.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.awt.image.BufferedImage;

/**
 * 二维码生成类
 * @author Administrator
 *
 */
public final class MatrixToImageWriter {

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;

	private MatrixToImageWriter() {
	}

	public static BufferedImage toBufferedImage(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
			}
		}
		return image;
	}

	public static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, file)) {
			throw new IOException("Could not write an image of format " + format + " to " + file);
		}
	}

	public static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
		BufferedImage image = toBufferedImage(matrix);
		if (!ImageIO.write(image, format, stream)) {
			throw new IOException("Could not write an image of format " + format);
		}
	}
	
	/**
	 * 将图片输出流中
	 * @param content
	 * @param stream
	 */
	public static void writeToResponse(String content,OutputStream stream){
		MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
	     Map hints = new HashMap();
	     hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
	     try {
			BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200,hints);
			MatrixToImageWriter.writeToStream(bitMatrix, "jpg", stream);
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void generatePic(){
		try {
            
		     String content = "http://zymobitest.appcan.cn/coopDevelopment/qr.html?url=http://appcan-cooperation.oss-cn-beijing.aliyuncs.com//11594473/15c623d030c-11232_IOS_01.03.0011_0000.ipa";
		     String path = "C:/Users/Administrator/Desktop/testImage";
		     
		     MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
		     
		     Map hints = new HashMap();
		     hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		     BitMatrix bitMatrix = multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200,hints);
		     File file1 = new File(path,"wjj.jpg");
		     MatrixToImageWriter.writeToFile(bitMatrix, "jpg", file1);
		     
		 } catch (Exception e) {
		     e.printStackTrace();
		 }
	}

	public static void main(String[] args) {
		MatrixToImageWriter.generatePic();
	}
}