/*
 * Class Name: Scouter
 * Description: The Scouter class represents a NPC in the game allowing the player to get
 * items from them and follow quests that they set out. The Scouter class also has text
 * dialogue that the user can follow which helps to create more of a story in the game.
 * The Scouter class scores information about the position of the Scouter, their
 * image, the current dialogue that they are showing and the quest that the player
 * is currently on.
 */

// Importing the necessary classes so that the Scouter can be rendered onto the screen
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Scouter {
	// The double x value stores the center position of the Scouter and the double
	// y stores the top y position of the Scouter.
	private double x, y;
	// The double relX stores the relative x position of the Scouter which is used
	// when rendering them in.
	private double relX;
	// The BufferedImage image stores the image that is used to display the scouter
	private BufferedImage image;
	// The integer nameIndex stores the representative number for the name of the
	// Scouter. This is used for the dialogue and quickly determining the scouter
	// that should be used for certain quests
	// There are only a certain number of names, each with associated
	// numbers for their dialogue
	// - Bryant (0)
	// - Hubert (1)
	// - Braydon (2)
	private int nameIndex;
	// The ArrayList of DynamicText displayText stores all of the text that is currently 
	// displayed in the Scouter's dialogue
	private ArrayList<DynamicText> displayText;
	// The integer curLineIndex stores the current line that the dialogue is on. This
	// doesn't necessarily mean sentence, but rather groups of sentences that could
	// be said in once speech bubble.
	// Note that speech bubble and dialogue box mean the same thing in these comments
	private int curLineIndex;
	// The String curLine stores the current line to be said by the Scouter
	private String curLine;
	// The integer curAddIndex stores the index of the current line that should be added
	// to the speech bubble
	private int curAddIndex;
	// The double textHeight stores the height of the text used in the dialogue bubble
	private double textHeight;
	// The integer framesSinceAdd stores the number of frames that have passed since the last
	// character was added.
	private int framesSinceAdd;
	// The integer nextLineTicksRem stores the number of ticks more that be waited before 
	// the next line can be moved onto once the previous line as ended.
	private int nextLineTicksRem;
	
	// The boolean skipped stores if the current line's typing out animation was skipped
	// so all of the text should be rendered out
	private boolean skipped;
	
	// The integer questLine stores what quest the player is currently on which is
	// also used to get the dialogue for each quest line from the questDialogue array
	/*
	 * 0 - Bryant introduces the player to the game
	 * 		Objective: Player has to go out and swat mosquitoes
	 * 1 - Hubert will tell the player to drop off a fish carcass
	 * on the dock
	 * 		Objective: Player will drop off the fish carcass
	 * 2 - Bryant tells the player to gather resources
	 * 		Objective: Player adds resources to the fire
	 * 3 - Bryant tells the player to go and find Braydon
	 * 		Objective: Player locates Braydon
	 * 4 - Braydon gives the player a flint and steel and tells 
	 * the player to return to Bryant
	 * 		Objective: Player returns and talks to Bryant
	 * 5 - Bryant tells the player to light the fire
	 * 		Objective: Player lights the fire
	 * 6 - Bryant tells the player to go and explore the rest of the camp
	 * to try and find the Porta-potty at the end of the game
	 * 		Objective: Find the Porta-potty
	 * 7 - Bryant will tell the player they have finished the game
	 */
	private static int questLine;
	
	// The String 2D array normalDialogue stores the normal dialogue for each scouter
	// so normalDialogue[nameIndex] stores an array of dialogue that will be said by the
	// scouter.
	// Two types of dialogue which is regular dialogue and quest dialogue, regular dialogue
	// is said at any time repeatedly, but quest dialogue is said only for certain quest lines
	private static String[][] normalDialogue;
	// The String 2D array questDialogue stores the quest dialogue for a certain quest line
	// so questDialogue[questLine] stores an array of dialogue that is said for that quest line
	private static String[][] questDialogue;
	// The BufferedImage speechBase stores the image used for the bottom of the dialogue box
	private static BufferedImage speechBase;
	// The FontMetrics fm allows the width and height of the text rendered in the speech bubbles to be gotten
	private static FontMetrics fm;
	// The integer textWidth stores the width of the text box where the dialogue is rendered
	private static final int textWidth = 180;
	// The final double bufferWidth is the multiplicative factor of the width that should be kept blank
	// on either side of text.
	private static final double bufferWidth = 0.03;
	// The integer textVerticalShift stores the number of pixels above the scouter's top y value
	// the bottom of the text will be. This means that the text's top left corner is actually rendered 
	// even higher than 20 above the top y value
	private static final int textVerticalShift = 20;
	// The integer speechBaseVerticalShift stores the number of pixels above the scouter's top y value
	// the speech base image should be rendered so that some of the text can still be in the speech base image
	private static final int speechBaseVerticalShift = 30;
	// The integer textVerticalPadding stores the amount of space both above and below the actual text the
	// dialogue box should be rendered so that the rectangle doesn't directly cut off at the text
	private static final int textVerticalPadding = 10;
	// The final integer textSpacing is the number of pixels that rows text should have between each other.
	private static final int textSpacing = 2;
	// The final integer framesTillChange marks how many frames are necessary to pass until
	// another character is added from the toAdd, however the adding actually happens every framesTillChange
	// + 1, so here characters are added every 3 frames.
	private static final int framesTillChange = 2;
	// The integer nextLineDelay stores the number of ticks that is waited between each line, so that
	// the player will have time to process the dialogue put out.
	private static final int nextLineDelay = 240;
	// The Color textBackColor stores the color that is used to render the text box where dialogue is put
	private static final Color textBackColor = new Color(237, 163, 59);
	
	// The BufferedImage questIndicator stores an image used to indicate that a Scouter is offering a quest
	private static BufferedImage questIndicator;
	// The integer indicatorHorizontalShift stores the number of pixels that the indicator has to be
	// shifted so that the image is aligned with the scouter as scouters are not center aligned 
	private static final int indicatorHorizontalShift = 4;
	
	// The Player player stores a reference to the player so that Scouters can add items to the player's
	// inventory
	private static Player player;
	
	// Method Description: The constructor for the Scouter class takes in the position and name of the
	// scouter and will declare all the instance variables for that new scouter instance.
	// Parameters: The double x stores the center x position where the scouter is placed, and the String
	// name stores the name of the Scouter, which is really only internally to make the loading of dialogue
	// and values more clear.
	public Scouter(double x, String name) {
		// The center x position of the scouter is set to the x value passed in
		this.x = x;
		// The name index is gotten relative to the scouter's name
		if(name.equals("Bryant")) {
			this.nameIndex = 0;
		} else if(name.equals("Hubert")) {
			this.nameIndex = 1;
		} else if(name.equals("Braydon")) {
			this.nameIndex = 2;
		}
		// The current dialogue is set to nothing so that the scouter is not saying anything
		this.curLineIndex = -1;
		displayText = new ArrayList<>();
		skipped = false;
		
		// The images are loaded in
		try {
			// Load in the image of the scouter
			if(name.equals("Bryant")) {
				image = ImageIO.read(new File("res/Scouter/ScouterBryant.png"));
			} else if(name.equals("Hubert")) {
				image = ImageIO.read(new File("res/Scouter/ScouterHubert.png"));
			} else if(name.equals("Braydon")) {
				image = ImageIO.read(new File("res/Scouter/Braydon.png"));
			}
			
			// Load in the static images of the base of the speech box and the quest indicator if they
			// haven't been loaded yet
			if(speechBase == null) {
				speechBase = ImageIO.read(new File("res/Scouter/SpeechBase.png"));
				questIndicator = ImageIO.read(new File("res/Scouter/QuestIndicator.png"));
			}
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
		
		// The y position of the scouter is determined based on the height of the image of the
		// scouter. This makes it so that scouters are always on the ground.
		y = Game.HEIGHT - Game.groundHeight - image.getHeight();
		
		// If the dialogue has not been written in yet, then it is done so. This can be done
		// once when the first scouter is loaded in as dialogue is just put into one static array
		// where the different scouters can access their lines. This is done statically
		// so that the Strings do not have to be passed through the constructor which is a bit tedious
		if(normalDialogue == null) {
			// Normal Dialogue
			// The normal dialogue of the scouters is declared
			normalDialogue = new String[3][];
			
			// Bryant's normal dialogue is written in
			normalDialogue[0] = new String[1];
			normalDialogue[0][0] = "Watch out for those mosquitoes!\n\nHere is a \\Racket\\ to help you stay safe out there.";
			
			// Hubert's normal dialogue is written in
			normalDialogue[1] = new String[3];
			normalDialogue[1][0] = "Have you ever eaten raw fish before?";
			normalDialogue[1][1] = "Shannon fished up our dinner tonight!";
			normalDialogue[1][2] = "Also, have you seen \n\\Braydon\\, he needs to help me prepare the fish.";
			
			// Braydon's normal dialogue is written in
			normalDialogue[2] = new String[2];
			normalDialogue[2][0] = "Ah man, you lost a \\Flint And Steel\\ again?";
			normalDialogue[2][1] = "Alright here is another one.";
			
			// Quest Dialogue
			// The quest dialogue is declared
			questDialogue = new String[8][];
			// The questLine is set to the first quest
			questLine = 0;
			// Now all of the quest dialogue is written in, where each quest line has a group
			// of text that will be displayed explaining the objective
			// Bryant introduces the player to the camp and gives them a mosquito quest
			questDialogue[0] = new String[5];
			questDialogue[0][0] = "Welcome to Camp Haliburton! I'm \\Scouter Bryant\\ and I will be helping you get around camp.";
			questDialogue[0][1] = "Firstly, mosquitoes are quite abundant around here and can take a toll on your health.";
			questDialogue[0][2] = "Make sure to use a \n\\Racket\\ to swat away the mosquitoes by pressing \\K\\.";
			questDialogue[0][3] = "Why don't you try it out by swatting \\10\\\nmosquitoes.";
			questDialogue[0][4] = "Here is your racket, if you are in need of another, either me or \\Scouter Hubert\\ to the right can give you one.";
			
			// Hubert tells the player to drop off the fish carcass on the dock
			questDialogue[1] = new String[4];
			questDialogue[1][0] = "Hello, I'm \\Scouter Hubert\\. Now that you can protect yourself against mosquitoes, could you help me with something?";
			questDialogue[1][1] = "We are making fish for dinner and we have a \\Fish Carcass\\ to get rid off.";
			questDialogue[1][2] = "Could you help me drop it off at the dock?";
			questDialogue[1][3] = "You can press \\Q\\ to drop an item from your inventory.";
			
			// Bryant tells the player to gather resources
			questDialogue[2] = new String[4];
			questDialogue[2][0] = "An important scouting skill is to be able to make fire.";
			questDialogue[2][1] = "The first step is to gather \\Tinder\\ and \\Kindling\\, which are necessary to make a fire start. \\Tinder\\ is lit "
					+ "first and then \\Kindling\\ will catch on and burn longer.";
			questDialogue[2][2] = "\\Tinder\\ can be \\Leaves,\\ \\Birch Bark,\\ or \\Cotton Balls,\\ while \\Kindling\\ is normally smaller \\Sticks\\.";
			questDialogue[2][3] = "Press \\L\\ to pick up these items on the ground, then press \\L\\ when selecting the item to add it to the fire, and"
					+ " report back to me when you have finished adding both \\Tinder\\ and \\Kindling\\ to the campfire.";
			
			// Bryant tells the player to go and find Braydon
			questDialogue[3] = new String[4];
			questDialogue[3][0] = "The next step is to light the fire itself.";
			questDialogue[3][1] = "We commonly do this with matches but I can't seem to find where they are.";
			questDialogue[3][2] = "Instead, you can use a \\Flint And Steel.\\ To get this, go and find \\Braydon.\\ Once you get it, return to me and "
					+ "I will tell you how to light the fire.";
			questDialogue[3][3] = "I think that he is over by the cotton field.";
			
			// Braydon gives the player the flint and steel and tells them to return to Bryant
			questDialogue[4] = new String[3];
			questDialogue[4][0] = "Hullo, I am just trying to light my fire here. I just can't seem to find anything other than cotton...";
			questDialogue[4][1] = "Oh, \\Scouter Bryant\\ sent you here to get a \\Flint And Steel?\\";
			questDialogue[4][2] = "Well, here you go! If you ever lose it, just come back here and ask me.";
			
			// Bryant tells the player how to light the fire
			questDialogue[5] = new String[3];
			questDialogue[5][0] = "Now that you have the \\Flint And Steel,\\ you can light the fire.";
			questDialogue[5][1] = "Press \\L\\ while selecting the \\Flint And Steel\\ nearby a campfire to light it.";
			questDialogue[5][2] = "Go and try it out!";
			
			// Bryant tells the player to go and explore the rest of the map
			questDialogue[6] = new String[3];
			questDialogue[6][0] = "That's all the activities that I have for now, but there is one more thing.";
			questDialogue[6][1] = "I heard that there is a \\Porta-potty\\ at our camp, but I could never find it.";
			questDialogue[6][2] = "If you want, go out and explore more of our camp and try and find the \\Porta-potty.\\";
			
			// Bryant tells the player that they have finished the game
			questDialogue[7] = new String[2];
			questDialogue[7][0] = "That's everything. Thanks for playing through Harry's experiences at \\Camp Haliburton!\\";
			questDialogue[7][1] = "You can try to set out to reach the highscore leaderboard. Hope you enjoyed the game!";
		}
	}
	
	// Method Description: The render method will render the Scouter onto the menu and will
	// render in the dialogue of the Scouter. Additionally, the dialogue is progressed in this method
	// as well rather than the tick method because adding text does require the use of the Graphics g
	// to get the bounds of the Strings.
	// Parameters: The Graphics g stores the graphics of the game where the Scouter should be rendered
	public void render(Graphics g) {
		// The image of the Scouter is drawn in
		g.drawImage(image, (int)(relX - image.getWidth() / 2), (int)y, null);
		
		if(skipped) {
			// If the user marked to skip through the filling in of the text animation
			// then the text is fully filled into the dialogue box and then the dialogue
			// is set to not be skipped on the next animation.
			fillAllText(g);
			skipped = false;
		}
		// The Scouter is checked to see if they are currently speaking
		if(curLineIndex >= 0) {
			// There is a line that the Scouter is saying so they are speaking
			// Try to see if a character can be added, first it is checked if
			// enough frames have passed before another character is added
			if(framesSinceAdd >= framesTillChange) {
				// If the enough number of frames has passed, then it is checked if the character
				// to add already exceeds the length of the line
				if(curAddIndex >= curLine.length()) {
					// If the length is exceeded, then a certain number of ticks have to pass
					// before the next line is shown
					if(nextLineTicksRem <= 0) {
						// If those ticks have passed then the next line can be gotten
						nextLine();
					} else {
						// Otherwise, the ticks left before the next line are decremented
						nextLineTicksRem--;
					}
				} else {
					// Here, there are more characters to be added so another character is added
					addOne(g);
				}
			} else {
				// If not enough frames have passed then the number of frames
				// is increased to get closer to adding another character
				framesSinceAdd++;
			}
			
			// Render in the dialogue with the speech box
			g.setColor(textBackColor);
			g.fillRect((int)(relX - textWidth / 2), (int)(y - speechBaseVerticalShift - textHeight - textVerticalPadding), 
					textWidth, (int)(textHeight + 2 * textVerticalPadding));
			g.drawImage(speechBase, (int)(relX - speechBase.getWidth() / 2), (int)(y - speechBaseVerticalShift), null);
			for (int i = 0; i < displayText.size(); i++) {
				displayText.get(i).render(g);
			}
		} else {
			// If the Scouter is not currently speaking then it is checked if they 
			// should be indicated for the current quest and it is rendered if they are 
			// the scouter for the current quest
			if((questLine == 0 || questLine == 2 || questLine == 3 || questLine == 5 || questLine == 6 || questLine == 7) && nameIndex == 0) {
				g.drawImage(questIndicator, (int)(relX - questIndicator.getWidth() / 2 + indicatorHorizontalShift), 
						(int)(y - textVerticalShift - questIndicator.getHeight()), null);
			} else if(questLine == 1 && nameIndex == 1) {
				g.drawImage(questIndicator, (int)(relX - questIndicator.getWidth() / 2 - indicatorHorizontalShift), 
						(int)(y - textVerticalShift - questIndicator.getHeight()), null);
			} else if(questLine == 4 && nameIndex == 2) {
				g.drawImage(questIndicator, (int)(relX - questIndicator.getWidth() / 2 - indicatorHorizontalShift), 
						(int)(y - textVerticalShift - questIndicator.getHeight()), null);
			}
		}
	}
	
	// Method Description: The interact method will update the scouter which is used when
	// the player presses the interact key with the scouter. This will prompt their dialogue
	// to play or advance.
	public void interact() {
		// The scouter is checked if they have any current dialogue
		if(curLineIndex == -1) {
			// If the scouter is not speaking then they are set to speak
			curLineIndex = 0;
			curAddIndex = 0;
			// The nextLineTicksRem is set so that a certain number of ticks has to be waited
			// before the next line is gotten after the current line is spoken
			nextLineTicksRem = nextLineDelay;
			
			// If the scouter should be saying quest dialogue then the quest dialogue takes precedence
			// over the normal dialogue and the quest dialogue is set to be said.
			if((questLine == 0 || questLine == 2 || questLine == 3 || questLine == 5 || questLine == 6 || questLine == 7) && nameIndex == 0) {
				curLine = questDialogue[questLine][curLineIndex];
			} else if(questLine == 1 && nameIndex == 1) {
				curLine = questDialogue[questLine][curLineIndex];
			} else if(questLine == 4 && nameIndex == 2) {
				curLine = questDialogue[questLine][curLineIndex];
			} else {
				if((questLine == 2 || questLine == 3 || questLine == 5 || questLine == 6 || questLine == 7) && nameIndex == 1) {
					// If Bryant cannot give the player a racket, Hubert will instead
					curLine = normalDialogue[0][curLineIndex];
				} else {
					// Otherwise, the scouter will simply say their regular dialogue
					curLine = normalDialogue[nameIndex][curLineIndex];
				}
			}
		} else {
			// Advance the current dialogue
			if(curAddIndex >= curLine.length()) {
				// If the player skips the dialogue once the filling in animation is already
				// done, then they wish to skip to the next line
				nextLine();
			} else {
				// Otherwise, the current line is just rendered out so they skip the filling
				// in animation but the next line is not gotten yet. To get to that they
				// can just press L again.
				curAddIndex = curLine.length();
				skipped = true;
			}
		}
	}
	
	// Method Description: The nextLine method will get the next dialogue line for the
	// scouter. If the dialogue has ended then certain actions will be performed depending
	// on the quest / scouter.
	private void nextLine() {
		// The next line is reset to be filled in
		nextLineTicksRem = nextLineDelay;
		curLineIndex++;
		displayText.clear();
		// The height of the text in the speech bubble is reset as the speech is cleared
		// for each line.
		textHeight = 0;
		
		// All of the cases in this if statement are similar, at the end of every dialogue, the 
		// curLineIndex is set to -1 so that the scouter will stop speaking. Otherwise, if the
		// curLineIndex is less than the dialogue's length then the next line is gotten and the 
		// current adding index is set to 0 so that the entire line will be added in.
		if(questLine == 0 && nameIndex == 0) {
			if(curLineIndex >= questDialogue[questLine].length) {
				// The line of dialogue has ended so the actions
				// can be performed here
				curLineIndex = -1;
				// Bryant gives the player a racket
				if(!player.addItem("Racket")) {
					Game.addItem(new EnvironmentItem(x, y + image.getHeight() * 1 / 4, "Racket", relX - x));
				}
			} else {
				// Otherwise, the line is progressed
				curLine = questDialogue[questLine][curLineIndex];
				curAddIndex = 0;
			}
		} else if(questLine == 1 && nameIndex == 1) {
			if(curLineIndex >= questDialogue[questLine].length) {
				curLineIndex = -1;
				// Hubert gives the player the fish carcass to drop off
				if(!player.addItem("Fish Carcass")) {
					Game.addItem(new EnvironmentItem(x, y + image.getHeight() * 1 / 4, "Fish Carcass", relX - x));
				}
			} else {
				curLine = questDialogue[questLine][curLineIndex];
				curAddIndex = 0;
			}
		} else if(questLine == 3 && nameIndex == 0) {
			if(curLineIndex >= questDialogue[questLine].length) {
				// The player immediately progresses to the quest to find Braydon
				curLineIndex = -1;
				nextQuestLine();
			} else {
				curLine = questDialogue[questLine][curLineIndex];
				curAddIndex = 0;
			}
		} else if(questLine == 4 && nameIndex == 2) {
			if(curLineIndex >= questDialogue[questLine].length) {
				// Braydon gives the player a flint and steel and immediately progresses to get back to Scouter Bryant
				curLineIndex = -1;
				if(!player.addItem("Flint And Steel")) {
					Game.addItem(new EnvironmentItem(x, y + image.getHeight() * 1 / 4, "Fish Carcass", relX - x));
				}
				nextQuestLine();
			} else {
				curLine = questDialogue[questLine][curLineIndex];
				curAddIndex = 0;
			}
		} else if((questLine == 2 || questLine == 5 || questLine == 6 || questLine == 7) && nameIndex == 0) {
			// Regular quest dialogue, without items given to the player
			if(curLineIndex >= questDialogue[questLine].length) {
				curLineIndex = -1;
			} else {
				curLine = questDialogue[questLine][curLineIndex];
				curAddIndex = 0;
			}
		} else {
			// It is checked if Scouter Bryant is busy on another quest so Hubert has to give the racket instead
			if((questLine == 2 || questLine == 3 || questLine == 5 || questLine == 6 || questLine == 7) && nameIndex == 1) {
				// If Bryant cannot give the player a racket, Hubert will instead
				if(curLineIndex >= normalDialogue[0].length) {
					curLineIndex = -1;
					// Hubert gives the player a racket
					if(!player.addItem("Racket")) {
						Game.addItem(new EnvironmentItem(x, y + image.getHeight() * 1 / 4, "Racket", relX - x));
					}
				} else {
					curLine = normalDialogue[0][curLineIndex];
					curAddIndex = 0;
				}
			} else {
				// Otherwise, the normal dialogue is used for the scouters
				if(curLineIndex >= normalDialogue[nameIndex].length) {
					curLineIndex = -1;
					if(nameIndex == 0) {
						// Bryant gives the player a racket at the end of his normal dialogue
						if(!player.addItem("Racket")) {
							Game.addItem(new EnvironmentItem(x, y + image.getHeight() * 1 / 4, "Racket", relX - x));
						}
					} else if(nameIndex == 2) {
						// Braydon gives the player a racket at the end of his normal dialogue
						if(!player.addItem("Flint And Steel")) {
							Game.addItem(new EnvironmentItem(x, y + image.getHeight() * 1 / 4, "Flint And Steel", relX - x));
						}
					}
				} else {
					curLine = normalDialogue[nameIndex][curLineIndex];
					curAddIndex = 0;
				}
			}
		}
		
	}
	
	// Method Description: The method addOne will add on the character at the current add index
	// of the curLine String so that the text can be animated. This method will reduce the amount
	// of displays that have to be computed by only updating the last display to append the text
	// Parameters: The Graphics g is the graphics of the game where the text is being rendered
	// which is used to get the size of the text so that it can be formatted and the height of the speech
	// box calculated.
	private void addOne(Graphics g) {
		// Local Variables
		// The String nextText stores the newText that should be appended onto the displayText. Note
		// that this will include the previously entered DynamicText as that is necessary so that the
		// current character will know where to be added.
		String newText = "";
		// The ArrayList of DynamicText newDisplays stores the new DynamicText that has to be appended
		// to the display text.
		ArrayList<DynamicText> newDisplays;
		// The Rectangle2D rect is used to get the space that the text will take up
		Rectangle2D rect;
		// The double firstY stores the location of the first text so that the height
		// of the speech box can be calculated with the first text and last text.
		double firstY;

		// Method Body
		// The newText is the previous text as that is the DynamicText that has to be
		// checked with in terms of position and color of the line, plus the new character that was added.
		if(displayText.size() == 0) {
			// If there was no previous text than the newText is simply the character added
			newText += curLine.charAt(curAddIndex);
		} else {
			newText = displayText.get(displayText.size() - 1).getText() + curLine.charAt(curAddIndex);
		}

		// The new displays are computed, things to add on
		if(displayText.size() == 0) {
			// Initially the text will be set to black, not purple. Here the new displays is simply calculated as
			// the character starts the speech text.
			newDisplays = DynamicText.makeTextGroup(g, x - textWidth / 2, 0, y, newText, textWidth, bufferWidth, textSpacing, false, false);
		} else {
			// Here the text is calculated as being from the previous DynamicText's location, and is set to purple if the 
			// previous text was purple and the text is added onto the same line so it is marked that way so that the program 
			// recognizes that it can enter a to make a new line with this DynamicText.
			newDisplays = DynamicText.makeTextGroup(g, x - textWidth / 2, displayText.get(displayText.size() - 1).getX() - bufferWidth * textWidth - x + textWidth / 2, 
					displayText.get(displayText.size() - 1).getY(), newText, textWidth, bufferWidth, textSpacing, displayText.get(displayText.size() - 1).getIsPurple(), true);
		}

		// Since the last displayText was used to compute with the appended character
		// it should already be included in the newDisplays, so remove it
		if(displayText.size() > 0) {
			displayText.remove(displayText.size() - 1);
		}
		// Add on the new dynamic text to display
		for(int i = 0; i < newDisplays.size(); i++) {
			displayText.add(newDisplays.get(i));
		}

		if (displayText.size() > 0) {
			// The height of the speech box is calculated from the last text and first text in the speech box.
			// The height is equal to the last text's y + height - the first text's y. Here the height is relative
			// so the y location of the speech box does not have to be considered.
			firstY = displayText.get(0).getY();
			rect = fm.getStringBounds(displayText.get(displayText.size() - 1).getText(), g);
			textHeight = displayText.get(displayText.size() - 1).getY() + rect.getHeight() - firstY;
		} else {
			// Otherwise, if there is no text to display the speech box has no height
			textHeight = 0;
		}

		// The text positions still have to be recalculated again since text should always be on the bottom
		// of the speech box, and when text is added, other text may have to be shifted up.
		calculateAllTextPos(g);
		// The current add index is increased, and the frames since the last add is set to zero
		curAddIndex++;
		framesSinceAdd = 0;
	}

	// Method Description: The calculateAllTextPos method will compute the text position of every
	// DynamicText in the speech box. This method essentially formats the y values of all the text
	// so that they latest text is put at the bottom of the dialogue box. The method will also
	// get the relative x values for all of the display text as the text is generated relative
	// to the true x initially.
	// Parameters: The Graphics g is the graphics of the application which is used to get the size of 
	// the text so that it can be formatted and the height of the speech box calculated.
	private void calculateAllTextPos(Graphics g) {
		// Local Variables
		// The double curBottom stores the current bottom position of the
		// text so that text can be put as low as possible in the dialogue box.
		double curBottom = y - textVerticalShift;
		// The Rectangle2D rect is used to get the space that the text will take up
		Rectangle2D rect;
		// The double oldY stores the old value of y that was calculated for the
		// dynamic text, this is used when considering if the next line was
		// on the same level as the previous, so the current bottom would not be
		// increased yet.
		double oldY;

		// The positions of the text are put as low as possible in the dialogue box
		// so the bottom text takes the lowest possible position.
		if(displayText.size() > 0) {
			g.setFont(DynamicText.getTextFont());
			// The displayText is looped backwards, and the latest text is put at the
			// bottom of the speech box
			for(int i = displayText.size() - 1; i >= 0; i--) {
				rect = fm.getStringBounds(displayText.get(i).getText(), g);
				oldY = displayText.get(i).getY();
				displayText.get(i).setY(curBottom - rect.getHeight());
				if(i > 0) {
					if(displayText.get(i - 1).getY() != oldY) {
						curBottom -= rect.getHeight() + textSpacing;
					}
				}
			}
		}
		
		// Additionally, the display text's relative x position is calculated
		findTextRelX();
	}
	
	// Method Description: The fillAllText method will calculate all of the DynamicText necessary
	// to render the current line. The method will also update the height of the text and find
	// the positions where the dynamic text should be rendered. This method is used whenever
	// the player skips the dialogue, and all of the dialogue has to be added in at once.
	// Parameters: The Graphics g stores the graphics of the game which is used to get the size of the
	// text so that it can be formatted and the height of the speech box calculated.
	private void fillAllText(Graphics g) {
		// Local Variables
		// The Rectangle2D rect is used to get the space that the text will take up
		Rectangle2D rect;

		// Method Body
		// The displayText is derived from all of the text in the dialogue box
		displayText = DynamicText.makeTextGroup(g, x - textWidth / 2, 0, y, curLine, textWidth, bufferWidth, textSpacing, false, false);

		if (displayText.size() > 0) {
			// The height of the speech box is calculated from the last text in the speech box.
			fm = g.getFontMetrics();
			rect = fm.getStringBounds(displayText.get(displayText.size() - 1).getText(), g);
			textHeight = displayText.get(displayText.size() - 1).getY() + rect.getHeight() - y;
		} else {
			// Otherwise, if there is no text to display, the speech box has no height
			textHeight = 0;
		}
		
		// The x and y values of the text is recomputed so that they can be shifted to the
		// bottom of the speech box and be in the correct relative position on the screen.
		calculateAllTextPos(g);
	}
	
	// Method Description: The findTextRelX method will find the relative x positions
	// of the display text based on the shift of the Scouter itself so that the text can be
	// rendered in the correct location.
	private void findTextRelX() {
		// All of the displayText is looped over and the relative x position is calculated
		for(int i = 0; i < displayText.size(); i++) {
			// The relative location of the displayText is gotten from the shift of the Scouter
			displayText.get(i).setRelX(displayText.get(i).getX() + relX - x);
		}
	}
	
	// Method Description: The initMetrics method will initialize the FontMetrics used
	// to get the size of the text in the dialogue box. Initializing the FontMetrics
	// can take a lot of time so it is done initially in the constructor of the game using
	// this method.
	// Parameters: The Graphics g stores the graphics of the game where the text is rendered
	public static void initMetrics(Graphics g) {
		// The Font is set to the font used for the DynamicText which is the dialogue font
		g.setFont(DynamicText.getTextFont());
		// The font metrics is declared
		fm = g.getFontMetrics();
	}
	
	// Method Description: The method nextQuestLine will progress the quest line and
	// reset all of the dialogue so that old quest dialogue will not jump to the
	// new quest dialogue.
	public static void nextQuestLine() {
		// The questLine is increased so the game will move onto the next quest line
		questLine++;
		// The scouters will all reset their speech and stop talking to avoid jumps
		// in the dialogue
		Game.resetScouterSpeech();
	}
	
	// Method Description: The method clearDialogue will stop the scouter's current
	// dialogue and remove the text in the speech box. This method is called in the
	// Game's resetScouterSpeech method to stop the scouters from continuing their
	// dialogue.
	public void clearDialogue() {
		// The current line index is set to -1 so that the scouter will have no line
		// to say.
		curLineIndex = -1;
		// The next line is prompted to be said after the next line delay. This isn't
		// really necessary here but is just put to make sure that the next line has
		// a delay.
		nextLineTicksRem = nextLineDelay;
		// The displayText is cleared so that the speech box becomes empty
		displayText.clear();
		// The height of the speech box is also set to zero as there is no more dialogue
		// as it has been cleared
		textHeight = 0;
	}
	
	// Getter Methods
	public double getX() {
		return x;
	}

	public double getRelX() {
		return relX;
	}

	public double getWidth() {
		return image.getWidth();
	}
	
	public static int getQuestLine() {
		return questLine;
	}

	// Setter Methods
	public void setRelX(double relX) {
		this.relX = relX;
		// The relative x position of the display text also has to be
		// updated
		findTextRelX();
	}
	
	public static void setPlayer(Player player) {
		Scouter.player = player;
	}
}
