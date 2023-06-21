/*
 * Class Name:
 * Description: The HeldButton class is used to create buttons that can be held down
 * in the menu. These buttons are used in the highscore page, where the certain
 * highscore category can be chosen and held down to indicate that the category
 * was selected. The HeldButton class extends the Button class to simplify the code
 * as many things used in the Button class are also in the HeldButton class.
 * 
 * This class is not to be confused with the HeldItem class, held buttons are
 * buttons that stay down, but held items are items that are held by the user.
 */

// Importing the necessary classes so that the HeldButton can be rendered and have
// aligned text
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

// The HeldButton class extends the Button class so that useful methods and variables 
// can be used from the Button class
public class HeldButton extends Button {
	// Instance Variable
	// The boolean selected marks if the button is currently selected and should be held
	// down. This variable is exclusive to held buttons.
	private boolean selected;
	
	// Static Variables
	// The Font heldButtonFont is the font used to render the text in the held button
	private static Font heldButtonFont;
	// The integer regularFontSize stores the size of the font used in the held button
	private static final int regularFontSize = 24;
	// The integer textVerticalShift stores the number of pixels that text has to be
	// shifted down in the button from the center position, as the image is not perfectly
	// centered in the dimensions of the held button.
	private static final int textVerticalShift = 3;
	// The BufferedImage regularImage is the image used for the button when it is not
	// selected or highlighted. The BufferedImage highlightedImage is the image that
	// is used in the button when it is highlighted or selected.
	private static BufferedImage regularImage, highlightedImage;
	
	// Method Description: The constructor for the HeldButton class takes in the position
	// of the button and text that should be rendered onto the button and declare in 
	// the instance variables of the button.
	// Parameters: The integer x stores the center x value of the button. The integer y
	// stores the center y value of the button. The String text stores the text that 
	// should be displayed on the button.
	public HeldButton(int x, int y, String text) {
		// The position and text on the button is declared in the superclass's constructor
		super(x, y, text);
		// The width and height of the held button is different from that of buttons so
		// it is set to the held button's width and height.
		width = 330;
		height = 50;
		// The button is initially set to not be selected
		selected = false;
		
		// If the images have no been loaded in yet then load them in. Since the images
		// are static they can be declared only once when the first button is loaded.
		if(regularImage == null) {
			try {
				// The images are loaded in
				regularImage = ImageIO.read(new File("res/Menu/HeldButton.png"));
				highlightedImage = ImageIO.read(new File("res/Menu/HighlightedHeldButton.png"));
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
		// If the font has not been loaded in yet then load it in. Similar to the images,
		// the button font only needs to be loaded in once since it is static and all
		// the held buttons use that font.
		if(heldButtonFont == null) {
			try {
				// Create the button font at the given path, and use the regular plain style
				// of the font as the fonts loaded in only have one style.
				heldButtonFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/RegularFont.ttf")).deriveFont(Font.PLAIN, regularFontSize);
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
	
	// Method Description: The render method will render the held button in the 
	// given graphics parameter. This method is called from the Menu's 
	// paintComponent method when drawing in the held buttons.
	// Parameters: The Graphics variable g is the graphics of the component
	// that the held buttons will be rendered on.
	public void render(Graphics g) {
		// If the held button is highlighted or selected, it should appear to be highlighted. If the 
		// held button is not highlighted or selected then it can appear as its regular instance.
		if(selected || highlighted) {
			// The images are center aligned. The reason why the x and y have to be center aligned
			// is because the highlighted and regular image do not have the same width and height, and
			// their center position should be kept constant not their left corner.
			g.drawImage(highlightedImage, x - highlightedImage.getWidth() / 2, y - highlightedImage.getHeight() / 2, null);
		} else {
			g.drawImage(regularImage, x - regularImage.getWidth() / 2, y - regularImage.getHeight() / 2, null);
		}

		// The text of the button now has to be rendered in, with white color and the font that 
		// the button is in.
		g.setColor(white);
		g.setFont(heldButtonFont);

		if(textX == -1 && textY == -1) {
			// If the text's position has not been set yet, then the correct position
			// for the text will be calculated. This is done only once in the render
			// method so that there can be a graphics instance for the font to refer to.
			calculateTextPos(g);
		}

		// The text of the held button is drawn in.
		g.drawString(text, textX, textY);
	}
	
	// Method Description: The calculateTextPos method will calculate the x and y
	// coordinate of the text such that the text is center aligned (x position) and 
	// in the middle of the held button (in y position).
	// Parameters: The method will take in a Graphics class, so that it can calculate
	// the bounds that a certain font will have which is the current font set in the graphics
	// instance.
	public void calculateTextPos(Graphics g) {
		// Local Variables
		// The FontMetrics class is used to get the information about the Font used
		// in the Graphics class passed in.
		FontMetrics fm = g.getFontMetrics();
		// The Rectangle2D rect is used to get the space that the text will take up
		// so that some math can be done to center the text.
		Rectangle2D rect = fm.getStringBounds(text, g);
		// The X and Y location of the text is calculated, the text center aligned
		// and in the middle of the button.
		textX = (int)(x - rect.getWidth() / 2);
		// The text will be centered in the y plane in the middle of the button.
		textY = (int)(y - rect.getHeight() / 2 + fm.getAscent()) + textVerticalShift;
	}
	
	// Setter Methods
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
