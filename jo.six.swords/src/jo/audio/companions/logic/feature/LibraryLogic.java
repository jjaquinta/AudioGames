package jo.audio.companions.logic.feature;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.logic.CompIOLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;

public class LibraryLogic
{
    public static final String MD_RED_QUEEN_ENTITEMENT = "RED_QUEEN_ENTITLEMENT";
    public static final String MD_RED_QUEEN_SEGMENT = "RED_QUEEN_SEGMENT";
    public static final String MD_RED_QUEEN_CHAPTER = "RED_QUEEN_CHAPTER";
    private static final String SET_CHAPTER = "{\"expr\":\"@function(library, ?0?)\", \"falseMessage\":{\"message\":\"SETTING_TO_CHAPTER_XXX\",\"args\":[?0?]}, \"trueMessage\":{\"message\":\"YOU_HAVE_NOT_UNLOCKED_THAT_CHAPTER\"}}";
    private static final String CHECK_CHAPTER = "{\"expr\":\"@getValue(RED_QUEEN_ENTITLEMENT) <  ?0?\",\"trueMessage\":{\"message\":\"YOU_HAVE_NOT_UNLOCKED_THAT_CHAPTER\"}}";
    
    public static CompRoomBean makeLibrary(FeatureBean feature)
    {
        CompRoomBean library = FeatureLogic.getRoom("castleLibrary");
        CompRoomBean nextChapter = FeatureLogic.getRoom("castleLibraryNextChapter");
        CompRoomBean pick = FeatureLogic.getRoom("castleLibraryPick");
        
        library.setNorth(nextChapter.getID());
        nextChapter.setSouth(library.getID());
        nextChapter.setNorth(nextChapter.getID());
        feature.getRooms().add(nextChapter);
        
        library.setSouth(pick.getID());
        pick.setNorth(library.getID());
        feature.getRooms().add(pick);
        addPickers(feature, pick, CompRoomBean.DIR_SOUTH, 1, 16);
        addPickers(feature, pick, CompRoomBean.DIR_EAST, 17, 32);
        addPickers(feature, pick, CompRoomBean.DIR_WEST, 33, 42);

        return library;
    }

    private static void addPickers(FeatureBean feature, CompRoomBean parent, int dir, int low, int high)
    {
        int span = high - low + 1;
        CompRoomBean picker;
        if (span <= 4)
        {
            picker = FeatureLogic.getRoom("castleLibraryPickChapter");
            for (int d = 0; d < span; d++)
            {
                picker.setDirection(d, "castleLibraryNextChapter");
                picker.setDirectionDesc(d, new AudioMessageBean(CompanionsModelConst.TEXT_CHAPTER_XXX, low + d));
                picker.setDirectionLock(d, constitute(SET_CHAPTER, low+d));
            }
        }
        else
        {
            picker = FeatureLogic.getRoom("castleLibraryPickChapters");
            int sub = (int)Math.ceil(span/4.0);
            for (int d = 0; d < 4; d++)
            {
                int l = low + d*sub;
                if (l > high)
                    break;
                int h = l + sub - 1;
                if (h > high)
                    h = high;
                addPickers(feature, picker, d, l, h);
            }
        }
        picker.setID(picker.getID()+"_"+low+"_"+high);
        picker.getName().setArgs(new Object[] { low, high });
        picker.getDescription().setArgs(new Object[] { low, high });
        parent.setDirection(dir, picker.getID());
        parent.setDirectionLock(dir, constitute(CHECK_CHAPTER, low));
        feature.getRooms().add(picker);
    }
    
    private static JSONObject constitute(String jsonStr, int chapter)
    {
        jsonStr = jsonStr.replace("?0?", String.valueOf(chapter));
        JSONObject json = JSONUtils.readJSONString(jsonStr);
        return json;
    }
    
    //, per chapter
    private static int[] RED_QUEEN_CHAPTERS = {
            4,4,8,4,3,6,6,6,6,6,5,6,6,5,6,6,7,6,5,6,6,5,6,5,6,5,8,7,6,6,5,5,6,5,8,7,6,6,7,5,4,5,
    };

    public static Object postEnter(CompContextBean context, List<String> args)
    {
        DebugUtils.trace("LibraryLogic.postEnter("+StringUtils.listize(args)+")");
        if (args.size() <= 1)
            return false;
        String cmd = args.get(1).trim();
        int ch = IntegerUtils.parseInt(context.getUser().getMetadata().getInt(MD_RED_QUEEN_CHAPTER));
        int seg = IntegerUtils.parseInt(context.getUser().getMetadata().getInt(MD_RED_QUEEN_SEGMENT));
        int entitled = IntegerUtils.parseInt(context.getUser().getMetadata().getInt(MD_RED_QUEEN_ENTITEMENT));
        if (entitled < 1)
            entitled = 1;
        ch = normalizeChapter(ch);
        seg = normalizeSegment(ch, seg);
        if ("read".equals(cmd))
        {
            DebugUtils.trace("LibraryLogic.postEnter, reading ch="+ch+", seg="+seg);
            context.addMessage(CompanionsModelConst.TEXT_RED_QUEEN_CH_XXX_SEG_YYY, 
                    StringUtils.zeroPrefix(ch, 2), "abcdefghijklmnopqrstuvwxyz".subSequence(seg, seg+1));
        }
        else if ("next".equals(cmd))
        {
            seg++;
            if (seg >= RED_QUEEN_CHAPTERS[ch-1])
            {
                ch++;
                seg = 0;
                ch = normalizeChapter(ch);
                if (ch > entitled)
                {
                    DebugUtils.trace("LibraryLogic.postEnter, not Entitled");
                    context.addMessage(CompanionsModelConst.TEXT_YOU_HAVE_ONLY_UNLOCKED_UP_TO_CHAPTER_XXX, entitled);
                    return true;
                }
            }
            DebugUtils.trace("LibraryLogic.postEnter, next ch="+ch+", seg="+seg);
            context.getUser().getMetadata().put(MD_RED_QUEEN_CHAPTER, ch);
            context.getUser().getMetadata().put(MD_RED_QUEEN_SEGMENT, seg);
            CompIOLogic.saveUser(context.getUser());
        }
        else
        {
            ch = IntegerUtils.parseInt(cmd);
            if (ch > 0)
            {
                DebugUtils.trace("LibraryLogic.postEnter, set command, ch="+ch);
                seg = 0;
                ch = normalizeChapter(ch);
                DebugUtils.trace("LibraryLogic.postEnter, after normalize, ch="+ch);
                if (ch > entitled)
                {
                    DebugUtils.trace("LibraryLogic.postEnter, not entitled="+entitled);
                    context.addMessage(CompanionsModelConst.TEXT_YOU_HAVE_ONLY_UNLOCKED_UP_TO_CHAPTER_XXX, entitled);
                    return true;
                }
                DebugUtils.trace("LibraryLogic.postEnter, set ch="+ch+", seg="+seg+", entitled="+entitled);
                context.getUser().getMetadata().put(MD_RED_QUEEN_CHAPTER, ch);
                context.getUser().getMetadata().put(MD_RED_QUEEN_SEGMENT, seg);
                CompIOLogic.saveUser(context.getUser());
            }
            else
            {
                DebugUtils.trace("LibraryLogic.postEnter, unknown cmd='"+cmd+"'");
                return true;
            }
        }
        return false;
    }

    public static int normalizeSegment(int ch, int seg)
    {
        if (seg < 0)
            seg = 0;
        else if (seg >= RED_QUEEN_CHAPTERS[ch-1])
            seg = 0;
        return seg;
    }

    public static int normalizeChapter(int ch)
    {
        if (ch <= 0)
            ch = 1;
        else if (ch > RED_QUEEN_CHAPTERS.length)
            ch = 1;
        return ch;
    }

    public static boolean discoverKey(CompContextBean context)
    {
        int entitled = IntegerUtils.parseInt(context.getUser().getMetadata().getInt(MD_RED_QUEEN_ENTITEMENT));
        DebugUtils.trace("LibraryLogic.discoverKey(entitled="+entitled+")");
        if (entitled > RED_QUEEN_CHAPTERS.length)
            return false;
        entitled++;
        context.getUser().getMetadata().put(MD_RED_QUEEN_ENTITEMENT, entitled);
        context.addMessage(CompanionsModelConst.TEXT_UNLOCK_XXX, entitled);
        CompIOLogic.saveUser(context.getUser());
        return true;
    }
}
