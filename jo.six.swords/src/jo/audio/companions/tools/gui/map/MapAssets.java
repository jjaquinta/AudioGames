package jo.audio.companions.tools.gui.map;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.CompOperationLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.GenerationLogic;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioMessageBean;
import jo.audio.util.model.data.InteractionModelBean;
import jo.audio.util.model.logic.ModelResolveLogic;
import jo.audio.util.model.logic.ParseModelLogic;

public class MapAssets
{
    private InteractionModelBean mModel;
    private Map<Integer, BufferedImage> mTerrainToImage = new HashMap<>();
    private Map<Integer, Color> mTerrainToColor = new HashMap<>();
    private Map<Integer, Color> mChallengeToColor = new HashMap<>();
    private Map<Integer, BufferedImage> mFeatureToImage = new HashMap<>();
    private Map<Integer, BufferedImage> mSubFeatureToImage = new HashMap<>();
    public final static Color ROAD1 = new Color(0xE79256);
    public final static Color ROAD2 = new Color(0xD2691E);
    public final static Color ROAD3 = new Color(0x8f4814);
    public final static Color RIVER1 = new Color(0x87CEEB);
    public final static Color RIVER2 = new Color(0x4169E1);
    public final static Color RIVER3 = new Color(0x000080);
    public final static Color BORDER1 = new Color(0x8B0000);
    public final static Color BORDER2 = BORDER1.brighter();
    public final static Color BORDER3 = BORDER2.brighter();
    public final static Color BORDER4 = BORDER3.brighter();
    public final static Color BORDER5 = BORDER4.brighter();
    
    public final static AlphaComposite mTerrainComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,.25f);

    public MapAssets()
    {
        loadImages();
        loadModel();
    }
    
    private void loadImages()
    {
        try
        {
            loadImage(mTerrainToImage,  CompConstLogic.TERRAIN_ARTIC,  "map_arctic.png");
            loadImage(mTerrainToImage,  CompConstLogic.TERRAIN_DESERT,  "map_desert.png");
            loadImage(mTerrainToImage,  CompConstLogic.TERRAIN_FOREST,  "map_jungle.png");
            loadImage(mTerrainToImage,  CompConstLogic.TERRAIN_FRESHWATER,  "map_freshwater.png");
            loadImage(mTerrainToImage,  CompConstLogic.TERRAIN_HILLS,  "map_hills.png");
            loadImage(mTerrainToImage,  CompConstLogic.TERRAIN_JUNGLE,  "map_jungle.png");
            loadImage(mTerrainToImage,  CompConstLogic.TERRAIN_MOUNTAINS,  "map_mountain.png");
            loadImage(mTerrainToImage,  CompConstLogic.TERRAIN_PLAINS,  "map_plains.png");
            loadImage(mTerrainToImage,  CompConstLogic.TERRAIN_SALTWATER,  "map_saltwater.png");
            loadImage(mTerrainToImage,  CompConstLogic.TERRAIN_SWAMP,  "map_swamp.png");
            loadImage(mFeatureToImage,  CompConstLogic.FEATURE_CASTLE,  "map_castle.png");
            loadImage(mFeatureToImage,  CompConstLogic.FEATURE_CITY,  "map_city.png");
            loadImage(mFeatureToImage,  CompConstLogic.FEATURE_FORT,  "map_fort.png");
            loadImage(mFeatureToImage,  CompConstLogic.FEATURE_HAMLET,  "map_hamlet.png");
            loadImage(mFeatureToImage,  CompConstLogic.FEATURE_OUTPOST,  "map_outpost.png");
            loadImage(mFeatureToImage,  CompConstLogic.FEATURE_TOWN,  "map_town.png");
            loadImage(mFeatureToImage,  CompConstLogic.FEATURE_VILLAGE,  "map_village.png");
            loadImage(mFeatureToImage,  CompConstLogic.FEATURE_RUIN,  "map_ruin.png");
            loadImage(mFeatureToImage,  CompConstLogic.FEATURE_STATIC, "map_static.png");
            loadImage(mFeatureToImage,  CompConstLogic.FEATURE_DOCK, "map_dock.png");
            loadImage(mFeatureToImage,  CompConstLogic.FEATURE_ARCH, "map_arch.png");
            loadImage(mFeatureToImage,  CompConstLogic.FEATURE_VORTEX, "map_arch.png");
            loadImage(mFeatureToImage,  CompConstLogic.FEATURE_DUNGEON, "map_ruin.png");
            loadImage(mSubFeatureToImage,  CompConstLogic.FEATURE_SUB_DEN,  "map_den.png");
            loadImage(mSubFeatureToImage,  CompConstLogic.FEATURE_SUB_MINE,  "map_mine.png");
            loadImage(mSubFeatureToImage,  CompConstLogic.FEATURE_SUB_TEMPLE,  "map_temple.png");
            loadImage(mSubFeatureToImage,  CompConstLogic.FEATURE_SUB_RUIN,  "map_ruin.png");
            loadImage(mSubFeatureToImage,  CompConstLogic.FEATURE_SUB_DEMON,  "map_demon.png");
            loadImage(mSubFeatureToImage,  CompConstLogic.FEATURE_SUB_DEVIL,  "map_devil.png");
            loadImage(mSubFeatureToImage,  CompConstLogic.FEATURE_SUB_DINO,  "map_dino.png");
            loadImage(mSubFeatureToImage,  CompConstLogic.FEATURE_SUB_GROVE,  "map_grove.png");
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
            loadColor(CompConstLogic.TERRAIN_PLAINS, 0, 0x00FF00);
            loadColor(CompConstLogic.TERRAIN_PLAINS, 1, 0x9ACD32);
            loadColor(CompConstLogic.TERRAIN_PLAINS, 2, 0x228B22);
            loadColor(CompConstLogic.TERRAIN_FRESHWATER, 0, 0x3182bd);
            loadColor(CompConstLogic.TERRAIN_FRESHWATER, 1, 0x3182bd);
            loadColor(CompConstLogic.TERRAIN_FRESHWATER, 2, 0x3182bd);
            loadColor(CompConstLogic.TERRAIN_JUNGLE, 0, 0x00FF00);
            loadColor(CompConstLogic.TERRAIN_JUNGLE, 1, 0x9ACD32);
            loadColor(CompConstLogic.TERRAIN_JUNGLE, 2, 0x228B22);
            loadColor(CompConstLogic.TERRAIN_FOREST, 0, 0x32CD32);
            loadColor(CompConstLogic.TERRAIN_FOREST, 1, 0x98D619);
            loadColor(CompConstLogic.TERRAIN_FOREST, 2, 0xFFDF00);
            loadColor(CompConstLogic.TERRAIN_SALTWATER, 0, 0x8856a7);
            loadColor(CompConstLogic.TERRAIN_SALTWATER, 1, 0x8856a7);
            loadColor(CompConstLogic.TERRAIN_SALTWATER, 2, 0x8856a7);
            loadColor(CompConstLogic.TERRAIN_SWAMP, 0, 0x98FB98);
            loadColor(CompConstLogic.TERRAIN_SWAMP, 1, 0x00FA9A);
            loadColor(CompConstLogic.TERRAIN_SWAMP, 2, 0x00FF7F);
//            mChallengeToColor.put(0, new Color(0xFFF0F0));
//            mChallengeToColor.put(1, new Color(0xFFC0C0));
//            mChallengeToColor.put(2, new Color(0xFFA0A0));
//            mChallengeToColor.put(3, new Color(0xFF8080));
//            mChallengeToColor.put(4, new Color(0xFF6060));
//            mChallengeToColor.put(5, new Color(0xFF4040));
//            mChallengeToColor.put(6, new Color(0xFF2020));
//            mChallengeToColor.put(7, new Color(0xFF0000));
//            mChallengeToColor.put(8, new Color(0xD00000));
//            mChallengeToColor.put(9, new Color(0xB00000));
//            mChallengeToColor.put(10, new Color(0x900000));
//            mChallengeToColor.put(11, new Color(0x700000));
//            mChallengeToColor.put(12, new Color(0x500000));
//            mChallengeToColor.put(13, new Color(0x300000));
//            mChallengeToColor.put(14, new Color(0x100000));
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
    
    private void loadImage(Map<Integer,BufferedImage> map, int key, String fname) throws IOException
    {
        BufferedImage img1 = ImageIO.read(new File("C:\\Users\\IBM_ADMIN\\git\\TsaTsaTzuAlexa\\jo.audio.companions\\WebContent\\images\\"+fname));
        BufferedImage img2 = new BufferedImage(48, 48, BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 48; x++)
            for (int y = 0; y < 48; y++)
            {
                int rgb = img1.getRGB(x, y);
//                if ((rgb&0xff) != 0xff)
//                    rgb |= 0xff000000;
//                else
//                    rgb &= 0x00ffffff;
                rgb &= 0x00ffffff;
                rgb |= ((~(rgb&0xff))<<24);
                img2.setRGB(x, y, rgb);
            }
        map.put(new Integer(key), img2);
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
    
    public Color getColorPlain(SquareBean sq)
    {
        if (CompConstLogic.isWater(sq.getTerrain()))
            return Color.LIGHT_GRAY;
        return Color.WHITE;
    }
    
    public Color getTerrainColor(int terrain, int depth)
    {
        return mTerrainToColor.get(new Integer(terrain*256 + depth));
    }
    
    public Color getChallengeColor(SquareBean sq)
    {
        return mChallengeToColor.get(sq.getChallenge());
    }
    
    public BufferedImage getImage(SquareBean sq)
    {
        BufferedImage img = null;
        if (sq.getFeature() != CompConstLogic.FEATURE_NONE)
        {
            RegionBean region = GenerationLogic.getRegion(sq.getOrds());
            if (region == null)
                System.err.println("NO REGION FOR "+sq.getOrds());
            FeatureBean feature = FeatureLogic.getFeature(region, sq, null);
            if (mSubFeatureToImage.containsKey(feature.getSubType()))
                img = mSubFeatureToImage.get(feature.getSubType());
            else if (mFeatureToImage.containsKey(sq.getFeature()))
                img = mFeatureToImage.get(sq.getFeature());
        }
        if (img == null)
            img = mTerrainToImage.get(sq.getTerrain());
        return img;
    }
    
    private void loadModel()
    {
        try
        {
            FeatureLogic.isStaticFeature(new CoordBean(), null); // force loading
            mModel = ParseModelLogic.parse("resource://jo/audio/companions/slu/Companions.model");
            CompOperationBean op = new CompOperationBean();
            op.setOperation(CompOperationBean.TEXT);
            CompContextBean context = CompOperationLogic.operate(op);
            loadText(context.getTextModel());
            //loadText(TextLogic.getText());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }
    private void loadText(JSONObject textModel)
    {
        for (String lang : textModel.keySet())
        {
            JSONObject dict = JSONUtils.getObject(textModel, lang);
            for (String key : dict.keySet())
            {
                JSONArray vals = JSONUtils.getArray(dict, key);
                if (vals == null)
                    System.out.println("Why is "+lang+":"+key+" null?");
                for (int i = 0; i < vals.size(); i++)
                    mModel.addText(lang, key, (String)vals.get(i));
            }
        }

    }
    
    public String expandInserts(String txt)
    {
        String msg = ModelResolveLogic.expandInserts(mModel, "en_US", BaseUserState.RND, null, txt);
        return msg;
    }
    
    public String expandInserts(AudioMessageBean txt)
    {
        if (txt.getIdent().indexOf("{") >= 0)
            return expandInserts(txt.getIdent());
        String msg = ModelResolveLogic.resolve(mModel, "en_US", BaseUserState.RND, null, txt.getIdent(), txt.getArgs());
        return msg;
    }
    
    public Map<String,List<String>> getText(String lang)
    {
        return mModel.getText().get(lang);
    }

    public final static AlphaComposite[] mAlphaComposites = new AlphaComposite[255];

    public AlphaComposite getAlpha(int alpha)
    {
        if ((alpha < 0) || (alpha >= mAlphaComposites.length))
            return null;
        if (mAlphaComposites[alpha] == null)
            mAlphaComposites[alpha] = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha/255f);
        return mAlphaComposites[alpha];
    }
}
