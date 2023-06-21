/*
 * Class Name: Button
 * Description: The Button class is used to easily create and render buttons that
 * the user can press in the menu of the game. These buttons are able to check
 * if the cursor is contained in the button, which makes checking if the button
 * was clicked easier.
 */

// Importing the necessary classes so that the Button can be rendered onto 
// the menu and have a custom image
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

public class Button {
	// Instance Variables
	// The integers x and y store the middle x and y of the button
	protected int x, y;
	// The boolean highlighted stores if the button is currently being
	// hovered over and should be highlighted
	protected boolean highlighted;
	// The String text stores the text that the button will display.
	protected String text;
	// The integers textX and textY store the x and y position of the left
	// corner of the text in the button
	protected int textX, textY;
	// The integers width and height store the width and height of the button
	protected int width, height;
	
	// Static Variables
	// The Font buttonFont stores the font that is used to render text onto the button
	private static Font buttonFont;
	// The integer regularFontSize stores the size of the text that is used to render the text
	private static final int regularFontSize = 40;
	// The integer textVerticalShift is the y quantity that is added the textY when rendering the
	// text. This is necessary because the button is not exactly centered in the image
	private static final int textVerticalShift = 2;
	// The BufferedImages regularImage and highlightedImage store the image used as the background
	// of the button when the button is not highlighted and when it is highlighted.
	private static BufferedImage regularImage, highlightedImage;
	// The Color white stores the white color that is used to render the text onto the button
	protected static final Color white = new Color(255, 255, 255);
	
	// Method Description: The constructor for the Button class will take in variables about the 
	// position where the button is placed and the text that should be stored onto the button
	// and declare in the instance variables of the button.
	// Parameters: The integer x stores the center x position where the button should be. The integer
	// y stores the center y position of where the button should be. The String text stores the text that
	// should be displayed on the button.
	public Button(int x, int y, String text) {
		// The instance variables are set to the values that are passed in
		this.x = x;
		this.y = y;
		this.text = text;
		// For regular buttons the width and height is always 400 and 80
		width = 400;
		height = 80;
		// Buttons are set to not be highlighted in the beginning
		highlighted = false;
		
		// The textX and textY position are initially set to -1, so that they can be
		// calculated later in the render method. This can be done because buttons are 
		// always placed in positive locations, because otherwise they would not be 
		// rendered on the screen.
		this.textX = -1;
		this.textY = -1;
		
		// Since the regular image and font are static, they can be loaded in when
		// the first button is declared but then do not have to be loaded in after.
		// If the images have no been loaded in yet then load them in
		if(regularImage == null) {
			try {
				// The images are loaded in
				regularImage = ImageIO.read(new File("res/Menu/Button.png"));
				highlightedImage = ImageIO.read(new File("res/Menu/HighlightedButton.png"));
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
		// If the font has not been loaded in yet then load it in
		if(buttonFont == null) {
			try {
				// Create the button font at the given path, and use the regular plain style
				// of the font as the fonts loaded in only have one style.
				buttonFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Menu/MenuFont.ttf")).deriveFont(Font.PLAIN, regularFontSize);
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
	
	// Method Description: The render method will render the button in the given
	// graphics parameter. This method is called from the Menu's paintComponent
	// method when drawing in the buttons.
	// Parameters: The Graphics variable g is the graphics of the component
	// that the buttons will be rendered on.
	public void render(Graphics g) {
		// If the button is highlighted, it should appear to be highlighted. If the button
		// is not highlighted then it can appear as its regular instance.
		if(highlighted) {
			// The images are center aligned. The reason why the x and y have to be center aligned
			// is because the highlighted and regular image do not have the same width and height, and
			// their center position should be kept constant not their left corner.
			g.drawImage(highlightedImage, x - highlightedImage.getWidth() / 2, y - highlightedImage.getHeight() / 2, null);
		} else {
			g.drawImage(regularImage, x - regularImage.getWidth() / 2, y - regularImage.getHeight() / 2, null);
		}

		// The text of the button is rendered in, with white color and the font that 
		// the button is in.
		g.setColor(white);
		g.setFont(buttonFont);

		if(textX == -1 && textY == -1) {
			// If the text's position has not been set yet, then the correct position
			// for the text will be calculated. This is done only once in the render
			// method so that there can be a graphics instance for the font to refer to.
			calculateTextPos(g);
		}

		// The text of the button is drawn in.
		g.drawString(text, textX, textY);
	}
	
	// Method Description: The calculateTextPos method will calculate the x and y
	// coordinate of the text such that the text is center aligned (x position) and 
	// in the middle of the button (in y position).
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
	
	// Method Description: The checkHover method will take in the x and y position of
	// the mouse and return if the mouse is contained inside the button.
	// Parameters: The doubles mouseX and mouseY store the x and y location of the mouse
	// relative to the JComponent which this button is stored.
	// Return: A boolean value representing if the mouse is contained within the button.
	public boolean checkHover(double mouseX, double mouseY) {
		// The button is assumed to be a rectangle, as the button is pixelated so it
		// can be difficult to determine the true hitbox of the button
		if(mouseX >= x - width / 2 && mouseX <= x + width / 2 && mouseY >= y - height / 2 && mouseY <= y + height / 2) {
			return true;
		}
		return false;
	}
	
	// Setter Methods
	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
	}
}
