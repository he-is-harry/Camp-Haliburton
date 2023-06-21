/*
 * Class Name: Menu
 * Description: The Menu class is the menu that is used in the game, allowing the
 * menu of the game to be handled in a separate class and avoid clutter in the Game
 * driver class. The class holds important methods for the rendering and updating
 * of the menu, holding many of the things necessary for the menu to work.
 */

// Importing the necessary classes so that the menu can be rendered and updated
// based on the user's mouse input
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Menu {
	// Static Variables
	// These variables are all static as there should only ever be one menu for
	// each game
	/*
	 * The String menuState stores the current page (state) that the menu is in
	 * Although it is more efficient to use integers to represent the menu state it
	 * is much more clear what blocks of code are used for if the menu state is set
	 * to be a String.
	 * Here is a list of all the possible menu states
	 * - Main Menu (0)
	 * - Enter Name (1)
	 * - High Scores (2)
	 * - Instructions 1 (3)
	 * - Instructions 2 (4)
	 * - Instructions 3 (5)
	 * - About (6)
	 * - Pause (7)
	 * 
	 * Each menu state also has an associated number in brackets beside it which is
	 * used for the buttons so that all the regular buttons can be stored in a 2D array.
	 */
	private static String menuState;
	// The 2D Button array buttons stores the buttons that are used for a certain menu state.
	// At buttons[menu state], it stores an array of all the buttons used in the menu state.
	private static Button[][] buttons;
	
	// The BufferedImage mainBackground stores the image used to render the background of the
	// title page
	private static BufferedImage mainBackground;
	// The BufferedImage array mainTitle stores all of the images used in the animation of the
	// title. The title increases and decreases in size gradually, so this array stores images
	// of increasing size, so that it can be looped up when getting larger and looped down
	// when getting smaller.
	private static BufferedImage[] mainTitle;
	// The integer curTitle stores the index of the image used for the title in the mainTitle array
	private static int curTitle;
	// The integer titleTickRem is the number of ticks that are left for the current title image
	private static int titleTickRem;
	// The boolean increasingTitle stores if the title is increasing in size or not
	private static boolean increasingTitle;
	// The integer titleTickDelay stores the number of ticks that a specific title image
	// will be rendered before the next one is gotten
	private static final int titleTickDelay = 2;
	
	// The BufferedImage dirtBackground stores the background that is used in the enter name page
	// and the highscores page.
	private static BufferedImage dirtBackground;
	// The BufferedImage nameField stores the image that is used when rendering in the background of 
	// the field where the name of the player is entered when it is not selected. The BufferedImage
	// highlightedNameField stores the image that is used when rendering in the background of the field
	// when it is selected.
	private static BufferedImage nameField, highlightedNameField;
	// The String playerName stores the name of the player entered which is often used in managing
	// the highscores.
	private static String playerName;
	// The boolean fieldHighlighted stores if the field where the player enters their name is selected
	private static boolean fieldHighlighted;
	// The String errorMessage stores the message that is rendered below the enter name field if the player
	// either tried to play the game without entering a name or if they enter a name too long.
	private static String errorMessage;
	
	// The HeldButton array categoryButtons are the buttons used to select the category of the highscore
	// that should be rendered.
	private static HeldButton[] categoryButtons;
	// The String category stores the category of the highscore that should be displayed
	private static String category;
	// The ArrayList of ArrayList of Strings names stores the names that are used in the highscores
	// in each category. The names.get(index) stores the names used in the highscores of the category
	// at that index. The ArrayList of ArrayList of Strings scores stores the scores that are used in the
	// highscores in each category. The initial depth arraylist is always size 3 for each category.
	// 0 - Mosquitoes
	// 1 - Campfire
	// 2 - Distance
	private static ArrayList<ArrayList<String>> names, scores;
	
	// The BufferedImages instructionsBackground1, instructionsBackground2, and instructionsBackground3
	// store the images that are used when rendering the first, second, and third instructions pages,
	// respectively.
	private static BufferedImage instructionsBackground1;
	private static BufferedImage instructionsBackground2;
	private static BufferedImage instructionsBackground3;
	
	// The BufferedImage aboutBackground is the background that is used for the about menu page
	private static BufferedImage aboutBackground;
	
	// The BufferedImage pauseBackground is the background used for the pause pop up box
	private static BufferedImage pauseBackground;
	
	// The Fonts smallRegularFont, mediumRegularFont and largeRegularFont store a small, medium
	// and large version of the font used in the menu. The regular font uses the RegularFont.ttf file.
	// The Fonts boldFont and largeBoldFont store a regular bold font used when rendering in smaller headings
	// and a large bold font when rendering larger titles of pages. The bold font uses the MenuFont.ttf file.
	private static Font smallRegularFont, mediumRegularFont, largeRegularFont, boldFont, largeBoldFont;
	// The integers smallFontSize, mediumFontSize, largeFontSize, and boldFontSize store the sizes of the fonts
	// used for the specific sizes of fonts as specified in the name. Note that the boldFontSize is used for the 
	// bold font, but the largeFontSize is used for the largeBoldFont and largeRegularFont.
	private static final int smallFontSize = 24, mediumFontSize = 28, largeFontSize = 52, boldFontSize = 40;
	// The Colors white, red, black, gray, and lightGray store colors that are used when rendering in text
	// in the menu.
	private static final Color white = new Color(255, 255, 255), red = new Color(255, 120, 120), black = new Color(0, 0, 0);
	private static final Color gray = new Color(47, 47, 47), lightGray = new Color(56, 56, 56);
	
	// The Game game stores the Game instance which represents the entire application. The Game variable is 
	// necessary so that the position of the mouse can be determined based on the position of the frame of the game
	// as used in the tick method. If this variable was not here, the menu's tick would have to be intertwined with the
	// Game's which would be a bit confusing.
	private static Game game;
	
	// Method Description: The constructor for the Menu class will set up all of the variables
	// used to render in the menu. Highscores loaded in from the game class are added after
	// this constructor is called so that the names and scores can all be declared first.
	// Parameters: The Game game stores a reference to the driver class used to run the application
	// which represents the game, which can be used to get information about the JPanel used to display the game.
	public Menu(Game game) {
		// The game variable is set to the game reference passed in
		Menu.game = game;
		// The menu is initially set to be on the main menu
		menuState = "Main Menu";
		
		// The title that increases and decreases in size is set to be at its smallest size, which
		// is where the title should begin
		curTitle = 0;
		// The amount time left for the current title image is set to the delay between each title image
		titleTickRem = titleTickDelay;
		// The title is set to be increasing in size
		increasingTitle = true;
		
		// The field in the enter name screen is initially set to be not selected
		fieldHighlighted = false;
		// The player's name is initially set to be blank
		playerName = "";
		// There is no error message initially
		errorMessage = "";
		// The default selected category for the highscores page is the mosquitoes category
		// as that is the category that appears first.
		category = "Mosquitoes";
		// The names and scores in the highscores are initially set to be blank array lists.
		names = new ArrayList<ArrayList<String>>(3);
		for(int i = 0; i < 3; i++) {
			names.add(new ArrayList<>());
		}
		scores = new ArrayList<ArrayList<String>>(3);
		for(int i = 0; i < 3; i++) {
			scores.add(new ArrayList<>());
		}
		
		try {
			// The images used to render the menu are loaded in
			// There are 10 images used to render in the animation of the title's increasing
			// and decreasing size, so they are all loaded in.
			mainTitle = new BufferedImage[10];
			for(int i = 1; i <= 10; i++) {
				mainTitle[i - 1] = ImageIO.read(new File("res/Menu/Title/MainMenuTitle" + i + ".png"));
			}
			// All of the backgrounds are loaded in
			mainBackground = ImageIO.read(new File("res/Menu/MainMenuBackground.png"));
			
			dirtBackground = ImageIO.read(new File("res/Menu/DirtBackground.png"));
			// The fields used to render the text field of the enter name screen is loaded in
			nameField = ImageIO.read(new File("res/Menu/EnterNameField.png"));
			highlightedNameField = ImageIO.read(new File("res/Menu/HighlightedEnterNameField.png"));
			
			instructionsBackground1 = ImageIO.read(new File("res/Menu/InstructionsBackground1.png"));
			instructionsBackground2 = ImageIO.read(new File("res/Menu/InstructionsBackground2.png"));
			instructionsBackground3 = ImageIO.read(new File("res/Menu/InstructionsBackground3.png"));
			
			aboutBackground = ImageIO.read(new File("res/Menu/AboutBackground.png"));
			
			pauseBackground = ImageIO.read(new File("res/Menu/PauseBackground.png"));
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
		
		// Load in the font
		try {
			// All of the fonts used to render text in the menu are loaded in using the regular 
			// plain style of the font as the fonts loaded in only have one style.
			boldFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Menu/MenuFont.ttf")).deriveFont(Font.PLAIN, boldFontSize);
			largeBoldFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/Menu/MenuFont.ttf")).deriveFont(Font.PLAIN, largeFontSize);
			largeRegularFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/RegularFont.ttf")).deriveFont(Font.PLAIN, largeFontSize);
			mediumRegularFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/RegularFont.ttf")).deriveFont(Font.PLAIN, mediumFontSize);
			smallRegularFont = Font.createFont(Font.TRUETYPE_FONT, new File("res/RegularFont.ttf")).deriveFont(Font.PLAIN, smallFontSize);
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
		
		// Declare in the buttons
		// There are 8 different pages which need buttons to be rendered
		buttons = new Button[8][];
		
		// Main title buttons are declared
		buttons[0] = new Button[5];
		buttons[0][0] = new Button(845, 137, "PLAY");
		buttons[0][1] = new Button(845, 237, "HIGHSCORES");
		buttons[0][2] = new Button(845, 337, "INSTRUCTIONS");
		buttons[0][3] = new Button(845, 437, "ABOUT");
		buttons[0][4] = new Button(845, 537, "QUIT");
		
		// The enter name buttons are declared
		buttons[1] = new Button[2];
		buttons[1][0] = new Button(240, 605, "BACK");
		buttons[1][1] = new Button(840, 605, "PLAY");
		
		// The highscore page buttons are declared
		buttons[2] = new Button[1];
		buttons[2][0] = new Button(240, 605, "BACK");
		
		// The instruction pages 1, 2 and 3 all have their buttons declared
		buttons[3] = new Button[2];
		buttons[3][0] = new Button(240, 605, "BACK");
		buttons[3][1] = new Button(840, 605, "NEXT");
		
		buttons[4] = new Button[2];
		buttons[4][0] = new Button(240, 605, "BACK");
		buttons[4][1] = new Button(840, 605, "NEXT");
		
		buttons[5] = new Button[1];
		buttons[5][0] = new Button(240, 605, "BACK");
		
		// The about page buttons are declared
		buttons[6] = new Button[1];
		buttons[6][0] = new Button(240, 605, "BACK");
		
		// The pause menu's buttons are declared
		buttons[7] = new Button[3];
		buttons[7][0] = new Button(540, 260, "RESUME");
		buttons[7][1] = new Button(540, 350, "HIGHSCORES");
		buttons[7][2] = new Button(540, 440, "MAIN MENU");
		
		// Declare in the high score category buttons
		categoryButtons = new HeldButton[3];
		categoryButtons[0] = new HeldButton(198, 135, "Mosquitoes");
		// Initially the mosquitoes category is selected. There always has to be
		// a category that is selected or else there is no highscores to render.
		categoryButtons[0].setSelected(true);
		categoryButtons[1] = new HeldButton(540, 135, "Campfire");
		categoryButtons[2] = new HeldButton(882, 135, "Distance Travelled");
	}
	
	// Method Description: The tick method will update the menu based on the current mouse 
	// position of the user. The method is mainly only used to highlight buttons that the
	// user hovers over, however it also animates the title in the main page.
	public void tick() {
		// Local Variables
		// The Point mouseLocation represents the location of the mouse relative to the 
		// computer's window and the Point windowLocation represents the location of the 
		// window relative to the computer's window.
		Point mouseLocation, windowLocation;
		// The doubles mouseX and mouseY are the x and y of the mouse
		// They are initially both set to -1 so that if the game is not showing, the mouse 
		// will not be indicated has hovering over any values as nothing should be rendered off the
		// screen.
		double mouseX = -1, mouseY = -1;
		
		// Method Body
		// First, check if the component is displayed yet, otherwise it is impossible
		// to tell the component's location on screen.
		if(game.isShowing()) {
			// The location of the mouse is calculated
			mouseLocation = MouseInfo.getPointerInfo().getLocation();
			windowLocation = game.getLocationOnScreen();
			mouseX = mouseLocation.getX() - windowLocation.getX();
			mouseY = mouseLocation.getY() - windowLocation.getY();
		}
		// The menu state is checked so that the corresponding buttons can be checked if they were
		// highlighted over.
		if(menuState.equals("Main Menu")) {
			// Main Menu
			// In the main menu the title is animated by changing the image used to render in the title
			if(titleTickRem <= 0) {
				// If there is no time left for the title image, then the title image is changed
				if(increasingTitle) {
					// If the animation is currently increasing, then the next image at the next index
					curTitle++;
					// If the current title's index has reached the end of the number of the titles
					// then it is set to not be increasing anymore, and so the titles will start going
					// down and decreasing in size.
					if(curTitle >= mainTitle.length - 1) {
						increasingTitle = false;
					}
				} else {
					// If the animation is currently decreasing, then the next image is at the previous index
					curTitle--;
					// If the current title's index has reached the beginning, then the titles will start
					// going up again and increasing in size.
					if(curTitle <= 0) {
						increasingTitle = true;
					}
				}
				// The total ticks remaining for the title image is set to the delay for each image
				titleTickRem = titleTickDelay;
			} else {
				// If there is more ticks left for the title image to be rendered, it is decremented
				titleTickRem--;
			}
			
			// Check if the mouse has hovered over any buttons
			// 0 is the representative number of the Main Menu menuState
			checkHoverButtons(0, mouseX, mouseY);
		} else if(menuState.equals("Enter Name")) {
			// Enter Name page
			// Check if the mouse has hovered over any buttons
			// 1 is the representative number of the Enter Name menuState
			checkHoverButtons(1, mouseX, mouseY);
		} else if(menuState.equals("High Scores")) {
			// Highscores page
			// Check if the mouse has hovered over any buttons
			// 2 is the representative number of the High Scores menuState
			checkHoverButtons(2, mouseX, mouseY);
			
			// Check if the held buttons have been hovered over
			for(int i = 0; i < categoryButtons.length; i++) {
				if(categoryButtons[i].checkHover(mouseX, mouseY)) {
					// The mouse is contained in the button, so the buttons should be
					// highlighted
					categoryButtons[i].setHighlighted(true);
				} else {
					categoryButtons[i].setHighlighted(false);
				}
			}
		} else if(menuState.equals("Instructions 1")) {
			// Instructions Page 1
			// Check if the mouse has hovered over any buttons
			// 3 is the representative number of the Instructions 1 menuState
			checkHoverButtons(3, mouseX, mouseY);
		} else if(menuState.equals("Instructions 2")) {
			// Instructions Page 2
			// Check if the mouse has hovered over any buttons
			// 4 is the representative number of the Instructions 2 menuState
			checkHoverButtons(4, mouseX, mouseY);
		} else if(menuState.equals("Instructions 3")) {
			// Instructions Page 3
			// Check if the mouse has hovered over any buttons
			// 5 is the representative number of the Instructions 3 menuState
			checkHoverButtons(5, mouseX, mouseY);
		} else if(menuState.equals("About")) {
			// About page
			// Check if the mouse has hovered over any buttons
			// 6 is the representative number of the About menuState
			checkHoverButtons(6, mouseX, mouseY);
		} else if(menuState.equals("Pause")) {
			// Pause screen
			// Check if the mouse has hovered over any buttons
			// 7 is the representative number of the Pause menuState
			checkHoverButtons(7, mouseX, mouseY);
		}
	}
	
	// Method Description: The render method will render in the menu in its current state
	// onto the application.
	// Parameters: The Graphics g stores the graphics of the application where the menu should
	// be rendered
	public void render(Graphics g) {
		// The menu state is checked and the corresponding page is rendered
		if(menuState.equals("Main Menu")) {
			// The background is rendered
			g.drawImage(mainBackground, 0, 0, null);
			// The title is drawn in
			// Here the title is center aligned so that the changing size can look more natural
			g.drawImage(mainTitle[curTitle], 325 - mainTitle[curTitle].getWidth() / 2, 
					185 - mainTitle[curTitle].getHeight() / 2, null);
			
			// The associated number for the Main Menu is 0,
			// which stores the location where the buttons are placed, so all of the buttons
			// for that menu page are rendered
			for(int i = 0; i < buttons[0].length; i++) {
				buttons[0][i].render(g);
			}
		} else if(menuState.equals("Enter Name")) {
			// The background is rendered
			g.drawImage(dirtBackground, 0, 0, null);
			// Draw in the name field
			if(!fieldHighlighted) {
				g.drawImage(nameField, 540 - nameField.getWidth() / 2, 220 - nameField.getHeight() / 2, null);
			} else {
				g.drawImage(highlightedNameField, 540 - highlightedNameField.getWidth() / 2, 
						220 - highlightedNameField.getHeight() / 2, null);
			}
			
			// The heading of the enter name screen is drawn
			g.setColor(white);
			g.setFont(boldFont);
			g.drawString("ENTER YOUR NAME", 80, 158);
			
			// The player name is rendered in, and if the player has not yet entered a name, they may
			// not know where to click so the field will prompt them to click to select and enter a name
			g.setFont(largeRegularFont);
			if(playerName.length() == 0 && !fieldHighlighted) {
				g.drawString("Click to Select", 95, 238);
			} else {
				g.drawString(playerName, 95, 238);
			}
			
			// The error message is shown if the player performed and illegal action
			if(errorMessage.length() != 0) {
				g.setFont(smallRegularFont);
				g.setColor(black);
				g.drawString(errorMessage, 83, 292);
				
				g.setColor(red);
				g.setFont(smallRegularFont);
				g.drawString(errorMessage, 85, 290);
			}
			
			// The associated number for the Enter Name menu is 1, so the buttons for page are rendered
			for(int i = 0; i < buttons[1].length; i++) {
				buttons[1][i].render(g);
			}
		} else if(menuState.equals("High Scores")) {
			// The background is rendered
			g.drawImage(dirtBackground, 0, 0, null);
			
			// The highscores heading is drawn in
			g.setColor(white);
			g.setFont(largeBoldFont);
			g.drawString("Highscores", 40, 90);
			
			// All of the held down buttons which represent the category that was selected
			// are rendered
			for(int i = 0; i < categoryButtons.length; i++) {
				categoryButtons[i].render(g);
			}
			
			// The highscore table is rendered in
			for(int i = 0; i < 6; i++) {
				if(i % 2 == 0) {
					g.setColor(lightGray);
				} else {
					g.setColor(gray);
				}
				g.fillRect(33, 180 + i * 60, 672, 60);
			}
			for(int i = 0; i < 6; i++) {
				if(i % 2 == 0) {
					g.setColor(lightGray);
				} else {
					g.setColor(gray);
				}
				g.fillRect(717, 180 + i * 60, 330, 60);
			}
			// The heading and main infor for the highscore table is rendered in with the 
			// font and color of the highscore table
			g.setColor(white);
			g.setFont(mediumRegularFont);
			g.drawString("#", 50, 219);
			g.drawString("Player", 100, 219);
			g.drawString("1", 55, 279);
			g.drawString("2", 54, 339);
			g.drawString("3", 54, 399);
			g.drawString("4", 54, 459);
			g.drawString("5", 54, 519);
			// All of the highscores for the corresponding high score category are rendered in
			if(category.equals("Mosquitoes")) {
				// The heading for the score is drawn in as well
				g.drawString("Mosquitoes Swatted", 734, 219);
				// 0 is the associated number for the mosquitoes category
				for(int i = 0; i < names.get(0).size(); i++) {
					g.drawString(names.get(0).get(i), 100, 279 + i * 60);
				}
				for(int i = 0; i < scores.get(0).size(); i++) {
					g.drawString(scores.get(0).get(i), 734, 279 + i * 60);
				}
			} else if(category.equals("Campfire")) {
				g.drawString("Time Fire Active", 734, 219);
				// 1 is the associated number for the campfire category
				for(int i = 0; i < names.get(1).size(); i++) {
					g.drawString(names.get(1).get(i), 100, 279 + i * 60);
				}
				for(int i = 0; i < scores.get(1).size(); i++) {
					g.drawString(scores.get(1).get(i), 734, 279 + i * 60);
				}
			} else if(category.equals("Distance")) {
				g.drawString("Meters Travelled", 734, 219);
				// 2 is the associated number for the distance category
				for(int i = 0; i < names.get(2).size(); i++) {
					g.drawString(names.get(2).get(i), 100, 279 + i * 60);
				}
				for(int i = 0; i < scores.get(2).size(); i++) {
					g.drawString(scores.get(2).get(i), 734, 279 + i * 60);
				}
			}
			
			// The associated number for the first high scores page menu is 2
			for(int i = 0; i < buttons[2].length; i++) {
				buttons[2][i].render(g);
			}
		} else if(menuState.equals("Instructions 1")) {
			// The background is rendered in
			g.drawImage(instructionsBackground1, 0, 0, null);
			
			// The header for the instructions page is drawn in
			g.setColor(black);
			g.setFont(boldFont);
			g.drawString("INSTRUCTIONS", 80, 80);
			
			// All of the first page instructions are drawn onto the screen
			g.setFont(smallRegularFont);
			g.drawString("Movement", 80, 120);
			g.fillRect(80, 123, 120, 4);
			g.drawString("Press A to move left", 90, 180);
			g.drawString("Press D to move right", 90, 210);
			
			g.drawString("Press SHIFT to", 210, 330);
			g.drawString("sprint", 210, 360);
			
			g.drawString("Press SPACE to jump", 90, 500);
			
			g.drawString("Interaction", 575, 120);
			g.fillRect(575, 123, 133, 4);
			g.drawString("Mosquitoes will attack the", 575, 170);
			g.drawString("player", 575, 200);
			g.drawString("Press K with a racket", 710, 255);
			g.drawString("to swat them away", 710, 285);
			g.drawString("Mosquito bites will cause", 710, 345);
			g.drawString("the player's health to", 710, 375);
			g.drawString("deplete until they have to respawn", 575, 405);
			g.drawString("Sprinting,", 575, 475);
			g.drawString("jumping, ", 575, 505);
			g.drawString("and swatting all use stamina", 575, 535);
			
			// The associated number for the first instructions page menu is 3
			for(int i = 0; i < buttons[3].length; i++) {
				buttons[3][i].render(g);
			}
		} else if(menuState.equals("Instructions 2")) {
			// The background is rendered in
			g.drawImage(instructionsBackground2, 0, 0, null);
			
			// The header for the page is drawn in
			g.setColor(black);
			g.setFont(boldFont);
			g.drawString("INSTRUCTIONS", 80, 80);
			
			// All of the second page instructions are drawn onto the screen
			g.setFont(smallRegularFont);
			g.drawString("Inventory", 80, 120);
			g.fillRect(80, 123, 116, 4);
			g.drawString("Press L to pick up items", 90, 160);
			g.drawString("All items will be added to the", 90, 190);
			g.drawString("inventory", 90, 220);
			g.drawString("Selected items appear blue", 90, 280);
			g.drawString("Press 1 - 9", 400, 314);
			g.drawString("to select", 400, 342);
			g.drawString("items", 400, 370);
			g.drawString("If the player becomes overburdened,", 90, 410);
			g.drawString("slots appear red, they cannot pick", 90, 440);
			g.drawString("up more items, and all stamina is lost", 90, 470);
			
			g.drawString("Campfire", 575, 120);
			g.fillRect(575, 123, 114, 4);
			g.drawString("Press L with selected", 575, 170);
			g.drawString("items to add them to", 575, 200);
			g.drawString("the fire", 575, 230);
			g.drawString("Campfires can only", 575, 290);
			g.drawString("be lit when it has", 575, 320);
			g.drawString("tinder and kindling", 575, 350);
			g.drawString("Press L with a flint", 780, 460);
			g.drawString("and steel to light", 780, 490);
			g.drawString("the fire", 780, 520);
			
			
			// The associated number for the second instructions page menu is 4
			for(int i = 0; i < buttons[4].length; i++) {
				buttons[4][i].render(g);
			}
		} else if(menuState.equals("Instructions 3")) {
			// The background is rendered in
			g.drawImage(instructionsBackground3, 0, 0, null);
			
			// The header for the page is drawn in
			g.setColor(black);
			g.setFont(boldFont);
			g.drawString("INSTRUCTIONS", 80, 80);
			
			// All of the third page instructions are drawn onto the screen
			g.setFont(smallRegularFont);
			g.drawString("Scouters", 80, 120);
			g.fillRect(80, 123, 104, 4);
			g.drawString("Press L to talk to", 90, 160);
			g.drawString("scouters", 90, 190);
			g.drawString("Scouters with", 90, 250);
			g.drawString("exclamation", 90, 280);
			g.drawString("marks offer quests", 90, 310);
			g.drawString("Press L to", 330, 415);
			g.drawString("advance", 330, 445);
			g.drawString("dialogue", 330, 475);
			
			g.drawString("Other Information", 575, 120);
			g.fillRect(575, 123, 220, 4);
			g.drawString("Press P to pause and unpause the", 575, 170);
			g.drawString("game", 575, 200);
			g.drawString("Quests are advanced once the ", 575, 260);
			g.drawString("objective is finished. However, if", 575, 290);
			g.drawString("the same scouter gives both quests,", 575, 320);
			g.drawString("the exclamation mark will not", 575, 350);
			g.drawString("move but dialogue will change", 575, 380);
			
			g.drawString("Press T to advance to the next", 575, 440);
			g.drawString("quest (only for QOL purposes)", 575, 470);
			
			// The associated number for the second instructions page menu is 5
			for(int i = 0; i < buttons[5].length; i++) {
				buttons[5][i].render(g);
			}
		} else if(menuState.equals("About")) {
			// The about page background is drawn in
			g.drawImage(aboutBackground, 0, 0, null);
			// The header for the about page is rendered as well
			g.setColor(black);
			g.setFont(largeBoldFont);
			g.drawString("About", 885, 105);
			
			// The about page information is drawn onto the screen
			g.setFont(smallRegularFont);
			g.drawString("This game was created by Harry He in 2023", 530, 160);
			g.drawString("under the guidance of Ms. Wong based on his", 528, 190);
			g.drawString("experiences in Scouts Canada.", 680, 220);
			g.drawString("The events in the game are based on true", 549, 280);
			g.drawString("teachings from Scouts and stories in Harry's", 519, 310);
			g.drawString("trip to the Haliburton Scout Reserve.", 612, 340);
			g.drawString("In order to generate the assets of the main", 534, 400);
			g.drawString("player, see Harry on the left", 711, 430);
			g.drawString("where he acted out all", 786, 460);
			g.drawString("of those motions.", 842, 490);
			
			// The associated number for the About menu is 6
			for(int i = 0; i < buttons[6].length; i++) {
				buttons[6][i].render(g);
			}
		} else if(menuState.equals("Pause")) {
			// The pause pop up background box is rendered in
			g.drawImage(pauseBackground, 320, 95, null);
			
			// The pause header is drawn in
			g.setColor(white);
			g.setFont(largeBoldFont);
			g.drawString("PAUSE", 463, 160);
			
			// The associated number for the About menu is 7, so all of the buttons in the
			// pause menu are rendered in.
			for(int i = 0; i < buttons[7].length; i++) {
				buttons[7][i].render(g);
			}
		}
	}
	
	// Method Description: The mousePressed method is called whenever the player presses
	// down on their mouse when in the menu and will update the menu if the player pressed a button.
	// Parameters: The MouseEvent e stores information about where the user pressed their
	// mouse.
	public void mousePressed(MouseEvent e) {
		// Local Variables
		// The integer selectedCategory stores the category that the user pressed which
		// is only used in the high scores page so that it can be detected if the
		// held buttons should be deselected.
		int selectedCategory;
		
		// Method Body
		// The menu state is checked so that the correct buttons for the page can
		// be checked if they are pressed
		if(menuState.equals("Main Menu")) {
			// 0 is the associated number for the main menu
			// The buttons are looped over to check if they are pressed
			for(int i = 0; i < buttons[0].length; i++) {
				if(buttons[0][i].checkHover(e.getX(), e.getY())) {
					if(i == 0) {
						// Play Button
						// Go to the Enter Name screen if the player
						// has not yet entered their name
						if(playerName.length() != 0) {
							// If player already entered their name, then they are 
							// entered into the game
							game.enterGame();
						} else {
							// Otherwise, the menu state is switched to go to the Enter
							// Name screen
							menuState = "Enter Name";
							errorMessage = "";
						}
					} else if(i == 1) {
						// High Scores Button
						// Go to the high scores screen
						menuState = "High Scores";
					} else if(i == 2) {
						// Instructions Button
						// Go to the first instructions page
						menuState = "Instructions 1";
					} else if(i == 3) {
						// About Button
						// go to the about screen
						menuState = "About";
					} else if(i == 4){
						// Quit Button
						// The game will be set to exit and the entire application
						// will be closed
						game.stop();
					}
				}
			}
		} else if(menuState.equals("Enter Name")) {
			// 1 is the associated number for the main menu
			// The buttons are looped over to check if they are pressed
			for(int i = 0; i < buttons[1].length; i++) {
				if(buttons[1][i].checkHover(e.getX(), e.getY())) {
					if(i == 0) {
						// Back Button
						// Go back to the main menu screen
						menuState = "Main Menu";
					} else if(i == 1) {
						// Play Button
						if(playerName.length() > 0) {
							// If the player entered their name then the game is activated, and the 
							// menuState is set back to the main menu, so that the player will not 
							// appear back in the enter name menu again
							game.enterGame();
							menuState = "Main Menu";
						} else {
							// Otherwise, the player is told that they have to enter their name to play
							errorMessage = "Enter a name to play";
						}
					}
				}
			}
			
			// Check if the user has pressed onto the name field.
			// If so, then select it, but if they have pressed anywhere
			// else then deselect the field
			if(e.getX() >= 540 - nameField.getWidth() / 2 && e.getX() <= 540 + nameField.getWidth() / 2
					&& e.getY() >= 220 - nameField.getHeight() / 2 && e.getY() <= 220 + nameField.getHeight() / 2) {
				fieldHighlighted = true;
			} else {
				fieldHighlighted = false;
			}
		} else if(menuState.equals("High Scores")) {
			// 2 is the associated number for the high scores page
			// The buttons are looped over to check if they are pressed
			for(int i = 0; i < buttons[2].length; i++) {
				if(buttons[2][i].checkHover(e.getX(), e.getY())) {
					if(i == 0) {
						// Back Button
						// Go back to the main menu screen
						menuState = "Main Menu";
					}
				}
			}
			
			// The selectedCategory is initially set to -1 as it is unknown which button was pressed
			selectedCategory = -1;
			// The held buttons are looped over to see if the player pressed a category button
			for(int i = 0; i < categoryButtons.length; i++) {
				if(categoryButtons[i].checkHover(e.getX(), e.getY())) {
					if(i == 0) {
						// Mosquitoes Button
						// The category is set to the mosquitoes category and that corresponding button
						// is set to be selected
						category = "Mosquitoes";
						categoryButtons[i].setSelected(true);
						// The selected category is set to the mosquito category's corresponding number
						selectedCategory = 0;
					} else if(i == 1) {
						// Campfire Button
						// The category is set to the campfire category and the corresponding button
						// is set to be selected
						category = "Campfire";
						categoryButtons[i].setSelected(true);
						// The selected category is set to the campfire category's corresponding number
						selectedCategory = 1;
					} else if(i == 2) {
						// Distance Traveled Button
						// The category is set to the distance category and the corresponding button is
						// set to be selected
						category = "Distance";
						categoryButtons[i].setSelected(true);
						// The selected category is set to the distance category's corresponding number
						selectedCategory = 2;
					}
				}
			}
			
			// If a category was selected, then the category buttons have to have their selection
			// changed
			if(selectedCategory != -1) {
				// The category buttons are looped over and the buttons that were not selected
				// are set to not be selected
				for(int i = 0; i < categoryButtons.length; i++) {
					if(i != selectedCategory) {
						categoryButtons[i].setSelected(false);
					}
				}
			}
		} else if(menuState.equals("Instructions 1")) {
			// 3 is the associated number for the first instructions page
			// The buttons are looped over to check if they are pressed
			for(int i = 0; i < buttons[3].length; i++) {
				if(buttons[3][i].checkHover(e.getX(), e.getY())) {
					if(i == 0) {
						// Back Button
						// Go back to the main menu screen
						menuState = "Main Menu";
					} else if(i == 1) {
						// Next Button
						// Go to the next instructions page
						menuState = "Instructions 2";
					}
				}
			}
		} else if(menuState.equals("Instructions 2")) {
			// 4 is the associated number for the first instructions page
			// The buttons are looped over to check if they are pressed
			for(int i = 0; i < buttons[4].length; i++) {
				if(buttons[4][i].checkHover(e.getX(), e.getY())) {
					if(i == 0) {
						// Back Button
						// Go to the previous instructions page
						menuState = "Instructions 1";
					} else if(i == 1){
						// Next Button
						// Go to the next instructions page
						menuState = "Instructions 3";
					}
				}
			}
		} else if(menuState.equals("Instructions 3")) {
			// 5 is the associated number for the first instructions page
			// The buttons are looped over to check if they are pressed
			for(int i = 0; i < buttons[5].length; i++) {
				if(buttons[5][i].checkHover(e.getX(), e.getY())) {
					if(i == 0) {
						// Back Button
						// Go to the previous instructions page
						menuState = "Instructions 2";
					}
				}
			}
		} else if(menuState.equals("About")) {
			// 6 is the associated number for the about menu
			// The buttons are looped over to check if they are pressed
			for(int i = 0; i < buttons[6].length; i++) {
				if(buttons[6][i].checkHover(e.getX(), e.getY())) {
					if(i == 0) {
						// Back Button
						// Go back to the main menu screen
						menuState = "Main Menu";
					}
				}
			}
		} else if(menuState.equals("Pause")) {
			// 7 is the associated number for the about menu
			// The buttons are looped over to check if they are pressed
			for(int i = 0; i < buttons[7].length; i++) {
				if(buttons[7][i].checkHover(e.getX(), e.getY())) {
					if(i == 0) {
						// Resume Button
						// The game is resumed again so the player is put back into the game
						game.enterGame();
					} else if(i == 1) {
						// High Scores Button
						// The menu goes to the high scores page
						menuState = "High Scores";
					} else if(i == 2) {
						// Main Menu Button
						// The menu goes back to the main menu page
						menuState = "Main Menu";
					}
				}
			}
		}
	}
	
	// Method Description: The keyPressed method is called whenever the player presses a key
	// in the menu. The method will update the player's name if the player is entering the 
	// name or will unpause the game if the player presses the pause hotkey.
	public void keyPressed(KeyEvent e) {
		// Local Variables
		// The integer key stores the key code of the key that the player pressed.
		// This key can be compared with constants in the KeyEvent class to determine
		// the key that was pressed.
		int key = e.getKeyCode();
		
		// Method Body
		// The menu state is checked as key presses will only do things in certain menu states
		if(menuState.equals("Enter Name") && fieldHighlighted) {
			// If the player is in the enter name screen and they have selected the text field
			// to enter in their name, their name will be updated depending on the keys they press.
			if(key == KeyEvent.VK_BACK_SPACE) {
				// If the player presses the back space key, then the name's last character will be removed
				if(playerName.length() > 0) {
					playerName = playerName.substring(0, playerName.length() - 1);
				}
			} else if(key == KeyEvent.VK_ENTER){
				// If the player enters, then the game will try to enter the game. However
				// if they have not entered a name then an error message is sent that the player has to
				// enter a name to play.
				if(playerName.length() > 0) {
					game.enterGame();
					menuState = "Main Menu";
				} else {
					errorMessage = "Enter a name to play";
				}
			} else if(e.getKeyChar() >= 32 && e.getKeyChar() <= 126){
				// If the player enters a valid ASCII code which is recognized in the text font, then the 
				// name will be added to with that character. However, if the player's name is too long
				// then an error message will be displayed.
				if(playerName.length() <= 24) {
					playerName += e.getKeyChar();
				} else {
					errorMessage = "Names cannot exceed 24 characters";
				}
			}
		} else if(menuState.equals("Pause")) {
			// If the player is in the pause screen and wish to unpause the game by pressing the 
			// pause hotkey P, then the game will resume.
			if(key == KeyEvent.VK_P) {
				game.unpause();
				game.enterGame();
			}
		}
	}
	
	// Method Description: The checkHoverButtons method will take in the index used
	// in the buttons 2D array list to represent all of the buttons currently used
	// in the menu and the X and Y position of the mouse, then highlight buttons that
	// the mouse highlights over.
	// Parameters: The buttonsIndex integer stores the index of the array of buttons
	// currently displayed. The mouseX and mouseY variables store the x and y position
	// of where the mouse is.
	private void checkHoverButtons(int buttonsIndex, double mouseX, double mouseY) {
		// All of the buttons in the current menu are checked if they are
		// hovered over.
		for(int i = 0; i < buttons[buttonsIndex].length; i++) {
			if(buttons[buttonsIndex][i].checkHover(mouseX, mouseY)) {
				// The mouse is contained in the button, so the buttons should be
				// highlighted
				buttons[buttonsIndex][i].setHighlighted(true);
			} else {
				buttons[buttonsIndex][i].setHighlighted(false);
			}
		}
	}
	
	// Method Description: The method setToPause is a method that allows the 
	// game to enter the menu's pause state by setting the menuState to "Pause"
	public void setToPause() {
		menuState = "Pause";
	}
	
	// Getter Methods
	public String getPlayerName() {
		return playerName;
	}
	
	// Setter Methods
	// Method Description: The setNames method will take in an index which designates
	// the category of highscores whose names are trying to be set and an ArrayList
	// of Strings and set all of the names in the highscore category to the names passed in.
	// Parameters: The integer index stores the category index which should be have its
	// highscores edited. The ArrayList of Strings nameColumn stores the names which
	// should appear in the name column of the highscores table.
	public void setNames(int index, ArrayList<String> nameColumn) {
		// The existing names are cleared
		names.get(index).clear();
		// All of the names are added into the highscores table in the
		// name section.
		for(int i = 0; i < nameColumn.size(); i++) {
			names.get(index).add(nameColumn.get(i));
		}
	}
	
	// Method Description: The setScores method will take in an index which designates
	// the category of highscores whose scores are trying to be set and an ArrayList
	// of Strings and set all of the scores in the highscore category to the scores passed in.
	// Parameters: The integer index stores the category index which should be have its
	// highscores edited. The ArrayList of Strings scoreColumn stores the scores which
	// should appear in the score column of the highscores table.
	public void setScores(int index, ArrayList<String> scoreColumn) {
		// The existing scores are cleared
		scores.get(index).clear();
		// All of the scores are added into the highscores table in the
		// scores section.
		for(int i = 0; i < scoreColumn.size(); i++) {
			scores.get(index).add(scoreColumn.get(i));
		}
	}
}
