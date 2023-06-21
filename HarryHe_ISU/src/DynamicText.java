/*
 * Class Name: DynamicText
 * Description: The DynamicText class allows the wrapped text in the dialogue of Scouters.
 * The class stores a String of text that should be rendered in at the textX and textY
 * position of the class in a certain font, and can sometimes be purple to highlight information.
 * The class mainly holds an important method makeTextGroup which will generate an ArrayList
 * of DynamicText that can be rendered as dialogue.
 */

// Importing the necessary classes for the DynamicText class to work
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class DynamicText {
	// Instance Variables
	// The String text is the text that should be rendered in this dynamic text instance
	private String text;
	// The double textX and textY store the x and y location of the upper left corner of the text
	// The double height stores the height of the text in pixels
	private double textX, textY, height;
	// The double relTextX stores the relative x location of the upper left corner of the text
	// which is used when rendering in the text in the game
	private double relTextX;
	// The boolean isPurple stores if the text stored in this DynamicText instance should be purple
	// or otherwise that is should be black.
	private boolean isPurple;

	// Static Variables
	// The Font textFont stores the font that should be used when rendering text in the dialogue box
	private static Font textFont;
	// The static final integer fontSize stores the size of the text that should be rendered
	// in speech box.
	private static final int fontSize = 14;
	// The FontMetrics variable fm is used to get the information about the Font used in this
	// class which is used to render dialogue, to get the width of text and wrap it.
	private static FontMetrics fm;
	// The Colors black and purple store the colors that are used to render black and purple
	// text respectively.
	private static final Color black = new Color(0, 0, 0), purple = new Color(185, 25, 252);

	// Method Description: The constructor for DynamicText takes in the information
	// of the DynamicText and will set the instance variables to the variables passed in.
	// Parameters: The String text is the text that the DynamicText should render, the
	// textX and textY store the x and y of the upper left corner of the text,
	// the double height stores the height of the text in pixels, and the boolean isPurple
	// stores if the text should be purple or not.
	public DynamicText(String text, double textX, double textY, double height, boolean isPurple) {
		// Set all the instance variables to the values passed in
		this.text = text;
		this.textX = textX;
		this.textY = textY;
		this.height = height;
		this.isPurple = isPurple;
		
		// Check if the font has to be loaded in, if it does have to be loaded, load it in
		if(textFont == null) {
			try {
				// Create the text font at the given path, and use the regular plain style
				// of the font as the fonts loaded in only have one style.
				textFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/RegularFont.ttf")).deriveFont(Font.PLAIN, fontSize);
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
	
	// Method Description: The render method will render the DynamicText onto
	// the graphics component passed in.
	// Parameters: The Graphics g stores the graphics of the component that
	// the text should be rendered on.
	public void render(Graphics g) {
		// The text uses the font that is used for all DynamicText
		g.setFont(textFont);
		// The text is rendered in with the color that it was set to be
		if(isPurple) {
			g.setColor(purple);
		} else {
			g.setColor(black);
		}
		g.drawString(text, (int)relTextX, (int)textY);
	}
	
	// Method Description: The initMetrics method will initialize the FontMetrics used to get
	// the size of text later on in the makeTextGroup method.
	// Parameters: The Graphics g stores the graphics that is used to render the game
	public static void initMetrics(Graphics g) {
		// The font is set to the regular text font and the metrics are gotten from
		// the graphics variable to get information about text rendered with the regular font
		g.setFont(textFont);
		fm = g.getFontMetrics();
	}
	
	// Method Description: The method makeTextGroup will take in an String of text and process 
	// it into an ArrayList of DynamicText that can be rendered. This method helps to make wrapped 
	// text for the dialogue box and have different purple highlighted words in the dialogue boxes.
	// Parameters:
	// The Graphics variable g represents the graphics of the component the text has to be drawn on.
	// The double x represents the left x position of the speech box. The double curX is the increased amount 
	// x that text should be shifted from the start of the line, which is also used beyond the value passed in
	// when different colored text has to change on the same line. The double y stores the top y position of the
	// speech box.

	// The String str stores all of the text that should be displayed in the speech box.

	// The integer width stores the width of the speech box. The double bufferWidth is the factor of the width that should be
	// kept blank so that the text does not extend all the way out to the ends. The integer textSpacing stores the increment 
	// text should have when being rendered, so that they are not right underneath each other. The boolean isPurple stores
	// if the first text that is entered is purple, so until there is a double backslash all of the text is purple. The boolean
	// sameLine stores if the text being entered at first is on the same line as some previous text, which is used when adding
	// characters.
	// Return: The method will return an ArrayList of DynamicText that can be rendered that holds the text to be rendered
	// in a speech box.
	public static ArrayList<DynamicText> makeTextGroup(Graphics g, double x, double curX, double y,
			String str, int width, double bufferWidth, int textSpacing, boolean isPurple, boolean sameLine) {
		// Local Variables
		// The ArrayList of DynamicText textGroup is the ArrayList of text that will be returned
		// and will be directly used to render the speech.
		ArrayList<DynamicText> textGroup = new ArrayList<>();
		// The currentY double stores the current y value of the text being added
		double currentY = y;
		// The String currentText stores the current text that has not been added yet.
		// This is added to as the texts are loop through.
		String currentText = "";
		// The integer lastSpace stores the index of the last space in to the
		// text that is being processed.
		int lastSpace = 0;
		// The integer previousEnter stores the last time the text was entered so a
		// new line was started.
		int previousEnter = 0;
		// The Rectangle2D rect is used to get the bounds of the text so that the text can
		// move to the next line when their width exceeds the width passed in.
		Rectangle2D rect;


		// Method Body
		// If the text is on the same line as the previous, the last space is marked as
		// -1 so that text can be entered if it exceeds the length of the line instead
		// of cutting the line, when it thinks that it was one big long word.
		if(sameLine) {
			lastSpace = -1;
		} else {
			lastSpace = 0;
		}

		// The String is looped over and put into the textGroup ArrayList
		// The font of the graphics is set so that the font metrics for the font can
		// be gotten
		g.setFont(textFont);
		// The string is looped over character by character so that the program can
		// check when to enter and make wrapped text
		for (int i = 0; i < str.length(); i++) {
			// The current text is added to and its bounds are gotten
			currentText += str.charAt(i);
			rect = fm.getStringBounds(currentText, g);

			if(str.charAt(i) == '\\') {
				// If there is a backslash the color of the text should toggle between
				// purple and black text
				
				// Backslashes are actually escape characters
				// in this string so they have to be removed from the current text
				currentText = currentText.substring(0, currentText.length() - 1);
				rect = fm.getStringBounds(currentText, g);
				
				if(!isPurple) {
					// Currently black text add those in
					textGroup.add(new DynamicText(currentText, curX + bufferWidth * width + x, currentY, 
							rect.getHeight(), false));
					// The current text is cleared as the text was put in
					currentText = "";
					// The previous enter is marked as the next character as that is the character
					// where the next line starts
					previousEnter = i + 1;
					// Some of the text may not take up the full line, so start the regular
					// text in between in the line.
					curX += rect.getWidth();

					// If two colored texts are on the same line, then the lastSpace can be set as 
					// negative to represent that their was text that was before, so the text 
					// can still be split by space rather than cutting off the word.
					lastSpace = -1;
					// The text is marked to be purple next
					isPurple = true;

					// A DynamicText is added so that the next thing that is added will register as purple
					// This is still efficient as the addOne method takes the last text and merges it with
					// the one added.
					textGroup.add(new DynamicText("", curX + bufferWidth * width + x, currentY, 
							rect.getHeight(), true));
				} else {
					// Currently purple text add those in, similar to that of black text
					textGroup.add(new DynamicText(currentText, curX + bufferWidth * width + x, currentY, 
							rect.getHeight(), true));
					currentText = "";
					previousEnter = i + 1;
					curX += rect.getWidth();
					lastSpace = -1;
					// The text is marked to be black next
					isPurple = false;

					// A DynamicText is added so that the next thing that is added will register as black
					textGroup.add(new DynamicText("", curX + bufferWidth * width + x, currentY, 
							rect.getHeight(), false));
				}
			} else if (str.charAt(i) == ' ') {
				// The lastSpace is set if there was a space
				lastSpace = i;
			} else if(str.charAt(i) == '\n') {
				// If there is a new line put in place, then enter the current line
				if(isPurple) {
					textGroup.add(new DynamicText(currentText, curX + bufferWidth * width + x, currentY, 
							rect.getHeight(), true));
				} else {
					textGroup.add(new DynamicText(currentText, curX + bufferWidth * width + x, currentY, 
							rect.getHeight(), false));
				}
				// The current text is reset, the previous enter is set to the next character as that is where the
				// new line starts, the line is shifted down a row, and the curX is set back to the beginning of the line
				currentText = "";
				previousEnter = i + 1;
				currentY += rect.getHeight() + textSpacing;
				curX = 0;
				// In order for the new line to actually render to the console a blank piece
				// of text is put on the text line.
				if(isPurple) {
					textGroup.add(new DynamicText(currentText, curX + bufferWidth * width + x, currentY, 
							rect.getHeight(), true));
				} else {
					textGroup.add(new DynamicText(currentText, curX + bufferWidth * width + x, currentY, 
							rect.getHeight(), false));
				}
			}
			// The bounds are checked if they go over the width.
			// Subtract a bit extra so that the buffer on both sides can be a bit more
			// balanced as only when the width is exceeded will the pane return the line.
			if (curX + rect.getWidth() > width * (1 - 2 * bufferWidth - 0.01)) {
				// The length of the text has exceeded the pane's width, so the
				// text has to enter in.
				if (lastSpace > previousEnter) {
					// Go to the last space and print out that line, allowing the
					// next words to spill over into the next line.
					// Here the lastSpace - previousEnter is added by 1 so that spaces are included in the same
					// line as text is added character by character, and spaces cannot be removed, or else there
					// would be no space.
					if(isPurple) {
						textGroup.add(new DynamicText(currentText.substring(0, lastSpace - previousEnter + 1), 
								curX + bufferWidth * width + x, currentY, rect.getHeight(), true));
					} else {
						textGroup.add(new DynamicText(currentText.substring(0, lastSpace - previousEnter + 1), 
								curX + bufferWidth * width + x, currentY, rect.getHeight(), false));
					}
					// The text is set to the remaining text, the previous enter is set to be where the next
					// character will be, the current y increases a row, and the current x is set back to the
					// beginning of the line
					currentText = currentText.substring(lastSpace - previousEnter + 1);
					previousEnter = lastSpace + 1;
					currentY += rect.getHeight() + textSpacing;
					curX = 0;
				} else {
					if (lastSpace == -1) {
						// The space was on the current line, so enter the current line
						// and let the line keep going, this is to avoid cutting of
						// words of the same color, allow words of different color to be cut of first
						currentY += rect.getHeight() + textSpacing;
						curX = 0;
						lastSpace = 0;
					} else {
						// If the space was already on the previous line, then simply cut
						// off the String, it is not possible to make it look nicer.
						if(isPurple) {
							textGroup.add(new DynamicText(currentText, curX + bufferWidth * width + x, currentY, 
									rect.getHeight(), true));
						} else {
							textGroup.add(new DynamicText(currentText, curX + bufferWidth * width + x, currentY, 
									rect.getHeight(), false));
						}

						currentText = "";
						previousEnter = i + 1;
						currentY += rect.getHeight() + textSpacing;
						curX = 0;
					}

				}
			}
		}

		// The rest of the text is added in
		if (currentText.length() > 0) {
			rect = fm.getStringBounds(currentText, g);
			if(isPurple) {
				textGroup.add(new DynamicText(currentText, curX + bufferWidth * width + x, currentY, 
						rect.getHeight(), true));
			} else {
				textGroup.add(new DynamicText(currentText, curX + bufferWidth * width + x, currentY, 
						rect.getHeight(), false));
			}
		}
		return textGroup;
	}
	
	// Getter Methods
	public String getText() {
		return text;
	}
	
	public double getX() {
		return textX;
	}
	
	public double getY() {
		return textY;
	}
	
	public double getHeight() {
		return height;
	}
	
	public boolean getIsPurple() {
		return isPurple;
	}
	
	public static Font getTextFont() {
		return textFont;
	}
	
	// Setter Methods
	public void setText(String text) {
		this.text = text;
	}
	
	public void setY(double textY) {
		this.textY = textY;
	}
	
	public void setRelX(double relTextX) {
		this.relTextX = relTextX;
	}
}
