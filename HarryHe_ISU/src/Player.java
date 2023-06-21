/*
 * Class Name: Player
 * Description: The Player class represents the player in the game and contains
 * many variables that help to animate and keep information about the player. The class
 * stores the position and velocity of the player as well as the inventory of the player.
 * The class will also render the heads up display of the player in the game as information
 * about the player's health and stamina are also kept here.
 */

// Importing the necessary classes so that the player can have an inventory, be rendered
// and have their items spread out in their original position when they respawn.
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Player {
	// While all of the variables in this class can be static as there should only ever
	// be one player, the variables are put in this manner so that the game can be more
	// easily scaled to have multiple players.
	
	// The double x stores the center position of the player
	// The double y stores the bottom position of the player, so
	// that it is easier to manage with the ground
	private double x, y;
	// The double spawnX and spawnY stores the position where
	// the player spawned in and is where they should respawn
	private double spawnX, spawnY;
	// The double velX and velY stores the x and y velocities of the player
	private double velX, velY;
	// The boolean changedX stores if the x value of the player has been changed which 
	// means that the relative positions of other elements in the game should be updated
	private boolean changedX;
	// The boolean array keyDown stores if important keys are pressed down and so their
	// actions can be repeated
	// keyDown has 5 indices that represent different things
	// that are helpful to have when held down
	// 0 - Left
	// 1 - Right
	// 2 - Jump
	// 3 - Attack
	// 4 - Shift (to Run)
	private boolean keyDown[];
	
	// The boolean onGround stores if the player is currently on the ground
	private boolean onGround;
	// The double walkSpeed stores the walking speed of the player, which should
	// only be in the x direction
	private double walkSpeed;
	// The double runSpeed stores the running speed of the player which is also only
	// in the x direction
	private double runSpeed;
	// The double jumpSpeed stores the initial velocity which the player will jump with
	private double jumpSpeed;
	// The integer burdenCapacity stores the amount of burden that the player can hold.
	// Burden represents how much an item will weigh down on the player and it
	// includes the number of slots that the player has to have to get all these items,
	// essentially a limit to how much the player can hold.
	private int burdenCapacity;
	// The integer attackDelayRem stores the number of ticks left before the next
	// swatting action can be performed.
	private int attackDelayRem;
	// The integer attackTickDelay determines the number of ticks that have
	// to be passed before the next attack can occur, storing the total
	// number of ticks, not the amount remaining
	private int attackTickDelay;
	
	// The double health stores the hit points of the player, where they can take that
	// much damage before they have to respawn
	private double health;
	// The double stamina stores the stamina of the player, which allows them to perform
	// a certain number of actions
	private double stamina;
	// The double maxHealth stores the maximum health of the player
	private double maxHealth;
	// The double maxStamina stores the maximum stamina of the player
	private double maxStamina;
	// The BufferedImage healthBarFrame stores the image that is rendered around the health bar
	private BufferedImage healthBarFrame;
	// The BufferedImage staminaBarFrame stores the image rendered around the stamina bar
	private BufferedImage staminaBarFrame;
	// The double swatStaminaCost stores the amount of stamina that a swat action will take
	private static final double swatStaminaCost = 100;
	// The double jumpStaminaCost stores the amount of stamina that jumping will take
	private static final double jumpStaminaCost = 20;
	
	// The double relX stores the position where the player is rendered on the screen, which
	// is always in the middle of the application
	private static final double relX = Game.WIDTH / 2;
	// The double gravity stores the acceleration of the player when they are in the air. This is 
	// the value added to the y velocity when the player is in the air.
	private static final double gravity = 1.3;
	// The double pickUpReach stores the x range which the player can pick up items, beyond the
	// player's size and the item's size.
	private static final double pickUpReach = 5.0;
	// The double swatStartReach stores the Euclidean distance where the player can start being able
	// to hit the mosquitoes and the double swatEndReach stores the Euclidean distance where the
	// player stops being able to hit mosquitoes.
	private static final double swatStartReach = 30, swatEndReach = 110;
	
	// Inventory Variables
	// The integer curBurden stores the burden of the items of the player
	private int curBurden;
	// The integer slotBurden stores the burden of the number of slots that the player has open. 
	// This is included to incentivize the player to bring only what they need and because in real
	// life it can be difficult to hold items of different size and texture.
	private int slotBurden;
	// The integer selectedIndex is the index of the selected item of the player in their inventory
	private int selectedIndex;
	// The boolean overBurdened stores if the player is has too many items and is overburdened, meaning
	// that they can no longer pick up items and run out of stamina.
	private boolean overBurdened;
	// The ArrayList of InventorySlots inventory represents the inventory of the player, storing all of
	// the items that they have and making it easier to render and see. An ArrayList is used becuase
	// getting at the selected index is much faster using an ArrayList.
	private ArrayList<InventorySlot> inventory;
	// The double slotY stores the y position of all slots in the inventory
	private static final double slotY = Game.HEIGHT - 10 - InventorySlot.getHeight() / 2;
	// The integer indivSlotBurden stores the amount of burden that each slot will take
	// on the player.
	private static final int indivSlotBurden = 4;
	// The integer respawnDropRange stores the range in the x direction where the items can dropped
	// when they respawn. When the player gets bitten by too many mosquitoes, they will respawn and 
	// their items will be dropped between their original position - respawnDropRange and 
	// original position + respawnDropRange - 1.
	private static final int respawnDropRange = 150;
	
	// Animation Variables
	// The booleans facingRight, walking, running, jumping, swatting, picking stores if the player
	// is doing those respective actions or is in that state.
	private boolean facingRight, walking, running, jumping, swatting, picking;
	// The integer curImageTick stores the number of ticks for the current image index in the animation
	// and the integer curImageIndex stores the current image index which is used to render the animation
	private int curImageTick, curImageIndex;
	// The String curAnimation stores the animation that the player is currently performing
	// There are 5 possible animations in the game
	// - Walking
	// - Running
	// - Jumping
	// - Swatting
	// - Picking
	private String curAnimation;
	
	// The BufferedImages playerStandingLeft and playerStandingRight store the images used to render
	// the player standing left and right
	private static BufferedImage playerStandingLeft, playerStandingRight;
	
	// The BufferedImage arrays walkLeft and walkRight store the images used to render the player
	// walking to the left side and walking to the right side.
	private static BufferedImage[] walkLeft, walkRight;
	// The double array walkFrameWeight stores the weight that each walking image takes in the 
	// animation compared to the other images. This is used to calculate the number of frames each 
	// image should be shown for. See the calculateAnimationFrames method for more information on how
	// this works.
	// 7 images in the walking animation
	private static final double [] walkFrameWeight = {0.1, 0.2, 0.3, 0.2, 0.2, 0.3, 0.1};
	// The integer totalWalkFrames stores the total number of frames in the walking animation
	private static final int totalWalkFrames = 36;
	// The integer array walkFrames stores the number of frames that each image should be rendered
	// for in the walking animation
	private static int[] walkFrames;
	
	// The running variables here are of an identical structure to the walking animations
	// and are used in the same way. Refer to the walking animation variables for more information
	// on each variable for the animation is used.
	private static BufferedImage[] runLeft, runRight;
	// 6 images in the running animation
	private static final double [] runFrameWeight = {0.1, 0.1, 0.3, 0.1, 0.1, 0.3};
	private static final int totalRunFrames = 28;
	private static int[] runFrames;
	
	// The swatting variables here are of an identical structure to the walking animations
	// and are used in the same way. Refer to the walking animation variables for more information
	// on each variable for the animation is used.
	private static BufferedImage[] swatLeft, swatRight;
	// 4 images in the swatting animation
	private static final double [] swatFrameWeight = {0.1, 0.2, 0.2, 0.1};
	private static final int totalSwatFrames = 28;
	private static int[] swatFrames;
	
	// The picking variables here are of an identical structure to the walking animations
	// and are used in the same way. Refer to the walking animation variables for more information
	// on each variable for the animation is used.
	private static BufferedImage[] pickLeft, pickRight;
	// 3 images in the picking animation
	private static final double [] pickFrameWeight = {0.1, 0.4, 0.1};
	private static final int totalPickFrames = 20;
	private static int[] pickFrames;
	
	// The BufferedImage arrays jumpLeft and jumpRight store the images that are used to 
	// render the player jumping facing left and right.
	private static BufferedImage[] jumpLeft, jumpRight;
	// The double array jumpFramePos stores the y positions above the ground where the next image can
	// then be rendered when the player is jumping.
	// Here the number of frames in the jump is dependent on the y position of the player
	// above the ground level, however some of the landing and launch animations do have some frames allocated
	// Note that the required positions, are actually for the next image, as you are already at the current
	// frame, you need to see how far you need to go to get to the next frame.
	// 5 images in the jumping animation
	private static final double [] jumpFramePos = {0, -70, -72, -1, 0};
	// The integer array jumpFrames stores the number of frames that have to be waited for
	// each image before the next image is gone to. This array is used in conjunction with the jumpFramePos
	// array as some images are dependent on number of frames while others are dependent on y position.
	private static final int [] jumpFrames = {8, 0, 0, 0, 8};
	
	// The double width stores the width of the player
	private static final double width = 50;
	// The double height stores the height of the player
	private static final double height = 140;
	
	// The Font regularHUDFont stores the font used to render regularly sized text in the heads up display (HUD)
	// and the Font smallHUDFont stores teh font used to render smaller text in the HUD.
	private static Font regularHUDFont, smallHUDFont;
	// The integer healthBarLength and staminaBarLength store the length of the health and stamina bar.
	private static final int healthBarLength = 362, staminaBarLength = 272;
	// The Colors lightRed, red, darkRed, lightBlue, blue, and darkBlue store the colors used in the HUD to render
	// in the health bar and the stamina bar.
	private static final Color lightRed = new Color(224, 88, 81), red = new Color(198, 57, 49), darkRed = new Color(127, 35, 30);
	private static final Color lightBlue = new Color(43, 120, 253), blue = new Color(2, 90, 242), darkBlue = new Color(2, 74, 197);
	
	// Method Description: The constructor for the Player class will take in the position of the player's spawn
	// and declare in all of the variables of the player, to set the player up to have a default state.
	// Parameters: The double x stores the center x position of the player. The double y stores the bottom
	// y position of the player.
	public Player(double x, double y) {
		// The position of the player is set up
		this.x = x;
		this.y = y;
		// The spawn location of the player is set to the values passed in which is where the 
		// player should spawn in
		spawnX = x;
		spawnY = y;
		// The player is set to be not moving
		velX = 0;
		velY = 0;
		// The player's keys pressed is declared and they initially have no keys pressed down
		keyDown = new boolean[5];
		// The player is set to not be on the ground when they spawn in as it is unknown if they
		// are on the ground or not. Conventionally, the game declares the player actually to be
		// in the air, so the player is actually not on the ground.
		onGround = false;
		// The player is initially not moving so they have not changed their x position yet
		changedX = false;
		
		// The speeds of the player is set up
		walkSpeed = 4;
		runSpeed = 7;
		jumpSpeed = -15;
		// The player's burden is set up, where they initially have
		// no items and can hold a burden of 30.
		burdenCapacity = 30;
		curBurden = 0;
		slotBurden = 0;
		overBurdened = false;
		// The attack delay is set up
		attackDelayRem = 0;
		attackTickDelay = 30;
		
		// The player's health and stamina is declared
		maxHealth = 100;
		maxStamina = 400;
		health = maxHealth;
		stamina = maxStamina;
		
		// The player's inventory is set up, where they have a blank inventory
		// and initially not selected item
		selectedIndex = -1;
		inventory = new ArrayList<>();
		
		// The player is initially facing right and not performing any other actions
		facingRight = true;
		walking = false;
		running = false;
		jumping = false;
		swatting = false;
		picking = false;
		
		// The current animation is set to be blank and the images are reset to be
		// at the start.
		curImageTick = 0;
		curImageIndex = 0;
		curAnimation = "";
		
		// Calculate the animation frame times
		calculateAnimationFrames();
		
		// Load in images
		try {
			// Load in the standing images
			playerStandingLeft = ImageIO.read(new File("res/Player/PlayerStandingLeft.png"));
			playerStandingRight = ImageIO.read(new File("res/Player/PlayerStandingRight.png"));
			
			// Load in walking images
			walkLeft = new BufferedImage[7];
			walkRight = new BufferedImage[7];
			for(int i = 1; i <= 7; i++) {
				walkLeft[i - 1] = ImageIO.read(new File("res/Player/Walk/WalkLeft" + i + ".png"));
				walkRight[i - 1] = ImageIO.read(new File("res/Player/Walk/WalkRight" + i + ".png"));
			}
			
			// Load in running images
			runLeft = new BufferedImage[6];
			runRight = new BufferedImage[6];
			for(int i = 1; i <= 6; i++) {
				runLeft[i - 1] = ImageIO.read(new File("res/Player/Run/RunLeft" + i + ".png"));
				runRight[i - 1] = ImageIO.read(new File("res/Player/Run/RunRight" + i + ".png"));
			}
			
			// Load in swatting images
			swatLeft = new BufferedImage[4];
			swatRight = new BufferedImage[4];
			for(int i = 1; i <= 4; i++) {
				swatLeft[i - 1] = ImageIO.read(new File("res/Player/Swat/SwatLeft" + i + ".png"));
				swatRight[i - 1] = ImageIO.read(new File("res/Player/Swat/SwatRight" + i + ".png"));
			}
			
			// Load in picking images
			pickLeft = new BufferedImage[3];
			pickRight = new BufferedImage[3];
			for(int i = 1; i <= 3; i++) {
				pickLeft[i - 1] = ImageIO.read(new File("res/Player/Pick/PickLeft" + i + ".png"));
				pickRight[i - 1] = ImageIO.read(new File("res/Player/Pick/PickRight" + i + ".png"));
			}
			
			// Load in jumping images
			jumpLeft = new BufferedImage[5];
			jumpRight = new BufferedImage[5];
			for(int i = 1; i <= 5; i++) {
				jumpLeft[i - 1] = ImageIO.read(new File("res/Player/Jump/JumpLeft" + i + ".png"));
				jumpRight[i - 1] = ImageIO.read(new File("res/Player/Jump/JumpRight" + i + ".png"));
			}
			
			// Load in HUD bar frames
			healthBarFrame = ImageIO.read(new File("res/Inventory/HealthBarFrame.png"));
			staminaBarFrame = ImageIO.read(new File("res/Inventory/StaminaBarFrame.png"));
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
		
		// Load in HUD regular and small fonts with size 16 and 14 respectively.
		try {
			// Create the HUD fonts at the given path, and use the regular plain style
			// of the font as the fonts loaded in only have one style.
			regularHUDFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/RegularFont.ttf")).deriveFont(Font.PLAIN, 16);
			smallHUDFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/RegularFont.ttf")).deriveFont(Font.PLAIN, 14);
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
	
	// Method Description: The tick method will update the player based on the keys that
	// they have pressed down and the actions that the player is taking. The method
	// also handles the animation of the player moving forwards the images
	// once the number of ticks needed for the image has passed.
	public void tick() {
		// The player cannot move in air, so it is checked if the player is on the ground
		// before their velocity is altered based on the keys they have pressed
		if(onGround) {
			if(keyDown[0] && keyDown[1]) {
				// If the player simultaneously presses left and right they should get no movement
				velX = 0;
			} else if(keyDown[0]) {
				// Move Left
				if(keyDown[4] && stamina > 0) {
					// Player is running, so set the velocity to be left based on the run speed
					velX = -runSpeed;
				} else {
					// Otherwise, the velocity is set to be left based on the walking speed
					velX = -walkSpeed;
				}
			} else if(keyDown[1]) {
				// Move Right
				if(keyDown[4] && stamina > 0) {
					// Player is running, so set the velocity to be right based on the run speed
					velX = runSpeed;
				} else {
					// Otherwise, the velocity is set to be right based on the walking speed
					velX = walkSpeed;
				}
			} else {
				// If the user is not pressing down any keys then there velocity is 0
				velX = 0;
			}
		}
		
		// Check if the player is trying to sprint
		if(keyDown[4]) {
			// If the player is trying to sprint the stamina will decrease
			if(stamina > 0) {
				// If the player has stamina left, then they can run
				stamina--;
			} else {
				// If the player has no stamina left, then they can no longer run
				running = false;
			}
		} else if(stamina < maxStamina && !overBurdened) {
			// The stamina only regenerates if the player is no longer
			// trying to sprint, the stamina is under the threshold, and
			// when the player is not overburdened
			stamina++;
		}
		
		// If the player is trying to attack, try to see if the swatting animation
		// can be played, which is when there is no more attack delay and when
		// the player has reached the stamina threshold.
		// The threshold is a bit generous as the entire stamina gained in that time period
		// will be less than the total swat frames, as the swat frames is for the entire animation
		// not just the part until the player attacked
		if(keyDown[3] && attackDelayRem <= 0 && stamina > swatStaminaCost - totalSwatFrames) {
			// The conditions are met so the swatting animation can be played
			swatting = true;
		} else if(attackDelayRem > 0) {
			// If the player recently attacked the attack delay remaining is decreased
			// so that the player can re-attack again after the delay
			attackDelayRem--;
		}
		
		// The player is trying to jump, so try to see if the jumping animation can be played
		// The jumping animation can only be set to played if the player has the key down
		// and is not currently picking or swatting
		if(keyDown[2] && !picking && !swatting) {
			jumping = true;
		}
		
		// The x position adds on the x component of velocity
		x += velX;
		if(x < -460) {
			// The player cannot travel outside of the map so they are
			// bounded at the left side of the map
			x = -460;
		} else if(x > 9105) {
			// The player cannot travel outside of the map so they are 
			// also bounded at the right side of the map
			x = 9105;
		} else {
			if(x > 8380) {
				// Manage the porta-potty quest
				if(Scouter.getQuestLine() == 6) {
					// If the player reaches around the area of the porta-potty
					// then the quest objective is completed
					Scouter.nextQuestLine();
				}
			}
			
			// If the player was not bounded at the ends of the map
			// then player's distance travelled is increased
			Game.addDistanceTravelled((int)(Math.abs(velX)));
		}
		// If the player has some velocity, it is marked that the player
		// changed x values and so the relative x of elements in the game
		// has to be recalculated
		if(velX != 0) {
			changedX = true;
		}
		// The y position adds on the y component of the velocity
		y += velY;
		if(!onGround) {
			// If the player is not on the ground, then their velocity
			// adds on the gravity so they accelerated back down
			velY += gravity;
		}
		
		// If the player ends up back on the ground, they will snap to the ground height
		// and be set back onto the ground
		if(y >= Game.HEIGHT - Game.groundHeight) {
			onGround = true;
			y = Game.HEIGHT - Game.groundHeight;
		}
		
		// If the player's health drops to zero or below, they were bit by too many 
		// mosquitoes and have to respawn back to their starting position
		if(health <= 0) {
			respawn();
		}
		
		// Move the animation stage for the current animation action
		// The precedence for animations goes
		// 1. Swatting
		// 2. Picking up
		// 3. Jumping
		// 4. Running
		// 5. Walking
		if(swatting) {
			// Swatting animation
			// If the player swaps out of holding a racket, they should no longer be attacking
			if(selectedIndex == -1 || !inventory.get(selectedIndex).getItemType().equals("Racket")) {
				swatting = false;
				curImageTick = 0;
				curImageIndex = 0;
			}
			
			// If the current animation is not swatting then the animation is set to swatting
			// and the animation is set to play from the start. This changing of the animation
			// is necessary so that other animations will not affect the current animation
			// and they can start from the beginning.
			if(!curAnimation.equals("Swatting")) {
				curAnimation = "Swatting";
				curImageTick = 0;
				curImageIndex = 0;
			}
			
			if(curImageTick < swatFrames[curImageIndex]) {
				// If the current image's ticks have not been all completed, then the current
				// image ticks will be increased
				curImageTick++;
			} else {
				// Otherwise, the image should be progressed forwards
				curImageIndex++;
				// The tick is reset back to 0 to make sure that the image has all of its
				// frames where it has to be rendered
				curImageTick = 0;
				if(curImageIndex >= swatFrames.length) {
					// If the animation has reached its end, the swat action will
					// stop
					swatting = false;
					curImageTick = 0;
					curImageIndex = 0;
				}
				
				// Check if the swat animation is on the 3rd stage (index 2)
				// meaning that the mosquitoes will be hit now and the attack
				// changes the game
				if(curImageIndex == 2) {
					Game.swatMosquitoes();
					attackDelayRem = attackTickDelay;
					stamina -= swatStaminaCost;
					if(stamina < 0) {
						stamina = 0;
					}
				}
			}
		} else if(picking) {
			// Picking animation
			// If the current animation is not picking then the animation is set to picking
			// and the animation is set to play from the start
			if(!curAnimation.equals("Picking")) {
				curAnimation = "Picking";
				curImageTick = 0;
				curImageIndex = 0;
			}
			
			if(curImageTick < pickFrames[curImageIndex]) {
				// If the current image's ticks have not been all completed, then the current
				// image ticks will be increased
				curImageTick++;
			} else {
				// Otherwise, the image should be progressed forwards
				curImageIndex++;
				// The tick is reset back to 0 to make sure that the image has all of its
				// frames where it has to be rendered
				curImageTick = 0;
				if(curImageIndex >= pickFrames.length) {
					// If the animation has reached its end, the pick action will
					// stop
					picking = false;
					curImageTick = 0;
					curImageIndex = 0;
				}
				
				// Check if the picking animation is on the 2nd stage (index 1)
				// meaning that the actual pick up will occur
				if(curImageIndex == 1) {
					Game.playerPickUp();
				}
			}
		} else if(jumping) {
			// Jumping animation
			// If the current animation is not jumping then the animation is set to jumping
			// and the animation is set to play from the start
			if(!curAnimation.equals("Jumping")) {
				curAnimation = "Jumping";
				curImageTick = 0;
				curImageIndex = 0;
			}
			
			// Check if the current jump frame is dependent on the player's position
			if(jumpFrames[curImageIndex] == 0) {
				// The comparisons for the jumps are ascending and descending so 
				// the inequality sign does swap as the indices increase, which is why there
				// is two parts to this if statement
				if(curImageIndex <= 1) {
					// The player is going up so it is checked if the player has reached the threshold 
					// for the next frame where the y has to be less than or equal to the height
					if(y <= Game.HEIGHT - Game.groundHeight + jumpFramePos[curImageIndex]) {
						// Reached the threshold for the next frame, so the image is increased
						// to the next one
						curImageIndex++;
						curImageTick = 0;
					} else {
						// Add a fail safe, which should theoretically never occur, however can happen
						// if the player manages to get into another jump frame with out leaving
						// the ground
						if(onGround && velY >= 0) {
							// Here the player will have already landed on the ground so skip
							// to the landing animation
							curImageIndex++;
						}
					}
				} else {
					// The player is going down so it is checked if the player has reached the threshold 
					// for the next frame where the y has to be greater than or equal to the height
					if(y >= Game.HEIGHT - Game.groundHeight + jumpFramePos[curImageIndex]) {
						// Reached the threshold for the next frame, so the image is increased
						// to the next one
						curImageIndex++;
						curImageTick = 0;
					} else {
						// Add a fail safe, which should theoretically never occur, however can happen
						// if the player manages to get into another jump frame with out leaving
						// the ground
						if(onGround && velY >= 0) {
							// Here the player will have already landed on the ground so jump
							// to the landing animation
							curImageIndex++;
						}
					}
				}
			} else {
				// Otherwise, the current jump frame is not dependent on the player's position
				if(curImageTick < jumpFrames[curImageIndex]) {
					// If the current image's ticks have not been all completed, then the current
					// image ticks will be increased
					curImageTick++;
				} else {
					// Otherwise, the image should be progressed forwards
					curImageIndex++;
					// The tick is reset back to 0 to make sure that the image has all of its
					// frames where it has to be rendered
					curImageTick = 0;
					if(curImageIndex >= jumpFrames.length) {
						// If the animation has reached its end, the jump action will
						// stop if the player is not pressing the jump key
						if(!keyDown[2]) {
							jumping = false;
						}
						curImageTick = 0;
						curImageIndex = 0;
					}
					
					// Check if the jumping animation is on the 2rd stage (index 1)
					// meaning that the actual jump will occur
					if(curImageIndex == 1) {
						// The player will jump if they are on the ground and if they have enough
						// stamina. If they do not have enough stamina, the animation is cancelled
						if(onGround && stamina >= jumpStaminaCost) {
							velY = jumpSpeed;
							onGround = false;
							stamina -= jumpStaminaCost;
						} else if(onGround && stamina < jumpStaminaCost){
							// Reset the image index and tick as the player may hold down jump
							// to try to continually jump, where the animation will now
							// show they player repeatedly trying to jump
							jumping = false;
							curImageIndex = 0;
							curImageTick = 0;
						}
					}
				}
			}
			
		} else if(running) {
			// Running animation
			// If the current animation is not running then the animation is set to running
			// and the animation is set to play from the start
			if(!curAnimation.equals("Running")) {
				curAnimation = "Running";
				curImageTick = 0;
				curImageIndex = 0;
			}
			
			if(curImageTick < runFrames[curImageIndex]) {
				// If the current image's ticks have not been all completed, then the current
				// image ticks will be increased
				curImageTick++;
			} else {
				// Otherwise, the image should be progressed forwards
				curImageIndex++;
				// The tick is reset back to 0 to make sure that the image has all of its
				// frames where it has to be rendered
				curImageTick = 0;
				if(curImageIndex >= runFrames.length) {
					// Since the running animation is continuous then the running animation
					// is reset back to the beginning if the running animation has reached its 
					// end
					curImageIndex = 0;
				}
			}
		} else if(walking) {
			// Walking animation
			// If the current animation is not walking then the animation is set to walking
			// and the animation is set to play from the start
			if(!curAnimation.equals("Walking")) {
				curAnimation = "Walking";
				curImageTick = 0;
				curImageIndex = 0;
			}
			
			if(curImageTick < walkFrames[curImageIndex]) {
				// If the current image's ticks have not been all completed, then the current
				// image ticks will be increased
				curImageTick++;
			} else {
				// Otherwise, the image should be progressed forwards
				curImageIndex++;
				// The tick is reset back to 0 to make sure that the image has all of its
				// frames where it has to be rendered
				curImageTick = 0;
				if(curImageIndex >= walkFrames.length) {
					// Since the walking animation is continuous then the walking animation
					// is reset back to the beginning if the walking animation has reached its 
					// end
					curImageIndex = 0;
				}
			}
		} else {
			// Otherwise, there is no current animation so the animation is set to blank
			// so that the next animation will be reset back to the start
			if(!curAnimation.equals("")) {
				curAnimation = "";
			}
		}
	}
	
	// Method Description: The render method will draw the player onto the screen in their
	// current animation state and the direction that the player is facing.
	// Parameters: The Graphics g stores the graphics of the application where the game is
	// being rendered which is where the player should be rendered.
	public void render(Graphics g) {
		// Local Variables
		// The BufferedImage animationImage stores the image that is currently being
		// displayed. This variable is here so that the code will be a bit less cluttered
		// storing a temporary reference so that the array at the index won't have to be 
		// repeatedly used.
		BufferedImage animationImage;
		
		// Method Body
		if(swatting && curAnimation.equals("Swatting")) {
			// The player swatting is drawn in. Here the right and left side of the animation
			// will continue on with the same current image index, to complete the animation.
			// So the player can look as if they are swatting left but can turn around and 
			// swat right.
			if(facingRight) {
				// The current image index is rendered for the right side
				animationImage = swatRight[curImageIndex];
				// The player is center aligned onto its x value, and the top of the image is calculated
				// by taking the bottom - the height.
				g.drawImage(animationImage, (int)(relX - animationImage.getWidth() / 2), (int)(y - animationImage.getHeight()), null);
			} else {
				// The current image index is rendered for the left side
				animationImage = swatLeft[curImageIndex];
				g.drawImage(animationImage, (int)(relX - animationImage.getWidth() / 2), (int)(y - animationImage.getHeight()), null);
			}
		} else if(picking && curAnimation.equals("Picking")) {
			// The player picking things up is drawn in
			if(facingRight) {
				// The current image index is rendered for the right side
				animationImage = pickRight[curImageIndex];
				g.drawImage(animationImage, (int)(relX - animationImage.getWidth() / 2), (int)(y - animationImage.getHeight()), null);
			} else {
				// The current image index is rendered for the left side
				animationImage = pickLeft[curImageIndex];
				g.drawImage(animationImage, (int)(relX - animationImage.getWidth() / 2), (int)(y - animationImage.getHeight()), null);
			}
		} else if(jumping && curAnimation.equals("Jumping")) {
			// The player jumping is drawn in
			if(facingRight) {
				// The current image index is rendered for the right side
				animationImage = jumpRight[curImageIndex];
				g.drawImage(animationImage, (int)(relX - animationImage.getWidth() / 2), (int)(y - animationImage.getHeight()), null);
			} else {
				// The current image index is rendered for the left side
				animationImage = jumpLeft[curImageIndex];
				g.drawImage(animationImage, (int)(relX - animationImage.getWidth() / 2), (int)(y - animationImage.getHeight()), null);
			}
		} else if(running && curAnimation.equals("Running")) {
			// The player running is drawn in
			if(facingRight) {
				// The current image index is rendered for the right side
				animationImage = runRight[curImageIndex];
				g.drawImage(animationImage, (int)(relX - animationImage.getWidth() / 2), (int)(y - animationImage.getHeight()), null);
			} else {
				// The current image index is rendered for the left side
				animationImage = runLeft[curImageIndex];
				g.drawImage(animationImage, (int)(relX - animationImage.getWidth() / 2), (int)(y - animationImage.getHeight()), null);
			}
		} else if(walking && curAnimation.equals("Walking")) {
			// The player walking is drawn in
			if(facingRight) {
				// The current image index is rendered for the right side
				animationImage = walkRight[curImageIndex];
				g.drawImage(animationImage, (int)(relX - animationImage.getWidth() / 2), (int)(y - animationImage.getHeight()), null);
			} else {
				// The current image index is rendered for the left side
				animationImage = walkLeft[curImageIndex];
				g.drawImage(animationImage, (int)(relX - animationImage.getWidth() / 2), (int)(y - animationImage.getHeight()), null);
			}
		} else {
			// The base case for rendering the player is for them to be standing
			if(facingRight) {
				// Render standing facing right side
				g.drawImage(playerStandingRight, (int)(relX - playerStandingRight.getWidth() / 2), (int)(y - playerStandingRight.getHeight()), null);
			} else {
				// Render standing facing left side
				g.drawImage(playerStandingLeft, (int)(relX - playerStandingLeft.getWidth() / 2), (int)(y - playerStandingLeft.getHeight()), null);
			}
		}
	}
	
	// Method Description: The renderHUD method will render the heads up display
	// of the player, including their health, stamina and their inventory.
	// Parameters: The Graphics g stores the graphics of the game where the heads
	// up display should be rendered.
	public void renderHUD(Graphics g) {
		// Local Variables
		// The integer curLength stores the length to use for the health bar and 
		// stamina bar so that the length does not have to be computed multiple times
		int curLength;
		
		// Method Body
		// The inventory is looped over and all of its InventorySlots are rendered
		for(int i = 0; i < inventory.size(); i++) {
			if(selectedIndex == i) {
				inventory.get(i).render(g, true, overBurdened);
			} else {
				inventory.get(i).render(g, false, overBurdened);
			}
		}
		
		// Draw the health bar
		// The length of the health bar is taken the factor of the 
		// maximum health the actual health is times the maximum length.
		// Essentially the health bar is proportional to the actual health.
		curLength = (int)(health / maxHealth * healthBarLength);
		g.drawImage(healthBarFrame, 10, 10, null);
		g.setColor(lightRed);
		g.fillRect(10, 33, curLength, 9);
		g.setColor(red);
		g.fillRect(10, 42, curLength, 12);
		g.setColor(darkRed);
		g.fillRect(10, 54, curLength, 3);
		if(health > 0) {
			// A bit of darker shading is put to the right of the health bar
			// to give the health bar a bit of 3D feel. This only occurs when
			// the health is greater than 0 however, as it can make it appear
			// that there is health when there really isn't.
			g.fillRect(10 + curLength, 33, 1, 24);
		}
		// A String is put in the health bar frame to indicate that it represents
		// health
		g.setFont(regularHUDFont);
		g.setColor(Color.black);
		g.drawString("Health", 15, 29);
		
		// Draw the stamina bar
		// The length of the stamina bar is derived similarly to the health bar
		curLength = (int)(stamina / maxStamina * staminaBarLength);
		g.drawImage(staminaBarFrame, 10, 70, null);
		g.setColor(lightBlue);
		g.fillRect(10, 88, curLength, 5);
		g.setColor(blue);
		g.fillRect(10, 93, curLength, 9);
		g.setColor(darkBlue);
		g.fillRect(10, 102, curLength, 3);
		if(stamina > 0) {
			// Give the stamina bar a bit of 3D feel by adding some
			// darker shading
			g.fillRect(10 + curLength, 88, 1, 17);
		}
		// A String is put in the stamina bar frame to indicate that it represents
		// stamina
		g.setFont(smallHUDFont);
		g.setColor(Color.black);
		g.drawString("Stamina", 15, 84);
	}
	
	// Method Description: The addItem method will attempt to add an item of the passed
	// in type to the player's inventory.
	// Parameters: The String itemType stores the type of the item that should be added
	// to the player's inventory.
	// Return: The method returns true if the item was successfully added
	// and returns false if it was not added
	public boolean addItem(String itemType) {
		// Local Variables
		// The integer itemIndex stores the index of the slot if it stores
		// the item type passed in
		int itemIndex = -1;
		
		// Method Body
		// The item can only be added if the player has not yet reached
		// their burden capacity
		if(curBurden + slotBurden < burdenCapacity) {
			// Find if the item already exists in the inventory
			for(int i = 0; i < inventory.size(); i++) {
				if(inventory.get(i).getItemType().equals(itemType)) {
					// If the inventory already has a slot that has that
					// item type then the itemIndex will indicate that
					// the slot should be changed instead of making a new slot
					itemIndex = i;
					break;
				}
			}
			
			if(itemIndex >= 0) {
				// If a slot with that item type exists then that slot's item
				// count will be increased
				inventory.get(itemIndex).increaseItemCount();
				// The burden is then increased
				curBurden += Item.getItemBurden(itemType);
			} else {
				// Otherwise, a new slot will be created that stores that
				// item
				inventory.add(new InventorySlot(slotY, itemType));
				// The slots x position is found
				allocateSlotPos();
				// The burden on the player is then increased, both for the
				// item itself and for the additional slot that is necessary
				slotBurden += indivSlotBurden;
				curBurden += Item.getItemBurden(itemType);
			}
			// If the player exceeds their burden capacity because of the additional 
			// item they they will become overburdened
			if(curBurden + slotBurden >= burdenCapacity) {
				overBurdened = true;
				// The stamina of the player becomes zero when the player is overburdened
				stamina = 0;
			}
			// The item was successfully added
			return true;
		}
		// The item is only failed to be added if the player has exceeded
		// their capacity
		return false;
	}
	
	// Method Description: The dropItem method allows the player to drop the selected item in their
	// inventory onto the map. This method is called whenever the player presses the drop item hotkey
	// in the game.
	public void dropItem() {
		// The player can only drop their selected item, so the selected index has to indicate an
		// item to drop
		if(selectedIndex >= 0) {
			// An environment item is added which is dropped by the player. Here the shift x quantity is derived from this
			// player's relative x subtract their x. The height at which the item is dropped is 3 / 4 of the player's height.
			Game.addItem(new EnvironmentItem(x, y - height * 3 / 4, inventory.get(selectedIndex).getItemType(), relX - x));
			
			// Manage the quest to drop the fish carcass
			if(Scouter.getQuestLine() == 1 && inventory.get(selectedIndex).getItemType().equals("Fish Carcass")
					&& x <= 0) {
				// If the player drops the fish carcass onto the dock then the quest will be progressed
				Scouter.nextQuestLine();
			}
			
			// The dropped item is removed from the player's inventory
			removeItem();
		}
	}
	
	// Method Description: The removeItem method will remove the player's selected item from their inventory.
	// This method is called whenever the player drops an item or adds an item to the campfire.
	public void removeItem() {
		// The current burden of the selected item is removed from the player
		curBurden -= Item.getItemBurden(inventory.get(selectedIndex).getItemType());
		if(inventory.get(selectedIndex).getItemCount() == 1) {
			// If the selected slot only has one more item left, then the slot should 
			// be removed as the item in that slot will be gone.
			inventory.remove(selectedIndex);
			// The slot positions have to be recalculated as the inventory's slots
			// may have to be shifted
			allocateSlotPos();
			// The slot burden is decreased as the player now has less slots
			slotBurden -= indivSlotBurden;
			
			// If the slot is removed, the selected index will move to the next slot lower
			// than it, if that is not possible it will move to the next slot higher which remains
			// at the same index, if that is also not possible the inventory is empty the selected 
			// index becomes -1
			if(selectedIndex > 0) {
				selectedIndex--;
			} else if(!inventory.isEmpty()) {
				selectedIndex = 0;
			} else {
				selectedIndex = -1;
			}
		} else {
			// Otherwise, there are multiple items in that slot so the item count can
			// be simply decremented
			inventory.get(selectedIndex).decreaseItemCount();
		}
		
		// If the player removed an item which allowed them to no longer be overburdened
		// then they are set to no longer be overburdened
		if(curBurden + slotBurden < burdenCapacity) {
			overBurdened = false;
		}
	}
	
	// Method Body: The allocateSlotPos method will calculate the current x positions
	// of all of the slots in the player's inventory. This method is called
	// whenever the player has an item added or the player drops an item.
	private void allocateSlotPos() {
		// Local Variables
		// The double curX stores the current x position for the slot it is initially
		// set to make the slot 20 pixels to the right of the start of the screen
		double curX = 20 + InventorySlot.getWidth() / 2;
		
		// Method Body
		// All of inventory slots have their current x position calculated, where the 
		// subsequent inventory slots have 5 pixels of space from the previous slot
		for(int i = 0; i < inventory.size(); i++) {
			inventory.get(i).setX(curX);
			curX += InventorySlot.getWidth() + 5;
		}
	}
	
	// Method Description: The calculateAnimationFrames method is used to initialize then number
	// of frames in each image in an animation. This method is only called once in the constructor
	// but it is made into another method to avoid clutter in the constructor.
	//
	// The way that the number of frames for each image is calculated is that the weights of each
	// image is summed together and the fraction of the image's weight that is the total weight
	// is multiplied by the total number of frames to get the relative number of frames that
	// the frame should be displayed. This results in a number of frames that is representative
	// of the weight of the image relative to others in the animation. These are always rounded up, 
	// so the weights may not be exactly representative of the true time, to avoid getting zero 
	// frames for some images.
	private void calculateAnimationFrames() {
		// Local Variables
		// The double weightSum stores the sum of the weights of the images in an animation
		double weightSum = 0;
		
		// Method Body
		// The weight sum for the walk animation is calculated
		for(int i = 0; i < walkFrameWeight.length; i++) {
			weightSum += walkFrameWeight[i];
		}
		// The walkFrames array is declared so that the number of frames for each image can
		// be stored
		walkFrames = new int[walkFrameWeight.length];
		for(int i = 0; i < walkFrames.length; i++) {
			// The number of frames that each image should have is calculated and the program 
			// always rounds up the calculated number of frames to avoid getting zero as number
			// of frames
			walkFrames[i] = (int)Math.ceil(walkFrameWeight[i] / weightSum * totalWalkFrames);
		}
		
		// The number of frames for the running animation is calculated similarly to the walking
		// animation
		weightSum = 0;
		for(int i = 0; i < runFrameWeight.length; i++) {
			weightSum += runFrameWeight[i];
		}
		runFrames = new int[runFrameWeight.length];
		for(int i = 0; i < runFrames.length; i++) {
			runFrames[i] = (int)Math.ceil(runFrameWeight[i] / weightSum * totalRunFrames);
		}
		
		// The number of frames for the swatting animation is calculated similarly to the walking
		// animation
		weightSum = 0;
		for(int i = 0; i < swatFrameWeight.length; i++) {
			weightSum += swatFrameWeight[i];
		}
		swatFrames = new int[swatFrameWeight.length];
		for(int i = 0; i < swatFrames.length; i++) {
			swatFrames[i] = (int)Math.ceil(swatFrameWeight[i] / weightSum * totalSwatFrames);
		}
		
		// The number of frames for the picking animation is calculated similarly to the walking
		// animation
		weightSum = 0;
		for(int i = 0; i < pickFrameWeight.length; i++) {
			weightSum += pickFrameWeight[i];
		}
		pickFrames = new int[pickFrameWeight.length];
		for(int i = 0; i < pickFrames.length; i++) {
			pickFrames[i] = (int)Math.ceil(pickFrameWeight[i] / weightSum * totalPickFrames);
		}
	}
	
	// Method Description: The method takeDamage will make the player take a certain amount of
	// damage which is passed in by decreasing their health by that damage.
	// Parameters: The double damage stores how much health should be removed
	public void takeDamage(double damage) {
		// The player's health is decreased by the amount of damage
		health -= damage;
	}
	
	// Method Description: The method respawn will put the player back at their spawning position
	// and remove all of the player's items. The items will be spread out where the player originally
	// was. The player's animation, health, and stamina will be reset as well.
	public void respawn() {
		// Local Variables
		// The double dropX stores the x location where the item should be dropped
		double dropX;
		// The double dropY stores the y location where the item should be dropped
		double dropY = y - height * 3 / 4;
		// The Random variable rand is used to generate random integers more easily to get the dropX
		Random rand = new Random();
		
		// Method Body
		for(int i = 0; i < inventory.size(); i++) {
			for(int j = 0; j < inventory.get(i).getItemCount(); j++) {
				// All of the items in the player's inventory are looped over and dropped onto the map
				// at a random location within the respawnDropRange of the player's original location
				dropX = x + 2 * rand.nextInt(respawnDropRange) - respawnDropRange;
				Game.addItem(new EnvironmentItem(dropX, dropY, inventory.get(i).getItemType(), relX - dropX));
			}
		}
		// The player's inventory is reset to having no items
		selectedIndex = -1;
		inventory.clear();
		curBurden = 0;
		slotBurden = 0;
		overBurdened = false;
		
		// The player is put back at their spawning position and set to not be moving
		x = spawnX;
		y = spawnY;
		velX = 0;
		velY = 0;
		// The player does not spawn on the ground as an effect is played to make the 
		// player slightly higher than the ground to signify that they have respawned
		onGround = false;
		// The player spawns facing the right side
		facingRight = true;
		// The health and stamina of the player are set back to their maximum values
		health = maxHealth;
		stamina = maxStamina;
		// The animations of the player are set to false as they should have their
		// actions reset
		walking = false;
		if(keyDown[0] || keyDown[1]) {
			// However, since the keyPressed method will not be reactivated if the player
			// does not let go of their keys, it has to be checked if the player is attempting
			// to move so then they are set to be walking.
			walking = true;
		}
		// All other animations do not handle their animation in a similar way to the walking
		// so they are handled differently.
		running = false;
		jumping = false;
		swatting = false;
		picking = false;
		// The animation is reset
		curImageTick = 0;
		curImageIndex = 0;
		curAnimation = "";
		
		// The player has changed their x position as they have respawned, so the changedX boolean
		// is set to true to indicate that the game's surroundings should be changed
		changedX = true;
	}
	
	// Getter Methods
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public boolean getChangedX() {
		return changedX;
	}
	
	public double getRelX() {
		return relX;
	}
	
	public static double getPickUpReach() {
		return pickUpReach;
	}
	
	public double getWidth() {
		return width;
	}
	
	public double getHeight() {
		return height;
	}
	
	public boolean getKeyDown(int index) {
		return keyDown[index];
	}
	
	public boolean getFacingRight() {
		return facingRight;
	}
	
	public boolean getSwatting() {
		return swatting;
	}
	
	public int getInventorySize() {
		return inventory.size();
	}
	
	public double getSwatStartReach() {
		return swatStartReach;
	}
	
	public double getSwatEndReach() {
		return swatEndReach;
	}
	
	// Method Description: The getSelectedItem method will get the item type
	// of the selected item of the player, as it is easier to get the String
	// representation of the current selected item rather than the Item class
	// itself
	// Return: The method will return a String storing the item type of the 
	// selected item
	public String getSelectedItem() {
		if(selectedIndex >= 0) {
			// If the player has a selected index, then the item type at the
			// selected index is returned
			return inventory.get(selectedIndex).getItemType();
		}
		// If there is no selected item then a blank item type is returned
		return "";
	}
	
	// Setter Methods
	public void setKeyDown(int index, boolean value) {
		keyDown[index] = value;
	}
	
	public void setChangedX(boolean changedX) {
		this.changedX = changedX;
	}
	
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	
	public void setWalking(boolean walking) {
		this.walking = walking;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	public void setJumping(boolean jumping) {
		this.jumping = jumping;
	}
	
	public void setPicking(boolean picking) {
		this.picking = picking;
		// When the player wishes to pick up another item the picking
		// animation is reset as the picking animation determines when
		// the actual pick up occurs, so if the player spams the pick up
		// button it won't actually pick anything up, simply resetting the
		// animation constantly
		if(picking) {
			curImageTick = 0;
			curImageIndex = 0;
		}
	}
	
	public void setSwatting(boolean swatting) {
		this.swatting = swatting;
	}
	
	public void setFacingRight(boolean facingRight) {
		this.facingRight = facingRight;
	}
}
