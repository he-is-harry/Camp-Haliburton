/*
 * Class Name: EnvironmentItem
 * Description: The EnvironmentItem class represents an item that is in the map of the game.
 * The class extends the abstract class Item so it will always contain the type of the item
 * that is in the map. EnvironmentItems are capable of falling down from a certain height
 * to make the items put into the game more realistic.
 */

// Importing the necessary classes so that the item can be rendered onto the map
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

// The EnvironmentItem class extends the Item class so that it can represent an item in the game
public class EnvironmentItem extends Item {
	// The double relX stores the center x position of the EnvironmentItem used when rendering
	private double relX;
	// The double x stores the center x position of the item relative to the true map of the game
	// The double y stores the bottom y position of the item
	private double x, y;
	// The double velY stores the velocity of the player in the y component. Items can only move
	// in the y direction so this is the only component that is necessary.
	private double velY;
	// The boolean onGround stores if the item is on the ground and is no longer falling
	private boolean onGround;
	
	// The double dropAccel stores the acceleration downwards of the item when it is falling
	private double dropAccel;
	// The double velBounce stores the amount of velocity that should be conserved when
	// the item falls and bounces back up from the ground
	private double velBounce;
	// The double adjustedGround stores the ground height of the specific item type, as some items
	// look better when they are a few pixel below the ground
	private double adjustedGround;
	
	// The Plant parentPlant stores the Plant that dropped the item so that the plant's drops
	// can be decreased once the item is picked up so that the plant can drop more items.
	private Plant parentPlant;
	
	// Method Description: This constructor for the EnvironmentItem class is used when a plant
	// drops an item, taking in the position, the type of the item, and the parent plant and setting
	// up the instance variables depending on the values that are passed in.
	// Parameters: The double x stores the center x position of the item, the double y stores the 
	// bottom y position of the item, the String type stores the type of the item, and the Plant
	// parentPlant stores the plant which dropped the item.
	public EnvironmentItem(double x, double y, String type, Plant parentPlant) {
		// The item type of the item is declared in the superclass's constructor
		super(type);
		// The x and y values are set to the values passed in
		this.x = x;
		this.y = y;
		// The relX is computed based on the shift of the parent plant. This has to be computed
		// here because items can be added at any point in the game, and the item will miss
		// the initial setting of the relative x's.
		relX = x + (parentPlant.getRelX() - parentPlant.getX());
		// The velocity is initally set to be 0
		velY = 0;
		// The parent plant is set to the Plant that is passed in
		this.parentPlant = parentPlant;
		
		// The type of the item is processed to declare the image, adjusted ground, drop acceleration, 
		// and factor of velocity that is conserved when the item bounces on the ground
		processType();
		
		// If the item was put on the ground, then it is marked as being on the ground already
		// but if it is above the ground's height it is marked as not being on the ground.
		if(y < Game.HEIGHT - adjustedGround) {
			onGround = false;
		} else {
			onGround = true;
		}
	}
	
	// Method Description: This constructor for the EnvironmentItem class is used when the player drops
	// an item, where there is no parent plant. It takes in takes in the position, the type of the item, 
	// and the shift quantity of the relative x from the true x position and sets up the instance variables 
	// depending on the values that are passed in.
	// Parameters: The double x stores the center x position of the item, the double y stores the 
	// bottom y position of the item, the String type stores the type of the item, and the double
	// shiftX stores the shift that has to be added to the x value to get the relative x position.
	public EnvironmentItem(double x, double y, String type, double shiftX) {
		// The item type of the item is declared in the superclass's constructor
		super(type);
		// The x and y values are set to the values passed in
		this.x = x;
		this.y = y;
		// The relX is computed based on the shift passed into the method
		relX = x + shiftX;
		// The velocity is initally set to be 0
		velY = 0;
		// There is no parent plant for the item so it is set to null
		this.parentPlant = null;
		
		// If the item was put on the ground, then it is marked as being on the ground already
		// but if it is above the ground's height it is marked as not being on the ground.
		if(y < Game.HEIGHT - adjustedGround) {
			onGround = false;
		} else {
			onGround = true;
		}
		
		// The type of the item is processed to declare the image, adjusted ground, drop acceleration, 
		// and factor of velocity that is conserved when the item bounces on the ground
		processType();
	}
	
	// Method Description: The tick method will update the item by changing its velocity
	// based on its acceleration if the item is falling down. If the item hits the ground,
	// then the item will be able to bounce back up until its velocity becomes too
	// low, then it becomes stabilized on the ground.
	public void tick() {
		if(!onGround) {
			// If the item is not on the ground, the velocity will add on the acceleration
			// and the y position is updated, so that the item will fall.
			velY += dropAccel;
			y += velY;
		}
		
		// It is checked if the item has hit the ground
		if(y >= Game.HEIGHT - adjustedGround) {
			if(velY < dropAccel / 10) {
				// If the velocity is too low, then the velocity will be set to 0 and the
				// item will be set to stay on the ground
				velY = 0;
				onGround = true;
				y = Game.HEIGHT - adjustedGround;
			} else {
				// Otherwise, the velocity will be reduced based on the factor of the velocity
				// which should be kept.
				velY = -velY * velBounce;
				// The y position is set to right at the surface of the ground so that once
				// the item's velocity is added, the item will not be recognized
				// as on the ground again so that the velocity is not reduced very quickly
				y = Game.HEIGHT - adjustedGround;
			}
		}
	}
	
	// Method Description: The render method will draw in the item into the game
	// Parameters: The Graphics g stores the graphics of the game which is used to draw
	// in the item into the game.
	public void render(Graphics g) {
		// The image of the item is drawn onto the game
		g.drawImage(image, (int)(relX - image.getWidth() / 2), (int)(y - image.getHeight()), null);
	}

	// Method Description: The processType method completes the method set out by the Item class
	// by initializing the characteristics of the item based on type. The method will declare
	// the image used to render the item, its adjusted ground height, drop acceleration and 
	// factor of velocity that is conserved when bounced onto the ground.
	public void processType() {
		// Local Variables
		// The String imagePath stores the path that is used to render in the image of the item.
		// It is initially set to a blank String to represent an unknown item
		String imagePath = "";
		
		// Method Body
		// The image path, adjustedGround, dropAccel and velBounce are declared
		// for different item types. Essentially here all the characteristics of the 
		// items which relate to their behavior around the map is set up.
		if(type.equals("Stick")) {
			imagePath = "res/Environment/Stick.png";
			// Ground height is slightly reduced for sticks so that they can
			// appear to be more soundly in the ground, this is also true
			// for many other items as you can see by the groundHeight - some value.
			adjustedGround = Game.groundHeight - 5;
			dropAccel = 0.8;
			velBounce = 0.3;
		} else if(type.equals("Leaf")) {
			imagePath = "res/Environment/Leaf.png";
			adjustedGround = Game.groundHeight - 1;
			dropAccel = 0.03;
			velBounce = 0.06;
		} else if(type.equals("Log")) {
			imagePath = "res/Environment/Log.png";
			adjustedGround = Game.groundHeight - 1;
			dropAccel = 1;
			velBounce = 0.2;
		} else if(type.equals("Birch Bark")) {
			imagePath = "res/Environment/BirchBark.png";
			adjustedGround = Game.groundHeight - 1;
			dropAccel = 0.2;
			velBounce = 0.05;
		} else if(type.equals("Flint And Steel")) {
			imagePath = "res/Environment/FlintAndSteel.png";
			adjustedGround = Game.groundHeight;
			dropAccel = 0.8;
			velBounce = 0.1;
		} else if(type.equals("Cotton Ball")) {
			imagePath = "res/Environment/CottonBall.png";
			adjustedGround = Game.groundHeight - 1;
			dropAccel = 0.1;
			velBounce = 0.08;
		} else if(type.equals("Racket")) {
			imagePath = "res/Environment/Racket.png";
			adjustedGround = Game.groundHeight - 1;
			dropAccel = 0.6;
			velBounce = 0.25;
		} else if(type.equals("Fish Carcass")) {
			imagePath = "res/Environment/FishCarcass.png";
			adjustedGround = Game.groundHeight - 3;
			dropAccel = 0.8;
			velBounce = 0.05;
		}
		
		// Load in the image
		if(imagePath.equals("")) {
			// If the item type was not found, meaning that an image path
			// could not be found for it, then it is printed out that the
			// item was not found. This should be impossible however, as items
			// are only added internally with defined types.
			System.out.println("Item type not found: " + type);
		} else {
			// The image is loaded in based on the image path declared by its type
			try {
				image = ImageIO.read(new File(imagePath));
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
		}
	}
	
	// Getter Methods
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}

	public int getWidth() {
		return image.getWidth();
	}
	
	public Plant getParentPlant() {
		return parentPlant;
	}

	// Setter Methods
	public double getRelX() {
		return relX;
	}

	public void setRelX(double relX) {
		this.relX = relX;
	}
	
}
