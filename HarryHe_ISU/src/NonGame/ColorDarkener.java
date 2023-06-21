package NonGame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ColorDarkener {
	public static void main(String[] args) {
		try {
			// Upload the image
			BufferedImage image = ImageIO.read(new File("raw/ScouterBryant.png"));
			int width = image.getWidth();
			int height = image.getHeight();
			int redDark = 20, blueDark = 20, greenDark = 20;

			BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			for (int i = 0; i < width; i++) {
				for (int j = 0; j < height; j++) {
					if(((image.getRGB(i, j) >> 24) & 0xff) != 0x00) {
						int r = (image.getRGB(i, j) >> 16) & 0xff;
						int g = (image.getRGB(i, j) >> 8) & 0xff;
						int b = image.getRGB(i, j) & 0xff;
						r -= redDark;
						if(r < 0) r = 0;
						g -= greenDark;
						if(g < 0) g = 0;
						b -= blueDark;
						if(b < 0) b = 0;
						Graphics2D graph = bufferedImage.createGraphics();
						graph.setColor(new Color(r, g, b));
						graph.fill(new Rectangle(i, j, 1, 1));
						graph.dispose();
					}
				}
			}

			ImageIO.write(bufferedImage, "png", new File("zoutput/darkend.png"));

		} catch (Exception exc) {
			System.out.println("Interrupted: " + exc.getMessage());
			exc.printStackTrace();
		}
	}
}
