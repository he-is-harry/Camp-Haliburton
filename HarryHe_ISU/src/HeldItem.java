/*
 * Class Name: HeldItem
 * Description: The HeldItem class represents the items that are stored in the player's inventory.
 * The class extends the Item class so that it can represent items in the game. The class is used
 * to render in the items in the player's inventory slot more easily so that the InventorySlot
 * class can be more clear about which item it is storing.
 * 
 * This class is different from the EnvironmentItem class in that it renders the images of items
 * as appearing in an inventory slot, not in the map.
 */

// Importing the necessary classes so that the item can be rendered in
import java.awt.Graphics;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

// The HeldItem class extends the Item class so that it can represent items in the game
public class HeldItem extends Item {
	// The doubles x and y here represent the center of the image icon
	// where the item should be rendered
	
	// The reason why the position is not already in the Item class is 
	// that the x and y of the EnvironmentItem and HeldItem classes 
	// represent different things.
	private double x, y;
	
	// Method Description: The constructor for the HeldItem class will take in the position
	// where the item should be rendered and the type of item to initialize the instance
	// variables of the current HeldItem instance.
	// Parameters: The double x stores the center x position where the item should be rendered.
	// The double y stores the center y position where the item should be rendered. The String
	// type stores the type of the item.
	public HeldItem(double x, double y, String type) {
		// The type of the item is declared in the superclass's (Item) constructor
		super(type);
		// The position of the item is set up
		this.x = x;
		this.y = y;
		// The images used to render in the item are gotten
		processType();
	}
	
	// Method Description: The render method will draw in the item onto the game
	// Parameters: The Graphics g stores the graphics of the game where the item image should be 
	// rendered
	public void render(Graphics g) {
		// The item is drawn onto the screen
		g.drawImage(image, (int)(x - image.getWidth() / 2), (int)(y - image.getHeight() / 2), null);
	}
	
	// Method Description: The processType method completes the method set out by the Item class
	// so that the images of the certain item types can be loaded in.
	public void processType() {
		// Local Variables
		// The String imagePath stores the path of the image used to render in the specific item
		// type. It is initially set to blank as the item's image has not been found yet.
		String imagePath = "";

		// Method Body
		// The imagePath is gotten based on the type of the item
		if(type.equals("Stick")) {
			imagePath = "res/Inventory/Stick.png";
		} else if(type.equals("Leaf")) {
			imagePath = "res/Inventory/Leaf.png";
		} else if(type.equals("Log")) {
			imagePath = "res/Inventory/Log.png";
		} else if(type.equals("Birch Bark")) {
			imagePath = "res/Inventory/BirchBark.png";
		} else if(type.equals("Flint And Steel")) {
			imagePath = "res/Inventory/FlintAndSteel.png";
		} else if(type.equals("Cotton Ball")) {
			imagePath = "res/Inventory/CottonBall.png";
		} else if(type.equals("Racket")) {
			imagePath = "res/Inventory/Racket.png";
		} else if(type.equals("Fish Carcass")) {
			imagePath = "res/Inventory/FishCarcass.png";
		}

		// Load in the image
		if(imagePath.equals("")) {
			// If the image path is blank then the item was not found and the user is told.
			// However, this should never occur as the items are only declared in the game
			System.out.println("Item type not found: " + type);
		} else {
			// Otherwise, the image is loaded from the image path
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
	
	// Setter Methods
	public void setX(double x) {
		this.x = x;
	}

}
