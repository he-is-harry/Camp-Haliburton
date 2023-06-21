/*
 * Class Name: Campfire
 * Description: The Campfire class represents a campfire in the game and will allow the
 * player to add items to the campfire and light it. The class will store information 
 * about how much time the campfire has left which is important feedback to the user when
 * lighting and fueling the fire. The class also contains many helpful methods like
 * convertTicksToTime and addItem that make the campfires easier to render and use.
 */

// Importing the necessary classes so that the campfire can be rendered onto the game
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
import java.util.Random;

import javax.imageio.ImageIO;

public class Campfire {
	// Instance Variables
	// The double relX stores the relative x value of the center of the campfire
	// which is used when rendering in the campfire.
	// Relative x values are determined such that the player is always in the center
	// of the map
	private double relX;
	// The double x stores the center x value of the campfire relative to the true
	// map of the game
	private double x;
	// The boolean lit stores if the campfire is currently lit on fire or not
	private boolean lit;
	
	// The integers tinderTicksLeft, kindlingTicksLeft, and fuelTicksLeft store the number
	// of ticks where the fire can stay lit that remain for the tinder, kindling and fuel 
	// resources put into the fire
	private int tinderTicksLeft, kindlingTicksLeft, fuelTicksLeft;
	// The integer totalTicksLeft stores the total ticks that remain that the fire can keep 
	// burning for all resources put into the fire.
	private int totalTicksLeft;
	// The Strings totalTimeLeft, tinderTimeLeft, kindlingTimeLeft, and fuelTimeLeft store the
	// time in seconds, minutes, and hours that the campfire can burn for, relative to all
	// the resources, the tinder resources, the kindling resources, and the fuel resources
	// in the fire, respectively.
	private String totalTimeLeft, tinderTimeLeft, kindlingTimeLeft, fuelTimeLeft;
	// The integers totalWidth, tinderWidth, kindlingWidth, and fuelWidth stores the size of
	// the text that is used to render in the total time left, tinder time left, kindling time
	// left, and fuel time left. This is used when try to find the size of the menu that
	// should be rendered so that the text can fit into it.
	private int totalWidth, tinderWidth, kindlingWidth, fuelWidth;
	// The boolean changedTicksLeft stores if the ticks that remain for the fire have been
	// changed, meaning that the time that is displayed has to be updated.
	private boolean changedTicksLeft;
	
	// The integer imageIndex stores the current lit image that is used when the fire is
	// burning and the integer imageTicksRem stores the number of ticks that remain
	// where that image is being rendered.
	private int imageIndex, imageTicksRem;
	// The boolean showingSparks stores if the campfire is currently showing sparks or not, which
	// is used when the campfire is initially lit.
	private boolean showingSparks;
	
	// Static Variables
	// The double y stores the bottom y of the campfire, which should be the same for all
	// campfires
	private static final double y = Game.HEIGHT - Game.groundHeight + 5;
	// The integer width stores the width of the campfire, in all of its states.
	private static final int width = 120;
	// The integer avgImageTicks stores the average number of ticks that a certain lit image
	// is rendered. The integer imageTickRange stores the quantity that the number of ticks
	// of an image can deviate, so the maximum number of image ticks should be 130 and 30. Note
	// that because the author used a specific way the number of ticks, the maximum number is 
	// actually 129. 
	// The integer sparkTickAmount stores the number of ticks that the sparking image should be
	// shown.
	private static final int avgImageTicks = 80, imageTickRange = 50, sparkTickAmount = 30;
	// The BufferedImage regularImage stores the image that is used for an unlit fire. The 
	// BufferedImage sparkImage stores the image that is used when the is being sparked.
	private BufferedImage regularImage, sparkImage;
	// The BufferedImage array litImages stores the images that can be used for the lit fire.
	// There are multiple images so that the fire does not always stay constant.
	private static BufferedImage litImages[];
	// The BufferedImages tinderIcon, kindlingIcon, and fuelIcon stores the icons that represent
	// if tinder, kindling, or fuel is added to the campfire, in the campfire menu.
	private static BufferedImage tinderIcon, kindlingIcon, fuelIcon;
	// The BufferedImages tinderIconBlank, kindlingIconBlank, and fuelIconBlank store the icons
	// that represent if tinder, kindling, or fuel is not added to the campfire.
	private static BufferedImage tinderIconBlank, kindlingIconBlank, fuelIconBlank;
	// The Font regularFont stores the font that is used to render text in the campfire menu
	private static Font regularFont;
	// The FontMetrics variable fm is used to get the information about the Font used in the 
	// campfire menu, so that the width of the text can be gotten.
	private static FontMetrics fm;
	
	// The integer menuMinWidth stores the minimum width of the menu rendered above the campfire
	private static final int menuMinWidth = 160;
	// The integer menuHeight stores the height of the menu rendered above the campfire
	private static final int menuHeight = 190;
	// The integer menuVerticalShift stores the vertical shift above the bottom of the campfire
	// that the menu of the campfire should be
	private static final int menuVerticalShift = 100;
	// The Colors black and white stores the colors black and white that are used when rendering
	// in the background of the menu and the text in the menu
	private static final Color black = new Color(0, 0, 0), white = new Color(255, 255, 255);
	
	// Method Description: The constructor for the campfire class will declare in all of the instance
	// variables of the campfire, where the campfire should have no resources added to it. It will set 
	// the center x position of the campfire to the value passed in.
	// Parameters: The double x stores the center x value of the campfire on the map
	public Campfire(double x) {
		// The x value is passed to the x value passed in
		this.x = x;
		// Initially the campfire is set to be unlit, where it should get the first lit
		// image if it is lit, and is not currently showing sparks.
		lit = false;
		imageIndex = 0;
		imageTicksRem = 0;
		showingSparks = false;
		// The campfire is initially set to have nothing added to it
		tinderTicksLeft = 0; 
		kindlingTicksLeft = 0; 
		fuelTicksLeft = 0;
		totalTicksLeft = 0;
		totalTimeLeft = "0 s";
		tinderTimeLeft = "0 s";
		kindlingTimeLeft = "0 s";
		fuelTimeLeft = "0 s";
		// The changedTicksLeft is initially set to true to get the bounds of the Strings in 
		// the render method
		changedTicksLeft = true;
		
		// If the images have not been loaded in yet, then load them in
		if(regularImage == null) {
			try {
				// The images are loaded in
				litImages = new BufferedImage[4];
				litImages[0] = ImageIO.read(new File("res/Campfire/LitCampfireLeftShort.png"));
				litImages[1] = ImageIO.read(new File("res/Campfire/LitCampfireLeftTall.png"));
				litImages[2] = ImageIO.read(new File("res/Campfire/LitCampfireRightShort.png"));
				litImages[3] = ImageIO.read(new File("res/Campfire/LitCampfireRightTall.png"));
				regularImage = ImageIO.read(new File("res/Campfire/UnlitCampfire.png"));
				sparkImage = ImageIO.read(new File("res/Campfire/SparkCampfire.png"));
				tinderIcon = ImageIO.read(new File("res/Campfire/TinderIcon.png"));
				tinderIconBlank = ImageIO.read(new File("res/Campfire/TinderIconGray.png"));
				kindlingIcon = ImageIO.read(new File("res/Campfire/KindlingIcon.png"));
				kindlingIconBlank = ImageIO.read(new File("res/Campfire/KindlingIconGray.png"));
				fuelIcon = ImageIO.read(new File("res/Campfire/FuelIcon.png"));
				fuelIconBlank = ImageIO.read(new File("res/Campfire/FuelIconGray.png"));
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
		
		// Load in fonts
		if(regularFont == null) {
			try {
				// Create the regular text font at the given path, and use the regular plain 
				// style of the font as the fonts loaded in only have one style.
				regularFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/RegularFont.ttf")).deriveFont(Font.PLAIN, 14);
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
	
	// Method Description: The tick method will update the campfire. This method really only
	// updates the images that are used when displaying the campfire, counting down the 
	// ticks left until the image has to be changed.
	public void tick() {
		// Local Variables
		// The Random rand is a variable that is used to generate random numbers more easily for integers
		Random rand = new Random();
		
		// Method Body
		if(showingSparks) {
			// If the campfire is showing sparks, then the image ticks remaining are counted down
			if(imageTicksRem <= 0) {
				// If the number of ticks remaining has run out, then the campfire will no longer
				// show sparks
				// The number of image ticks remaining is also set in case the campfire becomes lit
				// and needs to have some image ticks to show the first image
				showingSparks = false;
				imageTicksRem = avgImageTicks + rand.nextInt(2 * imageTickRange) - imageTickRange;
			} else {
				// If the number of ticks remaining has not run out then they are counted down
				imageTicksRem--;
			}
		} else if(lit) {
			// The number of image ticks remaining is counted down
			if(imageTicksRem <= 0) {
				// If there are no more ticks remaining, then the campfire will change its lit image.
				// Then, the number of image ticks remaining that the lit image will have is randomly generated
				imageIndex = rand.nextInt(litImages.length);
				imageTicksRem = avgImageTicks + rand.nextInt(2 * imageTickRange) - imageTickRange;
			} else {
				// Otherwise, the number of ticks remaining is counted down
				imageTicksRem--;
			}
			
			// The ticks remaining for the campfire's resources are counted down, with tinder ticks
			// removed first, then kindling ticks, and then fuel ticks.
			if(tinderTicksLeft > 0) {
				tinderTicksLeft--;
			} else if(kindlingTicksLeft > 0) {
				kindlingTicksLeft--;
			} else if(fuelTicksLeft > 0) {
				fuelTicksLeft--;
			}
			// The total number of ticks is decremented
			totalTicksLeft--;
			// The game's score of the number of ticks that a campfire was active is increased
			// Since there are two campfires in the game, it is technically more advantageous
			// to try and keep both active at the same time.
			Game.increaseCampfireTime();
			// The number of ticks is set to have been changed
			changedTicksLeft = true;
			// If the number of ticks left reaches zero, then the campfire ran out of resources and
			// it now unlit.
			if(totalTicksLeft <= 0) {
				lit = false;
			}
		}
	}
	
	// Method Description: The render method will render in the campfire onto the Graphics variable
	// passed in. If the player is within the bounds where the campfire should be rendered, the menu
	// is rendered to show information about the campfire to the player.
	// Parameters: The Graphics g stores the graphics of the JComponent where the campfire should be
	// rendered. The boolean inBounds stores if the player is within the bounds where the menu should
	// be rendered.
	public void render(Graphics g, boolean inBounds) {
		if(showingSparks) {
			// Sparking campfires take the greatest priority, so those are rendered first if the campfire
			// is showing sparks
			g.drawImage(sparkImage, (int)(relX - sparkImage.getWidth() / 2), (int)(y - sparkImage.getHeight()), null);
		} else if(lit) {
			// If the campfire is lit, then an image of a lit campfire is rendered
			g.drawImage(litImages[imageIndex], (int)(relX - litImages[imageIndex].getWidth() / 2), (int)(y - litImages[imageIndex].getHeight()), null);
		} else {
			// If the campfire is unlit, then an unlit campfire is rendered
			g.drawImage(regularImage, (int)(relX - regularImage.getWidth() / 2), (int)(y - regularImage.getHeight()), null);
		}
		
		// Check if the player is within the bounds of the campfire, if so then render the menu
		if(inBounds) {
			renderMenu(g);
		}
	}
	
	// Method Description: The renderMenu method will render in the menu that shows the resources that
	// a campfire has. The menu displays the amount of time that the fire has left to burn, in total
	// and with its respective resources.
	// Parameters: The Graphics g stores the graphics of the application where the menu should be rendered
	private void renderMenu(Graphics g) {
		// Local Variables
		// The integer menuTop stores the y position of the top of the menu
		int menuTop = (int)(y - menuHeight - menuVerticalShift);
		// The integer menuWidth stores the width of the menu. It is initially set
		// to the minimum menu width but can increase if the size of the text goes
		// beyond the menu width.
		int menuWidth = menuMinWidth;
		// The integer menuLeft stores the x position of the left bound of the menu
		int menuLeft;
		// The Rectangle2D rect stores the bounds of a String, storing the space that it takes
		// up
		Rectangle2D rect;
		
		// Method Body
		if(changedTicksLeft) {
			// If the number of ticks that remain has changed new Strings have to be computed
			// of the time left
			findTimeLeft();
			// Find widths of all of the text
			rect = fm.getStringBounds(totalTimeLeft, g);
			totalWidth = (int)rect.getWidth();
			rect = fm.getStringBounds(tinderTimeLeft, g);
			tinderWidth = (int)rect.getWidth();
			rect = fm.getStringBounds(kindlingTimeLeft, g);
			kindlingWidth = (int)rect.getWidth();
			rect = fm.getStringBounds(fuelTimeLeft, g);
			fuelWidth = (int)rect.getWidth();
			changedTicksLeft = false;
		}
		// The width of the menu is found here based on the size of the text. It is checked if the
		// String would exceed the width of the menu, if it was placed in its usual position and
		// also including some buffer space, which is 10 pixels. If it exceeds the width of the menu
		// the menu width is increased to make the String fit in.
		// Normally add 10 but add some buffer space, 10
		if(20 + totalWidth > menuWidth) {
			menuWidth = 20 + totalWidth;
		}
		// Normally adds 60
		if(70 + tinderWidth > menuWidth) {
			menuWidth = 70 + tinderWidth;
		}
		if(70 + kindlingWidth > menuWidth) {
			menuWidth = 70 + kindlingWidth;
		}
		if(70 + fuelWidth > menuWidth) {
			menuWidth = 70 + fuelWidth;
		}
		
		// The left bound of the menu is found from the center of the menu and the width of the menu
		menuLeft = (int)(relX - menuWidth / 2);
		
		// Render in the menu
		// Render in the background of the menu
		g.setColor(black);
		g.fillRect(menuLeft, menuTop, menuWidth, menuHeight);
		g.setColor(white);
		g.drawRect(menuLeft, menuTop, menuWidth, menuHeight);
		
		// Render in the total time
		g.setFont(regularFont);
		g.drawString(totalTimeLeft, menuLeft + 10, menuTop + 20);
		
		// Render in the tinder statistics including the icon that indicates if the is that type
		// of resource which is the same for the kindling and fuel
		if(tinderTicksLeft > 0) {
			g.drawImage(tinderIcon, menuLeft + 10, menuTop + 35, null);
		} else {
			g.drawImage(tinderIconBlank, menuLeft + 10, menuTop + 35, null);
		}
		g.drawString("Tinder Time: ", menuLeft + 60, menuTop + 50);
		g.drawString(tinderTimeLeft, menuLeft + 60, menuTop + 70);
		
		// Render in the kindling statistics
		if(kindlingTicksLeft > 0) {
			g.drawImage(kindlingIcon, menuLeft + 10, menuTop + 85, null);
		} else {
			g.drawImage(kindlingIconBlank, menuLeft + 10, menuTop + 85, null);
		}
		g.drawString("Kindling Time: ", menuLeft + 60, menuTop + 100);
		g.drawString(kindlingTimeLeft, menuLeft + 60, menuTop + 120);
		
		// Render in the fuel statistics
		if(fuelTicksLeft > 0) {
			g.drawImage(fuelIcon, menuLeft + 10, menuTop + 135, null);
		} else {
			g.drawImage(fuelIconBlank, menuLeft + 10, menuTop + 135, null);
		}
		g.drawString("Fuel Time: ", menuLeft + 60, menuTop + 150);
		g.drawString(fuelTimeLeft, menuLeft + 60, menuTop + 170);
	}
	
	// Method Description: The addItem method will take in a type of item to add
	// to the fire and update the time that the campfire can burn from the resources.
	// The method also declares the values of the amount of time that items can burn
	// Parameters: The String type stores the type of the item to add to the fire
	public void addItem(String type) {
		// The type of item is checked and the amount of ticks that the campfire can
		// burn is added to depending on the item. Since the amount of ticks is
		// updated, it also marks that the number of ticks left has changed
		if(type.equals("Stick")) {
			// Kindling
			kindlingTicksLeft += 300;
			totalTicksLeft += 300;
			changedTicksLeft = true;
		} else if(type.equals("Leaf")) {
			// Tinder
			tinderTicksLeft += 60;
			totalTicksLeft += 60;
			changedTicksLeft = true;
		} else if(type.equals("Log")) {
			// Fuel
			fuelTicksLeft += 3600;
			totalTicksLeft += 3600;
			changedTicksLeft = true;
		} else if(type.equals("Birch Bark")) {
			// Tinder
			tinderTicksLeft += 120;
			totalTicksLeft += 120;
			changedTicksLeft = true;
		} else if(type.equals("Cotton Ball")) {
			// Tinder
			tinderTicksLeft += 40;
			totalTicksLeft += 40;
			changedTicksLeft = true;
		}
		
		// Manage the quest to add resources to a fire
		if(Scouter.getQuestLine() == 2 && tinderTicksLeft > 0 && kindlingTicksLeft > 0) {
			// If the player is on the 2nd quest line, and adds in the tinder and kindling to a
			// fire, then they can progress onto the next quest line.
			Scouter.nextQuestLine();
		}
	}
	
	// Method Description: The lightFire method will light the campfire if it has enough resources
	// to be lit. It will always make sure to show the sparking of the fire image even if the campfire
	// does not have enough resources to be lit.
	public void lightFire() {
		if(!lit) {
			// If the campfire is not lit already, the campfire will be set to show the sparks image
			showingSparks = true;
			imageTicksRem = sparkTickAmount;
		}
		
		// The fire is attempted to be lit
		if(totalTicksLeft > 0 && tinderTicksLeft > 0 && kindlingTicksLeft > 0) {
			// In order to light the fire there has to be both tinder and kindling
			lit = true;
			
			// If the player is on the 5th quest line, then they have finished the lighting fire
			// objective and will progress in the quest line
			if(Scouter.getQuestLine() == 5) {
				Scouter.nextQuestLine();
			}
		}
	}
	
	// Method Description: The findTimeLeft method will calculate and set the amount of time left
	// that the campfire has for its tinder, kindling, and fuel as well as in total. It will convert the
	// ticks of time left into the amount of time in terms of seconds, minutes and hours.
	private void findTimeLeft() {
		// The Strings that display the amount of time left that the fire can burn are set
		// The total time left also includes the String, "Total Time: " so that its bounds
		// can be calculated including that String.
		totalTimeLeft = "Total Time: " + convertTicksToTime(totalTicksLeft);
		tinderTimeLeft = convertTicksToTime(tinderTicksLeft);
		kindlingTimeLeft = convertTicksToTime(kindlingTicksLeft);
		fuelTimeLeft = convertTicksToTime(fuelTicksLeft);
	}
	
	// Method Description: The convertTicksToTime method will convert a certain number of ticks that 
	// the campfire has burnt or can burn for into time in terms of hours, minutes and seconds.
	// This method is also used to get the highscores in the game so the method is public and static.
	// The method uses the conversion that 60 ticks is equal to 1 second, this is only approximately
	// true, but is a good approximation.
	// Parameters: The integer ticks stores the number of ticks that the campfire can burn for
	// or has burnt for (which is used for the highscores).
	// Return: The method will return a String representation of the number of ticks to stores the time
	// that it will take for those number of ticks to pass.
	public static String convertTicksToTime(int ticks) {
		// Local Variables
		// The String ans stores the String representation of the time of the ticks passed in
		String ans = "";

		// Method Body
		// If the the number of ticks is less than 60 then the time is less than a second so the
		// computer will just render it as 0 seconds
		if(ticks < 60) {
			return "0 sec";
		}
		// The number of hours of the ticks is calculated
		if(ticks >= 216000) {
			// More than an hour remains
			ans += " " + (ticks / 216000) + " hr";
			ticks = ticks % 216000;
		}
		// The number of minutes of the ticks is calculated
		if(ticks >= 3600) {
			// At least a minute remains
			ans += " " + (ticks / 3600) + " min";
			ticks = ticks % 3600;
		}
		// The number of seconds of the ticks is calculated
		if(ticks >= 60) {
			// At least a second remains
			ans += " " + (ticks / 60) + " sec";
			ticks = ticks % 60;
		}
		// The answer is returned and trimed so that the leading space
		// is removed
		return ans.trim();
	}
	
	// Method Description: The static method initMetrics will initialize the FontMetrics
	// used to get the bounds of the text in the application
	// Parameters: The Graphics g stores the graphics used to render in text in the application
	public static void initMetrics(Graphics g) {
		// The font is set to the font used to render in text so that the bounds of the text
		// gotten can be accurate
		g.setFont(regularFont);
		// The FontMetrics is declared
		fm = g.getFontMetrics();
	}
	
	// Getter Methods
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getRelX() {
		return relX;
	}
	
	public double getWidth() {
		return width;
	}
	
	// Setter Methods
	public void setRelX(double relX) {
		this.relX = relX;
	}
}
