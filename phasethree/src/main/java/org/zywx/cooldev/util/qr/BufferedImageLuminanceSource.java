package org.zywx.cooldev.util.qr;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

public final class BufferedImageLuminanceSource extends LuminanceSource {

  private final BufferedImage image;
  private final int left;
  private final int top;

  public BufferedImageLuminanceSource(BufferedImage image) {
    this(image, 0, 0, image.getWidth(), image.getHeight());
  }

  public BufferedImageLuminanceSource(BufferedImage image, int left, int top, int width, int height) {
    super(width, height);

    int sourceWidth = image.getWidth();
    int sourceHeight = image.getHeight();
    if (left + width > sourceWidth || top + height > sourceHeight) {
      throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
    }

    for (int y = top; y < top + height; y++) {
      for (int x = left; x < left + width; x++) {
        if ((image.getRGB(x, y) & 0xFF000000) == 0) {
          image.setRGB(x, y, 0xFFFFFFFF); // = white
        }
      }
    }

    this.image = new BufferedImage(sourceWidth, sourceHeight, BufferedImage.TYPE_BYTE_GRAY);
    this.image.getGraphics().drawImage(image, 0, 0, null);
    this.left = left;
    this.top = top;
  }

  @Override
  public byte[] getRow(int y, byte[] row) {
    if (y < 0 || y >= getHeight()) {
      throw new IllegalArgumentException("Requested row is outside the image: " + y);
    }
    int width = getWidth();
    if (row == null || row.length < width) {
      row = new byte[width];
    }
    image.getRaster().getDataElements(left, top + y, width, 1, row);
    return row;
  }

  @Override
  public byte[] getMatrix() {
    int width = getWidth();
    int height = getHeight();
    int area = width * height;
    byte[] matrix = new byte[area];
    image.getRaster().getDataElements(left, top, width, height, matrix);
    return matrix;
  }

  @Override
  public boolean isCropSupported() {
    return true;
  }

  @Override
  public LuminanceSource crop(int left, int top, int width, int height) {
    return new BufferedImageLuminanceSource(image, this.left + left, this.top + top, width, height);
  }

  @Override
  public boolean isRotateSupported() {
    return true;
  }

  @Override
  public LuminanceSource rotateCounterClockwise() {

      int sourceWidth = image.getWidth();
    int sourceHeight = image.getHeight();

    AffineTransform transform = new AffineTransform(0.0, -1.0, 1.0, 0.0, 0.0, sourceWidth);

    BufferedImage rotatedImage = new BufferedImage(sourceHeight, sourceWidth, BufferedImage.TYPE_BYTE_GRAY);

    Graphics2D g = rotatedImage.createGraphics();
    g.drawImage(image, transform, null);
    g.dispose();

    int width = getWidth();
    return new BufferedImageLuminanceSource(rotatedImage, top, sourceWidth - (left + width), getHeight(), width);
  }
  
	public static void parseImg2Content() {
		try {
			MultiFormatReader formatReader = new MultiFormatReader();
			String filePath = "C:/Users/Administrator/Desktop/qr.jpg";
			File file = new File(filePath);
			BufferedImage image = ImageIO.read(file);
			LuminanceSource source = new BufferedImageLuminanceSource(image);
			Binarizer binarizer = new HybridBinarizer(source);
			BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
			Map hints = new HashMap();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			Result result = formatReader.decode(binaryBitmap, hints);

			System.out.println("result = " + result.toString());
			System.out.println("resultFormat = " + result.getBarcodeFormat());
			System.out.println("resultText = " + result.getText());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		BufferedImageLuminanceSource.parseImg2Content();
	}

}
