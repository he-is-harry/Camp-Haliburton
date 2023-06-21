package NonGame.Bulk;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ImageMirrorBulk {
	public static void main(String[] args) {
        try {
        	for(int t = 1; t <= 5; t++) {
        		// Upload the image
                BufferedImage image = ImageIO.read(new File("res/Player/Jump/JumpLeft" + t + ".png"));
                int width = image.getWidth();
                int height = image.getHeight();
                
                BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                for (int i = image.getWidth() - 1; i >= 0; i--) {
    				for (int j = 0; j < image.getHeight(); j++) {
    					if(((image.getRGB(i, j) >> 24) & 0xff) != 0x00) {
    						int a = (image.getRGB(i, j) >> 24) & 0xff;
            				int r = (image.getRGB(i, j) >> 16) & 0xff;
            				int g = (image.getRGB(i, j) >> 8) & 0xff;
            				int b = image.getRGB(i, j) & 0xff;
            				Graphics2D graph = bufferedImage.createGraphics();
            				graph.setColor(new Color(r, g, b, a));
            				graph.fill(new Rectangle(width - i - 1, j, 1, 1));
            				graph.dispose();
    					}
    				}
    			}
                
                ImageIO.write(bufferedImage, "png", new File("res/Player/Jump/JumpRight" + t + ".png"));
        	}
        } catch (Exception exc) {
            System.out.println("Interrupted: " + exc.getMessage());
            exc.printStackTrace();
        }
    }
}
