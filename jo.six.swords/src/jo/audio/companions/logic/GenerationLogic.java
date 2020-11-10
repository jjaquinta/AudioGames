package jo.audio.companions.logic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.DomainBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.RegionGenBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.gen.LiteGenerator;
import jo.audio.companions.logic.gen.NullGenerator;
import jo.audio.companions.logic.gen.ResourceGenerator;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class GenerationLogic
{
    private static IGenerator[] GENERATORS = new IGenerator[] {
            new LiteGenerator(),
            new LiteGenerator(1L),
            //new ResourceGenerator("file://d:\\temp\\data\\sixswords"),
            new ResourceGenerator("resource://jo/audio/companions/slu/ee", true),
            new ResourceGenerator("resource://jo/audio/companions/slu/irl"),
            new NullGenerator("Eternal Plains", CompConstLogic.TERRAIN_PLAINS, true),
            new NullGenerator("The Washboard", CompConstLogic.TERRAIN_HILLS, true),
            new NullGenerator("J�tunheimr", CompConstLogic.TERRAIN_MOUNTAINS, true),
            new NullGenerator("Hyperborea", CompConstLogic.TERRAIN_ARTIC, true),
            new NullGenerator("Arboria", CompConstLogic.TERRAIN_FOREST, true),
            new NullGenerator("Karoo", CompConstLogic.TERRAIN_DESERT, true),
            new NullGenerator("Ranthambore", CompConstLogic.TERRAIN_JUNGLE, true),
            new NullGenerator("Slough of Despond", CompConstLogic.TERRAIN_SWAMP, true),
            new NullGenerator("The Great Lake", CompConstLogic.TERRAIN_FRESHWATER, true),
            new NullGenerator("World Ocean", CompConstLogic.TERRAIN_SALTWATER, true),
            new ResourceGenerator("resource://jo/audio/companions/slu/ice"),
    };
    
    public static DomainBean getDomain(CoordBean ord)
    {
        DomainBean d = GENERATORS[ord.getZ()].getDomain(ord);
        d.setLastUsed(System.currentTimeMillis());
        return d;
    }
    
    public static RegionBean getRegion(CoordBean ord)
    {
        RegionBean r = GENERATORS[ord.getZ()].getRegion(ord);
        r.setLastUsed(System.currentTimeMillis());
        return r;
    }
    
    public static SquareBean getSquare(CoordBean ord)
    {
        SquareBean sq = GENERATORS[ord.getZ()].getSquare(ord);
        sq.setLastUsed(System.currentTimeMillis());
        return sq;
    }
    
    public static FeatureBean getFeature(RegionBean region, SquareBean square, boolean athiest)
    {
        return GENERATORS[square.getOrds().getZ()].getFeature(region, square, athiest);
    }
    
    public static int getCloudCover(CoordBean ord, int time)
    {
        return GENERATORS[ord.getZ()].getCloudCover(ord, time);
    }
    public static int getPrecipitation(CoordBean ord, int time)
    {
        return GENERATORS[ord.getZ()].getPrecipitation(ord, time);
    }

    
    public static Thread startBackgroundDaemon()
    {
        DebugUtils.trace("Starting Generation Thread");
        Thread t = new Thread("Generation Update") { public void run() { 
            doGenerationDaemon();
        } };
        t.setDaemon(true);
        t.start();
        return t;
    }
    
    private static void doGenerationDaemon()
    {
        for (;;)
        {
            try
            {
                GenerationLogic.doSleep();
                GenerationLogic.doUpdateGeneration();
            }
            catch (Throwable t)
            {
                DebugUtils.trace("Error from generation daemon", t);
            }
        }
    }

    public static final long UPDATE_FREQUENCY = 15*60*1000L;
    
    private static void doSleep()
    {
        try
        {
            Thread.sleep(UPDATE_FREQUENCY);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private static void doUpdateGeneration()
    {
        DebugUtils.trace("Updating Generation Thread");
        try
        {
            Random rnd = new Random(System.currentTimeMillis());
            moveVorticies(rnd);
        }
        catch (Exception e)
        {
            DebugUtils.trace("Error while processing Generation Thread", e);
        }
        DebugUtils.trace("Done Generation Thread");
    }
    
    public static List<CoordBean> getVorticies()
    {
        List<CoordBean> vorticies = new ArrayList<>();
        for (IGenerator gen : GENERATORS)
            if (gen instanceof ResourceGenerator)
                ((ResourceGenerator)gen).getVorticies(vorticies);
            else if (gen instanceof NullGenerator)
                ((NullGenerator)gen).getVorticies(vorticies);
        return vorticies;
    }

    public static void moveVorticies(Random rnd)
    {
        for (IGenerator gen : GENERATORS)
            if (gen instanceof ResourceGenerator)
                ((ResourceGenerator)gen).moveVorticies(rnd);
            else if (gen instanceof NullGenerator)
                ((NullGenerator)gen).moveVorticies(rnd);
    }
    
    public static void main(String[] argv)
    {
        BufferedImage cover = new BufferedImage(CompConstLogic.SQUARES_PER_DOMAIN*2, CompConstLogic.SQUARES_PER_DOMAIN*2, BufferedImage.TYPE_INT_RGB);
        BufferedImage danger = new BufferedImage(CompConstLogic.SQUARES_PER_DOMAIN*2, CompConstLogic.SQUARES_PER_DOMAIN*2, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < cover.getWidth(); x++)
            for (int y = 0; y < cover.getHeight(); y++)
            {
                CoordBean ord = new CoordBean(x, y);
                SquareBean s = getSquare(ord);
                int rgb = 0xFF000000;
                if (s.getFeature() != 0)
                    rgb = 0xFF00FF;
                else if (s.isAnyRoads())
                    rgb = 0x00FFFF;
                else
                    switch (s.getTerrain())
                    {
                        case CompConstLogic.TERRAIN_ARTIC:
                            rgb = 0xFFFFFF;
                            break;
                        case CompConstLogic.TERRAIN_MOUNTAINS:
                            rgb = 0x808080;
                            break;
                        case CompConstLogic.TERRAIN_HILLS:
                            rgb = 0xA52A2A;
                            break;
                        case CompConstLogic.TERRAIN_FOREST:
                            rgb = 0x00FF00;
                            break;
                        case CompConstLogic.TERRAIN_JUNGLE:
                            rgb = 0x00C000;
                            break;
                        case CompConstLogic.TERRAIN_PLAINS:
                            rgb = 0x008000;
                            break;
                        case CompConstLogic.TERRAIN_SWAMP:
                            rgb = 0x008080;
                            break;
                        case CompConstLogic.TERRAIN_FRESHWATER:
                            rgb = 0x000080;
                            break;
                        case CompConstLogic.TERRAIN_DESERT:
                            rgb = 0xFFFF00;
                            break;
                        case CompConstLogic.TERRAIN_SALTWATER:
                            rgb = 0x000040;
                            break;
                    }
                cover.setRGB(x, y, rgb);
                rgb = s.getChallenge()*15;
                rgb |= (rgb<<8)|(rgb<<16);
                danger.setRGB(x, y, rgb);
            }
        try
        {
            ImageIO.write(cover, "PNG", new File("c:\\temp\\companion_cover.png"));
            ImageIO.write(danger, "PNG", new File("c:\\temp\\companion_danger.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        StringBuffer names = new StringBuffer();
        StringBuffer race = new StringBuffer();
        StringBuffer terrain = new StringBuffer();
        StringBuffer government = new StringBuffer();
        for (int y = 0; y < CompConstLogic.SQUARES_PER_DOMAIN*2; y += CompConstLogic.SQUARES_PER_REGION)
        {
            for (int x = 0; x < CompConstLogic.SQUARES_PER_DOMAIN*2; x += CompConstLogic.SQUARES_PER_REGION)
            {
                RegionGenBean region = (RegionGenBean)getRegion(new CoordBean(x, y));
                names.append(StringUtils.spacePrefix(region.getTitle(), 20));
                race.append(StringUtils.spacePrefix(CompConstLogic.RACE_NAMES[region.getPredominantRace()], 20));
                terrain.append(StringUtils.spacePrefix(CompConstLogic.TERRAIN_NAMES[region.getPredominantTerrain()], 20));
                government.append(StringUtils.spacePrefix(CompConstLogic.GOVERNMENT_NAMES[region.getGovernmentalStructure()], 12));
            }
            names.append("\r\n");
            race.append("\r\n");
            terrain.append("\r\n");
            government.append("\r\n");
        }
        System.out.println("Names:");
        System.out.println(names.toString());
        System.out.println("Races:");
        System.out.println(race.toString());
        System.out.println("Terrain:");
        System.out.println(terrain.toString());
        System.out.println("Government:");
        System.out.println(government.toString());
        
        RegionBean region = getRegion(new CoordBean(975, 920, 2));
        List<Float> deltas = new ArrayList<>();
        for (int x = 1; x < CompConstLogic.SQUARES_PER_REGION - 1; x++)
            for (int y = 1; y < CompConstLogic.SQUARES_PER_REGION - 1; y++)
            {
                SquareBean sq = region.getSquare(x, y);
                SquareBean n = region.getSquare(x, y-1);
                SquareBean s = region.getSquare(x, y+1);
                SquareBean e = region.getSquare(x+1, y);
                SquareBean w = region.getSquare(x-1, y);
                deltas.add(Math.abs(sq.getAltitude() - n.getAltitude()));
                deltas.add(Math.abs(sq.getAltitude() - s.getAltitude()));
                deltas.add(Math.abs(sq.getAltitude() - e.getAltitude()));
                deltas.add(Math.abs(sq.getAltitude() - w.getAltitude()));
            }
        Collections.sort(deltas);
        Float cutoff = deltas.get(deltas.size()*5/6);
        System.out.println("Terrain shift 5/6 point = "+cutoff);
    }
    
    public static String dumpRegions()
    {
        StringBuffer html = new StringBuffer();
        for (IGenerator gen : GENERATORS)
            html.append(gen.dumpCache());
        return html.toString();
    }
    
    public static void cleanup()
    {
        for (IGenerator gen : GENERATORS)
            gen.cleanup();
    }
}
