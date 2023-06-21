/*
 * Class Name: Score
 * Description: The Score class represents a highscore in any category. The class 
 * allows highscores to be sorted more easily by implementing the Comparable interface
 * so that highscores can be put inside of a TreeSet. Additionally the class does
 * include an indicator of when scores were added so that earlier achieved scores
 * are counted as higher.
 */

// The Score class implements the Comparable interface so that they can be recognized
// as being able to be ranked
public class Score implements Comparable<Score>{
	// The String name stores the name of the player which achieved the score
	private String name;
	// The integer score stores a certain score value that the player achieved.
	// For different score categories, this could be mosquitoes swatted, campfire
	// ticks while lit, and distance traveled
	private int score;
	// The long timeAdded stores the current time in milliseconds when the score
	// was added. The author recognizes that the long can have some limitations
	// but feels that it will last long enough, so instead of using some other date class,
	// the hundred years of longevity is sacrificed for faster score comparison
	private long timeAdded;
	
	// Method Description: The constructor for the Score class will take in information
	// about the score and declare the instance variables of the current instance to the
	// values passed in.
	// Parameters: The String name stores the name of the player who achieved the score. 
	// The integer score stores the score that the player achieved. The long timeAdded
	// stores the time when the score was achieved
	public Score(String name, int score, long timeAdded) {
		// The instance variables are set to the values passed in
		this.name = name;
		this.score = score;
		this.timeAdded = timeAdded;
	}

	// Method Description: The compareTo method completes the method set out by the Comparable
	// interface and allows Scores to be compared to each other. The method will compare two
	// scores and return a corresponding value based on where each score should go relative
	// to the other.
	// Parameters: The Score s represents the other score that the current score shold
	// be compared to.
	// Return: The method will return < 0 if the current score instance should be ranked
	// higher than the other score, 0 if they are equivalent scores, and > 0 if the 
	// current score should be put below the other score.
	public int compareTo(Score s) {
		if(this.score > s.score) {
			// First, the scores are compared. Higher score values means that the Score
			// is ranked higher so here the method returns -1 to put the current score
			// higher ranked.
			return -1;
		} else if(this.score < s.score) {
			// The current instance's score is worse than the other score so it is
			// put after
			return 1;
		} else {
			// If the scores are the same, then the time when the score was added is compared.
			// Earlier scores are ranked higher.
			if(this.timeAdded < s.timeAdded) {
				return -1;
			} else if(this.timeAdded > s.timeAdded) {
				return 1;
			}
		}
		
		// Otherwise, the scores are put in alphabetical order of the names. This is very unlikely
		// however, as the scores would have to be achieved within one millisecond of each other,
		// which is practically impossible in a single player game.
		return name.compareTo(s.name);
	}
	
	// Getter Methods
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
	}
	
	public long getTimeAdded() {
		return timeAdded;
	}
}
