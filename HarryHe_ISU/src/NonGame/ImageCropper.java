package NonGame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageCropper {
	public static void main(String [] args) throws IOException {
		BufferedImage org = ImageIO.read(new File("raw/Dock.png"));
//		BufferedImage newImage = org.getSubimage(0, org.getHeight() - 140, org.getWidth(), 140);
//		BufferedImage newImage = org.getSubimage(87, 0, org.getWidth() - 87, org.getHeight());
//		BufferedImage newImage = org.getSubimage(0, 24, org.getWidth(), org.getHeight() - 24);
		BufferedImage newImage = org.getSubimage(0, 0, org.getWidth(), org.getHeight() - 2);
		File outputfile = new File("zoutput/croppedImage.png");
		ImageIO.write(newImage, "png", outputfile);
	}
}
