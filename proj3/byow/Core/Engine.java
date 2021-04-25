package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.io.*;
import java.util.*;
import static byow.Core.Utils.*;

import java.awt.*;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final int MENU_WIDTH = 40;
    public static final int MENU_HEIGHT = 40;
    public static final String NUMBERS = "0123456789";
    private static final File CWD = new File(System.getProperty("user.dir"));
    private static final File SAVES = join(CWD, "saves");
    private boolean bigBoyNotCreated = true;
    private Area bigBoy;
    private TETile[][] finalWorldFrame = null;

    public void drawFrame(String s) {
        // draws the menu;
        Font font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 2, s);
        StdDraw.show();
    }

    public void drawSeed() {
        Font font = new Font("Monaco", Font.BOLD, 15);
        StdDraw.setFont(font);
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT * 3 / 4,
                "Enter a number seed! Press (S) when satisfied!");
        StdDraw.show();
    }

    public void drawMenu() {
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT * 3 / 4, "Best Game");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 2, "Press (N) to Start!");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 2 - 5, "Press (L) to Load Game!");
        StdDraw.text(MENU_WIDTH / 2, MENU_HEIGHT / 2 - 10, "Press (Q) to Quit!");
        StdDraw.show();
    }

    public void drawHud() {

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
        while (true) {
            // this should run once you type some keys to be the seed
            if (StdDraw.hasNextKeyTyped()) {
                //when the user does not type n or s
                char typed = StdDraw.nextKeyTyped();
                if (input.length() == 0) {
                    switch (typed) {
                        case 'n':
                            input += typed;
                            StdDraw.clear(Color.BLACK);
                            drawSeed();
                            break;
                        case 'l':
                            input = readContentsAsString(join(SAVES, "save"));
                            input += typed;
                            if (count == 0) {
                                ter.initialize(WIDTH, HEIGHT);
                                count++;
                            }
                            TETile[][] finalWorldFrame = interactWithInputString(input);
                            ter.renderFrame(finalWorldFrame);
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
                    } else if (input.length() > 2){
                        input += typed;
                        System.out.println(input);
                        String possQuit = input.toLowerCase().substring(0, input.length() - 2);
                        if (input.toLowerCase().substring(input.length() - 2).equals(":q")) {
                            File saveFile = join(SAVES, "save");
                            writeContents(saveFile, possQuit);
                            System.exit(0);
                            break;
                        }
                        if (count == 0) {
                            ter.initialize(WIDTH, HEIGHT);
                            count++;
                        }
                        TETile[][] finalWorldFrame = interactWithInputString(input);
                        ter.renderFrame(finalWorldFrame);
                    }
                }
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
        int sPos = input.toLowerCase().indexOf('s');
        long seed = Long.parseLong(input.substring(1, sPos));
        String commands = input.substring(sPos + 1);

        if (bigBoyNotCreated) {
            bigBoy = new Area(0, 0, WIDTH, HEIGHT);
            bigBoyNotCreated = false;
            finalWorldFrame = bigBoy.generateWorld(seed, commands);
        }

        finalWorldFrame = bigBoy.moveLee(commands, finalWorldFrame);

        return finalWorldFrame;
        // return null;
    }
}
