package NonGame.Bulk;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class ImagePixelatorBulk {
	public static class RGB {

        int red, green, blue;

        public RGB(int r, int g, int b) {
            red = r;
            green = g;
            blue = b;
        }
    }

    public static void main(String[] args) {
        try {
        	for(int t = 1; t <= 6; t++) {
        		// Upload the image
                BufferedImage image = ImageIO.read(new File("animation/Jump/Jump" + t + ".png"));
                String outputName = "bulk/Jump/pixelated" + t + ".png";
                int width = image.getWidth();
                int height = image.getHeight();
                int pixelationStrength = 8;

                // Retrieve pixel info and store in 'pixels' variable
//                PixelGrabber pgb = new PixelGrabber(image, 0, 0, width, height, pixels, 0, width);
//                pgb.grabPixels();
    //
//                RGB[][] pixelsRGB = new RGB[height][width];
//                for (int i = 0; i < height; i++) {
//                    for (int j = 0; j < width; j++) {
//                        ColorModel pix = image.getColorModel();
//                        int pixelNumber = i * width + j;
//                        int red = (pixels[pixelNumber] >> 16) & 0xff;
//                        int green = (pixels[pixelNumber] >> 8) & 0xff;
//                        int blue = (pixels[pixelNumber]) & 0xff;
//                        pixelsRGB[i][j] = new RGB(red, green, blue);
//                    }
//                }
                
                BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                for(int i = 0; i < width / pixelationStrength; i++) {
                	for(int j = 0; j < height / pixelationStrength; j++) {
                		int left = i * pixelationStrength;
                		int top = j * pixelationStrength;
                		
//                		int totalR = 0, totalG = 0, totalB = 0;
                		int totalPixels = 0;
                		ArrayList<ColorRanked> list = new ArrayList<>();
                		for(int k = left; k < left + pixelationStrength; k++) {
                			for(int l = top; l < top + pixelationStrength; l++) {
                				if(k < width && l < height) {
                					int a = (image.getRGB(k, l) & 0xff000000) >> 24;
                					if(a != 0x00) {
                						totalPixels++;
                						int r = (image.getRGB(k, l) >> 16) & 0xff;
                						int g = (image.getRGB(k, l) >> 8) & 0xff;
                						int b = image.getRGB(k, l) & 0xff;
                						list.add(new ColorRanked(r, g, b));
                					}
                				}
                				
                			}
                		}
                		
                		if(totalPixels > 0) {
//                			int r = totalR / totalPixels;
//                    		int g = totalG / totalPixels;
//                    		int b = totalB / totalPixels;
                			int bestIndex = 0;
                			for(int k = 0; k < list.size(); k++) {
                				for(int l = 0; l < list.size(); l++) {
                					list.get(k).totalError += (list.get(k).r - list.get(l).r) * (list.get(k).r - list.get(l).r);
                					list.get(k).totalError += (list.get(k).g - list.get(l).g) * (list.get(k).g - list.get(l).g);
                					list.get(k).totalError += (list.get(k).b - list.get(l).b) * (list.get(k).b - list.get(l).b);
//                					list.get(k).totalError += (list.get(k).r - list.get(l).r);
//                					list.get(k).totalError += (list.get(k).g - list.get(l).g);
//                					list.get(k).totalError += (list.get(k).b - list.get(l).b);
                				}
                			}
                			
                			for(int k = 1; k < list.size(); k++) {
                				if(list.get(k).totalError < list.get(bestIndex).totalError) {
                					if(list.get(k).r >= 10 && list.get(k).g >= 10 && list.get(k).b >= 10) {
                						bestIndex = k;
                					}
                				}
                			}
                    		
//                    		bufferedImage.getGraphics().setColor(Color.black);
//                    		bufferedImage.getGraphics().fillRect(left, top, 16, 16);
                    		
//                    		if(list.get(bestIndex).r >= 10 || list.get(bestIndex).g >= 10 || list.get(bestIndex).b >= 10) {
//                    			
//                    		}
                    		
                    		System.out.println(list.get(bestIndex).r + " " + list.get(bestIndex).g + " " + list.get(bestIndex).b);
                			Graphics2D graph = bufferedImage.createGraphics();
                            graph.setColor(new Color(list.get(bestIndex).r, list.get(bestIndex).g, list.get(bestIndex).b));
                            graph.fill(new Rectangle(left, top, pixelationStrength, pixelationStrength));
                            graph.dispose();
                    		
                		}
                		
                	}
                }
                
                ImageIO.write(bufferedImage, "png", new File(outputName));
        	}
        } catch (Exception exc) {
            System.out.println("Interrupted: " + exc.getMessage());
            exc.printStackTrace();
        }
    }
}
