package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        // Initialize random number generator
        this.rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        // Generate random string of letters of length n
        String temp = "";
        for (int i = 0; i < n; i++) {
            int size = CHARACTERS.length;
            int randInt = this.rand.nextInt(size);
            temp += CHARACTERS[randInt];
        }
        return temp;
    }

    public void drawFrame(String s) {
        // Take the string and display it in the center of the screen
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.text(this.width / 2, this.height / 2, s);
        // If game is not over, display relevant game information at the top of the screen
        if (!this.gameOver) {
            Font nextFont = new Font("Monaco", Font.BOLD, 15);
            StdDraw.setFont(nextFont);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.text(3, this.height - 1, "Round: " + this.round);
            int randInt = this.rand.nextInt(ENCOURAGEMENT.length);
            StdDraw.text(this.width - 6, this.height - 1, ENCOURAGEMENT[randInt]);
            if (this.playerTurn) {
                StdDraw.text(this.width / 2, this.height - 1, "Type!");
            } else {
                StdDraw.text(this.width / 2, this.height - 1, "Watch!");
            }
            StdDraw.line(0, this.height - 2, this.width, this.height - 2);
        }
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        // Display each character in letters, making sure to blank the screen between letters
        for (int i = 0; i < letters.length(); i++) {
            String letter = Character.toString(letters.charAt(i));
            drawFrame(letter);
            StdDraw.pause(1000);
            drawFrame("");
            StdDraw.pause(500);
        }
        StdDraw.clear(Color.BLACK);
    }

    public String solicitNCharsInput(int n) {
        // Read n letters of player input
        String input = "";
        while (input.length() != n) {
            if (StdDraw.hasNextKeyTyped()) {
                input += StdDraw.nextKeyTyped();
                drawFrame(input);
            }
        }
        return input;
    }

    public void startGame() {
        // Set any relevant variables before the game starts
        boolean correct = true;

        // Establish Engine loop
        while (correct) {
            this.playerTurn = false;
            this.round++;
            drawFrame("Round: " + this.round);
            StdDraw.pause(1000);
            String letters = generateRandomString(this.round);
            flashSequence(letters);
            this.playerTurn = true;
            String answer = solicitNCharsInput(this.round);
            StdDraw.pause(1000);
            correct = answer.equals(letters);
        }
        this.gameOver = true;
        drawFrame("Game Over! You made it to round: " + this.round);
    }

}
