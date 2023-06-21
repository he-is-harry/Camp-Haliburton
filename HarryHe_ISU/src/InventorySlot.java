/*
 * Class Name: InventorySlot
 * Description: The InventorySlot class allows the inventory to be comprised of slots
 * which will store common items. This allows the inventory to be more simplified
 * and cleaner.
 */

// Importing the necessary classes so that the Inventory slot can be rendered
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

public class InventorySlot {
	// Instance Variables
	// The HeldItem item stores the item that is stored in the inventory slot
	private HeldItem item;
	// The integer itemCount stores the number of the item that is stored in the slot
	private int itemCount;
	// The double x and y store the center x and y position of the inventory slot
	private double x, y;
	// The BufferedImage slotBackRegular, slotBackSelected, slotBackBurdened stores the slot 
	// background of a regular inventory slot, when the inventory slot is selected and
	// when the inventory is over burdened.
	private static BufferedImage slotBackRegular, slotBackSelected, slotBackBurdened;
	// The double textWidth stores the width of the text used to render in the item count
	private double textWidth;
	// The boolean changedItemCount stores if the item count has been changed.
	// This is used when determining if the text width has to be recalculated
	private boolean changedItemCount;
	
	// Static Variables
	// The double factShift determines how far into the corner the text
	// should be shifted based on a factor of the width / height
	// This determines the corner where the right bound of the text should be
	private static final double factShift = 0.725;
	// The Font countFont stores the font that is used to render in the item count of the game
	private static Font countFont;
	
	// The final integer width stores the width of the inventory slot
	private static final int width = 80;
	// The final integer height stores the height of the inventory slot
	private static final int height = 80;
	// The Color white stores a white color that is used when rendering the item count
	private static final Color white = new Color(255, 255, 255);
	
	// Method Description: The constructor for the InventorySlot class declares a new inventory
	// slot in the player's inventory. It sets up all of the instance variables of the inventory slot
	// declaring the type of item that is stored, the position of the slot, the number of items
	// and the variables used to render in the item count.
	// Parameters: The double y stores the center y position where the inventory slot should
	// be rendered, and the String itemType stores the type of the item that should be stored in the inventory
	// slot.
	public InventorySlot(double y, String itemType) {
		// Declaring in all the instance variables
		// The y position is set to the y position passed in
		this.y = y;
		// The item that is held in the inventory slot is set up, with the item type passed in
		// and the position declared
		item = new HeldItem(x, y, itemType);
		// The itemCount is declared as 1 as 1 item should be stored in the slot initially
		itemCount = 1;
		// The textWidth is set to an arbitrary value as the item count will not be rendered for 1 item
		textWidth = -1;
		// The item count has not been changed yet so it is set to false
		changedItemCount = false;
		
		// Check if the images need to be loaded in, here the regular only needs to be
		// checked because the selected and regular will be loaded in together
		if(slotBackRegular == null) {
			// If the images have not been loaded, load in all the images
			try {
				slotBackRegular = ImageIO.read(new File("res/Inventory/SlotBackRegular.png"));
				slotBackSelected = ImageIO.read(new File("res/Inventory/SlotBackSelected.png"));
				slotBackBurdened = ImageIO.read(new File("res/Inventory/SlotBackBurdened.png"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Check if the font has to be loaded in, if it does have to be loaded, load it in
		if(countFont == null) {
			try {
				// Create the font used to render the count of the items in the inventory slot at the given path, 
				// and use the regular plain style of the font as the fonts loaded in only have one style.
				countFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/RegularFont.ttf")).deriveFont(Font.PLAIN, width / 4);
			} catch (FileNotFoundException e) {
				// The font file was not found so the program tells the user
				// and the location (in the code) of the error.
				System.out.println("Error 404: Font File Not Found");
				e.printStackTrace();
			} catch (FontFormatException e) {
				// The font file could not be formatted so the program tells the user
				// and the location (in the code) of the error.
				System.out.println("Error 406: Font Format Not Valid");
				e.printStackTrace();
			} catch (IOException e) {
				// There was a critical error in reading the font so the program 
				// tells the user and the location (in the code) of the error.
				System.out.println("Error 405: Inputting Error");
				e.printStackTrace();
			}
		}
	}
	
	// Method Description: The render method will render the inventory slot onto the screen. The background
	// of the inventory slot does depend on if the slot is selected and if the player overBurdened.
	// Parameters: The Graphics g stores the graphics used to render onto the application. The boolean
	// selected stores if the inventory slot was selected and the boolean overBurdened stores if the 
	// player's inventory is over burdened.
	public void render(Graphics g, boolean selected, boolean overBurdened) {
		// The back portion of the inventory slot is rendered depending on the state of the inventory
		// of the user.
		if(selected) {
			// Selected slots take greatest precedence when rendering as the player has to know which
			// slot is currently selected
			g.drawImage(slotBackSelected, (int)(x - width / 2), (int)(y - height / 2), null);
		} else if(overBurdened) {
			// If the player is over burdened then the slots will appear red to signify that
			g.drawImage(slotBackBurdened, (int)(x - width / 2), (int)(y - height / 2), null);
		} else {
			// Otherwise, the regular back portion is rendered in
			g.drawImage(slotBackRegular, (int)(x - width / 2), (int)(y - height / 2), null);
		}
		
		// The item stored in the inventory slot is rendered
		item.render(g);
		
		// If the item count is greater than one, then the slot has to signify the item count, so
		// the item count is drawn onto the bottom right corner of the slot.
		if(itemCount > 1) {
			g.setColor(white);
			g.setFont(countFont);
			if(changedItemCount) {
				calculateTextWidth(g);
			}
			g.drawString(Integer.toString(itemCount), (int)(x + factShift * width / 2 - textWidth), (int)(y + factShift * height / 2));
		}
	}
	
	// Method Description: The calculatedTextWidth method will calculate the width of the text
	// used to render in the item counts.
	// Parameters: The Graphics g stores the graphics used to render the text onto the screen
	public void calculateTextWidth(Graphics g) {
		// Local Variables
		// The FontMetrics fm stores the font metrics of the font used to render in the
		// item count, which allows the bounds of the text to be gotten
		FontMetrics fm = g.getFontMetrics();
		// The Rectangle2D rect stores the rectangular bounds of the String text on the screen
		Rectangle2D rect = fm.getStringBounds(Integer.toString(itemCount), g);
		// Method Body
		// The textWidth is gotten from the bounds of the text
		textWidth = rect.getWidth();
	}
	
	// Method Description: The increaseItemCount method will increase the item count of the current
	// slot instance and set the program to update the String that displays the item count.
	public void increaseItemCount() {
		// The item count is increased and the program is prompted to change the item display
		itemCount++;
		changedItemCount = true;
	}
	
	// Method Description: The decreaseItemCount method will decrease the item count of the current
	// slot instance and set the program to update the String that displays the item count.
	public void decreaseItemCount() {
		// The item count is decreased and the program is prompted to change the item display
		itemCount--;
		changedItemCount = true;
	}
	
	// Getter Methods
	public static int getWidth() {
		return width;
	}
	
	public static int getHeight() {
		return height;
	}
	
	public String getItemType() {
		return item.getType();
	}
	
	public int getItemCount() {
		return itemCount;
	}
	
	// Setter Methods
	public void setX(double x) {
		this.x = x;
		item.setX(x);
	}
}
