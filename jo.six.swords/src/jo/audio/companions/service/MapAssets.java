package jo.audio.companions.service;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.GenerationLogic;
import jo.util.utils.DebugUtils;
import jo.util.utils.io.ResourceUtils;
import jo.util.utils.obj.StringUtils;

public class MapAssets
{
    private Map<Integer, Color> mTerrainToColor = new HashMap<>();
    private Map<Integer, Color> mChallengeToColor = new HashMap<>();
//    private Map<Integer, BufferedImage> mTerrainToImage = new HashMap<>();
//    private Map<Integer, BufferedImage> mFeatureToImage = new HashMap<>();
//    private Map<Integer, BufferedImage> mSubFeatureToImage = new HashMap<>();
//    private Map<String, BufferedImage> mRoomToImage = new HashMap<>();
    private Map<Integer, String> mTerrainToFile = new HashMap<>();
    private Map<Integer, String> mFeatureToFile = new HashMap<>();
    private Map<Integer, String> mSubFeatureToFile = new HashMap<>();
    private Map<String, String> mRoomToFile = new HashMap<>();
    public final static Color ROAD1 = new Color(0xE79256);
    public final static Color ROAD2 = new Color(0xD2691E);
    public final static Color ROAD3 = new Color(0x8f4814);
    public final static Color RIVER1 = new Color(0x87CEEB);
    public final static Color RIVER2 = new Color(0x4169E1);
    public final static Color RIVER3 = new Color(0x000080);
    
    public MapAssets()
    {
        loadImages();
    }
    
    private void loadImages()
    {
        try
        {
            loadImage(mTerrainToFile,  CompConstLogic.TERRAIN_ARTIC,  "map_arctic.png");
            loadImage(mTerrainToFile,  CompConstLogic.TERRAIN_DESERT,  "map_desert.png");
            loadImage(mTerrainToFile,  CompConstLogic.TERRAIN_FOREST,  "map_jungle.png");
            loadImage(mTerrainToFile,  CompConstLogic.TERRAIN_FRESHWATER,  "map_freshwater.png");
            loadImage(mTerrainToFile,  CompConstLogic.TERRAIN_HILLS,  "map_hills.png");
            loadImage(mTerrainToFile,  CompConstLogic.TERRAIN_JUNGLE,  "map_jungle.png");
            loadImage(mTerrainToFile,  CompConstLogic.TERRAIN_MOUNTAINS,  "map_mountain.png");
            loadImage(mTerrainToFile,  CompConstLogic.TERRAIN_PLAINS,  "map_plains.png");
            loadImage(mTerrainToFile,  CompConstLogic.TERRAIN_SALTWATER,  "map_saltwater.png");
            loadImage(mTerrainToFile,  CompConstLogic.TERRAIN_SWAMP,  "map_swamp.png");
            loadImage(mFeatureToFile,  CompConstLogic.FEATURE_CASTLE,  "map_castle.png");
            loadImage(mFeatureToFile,  CompConstLogic.FEATURE_CITY,  "map_city.png");
            loadImage(mFeatureToFile,  CompConstLogic.FEATURE_FORT,  "map_fort.png");
            loadImage(mFeatureToFile,  CompConstLogic.FEATURE_HAMLET,  "map_hamlet.png");
            loadImage(mFeatureToFile,  CompConstLogic.FEATURE_OUTPOST,  "map_outpost.png");
            loadImage(mFeatureToFile,  CompConstLogic.FEATURE_TOWN,  "map_town.png");
            loadImage(mFeatureToFile,  CompConstLogic.FEATURE_VILLAGE,  "map_village.png");
            loadImage(mFeatureToFile,  CompConstLogic.FEATURE_RUIN,  "map_ruin.png");
            loadImage(mFeatureToFile,  CompConstLogic.FEATURE_STATIC, "map_static.png");
            loadImage(mFeatureToFile,  CompConstLogic.FEATURE_DOCK, "map_dock.png");
            loadImage(mFeatureToFile,  CompConstLogic.FEATURE_ARCH, "map_arch.png");
            loadImage(mFeatureToFile,  CompConstLogic.FEATURE_VORTEX, "map_arch.png");
            loadImage(mSubFeatureToFile,  CompConstLogic.FEATURE_SUB_DEN,  "map_den.png");
            loadImage(mSubFeatureToFile,  CompConstLogic.FEATURE_SUB_MINE,  "map_mine.png");
            loadImage(mSubFeatureToFile,  CompConstLogic.FEATURE_SUB_TEMPLE,  "map_temple.png");
            loadImage(mSubFeatureToFile,  CompConstLogic.FEATURE_SUB_RUIN,  "map_ruin.png");
            loadImage(mSubFeatureToFile,  CompConstLogic.FEATURE_SUB_DEMON,  "map_demon.png");
            loadImage(mSubFeatureToFile,  CompConstLogic.FEATURE_SUB_DEVIL,  "map_devil.png");
            loadImage(mSubFeatureToFile,  CompConstLogic.FEATURE_SUB_DINO,  "map_dino.png");
            loadImage(mSubFeatureToFile,  CompConstLogic.FEATURE_SUB_GROVE,  "map_grove.png");
            loadRawImage(mRoomToFile,  "default",  "tiles_default.png");
            loadRawImage(mRoomToFile,  "$exit",  "tiles_exit.png");
            loadRawImage(mRoomToFile,  "townRoadIn",  "tiles_road.png");
            loadRawImage(mRoomToFile,  "townMainStreet,townCrossStreet,townNorthStreet,townSouthStreet,townEastStreet,townWestStreet",  "tiles_cobble.png");
            loadRawImage(mRoomToFile,  "townCommon",  "tiles_common.png");
            loadRawImage(mRoomToFile,  "castleGatehouse,castleCourtyard",  "tiles_flagstone.png");
            loadRawImage(mRoomToFile,  "castleOffice",  "tiles_wood.png");
            loadRawImage(mRoomToFile,  "castleOffice",  "tiles_wood.png");
            loadRawImage(mRoomToFile,  "shophand,",  "tiles_weapon.png");
            loadRawImage(mRoomToFile,  "shophurled,",  "tiles_hurled.png");
            loadRawImage(mRoomToFile,  "shopammo,",  "tiles_ammo.png");
            loadRawImage(mRoomToFile,  "shoplauncher,",  "tiles_launcher.png");
            loadRawImage(mRoomToFile,  "shoparmor,",  "tiles_armor.png");
            loadRawImage(mRoomToFile,  "shopshield,",  "tiles_shield.png");
            loadRawImage(mRoomToFile,  "shoppotion,",  "tiles_potion.png");
            loadRawImage(mRoomToFile,  "shopcastle,",  "tiles_deed.png");
            loadRawImage(mRoomToFile,  "fightersguild,",  "tiles_hiring.png");
            loadRawImage(mRoomToFile,  "den1,den2,den3,den4",  "tiles_dirt.png");
            loadRawImage(mRoomToFile,  "mineEntrance",  "tiles_mineentrance.png");
            loadRawImage(mRoomToFile,  "mineShaft,mineCollapse,mineGallery,mineSmelter",  "tiles_mineshaft.png");
            loadRawImage(mRoomToFile,  "temple0,temple1,temple2,temple3,temple4,temple5,temple6,temple7,temple8,temple9,temple10,temple11"
                    + ",temple21,temple22,temple23,temple24,temple25,temple26,temple27",  "tiles_templegreek.png");
            loadRawImage(mRoomToFile,  "temple12,temple13,temple14,temple15",  "tiles_templelizard.png");
            loadRawImage(mRoomToFile,  "temple16,temple17,temple18,temple19,temple20,temple21",  "tiles_templedwarven.png");
            loadRawImage(mRoomToFile,  "mask0",  "tiles_mask00.png");
            loadRawImage(mRoomToFile,  "mask1",  "tiles_mask01.png");
            loadRawImage(mRoomToFile,  "mask2",  "tiles_mask02.png");
            loadRawImage(mRoomToFile,  "mask3",  "tiles_mask03.png");
            loadRawImage(mRoomToFile,  "mask4",  "tiles_mask04.png");
            loadRawImage(mRoomToFile,  "mask5",  "tiles_mask05.png");
            loadRawImage(mRoomToFile,  "mask6",  "tiles_mask06.png");
            loadRawImage(mRoomToFile,  "mask7",  "tiles_mask07.png");
            loadRawImage(mRoomToFile,  "mask8",  "tiles_mask08.png");
            loadRawImage(mRoomToFile,  "mask9",  "tiles_mask09.png");
            loadRawImage(mRoomToFile,  "mask10",  "tiles_mask10.png");
            loadRawImage(mRoomToFile,  "mask11",  "tiles_mask11.png");
            loadRawImage(mRoomToFile,  "mask12",  "tiles_mask12.png");
            loadRawImage(mRoomToFile,  "mask13",  "tiles_mask13.png");
            loadRawImage(mRoomToFile,  "mask14",  "tiles_mask14.png");
            loadRawImage(mRoomToFile,  "mask15",  "tiles_mask15.png");
            loadColor(CompConstLogic.TERRAIN_ARTIC, 2, 0xFFFFFF);
            loadColor(CompConstLogic.TERRAIN_ARTIC, 1, 0xD8D8D8);
            loadColor(CompConstLogic.TERRAIN_ARTIC, 0, 0xC0C0C0);
            loadColor(CompConstLogic.TERRAIN_MOUNTAINS, 2, 0xC0C0C0);
            loadColor(CompConstLogic.TERRAIN_MOUNTAINS, 1, 0xA0A0A0);
            loadColor(CompConstLogic.TERRAIN_MOUNTAINS, 0, 0x808080);
            loadColor(CompConstLogic.TERRAIN_HILLS, 2, 0xCD853F);
            loadColor(CompConstLogic.TERRAIN_HILLS, 1, 0xCF9C65);
            loadColor(CompConstLogic.TERRAIN_HILLS, 0, 0xD2B48C);
            loadColor(CompConstLogic.TERRAIN_DESERT, 0, 0xADFF2F);
            loadColor(CompConstLogic.TERRAIN_DESERT, 1, 0xFFFF66);
            loadColor(CompConstLogic.TERRAIN_DESERT, 2, 0xFFFF00);
            loadColor(CompConstLogic.TERRAIN_FOREST, 0, 0x00FF00);
            loadColor(CompConstLogic.TERRAIN_FOREST, 1, 0x9ACD32);
            loadColor(CompConstLogic.TERRAIN_FOREST, 2, 0x228B22);
            loadColor(CompConstLogic.TERRAIN_FRESHWATER, 0, 0x20B2AA);
            loadColor(CompConstLogic.TERRAIN_FRESHWATER, 1, 0x3CB371);
            loadColor(CompConstLogic.TERRAIN_FRESHWATER, 2, 0x8FBC8F);
            loadColor(CompConstLogic.TERRAIN_JUNGLE, 0, 0x00FF00);
            loadColor(CompConstLogic.TERRAIN_JUNGLE, 1, 0x9ACD32);
            loadColor(CompConstLogic.TERRAIN_JUNGLE, 2, 0x228B22);
            loadColor(CompConstLogic.TERRAIN_PLAINS, 0, 0x32CD32);
            loadColor(CompConstLogic.TERRAIN_PLAINS, 1, 0x98D619);
            loadColor(CompConstLogic.TERRAIN_PLAINS, 2, 0xFFDF00);
            loadColor(CompConstLogic.TERRAIN_SALTWATER, 0, 0xADD8E6);
            loadColor(CompConstLogic.TERRAIN_SALTWATER, 1, 0x87CEFA);
            loadColor(CompConstLogic.TERRAIN_SALTWATER, 2, 0x00BFFF);
            loadColor(CompConstLogic.TERRAIN_SWAMP, 0, 0x98FB98);
            loadColor(CompConstLogic.TERRAIN_SWAMP, 1, 0x00FA9A);
            loadColor(CompConstLogic.TERRAIN_SWAMP, 2, 0x00FF7F);
            mChallengeToColor.put(0, Color.BLUE);
            mChallengeToColor.put(1, Color.GREEN);
            mChallengeToColor.put(2, Color.YELLOW);
            mChallengeToColor.put(3, Color.ORANGE);
            mChallengeToColor.put(4, Color.RED);
            mChallengeToColor.put(5, Color.MAGENTA);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private <T> void loadImage(Map<T,String> map, T key, String fname)
    {
        map.put(key, fname);
    }
    
    @SuppressWarnings("unchecked")
    private <T> void loadRawImage(Map<T,String> map, T key, String fname) throws IOException
    {
        if (key instanceof String)
            for (StringTokenizer st = new StringTokenizer((String)key, ","); st.hasMoreTokens(); )
                map.put((T)st.nextToken(), fname);
    }
    
    private BufferedImage loadSystemResourceImage(String fname)
    {
        try
        {
            InputStream is = ResourceUtils.loadSystemResourceStream("images/"+fname, MapAssets.class);
            BufferedImage img1 = ImageIO.read(is);
            is.close();
            return img1;
        }
        catch (IOException e)
        {
            DebugUtils.trace("Cannot load image "+fname, e);
            return null;
        }
    }
    
    private BufferedImage loadSystemResourceTile(String fname)
    {
        try
        {
            InputStream is = ResourceUtils.loadSystemResourceStream("images/"+fname, MapAssets.class);
            BufferedImage img1 = ImageIO.read(is);
            is.close();
            BufferedImage img2 = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < 48; x++)
                for (int y = 0; y < 48; y++)
                {
                    int rgb = img1.getRGB(x, y);
                    rgb &= 0x00ffffff;
                    rgb |= ((~(rgb&0xff))<<24);
                    img2.setRGB(x, y, rgb);
                }
            return img2;
        }
        catch (IOException e)
        {
            DebugUtils.trace("Cannot load image "+fname, e);
            return null;
        }
    }

    private void loadColor(int terrain, int depth, int color)
    {
        Color c = new Color(color);
        mTerrainToColor.put(new Integer(terrain*256 + depth), c);
    }

    public Color getColor(SquareBean sq, boolean challenge)
    {
        if (challenge)
        {
            return mChallengeToColor.get(sq.getChallenge2());
        }
        else
            return mTerrainToColor.get(new Integer(sq.getTerrain()*256 + sq.getTerrainDepth()));
    }
    
    public BufferedImage getImage(SquareBean sq)
    {
        String fname = null;
        if (sq.getFeature() != CompConstLogic.FEATURE_NONE)
        {
            RegionBean region = GenerationLogic.getRegion(sq.getOrds());
            FeatureBean feature = FeatureLogic.getFeature(region, sq, null);
            if (mSubFeatureToFile.containsKey(feature.getSubType()))
                fname = mSubFeatureToFile.get(feature.getSubType());
            else if (mFeatureToFile.containsKey(sq.getFeature()))
                fname = mFeatureToFile.get(sq.getFeature());
        }
        if (fname == null)
            fname = mTerrainToFile.get(sq.getTerrain());
        return loadSystemResourceTile(fname);
    }
    
    public BufferedImage getRoomImage(String id)
    {
        if (StringUtils.isTrivial(id))
            return null;
        String fname = mRoomToFile.get(id);
        if (fname != null)
            return loadSystemResourceImage(fname);
        int o = id.indexOf("$");
        if (o >= 0)
        {
            id = id.substring(0, o);
            fname = mRoomToFile.get(id);
            if (fname != null)
                return loadSystemResourceImage(fname);
        }
        while (id.length() > 0)
        {
            id = id.substring(0, id.length() -1);
            fname = mRoomToFile.get(id);
            if (fname != null)
                return loadSystemResourceImage(fname);
        }
        fname =  mRoomToFile.get("default");
        return loadSystemResourceImage(fname);
    }
    
    public BufferedImage getRoomMask(int mask)
    {
        String fname = mRoomToFile.get("mask"+mask);
        return loadSystemResourceImage(fname);
    }

}
