package jo.audio.thieves.slu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.util.utils.obj.IntegerUtils;

public class ThievesModelConst
{
    public static final String TEXT_NORTH = "NORTH";
    public static final String TEXT_NORTHEAST = "NORTHEAST";
    public static final String TEXT_NORTHWEST = "NORTHWEST";
    public static final String TEXT_SOUTH = "SOUTH";
    public static final String TEXT_SOUTHEAST = "SOUTHEAST";
    public static final String TEXT_SOUTHWEST = "SOUTHWEST";
    public static final String TEXT_EAST = "EAST";
    public static final String TEXT_WEST = "WEST";
    public static final String TEXT_UP = "UP";
    public static final String TEXT_DOWN = "DOWN";

    /*
	// intent constants
	public static final String INTENT_CANCEL = "cancel";
	public static final String INTENT_NO = "no";
	public static final String INTENT_HELP = "help";
	public static final String INTENT_MOVE = "move";
	public static final String INTENT_STOP = "stop";
	public static final String INTENT_MORE = "more";
	public static final String INTENT_YES = "yes";
	public static final String INTENT_REPEAT = "repeat";
	public static final String INTENT_ABOUT = "about";
	public static final String INTENT_LOOK = "look";
	// text constants
	public static final String TEXT_ = "";
	public static final String TEXT_ALEXA_IF_YOU_HAVE_QUESTIONS_OR_SUGGESTIONS = "ALEXA If you have questions or suggestions";
	public static final String TEXT_ASSISTANT_IF_YOU_HAVE_QUESTIONS_OR_SUGGESTIONS = "ASSISTANT If you have questions or suggestions";
	public static final String TEXT_AT_THE_END_OF_THIS_MESSAGE_YOU_WILL_HEAR_THIS_SHORT_TONE = "At the end of this message you will hear this short tone";
	public static final String TEXT_BRIDGE_NAME = "BRIDGE_NAME";
	public static final String TEXT_BENEATH_YOU_SWIRL_THE_DARK_WATERS_OF_THE_MIGHTY_XXX = "Beneath you swirl the dark waters of the mighty XXX";
	public static final String TEXT_CITY_NAME = "CITY_NAME";
	public static final String TEXT_DAWN = "DAWN";
	public static final String TEXT_DUSK = "DUSK";
	public static final String TEXT_ERROR_CANT_MOVE_FROM_INTERSECTION_XXX_TO_INTERSECTION_YYY = "ERROR Cant move from intersection XXX to intersection YYY";
	public static final String TEXT_ERROR_CANT_MOVE_FROM_INTERSECTION_XXX_TO_STREET_YYY = "ERROR Cant move from intersection XXX to street YYY";
	public static final String TEXT_ERROR_CANT_MOVE_FROM_STREET_XXX_TO_INTERSECTION_YYY = "ERROR Cant move from street XXX to intersection YYY";
	public static final String TEXT_ERROR_CANT_MOVE_FROM_STREET_XXX_TO_STREET_YYY = "ERROR Cant move from street XXX to street YYY";
	public static final String TEXT_ERROR_UNKNOWN_LOCATION_XXX = "ERROR Unknown location XXX";
	public static final String TEXT_ERROR_YOU_ARE_ALREADY_AT_XXX = "ERROR You are already at XXX";
	public static final String TEXT_FORUM = "FORUM";
	public static final String TEXT_FROM_A_STREET_YOU_CAN_SAY_UP_OR_DOWN_TO_GO_UP_OR_DOWN_THE_STREE_TO_THE_INTERSECTION_IN_THAT_DIRECTION = "From a street you can say up or down to go up or down the stree to the intersection in that direction";
	public static final String TEXT_FROM_AN_INTERESECTION_YOU_CAN_SAY_THE_NAME_OF_ONE_OF_THE_STREETS_TO_MOVE_DOWN_IT = "From an interesection you can say the name of one of the streets to move down it";
	public static final String TEXT_HELP_BASE_FIVE = "HELP_BASE_FIVE";
	public static final String TEXT_HELP_BASE_FOUR = "HELP_BASE_FOUR";
	public static final String TEXT_HELP_BASE_ONE = "HELP_BASE_ONE";
	public static final String TEXT_HELP_BASE_THREE = "HELP_BASE_THREE";
	public static final String TEXT_HELP_BASE_TWO = "HELP_BASE_TWO";
	public static final String TEXT_I_DON_T_SEE_HOW_TO_GO_XXX_FROM_HERE = "I don't see how to go XXX from here";
	public static final String TEXT_I_DONT_KNOW_WHAT_OR_WHO_XXX_IS = "I dont know what or who XXX is";
	public static final String TEXT_INTRO_SOUND = "INTRO_SOUND";
	public static final String TEXT_INTRO_TO_THIEVES = "INTRO_TO_THIEVES";
	public static final String TEXT_IM_SORRY_I_DONT_HAVE_ANY_MORE_INFORMATION_FOR_YOU_ON_THAT_SUBJECT = "Im sorry I dont have any more information for you on that subject";
	public static final String TEXT_LANDMARK_NAME = "LANDMARK_NAME";
	public static final String TEXT_MIDNIGHT = "MIDNIGHT";
	public static final String TEXT_MOON_IS_BELOW_THE_HORIZON = "MOON_IS_BELOW_THE_HORIZON";
	public static final String TEXT_MOON_IS_DECLINING = "MOON_IS_DECLINING";
	public static final String TEXT_MOON_IS_OVERHEAD = "MOON_IS_OVERHEAD";
	public static final String TEXT_MOON_IS_RISING = "MOON_IS_RISING";
	public static final String TEXT_MOON_IS_RISING_OVER_THE_HORIZON = "MOON_IS_RISING_OVER_THE_HORIZON";
	public static final String TEXT_MOON_IS_SETTING = "MOON_IS_SETTING";
	public static final String TEXT_MOON_PHASE_0 = "MOON_PHASE_0";
	public static final String TEXT_MOON_PHASE_1 = "MOON_PHASE_1";
	public static final String TEXT_MOON_PHASE_2 = "MOON_PHASE_2";
	public static final String TEXT_MOON_PHASE_3 = "MOON_PHASE_3";
	public static final String TEXT_MOON_PHASE_4 = "MOON_PHASE_4";
	public static final String TEXT_MOON_PHASE_5 = "MOON_PHASE_5";
	public static final String TEXT_MOON_PHASE_6 = "MOON_PHASE_6";
	public static final String TEXT_MOON_PHASE_7 = "MOON_PHASE_7";
	public static final String TEXT_MORE_SOUND = "MORE_SOUND";
	public static final String TEXT_NOON = "NOON";
	public static final String TEXT_PRE_DAWN = "PRE_DAWN";
	public static final String TEXT_PRE_DUSK = "PRE_DUSK";
	public static final String TEXT_PROMPT_TIME = "PROMPT_TIME";
	public static final String TEXT_QUAY_NAME = "QUAY_NAME";
	public static final String TEXT_RIVER_NAME = "RIVER_NAME";
	public static final String TEXT_SENTENCE = "SENTENCE";
	public static final String TEXT_STREET_NAME = "STREET_NAME";
	public static final String TEXT_TIME_AFTERNOON = "TIME_AFTERNOON";
	public static final String TEXT_TIME_MORNING = "TIME_MORNING";
	public static final String TEXT_TIME_NIGHT = "TIME_NIGHT";
	public static final String TEXT_TO_ENTER_A_HOUSE_ON_THIS_STREET = "TO_ENTER_A_HOUSE_ON_THIS_STREET";
	public static final String TEXT_TRY_SAYING_XXX = "TRY_SAYING_XXX";
	public static final String TEXT_THANK_YOU_FOR_PLAYING = "Thank you for playing";
	public static final String TEXT_THE_HOUSES_HERE_ARE_THE_BEST_IN_THE_CITY = "The houses here are the best in the city";
	public static final String TEXT_THE_HOUSES_HERE_ARE_THE_WORST_IN_THE_CITY = "The houses here are the worst in the city";
	public static final String TEXT_THE_HOUSES_ON_THIS_STREET_ARE_MIDDLE_OF_THE_ROAD = "The houses on this street are middle of the road";
	public static final String TEXT_THE_HOUSES_ON_THIS_STREET_ARE_PRETTY_RUN_DOWN = "The houses on this street are pretty run down";
	public static final String TEXT_THE_HOUSES_ON_THIS_STREET_ARE_VERY_NICE = "The houses on this street are very nice";
	public static final String TEXT_THE_WEATHER_IS_PLEASANT = "The weather is pleasant";
	public static final String TEXT_TO_THE_XXX_YOU_SEE_YYY = "To the XXX you see YYY";
	public static final String TEXT_UNKNOWN_OPERATION_XXX = "Unknown operation XXX";
	public static final String TEXT_WELCOME_TO_THIEVES = "WELCOME_TO_THIEVES";
	public static final String TEXT_WTF = "WTF";
	public static final String TEXT_WTF_WHATIS = "WTF_WHATIS";
	public static final String TEXT_XXX_AT_YYY = "XXX at YYY";
	public static final String TEXT_XXX_HOUSES_LINE_THIS_STREET = "XXX houses line this street";
	public static final String TEXT_XXX_LARGE_WAREHOUSES_LINE_THE_QUAYSIDE = "XXX large warehouses line the quayside";
	public static final String TEXT_XXX_RUNS_TO_THE_YYY = "XXX runs to the YYY";
	public static final String TEXT_XXX_RUNS_UP_TO_THE_YYY_AND_DOWN_TO_THE_ZZZ = "XXX runs up to the YYY and down to the ZZZ";
	public static final String TEXT_YOU_ENTER_NUMBER_XXX_YYY = "YOU_ENTER_NUMBER_XXX_YYY";
	public static final String TEXT_YOU_LEAVE_NUMBER_XXX_AND_RETURN_TO_YYY = "YOU_LEAVE_NUMBER_XXX_AND_RETURN_TO_YYY";
	public static final String TEXT_YOU_ACHIEVEMENTS_ARE_XXX = "You achievements are XXX";
	public static final String TEXT_YOU_ARE_AT_THE_INTERSECTION_OF_XXX = "You are at the intersection of XXX";
	public static final String TEXT_YOU_ARE_ON_XXX = "You are on XXX";
	public static final String TEXT_YOU_ARE_RANKED_XXX = "You are ranked XXX";
	public static final String TEXT_YOU_CAN_MOVE_BY_STATING_THE_DIRECTION_ALONG_THE_STREET_YOU_WISH_TO_GO_TO = "You can move by stating the direction along the street you wish to go to";
	public static final String TEXT_YOU_CAN_MOVE_BY_STATING_THE_DIRECTION_OF_THE_STREET_YOU_WISH_TO_GO_TO = "You can move by stating the direction of the street you wish to go to";
	public static final String TEXT_YOU_CAN_MOVE_BY_STATING_THE_NAME_OF_THE_STREET_YOU_WISH_TO_GO_TO = "You can move by stating the name of the street you wish to go to";
	public static final String TEXT_YOU_CAN_SAY_UP_OR_DOWN_TO_MOVE_TO_THAT_END_OF_THE_STREET = "You can say up or down to move to that end of the street";
	public static final String TEXT_YOU_CAN_SEE_A_CLOUD_SHAPED = "You can see a cloud shaped";
	public static final String TEXT_YOU_HAVE_TRAVELED_A_TOTAL_OF_XXX_MILES = "You have traveled a total of XXX miles";
	public static final String TEXT_YOU_HAVE_VISITED_XXX_LOCATIONS_AND_CARRIED_A_MAXIMUM_OF_YYY_GOLD_PIECES = "You have visited XXX locations and carried a maximum of YYY gold pieces";
	public static final String TEXT_YOU_MOVE_XXX_DOWN_YYY = "You move XXX down YYY";
	public static final String TEXT_YOU_MOVE_XXX_UP_YYY = "You move XXX up YYY";
	public static final String TEXT_YOU_MOVE_DOWN_XXX = "You move down XXX";
	public static final String TEXT_YOU_MOVE_UP_XXX = "You move up XXX";
	public static final String TEXT_BANKER_BRONZE = "banker_BRONZE";
	public static final String TEXT_BANKER_GOLD = "banker_GOLD";
	public static final String TEXT_BANKER_HELP = "banker_HELP";
	public static final String TEXT_BANKER_PLATINUM = "banker_PLATINUM";
	public static final String TEXT_BANKER_SILVER = "banker_SILVER";
	public static final String TEXT_RANGER_BRONZE = "ranger_BRONZE";
	public static final String TEXT_RANGER_GOLD = "ranger_GOLD";
	public static final String TEXT_RANGER_HELP = "ranger_HELP";
	public static final String TEXT_RANGER_PLATINUM = "ranger_PLATINUM";
	public static final String TEXT_RANGER_SILVER = "ranger_SILVER";
	public static final String TEXT_TRAVELLER_BRONZE = "traveller_BRONZE";
	public static final String TEXT_TRAVELLER_GOLD = "traveller_GOLD";
	public static final String TEXT_TRAVELLER_HELP = "traveller_HELP";
	public static final String TEXT_TRAVELLER_PLATINUM = "traveller_PLATINUM";
	public static final String TEXT_TRAVELLER_SILVER = "traveller_SILVER";
	public static final String TEXT_VISIT_BRONZE = "visit_BRONZE";
	public static final String TEXT_VISIT_GOLD = "visit_GOLD";
	public static final String TEXT_VISIT_HELP = "visit_HELP";
	public static final String TEXT_VISIT_PLATINUM = "visit_PLATINUM";
	public static final String TEXT_VISIT_SILVER = "visit_SILVER";
     */
    
    private static Map<String,String[]> mModel = null;
    
    private static void loadModel()
    {
        if (mModel != null)
            return;
        try
        {
            JSONObject json = JSONUtils.readJSON("resource://jo/audio/thieves/slu/place_names.model");
            mModel = new HashMap<>();
            JSONObject model = JSONUtils.getObject(json, "text.en_US");
            for (String key : model.keySet())
            {
                JSONArray values = JSONUtils.getArray(model, key);
                String[] texts = new String[values.size()];
                for (int i = 0; i < values.size(); i++)
                    texts[i] = values.get(i).toString();
                mModel.put(key, texts);
            }
        }
        catch (IOException e)
        {
            throw new IllegalStateException(e);
        }
    }
    
    public static String[] getTexts(String id)
    {
        loadModel();
        return mModel.get(id);
    }
    
    public static String getText(String id, int 
            off)
    {
        String[] texts = getTexts(id);
        if (texts == null)
            return null;
        return texts[off%texts.length];
    }
    
    public static String expand(String inbuf)
    {
        if (inbuf == null)
            return null;
        StringBuffer outbuf = new StringBuffer();
        for (;;)
        {
            int o = inbuf.indexOf("{{");
            if (o < 0)
            {
                outbuf.append(inbuf);
                break;
            }
            outbuf.append(inbuf.substring(0,  o));
            inbuf = inbuf.substring(o + 2);
            o = inbuf.indexOf("}}");
            if (o < 0)
            {
                outbuf.append("{{"+inbuf);
                break;
            }
            String id = inbuf.substring(0, o);
            inbuf = inbuf.substring(o + 2);
            o = id.indexOf("#");
            if (o < 0)
                outbuf.append(getText(id, 0));
            else
                outbuf.append(getText(id.substring(0, o), IntegerUtils.parseInt(id.substring(o + 1))));
        }
        return outbuf.toString();
    }
    
}
