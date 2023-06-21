/*
 * Class Name: Mosquito
 * Description: The Mosquito class represents a mosquito in the game. This class will
 * allow the mosquitoes to be moved around the map and attack the player whenever
 * they too near the mosquito. The class holds important methods that allow the 
 * behavior of the mosquito to be handled and stores variables that manage the movement
 * and position of the mosquito.
 * 
 * Mosquitoes will often fly around where they spawn in a certain range until they
 * find the player, where they will try to attack them. Once the mosquito attacks the
 * player they will start to move back to their spawn again for a certain period of time.
 */

// Importing the necessary classes so that the mosquito can be rendered onto the game
// and so that its random movement can be determined
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class Mosquito {
	// The doubles x and y store the center x and y position of the mosquito
	private double x, y;
	// The double relX stores the relative x value to the player that is used when 
	// rendering the mosquito
	private double relX;
	// The doubles velX and velY store the velocity of the mosquito in the x direction
	// and in the y direction.
	private double velX, velY;
	// The doubles sourceX and sourceY store the x and y position where the mosquito was
	// spawned, so that the mosquito will only move around where it spawned, so that they
	// cluster more and behavior can be predicted.
	private double sourceX, sourceY;
	// The double rangeDist stores the distance which the mosquito can fly around from the
	// source until it should return back to its source.
	// The range marks how clustered the mosquito should move around the source and it is random
	// so that mosquitoes will not all be together, there is more natural motion
	private double rangeDist;
	// The integer movementTick stores the tick which the current movement is on, so 
	// that once it reaches the integer endTick, the movement stops.
	// The integer endTick stores the tick when the current movement should end, so that
	// a certain movement should take an endTick number of ticks.
	private int movementTick, endTick;
	// The integer correctionTicks marks how many ticks are left in a correction movement
	// A correction movement is where the mosquito has gone out of bounds and has to get
	// some ticks before it should be checked if it is out of bounds again. This is necessary 
	// to avoid continuous ticks where the mosquito is not moving.
	private int correctionTicks;
	
	// The BufferedImages mosquitoLeft and mosquitoRight store the images used to render
	// the mosquito when they are facing the left side and right side
	private static BufferedImage mosquitoLeft, mosquitoRight;
	// The integer width stores the width of the mosquito
	private static final int width = 27;
	
	// The double maxSpeed stores the maximum resultant velocity (so the combination of the 
	// components) that the mosquito can move at. The double baseSpeed stores the minimum
	// resultant velocity that the mosquito can move at.
	private static final double maxSpeed = 4.8, baseSpeed = 0.4;
	// The double averageRange stores the average distance in pixels that a mosquito can 
	// move from its source
	private static final double averageRange = 240;
	// The double rangeDistribution stores the number of pixels that the range can deviate
	// having the minimum range being 140 and 340, however since the author used a certain
	// way to generate the random numbers, the maximum is actually 339.
	private static final double rangeDistribution = 100;
	// The integer averageEndTick stores the average number of ticks that have to occur
	// before the next movement is chosen.
	private static final int averageEndTick = 20;
	// The integer endTickDistribution stores the number of ticks that the end tick can 
	// deviate. Since the averageEndTick is equal to the distribution it is possible
	// for a movement to get 0 ticks.
	private static final int endTickDistribution = 20;
	
	// The integer attackDelayRem stores the number of ticks that have to be waited
	// before the mosquito can attack again. Mosquitoes that still have this
	// delay will not approach the player instead continuing to move randomly or
	// towards the source.
	private int attackDelayRem;
	
	// The integer damage stores the amount of damage that mosquito's attacks will do to the player
	private static final int damage = 4;
	// The integer recognitionRange stores the Euclidean distance in pixels that the mosquito can
	// recognize the player and try to attack them
	private static final int recognitionRange = 240;
	// The integer attackRange stores the Euclidean distance in pixels that the mosquito can attack
	// the player.
	private static final int attackRange = 15;
	// The integer attackDelay stores the number of pixels that have to be waited between
	// attacks of a mosquito.
	private static final int attackDelay = 70;
	
	// The Player player stores a reference to the player in the game so that mosquitoes can 
	// know where to approach.
	private static Player player;
	
	// Method Description: The constructor for the Mosquito class will take in the position of the
	// mosquito and set up the instance variables for the mosquito instance so that it can
	// spawn in at that position. It will also load in the images of the mosquito if it hasn't
	// been done already.
	// Parameters: The double x stores the center x value of where the mosquito should spawn. The
	// double y stores the center y position where the mosquito should spawn.
	public Mosquito(double x, double y) {
		// The position of the mosquito is set
		this.x = x;
		this.y = y;
		// The spawning location of the mosquito is set to the values passed in as that
		// is where the mosquito spawned
		sourceX = x;
		sourceY = y;
		// The mosquito's velocity is gotten randomly first so that the mosquito will be
		// moving when it spawns in.
		getRandomMotion();
		// The mosquito has no correction ticks as they just spawned in and have not moved
		// outside of the range yet
		correctionTicks = 0;
		// The mosquito can also attack right away when it is spawned in
		attackDelayRem = 0;
		
		// The range where the mosquito can move is determined
		rangeDist = averageRange + Math.random() * 2 * rangeDistribution - rangeDistribution;
		
		// Load in the images if they haven't been loaded yet. Since the images used to render
		// the mosquitoes are static as they are used for all mosquitoes, they only have to be
		// loaded in when the first mosquito is declared
		if(mosquitoLeft == null) {
			try {
				// The images of the mosquito are loaded in
				mosquitoLeft = ImageIO.read(new File("res/MosquitoLeft.png"));
				mosquitoRight = ImageIO.read(new File("res/MosquitoRight.png"));
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
	
	// Method Description: The tick method will update the mosquito by moving it based
	// on its velocity, and if the mosquito is within range of the player or if it needs
	// to determine its next movement, the new velocity of the mosquito is also generated.
	public void tick() {
		// Local Variables
		// The center y position of the player is calculated as mosquitoes should aim for the 
		// center of the player rather than their feet, which is normally declared by the player's y
		// position.
		double playerMidY = player.getY() - player.getHeight() / 2;
		
		// Method Body
		// Check if the player is within the attacking range of the mosquito and is able to attack the player
		// When the mosquito is on attack cooldown, they will simply have the regular motion.
		if((x - player.getX()) * (x - player.getX()) + (y - playerMidY) * (y - playerMidY) <= recognitionRange * recognitionRange
				&& attackDelayRem <= 0) {
			// The motion of attack is constantly taken as the player may be moving, similar to how mosquitoes
			// can sense where the person moves and adjust their velocity accordingly
			getAttackMotion();
			
			// The position of the mosquito is updated based on its velocity
			x += velX;
			y += velY;
			// Get the relative x of the mosquito, as mosquitoes have their own velocity outside
			// of the player in the x direction, so they cannot be handled in the Game's repositionElements
			// method like other classes
			relX = x + player.getRelX() - player.getX();
			
			// Check if the player is within attacking bounds, if they are then attack the player
			if((x - player.getX()) * (x - player.getX()) + (y - playerMidY) * (y - playerMidY) <= attackRange * attackRange) {
				// The player takes damage from the mosquito and the mosquito is set to not attack
				// the player for a little while
				player.takeDamage(damage);
				attackDelayRem = attackDelay;
			}
		} else {
			// If they are not within attacking bounds, the mosquito has random motion. However, if the mosquito is not
			// at the source, then the mosquito will always try to move towards the center, but with some randomly generated
			// angle offset.
			
			if((x - sourceX) * (x - sourceX) + (y - sourceY) * (y - sourceY) <= rangeDist * rangeDist || correctionTicks > 0) {
				// If the mosquito is still inside of their range or more ticks are required for the correction motion to 
				// have more effect, then the mosquito can continue to move with their velocity of the movement.
				x += velX;
				y += velY;
				// Get the relative x
				relX = x + player.getRelX() - player.getX();
				
				// If the mosquito has correction ticks left, then they are decremented so that it can
				// be checked later if the mosquito is outside of the range and a new correction motion can be determined
				if(correctionTicks > 0) {
					correctionTicks--;
				}
			} else {
				// Otherwise, the mosquito is outside of their range so move the mosquito back towards the source
				getRandomMotion();
				// The mosquito is given 6 ticks to get back to get back before another random motion is taken
				correctionTicks = 6;
			}
			
			// When the mosquito is moving regularly their attack delay remaining can be lowered down to prepare them
			// for their next attack
			if(attackDelayRem > 0) {
				attackDelayRem--;
			}
			
			// If the mosquito has reached the end of their movement, a new one can be determined
			// so that the mosquito can move in a different direction now
			movementTick++;
			if(movementTick >= endTick) {
				getRandomMotion();
			}
		}
		
		if(y > Game.HEIGHT - Game.groundHeight) {
			// Avoid the mosquitoes going into the ground by bouncing
			// them back up
			velY = -Math.abs(velY);
		} else if(y < Game.HEIGHT - Game.groundHeight - rangeDist) {
			// Avoid the mosquitoes going too high by bouncing them
			// back down
			velY = Math.abs(velY);
		}
		
	}
	
	// Method Description: The render method will draw the mosquito onto the game with the
	// corresponding direction they are facing based on their velocity
	// Parameters: The Graphics g stores the graphics of the game, where the mosquito should be
	// rendered.
	public void render(Graphics g) {
		if(velX > 0) {
			// If the mosquito has a x velocity that is positive, they are moving right and should
			// face right
			g.drawImage(mosquitoRight, (int)(relX - mosquitoRight.getWidth() / 2), (int)(y - mosquitoRight.getHeight() / 2), null);
		} else {
			// Otherwise, the mosquito faces left
			g.drawImage(mosquitoLeft, (int)(relX - mosquitoLeft.getWidth() / 2), (int)(y - mosquitoLeft.getHeight() / 2), null);
		}
	}
	
	// Method Description: The getAttackMotion method will calculate the velocity of the mosquito so 
	// that they will move towards the player's position.
	private void getAttackMotion() {
		// Local Variables
		// The double diffX stores the difference in x position of the player to the mosquito.
		// This is how much the mosquito has to move in the x direction to get to the player.
		double diffX = player.getX() - x;
		// The double diffY stores the difference in y position of the player to the mosquito
		// The mosquito is chosen to attack the player in their middle
		// rather than at their feet
		double diffY = player.getY() - player.getHeight() / 2 - y;
		// The double attackAngle stores the angle which the mosquito has to move to get to the
		// player
		double attackAngle = Math.atan2(diffY, diffX);
		
		// Method Body
		// The velocity of the mosquito is found by having the mosquito move at max speed towards
		// the player's position
		velX = maxSpeed * Math.cos(attackAngle);
		velY = maxSpeed * Math.sin(attackAngle);
		
		// The movement and end tick is reset so that the mosquito is prompted to get
		// another random motion right after they finished attacking
		movementTick = 0;
		endTick = 0;
	}
	
	// Method Description: The getRandomMotion method calculates a new velocity of the mosquito which
	// is random to allow the mosquito to move in a more interesting manner. In most cases
	// the method will get the mosquito to move back towards the source with a bit of angle
	// variance of 45 degrees up and down.
	private void getRandomMotion() {
		// Local Variables
		// The Random variable rand is used to generate random numbers more easily
		Random rand = new Random();
		// The integer angle stores the angle that the mosquito should move in degrees
		int angle;
		// The double speed stores the speed which the mosquito should move
		// Here it is randomly generated to be between the base and max speed
		double speed = (maxSpeed - baseSpeed) * Math.random() + baseSpeed;
		
		// The double sourceAngle stores the angle that the mosquito needs to move to get to 
		// the source spawning location. This is used and the diffX and diffY variables below
		// are used when the mosquito is not at the source location.
		double sourceAngle;
		// The double diffX and diffY store the amount that the mosquito has to move in the x
		// and y direction to get to the source.
		double diffX, diffY;
		
		// Method Body
		// It is checked if the mosquito is at the source position
		if(x == sourceX && y == sourceY) {
			// If the mosquito is in the center, the mosquito has free range of motion from the center
			// So a random angle is taken and the velocity is determined where the mosquito will
			// move in that direction at the random speed.
			angle = rand.nextInt(360);
			velX = speed * Math.cos(Math.toRadians(angle));
			velY = speed * Math.sin(Math.toRadians(angle));
		} else {
			// Move the mosquito towards the center more likely so that they can stay within their range
			// The source angle is calculated
			diffX = sourceX - x;
			diffY = sourceY - y;
			sourceAngle = Math.toDegrees(Math.atan2(diffY, diffX));
			// The angle which the mosquito can move can be lower or higher by 45 degrees
			angle = (int)(sourceAngle + 2 * rand.nextInt(45) - rand.nextInt(45));
			// The velocity is calculated based on that angle and the random speed
			velX = speed * Math.cos(Math.toRadians(angle));
			velY = speed * Math.sin(Math.toRadians(angle));
		}
		
		// The movement tick is reset and the delay before the next movement is chosen is randomly
		// generated.
		movementTick = 0;
		endTick = averageEndTick + 2 * rand.nextInt(endTickDistribution) - endTickDistribution;
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
	
	public static void setPlayer(Player player) {
		Mosquito.player = player;
	}
}
