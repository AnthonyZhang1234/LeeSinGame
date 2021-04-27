package byow.TileEngine;

import java.awt.Color;

/**
 * Contains constant tile objects, to avoid having to remake the same tiles in different parts of
 * the code.
 *
 * You are free to (and encouraged to) create and add your own tiles to this file. This file will
 * be turned in with the rest of your code.
 *
 * Ex:
 *      world[x][y] = Tileset.FLOOR;
 *
 * The style checker may crash when you try to style check this file due to use of unicode
 * characters. This is OK.
 */

public class Tileset {
    public static final TETile AVATAR = new TETile('@', Color.white, Color.black, "you");
    public static final TETile WALL = new TETile('#', new Color(216, 128, 128), Color.darkGray,
            "wall");
    public static final TETile FLOOR = new TETile('·', new Color(128, 192, 128), Color.black,
            "floor");
    public static final TETile NOTHING = new TETile(' ', Color.black, Color.black,
            "Nothing (The endless void of my heart, someone call me (248)434-5508...)");
    public static final TETile GRASS = new TETile('"', Color.green, Color.black, "grass");
    public static final TETile WATER = new TETile('≈', Color.blue, Color.black, "water");
    public static final TETile FLOWER = new TETile('❀', Color.magenta, Color.pink, "flower");
    public static final TETile LOCKED_DOOR = new TETile('█', Color.orange, Color.black,
            "locked door");
    public static final TETile UNLOCKED_DOOR = new TETile('▢', Color.orange, Color.black,
            "unlocked door");
    public static final TETile SAND = new TETile('▒', Color.yellow, Color.black, "sand");
    public static final TETile MOUNTAIN = new TETile('▲', Color.gray, Color.black, "mountain");
    public static final TETile TREE = new TETile('♠', Color.green, Color.black, "tree");
    public static final TETile LEE_SIN = new TETile('L', Color.yellow, Color.black,
            "Lee Sin (\"Your will, my hands...\")", "lee_sin_floor_3.png");
    public static final TETile LEE_SIN_2 = new TETile('E', Color.yellow, Color.black,
            "lee sin 2", "lee_sin2.png");
    public static final TETile BASIC_WALL = new TETile('W', Color.black, Color.black,
            "Wall (Chuck Norris once took down a fence. "
                    + "Maybe you heard of it, the Berlin wall...)", "basic_wall.png");
    public static final TETile BASIC_FLOOR = new TETile('F', Color.black, Color.black,
            "basic floor", "basic_floor.png");
    public static final TETile BASIC_FLOOR_2 = new TETile('F', Color.black, Color.black,
            "Floor", "basic_floor_2.png");
    public static final TETile LIT_ONE = new TETile('F', Color.black, Color.black,
            "Floor (Shine bright like a diamond...)", "lit_floor_p_one.png");
    public static final TETile BASE = new TETile('F', Color.black, Color.black,
            "Floor (Floor gang...)", "basic_floor_3.png");
    public static final TETile DIM_ONE = new TETile('F', Color.black, Color.black,
            "Floor (If you fall, I will be there for you - Floor...)", "lit_floor_m_one.png");
    public static final TETile DIM_TWO = new TETile('F', Color.black, Color.black,
            "Floor (What's the highest floor of any building? Floor 20 ;)...)",
            "lit_floor_m_two.png");
    public static final TETile DIM_THREE = new TETile('F', Color.black, Color.black,
            "Floor (Please do not pee on me...)", "lit_floor_m_three.png");
    public static final TETile DIM_FOUR = new TETile('F', Color.black, Color.black,
            "Floor (You're so fat no one was laughing but the floor was cracking...)",
            "lit_floor_m_four.png");
    public static final TETile DIM_FIVE = new TETile('F', Color.black, Color.black,
            "Floor (Yo mama’s so stupid she got locked in "
                    + "Mattress World and slept on the floor...)", "lit_floor_m_five.png");
    public static final TETile DIM_LANTERN = new TETile('F', Color.black, Color.black,
            "Unlit Lantern (My soul after Gitlet (╥_╥)...)", "dim_lantern.png");
    public static final TETile LIT_LANTERN = new TETile('F', Color.black, Color.black,
            "Lit Lantern (My soul before starting Gitlet...)", "lit_lantern.png");
    public static final TETile FLAME = new TETile('F', Color.black, Color.black,
            "Devil's Flame (You just HAD to turn on that lantern...)", "flame.png");
    public static final TETile DEAD_LEE = new TETile('F', Color.black, Color.black,
            "Dead Lee Sin (You speedy if you read this...)", "dead_lee_sin.png");
}
