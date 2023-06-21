/*
 * Harry He
 * June 18 2023
 * 
 * Program Description:
 * The program will run a 2D game with keyboard input used to play the actual game, and
 * mouse input used to go through the menu. The game will allow a player to play as a 
 * scout in the wilderness, being able to make fire, swat mosquitoes and complete quests
 * sought out by NPC's. The player's actions work based on animations, where actions are
 * completed during an animation rather than immediately after a key is pressed. The menu
 * will provide 5 high scores for 3 categories where the high scores must be unique in
 * player user name, so previous high scores achieved by the same player are replaced. The
 * menu will also provide instructions on how to play and give credits to the development
 * team.
 */

// Importing the necessary classes so that the game can be rendered, the keyboard and
// mouse input handled, the high score can be managed and so that mosquitoes can be
// randomly placed
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;

// The main Game class extends the JPanel class so that it can itself be shown as the 
// window for the game and implements the Runnable, MouseListener, and KeyListener interface
// to allow for threading, mouse input and keyboard input.
public class Game extends JPanel implements Runnable, MouseListener, KeyListener {
	// Static Variables
	// All of the variables in this class are static as there should only ever be one
	// game. This is done so that other classes do not need to hold a reference to the game
	// object, but can rather call methods like Game.addItem() more easily
	
	// The integer WIDTH and HEIGHT variables represent the width and height
	// of the window, JPanel, which the game will be played in.
	public static final int WIDTH = 1080;
	public static final int HEIGHT = 675;
	// The integer groundHeight stores the height of the ground in the game, the distance
	// above the bottom of the window where the player can stand
	public static final int groundHeight = 100;
	// The boolean running stores if the game should still be running
	private static boolean running;
	
	// The boolean inMenu stores if the player is currently in the menu or not
	private static boolean inMenu;
	// The boolean paused stores if the player is in the pause screen
	private static boolean paused;
	// The Menu menu stores a reference to the menu used for the game, so that it can
	// be shown in this driver class.
	private static Menu menu;
	
	// The AnimatedBackground backgroundImage allows the background of the game to
	// be rendered with swaying trees
	private static AnimatedBackground backgroundImage;
	// The Ground ground represents the ground in the game, and will render in the ground
	// that the player stands on
	private static Ground ground;
	// The Player player stores the player in the game which holds all of the information
	// about the player and their inventory
	private static Player player;
	// The integer mosquitoesSwatted stores the number of mosquitoes that the player has
	// swatted, used for the highscores
	private static int mosquitoesSwatted;
	// The integer campfireTickTime stores the number of ticks that the player has lit the
	// campfire, also used for highscores.
	private static int campfireTickTime;
	// The integer distanceTravelled stores the number of pixels that the player has travelled
	// horzontally, also used for highscores.
	private static int distanceTravelled;
	
	// The integer mapEnd stores the integer value where the map should end, where the mosquitoes
	// can only spawn from 0 and to the map end
	private int mapEnd;
	// The Plant array plants stores all of the plants used in the map of the game where these
	// can drop items to the player.
	private static Plant[] plants;
	// The List of Environment items stores a LinkedList of all of the items that are in the game.
	// The reason why a LinkedList is used is because items can be removed from anywhere in the
	// list if the player picks them up, which makes LinkedList better as it is more efficient
	// for removing elements anywhere in the list.
	// Note that the items and mosquitoes variables appear to be generic Lists but are backed
	// by a LinkedList and an ArrayList respectively. The reason why they are generic lists
	// are because they have to be synchronized using the Collections.synchronizedList method
	// which only returns generic lists. Other lists are not synchronized as they are not ticked
	// in the run method like these two, so the chance that they result in a 
	// ConcurrentModificationException is fairly low.
	private static List<EnvironmentItem> items;
	// The List of Mosquito objects mosquitoes stores all of the mosquitoes in the game
	// An ArrayList is used here because mosquitoes are removed in batches, the
	// speed provided by the LinkedList when removing items is not necessarily advantageous
	// here because mosquitoes are made into a new Arraylist every time since multiple mosquitoes can
	// be removed. Another use case of ArrayLists is in the Player's inventory.
	private static List<Mosquito> mosquitoes;
	// The Campfire array campfires stores all of the campfires on the map, which the player can
	// add resources to and light the fire.
	private static Campfire[] campfires;
	// The Scouter array scouters stores all of the scouters on the map, which the player can interact
	// with and also be given items
	private static Scouter[] scouters;
	// The boolean metricsLoaded stores if the FontMetrics of the Campfire, Scouter, and DynamicText
	// classes have been loaded in yet. Those metrics are loaded in when the game first starts
	// as they can take some time to load. The FontMetrics can be pre-loaded because the game does not
	// change in size
	private static boolean metricsLoaded;
	
	// The integer mosquitoTickRem stores the number of ticks that remain before next mosquito wave
	// is spawned
	private static int mosquitoTickRem;
	// The integer tickDelayIndivMosquito stores the number of ticks that the next wave will have
	// to wait because of each mosquito already spawned in
	private static final int tickDelayIndivMosquito = 120;
	// The double tickRemovalFactor stores by what factor the ticks to spawn back
	// mosquitoes will be subtracted of the individual tick delay when mosquitoes are swatted
	// It is put at a fraction so that the player is incentivized to swat mosquitoes
	private static final double tickRemovalFactor = 0.5;
	// The integer avgMosquitoWave stores the average number of mosquitoes that are spawned
	// per wave
	private static final int avgMosquitoWave = 3;
	// The integer mosquitoWaveRange stores how much the number of mosquitoes in each wave
	// can deviate. If the mosquitoRange makes the mosquitoes to spawn 0, the wave will only spawn 1
	// mosquito. The minimum number of mosquitoes that can spawn is 1 and the maximum is 3 + 5 - 1
	// which is 7.
	private static final int mosquitoWaveRange = 5;
	// The integer avgSpawnY stores the average y position where mosquitoes are spawned
	private static final int avgSpawnY = HEIGHT - groundHeight - 60;
	// The integer spawnY range stores the amount of y position where the mosquito
	// spawning y can deviate. If the mosquitoRange makes the mosquitoes spawn under 10 above 
	// the ground, the mosquitoes will spawn 10 above the ground instead
	private static final int spawnYRange = 100;
	
	// The HashMap of String keys to Score values mosquitoScores stores the mosquitoes swatted
	// score achieved by the player name passed in. This HashMap is necessary so that it can be
	// checked if the player has achieved a high score before, and if that high score should
	// be replaced or not.
	private static HashMap<String, Score> mosquitoScores;
	// The TreeSet of Scores mosquitoLeaderboard stores the top 5 players who swatted mosquitoes
	// with the Score that they achieved, which stores the name, score, and time when the score
	// was achieved.
	private static TreeSet<Score> mosquitoLeaderboard;
	// The HashMap of String keys to Score values campfireScores stores the campfire ticks
	// score achieved by the player name passed in.
	private static HashMap<String, Score> campfireScores;
	// The TreeSet of Scores campfireLeaderboard stores the top 5 players who lit the campfire
	// with the Score that they achieved
	private static TreeSet<Score> campfireLeaderboard;
	// The HashMap of String keys to Score values distanceScores stores the distance traveled
	// score achieved by the player name passed in.
	private static HashMap<String, Score> distanceScores;
	// The TreeSet of Scores distanceLeaderboard stores the top 5 players who traveled a distance
	// with the Score that they achieved
	private static TreeSet<Score> distanceLeaderboard;
	// The ArrayList of Strings campfireStrScores stores the highscores of the campfire ticks
	// but into a more understandable String format in terms of the time the campfire was lit.
	private static ArrayList<String> campfireStrScores;
	// The integer saveScoreTicksRem stores the number of ticks that remain before the next
	// highscores are saved. This is done so that high scores can be periodically saved, as
	// the player must pause the game if they wish to save their scores manually, so to keep
	// some updated high scores if the player forgets to save, the game will periodically save the scores.
	private static int saveScoreTicksRem;
	// The integer saveScoreDelay stores the number of ticks that have to be waited between 
	// high score saves which is about 2 minutes.
	private static final int saveScoreDelay = 7200;
	
	// Method Description: The constructor for the Game class will initialize all of the components
	// of the game and load in the highscores of the game. The constructor will also enable
	// the graphics of the application to be shown and the user to have keyboard and mouse input.
	public Game() {
		// The menu is initialized to put the player in the main menu page where they 
		// are not paused.
		inMenu = true;
		paused = false;
		menu = new Menu(this);
		// The background and ground of the game are initialized
		backgroundImage = new AnimatedBackground();
		ground = new Ground();
		
		// The player is put into the game with no scores initially as they have not done
		// anything yet. The player is spawned slightly above groundHeight so that when 
		// the player respawns they know as they are slightly higher
		player = new Player(800, HEIGHT - groundHeight - 40);
		mosquitoesSwatted = 0;
		campfireTickTime = 0;
		distanceTravelled = 0;
		
		// The map of the game is declared
		initMap();
		// The Scouters are set to see the player so that they can interact with them
		Scouter.setPlayer(player);
		// The metrics are set to be loaded when the first paint component is called. They
		// have to be initialized there because the metrics need a graphics instance to refer to
		metricsLoaded = false;
		// The mosquitoes are declared to be a synchronized list
		mosquitoes = Collections.synchronizedList(new ArrayList<>());
		// The mosquitoes are set to attack the player and the next wave of mosquitoes should
		// spawn immediately
		Mosquito.setPlayer(player);
		mosquitoTickRem = 0;

		// All of the elements of the map's relative x is found to put it relative to the player
		repositionElements();
		
		// The highscores variables are initialized
		mosquitoScores = new HashMap<>();
		mosquitoLeaderboard = new TreeSet<>();
		campfireScores = new HashMap<>();
		campfireLeaderboard = new TreeSet<>();
		distanceScores = new HashMap<>();
		distanceLeaderboard = new TreeSet<>();
		campfireStrScores = new ArrayList<>();
		// The next highscore update is set to be in 2 minutes
		saveScoreTicksRem = saveScoreDelay;
		// The highscores are loaded in
		loadScores();
		updateScores();
		
		// The window is declared, with the given width and height
		// declared at the start of the file
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		// The default background color is white, however, the ambient background image
		// should always apart be the background of the game
		setBackground(new Color(255, 255, 255));
		// Adding mouse and keyboard input
		addKeyListener(this);
		addMouseListener(this);
		this.setFocusable(true);

		// The thread is declared and started which will be used to
		// start the timer and as such the game
		Thread thread = new Thread(this);
		running = true;
		thread.start();
	}
	
	// Method Description: The tick method will update all of the components of the game
	// and allow the game's movement and events to occur. If the menu is the current
	// state then the menu's tick method will handle the updating of the components
	public void tick() {
		if(inMenu) {
			// If the game is currently in the menu then the menu is updated
			menu.tick();
		} else {
			// The background image and player are updated
			backgroundImage.tick();
			player.tick();
			// If the player changed their x position then the elements of the game
			// have to shift their position to make sure that the player is at a
			// constant frame of reference
			if(player.getChangedX()) {
				repositionElements();
				player.setChangedX(false);
			}
			
			// The plants and campfires are updated, where the plants will sometimes
			// drop items and the campfire will burn if lit and increase the player's score
			for(int i = 0; i < plants.length; i++) {
				plants[i].tick();
			}
			for(int i = 0; i < campfires.length; i++) {
				campfires[i].tick();
			}
			// The EnvironmentItems are updated where they can fall down
			synchronized(items) {
				for(EnvironmentItem item: items) {
					item.tick();
				}
			}
			// The mosquitoes are updated where they can move around and attack the player
			synchronized(mosquitoes) {
				for(int i = 0; i < mosquitoes.size(); i++) {
					mosquitoes.get(i).tick();
				}
			}
			
			// If the time has come for the mosquitoes to be spawned in then a new wave
			// is spawned in
			if(mosquitoTickRem <= 0) {
				spawnMosquitoes();
			} else {
				mosquitoTickRem--;
			}
			
			// Periodically the high scores will be saved, should be
			// every 2 minutes
			if(saveScoreTicksRem <= 0) {
				updateScores();
				saveScores();
				saveScoreTicksRem = saveScoreDelay;
			} else {
				saveScoreTicksRem--;
			}
		}
	}
	
	// Method Description: The paintComponent method will render in all of the graphics of
	// the game. The method will also initialize the metrics used in the Campfire, Scouter
	// and DynamicText class if it has not been done so already.
	// Parameters: The Graphics g stores the graphics of the panel where the game should
	// be rendered.
	public void paintComponent(Graphics g) {
		// The panel is cleared
		super.paintComponent(g);
		// If the player is in the game or they are in the pause screen, then
		// the game is rendered in
		if(!inMenu || paused) {
			// The metrics can take a long time to load, so the metrics are loaded
			// once during the first render of the actual game
			if(!metricsLoaded) {
				Campfire.initMetrics(g);
				Scouter.initMetrics(g);
				DynamicText.initMetrics(g);
				metricsLoaded = true;
			}
			
			// The background image of the game is rendered
			backgroundImage.render(g);
			// Render the images that are behind the player first
			// The plants looped over and rendered in
			for(int i = 0; i < plants.length; i++) {
				// Many of the components here have smart rendering, so if the 
				// plant is on the screen then it is only rendered, a simialr thing is
				// done with campfires, mosquitoes, scouters, and environment items.
				if(plants[i].getRelX() + plants[i].getWidth() / 2 >= 0 
						&& plants[i].getRelX() - plants[i].getWidth() / 2 <= WIDTH) {
					plants[i].render(g);
				}
			}
			// The campfires are looped over and rendered in
			for(int i = 0; i < campfires.length; i++) {
				if(campfires[i].getRelX() + campfires[i].getWidth() / 2 >= 0 
						&& campfires[i].getRelX() - campfires[i].getWidth() / 2 <= WIDTH) {
					// Check if the player is within the bounds to render the menu, the player has
					// to be overlapping or on the edge of the campfire.
					if(campfires[i].getX() - campfires[i].getWidth() / 2 <= player.getX() + player.getWidth() / 2
							&& campfires[i].getX() + campfires[i].getWidth() / 2 >= player.getX() - player.getWidth() / 2) {
						// If the player is within bounds to render the campfire menu, then the pop up menu
						// is rendered
						campfires[i].render(g, true);
					} else {
						// Otherwise, only the campfire is rendered
						campfires[i].render(g, false);
					}
				}
			}
			
			// The Scouters are looped over rendered into the game
			for(int i = 0; i < scouters.length; i++) {
				if(scouters[i].getRelX() + scouters[i].getWidth() / 2 >= 0 
						&& scouters[i].getRelX() - scouters[i].getWidth() / 2 <= WIDTH) {
					scouters[i].render(g);
				}
			}
			// The ground is rendered into the game
			ground.render(g);

			// The player is rendered into the game
			player.render(g);

			// Render the images in front the player after
			synchronized(items) {
				// All of the items are looped over and rendered with smart rendering
				// as mentioned before. Here the items have to be synchronized to 
				// avoid the items being modified as they are rendered
				for(EnvironmentItem item: items) {
					if(item.getRelX() + item.getWidth() / 2 >= 0
							&& item.getRelX() - item.getWidth() / 2 <= Game.WIDTH) {
						item.render(g);
					}
				}
			}

			// Render the mosquitoes in the game on top of other environment factors as
			// the player has to be able to see the mosquitoes even if they are
			// low and next to environment items
			synchronized(mosquitoes) {
				// The mosquitoes are looped over and rendered into the game and they
				// have to be synchronized to avoid being modified as they are being rendered
				for(int i = 0; i < mosquitoes.size(); i++) {
					if(mosquitoes.get(i).getRelX() + mosquitoes.get(i).getWidth() / 2 >= 0
							&& mosquitoes.get(i).getRelX() - mosquitoes.get(i).getWidth() / 2 <= Game.WIDTH) {
						mosquitoes.get(i).render(g);
					}
				}
			}

			// Render the player's heads up display on top of any of the other images
			player.renderHUD(g);
		}
		
		// If the player is in the menu including the pause screen the menu is rendered
		// in, in its current menu state
		if(inMenu) {
			menu.render(g);
		}
	}
	
	// Method Description: The keyPressed method will activate any time a key is pressed by the
	// player. The method will update any of the corresponding events in the game, like performing
	// player actions or filling in text fields.
	// Parameters: The KeyEvent e stores information about what key was pressed
	public void keyPressed(KeyEvent e) {
		// Local Variables
		// The integer key stores a representative number of what key was pressed so they
		// can be checked with the constants in the KeyEvent class
		int key = e.getKeyCode();
		// The integer slot is only used when the player presses a number key which stores which
		// index slot they chose to avoid excess computation
		int slot;
		// The boolean interacted is only used when the player is trying to interact with things
		// on the map, storing if the player interacted with a campfire or scouter and that
		// the player's picking up animation should not be played then.
		boolean interacted;
		
		// Method Body
		if(inMenu) {
			// If the player is in the menu then the menu is updated based on the key pressed
			menu.keyPressed(e);
		} else {
			// Otherwise the player is in the game
			if(key == KeyEvent.VK_A) {
				// If the player presses A they wish to move left, so they
				// should be set to walking, facing left, and if the player is
				// trying to sprint, that they are running.
				player.setKeyDown(0, true);
				player.setWalking(true);
				player.setFacingRight(false);
				if(player.getKeyDown(4)) {
					player.setRunning(true);
				}
			} else if(key == KeyEvent.VK_D) {
				// If the player presses D then they wish to move right, then they should
				// be set to walking, facing right, and if they are trying to sprint, running.
				player.setKeyDown(1, true);
				player.setWalking(true);
				player.setFacingRight(true);
				if(player.getKeyDown(4)) {
					player.setRunning(true);
				}
			} else if(key == KeyEvent.VK_SPACE) {
				// If the player presses Space then they are trying to jump and the player
				// will update the key pressed down to set the player to jump
				player.setKeyDown(2, true);
			} else if(key == KeyEvent.VK_K) {
				// If the player presses K then they are trying to swat
				// The player can only attack if they are holding a racket
				if(player.getSelectedItem().equals("Racket")) {
					// The player is set to be trying to attack
					player.setKeyDown(3, true);
					// If the player attacks, cancel picking and jumping moves
					player.setPicking(false);
					player.setJumping(false);
				}
			} else if(key == KeyEvent.VK_Q) {
				// If the player presses Q then they are trying to drop their selected item
				player.dropItem();
			} else if(key == KeyEvent.VK_L) {
				// Interact Button
				// If the player presses L then they are trying to interact or pick up items
				// The player can only pick up items if they are no longer swatting
				if(!player.getSwatting()) {
					// If the player has an item that they can pick up, they should first pick up the item
					// first, as you can only pick up items a finite number of times but interact
					// an infinite number of times.
					// Check if the player is in range of picking up an item
					if(checkDropsInRange()) {
						// If they are in range to pick up and item then they should be set to pick it up
						player.setPicking(true);
						// The player is now picking so cancel jumping moves so that the player
						// does not jump up after picking
						player.setJumping(false);
					} else {
						// Otherwise, they are not in range of picking up items to then check if the player 
						// should interact with the camp fire or with NPCs
						// The boolean interacted is set to false as the player is unknown to have interacted
						// with any campfires or scouters yet
						interacted = false;
						// The campfires are looped over and checked if they are in range of the player
						for(int i = 0; i < campfires.length; i++) {
							if(Math.abs(campfires[i].getX() - player.getX()) <= 
									Player.getPickUpReach() + campfires[i].getWidth() / 2 + player.getWidth() / 2) {
								// If the campfire is in range, then the player will interact with that campfire
								interacted = true;
								// Add the player's selected item to the campfire if it is valid
								if(Item.campfireValid(player.getSelectedItem())) {
									campfires[i].addItem(player.getSelectedItem());
									player.removeItem();
								} else if(player.getSelectedItem().equals("Flint And Steel")) {
									// Otherwise, check if the player wants to light the fire
									campfires[i].lightFire();
								}
							}
						}
						
						// Check if they should interact with NPC's, the scouters are looped over and checked
						// if they are in range of the player
						for(int i = 0; i < scouters.length; i++) {
							if(Math.abs(scouters[i].getX() - player.getX()) <= 
									Player.getPickUpReach() + scouters[i].getWidth() / 2 + player.getWidth() / 2) {
								// If the scouter is in range, then the player will interact with the sctouer
								interacted = true;
								scouters[i].interact();
							}
						}
						
						if(!interacted) {
							// If the player is not in range of those, the player is set to pick up an item
							player.setPicking(true);
							// The player is now picking so cancel jumping moves so that the player
							// does not jump up after picking
							player.setJumping(false);
						}
					}
				}
			} else if(key == KeyEvent.VK_SHIFT) {
				// If the player presses Shift then they are trying to sprint
				player.setKeyDown(4, true);
				if(player.getKeyDown(0) || player.getKeyDown(1)) {
					// If the player is trying to move then they are set to be running
					// now that they are trying to sprint
					player.setRunning(true);
				}
			} else if(e.getKeyChar() >= '1' && e.getKeyChar() <= '9') {
				// If the player presses the number keys allow them to select
				// an inventory slot. However, note that while you cannot select if you have
				// more than 9 items at a time, there are actually less than 9 items in the 
				// game, so it is impossible to have this occur
				// The player's selected index is set to the selected slot
				slot = e.getKeyChar() - '1';
				if(slot < player.getInventorySize()) {
					player.setSelectedIndex(slot);
				}
			} else if(key == KeyEvent.VK_P) {
				// If the player presses P then the game should be paused
				// The menu goes to the pause state
				inMenu = true;
				paused = true;
				menu.setToPause();
				// When the player pauses the player will be set to doing
				// no actions
				for(int i = 0; i < 5; i++) {
					player.setKeyDown(i, false);
				}
				player.setWalking(false);
				player.setRunning(false);
				player.setJumping(false);
				player.setPicking(false);
				player.setSwatting(false);
				
				// If the player tries to pause, they may go back
				// to the menu, so save all of the high scores
				// so that they are updated in the menu
				updateScores();
				saveScores();
			} else if(key == KeyEvent.VK_T) {
				// If the player presses T the quests will be advanced
				// for ease of use
				Scouter.nextQuestLine();
			}
		}
	}

	// Method Description: The keyReleased method will be called whenever the player
	// releases a key. This will update the actions performed by the player that
	// can be repeatedly performed to stop being performed.
	// Parameters: The KeyEvent e stores information about the key that was released
	public void keyReleased(KeyEvent e) {
		// Local Variables
		// The integer key stores a representative number of what key was pressed so they
		// can be checked with the constants in the KeyEvent class
		int key = e.getKeyCode();
		
		// Method Body
		if(!inMenu) {
			// If the player is in the actual game then the key releases
			// will actually affect the player
			if(key == KeyEvent.VK_A) {
				// The player released the A key so they should be set
				// to no longer be walking left
				player.setKeyDown(0, false);
				// If the D key is pressed down then
				// the player is still walking
				if(!player.getKeyDown(1)) {
					// Otherwise, the player is no longer walking or running
					player.setWalking(false);
					player.setRunning(false);
				}
			} else if(key == KeyEvent.VK_D) {
				// The player released the D key so they should be set to no longer
				// be walking right
				player.setKeyDown(1, false);
				// If the A key is pressed down then
				// the player is still walking
				if(!player.getKeyDown(0)) {
					// Otherwise, the player is no longer walking or running
					player.setWalking(false);
					player.setRunning(false);
				}
			} else if(key == KeyEvent.VK_SPACE) {
				// If the player released the Space key then they are set to no longer
				// continually jump
				player.setKeyDown(2, false);
			} else if(key == KeyEvent.VK_K) {
				// If the player released the K key then they are set to longer
				// continually swat
				player.setKeyDown(3, false);
			} else if(key == KeyEvent.VK_SHIFT) {
				// If the player released the Shift key then they are set to no longer
				// be trying to sprint
				player.setKeyDown(4, false);
				player.setRunning(false);
			}
		}
	}
	
	// Method Description: The mousePressed method will be called whenever the player
	// presses with their mouse. Since the game only uses keyboard input only the
	// menu will be updated if the game is currently in the menu
	// Parameters: The MouseEvent e stores information about where the mouse was pressed
	// down.
	public void mousePressed(MouseEvent e) {
		// The menu is the only state where mouse presses will change the state of the game
		if(inMenu) {
			// Let the menu handle the pressing of the mouse as it has easier access
			// to the variables that display the menu
			menu.mousePressed(e);
		}
	}
	
	// Method Description: The initMap method will initialize the map of the game
	public void initMap() {
		// The plants, campfires and scouters array are initialized to the number of 
		// those elements that are added
		plants = new Plant[56];
		campfires = new Campfire[2];
		scouters = new Scouter[3];
		// All of the plants of the map are declared in
		// Campsite Zone
		plants[0] = new Plant(140, HEIGHT - groundHeight, "Pine Tree", false);
		plants[1] = new Plant(330, HEIGHT - groundHeight, "Pine Tree", true);
		plants[2] = new Plant(600, HEIGHT - groundHeight, "Birch Tree", false);
		plants[3] = new Plant(1500, HEIGHT - groundHeight, "Birch Tree", false);
		plants[4] = new Plant(1700, HEIGHT - groundHeight, "Pine Tree", true);
		plants[5] = new Plant(2000, HEIGHT - groundHeight, "Birch Tree", true);
		plants[6] = new Plant(2400, HEIGHT - groundHeight, "Pine Tree", false);
		plants[7] = new Plant(2600, HEIGHT - groundHeight, "Pine Tree", false);
		
		// Transition Area
		plants[8] = new Plant(3000, HEIGHT - groundHeight, "Birch Tree", false);
		plants[9] = new Plant(3200, HEIGHT - groundHeight, "Birch Tree", false);
		plants[10] = new Plant(3450, HEIGHT - groundHeight, "Birch Tree", true);
		// Put cotton plants afterwards so they appear before trees
		plants[11] = new Plant(3100, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[12] = new Plant(3300, HEIGHT - groundHeight, "Cotton Plant", false);
		
		// Cotton Field
		plants[13] = new Plant(3600, HEIGHT - groundHeight, "Cotton Plant", false);
		plants[14] = new Plant(3700, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[15] = new Plant(3750, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[16] = new Plant(3810, HEIGHT - groundHeight, "Cotton Plant", false);
		plants[17] = new Plant(3880, HEIGHT - groundHeight, "Cotton Plant", false);
		plants[18] = new Plant(3930, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[19] = new Plant(4000, HEIGHT - groundHeight, "Cotton Plant", false);
		plants[20] = new Plant(4090, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[21] = new Plant(4150, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[22] = new Plant(4220, HEIGHT - groundHeight, "Cotton Plant", false);
		plants[23] = new Plant(4300, HEIGHT - groundHeight, "Cotton Plant", false);
		plants[24] = new Plant(4350, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[25] = new Plant(4850, HEIGHT - groundHeight, "Pine Tree", false);
		plants[26] = new Plant(5000, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[27] = new Plant(5100, HEIGHT - groundHeight, "Cotton Plant", false);
		plants[28] = new Plant(5170, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[29] = new Plant(5220, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[30] = new Plant(5300, HEIGHT - groundHeight, "Cotton Plant", false);
		plants[31] = new Plant(5400, HEIGHT - groundHeight, "Cotton Plant", false);
		plants[32] = new Plant(5480, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[33] = new Plant(5540, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[34] = new Plant(5640, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[35] = new Plant(5720, HEIGHT - groundHeight, "Cotton Plant", false);
		plants[36] = new Plant(5820, HEIGHT - groundHeight, "Cotton Plant", false);
		plants[37] = new Plant(5910, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[38] = new Plant(6020, HEIGHT - groundHeight, "Cotton Plant", false);
		
		// Transition Area
		plants[39] = new Plant(6200, HEIGHT - groundHeight, "Birch Tree", true);
		plants[40] = new Plant(6400, HEIGHT - groundHeight, "Birch Tree", false);
		plants[41] = new Plant(6300, HEIGHT - groundHeight, "Cotton Plant", false);
		plants[42] = new Plant(6500, HEIGHT - groundHeight, "Cotton Plant", true);
		
		// Main Forest Wilderness
		plants[43] = new Plant(6650, HEIGHT - groundHeight, "Pine Tree", false);
		plants[44] = new Plant(6550, HEIGHT - groundHeight, "Birch Tree", true);
		plants[45] = new Plant(6790, HEIGHT - groundHeight, "Pine Tree", true);
		plants[46] = new Plant(6900, HEIGHT - groundHeight, "Birch Tree", false);
		plants[47] = new Plant(7200, HEIGHT - groundHeight, "Pine Tree", false);
		plants[48] = new Plant(7500, HEIGHT - groundHeight, "Pine Tree", true);
		plants[49] = new Plant(7740, HEIGHT - groundHeight, "Birch Tree", true);
		plants[50] = new Plant(7700, HEIGHT - groundHeight, "Cotton Plant", true);
		plants[51] = new Plant(7900, HEIGHT - groundHeight, "Pine Tree", false);
		plants[52] = new Plant(8070, HEIGHT - groundHeight, "Pine Tree", true);
		plants[53] = new Plant(8300, HEIGHT - groundHeight, "Pine Tree", true);
		plants[54] = new Plant(8600, HEIGHT - groundHeight, "Birch Tree", false);
		plants[55] = new Plant(8480, HEIGHT - groundHeight, "Porta-potty", false);
		
		// The campfires of the game are declared, one in the cotton field and another
		// at the spawning camp area
		campfires[0] = new Campfire(1200);
		campfires[1] = new Campfire(4550);
		// The scouters of the game are declared at their positions, two in the main
		// spawning camp and another in the cotton field
		scouters[0] = new Scouter(1000, "Bryant");
		scouters[1] = new Scouter(1450, "Hubert");
		scouters[2] = new Scouter(4400, "Braydon");
		// The map end is set to where the main ground of the map will end
		mapEnd = 8640;
		
		// The EnvironmentItems on the map is also declared here, where the items
		// LinkedList is a synchronized list that is initially blank
		items = Collections.synchronizedList(new LinkedList<>());
	}
	
	// Method Description: The repositionElements method will find the relative x
	// positions of all of the elements on the map so that the player is always
	// rendered in the center of the game. Mosquitoes do not have to set their relative x 
	// here as that is always done with in the tick method
	public void repositionElements() {
		// Local Variables
		// The double shiftX stores the amount of shift that has to be done so that the player's
		// x position becomes the relative x position. This indicates how much other
		// elements in the game have to shift as well.
		// Essentially how this works is that every thing in the game is moved over so
		// that it fits within the bounds of the game where the player is in the center
		double shiftX = player.getRelX() - player.getX();
		
		// Method Body
		// The ground's relative x position is determined
		ground.setRelX(ground.getX() + shiftX);
		// The plant's relative x position is found
		for(int i = 0; i < plants.length; i++) {
			plants[i].setRelX(plants[i].getX() + shiftX);
		}
		// The items relative x position is found
		synchronized(items) {
			for(EnvironmentItem item: items) {
				item.setRelX(item.getX() + shiftX);
			}
		}
		// The campfire's relative x position is found
		for(int i = 0; i < campfires.length; i++) {
			campfires[i].setRelX(campfires[i].getX() + shiftX);
		}
		// The scouter's relative x position is found
		for(int i = 0; i < scouters.length; i++) {
			scouters[i].setRelX(scouters[i].getX() + shiftX);
		}
	}
	
	// Method Description: The addItem method will add an EnvironmentItem to the items
	// LinkedList. This is done whenever a plant drops a new item or the player drops
	// an item.
	// Parameters: The EnvironmentItem item stores the item that should be added to the map
	public static void addItem(EnvironmentItem item) {
		synchronized(items){
			// The item is added to the items ArrayList
			items.add(item);
		}
	}
	
	// Method Description: The spawnMosquitoes method will spawn in a new wave of mosquitoes
	// into the game at a random position on the map. The only area where mosquitoes cannot
	// be spawned in is where the player spawns in, so that the player is safe when they first
	// load in the game.
	public void spawnMosquitoes() {
		// Local Variables
		// The Random variable rand is used to get the random integers about where the mosquitoes
		// should spawn and the number that spawns can be gotten more easily.
		Random rand = new Random();
		// The integer numSpawn stores the number of mosquitoes that should spawn
		int numSpawn = avgMosquitoWave + rand.nextInt(2 * mosquitoWaveRange) - mosquitoWaveRange;
		// The integer spawnX stores where the mosquitoes should spawn in their x value
		int spawnX = rand.nextInt(mapEnd - 1000);
		// The integer spawnY stores the y value where the mosquitoes should spawn
		int spawnY = avgSpawnY + rand.nextInt(2 * spawnYRange) - spawnYRange;
		
		// Method Body
		// The spawnX is altered so that mosquitoes will not spawn in the player's spawn area
		if(spawnX > 500) {
			spawnX += 1000;
		}
		// If there is set to be no mosquitoes that spawn, at least one has to spawn
		// so the number that spawn is set to be 1
		if(numSpawn <= 0) {
			numSpawn = 1;
		}
		// If the mosquitoes spawn too close the the ground, they are set to be at least a little
		// bit higher up
		if(spawnY > HEIGHT - groundHeight - 10) {
			spawnY = HEIGHT - groundHeight - 10;
		}
		synchronized(mosquitoes) {
			// All of the mosquitoes are added to the mosquito array, spawning them into the game
			for(int i = 0; i < numSpawn; i++) {
				mosquitoes.add(new Mosquito(spawnX, spawnY));
			}
		}
		
		// The mosquito ticks until the next spawn is set based on the number of mosquitoes that
		// are already in the game. This is to avoid having the mosquitoes build up.
		mosquitoTickRem = mosquitoes.size() * tickDelayIndivMosquito;
	}
	
	// Method Description: The swatMosquitoes method is called whenever the player swats the
	// mosquitoes midway through their animation. The method will remove any mosquitoes 
	// within attacking range of the player.
	public static void swatMosquitoes() {
		// Local Variables
		// The double dist stores the Euclidean distance of the mosquito to the player
		double dist;
		// The ArrayList of Mosquito objects newMosquitoes stores the mosquitoes that
		// were not swatted away
		List<Mosquito> newMosquitoes = Collections.synchronizedList(new ArrayList<>());
		// The double playerMidX stores the center x position of the player
		double playerMidX = player.getX();
		// The double playerMidY stores the center y position of the player
		double playerMidY = player.getY() - player.getHeight() / 2;
		
		// Method Body
		synchronized(mosquitoes) {
			// The mosquitoes are looped over and checked if they can be hit by the player
			for(int i = 0; i < mosquitoes.size(); i++) {
				// The distance of the mosquito to the player is calculated
				dist = Math.sqrt((mosquitoes.get(i).getX() - playerMidX) * (mosquitoes.get(i).getX() - playerMidX) + 
						(mosquitoes.get(i).getY() - playerMidY) * (mosquitoes.get(i).getY() - playerMidY));
				// The mosquito is checked if it is not within range of the player
				if(!(dist >= player.getSwatStartReach() && dist <= player.getSwatEndReach())) {
					// Mosquito is safe from the swat so it can be added to the new arraylist as it is still
					// in the game
					newMosquitoes.add(mosquitoes.get(i));
				} else {
					// Mosquito is within reach, but check if the player is oriented correctly. If the player cannot
					// hit the mosquito because of the direction they are facing, the mosquito is still in the game
					if(player.getFacingRight()) {
						// The player is facing right so check if the mosquito is to the left
						if(mosquitoes.get(i).getX() < player.getX()) {
							// The mosquito is to the left so it can be kept in the game
							newMosquitoes.add(mosquitoes.get(i));
						} else {
							// Mosquito was removed so the spawn delay decreases again
							// However, by a reduced quantity to incentivise the player to swat
							// mosquitoes
							mosquitoTickRem -= tickDelayIndivMosquito * tickRemovalFactor;
							mosquitoesSwatted++;
						}
					} else {
						// Player is facing left
						if(mosquitoes.get(i).getX() > player.getX()) {
							// Mosquito is to the right so it can be kept in the game
							newMosquitoes.add(mosquitoes.get(i));
						} else {
							// Mosquito was removed so the spawn delay decreases again
							mosquitoTickRem -= tickDelayIndivMosquito * tickRemovalFactor;
							mosquitoesSwatted++;
						}
					}
				}
			}
			// The mosquitoes in the game are set to the mosquitoes that were left over
			mosquitoes = newMosquitoes;
			
			// The quest to swat away mosquitoes is advanced if the player has swatted 10 mosquitoes
			if(mosquitoesSwatted >= 10 && Scouter.getQuestLine() == 0) {
				Scouter.nextQuestLine();
			}
		}
	}
	
	// Method Description: The method playerPickUp will have the player pick up the item
	// that is closest, then the one that is dropped first. For example, if the leaf and a stick 
	// were dropped in the same position, but the left was dropped first the leaf would be picked up first
	public static void playerPickUp() {
		// Local Variables
		// The integer itemIndex store the index of the item that is closest to the player
		int itemIndex = -1;
		// The double minDropDist stores the minimum distance that an item was to the player
		double minDropDist = Double.MAX_VALUE;
		// The ListIterator iter is the iterator used to traverse through the items LinkedList
		ListIterator<EnvironmentItem> iter = items.listIterator();
		// The EnvironmentItem curItem stores a reference to the current item gotten by the 
		// ListIterator so that it can be accessed multiple times which would not work
		// if the iter.next was constantly called instead.
		EnvironmentItem curItem;
		
		// Method Body
		synchronized(items) {
			// The items on the map are looped over and the closest item to the player
			// is determined
			while(iter.hasNext()) {
				curItem = iter.next();
				// It is also checked if the item is actually within the player's reach
				// as the player can only pick up items that they can reach.
				// The condition is that the item's end at least the player's pick up reach
				// to the left or right of the player's end and the item must be
				// at least below half of the player's height as the player drops down to pick things up.
				if(Math.abs(curItem.getX() - player.getX()) <= 
						Player.getPickUpReach() + curItem.getWidth() / 2 + player.getWidth() / 2
						&& curItem.getY() > player.getY() - player.getHeight() / 2) {
					if(Math.abs(curItem.getX() - player.getX()) < minDropDist) {
						// The item has a closer distance to the player so that is the one that is picked
						// up
						itemIndex = iter.previousIndex();
						minDropDist = Math.abs(curItem.getX() - player.getX());
					}
				}
			}
			
			// If an item was found that the player could pick up then it is picked up
			if(itemIndex >= 0) {
				// The current item is gotten
				curItem = items.get(itemIndex);
				// The item is attempted to be added to the player's inventory
				if(player.addItem(curItem.getType())) {
					// The item was successfully added
					// If the item was dropped from a plant, then the plant's drops is decreased
					// so that it can drop more items.
					if(curItem.getParentPlant() != null) {
						curItem.getParentPlant().decreaseNumItems();
					}
					// Since the item was successfully added, then the item is removed
					items.remove(curItem);
				}
			}
		}
	}
	
	// Method Description: The method checkDropsInRange checks if there are any dropped items
	// in range of the player. The method is used when determining if the player should pick
	// up an item or if they should interact with a scouter or campfire.
	// Return: The boolean stores if there are drops that could be picked up by the player
	public boolean checkDropsInRange() {
		// Local Variables
		// The ListIterator iter is used to traverse through the items LinkedList
		ListIterator<EnvironmentItem> iter = items.listIterator();
		// The EnvironmentItem curItem stores the current item that the iterator has gotten
		// so that it can be accessed multiple times.
		EnvironmentItem curItem;
		
		// Method Body
		synchronized(items) {
			// The items are looped over and checked if they can be picked up by the player
			while(iter.hasNext()) {
				curItem = iter.next();
				// If the item is within the pick up reach in the x directions and below
				// half of the player's height it can be picked up by the player
				if(Math.abs(curItem.getX() - player.getX()) <= 
						Player.getPickUpReach() + curItem.getWidth() / 2 + player.getWidth() / 2
						&& curItem.getY() > player.getY() - player.getHeight() / 2) {
					return true;
				}
			}
		}
		// If no items could be picked up by the player then the method will return false
		return false;
	}
	
	// Method Description: The method increaseCampfireTime will increase the number of ticks
	// that the current player's score for the amount of time the campfire was active by 1.
	public static void increaseCampfireTime() {
		// The player's campfire tick time is incremented
		campfireTickTime++;
	}
	
	// Method Description: The method addDistanceTravelled will increase the distance that the
	// player has traveled by the quantity passed in to increase their player's distance score.
	// Parameters: The integer dist stores the distance that the player has additionally traveled
	public static void addDistanceTravelled(int dist) {
		// The player's distance travelled is added to by the amount of distance passed in
		distanceTravelled += dist;
	}
	
	// Method Description: The enterGame method will set the game to be in the game state,
	// no longer in the menu.
	public void enterGame() {
		inMenu = false;
	}
	
	// Method Description: The unpause method will move the game from the pause menu back
	// into the game.
	public void unpause() {
		paused = false;
	}
	
	// Method Description: The resetScouterSpeech method will clear the dialogue of every scouter
	// in the game. This method is called whenever the questLine is advanced so that the 
	// dialogue will not suddenly jump.
	public static void resetScouterSpeech() {
		// The scouters are all looped over and their dialogue is cleared
		for(int i = 0; i < scouters.length; i++) {
			scouters[i].clearDialogue();
		}
	}
	
	// Method Description: The loadScores method will load in the scores of the
	// game from the hard drive to update the current scores of the game when the
	// game is first initialized.
	// Note that the input file should be of format for each score
	// name score timeAdded
	// and have a blank line in between each category
	public void loadScores() {
		// Local Variables
		// The BufferedReader inputFile allows the highscores to be read in from the
		// highscores.txt text file.
		BufferedReader inputFile;
		// The String line stores the line that is currently being read in from the input file
		String line;
		// The String name stores the name of the player that achieved the high score
		String name;
		// The integer score stores the score that the player achieved, which can represent different
		// things depending on the category of the highscore: Mosquitoes, Campfire, and Distance.
		int score;
		// The long timeAdded stores the time that the high score was achieved
		long timeAdded;
		
		// Method Body
		try {
			// The inputFile is declared to read in from the highscores text file
			inputFile = new BufferedReader(new FileReader("highscores.txt"));
			// The current line is read in from the highscore text file
			line = inputFile.readLine();
			// The highscores are determined for the mosquito category
			// Once the line is blank then the category has ended and the next one should
			// be accounted for
			for(int i = 0; i < 5 && line != null && !line.equals(""); i++) {
				// The timeAdded is determined from the last token of the input
				timeAdded = Long.parseLong(line.substring(line.lastIndexOf(' ') + 1));
				line = line.substring(0, line.lastIndexOf(' '));
				// The score is determined from the second last token of the input
				score = Integer.parseInt(line.substring(line.lastIndexOf(' ') + 1));
				line = line.substring(0, line.lastIndexOf(' '));
				// The name is simply the rest of the line
				name = line;
				// The current score is added to the mosquito leaderboard
				mosquitoScores.put(name, new Score(name, score, timeAdded));
				mosquitoLeaderboard.add(new Score(name, score, timeAdded));
				line = inputFile.readLine();
			}
			
			// Get to the next line after the blank string
			line = inputFile.readLine();
			
			// The highscores are determined for the campfire category
			for(int i = 0; i < 5 && line != null && !line.equals(""); i++) {
				timeAdded = Long.parseLong(line.substring(line.lastIndexOf(' ') + 1));
				line = line.substring(0, line.lastIndexOf(' '));
				score = Integer.parseInt(line.substring(line.lastIndexOf(' ') + 1));
				line = line.substring(0, line.lastIndexOf(' '));
				name = line;
				campfireScores.put(name, new Score(name, score, timeAdded));
				campfireLeaderboard.add(new Score(name, score, timeAdded));
				line = inputFile.readLine();
			}
			// Get to the next line after the blank string
			line = inputFile.readLine();
			
			// The highscores for the distance category is determined
			for(int i = 0; i < 5 && line != null && !line.equals(""); i++) {
				timeAdded = Long.parseLong(line.substring(line.lastIndexOf(' ') + 1));
				line = line.substring(0, line.lastIndexOf(' '));
				score = Integer.parseInt(line.substring(line.lastIndexOf(' ') + 1));
				line = line.substring(0, line.lastIndexOf(' '));
				name = line;
				distanceScores.put(name, new Score(name, score, timeAdded));
				distanceLeaderboard.add(new Score(name, score, timeAdded));
				line = inputFile.readLine();
			}
			// The input file is closed to avoid any resource leakages and so that the highscores
			// can be saved, as the text file cannot be accessed if it has not been closed
			inputFile.close();
		} catch (FileNotFoundException e) {
			// The highscore file was not found so the program tells the user
			// and the location (in the code) of the error.
			System.out.println("Error 407: Highscore File Not Found");
			e.printStackTrace();
		} catch (IOException e) {
			// If there was a critical error in reading the highscore file, then the user
			// is told and the location (in the code) of the error is printed.
			System.out.println("Error 405: Inputting Error");
			e.printStackTrace();
		}
	}
	
	// Method Description: The saveScores method will save all of the scores that are currently
	// on the game's highscore leaderboard to the hard drive so that it can be accessed later.
	// The highscores are saved in a specific way that matches with the way that they are read in.
	// Scores are outputted in the format
	// name score timeAdded
	// and there is a space in between when each category ends to signify that the next category's
	// highscores have started.
	public void saveScores() {
		// Local Variables
		// The PrintWriter outputFile allows the information held in the highscores sets to be
		// printed out to the highscores text file.
		PrintWriter outputFile;
		
		// Method Body
		try {
			// The outputFile is declared to print out to the highscores text file
			outputFile = new PrintWriter(new FileWriter("highscores.txt"));
			// All of the scores are saved for the mosquito category
			for(Score score: mosquitoLeaderboard) {
				outputFile.println(score.getName() + " " + score.getScore() + " " + score.getTimeAdded());
			}
			// A blank line is put in place to signify that the next category has started
			outputFile.println();
			// All of the scores for the campfire category
			for(Score score: campfireLeaderboard) {
				outputFile.println(score.getName() + " " + score.getScore() + " " + score.getTimeAdded());
			}
			// A blank line is put in place to signify that the next category has started
			outputFile.println();
			// All of the scores for the distance category
			for(Score score: distanceLeaderboard) {
				outputFile.println(score.getName() + " " + score.getScore() + " " + score.getTimeAdded());
			}
			outputFile.println();
			// The outputFile is closed to save the new updated highscores
			outputFile.close();
		} catch (IOException e) {
			// If there was a critical error when printing out to the highscore file, then the user
			// is told and the location (in the code) of the error is printed.
			System.out.println("Error 408: Outputting Error");
			e.printStackTrace();
		}
	}
	
	// Method Description: The updateScores method will update the current highscores of the game
	// with the highscores that the player has at the time. The method only allows
	// one highscore of the same name, so if the player already has a highscore in the category
	// the highscore is updated if the score currently achieved is higher.
	public void updateScores() {
		// Local Variables
		// The ArrayList of Strings tempColumn is a temporary arraylist that is used to keep 
		// the values that should be displayed in a column (names or scores) that will update
		// the values used by the menu.
		ArrayList<String> tempColumn;
		
		// If the player has a name, so they have entered the game, then the highscores will
		// try to be updated with the current scores, otherwise, the existing older scores are used.
		if(!menu.getPlayerName().equals("")) {
			// Check if current player's mosquito score is better than previous score achieved
			// by the player or if the player does not yet have a highscore.
			if(mosquitoScores.get(menu.getPlayerName()) == null 
					|| mosquitoScores.get(menu.getPlayerName()).getScore() < mosquitoesSwatted) {
				// The score is better or the player does not have a highscore yet
				// Remove the old score so that the scores are updated
				if(mosquitoScores.get(menu.getPlayerName()) != null) {
					mosquitoLeaderboard.remove(mosquitoScores.get(menu.getPlayerName()));
				}
				// The score is added to the leader board which then sorts the scores
				mosquitoLeaderboard.add(new Score(menu.getPlayerName(), mosquitoesSwatted, System.currentTimeMillis()));
				// The player is set to have achieved the current score
				mosquitoScores.put(menu.getPlayerName(), new Score(menu.getPlayerName(), mosquitoesSwatted, System.currentTimeMillis()));
			}
			// Remove the bottom scores if they exceed the capacity of 5
			while(mosquitoScores.size() > 5) {
				mosquitoScores.remove(mosquitoLeaderboard.pollLast().getName());
			}
			
			// Check if current player's campfire score is better than previous score achieved
			// by the player or if the player does not yet have a highscore.
			if(campfireScores.get(menu.getPlayerName()) == null 
					|| campfireScores.get(menu.getPlayerName()).getScore() < campfireTickTime) {
				// Replace the old score with the new score so that the scores are updated
				if(campfireScores.get(menu.getPlayerName()) != null) {
					campfireLeaderboard.remove(campfireScores.get(menu.getPlayerName()));
				}
				campfireLeaderboard.add(new Score(menu.getPlayerName(), campfireTickTime, System.currentTimeMillis()));
				campfireScores.put(menu.getPlayerName(), new Score(menu.getPlayerName(), campfireTickTime, System.currentTimeMillis()));
			}
			// Remove the bottom scores if they exceed the capacity of 5
			while(campfireScores.size() > 5) {
				campfireScores.remove(campfireLeaderboard.pollLast().getName());
			}
			
			// Check if current player's distance score is better than previous score achieved
			// by the player or if the player does not yet have a highscore.
			if(distanceScores.get(menu.getPlayerName()) == null 
					|| distanceScores.get(menu.getPlayerName()).getScore() < distanceTravelled) {
				// Replace the old score with the new score so that the scores are updated
				if(distanceScores.get(menu.getPlayerName()) != null) {
					distanceLeaderboard.remove(distanceScores.get(menu.getPlayerName()));
				}
				distanceLeaderboard.add(new Score(menu.getPlayerName(), distanceTravelled, System.currentTimeMillis()));
				distanceScores.put(menu.getPlayerName(), new Score(menu.getPlayerName(), distanceTravelled, System.currentTimeMillis()));
			}
			// Remove the bottom scores if they exceed the capacity of 5
			while(distanceScores.size() > 5) {
				distanceScores.remove(distanceLeaderboard.pollLast().getName());
			}
		}
		
		// Load in the String formatted campfire scores that are easier to read for the user
		campfireStrScores.clear();
		for(Score score: campfireLeaderboard) {
			campfireStrScores.add(Campfire.convertTicksToTime(score.getScore()));
		}
		
		// Update the menu with the scores that it should now use
		// The mosquito scores are updated with the new names in one column and scores
		// in the other column
		tempColumn = new ArrayList<>();
		for(Score score: mosquitoLeaderboard) {
			tempColumn.add(score.getName());
		}
		menu.setNames(0, tempColumn);
		tempColumn.clear();
		for(Score score: mosquitoLeaderboard) {
			tempColumn.add(Integer.toString(score.getScore()));
		}
		menu.setScores(0, tempColumn);
		// The campfire scores are updated with the new names in one column and scores
		// in the other column
		tempColumn.clear();
		for(Score score: campfireLeaderboard) {
			tempColumn.add(score.getName());
		}
		menu.setNames(1, tempColumn);
		menu.setScores(1, campfireStrScores);
		// The distance scores are updated with the new names in one column and scores
		// in the other column
		tempColumn.clear();
		for(Score score: distanceLeaderboard) {
			tempColumn.add(score.getName());
		}
		menu.setNames(2, tempColumn);
		tempColumn.clear();
		for(Score score: distanceLeaderboard) {
			// Here the distances are also formatted to a more understandable value so
			// each 80 pixels is approximated to be about 1 meter so the player sort
			// of understands how far each player actually went
			tempColumn.add(String.format("%.1f m", (double)(score.getScore()) / 80));
		}
		menu.setScores(2, tempColumn);
	}
	
	// Method Description: The stop method will stop the game from running
	// so the program will terminate in the next tick cycle.
	public void stop() {
		running = false;
	}

	// Method Description: The run method is required to be completed from the Runnable interface.
	// The run method is a threading method that allows the game to be updated every 15 milliseconds
	// for about 60 frames a second. Also once the game is declared to stop running, the frame
	// will be closed.
	// The reason why 15 milliseconds is used even though it appears to result in a cycle faster than
	// 60 frames a second is because the program does take a bit of time to ticks and render all the 
	// frames, so that time makes up to get the 60 frames a second
	public void run() {
		// While the game is running, the game will be updated and ticked every 15 milliseconds
		while (running) {
			// Render the game
			repaint();
			try {
				// Update the game
				tick();
				// Sleep 15 milliseconds
				Thread.sleep(15);
			} catch (Exception e) {
				// If there is an error when sleeping the frame it is
				// caught and the program will print out the error then exit
				e.printStackTrace();
				System.exit(1);
			}
		}
		// If the game stops running then the program will terminate
		System.exit(0);
	}
	
	public static void main(String[] args) {
		// Variables
		// The JFrame frame is the frame that holds the application
		JFrame frame = new JFrame("Camp Haliburton");
		// The Game panel is the JPanel that holds the game and also holds
		// the components of the game itself
		Game panel = new Game();
		
		// Run Code
		// The game is put inside the frame and its dimensions are fitted
		frame.add(panel);
		frame.pack();
		// The game is set to terminate once the JFrame is closed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// The game is put in the center of the screen and shown to the user
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	// Unused Methods
	// These methods are necessary for the class to implement KeyListener and
	// MouseListener, but these methods are unused and will not do anything
	public void mouseClicked(MouseEvent e) {
	}
	
	public void keyTyped(KeyEvent e) {
	}
	
	public void mouseReleased(MouseEvent e) {
	}
	
	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
}