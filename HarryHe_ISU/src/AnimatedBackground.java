/*
 * Class Name: AnimatedBackground
 * Description: The AnimatedBackground class allows the game to have an animated background
 * where the trees will sway in the background. The trees will randomly get a new sway position 
 * every second.
 */

// Importing the necessary classes so that the class can render in the background
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

public class AnimatedBackground {
	// Static Variables
	// These variables are static as there should only ever be one background
	
	// The integer curImage stores the index of the image used as the background
	private static int curImage;
	// The integer curSwayWait stores how many more ticks are to be waited
	// until the next sway image is taken
	private static int curSwayWait;
	// The BufferedImage array images stores all the possible sway images of the background
	private static BufferedImage [] images;
	// The final integer swayDelay stores how many ticks have to be waited before
	// the next image is taken. 60 ticks means approximately 1 second is waited
	private static final int swayDelay = 60;
	
	// Method Description: The constructor declares in all of the background images
	// and the current image is set to the centered sway trees, and the ticks to wait
	// until the next image is taken is set up to wait the full swayDelay.
	public AnimatedBackground() {
		// The background images are loaded in
		try {
			images = new BufferedImage[5];
			images[0] = ImageIO.read(new File("res/Background/BackgroundImageLeft2.png"));
			images[1] = ImageIO.read(new File("res/Background/BackgroundImageLeft1.png"));
			images[2] = ImageIO.read(new File("res/Background/BackgroundImage.png"));
			images[3] = ImageIO.read(new File("res/Background/BackgroundImageRight1.png"));
			images[4] = ImageIO.read(new File("res/Background/BackgroundImageRight2.png"));
		} catch (FileNotFoundException e) {
			// The image file was not found so the program tells the user
			// and the location (in the code) of the error.
			System.out.println("Error 404: Image File Not Found");
			e.printStackTrace();
		} catch (IOException e) {
			// If there was a critical error in reading the image file, then the user
			// is told and the location (in the code) of the error is printed.
			System.out.println("Error 405: Inputting Error");
			e.printStackTrace();
		}
		// The current image is set to the middle image, so the trees do not sway a certain direction
		curImage = 2;
		// The tick counter is set to 0, so that the swayDelay has to be waited before the next image
		// is chosen. Here the wait is ticked upwards until it reaches the sway delay
		curSwayWait = 0;
	}
	
	// Method Description: The tick method updates the current background, either decreasing the delay
	// to get to the next image or choosing the next image.
	public void tick() {
		if(curSwayWait < swayDelay) {
			// The there is still time that the current image has to be rendered for, the wait
			// counter is incremented
			curSwayWait++;
		} else {
			// If the next image has to be taken, there is a 1 / 3 chance that the current image
			// will sway back left if it is possible, 1 / 3 chance it sways right if it is possible
			// and a 1 / 3 chance that it stays the same
			if(Math.random() < 0.333 && curImage > 0) {
				curImage--;
			} else if(Math.random() >= 0.667 && curImage < images.length - 1) {
				curImage++;
			}
			// The current wait is set back to zero so that the swayDelay number of ticks has to
			// be passed before the next image is taken
			curSwayWait = 0;
		}
	}
	
	// Method Description: The render method will render in the current background onto the game
	// Parameters: The Graphics g stores the graphics of the game where the background should be
	// rendered.
	public void render(Graphics g) {
		// The background is drawn onto the screen
		g.drawImage(images[curImage], 0, 0, null);
	}
}
