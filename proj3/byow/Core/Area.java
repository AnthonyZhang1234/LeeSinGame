package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.Random;
import java.util.*;

/**
 * @source BSP Algorithm
 * Generates Areas via a variant of the BSP algorithm, and randomly creates rectangular
 * rooms via random 2 points selected to act as corners of the rooms. Then connects
 * the rooms through hallways that connect the bottom left corner of one room to the
 * top right corner of another room.
 */
public class Area {

    private final int WORLD_WIDTH = Engine.WIDTH;
    private final int WORLD_LENGTH = Engine.HEIGHT - 1;
    private final int minSideLength = Math.min(WORLD_LENGTH, WORLD_WIDTH) / 3;
    private final int maxSideLength = Math.min(WORLD_LENGTH, WORLD_WIDTH) / 2;
    private final ArrayList<Area> ALL_MIGHTY = new ArrayList<Area>();
    private final TETile FLOOR_TILE = Tileset.DIM_FIVE;
    private final TETile CURSED_TILE = Tileset.FLAME;


    /** Indicates the length of the area. */
    private int length;
    /** Indicates the width of the area. */
    private int width;
    /** Indicates bottom left x-coordinate of the Area. */
    private int xPos;
    /** Indicates bottom left y-coordinate of the Area. */
    private int yPos;
    /** First Child. */
    private Area child1;
    /** Second Child. */
    private Area child2;
    /** Room coordinates. */
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    /** Lee's coordinates. */
    private int leeX;
    private int ogLeeX;
    private int leeY;
    private int ogLeeY;
    private TETile leeTile;
    /** Set holding solid tiles. */
    private HashSet<TETile> solidTiles;
    /** Array of floor tiles. */
    private ArrayList<TETile> floors = new ArrayList<>();
    /** Rooms. */
    private ArrayList<Area> allTiny;
    /** Lantern coordinates. */
    private int lanternX;
    private int lanternY;
    /** Devil's lantern. */
    private boolean devil;
    /** Devil's curse. */
    private boolean cursed;

    /** Creates Area. */
    public Area(int x, int y, int w, int l) {
        this.xPos = x;
        this.yPos = y;
        this.length = l;
        this.width = w;
        this.solidTiles = new HashSet<>();
        this.allTiny = new ArrayList<>();
        this.devil = false;
        this.cursed = false;
        solidTiles.add(Tileset.BASIC_WALL);
        solidTiles.add(Tileset.DIM_LANTERN);
        solidTiles.add(Tileset.LIT_LANTERN);
        floors.add(Tileset.LIT_ONE);
        floors.add(Tileset.BASE);
        floors.add(Tileset.DIM_ONE);
        floors.add(Tileset.DIM_TWO);
        floors.add(Tileset.DIM_THREE);
        floors.add(Tileset.DIM_FOUR);
        floors.add(Tileset.DIM_FIVE);
    }

    /** Splits the area into two, assigns child1 & child2 to the split areas. */
    private boolean chopper(Random gen) {
        if (this.child1 != null && this.child2 != null) {
            return false;
        }
        //if width is 35% larger than height, guillotine the area
        //if length is 35% larger than width, give the area a tight belt
        //if the area square asf, do nothin lul
        boolean chopHorizontal = RandomUtils.uniform(gen) > 0.5;
        if (this.width > this.length && (double) this.width / this.length >= 1.35) {
            chopHorizontal = false;
        } else if (this.length > this.width && (double) this.length / this.width >= 1.35) {
            chopHorizontal = true;
        }
        // Assign max leaf area size
        int max;
        if (chopHorizontal) {
            max = this.length - minSideLength;
        } else {
            max = this.width - minSideLength;
        }
        if (max <= minSideLength) {
            return false;
        }
        // Assign splitPt
        int splitPt = RandomUtils.uniform(gen, minSideLength, max);
        // Make the kids
        if (chopHorizontal) {
            this.child1 = new Area(this.xPos, this.yPos, this.width, splitPt);
            this.child2 = new Area(this.xPos, this.yPos + splitPt,
                    this.width, this.length - splitPt);
        } else {
            this.child1 = new Area(this.xPos, this.yPos, splitPt, this.length);
            this.child2 = new Area(this.xPos + splitPt, this.yPos,
                    this.width - splitPt, this.length);
        }
        return true;
    }

    private void makeAreas(TETile[][] world, Random gen) {
        // Create a queue of our Areas, and a starting Area
        Area bigBoy = new Area(0, 0, WORLD_WIDTH, WORLD_LENGTH);
        ALL_MIGHTY.add(bigBoy);
        boolean hadBabies = true;
        // Loop through areas, splitting them if they're too big or via random chance
        while (hadBabies) {
            hadBabies = false;
            LinkedList<Area> temp = new LinkedList<Area>();
            for (Area yeet : ALL_MIGHTY) {
                if (yeet.child1 == null && yeet.child2 == null) {
                    if (yeet.width >= maxSideLength || yeet.length >= maxSideLength
                            || RandomUtils.uniform(gen) > .5) {
                        if (yeet.chopper(gen)) {
                            temp.add(yeet.child1);
                            temp.add(yeet.child2);
                            hadBabies = true;
                        }
                    }
                }
            }
            ALL_MIGHTY.addAll(temp);
        }
        bigBoy.makeRooms(world, gen);
    }

    /** Puts rooms inside all our areas for BIGBOI; also puts one lantern which lights the room */
    private void makeRooms(TETile[][] world, Random gen) {
        //fill the inside with floor
        if (this.child1 != null || this.child2 != null) {
            if (this.child1 != null) {
                this.child1.makeRooms(world, gen);
            }
            if (this.child2 != null) {
                this.child2.makeRooms(world, gen);
            }
        } else {
            int tempX1 = 0;
            int tempY1 = 0;
            int tempX2 = 0;
            int tempY2 = 0;
            boolean notFarEnuf = true;
            while (notFarEnuf) {
                tempX1 = RandomUtils.uniform(gen, this.xPos + 2, this.xPos + this.width - 2);
                tempX2 = RandomUtils.uniform(gen, this.xPos + 2, this.xPos + this.width - 2);
                tempY1 = RandomUtils.uniform(gen, this.yPos + 2, this.yPos + this.length - 2);
                tempY2 = RandomUtils.uniform(gen, this.yPos + 2, this.yPos + this.length - 2);
                if (tempX1 > 0 && tempX2 > 0 && tempX1 < WORLD_WIDTH - 1
                        && tempX2 < WORLD_WIDTH - 1 && tempY1 > 0 && tempY2 > 0
                        && tempY1 < WORLD_LENGTH - 1 && tempY2 < WORLD_LENGTH - 1) {
                    notFarEnuf = Math.abs(tempX1 - tempX2) < 2 || Math.abs(tempY1 - tempY2) < 2;
                }
            }
            this.x1 = Math.min(tempX1, tempX2);
            this.x2 = this.x1 + Math.abs(tempX1 - tempX2);
            this.y1 = Math.min(tempY1, tempY2);
            this.y2 = this.y1 + Math.abs(tempY1 - tempY2);
            // Create lantern in random (not (x1, y1) || (x2, y2))
            this.lanternX = RandomUtils.uniform(gen, this.x1 + 1, this.x2);
            this.lanternY = RandomUtils.uniform(gen, this.y1 + 1, this.y2);
            for (int i = this.x1; i <= this.x2; i++) {
                for (int j = this.y1; j <= this.y2; j++) {
                    // if tile is # distance away from lantern, make it # tile
                    world[i][j] = FLOOR_TILE;
                }
            }
            world[this.lanternX][this.lanternY] = Tileset.DIM_LANTERN;
        }
    }

    private void lightRoomOfLantern(TETile[][] world, int roomX1, int roomX2,
                                    int roomY1, int roomY2, int lX, int lY) {
        for (int i = roomX1; i <= roomX2; i++) {
            for (int j = roomY1; j <= roomY2; j++) {
                if (!(i == lX && j == lY) && world[i][j] != CURSED_TILE) {
                    int distance = absoluteDist(i, j, lX, lY);
                    world[i][j] = floors.get(Math.min(distance - 1, 6));
                    if (world[i][j] == leeTile) {
                        leeTile = floors.get(Math.min(distance - 1, 6));
                    }
                }
            }
        }
    }

    /** makes all tiles in the room except the lantern now the darkest tile/shadow */
    private void darkenRoomOfLantern(TETile[][] world, int roomX1, int roomX2,
                                    int roomY1, int roomY2, int lX, int lY) {
        for (int i = roomX1; i <= roomX2; i++) {
            for (int j = roomY1; j <= roomY2; j++) {
                if (!(i == lX && j == lY) && world[i][j] != CURSED_TILE) {
                    world[i][j] = FLOOR_TILE;
                    if (world[i][j] == leeTile) {
                        leeTile = FLOOR_TILE;
                    }
                }
            }
        }
    }

    private int absoluteDist(int x, int y, int i, int j) {
        return Math.max(Math.abs(x - i), Math.abs(y - j));
    }

    private void connect4(TETile[][] world, Random gen) {
        for (Area rose : ALL_MIGHTY) {
            if (rose.child1 == null || rose.child2 == null) {
                allTiny.add(rose);
            }
        }
        for (int i = 0; i < allTiny.size() - 1; i++) {
            hallwayHelper(allTiny.get(i), allTiny.get(i + 1), world, gen);
        }
    }

    private void makeHallway(int x, int y, TETile[][] world) {
        if (world[x][y] == Tileset.NOTHING) {
            world[x][y] = FLOOR_TILE;
        }
    }

    private void hallwayHelper(Area a1, Area a2, TETile[][] world, Random gen) {
        boolean el = RandomUtils.uniform(gen) > 0.5;
        int room1x;
        int room2x;
        int room1y;
        int room2y;
        if (a1.x1 < a2.x2) {
            room1x = a1.x1;
            room2x = a2.x2;
            room1y = a1.y1;
            room2y = a2.y2;
        } else {
            room1x = a2.x2;
            room1y = a2.y2;
            room2x = a1.x1;
            room2y = a1.y1;
        }
        if (room1y > room2y) {  // Right-Down or Down-Right
            if (el) {
                for (int x = room1x; x <= room2x; x++) {
                    makeHallway(x, room1y, world);
                }
                for (int y = room1y; y >= room2y; y--) {
                    makeHallway(room2x, y, world);
                }
            } else {
                for (int y = room1y; y >= room2y; y--) {
                    makeHallway(room1x, y, world);
                }
                for (int x = room1x; x <= room2x; x++) {
                    makeHallway(x, room2y, world);
                }
            }
        } else {                // Right-Up or Up-Right
            if (el) {
                for (int x = room1x; x <= room2x; x++) {
                    makeHallway(x, room1y, world);
                }
                for (int y = room1y; y <= room2y; y++) {
                    makeHallway(room2x, y, world);
                }
            } else {
                for (int y = room1y; y <= room2y; y++) {
                    makeHallway(room1x, y, world);
                }
                for (int x = room1x; x <= room2x; x++) {
                    makeHallway(x, room2y, world);
                }
            }
        }
    }

    private boolean neighborsIsFloor(int x, int y, TETile[][] world) {
        boolean xOverZero = x - 1 >= 0;
        boolean yOverZero = y - 1 >= 0;
        boolean xBehindBorder = x + 1 < WORLD_WIDTH;
        boolean yBehindBorder = y + 1 < WORLD_LENGTH;
        boolean botLeft = xOverZero && yOverZero && floors.contains(world[x - 1][y - 1]);
        boolean botRight = xBehindBorder && yOverZero && floors.contains(world[x + 1][y - 1]);
        boolean topLeft = xOverZero && yBehindBorder && floors.contains(world[x - 1][y + 1]);
        boolean topRight = xBehindBorder && yBehindBorder && floors.contains(world[x + 1][y + 1]);
        return botLeft || botRight || topLeft || topRight;
    }

    public TETile[][] generateWorld(long seed) {
        Random gen = new Random(seed);

        // initialize tiles
        TETile[][] world = new TETile[WORLD_WIDTH][WORLD_LENGTH];
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_LENGTH; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        makeAreas(world, gen);
        connect4(world, gen);
        int leeCount = 0;
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_LENGTH; y++) {
                if (world[x][y] == Tileset.NOTHING && neighborsIsFloor(x, y, world)) {
                    world[x][y] = Tileset.BASIC_WALL;
                }
                //creates Lee Sin
                if (leeCount == 0 && floors.contains(world[x][y])) {
                    this.leeTile = world[x][y]; // Saves tile where we want Lee Sin
                    world[x][y] = Tileset.LEE_SIN; // Puts Lee Sin on tile
                    leeCount++;
                    leeX = x;
                    ogLeeX = x;
                    leeY = y;
                    ogLeeY = y;
                }
            }
        }
        // Choose a lantern to be the devil's lantern
        int devilInt = RandomUtils.uniform(gen, allTiny.size());
        allTiny.get(devilInt).devil = true;
        return world;
    }

    private void changeLantern(TETile[][] world, boolean turnOff) {
        TETile currentLantern = Tileset.DIM_LANTERN;
        TETile newLantern = Tileset.LIT_LANTERN;
        if (turnOff) {
            currentLantern = Tileset.LIT_LANTERN;
            newLantern = Tileset.DIM_LANTERN;
        }
        int lanX = leeX;
        int lanY = leeY;
        // If try to turn off unlit lantern or try to turn on lit lantern, coords off
        if (world[leeX + 1][leeY] == currentLantern) {
            world[leeX + 1][leeY] = newLantern;
            lanX++;
        } else if (world[leeX - 1][leeY] == currentLantern) {
            world[leeX - 1][leeY] = newLantern;
            lanX--;
        } else if (world[leeX][leeY + 1] == currentLantern) {
            world[leeX][leeY + 1] = newLantern;
            lanY++;
        } else if (world[leeX][leeY - 1] == currentLantern) {
            world[leeX][leeY - 1] = newLantern;
            lanY--;
        }
        if (lanX == leeX && lanY == leeY) {
            return;
        }
        int roomX1 = 0;
        int roomY1 = 0;
        int roomX2 = 0;
        int roomY2 = 0;
        for (Area dab : allTiny) {
            roomX1 = dab.x1;
            roomX2 = dab.x2;
            roomY1 = dab.y1;
            roomY2 = dab.y2;
            if (lanX == dab.lanternX  && lanY == dab.lanternY) {
                if (dab.devil) {
                    this.cursed = true;
                }
                break;
            }
        }
        if (turnOff) {
            darkenRoomOfLantern(world, roomX1, roomX2, roomY1, roomY2, lanX, lanY);
        } else {
            lightRoomOfLantern(world, roomX1, roomX2, roomY1, roomY2, lanX, lanY);
        }
    }

    public void gameOver(TETile[][] world, int x, int y, TERenderer ter) {
        if (world[leeX][leeY] == CURSED_TILE) {
            world[x][y] = Tileset.DEAD_LEE;
            ter.renderFrame(world);
            StdDraw.pause(1000);
            Font font = new Font("Monaco", Font.BOLD, 30);
            StdDraw.setFont(font);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.clear(Color.BLACK);
            StdDraw.text(WORLD_WIDTH / 2, WORLD_LENGTH / 2, "Game Over!");
            StdDraw.show();
            StdDraw.pause(5000);
            System.exit(0);
        }
    }

    public TETile[][] moveLee(String commands, TETile[][] world, TERenderer ter) {
        // ISSUE: When loading, this command is run once, so cursed tiles aren't reinstated
        if (commands.length() > 0) {
            if (this.cursed) {
                world[leeX][leeY] = CURSED_TILE;
            } else {
                world[leeX][leeY] = this.leeTile;
            }
        }
        int lastLeeX = leeX;
        int lastLeeY = leeY;
        leeX = ogLeeX;
        leeY = ogLeeY;
        // Attempted already: Put curse check in this loop, it breaks normal gameplay
        // Did above and alse set this.curse = false at the end of the loop, nothing happens
        for (int i = 0; i < commands.length(); i++) {
            char move = commands.toLowerCase().charAt(i);
            switch (move) {
                case 'w':
                    if (!this.solidTiles.contains(world[leeX][leeY + 1])) {
                        this.leeY++;
                    }
                    break;
                case 'a':
                    if (!this.solidTiles.contains(world[leeX - 1][leeY])) {
                        this.leeX--;
                    }
                    break;
                case 's':
                    if (!this.solidTiles.contains(world[leeX][leeY - 1])) {
                        this.leeY--;
                    }
                    break;
                case 'd':
                    if (!this.solidTiles.contains(world[leeX + 1][leeY])) {
                        this.leeX++;
                    }
                    break;
                case 'p':
                    changeLantern(world, false);
                    break;
                case 'o':
                    changeLantern(world, true);
                    break;
                default:
                    break;
            }
        }
        if (commands.length() > 0) {
            leeTile = world[leeX][leeY];
        }
        if (!(lastLeeX == leeX && lastLeeY == leeY)) {
            gameOver(world, leeX, leeY, ter);
        }
        world[leeX][leeY] = Tileset.LEE_SIN;
        return world;
    }
}
