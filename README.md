# Camp Haliburton
Author: Harry He

## Project Description
A video demo of how the game is played.

https://github.com/he-is-harry/Camp-Haliburton/assets/69978107/1f106d4d-65f5-4c0a-aa85-c87d70a9f1c6

The application will be able to run a 2D game with keyboard input used to play the actual game and mouse input used to go through the game's menu. The game will be about the author's experiences in Scouts Canada, allowing the player to relive through them through an adventure style game. All technologies used in the game are apart of Java Standard Library, with features used like graphics and text file streaming. The challenges faced when making this game were creating animated wrapped text within speech box bounds, creating a coherent item system, and integrating animation into the actions performed in the game.

## Asset Generation
Many of the assets used in this game are taken from commercially published images. As such, this game cannot be sold for commercial use as it is simply used for educational purposes. That being said, some of the assets were generated by the author himself, acting out the animations of the player that you see in the game. In the **developer** branch of this repository, you will find classes used by the author to pixelate and process the images in the game.

Here is an example of the asset modelling that was done to create the animations in the game.

https://github.com/he-is-harry/Camp-Haliburton/assets/69978107/7a7ebcd6-6708-4a38-bbe3-3cfcf4b9ec4d

Resources that were of great assistance in creating this program are listed below for reference for other developers
- Removing Backgrounds: [https://www.remove.bg/](https://www.remove.bg/)
- Pixelating Images: [https://onlineimagetools.com/pixelate-image](https://onlineimagetools.com/pixelate-image)
    - The author has found that the algorithm used in this program is superior to the simple heuristic developed by himself and would recommend others try this software if attempting to pixelate assets like he did.
- Performing Pixel Art and Refinement: [https://www.piskelapp.com/p/create/sprite](https://www.piskelapp.com/p/create/sprite)
- Creating Raw Images from Unpixelated Images: [https://docs.google.com/drawings/](https://docs.google.com/drawings/)

## Installation and Running
1\. Open the zip file of the project, inside there should be a folder called "**HarryHe_ISU**". Move the folder outside of the zip package.  
**Note**: If you see a folder called `_MACOSX` you can ignore this.

Since the game is a java application, at least a JRE, Java Runtime Environment, is
required to run the game. However, I (Harry) would recommend that the JDK, Java Development
Kit, should be downloaded for ease of use.
	
If you do not have a JDK, follow these steps  

2\. Go to [https://www.oracle.com/java/technologies/downloads/](https://www.oracle.com/java/technologies/downloads/) and download
the version of the JDK that matches your operating system and your device specifics.
The installer may be the easiest way to down load the JDK.
	
3\. Once downloaded, open the installer and follow the instructions to install the
JDK.

From here you have two options of running the java application
- Using the [command line](#terminal)
- Using an [IDE](#eclipse)

### <a name="terminal"></a>Using the command line to run the application
4\. To correctly format the java project for command line use, open the project folder
which should be named "HarryHe_ISU", and move the folder "**res**" into the "**src**" folder.
Then, you must move the file "**highscore.txt**" into the "**src**" folder.  

5\. Find the directory in which the `HarryHe_ISU` folder is found, should
look similar to `C:\Users\username\Desktop` but will be different due to a different
user name and download location. This path should not include the `HarryHe_ISU` folder,
just the part before.  

6\. Open the command prompt, this can be done by searching `cmd` in the start window, 
or searching `terminal` on mac.  

7\. Change your directory to the path found before.  

    cd C:\Users\username\Desktop

8\. Change your directory to the src folder found within the "HarryHe_ISU" folder

    cd HarryHe_ISU\src

9\. Compile the Main.java file to allow the game to be run

    javac Main.java

10\. Run the Main class file to run the project

    java Main


#### Troubleshooting
If you see `javac is not recognized as an internal or external command`, here are
some steps to solve your issue
	
#### Windows
1. Locate where the JDK is installed, for guidance it should look something like
`C:\Program Files\Java\jdk-18.0.1.1`.

2. Then add upon the sub folder bin to the path, this is where the javac application
is held. It should look like `C:\Program Files\Java\jdk-18.0.1.1\bin`.
	
3. In the start window search `environment variable` and click the `Edit the
system environment variables` option.
	
4. Click on the environment variables button. Then you will find a list
of your user and system variables.
	
5. If you do not have a `Path` variable under your user, you can add a new variable
named `PATH` with the path of the JDK's bin, i.e. `C:\Program Files\Java\jdk-18.0.1.1\bin`.
	
6. If you do have a `Path` variable, you should edit the `Path` variable
by adding a semicolon to the end of the pre-existing paths, then add the
path of the JDK's bin. For instance the change would look like,

	```
	"C:\Users\jack\AppData\Local\Programs\Python\Python39\Scripts\
	C:\Users\jack\AppData\Local\Programs\Python\Python39\
	%USERPROFILE%\AppData\Local\Microsoft\WindowsApps"
	
	to
	
 	"C:\Users\jack\AppData\Local\Programs\Python\Python39\Scripts\
	C:\Users\jack\AppData\Local\Programs\Python\Python39\
	%USERPROFILE%\AppData\Local\Microsoft\WindowsApps; C:\Program Files\Java\jdk-18.0.1.1\bin"
 	```
 
8. Save your changes by pressing `OK`.

#### Mac
1. Locate where the JDK is installed, for guidance it should look something like `/Library/Java/JavaVirtualMachines/jdk-11.0.10.jdk/Contents/Home`.
	
2. Add upon the subfolder bin to the path, which is where the javac application
is held. It should look like `/Library/Java/JavaVirtualMachines/jdk-11.0.10.jdk/Contents/Home/bin`.
	
3. Open the terminal, by searching `terminal` in the search bar.

4. Open the paths file, by running the command 
	```	
	sudo nano /etc/paths
 	```
	
5. Go to the bottom of the path, then add on the path of the JDK bin.
	```
	UW PICO 5.09                        File: /etc/paths                          
	
	/usr/local/bin
	/usr/bin
	/bin
	/usr/sbin
	/sbin
	/Library/Java/JavaVirtualMachines/jdk-11.0.10.jdk/Contents/Home/bin
 	```
		
6. Press Control-X to save quit the file editor, and press `Y` to save the new
path file.
	
7. To test, you can refresh the terminal and run the command below to see the new
edited paths.
	
 	```
	echo $PATH
	```
		
### <a name="eclipse"></a>Using an IDE, Eclipse
If you do not have Eclipse or another IDE that can run Java, follow steps 4 - 5,
otherwise you can skip to step 6.

4\. Download the Eclipse installer from [https://www.eclipse.org/downloads/](https://www.eclipse.org/downloads/)

5\. Run the Eclipse installer and follow the instructions to install the Eclipse
application.  
**Note**: Select Eclipse for Java Developers since this project is a java application.
	
6\. Open the Eclipse application.

7\. Create a new Java Project and enter a name for the project.
	
8\. Right click the project and press import.
	
9\. Select the General option, and then the File System.
	
10\. Select the `HarryHe_ISU` folder to import from, which will be in the directory
in which you copied the folder to. Check off the `HarryHe_ISU` box to import all 
content within.  
**Note**: You may need to overwrite the existing default settings, just click yes
to allow the entire java application to copy over.
	
11. From there run `Main.java` as a java application or you can select the entire
project and run it as a java application.

## Using the Project
The project will accept mouse and keyboard input. To play the game, refer to the instructions screen to learn about keyboard controls. To control the application use the mouse and cursor as input, pressing the buttons to navigate the game.

## Credits
Author: Harry He  
Advisor: Ms. Wong  
Game Model: Harry He  
	
Thanks to the creative team for the idea of this game, Harry He, who came up with this idea during the summer of 2022, when trying to think of ideas for his grade 12 final project. Harry's experiences in Scouts Canada inspired him to make the game, where he wanted to share his learnings with the other students at his school. Thanks to the scouter's who inspired the game, Scouter Bryant, Scouter Hubert, Scouter Paige, Scouter Tom and Scouter John.

## License

MIT License

Copyright (c) 2023 Harry He

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
	
