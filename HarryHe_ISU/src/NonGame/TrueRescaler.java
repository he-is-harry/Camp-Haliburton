package NonGame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;

import javax.imageio.ImageIO;

public class TrueRescaler {
	public static void main(String[] args) {
        try {
            // Upload the image
            BufferedImage image = ImageIO.read(new File("raw/Porta-potty.png"));
        	String outputPath = "zoutput/ScaledPorta-potty.png";
        	int setHeight = 240;
//        	int setWidth = 120;
        	
            int left = 0, right = image.getWidth() - 1, top = 0, bottom = image.getHeight() - 1;
            boolean found = false;
            
            // Get Left
            for (int i = 0; i < image.getWidth() && !found; i++) {
				for (int j = 0; j < image.getHeight(); j++) {
					if(((image.getRGB(i, j) >> 24) & 0xff) != 0x00) {
						left = i;
						found = true;
						break;
					}
				}
			}
            
            found = false;
            for (int i = image.getWidth() - 1; i >= 0 && !found; i--) {
				for (int j = 0; j < image.getHeight(); j++) {
					if(((image.getRGB(i, j) >> 24) & 0xff) != 0x00) {
						right = i;
						found = true;
						break;
					}
				}
			}
            
            found = false;
            for (int j = 0; j < image.getHeight() && !found; j++) {
				for (int i = 0; i < image.getWidth(); i++) {
					if(((image.getRGB(i, j) >> 24) & 0xff) != 0x00) {
						top = j;
						found = true;
						break;
					}
				}
			}
            
            found = false;
            for (int j = image.getHeight() - 1; j >= 0 && !found; j--) {
				for (int i = 0; i < image.getWidth(); i++) {
					if(((image.getRGB(i, j) >> 24) & 0xff) != 0x00) {
						bottom = j;
						found = true;
						break;
					}
				}
			}
//            System.out.println(left + " " + right + " " + top + " " + bottom);
            System.out.println("Ref: " + (right - left + 1) + " " + (bottom - top + 1));
            
            BufferedImage newImage = image.getSubimage(left, top, right - left + 1, bottom - top + 1);
            System.out.println("New: " + ((int)((double)newImage.getWidth() / newImage.getHeight() * setHeight)) + " " + (setHeight));
//            System.out.println("New: " + (setWidth) + " " + ((int)((double)newImage.getHeight() / newImage.getWidth() * setWidth)));
//            BufferedImage rescaled = getScaledImage(newImage, setWidth, (int)((double)newImage.getHeight() / newImage.getWidth() * setWidth));
            BufferedImage rescaled = getScaledImage(newImage, (int)((double)newImage.getWidth() / newImage.getHeight() * setHeight), setHeight);
            
            ImageIO.write(rescaled, "png", new File(outputPath));
            
//            ImageIO.write(newImage, "png", new File("cropped.png"));

        } catch (Exception exc) {
//        	System.out.println(exc instanceof FileNotFoundException);
            System.out.println("Interrupted: " + exc.getMessage());
            exc.printStackTrace();
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
