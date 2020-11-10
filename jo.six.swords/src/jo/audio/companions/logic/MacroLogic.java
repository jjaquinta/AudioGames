package jo.audio.companions.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.GeoBean;
import jo.audio.companions.data.LocationBean;
import jo.audio.companions.logic.feature.LibraryLogic;
import jo.audio.companions.logic.feature.PantheonLogic;
import jo.audio.companions.logic.feature.dungeon.DungeonCommandLogic;
import jo.audio.companions.logic.feature.town.HouseLogic;
import jo.audio.companions.logic.feature.town.TempleLogic;
import jo.audio.util.BaseUserState;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.BeanUtils;
import jo.util.utils.DebugUtils;
import jo.util.utils.ObjectUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.LongUtils;
import jo.util.utils.obj.StringUtils;

public class MacroLogic
{

    public static Object executeSimple(CompContextBean context, Object script)
    {
        log("Executing simple (2) script of type "+script.getClass().getName());
        Object ret;
        if (script instanceof JSONArray)
            ret = executeSimple(context, (JSONArray)script);
        else if (script instanceof JSONObject)
            ret = executeSimple(context, (JSONObject)script);
        else if (script instanceof Number)
            ret = script;
        else if (script instanceof Boolean)
            ret = script;
        else
            ret = executeSimple(context, script.toString());
        log("Execution complete (2) of simple script of type "+script.getClass().getName()+", return="+ret);
        return ret;
    }

    public static Object executeSimple(CompContextBean context, JSONObject script)
    {
        log("Executing simple (3) "+script.toJSONString());
        Object ret = null;
        if (script.containsKey("if"))
        {
            log("Executing simple if "+script.get("if"));
            boolean cond = executeConditional(context, script.get("if"));
            log("Executing simple if cond="+cond);
            if (cond && script.containsKey("then"))
            {
                log("Executing simple if conditional true, then="+script.get("then"));
                ret = executeSimple(context, script.get("then"));
            }
            else if (!cond && script.containsKey("else"))
            {
                log("Executing simple if conditional false, else="+script.get("else"));
                ret = executeSimple(context, script.get("else"));
            }
        }
        else if (script.containsKey("cmd"))
        {
            log("Executing simple cmd "+script.get("cmd"));
            ret = executeSimple(context, script.get("cmd"));
        }
        log("Executed simple (3) "+script.toJSONString()+"="+ret);
        return ret;
    }

    public static Object executeSimple(CompContextBean context, String cmd)
    {
        log("Executing simple (4) script "+cmd);
        List<String> args = new ArrayList<>();
        int o = cmd.indexOf('(');
        if (o >= 0)
        {
            char[] argChars = cmd.toCharArray();
            StringBuffer arg = new StringBuffer();
            boolean inQuote = false;
            char quoteType = ' ';
            for (int i = o + 1; i < argChars.length; i++)
            {
                if (!inQuote)
                {
                    if ((argChars[i] == ',') || (argChars[i] == ')'))
                    {
                        args.add(arg.toString());
                        arg.setLength(0);
                    }
                    else if (argChars[i] == '\'')
                    {
                        inQuote = true;
                        quoteType = '\'';
                    }
                    else if (argChars[i] == '\"')
                    {
                        inQuote = true;
                        quoteType = '\"';
                    }
                    else
                        arg.append(argChars[i]);
                }
                else
                {
                    if (argChars[i] == quoteType)
                    {
                        inQuote = false;
                    }
                    else
                        arg.append(argChars[i]);
                }
            }
            if (arg.length() > 0)
                args.add(arg.toString());
            cmd = cmd.substring(0, o);
        }
        return executeCommand(context, cmd, args);
    }

    public static Object executeSimple(CompContextBean context, JSONArray script)
    {
        log("Executing simple (1)="+script.toJSONString());
        Object ret = null;
        for (Object o : script)
            ret = executeSimple(context, o);
        log("Executied simple (1) "+script.toJSONString()+", ret="+ret);
        return ret;
    }

    public static Object executeCommand(CompContextBean context, String cmd, List<String> args)
    {
        log("Executing command="+cmd+", args="+args);
        switch (cmd.toLowerCase())
        {
            case "respond":
            {
                String id = args.get(0);
                args.remove(0);
                AudioMessageBean msg = new AudioMessageBean(id, args.toArray());
                log("responding with '"+msg+"'");
                context.addMessage(msg);
                return null;
            }
            case "increment":
                if (args.size() > 0)
                {
                    long v = JSONUtils.getLong(context.getUser().getMetadata(), args.get(0));
                    if (args.size() > 1)
                        v += LongUtils.parseLong(args.get(1));
                    else
                        v++;
                    context.getUser().getMetadata().put(args.get(0), v);
                    return v;
                }
                return null;
            case "decrement":
                if (args.size() > 0)
                {
                    long v = JSONUtils.getLong(context.getUser().getMetadata(), args.get(0));
                    if (args.size() > 1)
                        v -= LongUtils.parseLong(args.get(1));
                    else
                        v--;
                    context.getUser().getMetadata().put(args.get(0), v);
                    return v;
                }
                return null;
            case "getvalue":
            {
                Object val = null;
                if (args.size() > 0)
                {
                    String arg = args.get(0);
                    if (arg.startsWith("$"))
                        val = BeanUtils.get(context, arg.substring(1));
                    else
                        val = JSONUtils.get(context.getUser().getMetadata(), arg);
                }
                log("  return "+val);
                return val;
            }
            case "setvalue":
            {
                Object val = null;
                if (args.size() >= 2)
                {
                    val = args.get(1);
                    String arg = args.get(0);
                    if (arg.startsWith("$"))
                        BeanUtils.set(context, arg.substring(1), val);
                    else
                        context.getUser().getMetadata().put(arg, val);
                }
                log("  return "+val);
                return val;
            }
            case "cureall":
                return UserLogic.fullyCureCompanions(context, context.getUser());
            case "healall":
                return UserLogic.fullyHealCompanions(context, context.getUser());
            case "tag":
                if (args.size() > 1)
                {
                    String listName = args.get(0).toString();
                    String listItem = args.get(1).toString();
                    String list = JSONUtils.getString(context.getUser().getMetadata(), listName);
                    list = ResponseUtils.addToList(list, listItem);
                    context.getUser().getMetadata().put(listName, list);
                    return list;
                }
                return null;
            case "give":
                int amnt = (args.size() > 1) ? IntegerUtils.parseInt(args.get(1)) : 1;
                CompItemInstanceBean item = ItemLogic.createInstance(args.get(0), amnt);
                if (args.size() > 2)
                    item.setName(args.get(2));
                context.getUser().getItems().add(0, item);
                return null;
            case "divineblessing":
            {
                int god = IntegerUtils.parseInt(args.get(0));
                PantheonLogic.divineBlessing(context, god);
                return null;
            }
            case "teleport":
                return doTeleport(context, args);
            case "dimensiongo":
                return doDimensionGo(context, args);
            case "dimensionrandom":
                return doDimensionRandom(context, args);
            case "dimensionreturn":
                return doDimensionReturn(context, args);
            case "function":
                switch (args.get(0).toLowerCase())
                {
                    case "patronize":
                    {
                        int god = IntegerUtils.parseInt(args.get(1));
                        TempleLogic.patronize(context, god);
                        break;
                    }
                    case "tithe":
                    {
                        int god = IntegerUtils.parseInt(args.get(1));
                        TempleLogic.tithe(context, god);
                        break;
                    }
                    case "templeprestigereport":
                    {
                        int god = IntegerUtils.parseInt(args.get(1));
                        TempleLogic.prestige(context, god);
                        break;
                    }
                    case "templemissionstart":
                    {
                        int god = IntegerUtils.parseInt(args.get(1));
                        TempleLogic.startMission(context, god);
                        break;
                    }
                    case "templemissionend":
                    {
                        int god = IntegerUtils.parseInt(args.get(1));
                        TempleLogic.endMission(context, god);
                        break;
                    }
                    case "houseentrance":
                    {
                        HouseLogic.postEnterEntrance(context);
                        break;
                    }
                    case "househall":
                    {
                        HouseLogic.postEnterHall(context);
                        break;
                    }
                    case "dungeon":
                    {
                        DungeonCommandLogic.postEnter(context, args);
                        break;
                    }
                    case "library":
                    {
                        return LibraryLogic.postEnter(context, args);
                    }
                    case "buy":
                    {
                        String itemID = args.get(1);
                        int amount = 1;
                        if (args.size() > 2)
                            amount = IntegerUtils.parseInt(args.get(2));
                        PurchaseLogic.buy(context, itemID, amount);
                        return Boolean.TRUE;
                    }
                    default:
                        log("unknown function '"+args.get(0).toLowerCase()+"'");
                        break;
                }
                return null;
            default:
                log("unknown command");
        }
        return null;
    }

    public static boolean executeConditional(CompContextBean context, Object script)
    {
        log("Executing conditionalObj "+script);
        boolean ret;
        if (script instanceof JSONArray)
            ret = executeConditional(context, (JSONArray)script);
        else if (script instanceof JSONObject)
            ret = BooleanUtils.parseBoolean(executeSimple(context, (JSONObject)script));
        else
            ret = BooleanUtils.parseBoolean(executeSimple(context, script.toString()));
        log("Executed conditionalObj "+script+" => "+ret);
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static boolean executeConditional(CompContextBean context, JSONArray script)
    {
        if (script.size() == 0)
            return false;
        log("Executing conditionalArr "+script);
        List<Object> values = new ArrayList<>();
        values.addAll(script);
        for (int i = 0; i < values.size(); i++)
            if ("(".equals(values.get(i)))
                resolveParenthesis(context, values, i);
        Object ret = executeConditional(context, values, 0, values.size());
        boolean r = BooleanUtils.parseBoolean(ret);
        log("Executed conditionalArr "+script+" => "+ret);
        return r;
    }
    
    private static Set<String> BI_OP = new HashSet<>();
    static
    {
        BI_OP.add("or");BI_OP.add("|");BI_OP.add("||");
        BI_OP.add("and");BI_OP.add("&");BI_OP.add("&&");
        BI_OP.add("<");
        BI_OP.add(">");
        BI_OP.add("<=");
        BI_OP.add(">=");
        BI_OP.add("=");BI_OP.add("==");
        BI_OP.add("!=");BI_OP.add("<>");        
    }

    private static Object executeConditional(CompContextBean context, List<Object> values, int start, int end)
    {
        log("Executing conditionalExpr");
        Object ret = executeSimple(context, values.get(start));
        log("Executing conditionalExpr, init='"+ret+"'");
        for (int i = start + 1; i < end; i += 2)
        {
            Object val = values.get(i);
            log("Executing conditionalExpr, op="+val);
            if (BI_OP.contains(val))
            {
                Object arg2 = values.get(i+1);
                Object arg2val = executeSimple(context, arg2);
                log("Executing conditionalExpr, eval arg2 "+arg2+" => '"+arg2val+"'");
                if ("or".equals(val) || "|".equals(val) || "||".equals(val))
                    ret = BooleanUtils.parseBoolean(ret) || BooleanUtils.parseBoolean(arg2val);
                else if ("and".equals(val) || "&".equals(val) || "&&".equals(val))
                    ret = BooleanUtils.parseBoolean(ret) && BooleanUtils.parseBoolean(arg2val);
                else if ("<".equals(val))
                    ret = DoubleUtils.parseDouble(ret) < DoubleUtils.parseDouble(arg2val);
                else if (">".equals(val))
                    ret = DoubleUtils.parseDouble(ret) > DoubleUtils.parseDouble(arg2val);
                else if ("<=".equals(val))
                    ret = DoubleUtils.parseDouble(ret) <= DoubleUtils.parseDouble(arg2val);
                else if (">=".equals(val))
                    ret = DoubleUtils.parseDouble(ret) >= DoubleUtils.parseDouble(arg2val);
                else if ("=".equals(val) || "==".equals(val))
                {
                    if ((ret instanceof Boolean) && (arg2val instanceof Boolean))
                        ret = ObjectUtils.equals(ret, arg2val);
                    else
                        ret = LongUtils.parseLong(ret) == LongUtils.parseLong(arg2val);
                }
                else if ("!=".equals(val) || "<>".equals(val))
                {
                    if ((ret instanceof Boolean) && (arg2val instanceof Boolean))
                        ret = !ObjectUtils.equals(ret, arg2val);
                    else
                        ret = LongUtils.parseLong(ret) != LongUtils.parseLong(arg2val);
                }
            }
            log("Executing conditionalExpr, after op='"+ret+"'");
        }
        log("Executed conditionalExpr => '"+ret+"'");
        return ret;

    }
    
    private static void resolveParenthesis(CompContextBean context, List<Object> values, int start)
    {
        int end = start;
        while (++end < values.size())
            if ("(".equals(values.get(end)))
                resolveParenthesis(context, values, end);
            else if (")".equals(values.get(end)))
                break;
        Object val = executeConditional(context, values, start + 1, end);
        while (end-- > start)
            values.remove(start);
        values.add(start, val);
    }

    private static Object doTeleport(CompContextBean context, List<String> args)
    {
        String location = null;
        if ((args.size() == 1) && args.get(0).startsWith("$"))
        {
            String roomID = args.get(0).substring(1);
            log("MacroLogic.doTeleport resolving as roomID="+roomID);
            // look for exact match
            for (CompRoomBean r : context.getFeature().getFeature().getRooms())
            {
                if (r.getID().equals(roomID))
                {
                    LocationBean l = new LocationBean(context.getLocation());
                    l.setRoomID(r.getID());
                    location = l.toString();
                    roomID = null;
                    log("MacroLogic.doTeleport found room="+r.getName()+", l="+location);
                    break;
                }
            }
            // look for prefix
            if (roomID != null)
            {
                log("MacroLogic.doTeleport did not find room, looking with prefix");
                for (CompRoomBean r : context.getFeature().getFeature().getRooms())
                {
                    if (r.getID().startsWith(roomID))
                    {
                        LocationBean l = new LocationBean(context.getLocation());
                        l.setRoomID(r.getID());
                        location = l.toString();
                        roomID = null;
                        log("MacroLogic.doTeleport found room="+r.getID()+"/"+r.getName()+", l="+location);
                        break;
                    }
                }
            }
            if (roomID != null)
            {
                log("MacroLogic.doTeleport unable to locate '"+roomID+"', aborting");
                return null;
            }
        }
        else
        {
            location = args.get(0);
            for (int i = 1; i < args.size(); i++)
                location += ","+args.get(i);
        }
        log("MacroLogic.doTeleport move to '"+location+"'");
        //log("MacroLogic.doTeleport1 # messages="+context.getMessages().size());
        LocationBean l = new LocationBean(location);
        if (StringUtils.isTrivial(l.getRoomID()))
            UserLogic.moveToStrategic(context, context.getUser(), l);
        else
            UserLogic.moveToTactical(context, context.getUser(), l);
        //log("MacroLogic.doTeleport2 # messages="+context.getMessages().size());
        CompIOLogic.saveUser(context.getUser());
        //log("MacroLogic.doTeleport3 # messages="+context.getMessages().size());
        CompOperationLogic.fillContext(context);
        //log("MacroLogic.doTeleport4 # messages="+context.getMessages().size());
        return l;
    }

    private static Object doDimensionGo(CompContextBean context, List<String> args)
    {
        GeoBean currentLocation = context.getLocation();
        int newDimension = IntegerUtils.parseInt(args.get(0));
        LocationBean newLocation;
        if (context.getUser().getMetadata().containsKey("location_dim_"+newDimension))
            newLocation = new LocationBean((String)context.getUser().getMetadata().get("location_dim_"+newDimension));
        else
            newLocation = new LocationBean(CompConstLogic.INITIAL_DIM_LOCATION_X[newDimension],
                CompConstLogic.INITIAL_DIM_LOCATION_Y[newDimension],
                newDimension);
        return doDimensionMove(context, currentLocation, newLocation);
    }
    
    private static LocationBean doDimensionMove(CompContextBean context, LocationBean currentLocation, LocationBean newLocation)
    {
        log("MacroLogic.doDimensionGo move to '"+newLocation+"'");
        context.getUser().getMetadata().put("location_dim_"+currentLocation.getZ(), currentLocation.toString());
        if (context.getUser().getMetadata().containsKey("location_dim_stack"))
            context.getUser().getMetadata().put("location_dim_stack", context.getUser().getMetadata().get("location_dim_stack")+","+currentLocation.getZ());
        else
            context.getUser().getMetadata().put("location_dim_stack", String.valueOf(currentLocation.getZ()));
        UserLogic.moveToStrategic(context, context.getUser(), newLocation);
        //log("MacroLogic.doDimensionGo2 # messages="+context.getMessages().size());
        CompIOLogic.saveUser(context.getUser());
        //log("MacroLogic.doDimensionGo3 # messages="+context.getMessages().size());
        CompOperationLogic.fillContext(context);
        //log("MacroLogic.doDimensionGo4 # messages="+context.getMessages().size());
        return newLocation;
    }

    private static Object doDimensionRandom(CompContextBean context, List<String> args)
    {
        GeoBean currentLocation = context.getLocation();
        int newDimension = IntegerUtils.parseInt(args.get(BaseUserState.RND.nextInt(args.size())));
        LocationBean newLocation = new LocationBean(
                CompConstLogic.INITIAL_DIM_LOCATION_X[newDimension]+BaseUserState.RND.nextInt(CompConstLogic.SQUARES_PER_DOMAIN)-CompConstLogic.SQUARES_PER_DOMAIN/2,
                CompConstLogic.INITIAL_DIM_LOCATION_Y[newDimension]+BaseUserState.RND.nextInt(CompConstLogic.SQUARES_PER_DOMAIN)-CompConstLogic.SQUARES_PER_DOMAIN/2,
                newDimension);
        return doDimensionMove(context, currentLocation, newLocation);
    }

    private static Object doDimensionReturn(CompContextBean context, List<String> args)
    {
        GeoBean currentLocation = context.getLocation();
        String stack = (String)context.getUser().getMetadata().get("location_dim_stack");
        int newDimension = 0;
        if (StringUtils.isTrivial(stack))
            newDimension = IntegerUtils.parseInt(args.get(0));
        else
        {
            int o = stack.lastIndexOf(',');
            if (o < 0)
            {
                newDimension = IntegerUtils.parseInt(stack);
                stack = "";
            }
            else
            {
                newDimension = IntegerUtils.parseInt(stack.substring(o + 1));
                stack = stack.substring(0, o);
            }
        }
        LocationBean newLocation;
        if (context.getUser().getMetadata().containsKey("location_dim_"+newDimension))
            newLocation = new LocationBean((String)context.getUser().getMetadata().get("location_dim_"+newDimension));
        else
            newLocation = new LocationBean(CompConstLogic.INITIAL_DIM_LOCATION_X[newDimension],
                CompConstLogic.INITIAL_DIM_LOCATION_Y[newDimension],
                newDimension);
        newLocation.setRoomID(null);
        return doDimensionMove(context, currentLocation, newLocation);
    }
    
    private static void log(String msg)
    {
        DebugUtils.trace(msg);
    }
}
