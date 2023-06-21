package NonGame.Bulk;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageScalerBulk {
	public static void main(String [] args) throws IOException {
		double maxFactor = 1.1;
		double fact = 1;
		int orgWidth = 600, orgHeight = 280;
		BufferedImage originalImage = ImageIO.read(new File("res/Menu/MainMenuTitle.png"));
		int numImages = 10;
		
		double diff = maxFactor - fact;
		
		for(int t = 1; t <= numImages; t++) {
			int newWidth = (int)(orgWidth * fact);
			int newHeight = (int)(orgHeight * fact);
			
			BufferedImage newImage = getScaledImage(originalImage, newWidth, newHeight);
			ImageIO.write(newImage, "png", new File("res/Menu/Title/MainMenuTitle" + t + ".png"));
			fact += (diff / numImages);
		}
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
