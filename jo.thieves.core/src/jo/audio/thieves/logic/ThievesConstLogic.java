package jo.audio.thieves.logic;

import jo.audio.thieves.data.gen.Intersection;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.slu.ThievesModelConst;
import jo.util.utils.MathUtils;

public class ThievesConstLogic
{
    public static final long CITY_SEED = 0;
    public static final int CITY_SIZE_INTERSECTIONS = 100;

    public static final String STREET_URI = "street://";
    public static final String INTERSECTION_URI = "intersection://";
    public static final String HOUSE_URI = "house://";
    public static final String INITIAL_LOCATION = INTERSECTION_URI+"INT1631478226";

    public static final String CAT_WAREHOUSE = "warehouse";
    public static final String CAT_DWARF = "dwarf";
    public static final String CAT_GUILD = "guild";
    
    public static final int EAST = 0; 
    public static final int SOUTHEAST = 1; 
    public static final int SOUTH = 2; 
    public static final int SOUTHWEST = 3; 
    public static final int WEST = 4; 
    public static final int NORTHWEST = 5; 
    public static final int NORTH = 6; 
    public static final int NORTHEAST = 7;
    public static final int UP = 8;
    public static final int DOWN = 9;
    
    public static final String[] CARDINAL_NAMES = {
            "WEST", 
            "NORTHWEST", 
            "NORTH", 
            "NORTHEAST",
            "EAST", 
            "SOUTHEAST", 
            "SOUTH", 
            "SOUTHWEST", 
            "UP", 
            "DOWN", 
    };
    public static final int[] ORTHOGONAL_DIRS = {
            EAST, 
            SOUTH, 
            WEST, 
            NORTH, 
            UP,
            DOWN,      
    };
    
    public static final int[][] ORTHOGONAL_DELTAS = {
            { 1, 0, 0 }, //  EAST 
            { 1, 1, 0 }, //  SOUTHEAST 
            { 0, 1, 0 }, //  SOUTH 
            {-1, 1, 0 }, //  SOUTHWEST 
            {-1, 0, 0 }, //  WEST 
            {-1,-1, 0 }, //  NORTHWEST 
            { 0,-1, 0 }, //  NORTH 
            { 1,-1, 0 }, //  NORTHEAST 
            { 0, 0, 1 }, //  UP 
            { 0, 0,-1 }, //  DOWN 
    };
    
    public static final int MAX_INTERSECTION_DESCRIPTIONS = 62;
    
    public static final String RACE_HUMAN = "human";
    public static final String RACE_DWARF = "dwarf";
    public static final String RACE_ELF = "elf";
    public static final String RACE_GNOME = "gnome";
    public static final String RACE_HALF_ELF = "half-elf";
    public static final String RACE_HALFLING = "halfling";
    public static final String RACE_HALF_ORC = "half-orc";
    
    public static final long GAME_TIME_COMPRESSION = 24; // game day = 1 hour real time
    public static final long ONE_DAY = 24*60*60; // in seconds
    
    public static final double bearingDouble(int x, int y)
    {
        double theta = Math.atan2(y, x);
        double interp = MathUtils.interpolate(theta, -Math.PI, Math.PI, 0, 8);
        return interp;
    }
    
    public static final int bearing(int x, int y)
    {
        double interp = bearingDouble(x, y);
        int cardinal = ((int)Math.floor(interp + .5))%8;
        return cardinal;
    }
    
    public static final int bearing(int x1, int y1, int x2, int y2)
    {
        return bearing(x2 - x1, y2 - y1);
    }
    
    public static final int bearing(Intersection i1, Intersection i2)
    {
        return bearing(i1.getX(), i1.getY(), i2.getX(), i2.getY());
    }
    
    public static final int bearing(Intersection i, Street s)
    {
        if (s.getHighIntersection() == i)
            return bearing(i, s.getLowIntersection());
        else
            return bearing(i, s.getHighIntersection());
    }
    
    public static final double bearingDouble(Intersection i1, Street s)
    {
        Intersection i2;
        if (s.getHighIntersection() == i1)
            i2 = s.getLowIntersection();
        else
            i2 = s.getHighIntersection();
        return bearingDouble(i2.getX() - i1.getX(), i2.getY() - i1.getY());
    }

    public static int opposite(int dir)
    {
        switch (dir)
        {
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            case EAST:
                return WEST;
            case WEST:
                return EAST;
            case NORTHWEST:
                return SOUTHEAST;
            case SOUTHEAST:
                return NORTHWEST;
            case NORTHEAST:
                return SOUTHWEST;
            case SOUTHWEST:
                return NORTHEAST;
            case UP:
                return DOWN;
            case DOWN:
                return UP;
        }
        throw new IllegalArgumentException("Unknown dir="+dir);
    }
    
    public static int left(int dir)
    {
        switch (dir)
        {
            case NORTH:
                return WEST;
            case SOUTH:
                return EAST;
            case EAST:
                return NORTH;
            case WEST:
                return SOUTH;
            case NORTHWEST:
                return SOUTHWEST;
            case SOUTHEAST:
                return NORTHEAST;
            case NORTHEAST:
                return NORTHWEST;
            case SOUTHWEST:
                return SOUTHEAST;
        }
        throw new IllegalArgumentException("Unknown dir="+dir);
    }
    
    public static int right(int dir)
    {
        return opposite(left(dir));
    }
    
    public static String dirToAudio(int dir)
    {
        switch (dir)
        {
            case NORTH:
                return ThievesModelConst.TEXT_NORTH;
            case SOUTH:
                return ThievesModelConst.TEXT_SOUTH;
            case EAST:
                return ThievesModelConst.TEXT_EAST;
            case WEST:
                return ThievesModelConst.TEXT_WEST;
            case NORTHWEST:
                return ThievesModelConst.TEXT_NORTHWEST;
            case SOUTHEAST:
                return ThievesModelConst.TEXT_SOUTHEAST;
            case NORTHEAST:
                return ThievesModelConst.TEXT_NORTHEAST;
            case SOUTHWEST:
                return ThievesModelConst.TEXT_WEST;            
            case UP:
                return ThievesModelConst.TEXT_UP;            
            case DOWN:
                return ThievesModelConst.TEXT_DOWN;            
        }
        throw new IllegalArgumentException("Unknown direction: "+dir);
    }
    
    public static final long gameTime(long ticks) // in seconds
    {
        long ms = ticks/GAME_TIME_COMPRESSION;
        long secs = ms/1000L;
        return secs;
    }
    
    public static final long gameTime() // in seconds
    {
        return gameTime(System.currentTimeMillis());
    }
    
    public static final int gameHour(long gameTimeInSeconds)
    {
        long gameTimeInHours = gameTimeInSeconds/60/60;
        return (int)(gameTimeInHours%24);
    }
    
    public static final int gameMinute(long gameTimeInSeconds)
    {
        long gameTimeInMinutes = gameTimeInSeconds/60;
        return (int)(gameTimeInMinutes%60);
    }
    
    public static boolean isDaytime(long gameTime)
    {
        int hour = gameHour(gameTime);
        return (hour >= 6) && (hour <= 18);
    }
    
    public static boolean isNightime(long gameTime)
    {
        return !isDaytime(gameTime);
    }
    
    public static final float ONE_MOON_DAY = 1493.333333333333333333f;
    public static final float ONE_MOON_HOUR = (ONE_MOON_DAY/24);
    public static final long MOON_CYCLE = 28*ONE_DAY; // in seconds 
    
    public static final int MOON_PHASE_NEW = 0;
    public static final int MOON_PHASE_CRESCENT_WAX = 1;
    public static final int MOON_PHASE_HALF_WAX = 2;
    public static final int MOON_PHASE_GIBBOUS_WAX = 3;
    public static final int MOON_PHASE_FULL = 4;
    public static final int MOON_PHASE_GIBBOUS_WANE = 5;
    public static final int MOON_PHASE_HALF_WANE = 6;
    public static final int MOON_PHASE_CRESCENT_WANE = 7;
    
    // 0 = new moon, .5 = full moon
    public static float getMoonCycle(long gameTime)
    {
        float c = (float)gameTime/(float)MOON_CYCLE;
        c -= Math.floor(c);
        return c;
    }
    
    public static int getMoonPhase(long gameTime)
    {
        float c = getMoonCycle(gameTime) + (1f/16f);
        int ph = (int)(c/(1f/8f));
        return ph%8;
    }

    public static int getHourOfMoon(long gameTime)
    {
        float c = (float)gameTime/ONE_MOON_DAY;
        c -= Math.floor(c);
        return (int)(c*24);
    }
    
    public static final void main(String[] argv)
    {
        System.out.println(CARDINAL_NAMES[bearing(1, 0)]+", expected EAST");
        System.out.println(CARDINAL_NAMES[bearing(1, 1)]+", expected SOUTHEAST");
        System.out.println(CARDINAL_NAMES[bearing(0, 1)]+", expected SOUTH");
        System.out.println(CARDINAL_NAMES[bearing(-1, 1)]+", expected SOUTHWEST");
        System.out.println(CARDINAL_NAMES[bearing(-1, 0)]+", expected WEST");
        System.out.println(CARDINAL_NAMES[bearing(-1, -1)]+", expected NORTHWEST");
        System.out.println(CARDINAL_NAMES[bearing(0, -1)]+", expected NORTH");
        System.out.println(CARDINAL_NAMES[bearing(1, -1)]+", expected NORTHEAST");
    }

    public static String dirToName(int dir)
    {
        return "{{DIRECTION_NAME#"+dir+"}}";
    }

    public static String poshToName(double posh)
    {
        String[] names = ThievesModelConst.getTexts("POSH_NAME");
        int idx = (int)Math.round(MathUtils.interpolate(posh, 0, 1, 0, names.length-1));
        return "{{POSH_NAME#"+idx+"}}";
    }
}
