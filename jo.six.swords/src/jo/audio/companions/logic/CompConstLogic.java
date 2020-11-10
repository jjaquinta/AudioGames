package jo.audio.companions.logic;

import java.util.Random;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CoordBean;

public class CompConstLogic
{
    public static final String PREMIUM_SUBSCRIPTION = "PREMIUM_SUBSCRIPTION";
    public static final String PREMIUM_SUBSCRIPTION_ID1 = "amzn1.adg.product.e77c49c6-7140-48d4-8b97-dd358da0b8bb";
    public static final String PREMIUM_SUBSCRIPTION_ID2 = "amzn1.adg.product.e13a8b4b-c0fb-4de1-837d-8c8d91895696";
    
    public static final int SQUARES_PER_REGION = 64;
    public static final int REGIONS_PER_DOMAIN = 4;
    public static final int SQUARES_PER_DOMAIN = SQUARES_PER_REGION*REGIONS_PER_DOMAIN;

    public static final int DIM_FREEPLAY = 0;
    public static final int DIM_CIRRANE = 1;
    public static final int DIM_ENUMA = 2;
    public static final int DIM_IRELAND = 3;
    public static final int DIM_PLAINS = 4;
    public static final int DIM_HILLS = 5;
    public static final int DIM_MOUNTAINS = 6;
    public static final int DIM_ARTIC = 7;
    public static final int DIM_FOREST = 8;
    public static final int DIM_DESERT = 9;
    public static final int DIM_JUNGLE = 10;
    public static final int DIM_SWAMP = 11;
    public static final int DIM_FRESHWATER = 12;
    public static final int DIM_SALTWATER = 13;
    public static final int DIM_ICELAND = 14;
    
    public static final int INITIAL_COMPANIONS = 4;
    public static final int MAX_COMPANIONS = 6;
    public static final int INITIAL_LOCATION_X = 93;
    public static final int INITIAL_LOCATION_Y = 174;
    public static final String INITIAL_LOCATION = "("+INITIAL_LOCATION_X+","+INITIAL_LOCATION_Y+")";
    public static final int INITIAL_ENUMA_LOCATION_X = 751;
    public static final int INITIAL_ENUMA_LOCATION_Y = 927;
    public static final int MAX_ENUMA_LOCATION_X = 2407;
    public static final int MAX_ENUMA_LOCATION_Y = 1605;
    public static final String INITIAL_ENUMA_LOCATION = "("+INITIAL_ENUMA_LOCATION_X+","+INITIAL_ENUMA_LOCATION_Y+","+DIM_ENUMA+")";
    public static final int INITIAL_CIRRANE_LOCATION_X = 96;
    public static final int INITIAL_CIRRANE_LOCATION_Y = 172;
    public static final String INITIAL_LOCATION_CIRRANE = "("+INITIAL_CIRRANE_LOCATION_X+","+INITIAL_CIRRANE_LOCATION_Y+","+DIM_CIRRANE+")";
    public static final int INITIAL_IRL_LOCATION_X = 272;
    public static final int INITIAL_IRL_LOCATION_Y = 293;
    public static final String INITIAL_LOCATION_IRL = "("+INITIAL_IRL_LOCATION_X+","+INITIAL_IRL_LOCATION_Y+","+DIM_IRELAND+")";
    public static final int INITIAL_ICE_LOCATION_X = 208;
    public static final int INITIAL_ICE_LOCATION_Y = 291;
    public static final String INITIAL_LOCATION_ICE = "("+INITIAL_ICE_LOCATION_X+","+INITIAL_ICE_LOCATION_Y+","+DIM_ICELAND+")";
    public static final String[][] INITIAL_ITEMS = {
            { "leather_armor", "short_sword", "small_shield", "sling_stone:20", "sling", },
            { "padded_armor", "broad_sword", "small_shield", "sling_stone:20", "sling", },
            { "padded_armor", "dagger", "arrow:20", "short_bow", },
            { "ring_armor", "hand_axe:5", "small_shield", },
    };
    public static final int[] INITIAL_DIM_LOCATION_X = new int[] { INITIAL_LOCATION_X, INITIAL_CIRRANE_LOCATION_X, INITIAL_ENUMA_LOCATION_X, 
            INITIAL_IRL_LOCATION_X, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            INITIAL_ICE_LOCATION_X };
    public static final int[] INITIAL_DIM_LOCATION_Y = new int[] { INITIAL_LOCATION_Y, INITIAL_CIRRANE_LOCATION_Y, INITIAL_ENUMA_LOCATION_Y,
            INITIAL_IRL_LOCATION_Y, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            INITIAL_ICE_LOCATION_Y };
    
    public static final int CLOUD_CLOUDY = 0;
    public static final int CLOUD_MOSTLY_CLOUDY = 1;
    public static final int CLOUD_PARTLY_CLOUDY = 2;
    public static final int CLOUD_PARTLY_SUNNY = 3;
    public static final int CLOUD_MOSTLY_SUNNY = 4;
    public static final int CLOUD_SUNNY = 5;
    
    public static final int PRECIPITATION_NONE = 0;
    public static final int PRECIPITATION_DRIZZLE = 1;
    public static final int PRECIPITATION_LIGHT = 2;
    public static final int PRECIPITATION_SHOWERS = 3;
    public static final int PRECIPITATION_HEAVY = 4;
    public static final int PRECIPITATION_TORRENTS = 0;
    
    public static final int RACE_MIXED = 0;
    public static final int RACE_HUMAN = 1;
    public static final int RACE_ELF = 2;
    public static final int RACE_DWARF = 3;
    public static final int RACE_HALFLING = 4;
    public static final int RACE_GNOME = 5;
    public static final int RACE_HALFORC = 6;
    public static final int RACE_HALFELF = 7;
    
    public static final String[] RACE_NAMES = {
            "Mixed",
            "Human",
            "Elf",
            "Dwarf",
    };
    
    public static final int GOVERNMENT_ANARCHY = 0;
    public static final int GOVERNMENT_COUNTY = 1;
    public static final int GOVERNMENT_DUCHY = 2;
    public static final int GOVERNMENT_KINGDOM = 3;
    public static final int GOVERNMENT_EMPIRE = 4;

    public static final String[] GOVERNMENT_NAMES = {
            "Anarchy",
            "County",
            "Duchy",
            "Kingdom",
            "Empire",
    };
    
    public static final int TERRAIN_PLAINS = 0;
    public static final int TERRAIN_HILLS = 1;
    public static final int TERRAIN_MOUNTAINS = 2;
    public static final int TERRAIN_ARTIC = 3;
    public static final int TERRAIN_FOREST = 4;
    public static final int TERRAIN_DESERT = 5;
    public static final int TERRAIN_JUNGLE = 6;
    public static final int TERRAIN_SWAMP = 7;
    public static final int TERRAIN_FRESHWATER = 8;
    public static final int TERRAIN_SALTWATER = 9;

    public static final String[] TERRAIN_NAMES = {
            "Plains",
            "Hills",
            "Mountains",
            "Artic",
            "Forest",
            "Desert",
            "Jungle",
            "Swamp",
            "Freshwater",
            "Saltwater",
    };

    public static final int FEATURE_NONE = 0;
    public static final int FEATURE_HAMLET = 1;
    public static final int FEATURE_VILLAGE = 2;
    public static final int FEATURE_TOWN = 3;
    public static final int FEATURE_CITY = 4;
    public static final int FEATURE_RUIN = 5;
    public static final int FEATURE_OUTPOST = 6;
    public static final int FEATURE_FORT = 7;
    public static final int FEATURE_CASTLE = 8;
    public static final int FEATURE_STATIC = 9;
    public static final int FEATURE_DOCK = 10;
    public static final int FEATURE_ARCH = 11;
    public static final int FEATURE_VORTEX = 12;
    public static final int FEATURE_DUNGEON = 13;
    
    public static final String[] FEATURE_NAMES = {
            "None",
            "Hamlet",
            "Village",
            "Town",
            "City",
            "Ruin",
            "Outpost",
            "Fort",
            "Castle",
            "Special",
            "Dock",
            "Arch",
            "Vortex",
            "Dungeon"
    };
    
    public static final int ONE_MINUTE = 1;
    public static final int ONE_HOUR = ONE_MINUTE*60;
    public static final int ONE_DAY = ONE_HOUR*24;
    
    public static int getHourOfDay(int time)
    {
        return (time%ONE_DAY)/ONE_HOUR;
    }
    
    public static int getMinuteOfHour(int time)
    {
        return time%ONE_HOUR;
    }
    
    public static int getMinuteOfDay(int time)
    {
        return time%ONE_DAY;
    }
    
    public static boolean isDaytime(int time)
    {
        int hour = getHourOfDay(time);
        return (hour >= 6) && (hour <= 18);
    }
    
    public static boolean isNightime(int time)
    {
        return !isDaytime(time);
    }
    
    public static final float ONE_MOON_DAY = 1493.333333333333333333f;
    public static final float ONE_MOON_HOUR = (ONE_MOON_DAY/24);
    public static final int MOON_CYCLE = 28*ONE_DAY; 
    
    public static final int MOON_PHASE_NEW = 0;
    public static final int MOON_PHASE_CRESCENT_WAX = 1;
    public static final int MOON_PHASE_HALF_WAX = 2;
    public static final int MOON_PHASE_GIBBOUS_WAX = 3;
    public static final int MOON_PHASE_FULL = 4;
    public static final int MOON_PHASE_GIBBOUS_WANE = 5;
    public static final int MOON_PHASE_HALF_WANE = 6;
    public static final int MOON_PHASE_CRESCENT_WANE = 7;
    
    // 0 = new moon, .5 = full moon
    public static float getMoonCycle(int time)
    {
        float c = (float)time/(float)MOON_CYCLE;
        c -= Math.floor(c);
        return c;
    }
    
    public static int getMoonPhase(int time)
    {
        float c = getMoonCycle(time) + (1f/16f);
        int ph = (int)(c/(1f/8f));
        return ph%8;
    }

    public static int getHourOfMoon(int time)
    {
        float c = (float)time/ONE_MOON_DAY;
        c -= Math.floor(c);
        return (int)(c*24);
    }
    
    
    public static final int TIME_COMBAT = ONE_MINUTE;
    public static final int TIME_TACTICAL = 10*ONE_MINUTE;
    public static final int TIME_STRATEGIC = ONE_HOUR;
    
    public static final float DISTANCE_STRATEGIC = 1f;
    public static final float DISTANCE_TACTICAL = 0.01f;

    public static final float[] ROAD_DIVISOR = new float[] { 1, 1.5f, 2, 4, 1 };
    
    public static boolean isWater(int terrain)
    {
        return (terrain == TERRAIN_FRESHWATER) || (terrain == TERRAIN_SALTWATER);
    }
    
    public static boolean isTown(int feature)
    {
        return (feature >= FEATURE_HAMLET) && (feature <= FEATURE_CITY);
    }
    
    public static boolean isRuin(int feature)
    {
        return (feature == FEATURE_RUIN) || (feature == FEATURE_DUNGEON);
    }
    
    public static boolean isCastle(int feature)
    {
        return (feature >= FEATURE_OUTPOST) && (feature <= FEATURE_CASTLE);
    }

    public static final int FEATURE_SUB_DEN = 1;
    public static final int FEATURE_SUB_MINE = 2;
    public static final int FEATURE_SUB_RUIN = 3;
    public static final int FEATURE_SUB_TEMPLE = 4;
    public static final int FEATURE_SUB_DUNGEON = 5;
    public static final int FEATURE_SUB_DEMON = 6;
    public static final int FEATURE_SUB_DEVIL = 7;
    public static final int FEATURE_SUB_DINO = 8;
    public static final int FEATURE_SUB_GROVE = 9;
    public static final int FEATURE_SUB_GIANT = 10;
    public static final int FEATURE_SUB_DRAGON = 11;
    
    public static final String[] FEATURE_SUB_NAMES = {
            "",
            "Den",
            "Mine",
            "Ruin",
            "Temple",
            "Dungeon",
            "Demon",
            "Devil",
            "Dino",
            "Grove",
            "Giant Hall",
            "Dragon Lair",
    };

    public static final int CLASS_FIGHTER = 0;
    
    public static final String[] CLASS_NAMES = {
            "Fighter",
    };
    
    public static final int[][] TABLE_RACE_IN_DOMAIN = {
            { 5, RACE_HUMAN },
            { 2, RACE_DWARF },
            { 1, RACE_ELF },
            { 2, RACE_MIXED },
    };
    
    public static final int[][] TABLE_GOVERNMENT_IN_DOMAIN = {
            { 2, GOVERNMENT_ANARCHY },
            { 2, GOVERNMENT_COUNTY },
            { 4, GOVERNMENT_DUCHY },
            { 2, GOVERNMENT_KINGDOM },
            { 1, GOVERNMENT_EMPIRE },
    };
    
    public static final int[][] TABLE_GOVERNMENT_NOT_IN_DOMAIN = {
            { 47, GOVERNMENT_ANARCHY },
            { 53, GOVERNMENT_COUNTY },
    };
    
    public static final int[][][] TABLE_RACE_IN_REGION = {
        {   // MIXED
            { 1, RACE_HUMAN },
            { 2, RACE_DWARF },
            { 2, RACE_ELF },
            { 5, RACE_MIXED },
        },
        {   // HUMAN
            { 5, RACE_HUMAN },
            { 3, RACE_DWARF },
            { 1, RACE_ELF },
            { 1, RACE_MIXED },
        },
        {   // DWARF
            { 3, RACE_HUMAN },
            { 5, RACE_DWARF },
            { 1, RACE_ELF },
            { 2, RACE_MIXED },
        },
        {   // ELF
            { 2, RACE_HUMAN },
            { 1, RACE_DWARF },
            { 5, RACE_ELF },
            { 3, RACE_MIXED },
        },
    };
    
    public static final int[][][] TABLE_TERRAIN_IN_REGION = {
        {   // MIXED
            { 10, TERRAIN_PLAINS },
            { 10, TERRAIN_HILLS },
            { 30, TERRAIN_MOUNTAINS },
            { 10, TERRAIN_ARTIC },
            { 20, TERRAIN_FOREST },
            { 20, TERRAIN_DESERT },
            { 30, TERRAIN_JUNGLE },
            { 30, TERRAIN_SWAMP },
            { 5, TERRAIN_FRESHWATER },
            { 5, TERRAIN_SALTWATER },
        },
        {   // HUMAN
            { 50, TERRAIN_PLAINS },
            { 20, TERRAIN_HILLS },
            { 5, TERRAIN_MOUNTAINS },
            { 0, TERRAIN_ARTIC },
            { 20, TERRAIN_FOREST },
            { 20, TERRAIN_DESERT },
            { 5, TERRAIN_JUNGLE },
            { 5, TERRAIN_SWAMP },
            { 0, TERRAIN_FRESHWATER },
            { 0, TERRAIN_SALTWATER },
        },
        {   // DWARF
            { 20, TERRAIN_PLAINS },
            { 40, TERRAIN_HILLS },
            { 50, TERRAIN_MOUNTAINS },
            { 0, TERRAIN_ARTIC },
            { 5, TERRAIN_FOREST },
            { 5, TERRAIN_DESERT },
            { 5, TERRAIN_JUNGLE },
            { 5, TERRAIN_SWAMP },
            { 0, TERRAIN_FRESHWATER },
            { 0, TERRAIN_SALTWATER },
        },
        {   // ELF
            { 10, TERRAIN_PLAINS },
            { 20, TERRAIN_HILLS },
            { 30, TERRAIN_MOUNTAINS },
            { 0, TERRAIN_ARTIC },
            { 50, TERRAIN_FOREST },
            { 5, TERRAIN_DESERT },
            { 30, TERRAIN_JUNGLE },
            { 5, TERRAIN_SWAMP },
            { 0, TERRAIN_FRESHWATER },
            { 0, TERRAIN_SALTWATER },
        },
    };
    
    public static final Object[][][] TABLE_TERRAIN_IN_TERRAIN = {
            //TERRAIN_PLAINS
            {
                { 1, CompConstLogic.TERRAIN_ARTIC },
                { 9, CompConstLogic.TERRAIN_MOUNTAINS },
                { 20, CompConstLogic.TERRAIN_HILLS },
                { 10, CompConstLogic.TERRAIN_FOREST },
                { 50, CompConstLogic.TERRAIN_PLAINS },
                //{ 0, CompConstLogic.TERRAIN_SWAMP },  
                { 5, CompConstLogic.TERRAIN_FRESHWATER },  
            },
            //TERRAIN_HILLS
            {
                { 2, CompConstLogic.TERRAIN_ARTIC },
                { 18, CompConstLogic.TERRAIN_MOUNTAINS },
                { 40, CompConstLogic.TERRAIN_HILLS },
                { 20, CompConstLogic.TERRAIN_FOREST },
                { 10, CompConstLogic.TERRAIN_PLAINS },
                { 2, CompConstLogic.TERRAIN_SWAMP },  
                { 3, CompConstLogic.TERRAIN_FRESHWATER },  
            },
            //TERRAIN_MOUNTAINS
            {
                { 3, CompConstLogic.TERRAIN_ARTIC },
                { 27, CompConstLogic.TERRAIN_MOUNTAINS },
                { 30, CompConstLogic.TERRAIN_HILLS },
                { 20, CompConstLogic.TERRAIN_FOREST },
                { 10, CompConstLogic.TERRAIN_PLAINS },
                { 5, CompConstLogic.TERRAIN_SWAMP },  
            },
            //TERRAIN_ARTIC
            {
                { 20, CompConstLogic.TERRAIN_ARTIC },
                { 20, CompConstLogic.TERRAIN_MOUNTAINS },
                { 20, CompConstLogic.TERRAIN_HILLS },
                { 10, CompConstLogic.TERRAIN_FOREST },
                { 10, CompConstLogic.TERRAIN_PLAINS },
                { 2, CompConstLogic.TERRAIN_FRESHWATER },  
            },
            //TERRAIN_FOREST
            {
                { 1, CompConstLogic.TERRAIN_ARTIC },
                { 9, CompConstLogic.TERRAIN_MOUNTAINS },
                { 20, CompConstLogic.TERRAIN_HILLS },
                { 40, CompConstLogic.TERRAIN_FOREST },
                { 20, CompConstLogic.TERRAIN_PLAINS },
                { 2, CompConstLogic.TERRAIN_SWAMP },  
                { 3, CompConstLogic.TERRAIN_FRESHWATER },  
            },
            //TERRAIN_DESERT
            {
                { 1, CompConstLogic.TERRAIN_ARTIC },
                { 9, CompConstLogic.TERRAIN_MOUNTAINS },
                { 20, CompConstLogic.TERRAIN_HILLS },
                { 10, CompConstLogic.TERRAIN_PLAINS },
                { 50, CompConstLogic.TERRAIN_DESERT },
            },
            //TERRAIN_JUNGLE
            {
                { 1, CompConstLogic.TERRAIN_ARTIC },
                { 9, CompConstLogic.TERRAIN_MOUNTAINS },
                { 20, CompConstLogic.TERRAIN_HILLS },
                { 40, CompConstLogic.TERRAIN_JUNGLE },
                { 15, CompConstLogic.TERRAIN_PLAINS },
                { 9, CompConstLogic.TERRAIN_SWAMP },  
                { 1, CompConstLogic.TERRAIN_FRESHWATER },  
            },
            //TERRAIN_SWAMP
            {
                { 1, CompConstLogic.TERRAIN_ARTIC },
                { 9, CompConstLogic.TERRAIN_MOUNTAINS },
                { 20, CompConstLogic.TERRAIN_HILLS },
                { 10, CompConstLogic.TERRAIN_FOREST },
                { 10, CompConstLogic.TERRAIN_PLAINS },
                { 30, CompConstLogic.TERRAIN_SWAMP },  
                { 10, CompConstLogic.TERRAIN_FRESHWATER },  
            },
            //TERRAIN_FRESHWATER
            {
                { 1, CompConstLogic.TERRAIN_ARTIC },
                { 9, CompConstLogic.TERRAIN_MOUNTAINS },
                { 10, CompConstLogic.TERRAIN_HILLS },
                { 10, CompConstLogic.TERRAIN_FOREST },
                { 10, CompConstLogic.TERRAIN_PLAINS },
                { 10, CompConstLogic.TERRAIN_SWAMP },  
                { 40, CompConstLogic.TERRAIN_FRESHWATER },  
            },
            //TERRAIN_SALTWATER            
            {
                { 1, CompConstLogic.TERRAIN_ARTIC },
                { 9, CompConstLogic.TERRAIN_MOUNTAINS },
                { 10, CompConstLogic.TERRAIN_HILLS },
                { 20, CompConstLogic.TERRAIN_DESERT },
                { 50, CompConstLogic.TERRAIN_SALTWATER },  
            },
    };
    
    public static final float[] TABLE_TERRAIN_COST = {
            1.0f*CompConstLogic.DISTANCE_STRATEGIC, 
            2.0f*CompConstLogic.DISTANCE_STRATEGIC, 
            5.0f*CompConstLogic.DISTANCE_STRATEGIC, 
            2.5f*CompConstLogic.DISTANCE_STRATEGIC, 
            1.5f*CompConstLogic.DISTANCE_STRATEGIC, 
            1.2f*CompConstLogic.DISTANCE_STRATEGIC, 
            1.7f*CompConstLogic.DISTANCE_STRATEGIC, 
            1.5f*CompConstLogic.DISTANCE_STRATEGIC, 
            4.0f*CompConstLogic.DISTANCE_STRATEGIC, 
            5.0f*CompConstLogic.DISTANCE_STRATEGIC
    };

    public static final float[] TABLE_TERRAIN_CHALLENGE = {
            -2, 0, 4, 2, 1, 0, 2, 2, 0, 0,
    };

    public static final float TABLE_ROUGHNESS_TERRAIN[] = {
            0.75f, //TERRAIN_PLAINS
            1.25f, //TERRAIN_HILLS
            1.5f, //TERRAIN_MOUNTAINS
            1.0f, //TERRAIN_ARTIC
            1.0f, //TERRAIN_FOREST
            0.70f, //TERRAIN_DESERT
            1.1f, //TERRAIN_JUNGLE
            0.8f, //TERRAIN_SWAMP
            1.0f, //TERRAIN_FRESHWATER
            1.0f, //TERRAIN_SALTWATER            
    };

    public static final float TABLE_CITY_SITE[] = {
            15.0f, //TERRAIN_PLAINS
            2.0f, //TERRAIN_HILLS
            0.0f, //TERRAIN_MOUNTAINS
            0.0f, //TERRAIN_ARTIC
            3.0f, //TERRAIN_FOREST
            0.0f, //TERRAIN_DESERT
            0.0f, //TERRAIN_JUNGLE
            0.0f, //TERRAIN_SWAMP
            4.0f, //TERRAIN_FRESHWATER
            0.0f, //TERRAIN_SALTWATER            
    };

    public static final float TABLE_FORT_SITE[] = {
            4.0f, //TERRAIN_PLAINS
            15.0f, //TERRAIN_HILLS
            2.0f, //TERRAIN_MOUNTAINS
            0.0f, //TERRAIN_ARTIC
            3.0f, //TERRAIN_FOREST
            0.0f, //TERRAIN_DESERT
            0.0f, //TERRAIN_JUNGLE
            0.0f, //TERRAIN_SWAMP
            0.0f, //TERRAIN_FRESHWATER
            0.0f, //TERRAIN_SALTWATER            
    };

    public static final int[][] TABLE_FEATURES_PER_GOVERNMENT = {
        //{ NONE, HAMLET, VILLAGE, TOWN, CITY, RUIN, OUTPOST, FORT, CASTLE }
        // GOVERNMENT_ANARCHY
        { 0, 2 , 1 , 0, 0, 128,  0, 0, 0, 0, 0, 0, 0,128 },
        // GOVERNMENT_COUNTY
        { 0, 4 , 2 , 1, 0,  64,  4, 1, 0, 0, 0, 0, 0, 64 },
        // GOVERNMENT_DUCHY
        { 0, 8 , 4 , 2, 1,  48,  4, 2, 1, 0, 0, 0, 0, 48 },
        // GOVERNMENT_KINGDOM
        { 0, 16, 8 , 4, 2,  32,  6, 4, 2, 0, 0, 0, 0, 32 },
        // GOVERNMENT_EMPIRE
        { 0, 32, 16, 8, 4,  16,  8, 6, 3, 0, 0, 0, 0, 16 },
    };

    public static final int[] TABLE_FEATURE_RADIUS = {
        0, 4, 8, 12, 16, 4, 2, 4, 8,
    };
    
    public static final int[][][] TABLE_SWAP_MASKS = {
        { {0, 0}, {0, 1}, {1, 0}, {1, 1} },
        { {0, 0}, {1, 0}, {1, 1}, {2, 1} },
        { {0, 0}, {0, 1}, {1, 1}, {1, 2} },
        { {1, 0}, {2, 0}, {0, 1}, {1, 1} },
        { {1, 0}, {1, 1}, {0, 1}, {0, 2} },
        { {1, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2}, {2, 2},  },
        { {0, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2}, {2, 2},  },
        { {0, 0}, {1, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2}, {2, 2},  },
        { {0, 0}, {1, 0}, {2, 0}, {1, 1}, {2, 1}, {0, 2}, {1, 2}, {2, 2},  },
        { {0, 0}, {1, 0}, {2, 0}, {0, 1}, {1, 1}, {0, 2}, {1, 2}, {2, 2},  },
        { {0, 0}, {1, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {1, 2}, {2, 2},  },
        { {0, 0}, {1, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {2, 2},  },
        { {0, 0}, {1, 0}, {2, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2},  },
    };
    
    public static final int[] TABLE_XP_PER_HD = {
            5,
            10,
            30,
            50,
            75,
            110,
            160,
            225,
            350,
            600,
            700,
            900,
            1300,
            1500,
            1800,
            2100,
            2400,
            2700,
            3000,
            3500,
            4000,
            4500,
            5000,
    };
    public static final int[] TABLE_XP_PER_HP = {
            1,
            1,
            1,
            2,
            3,
            4,
            6,
            8,
            10,
            12,
            13,
            14,
            16,
            17,
            18,
            19,
            20,
            23,
            25,
            28,
            30,
            33,
            35,
    };

    public static final int[][] TABLE_XP_FOR_CLASS = {
        // fighter
            {
                0,
                1900,
                4250,
                7750,
                16000,
                35000,
                75000,
                125000,
                250000,
                500000,
                750000,
            }
    };
    public static final long ROOM_FIGHT_TIMEOUT = 10*60*1000L;

    public static final int INVENTORY_CHUNK = 5;
    
    public static final int PRESTIGE_HERMIT = 1;
    public static final int PRESTIGE_DEMON = 2;
    public static final int PRESTIGE_TITHE = 5;
    
    public static Random getRandom(CoordBean ords)
    {
        return getRandom(ords, 0);
    }
    
    public static Random getRandom(CoordBean ords, Long seed1)
    {
        if (seed1 == null)
            return getRandom(ords, 0);
        long seed2 = ords.toSeed();
        long seed = seed1^seed2;
        Random rnd = new Random(seed);
        return rnd;
    }
    
    public static Random getRandom(CoordBean ords, int var)
    {
        long seed = ords.toSeed();
        if (var == 1)
            seed = ~seed;
        Random rnd = new Random(seed);
        return rnd;
    }
    
    public static Random getRandom(CoordBean ords1, CoordBean ords2)
    {
        long seed1 = ords1.toSeed();
        long seed2 = ords2.toSeed();
        long seed = seed1^seed2;
        Random rnd = new Random(seed);
        return rnd;
    }

    public static int roll(int[][] table, Random rnd)
    {
        int tot = 0;
        for (int i = 0; i < table.length; i++)
            tot += table[i][0];
        int idx = rnd.nextInt(tot);
        for (int i = 0; i < table.length; i++)
        {
            idx -= table[i][0];
            if (idx < 0)
                return table[i][1];
        }
        throw new IllegalStateException();
    }

    public static <T> T roll(T[] table, Random rnd)
    {
        return table[rnd.nextInt(table.length)];
    }
    
    public static String mapProductID(String productID, CompContextBean context)
    {
        switch (productID)
        {
            case PREMIUM_SUBSCRIPTION:
                if (context.getLastOperation().getFlags().toLowerCase().indexOf("beta") >= 0)
                    return PREMIUM_SUBSCRIPTION_ID2;
                else
                    return PREMIUM_SUBSCRIPTION_ID1;
        }
        return productID;
    }

    public static boolean isPremium(CoordBean ords)
    {
        return ords.getZ() >= 2;
    }
}
