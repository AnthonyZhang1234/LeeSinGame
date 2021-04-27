package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.io.*;

import java.awt.*;

public class Engine {
    /** Game renderer. */
    TERenderer ter = new TERenderer();
    /** Dimensions of the game world. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    /** Dimensions of the home menu. */
    public static final int MENU_WIDTH = 40;
    public static final int MENU_HEIGHT = 40;
    /** Helper variable storing number characters. */
    public static final String NUMBERS = "0123456789";
    /** Current Working Directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** Folder that holds save files. */
    public static final File SAVES = Utils.join(CWD, "saves");
    /** Helper variable that determines if the world has been created. */
    private boolean bigBoyNotCreated = true;
    /** Current game world. */
    private Area bigBoy;
    /** Tiles of the current game world. */
    private TETile[][] finalWorldFrame = null;

    /**
     * Method that displays what the user is typing.
     */
    public void drawFrame(String s) {
        // draws the menu;
        Font font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 2, s);
        StdDraw.show();
    }

    /**
     * Method that sustains the seed prompt while player inputs desired seed.
     */
    public void drawSeed() {
        Font font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT * 3 / 4,
                "Enter a number seed! Press (S) when satisfied!");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT * 3 / 4 + 2,
                "Goal: Turn on all the lanterns!");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 4, "Controls [Usually ;)]:");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 4 - 1, "W - Move Up");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 4 - 2, "A - Move Left");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 4 - 3, "S - Move Down");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 4 - 4, "D - Move Right");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 4 - 5, "O - Turn Lantern Off");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 4 - 6, "P - Turn Lantern On");
        StdDraw.show();
    }

    /**
     * Method that draws the home menu of the game.
     */
    public void drawMenu() {
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT * 3 / 4, "Lee Sin and the Spooky Lanterns");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 2, "Press (N) to Start!");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 2 - 5, "Press (L) to Load Game!");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 2 - 10, "Press (Q) to Quit!");
        StdDraw.show();
    }

    /**
     * Method responsible for updating the HUD of the world based on players' mouse location.
     */
    public void drawHud(TETile[][] world) {
        Font font = new Font("Monaco", Font.BOLD, 10);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.BLACK);
        StdDraw.filledRectangle(WIDTH / 2, HEIGHT - 1, WIDTH / 2, 0.5);
        StdDraw.setPenColor(Color.WHITE);
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if (bigBoy.isCursed()) {
            StdDraw.text(5, HEIGHT - 1, "You've been cursed!");
        }
        if (bigBoy.isConfused()) {
            StdDraw.text(WIDTH - 5, HEIGHT - 1, "You're now confused!");
        }
        if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT - 1) {
            TETile hudTile = world[x][y];
            StdDraw.text(WIDTH / 2, HEIGHT - 1, hudTile.description());
            StdDraw.show();
        }
    }

    /**
     * Helper method to turn chars into lowercase.
     */
    private char lowerCase(char c) {
        String temp = "" + c;
        return temp.toLowerCase().charAt(0);
    }

    /**
     * Method to update the world whenever the user presses a key.
     */
    private int updateWorld(String input, int count) {
        if (count == 0) {
            ter.initialize(WIDTH, HEIGHT);
            count++;
        }
        finalWorldFrame = interactWithInputString(input);
        ter.renderFrame(finalWorldFrame);
        return count;
    }

    /**
     * Method that handles the scenario where the player types ':q' to quit the game.
     */
    public void handleQuitCase(String input, boolean ag) {
        String possQuit = input.toLowerCase().substring(0, input.length() - 2);
        if (input.toLowerCase().substring(input.length() - 2).equals(":q")) {
            File saveFile = Utils.join(CWD, "save.txt");
            Utils.writeContents(saveFile, possQuit);
            if (!ag) {
                System.exit(0);
            }
        }
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        String input = "";
        if (!SAVES.exists()) {
            SAVES.mkdir();
        }
        ter.initialize(MENU_WIDTH, MENU_HEIGHT);
        drawMenu();
        int count = 0;
        boolean inGame = false;
        while (true) {
            // this should run once you type some keys
            if (StdDraw.hasNextKeyTyped()) {
                //checks if the user wanted to do some action from the menu
                char typed = lowerCase(StdDraw.nextKeyTyped());
                if (input.length() == 0) {
                    switch (typed) {
                        case 'n':
                            input += typed;
                            StdDraw.clear(Color.BLACK);
                            drawSeed();
                            break;
                        case 'l':
                            input = "load"
                                    + Utils.readContentsAsString(Utils.join(CWD, "save.txt"));
                            input += typed;
                            count = updateWorld(input, count);
                            input = input.substring(4);
                            break;
                        case 'q':
                            System.exit(0);
                            break;
                        default:
                            continue;
                    }
                } else {
                    if (!input.contains("s") && typed != 's') {
                        //only reads the parts where the character a number to pass as a seed
                        if (NUMBERS.indexOf(typed) >= 0) {
                            input += typed;
                            drawFrame(input.substring(1));
                            drawSeed();
                        }
                    } else if (input.length() > 1) {
                        inGame = true;
                        input += typed;
                        handleQuitCase(input, false);
                        count = updateWorld(input, count);
                    }
                }
            }
            if (inGame) {
                drawHud(finalWorldFrame);
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        if (input.toLowerCase().charAt(0) == 'l') { // for AG
            input = Utils.readContentsAsString(Utils.join(CWD, "save.txt")) + input; // for AG
        } // for AG
        boolean load = false;
        if (input.length() > 4 && input.substring(0, 4).equals("load")) {
            input = input.substring(4);
            load = true;
        }
        int sPos = input.toLowerCase().indexOf('s');
        long seed = Long.parseLong(input.substring(1, sPos));
        String commands = input.substring(sPos + 1);
        if (bigBoyNotCreated) {
            bigBoy = new Area(0, 0, WIDTH, HEIGHT);
            bigBoyNotCreated = false;
            finalWorldFrame = bigBoy.generateWorld(seed);
        }
        finalWorldFrame = bigBoy.moveLee(commands, finalWorldFrame, ter, load);
        handleQuitCase(input, true); // for AG
        return finalWorldFrame;
    }
}
