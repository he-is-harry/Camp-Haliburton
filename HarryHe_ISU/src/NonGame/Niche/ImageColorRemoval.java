package NonGame.Niche;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageColorRemoval {
	public static void main(String[] args) {
        try {
        	// Upload the image
        	String inputPath = "res/Player/Swat/SwatLeft3.png";
        	String outputPath = "zoutput/colorRemoved.png";
        	BufferedImage image = ImageIO.read(new File(inputPath));
        	int width = image.getWidth();
        	int height = image.getHeight();
        	int rRem = 255, gRem = 255, bRem = 255;
        	
        	int quantityRem = 0;

        	BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        	for (int i = 0; i < image.getWidth(); i++) {
        		for (int j = 0; j < image.getHeight(); j++) {
        			if(((image.getRGB(i, j) >> 24) & 0xff) != 0x00) {
        				int a = (image.getRGB(i, j) >> 24) & 0xff;
        				int r = (image.getRGB(i, j) >> 16) & 0xff;
        				int g = (image.getRGB(i, j) >> 8) & 0xff;
        				int b = image.getRGB(i, j) & 0xff;
        				if(!(r == rRem && g == gRem && b == bRem)) {
        					Graphics2D graph = bufferedImage.createGraphics();
            				graph.setColor(new Color(r, g, b, a));
            				graph.fill(new Rectangle(i, j, 1, 1));
            				graph.dispose();
        				} else {
        					quantityRem++;
        				}
        			}
        		}
        	}
        	System.out.println(quantityRem);
        	ImageIO.write(bufferedImage, "png", new File(outputPath));
        } catch (Exception exc) {
            System.out.println("Interrupted: " + exc.getMessage());
            exc.printStackTrace();
        }
    }
}
