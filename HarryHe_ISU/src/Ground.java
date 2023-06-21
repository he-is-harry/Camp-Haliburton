/*
 * Class Name: Ground
 * Description: The Ground class represents the ground of the game. The class makes
 * rendering in the ground easier and makes it so that the ground can be repeating
 * in the middle of the map and have docks on the edges of the map. This class actually
 * does not interact with anything in the game, simply just rendering in the ground.
 */

// Importing the necessary classes so that the Ground class can render in the ground
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Ground {
	// Instance Variables
	// The BufferedImage ground stores the regular ground image that is rendered
	// in the middle of the map. This image is repeated in the middle of the map.
	private BufferedImage ground;
	// The BufferedImages dockLeft and dockRight store the image of the dock that
	// is rendered at the edges of the map, facing left and right respectively.
	private BufferedImage dockLeft, dockRight;
	// The double relLeftX stores the x position of the left bound of the map where
	// the ground should be rendered if the ground started at the leftX position.
	// Note that the relLeftX constantly changes depending on the position of the player
	// which is calculated in the Game class.
	private double relLeftX;
	// The double leftX stores the true x position where the ground starts in the map
	// which is actually always set to 0.
	private double leftX;
	
	// Method Description: The constructor for the Ground class will initialize the variables
	// of the ground, which actually do not depend on any values passed in. It initializes
	// the images that are used when rendering the ground and the position of the ground images.
	public Ground() {
		// The images that are used to render in the ground are loaded in
		try {
			ground = ImageIO.read(new File("res/PixelGround.png"));
			dockLeft = ImageIO.read(new File("res/DockLeft.png"));
			dockRight = ImageIO.read(new File("res/DockRight.png"));
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
		
		// The leftX is set to 0 as the ground always starts at x position 0, however
		// the relLeftX value is actually arbitrary, as the correct position where the
		// ground should be rendered is calculated later in the Game class's repositionElements method
		relLeftX = 0;
		leftX = 0;
	}
	
	// Method Description: The render method will draw the ground onto the game. The method
	// will allow the edges of the map to be rendered, but then repeat the ground image when
	// the player is within the middle of the map.
	public void render(Graphics g) {
		if(relLeftX < -7560) {
			// If the relative left x is very low, meaning that the player is very far in the positive x direction
			// they reach the of the map on the right. As such, the dock facing right is rendered to represent that the
			// map has ended.
			g.drawImage(ground, ((int)relLeftX) % Game.WIDTH, Game.HEIGHT - ground.getHeight(), null);
			g.drawImage(dockRight, ((int)relLeftX) % Game.WIDTH + Game.WIDTH, Game.HEIGHT - dockRight.getHeight(), null);
		} else if(relLeftX < 0) {
			// If the relative left x is still to the left of the screen, the player is in the middle of the map
			// and the ground is repeated by rendering the ground onto the left of the screen, then render
			// the ground onto the right of the screen.
			// The position where to put the ground is based on the remainder of the screen that should be
			// rendered to the left and then the ground is put to the right of that remainder
			g.drawImage(ground, ((int)relLeftX) % Game.WIDTH, Game.HEIGHT - ground.getHeight(), null);
			g.drawImage(ground, ((int)relLeftX) % Game.WIDTH + Game.WIDTH, Game.HEIGHT - ground.getHeight(), null);
		} else {
			// If the relative left x is positive, the player has reached the left end of the map and a dock facing
			// left is rendered to represent that the map has ended.
			g.drawImage(ground, ((int)relLeftX), Game.HEIGHT - ground.getHeight(), null);
			g.drawImage(dockLeft, ((int)relLeftX - dockLeft.getWidth()), Game.HEIGHT - dockLeft.getHeight(), null);
		}
	}
	
	// Getter Methods
	public double getX() {
		return leftX;
	}
	
	public double getRelX() {
		return relLeftX;
	}
	
	// Setter Methods
	public void setRelX(double relLeftX) {
		this.relLeftX = relLeftX;
	}
}
