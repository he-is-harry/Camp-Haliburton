package NonGame.Niche;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImagePadder {
	public static void main(String [] args) {
		try {
			BufferedImage originalImage = ImageIO.read(new File("res/Player/Swat/SwatLeft2.png"));
			int width = originalImage.getWidth();
			int height = originalImage.getHeight();
			int padding = 25;
			BufferedImage convertImage = new BufferedImage(width + padding, height, BufferedImage.TYPE_INT_ARGB);
			// The Graphics2D is just used to draw on the Image onto the blank BufferedImage.
			Graphics2D bufferedGraphics = convertImage.createGraphics();

			// Method Body
			bufferedGraphics.drawImage(originalImage, padding, 0, null);
			bufferedGraphics.dispose();
			ImageIO.write(convertImage, "png", new File("zoutput/padded.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
