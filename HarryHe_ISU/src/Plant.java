/*
 * Class Name: Plant
 * Description: The Plant class represents a plant in the game which can drop down
 * items onto the map that the player can pick up. The class can also support
 * only decorative images in the game like the porta-potty which do not drop items.
 * The class makes the processing of item dropping easier so that plants can be
 * simply looped over and the tick method called so that it will drop items periodically.
 */

// Importing the necessary classes so that the plant can be rendered onto the game
// and drops can occur at random intervals.
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class Plant {
	// Instance Variables
	// The BufferedImage image stores the image that is used to render the plant onto the game
	private BufferedImage image;
	// The double x stores the center x position of the plant and the double relX stores
	// the relative x position of the plant which is used when rendering the plant.
	private double x, relX;
	// The double y stores the bottom y position of the plant
	private double y;
	// The String type stores the type of the plant of the plant instance
	// There are a select number of types of plants in the game which drop different
	// items
	// 1. Pine Tree
	// 		- Sticks (60%), Leaves (30%), Logs (10%)
	// 2. Birch Tree
	// 		- Sticks (50%), Birch Bark (50%)
	// 3. Cotton Plant
	//		- Cotton Ball (100%)
	// 4. Porta-potty
	//		- No Drops
	private String type;
	
	// The integer dropTick stores the number of ticks that remain before the next
	// item of the plant is dropped
	private int dropTick;
	// The integer avgDropPeriod stores the average number of ticks that have to be
	// waited before the next item is dropped.
	// Note that the average drop period and average drop spread of different plants
	// is different as some plants will drop items more frequently than others.
	private int avgDropPeriod;
	// The integer dropPeriodSpread stores the number of ticks that the drop period
	// can deviate from the avgDropPeriod, so the minimum drop period is
	// avgDropPeriod - dropPeriodSpread and the maximum is 
	// avgDropPeriod + dropPeriodSpread - 1, because of the way that the dropTicks are
	// generated.
	private int dropPeriodSpread;
	// The integer numItems stores the number of items that the current plant has dropped
	private int numItems;
	// The integer maxDrops stores the maximum number of items that a current plant can
	// have on the ground at a time.
	private int maxDrops;
	
	// Static Variables
	// The double dropHeight stores the factor of the height of the plant which items 
	// should drop from
	private static final double dropHeight = 0.7;
	
	// Method Description: The constructor for the Plant class will take information about
	// the plant and declare the instance variables of the plant so that it is ready to
	// be rendered and drop items.
	// Parameters: The double x stores the center x position of the plant. The double y stores
	// the bottom y position of the plant. The String type stores the type of the plant that
	// the current plant instance is. The boolean reflected stores if the plant image should
	// be reflected, which is used so that the plants can look more varied.
	public Plant(double x, double y, String type, boolean reflected) {
		// The position and type of the plant is set
		this.x = x;
		this.y = y;
		this.type = type;
		// The plant is set to drop an item immediately so that the player can have an easier
		// time finding resources at the beginning of the game
		dropTick = 0;
		// The plant has initially dropped any items
		numItems = 0;
		
		// The type of the plant is processed which declares the image, average drop period,
		// drop period spread, and maximum number of items to drop
		processType(reflected);
	}
	
	// Method Description: The tick method will update the plant by dropping items or decreasing
	// the wait time before the next item is dropped.
	public void tick() {
		// Local Variables
		// The Random variable rand is used to generate random integers more easily
		Random rand;
		
		// Method Body
		// If the plant has not reached the maximum number of drops yet or is not a porta-potty
		// then it can drop more items
		if(numItems < maxDrops && !type.equals("Porta-potty")) {
			// It is checked if it is time for the plant to drop and item
			if(dropTick <= 0) {
				// If there are no more ticks remaining until the next drop, then the plant will
				// drop a random item based on the set of items it can drop.
				rand = new Random();
				// The item is added to the game
				Game.addItem(new EnvironmentItem(x + rand.nextInt(image.getWidth()) - image.getWidth() / 2, 
						y - image.getHeight() * dropHeight, getDrop(), this));
				// The number of items that the plant has dropped is increased
				numItems++;
				// The number of ticks left before the next drop is generated
				dropTick = avgDropPeriod + rand.nextInt(2 * dropPeriodSpread) - dropPeriodSpread; 
			} else {
				// Otherwise, there has to be more time before the next drop so the ticks
				// left is decremented
				dropTick--;
			}
		}
	}
	
	// Method Description: The render method will render in the plant onto the game
	// Parameters: The Graphics g stores the graphics of the game where the plant should
	// be rendered
	public void render(Graphics g) {
		// An image of the plant is drawn onto the game. Here the plant is center aligned in the
		// x and the y position is increased to get the top y value of the plant.
		g.drawImage(image, (int)(relX - image.getWidth() / 2), (int)(y - image.getHeight()), null);
	}
	
	// Method Description: The processType method will declare in the instance variables that determine
	// the characteristics of the plant based on the type of the plant, such as its max drops and
	// average drop period.
	// Parameters: The boolean reflected stores if the plant should be reflected or not which
	// helps to make the plants more varied
	public void processType(boolean reflected) {
		// Local Variables
		// The String imagePath stores the path of the image that should be used to render the plant
		// It is initially set to blank as the image of the plant is unknown
		String imagePath = "";
		
		// Method Body
		// The type of the plant is checked and the image, average drop period, drop period spread, 
		// and maximum number of drops is determined based on the type.
		if(type.equals("Pine Tree")) {
			if(reflected) {
				// If the image of the plant is reflected then a reflected version of the image is
				// loaded, which also occurs most of the other plants
				imagePath = "res/Environment/PineTreeReflected.png";
			} else {
				imagePath = "res/Environment/PineTree.png";
			}
			
			// The pine tree plant has to have its y level increased to make the plant a bit lower
			// so that the plant can look more realistic.
			y += 4;
			// The drop period and spread are entered in
			avgDropPeriod = 1350;
			dropPeriodSpread = 420;
			// The maximum number of drop is declared
			maxDrops = 3;
		} else if(type.equals("Birch Tree")) {
			// Image, drop period and max drops are declared similar to the Pine Tree type
			if(reflected) {
				imagePath = "res/Environment/BirchTreeReflected.png";
			} else {
				imagePath = "res/Environment/BirchTree.png";
			}
			avgDropPeriod = 1500;
			dropPeriodSpread = 510;
			maxDrops = 2;
		} else if(type.equals("Cotton Plant")) {
			// Image, drop period and max drops are declared similar to the Pine Tree type
			if(reflected) {
				imagePath = "res/Environment/CottonPlantReflected.png";
			} else {
				imagePath = "res/Environment/CottonPlant.png";
			}
			avgDropPeriod = 4800;
			dropPeriodSpread = 1200;
			maxDrops = 2;
		} else if(type.equals("Porta-potty")) {
			// The Porta-potty is never reflected and does not drop any items
			// so its image path is simply set up
			imagePath = "res/Environment/Porta-potty.png";
		}
		
		// Load in the image
		if(imagePath.equals("")) {
			// If the type of plant was not found then the image path is blank and 
			// an image cannot be loaded in. However, this should never occur as plants
			// are added internally, which makes this really only for debugging purposes
			System.out.println("Plant type not found: " + type);
		} else {
			// Otherwise the image is loaded in from its given path
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
	
	// Method Description: The getDrop method will get a random item type to drop from the plant 
	// which is used when the drop period has been taken up and the next item should be gotten.
	public String getDrop() {
		// Local Variables
		// The Random variable rand is used to more easily get randomly generated integers
		Random rand = new Random();
		// The integer randNum stores a random number generated so that the probabilities that
		// certain items are dropped can be matched with the random number
		int randNum;
		
		if(type.equals("Pine Tree")) {
			// A random object is gotten for the Pine Tree type
			randNum = rand.nextInt(10);
			if(randNum < 6) {
				return "Stick";
			} else if(randNum < 9) {
				return "Leaf";
			} else {
				return "Log";
			}
		} else if(type.equals("Birch Tree")) {
			// A random object is gotten for the Birch Tree type
			randNum = rand.nextInt(2);
			if(randNum < 1) {
				return "Stick";
			} else {
				return "Birch Bark";
			}
		} else if(type.equals("Cotton Plant")) {
			// If the plant is a cotton plant, then it will only drop a cotton ball
			return "Cotton Ball";
		}
		// Otherwise, the type of the plant is not recognized so a blank item type is returned
		return "";
	}
	
	// Getter Methods
	public double getX() {
		return x;
	}
	
	public int getWidth() {
		return image.getWidth();
	}
	
	public double getRelX() {
		return relX;
	}
	
	// Setter Methods
	public void setRelX(double relX) {
		this.relX = relX;
	}
	
	public void decreaseNumItems() {
		numItems--;
	}
}
