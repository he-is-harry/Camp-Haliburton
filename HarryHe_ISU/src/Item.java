/*
 * Class Name: Item
 * Description: The Item class is an abstract class to store important values about
 * the items in the game and declares a frame work so that all items will have common
 * properties, like a type and an image.
 */

// Importing the necessary class so that every Item can have an image
import java.awt.image.BufferedImage;

public abstract class Item {
	/*
	 * The String type defines what type of item the object is, which limits
	 * the use that the item has in the game and how it behaves in the game itself
	 * Here is a list of all the possible items
	 * - Stick
	 * - Leaf
	 * - Log
	 * - Birch Bark
	 * - Flint And Steel
	 * - Cotton Ball
	 * - Racket
	 */
	protected String type;
	// The BufferedImage image stores the image that is used to render in the type, note
	// that in EnvironmentItems and HeldItems, the image used is different even for the same
	// type.
	protected BufferedImage image;
	
	// Method Description: The constructor for the Item class will declare the type of the item
	// to the value that is passed in.
	// Parameters: The String type stores the type of the item, which should only be the items that
	// are listed above in the list.
	public Item(String type) {
		// The type of the current Item instance is set to the type passed in
		this.type = type;
	}
	
	// Method Description: The processType method is a method that is common for all Item subclasses
	// as for every type, the class should have to declare in that item's image and other properties.
	public abstract void processType();
	
	// Method Description: The getItemBurden method is a static final method for the item class used
	// as a method to get the constant burden quantities of every item type. This method keeps
	// all of the burdens on the player that an item type will have. See the Player class for more
	// information on what burden represents.
	// Parameters: The String type stores the type of the item
	// Return: The method will return the constant burden of the item type
	public static final int getItemBurden(String type) {
		// The burdens for every type is returned, as every item can be picked up by the player
		// which should put a burden onto their inventory
		if(type.equals("Stick")) {
			return 5;
		} else if(type.equals("Leaf")) {
			return 2;
		} else if(type.equals("Log")) {
			return 28;
		} else if(type.equals("Birch Bark")) {
			return 2;
		} else if(type.equals("Flint And Steel")) {
			return 4;
		} else if(type.equals("Cotton Ball")) {
			return 1;
		} else if(type.equals("Racket")) {
			return 4;
		} else if(type.equals("Fish Carcass")) {
			return 50;
		}
		// If the type is not found, which should never occur, the item has 0 burden
		return 0;
	}
	
	// Method Description: The campfireValid method returns if a certain item type can be added to a fire,
	// essentially if the item can be burned. The method however does not declare if the item is tinder, kindling
	// or fuel.
	// Parameters: The String type stores the type of the item
	// Return: The method will return if the item can be added to the campfire
	public static final boolean campfireValid(String type) {
		// If the item is in a certain set of types, then it can be added to the fire
		if(type.equals("Stick") || type.equals("Leaf") || type.equals("Log") || type.equals("Birch Bark") || type.equals("Cotton Ball")) {
			return true;
		}
		// Otherwise, it cannot be added to the campfire
		return false;
	}
	
	// Getter Methods
	public String getType() {
		return type;
	}
}
