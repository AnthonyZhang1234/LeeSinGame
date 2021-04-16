package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;
import java.util.*;

public class Area {

    private static final int WORLD_WIDTH = Engine.WIDTH;
    private static final int WORLD_LENGTH = Engine.HEIGHT;
    private static final int minSideLength = Math.min(WORLD_LENGTH, WORLD_WIDTH) / 4;
    private static final int maxSideLength = Math.min(WORLD_LENGTH, WORLD_WIDTH) / 2;
    private static final ArrayList<Area> ALL_MIGHTY = new ArrayList<Area>();


    /** Indicates the length of the area. */
    private int length;
    /** Indicates the width of the area. */
    private int width;
    /** Indicates bottom left x-coordinate. */
    private int xPos;
    /** Indicates bottom left y-coordinate. */
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


    /** Creates Area. */
    public Area(int x, int y, int w, int l) {
        this.xPos = x;
        this.yPos = y;
        this.length = l;
        this.width = w;
    }

    /** Splits the area into two, assigns child1 & child2 to the split areas. */
    public boolean chopper(Random gen) {
        if (this.child1 != null && this.child2 != null) {
            return false;
        }
        //if width is 35% larger than height, guillotine the area
        //if length is 35% larger than width, give the area a tight belt
        //if the area square asf, do nothin lul
        boolean chopHorizontal = RandomUtils.uniform(gen) > 0.5;
        if (this.width > this.length && (double)this.width / this.length >= 1.35) {
            chopHorizontal = false;
        } else if (this.length > this.width && (double)this.length / this.width >= 1.35) {
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

    public static void makeAreas(TETile[][] world, Random gen) {
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
    /** Puts rooms inside all our areas for BIGBOI*/
    public void makeRooms(TETile[][] world, Random gen) {
        //fill the inside with floor
        if (this.child1 != null || this.child2 != null) {
            if (this.child1 != null) {
                this.child1.makeRooms(world, gen);
            } if (this.child2 != null) {
                this.child2.makeRooms(world, gen);
            }
        } else {
            int x1 = 0;
            int y1 = 0;
            int x2 = 0;
            int y2 = 0;
            boolean notFarEnuf = true;
            while(notFarEnuf) {
                x1 = RandomUtils.uniform(gen, this.xPos, this.xPos + this.width);
                x2 = RandomUtils.uniform(gen, this.xPos, this.xPos + this.width);
                y1 = RandomUtils.uniform(gen, this.yPos, this.yPos + this.length);
                y2 = RandomUtils.uniform(gen, this.yPos, this.yPos + this.length);
                if (x1 > 0 && x2 > 0 && x1 < WORLD_WIDTH - 1 && x2 < WORLD_WIDTH - 1
                        && y1 > 0 && y2 > 0 && y1 < WORLD_LENGTH - 1 && y2 < WORLD_LENGTH - 1) {
                    notFarEnuf = Math.abs(x1 - x2) < 2 || Math.abs(y1 - y2) < 2;
                }
            }
            this.x1 = Math.min(x1, x2);
            this.x2 = this.x1 + Math.abs(x1 - x2);
            this.y1 = Math.min(y1, y2);
            this.y2 = this.y1 + Math.abs(y1 - y2);
            for(int i = this.x1; i <= this.x2; i++) {
                for (int j = this.y1; j <= this.y2; j++) {
                    world[i][j] = Tileset.FLOOR;
                }
            }
        }
    }
    public static void connect4(TETile[][] world, Random gen) {
        ArrayList<Area> allTiny = new ArrayList<Area>();
        for (Area rose : ALL_MIGHTY) {
            if (rose.child1 == null || rose.child2 == null) {
                allTiny.add(rose);
            }
        }
        for (int i = 0; i < allTiny.size() - 1; i++) {
            hallwayHelper(allTiny.get(i), allTiny.get(i + 1), world, gen);
        }
    }

    public static void hallwayHelper(Area a1, Area a2, TETile[][] world, Random gen) {
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
                    world[x][room1y] = Tileset.FLOOR;
                }
                for (int y = room1y; y >= room2y; y--) {
                    world[room2x][y] = Tileset.FLOOR;
                }
            } else {
                for (int y = room1y; y >= room2y; y--) {
                    world[room1x][y] = Tileset.FLOOR;
                }
                for (int x = room1x; x <= room2x; x++) {
                    world[x][room2y] = Tileset.FLOOR;
                }
            }
        } else {                // Right-Up or Up-Right
            if (el) {
                for (int x = room1x; x <= room2x; x++) {
                    world[x][room1y] = Tileset.FLOOR;
                }
                for (int y = room1y; y <= room2y; y++) {
                    world[room2x][y] = Tileset.FLOOR;
                }
            } else {
                for (int y = room1y; y <= room2y; y++) {
                    world[room1x][y] = Tileset.FLOOR;
                }
                for (int x = room1x; x <= room2x; x++) {
                    world[x][room2y] = Tileset.FLOOR;
                }
            }
        }
    }

    public static boolean neighborsIsFloor(int x, int y, TETile[][] world) {
        boolean xOverZero = x - 1 >= 0;
        boolean yOverZero = y - 1 >= 0;
        boolean xBehindBorder = x + 1 < WORLD_WIDTH;
        boolean yBehindBorder = y + 1 < WORLD_LENGTH;
        boolean botLeft = xOverZero && yOverZero && world[x - 1][y - 1] == Tileset.FLOOR;
        boolean botRight = xBehindBorder && yOverZero && world[x + 1][y - 1] == Tileset.FLOOR;
        boolean topLeft = xOverZero && yBehindBorder && world[x - 1][y + 1] == Tileset.FLOOR;
        boolean topRight = xBehindBorder && yBehindBorder && world[x + 1][y + 1] == Tileset.FLOOR;
        return botLeft || botRight || topLeft || topRight;
    }

    public static TETile[][] generateWorld(long seed, TERenderer ter) {
        // long SEED = 95953;
        final Random gen = new Random(seed & Long.MAX_VALUE);
        // TERenderer ter = new TERenderer();
        // ter.initialize(WORLD_WIDTH, WORLD_LENGTH);

        // initialize tiles
        TETile[][] world = new TETile[WORLD_WIDTH][WORLD_LENGTH];
        for (int x = 0; x < WORLD_WIDTH; x ++) {
            for (int y = 0; y < WORLD_LENGTH; y ++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        makeAreas(world, gen);
        connect4(world, gen);
        for (int x = 0; x < WORLD_WIDTH; x ++) {
            for (int y = 0; y < WORLD_LENGTH; y ++) {
                if (world[x][y] == Tileset.NOTHING && neighborsIsFloor(x, y, world)) {
                    world[x][y] = Tileset.WALL;
                }
            }
        }
        // ter.renderFrame(world);
        return world;
    }
}
