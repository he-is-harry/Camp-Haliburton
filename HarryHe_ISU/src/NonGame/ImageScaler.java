package NonGame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageScaler {
	public static void main(String [] args) throws IOException {
		int setHeight = 8;
		BufferedImage originalImage = ImageIO.read(new File("zoutput/LargerRacket.png"));
//		BufferedImage newImage = getScaledImage(originalImage, (int)((double)originalImage.getWidth() / originalImage.getHeight() * setHeight), setHeight);
		BufferedImage newImage = getScaledImage(originalImage, 80, 30);
		ImageIO.write(newImage, "png", new File("zoutput/scaled.png"));
		
	}
	
	public static BufferedImage getScaledImage(BufferedImage originalImage, int width, int height) {
		// Local Variables
		// The scaledImage variable stores the Image instance of the scaled image, which should be
		// converted to a BufferedImage to allow for more functionality with the image.
		Image scaledImage = originalImage.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
		// The convertImage is the BufferedImage with the same dimensions of the Image
		// used as the return value.
		BufferedImage convertImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		// The Graphics2D is just used to draw on the Image onto the blank BufferedImage.
		Graphics2D bufferedGraphics = convertImage.createGraphics();

		// Method Body
		bufferedGraphics.drawImage(scaledImage, 0, 0, null);
		bufferedGraphics.dispose();
		return convertImage;
	}
}
