package jo.audio.companions.logic;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompLogBean;
import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.LocationBean;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.BeanUtils;
import jo.util.utils.DebugUtils;
import jo.util.utils.MathUtils;
import jo.util.utils.ObjectUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.LongUtils;
import jo.util.utils.obj.StringUtils;

public class RoomLogic
{
    private static final Map<String,Long> mRoomFightTimeout = new HashMap<>();

    public static void checkRoomEffect(CompContextBean context, CompUserBean user, CompRoomBean room)
    {
        DebugUtils.trace("RoomLogic.checkRoomEffect");
        if (room == null)
            return;
        JSONObject params = room.getParams();
        if (params == null)
            return;
        JSONArray effects = (JSONArray)params.get("effects");
        if (effects == null)
            return;
        DebugUtils.trace("RoomLogic.checkRoomEffect "+effects.size()+" effects");
        for (int i = 0; i < effects.size(); i++)
        {
            JSONObject effect = (JSONObject)effects.get(i);
            String id = effect.getString("id");
            DebugUtils.trace("RoomLogic.checkRoomEffect #"+i+" = "+id);
            switch (id)
            {
                case "message":
                    doMessage(context, user, room, effect);
                    break;
                case "response":
                    doResponse(context, user, room, effect);
                    break;
                case "teleport":
                    doTeleport(context, user, room, effect);
                    break;
                case "debit":
                    doDebit(context, user, room, effect);
                    break;
                case "voice":
                    doVoice(context, user, room, effect);
                    break;
                case "rate":
                    doRate(context, user, room, effect);
                    break;
                case "pitch":
                    doPitch(context, user, room, effect);
                    break;
                case "set":
                    doSet(context, user, room, effect);
                    break;
                case "resurrect":
                    doResurrect(context, user, room, effect);
                    break;
                default:
                    DebugUtils.trace("Unknown room effect '"+effect+"'");
                    break;
            }
        }
    }
    
    private static void doDebit(CompContextBean context, CompUserBean user, CompRoomBean room, JSONObject params)
    {
        int amount = IntegerUtils.parseInt(params.get("amount"));
        if (amount == 0)
            return;
        ExperienceLogic.addGold(user, -amount);
    }
    
    private static void doVoice(CompContextBean context, CompUserBean user, CompRoomBean room, JSONObject params)
    {
        String voice = params.getString("voice");
        if (voice.equals("reset"))
            user.setTaleStreamVoice(null);
        else
            user.setTaleStreamVoice(voice);
        DebugUtils.trace("RoomLogic.doVoice voice set to '"+user.getTaleStreamVoice()+"'");
        CompIOLogic.saveUser(user);
        CompIOLogic.log(user, CompLogBean.TS, "voice", user.getTaleStreamVoice(), null);
    }
    
    private static void doPitch(CompContextBean context, CompUserBean user, CompRoomBean room, JSONObject params)
    {
        String pitch = params.getString("pitch");
        if (pitch.equals("reset"))
            user.setTaleStreamPitch(null);
        else if ("++".equals(pitch))
        {
            String exitingPitch = user.getTaleStreamPitch();
            if ("x-low".equals(exitingPitch))
                user.setTaleStreamPitch("low");
            else if ("low".equals(exitingPitch))
                user.setTaleStreamPitch("medium");
            else if ("medium".equals(exitingPitch) || (exitingPitch == null))
                user.setTaleStreamPitch("high");
            else if ("high".equals(exitingPitch))
                user.setTaleStreamPitch("x-high");
            else if ("x-high".equals(exitingPitch))
                ;
            else
                user.setTaleStreamPitch("medium");
        }
        else if ("--".equals(pitch))
        {
            String exitingPitch = user.getTaleStreamPitch();
            if ("x-low".equals(exitingPitch))
                ;
            else if ("low".equals(exitingPitch))
                user.setTaleStreamPitch("x-low");
            else if ("medium".equals(exitingPitch) || (exitingPitch == null))
                user.setTaleStreamPitch("low");
            else if ("high".equals(exitingPitch))
                user.setTaleStreamPitch("medium");
            else if ("x-high".equals(exitingPitch))
                user.setTaleStreamPitch("high");
            else
                user.setTaleStreamPitch("medium");
        }
        else
            user.setTaleStreamPitch(pitch);
        if (!StringUtils.isTrivial(user.getTaleStreamPitch()) && StringUtils.isTrivial(user.getTaleStreamVoice()))
            user.setTaleStreamVoice("Amy");
        DebugUtils.trace("RoomLogic.doPitch pitch set to '"+user.getTaleStreamPitch()+"'");
        CompIOLogic.saveUser(user);
        CompIOLogic.log(user, CompLogBean.TS, "pitch", user.getTaleStreamPitch(), null);
    }
    
    private static void doRate(CompContextBean context, CompUserBean user, CompRoomBean room, JSONObject params)
    {
        DebugUtils.trace("RoomLogic.doRate rate starts as '"+user.getTaleStreamRate()+"'");
        String rate = params.getString("rate");
        if (rate.equals("reset"))
            user.setTaleStreamRate(null);
        else if (rate.equals("++"))
        {
            String exitingRate = user.getTaleStreamRate();
            if ("x-slow".equals(exitingRate))
                user.setTaleStreamRate("slow");
            else if ("slow".equals(exitingRate))
                user.setTaleStreamRate("medium");
            else if ("medium".equals(exitingRate) || (exitingRate == null))
                user.setTaleStreamRate("fast");
            else if ("fast".equals(exitingRate))
                user.setTaleStreamRate("x-fast");
            else if ("x-fast".equals(exitingRate))
                ;
            else
                user.setTaleStreamRate("medium");
        }
        else if (rate.equals("--"))
        {
            String exitingRate = user.getTaleStreamRate();
            if ("x-slow".equals(exitingRate))
                ;
            else if ("slow".equals(exitingRate))
                user.setTaleStreamRate("x-slow");
            else if ("medium".equals(exitingRate) || (exitingRate == null))
                user.setTaleStreamRate("slow");
            else if ("fast".equals(exitingRate))
                user.setTaleStreamRate("medium");
            else if ("x-fast".equals(exitingRate))
                user.setTaleStreamRate("fast");
            else
                user.setTaleStreamRate("medium");
        }
        else
            user.setTaleStreamRate(rate);
        if (!StringUtils.isTrivial(user.getTaleStreamRate()) && StringUtils.isTrivial(user.getTaleStreamVoice()))
            user.setTaleStreamVoice("Amy");
        DebugUtils.trace("RoomLogic.doRate rate set to '"+user.getTaleStreamRate()+"'");
        CompIOLogic.saveUser(user);
        CompIOLogic.log(user, CompLogBean.TS, "rate", user.getTaleStreamRate(), null);
    }
    
    private static void doTeleport(CompContextBean context, CompUserBean user, CompRoomBean room, JSONObject params)
    {
        String location = params.getString("location");
        if (location == null)
            return;
        DebugUtils.trace("RoomLogic.doTeleport to '"+location+"'");
        if (location.startsWith("$") && (context.getFeature() != null))
        {
            String roomID = location.substring(1);
            DebugUtils.trace("RoomLogic.doTeleport resolving as roomID="+roomID);
            // look for exact match
            for (CompRoomBean r : context.getFeature().getFeature().getRooms())
            {
                if (r.getID().equals(roomID))
                {
                    LocationBean l = new LocationBean(context.getLocation());
                    l.setRoomID(r.getID());
                    location = l.toString();
                    roomID = null;
                    DebugUtils.trace("RoomLogic.doTeleport found room="+r.getName()+", l="+location);
                    break;
                }
            }
            // look for prefix
            if (roomID != null)
            {
                DebugUtils.trace("RoomLogic.doTeleport did not find room, looking with prefix");
                for (CompRoomBean r : context.getFeature().getFeature().getRooms())
                {
                    if (r.getID().startsWith(roomID))
                    {
                        LocationBean l = new LocationBean(context.getLocation());
                        l.setRoomID(r.getID());
                        location = l.toString();
                        roomID = null;
                        DebugUtils.trace("RoomLogic.doTeleport found room="+r.getID()+"/"+r.getName()+", l="+location);
                        break;
                    }
                }
            }
            if (roomID != null)
            {
                DebugUtils.trace("RoomLogic.doTeleport unable to locate '"+roomID+"', aborting");
                return;
            }
        }
        DebugUtils.trace("RoomLogic.doTeleport move to '"+location+"'");
        DebugUtils.trace("RoomLogic.doTeleport1 # messages="+context.getMessages().size());
        LocationBean l = new LocationBean(location);
        if (StringUtils.isTrivial(l.getRoomID()))
            UserLogic.moveToStrategic(context, context.getUser(), l);
        else
            UserLogic.moveToTactical(context, context.getUser(), l);
        DebugUtils.trace("RoomLogic.doTeleport2 # messages="+context.getMessages().size());
        CompIOLogic.saveUser(context.getUser());
        DebugUtils.trace("RoomLogic.doTeleport3 # messages="+context.getMessages().size());
        CompOperationLogic.fillContext(context);
        DebugUtils.trace("RoomLogic.doTeleport4 # messages="+context.getMessages().size());
    }
    
    private static void doMessage(CompContextBean context, CompUserBean user, CompRoomBean room, JSONObject params)
    {
        AudioMessageBean msg = extractMessage(context, params);
        if (msg == null)
            return;
        LocationBean loc = new LocationBean(user.getLocation());
        DebugUtils.trace("RoomLogic.doMessage message="+msg);
        if (roomRegen(user, loc, room))
        {
            context.addMessage(msg);
            roomUsed(user, loc);
        }
    }
    
    private static void doResponse(CompContextBean context, CompUserBean user, CompRoomBean room, JSONObject params)
    {
        AudioMessageBean msg = extractMessage(context, params);
        if (msg == null)
            return;
        context.addMessage(msg);
    }
    
    private static AudioMessageBean extractMessage(CompContextBean context, JSONObject params)
    {
        DebugUtils.trace("RoomLogic.extractMessage, params="+params.toJSONString());
        String msg = params.getString("message");
        if (msg == null)
            return null;
        DebugUtils.trace("RoomLogic.extractMessage, msg="+msg);
        AudioMessageBean m = new AudioMessageBean(msg);
        JSONArray args = (JSONArray)params.get("args");
        if (args != null)
        {
            Object[] a = new Object[args.size()];
            for (int i = 0; i < a.length; i++)
            {
                Object arg = args.get(i);
                DebugUtils.trace("RoomLogic.extractMessage, arg#"+i+" before "+arg.getClass().getSimpleName());
                if (arg instanceof JSONObject)
                    a[i] = extractMessage(context, (JSONObject)arg);
                else
                {
                    String sarg = arg.toString();
                    if (sarg.startsWith("$"))
                        a[i] = BeanUtils.get(context, sarg.substring(1));
                    else
                        a[i] = sarg;
                }
                DebugUtils.trace("RoomLogic.extractMessage, arg#"+i+" after "+arg.getClass().getSimpleName());
            }
            m.setArgs(a);
        }
        return m;
    }
    
    private static void doSet(CompContextBean context, CompUserBean user, CompRoomBean room, JSONObject params)
    {
        String lval = params.getString("lval");
        if (StringUtils.isTrivial(lval))
            return;
        String rval = params.getString("rval");
        if (StringUtils.isTrivial(rval))
            return;
        Object eval = RoomLogic.eval(context, rval);
        BeanUtils.set(context, lval, eval);
        DebugUtils.trace("RoomLogic.doSet '"+rval+"'->'"+eval+"':='"+lval+"' -> "+BeanUtils.get(context, lval));
        CompIOLogic.saveUser(user);
    }
    
    private static void doResurrect(CompContextBean context, CompUserBean user, CompRoomBean room, JSONObject params)
    {
        int idx = IntegerUtils.parseInt(params.getString("idx"));
        DebugUtils.trace("RoomLogic.doResurrect "+idx);
        if (user.getReallyDeadCompanions().size() <= idx)
        {
            DebugUtils.trace("RoomLogic.doResurrect no one to resurrect");
            return;
        }
        CompCompanionBean dead = user.getReallyDeadCompanions().get(idx);
        dead.setCurrentHitPoints(dead.getHitPoints());
        user.getCompanions().add(0, dead);
        user.getReallyDeadCompanions().remove(idx);
        user.setActiveCompanion(dead.getID());
        DebugUtils.trace("RoomLogic.doResurrect resurrected '"+dead.getName()+"'");
        CompIOLogic.saveUser(user);
    }

    private static String makeRoomKey(CompUserBean user, LocationBean loc)
    {
        return user.getURI()+"!"+loc.toString();
    }
    
    public static void roomUsed(CompUserBean user, LocationBean loc)
    {
        if (loc == null)
            loc = new LocationBean(user.getLocation());
        if (StringUtils.isTrivial(loc.getRoomID()))
            return;
        String key = makeRoomKey(user, loc);
        //DebugUtils.trace("RoomLogic.roomUsed("+key+")");
        mRoomFightTimeout.put(key, new Long(System.currentTimeMillis()));
    }
    
    public static boolean roomRegen(CompUserBean user, LocationBean loc, CompRoomBean room)
    {
        if (StringUtils.isTrivial(loc.getRoomID()))
            return true;
        String key = makeRoomKey(user, loc);
        Long timeout = mRoomFightTimeout.get(key);
        //DebugUtils.trace("RoomLogic.roomRegen("+key+")");
        if (timeout == null)
        {
            DebugUtils.trace("No timeout!");
            return true;
        }
        long elapsed = System.currentTimeMillis() - timeout;
        long waitTime = CompConstLogic.ROOM_FIGHT_TIMEOUT;
        if (room.getParams() != null)
            if (room.getParams().containsKey(CompRoomBean.MD_WAIT_TIME))
                waitTime = LongUtils.parseLong(room.getParams().get(CompRoomBean.MD_WAIT_TIME));
        //DebugUtils.trace("RoomLogic.roomRegen "+elapsed+" > "+waitTime);
        return elapsed > waitTime;
    }

    public static boolean checkRoomLock(CompContextBean context,
            CompUserBean user, CompRoomBean room, int dir)
    {
        if (room.getParams() == null)
            return false;
        DebugUtils.trace("RoomLogic.checkRoomLock(dir="+dir+", params="+room.getParams()+")");
        if (room.getDirectionLock(dir - 1) == null)
            return false;
        JSONObject lock = room.getDirectionLock(dir - 1);
        return evalLock(context, lock);
    }

    public static boolean evalLock(CompContextBean context, JSONObject lock)
    {
        if (lock == null)
            return false;
        String expr = lock.getString("expr");
        if (expr == null)
            return false;
        DebugUtils.trace("RoomLogic.checkRoomLock(expr="+expr+")");
        boolean locked = BooleanUtils.parseBoolean(eval(context, expr));
        if (locked && lock.containsKey("trueMessage"))
        {
            AudioMessageBean msg = extractMessage(context, (JSONObject)lock.get("trueMessage"));
            if (msg != null)
                context.addMessage(msg);
        }
        if (!locked && lock.containsKey("falseMessage"))
        {
            AudioMessageBean msg = extractMessage(context, (JSONObject)lock.get("falseMessage"));
            if (msg != null)
                context.addMessage(msg);
        }            
        if (locked && lock.containsKey("trueExpr"))
        {
            locked = evalLock(context, (JSONObject)lock.get("trueExpr"));
        }
        if (!locked && lock.containsKey("falseExpr"))
        {
            locked = evalLock(context, (JSONObject)lock.get("falseExpr"));
        }            
        DebugUtils.trace("RoomLogic.checkRoomLock, locked="+locked);
        return locked;
    }
    
    private static final String[] OPS = {
            "&&", "||", ">=", "<=", "!=", "==", "<", ">", "+", "-",
            "includes"
    };

    public static Object eval(CompContextBean context, String expr)
    {
        DebugUtils.trace("RoomLogic.eval(expr="+expr+")");
        if (expr == null)
            return false;
        expr = expr.trim();
        if ("true".equalsIgnoreCase(expr))
        {
            DebugUtils.trace("RoomLogic.eval, ret=TRUE");
            return true;
        }
        if ("false".equalsIgnoreCase(expr))
        {
            DebugUtils.trace("RoomLogic.eval, ret=FALSE");
            return false;
        }
        String op = null;
        int idx = 0;
        for (int i = 0; i < OPS.length; i++)
        {
            idx = indexOf(expr, OPS[i]);
            if (idx >= 0)
            {
                op = OPS[i];
                break;
            }
        }
        if (op == null)
        {
            if (expr.startsWith("$"))
            {
                Object val = BeanUtils.get(context, expr.substring(1));
                DebugUtils.trace("RoomLogic.eval, (Object) ret="+val);
                return val;
            }
            if (expr.startsWith("@"))
            {
                String cmd = expr.substring(1);
                Object val = MacroLogic.executeSimple(context, cmd);
                DebugUtils.trace("RoomLogic.eval, (cmd) ret="+val);
                return val;
            }
            if (expr.startsWith("\"") && expr.endsWith("\""))
            {
                String str = expr.substring(1, expr.length() - 1);
                DebugUtils.trace("RoomLogic.eval, (String) ret="+str);
                return str;
            }
            if (expr.startsWith("\'") && expr.endsWith("\'"))
            {
                String str = expr.substring(1, expr.length() - 1);
                DebugUtils.trace("RoomLogic.eval, (string) ret="+str);
                return str;
            }
            int i = IntegerUtils.parseInt(expr);
            DebugUtils.trace("RoomLogic.eval, (int) ret="+i);
            return i;
        }
        DebugUtils.trace("RoomLogic.eval, op="+op);
        Object arg1 = eval(context, expr.substring(0, idx));
        Object arg2 = eval(context, expr.substring(idx + op.length()));
        DebugUtils.trace("RoomLogic.eval, arg1="+arg1+", arg2="+arg2);
        Object ret = null;
        switch (op)
        {
            case "&&":
                ret = BooleanUtils.parseBoolean(arg1) && BooleanUtils.parseBoolean(arg2);
                break;
            case "||":
                ret = BooleanUtils.parseBoolean(arg1) || BooleanUtils.parseBoolean(arg2);
                break;
            case ">=":
                ret = DoubleUtils.parseDouble(arg1) >= DoubleUtils.parseDouble(arg2);
                break;
            case "<=":
                ret = DoubleUtils.parseDouble(arg1) <= DoubleUtils.parseDouble(arg2);
                break;
            case "!=":
                if ((arg1 instanceof Boolean) && (arg2 instanceof Boolean))
                    ret = !ObjectUtils.equals(arg1, arg2);
                else
                    ret = !MathUtils.equals(DoubleUtils.parseDouble(arg1), DoubleUtils.parseDouble(arg2));
                break;
            case "==":
                if ((arg1 instanceof Boolean) && (arg2 instanceof Boolean))
                    ret = ObjectUtils.equals(arg1, arg2);
                else
                    ret = MathUtils.equals(DoubleUtils.parseDouble(arg1), DoubleUtils.parseDouble(arg2));
                break;
            case "<":
                ret = DoubleUtils.parseDouble(arg1) < DoubleUtils.parseDouble(arg2);
                break;
            case ">":
                ret = DoubleUtils.parseDouble(arg1) > DoubleUtils.parseDouble(arg2);
                break;
            case "+":
                ret = DoubleUtils.parseDouble(arg1) + DoubleUtils.parseDouble(arg2);
                break;
            case "-":
                ret = DoubleUtils.parseDouble(arg1) - DoubleUtils.parseDouble(arg2);
                break;
            case "includes":
                ret = String.valueOf(arg1).toLowerCase().indexOf(String.valueOf(arg2)) >= 0;
                break;
            default:
                throw new IllegalStateException("Unhandled operation '"+op+"'");
        }
        DebugUtils.trace("RoomLogic.eval, ret="+arg2);
        return ret;
    }

    private static int indexOf(String expr, String pattern)
    {
        String stack = "";
        char[] c = expr.toCharArray();
        for (int i = 0; i < c.length; i++)
            switch (c[i])
            {
                case '\"':
                    if (stack.endsWith("\""))
                        stack = stack.substring(0, stack.length());
                    else
                        stack += "\"";
                    break;
                case '\'':
                    if (stack.endsWith("\'"))
                        stack = stack.substring(0, stack.length());
                    else
                        stack += "\'";
                    break;
                case ')':
                    if (stack.endsWith("("))
                        stack = stack.substring(0, stack.length());
                    break;
                case '(':
                    stack += "(";
                    break;
                default:
                    if (stack.length() == 0)
                        if (expr.substring(i).startsWith(pattern))
                            return i;
            }
        return -1;
    }
}
