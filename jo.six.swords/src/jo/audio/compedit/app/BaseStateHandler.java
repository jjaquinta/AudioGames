package jo.audio.compedit.app;

import jo.audio.companions.data.GeoBean;
import jo.audio.compedit.app.logic.FeatureLogic;
import jo.audio.compedit.app.logic.LookLogic;
import jo.audio.compedit.app.logic.OperationLogic;
import jo.audio.compedit.data.CompEditOperationBean;
import jo.audio.compedit.data.CompEditState;
import jo.audio.compedit.slu.CompEditModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioMessageBean;
import jo.audio.util.model.data.IntentReqBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.StringUtils;

public class BaseStateHandler extends CompEditStateHandler
{
    @Override
    public void handleWelcome(BaseUserState s)
    {
        CompEditState state = (CompEditState)s;
        long lastInteraction = state.getContext().getUser()
                .getLastInteraction();
        if (lastInteraction <= 0)
        {
            state.respond(CompEditModelConst.TEXT_INTRO_TO_EDITOR);
        }
        else
        {
            state.respond(CompEditModelConst.TEXT_WELCOME_TO_EDITOR,
                    state.getContext().getID().getDisplayName());
            //LookLogic.doLook(state, null);
        }
        LookLogic.doLook(state);
        if (state.getContext().getModule() == null)
        {
            state.prompts(CompEditModelConst.INTENT_NEW,
                    CompEditModelConst.INTENT_LOAD,
                    CompEditModelConst.INTENT_LIST);
        }
        else
        {
            state.prompts(CompEditModelConst.INTENT_LOOK);
        }
        state.getResponse().setLinkOutName(state.resolve(CompEditModelConst.TEXT_FORUM));
        state.getResponse().setLinkOutURL(
                "http://starlanes.freeforums.net/board/5/6-swords-discussion");
    }

    public void doLoad(CompEditState state, String featureName)
    {
        DebugUtils.trace("doLoad(featureName="+featureName+")");
        if (StringUtils.isTrivial(featureName))
        {
            FeatureLogic.listFeatures(state);
            state.prompts(CompEditModelConst.INTENT_LOAD,
                    CompEditModelConst.INTENT_NEW);
        }
        else
        {
            FeatureLogic.loadFeature(state, featureName);
            state.prompts(CompEditModelConst.INTENT_SETFEATURENAME);
        }
    }

    public void doList(CompEditState state)
    {
        DebugUtils.trace("doList()");
        FeatureLogic.listFeatures(state);
        state.prompts(CompEditModelConst.INTENT_LOAD);
    }

    public void doNew(CompEditState state, String featureName)
    {
        DebugUtils.trace("doNew(featureName="+featureName+")");
        FeatureLogic.newFeature(state, featureName);
        state.prompts(CompEditModelConst.INTENT_SETFEATURENAME);
    }

    public void doSetFeatureName(CompEditState state, String featureName)
    {
        DebugUtils.trace("doSetFeatureName(featureName="+featureName+")");
        if (StringUtils.isTrivial(featureName))
        {
            FeatureLogic.reportFeatureName(state);
            state.prompts(CompEditModelConst.INTENT_SETFEATURENAME);
        }
        else
        {
            FeatureLogic.setFeatureName(state, featureName);
            state.prompts(CompEditModelConst.INTENT_LOOK);
        }
    }

    public void doSetName(CompEditState state, String text)
    {
        DebugUtils.trace("doSetName(text="+text+")");
        if (StringUtils.isTrivial(text))
        {
            FeatureLogic.reportRoomName(state);
            state.prompts(CompEditModelConst.INTENT_SETNAME);
        }
        else
        {
            FeatureLogic.setRoomName(state, text);
            state.prompts(CompEditModelConst.INTENT_SETDESC,
                    CompEditModelConst.INTENT_DIG);
        }
    }

    public void doSetDesc(CompEditState state, String text)
    {
        DebugUtils.trace("doSetDescription(text="+text+")");
        if (StringUtils.isTrivial(text))
        {
            FeatureLogic.reportRoomDescription(state);
            state.prompts(CompEditModelConst.INTENT_SETDESC);
        }
        else
        {
            FeatureLogic.setRoomDescription(state, text);
            state.prompts(CompEditModelConst.INTENT_DIG);
        }
    }

    public void doDig(CompEditState state, String direction)
    {
        DebugUtils.trace("doDig(direction="+direction+")");
        if (StringUtils.isTrivial(direction))
        {
            LookLogic.doLook(state);
            state.prompts(CompEditModelConst.INTENT_DIG);
        }
        else
        {
            FeatureLogic.digRoom(state, direction);
            state.prompts(CompEditModelConst.INTENT_SETNAME,
                    CompEditModelConst.INTENT_SETDESC);
        }
    }

    public void doLink(CompEditState state, String direction, String roomName)
    {
        DebugUtils.trace("doLink(direction="+direction+", room="+roomName+")");
        if (StringUtils.isTrivial(direction))
        {
            state.respond(CompEditModelConst.TEXT_HELP_LINK);
            state.prompts(CompEditModelConst.INTENT_LINK);
            return;
        }
        if (StringUtils.isTrivial(roomName))
        {
            state.respond(CompEditModelConst.TEXT_HELP_LINK);
            state.prompts(CompEditModelConst.INTENT_LINK);
            return;
        }
        FeatureLogic.linkRoom(state, direction, roomName);
        LookLogic.doLook(state);
    }

    public void doUnlink(CompEditState state, String direction)
    {
        DebugUtils.trace("doUnlink(direction="+direction+")");
        if (StringUtils.isTrivial(direction))
        {
            LookLogic.doLook(state);
            state.prompts(CompEditModelConst.INTENT_UNLINK);
        }
        else
        {
            FeatureLogic.unlinkRoom(state, direction);
            state.prompts(CompEditModelConst.INTENT_SETNAME,
                    CompEditModelConst.INTENT_SETDESC);
        }
    }

    public void doLook(CompEditState state)
    {
        DebugUtils.trace("doLook()");
        LookLogic.doLook(state);
        state.prompts(CompEditModelConst.INTENT_SETNAME,
                CompEditModelConst.INTENT_SETDESC,
                CompEditModelConst.INTENT_DIG);
    }
    
    public void doMove(CompEditState state, String direction)
    {
        LookLogic.move(state, direction);
        state.prompts(CompEditModelConst.INTENT_SETNAME,
                CompEditModelConst.INTENT_SETDESC);
    }
    
    public void doNorth(CompEditState state)
    {
        LookLogic.move(state, CompEditOperationBean.NORTH);
        state.prompts(CompEditModelConst.INTENT_SETNAME,
                CompEditModelConst.INTENT_SETDESC);
    }
    
    public void doSouth(CompEditState state)
    {
        LookLogic.move(state, CompEditOperationBean.SOUTH);
        state.prompts(CompEditModelConst.INTENT_SETNAME,
                CompEditModelConst.INTENT_SETDESC);
    }
    
    public void doEast(CompEditState state)
    {
        LookLogic.move(state, CompEditOperationBean.EAST);
        state.prompts(CompEditModelConst.INTENT_SETNAME,
                CompEditModelConst.INTENT_SETDESC);
    }
    
    public void doWest(CompEditState state)
    {
        LookLogic.move(state, CompEditOperationBean.WEST);
        state.prompts(CompEditModelConst.INTENT_SETNAME,
                CompEditModelConst.INTENT_SETDESC);
    }
    
    public void doHelp(CompEditState state, String topic)
    {
        HelpLogic.doHelp(state, topic);
    }
    
    public void doSetMonster(CompEditState state, String monster)
    {
        MonsterLogic.doMonster(state, monster);
    }
    
    public void doChallengeUp(CompEditState state)
    {
        MonsterLogic.doChallengeDelta(state, 1);
    }
    
    public void doChallengeDown(CompEditState state)
    {
        MonsterLogic.doChallengeDelta(state, -1);
    }
    
    public void doSetLocation(CompEditState state, Integer nsdegrees, String nsdname, Integer nsminutes, String nsmname, String nsdir, 
            Integer ewdegrees, String ewdname, Integer ewminutes, String ewmname, String ewdir)
    {
        DebugUtils.trace("doSetLocation(nsdegrees="+nsdegrees+", nsminutes="+nsminutes+", nsdir="+nsdir+", ewdegrees="+ewdegrees+", ewminutes="+ewminutes+", ewdir="+ewdir+")");
        if ((nsdir != null) || (ewdir != null))
        {
            GeoBean location;
            String l = state.getContext().getFeature().getLocation();
            if (l != null)
                location = new GeoBean(l);
            else
                location = new GeoBean();
            if (nsdir != null)
            {
                int lattitude = nsdegrees*60 + nsminutes;
                nsdir = nsdir.toLowerCase();
                if (nsdir.startsWith("s"))
                    ;
                else if (nsdir.startsWith("n"))
                    lattitude = -lattitude;
                else if (nsdir.indexOf("s") > 0)
                    ;
                else if (nsdir.indexOf("n") > 0)
                    lattitude = -lattitude;
                location.setLattitude(lattitude);
            }
            if (ewdir != null)
            {
                int longitude = ewdegrees*60 + ewminutes;
                ewdir = ewdir.toLowerCase();
                if (ewdir.startsWith("e"))
                    ;
                else if (ewdir.startsWith("w"))
                    longitude = -longitude;
                else if (ewdir.indexOf("e") > 0)
                    ;
                else if (ewdir.indexOf("w") > 0)
                    longitude = -longitude;
                location.setLongitude(longitude);
            }
            OperationLogic.setLocation(state, location.getX(), location.getY());
        }
        String l = state.getContext().getFeature().getLocation();
        if (StringUtils.isTrivial(l))
            state.respond(CompEditModelConst.TEXT_LOCATION_HAS_NOT_BEEN_SET);
        else
        {
            GeoBean location = new GeoBean(l);
            state.respond(makeLonLatMessage(location));
        }
    }

    public static AudioMessageBean makeLonLatMessage(GeoBean location)
    {
        Object[] args = new Object[6];
        int lat = location.getLattitude();
        int lon = location.getLongitude();
        args[0] = Math.abs(lat)/60;
        args[1] = Math.abs(lat)%60;
        args[2] = (lat < 0) ? "{{North}}" : "{{South}}";
        args[3] = Math.abs(lon)/60;
        args[4] = Math.abs(lon)%60;
        args[5] = (lon < 0) ? "{{West}}" : "{{East}}";
        return new AudioMessageBean(CompEditModelConst.TEXT_LOCATION_SET_TO_XXX_YYY, args);
    }
    
    public void doSetDimension(CompEditState state, String dim)
    {
        DebugUtils.trace("doSetDimension(dim="+dim+")");
        if (!StringUtils.isTrivial(dim))
        {
            dim = dim.toLowerCase();
            int z = 0;
            if (dim.equals("free"))
                z = 0;
            else if (dim.equals("premium"))
                z = 2;
            else if (dim.startsWith("f"))
                z = 0;
            else if (dim.startsWith("p"))
                z = 2;
            OperationLogic.setDimension(state, z);
        }
        String l = state.getContext().getFeature().getLocation();
        if (StringUtils.isTrivial(l))
            state.respond(CompEditModelConst.TEXT_DIMENSION_HAS_NOT_BEEN_SET);
        else
        {
            GeoBean location = new GeoBean(l);
            state.respond(CompEditModelConst.TEXT_THIS_FEATURE_WILL_APPEAR_IN_THE_XXX_DIMENSION,
                    "{{DIMENSION#"+location.getZ()+"}}");
        }
    }
    
    public void doEnable(CompEditState state)
    {
        OperationLogic.setEnabledBy(state, "true");
        if (BooleanUtils.parseBoolean(state.getContext().getFeature().getEnabledBy()))
            state.respond(CompEditModelConst.TEXT_THIS_FEATURE_IS_ENABLED);
        else
            state.respond(CompEditModelConst.TEXT_THIS_FEATURE_IS_DISABLED);
    }
    
    public void doDisable(CompEditState state)
    {
        OperationLogic.setEnabledBy(state, "false");
        if (BooleanUtils.parseBoolean(state.getContext().getFeature().getEnabledBy()))
            state.respond(CompEditModelConst.TEXT_THIS_FEATURE_IS_ENABLED);
        else
            state.respond(CompEditModelConst.TEXT_THIS_FEATURE_IS_DISABLED);
    }

    public void doStop(CompEditState state)
    {
        state.respond(CompEditModelConst.TEXT_GOODBYE);
        state.endSession();
    }
    
    public void doCancel(CompEditState state)
    {
        state.respond(CompEditModelConst.TEXT_GOODBYE);
        state.endSession();
    }

    public void doDefault(CompEditState state, IntentReqBean intent)
    {
        state.respond("I heard intent "+intent.getIntentID()+". ");
        state.respond("I heard "+intent.getSlots().size()+" slots. ");
        String[] slotNames = intent.getSlots().keySet().toArray(new String[0]);
        for (String key : slotNames)
        {
            String val = intent.getSlots().getProperty(key);
            state.respond("I heard slot \""+key+"\" as value \""+val+"\". ");
        }
        state.respond(CompEditModelConst.TEXT_WTF);
    }
}
