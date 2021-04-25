package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

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
    private final TETile FLOOR_TILE = Tileset.BASIC_FLOOR_3;


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
    /** Lee's coordinates. */
    private int leeX;
    private int ogLeeX;
    private int leeY;
    private int ogLeeY;

    /** Creates Area. */
    public Area(int x, int y, int w, int l) {
        this.xPos = x;
        this.yPos = y;
        this.length = l;
        this.width = w;
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
    /** Puts rooms inside all our areas for BIGBOI*/
    private void makeRooms(TETile[][] world, Random gen) {
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
                x1 = RandomUtils.uniform(gen, this.xPos + 2, this.xPos + this.width - 2);
                x2 = RandomUtils.uniform(gen, this.xPos + 2, this.xPos + this.width - 2);
                y1 = RandomUtils.uniform(gen, this.yPos + 2, this.yPos + this.length - 2);
                y2 = RandomUtils.uniform(gen, this.yPos + 2, this.yPos + this.length - 2);
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
                    world[i][j] = FLOOR_TILE;
                }
            }
        }
    }
    private void connect4(TETile[][] world, Random gen) {
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
                    world[x][room1y] = FLOOR_TILE;
                }
                for (int y = room1y; y >= room2y; y--) {
                    world[room2x][y] = FLOOR_TILE;
                }
            } else {
                for (int y = room1y; y >= room2y; y--) {
                    world[room1x][y] = FLOOR_TILE;
                }
                for (int x = room1x; x <= room2x; x++) {
                    world[x][room2y] = FLOOR_TILE;
                }
            }
        } else {                // Right-Up or Up-Right
            if (el) {
                for (int x = room1x; x <= room2x; x++) {
                    world[x][room1y] = FLOOR_TILE;
                }
                for (int y = room1y; y <= room2y; y++) {
                    world[room2x][y] = FLOOR_TILE;
                }
            } else {
                for (int y = room1y; y <= room2y; y++) {
                    world[room1x][y] = FLOOR_TILE;
                }
                for (int x = room1x; x <= room2x; x++) {
                    world[x][room2y] = FLOOR_TILE;
                }
            }
        }
    }

    private boolean neighborsIsFloor(int x, int y, TETile[][] world) {
        boolean xOverZero = x - 1 >= 0;
        boolean yOverZero = y - 1 >= 0;
        boolean xBehindBorder = x + 1 < WORLD_WIDTH;
        boolean yBehindBorder = y + 1 < WORLD_LENGTH;
        boolean botLeft = xOverZero && yOverZero && world[x - 1][y - 1] == FLOOR_TILE;
        boolean botRight = xBehindBorder && yOverZero && world[x + 1][y - 1] == FLOOR_TILE;
        boolean topLeft = xOverZero && yBehindBorder && world[x - 1][y + 1] == FLOOR_TILE;
        boolean topRight = xBehindBorder && yBehindBorder && world[x + 1][y + 1] == FLOOR_TILE;
        return botLeft || botRight || topLeft || topRight;
    }

    public TETile[][] generateWorld(long seed, String commands) {
        Random gen = new Random(seed % Long.MAX_VALUE);

        // initialize tiles
        TETile[][] world = new TETile[WORLD_WIDTH][WORLD_LENGTH];
        for (int x = 0; x < WORLD_WIDTH; x ++) {
            for (int y = 0; y < WORLD_LENGTH; y ++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        makeAreas(world, gen);
        connect4(world, gen);
        int leeCount = 0;
        for (int x = 0; x < WORLD_WIDTH; x ++) {
            for (int y = 0; y < WORLD_LENGTH; y ++) {
                if (world[x][y] == Tileset.NOTHING && neighborsIsFloor(x, y, world)) {
                    world[x][y] = Tileset.BASIC_WALL;
                }
                if (leeCount == 0 && world[x][y] == FLOOR_TILE) {
                    // Maybe set some avatar object or variable to these coords
                    world[x][y] = Tileset.LEE_SIN;
                    leeCount++;
                    leeX = x;
                    ogLeeX = x;
                    leeY = y;
                    ogLeeY = y;
                }
            }
        }
        return world;
    }

    public TETile[][] moveLee(String commands, TETile[][] world) {
        if (commands.length() > 0) {
            world[leeX][leeY] = FLOOR_TILE;
        }
        leeX = ogLeeX;
        leeY = ogLeeY;
        for (int i = 0; i < commands.length(); i++) {
            char move = commands.toLowerCase().charAt(i);
            switch(move) {
                case 'w':
                    if (world[leeX][leeY + 1] != Tileset.BASIC_WALL) {
                        this.leeY++;
                    }
                    break;
                case 'a':
                    if (world[leeX - 1][leeY] != Tileset.BASIC_WALL) {
                        this.leeX--;
                    }
                    break;
                case 's':
                    if (world[leeX][leeY - 1] != Tileset.BASIC_WALL) {
                        this.leeY--;
                    }
                    break;
                case 'd':
                    if (world[leeX + 1][leeY] != Tileset.BASIC_WALL) {
                        this.leeX++;
                    }
                    break;
            }
        }
        world[leeX][leeY] = Tileset.LEE_SIN;
        return world;
    }
}
