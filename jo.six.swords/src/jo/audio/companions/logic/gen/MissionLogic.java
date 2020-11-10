package jo.audio.companions.logic.gen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.audio.common.logic.CommonIOLogic;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.MissionBean;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.tools.gui.edit.data.PFeatureBean;
import jo.audio.compedit.data.CompEditModuleBean;
import jo.audio.compedit.logic.CompEditIOLogic;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.LongUtils;
import jo.util.utils.obj.StringUtils;

public class MissionLogic
{
    public static final long UPDATE_FREQUENCY = 60*60*1000L;
    private static final String MISSION_DATA_URI = "sixswords://missions";
    private static final String MISSION_LAST_RUN = "lastRun";
    
    public static Thread startBackgroundDaemon()
    {
        DebugUtils.trace("Starting Mission Thread");
        Thread t = new Thread("Mission Update") { public void run() { 
            doMissionDaemon();
        } };
        t.setDaemon(true);
        t.start();
        return t;
    }
    
    private static void doMissionDaemon()
    {
        for (;;)
        {
            try
            {
                MissionLogic.doSleep();
                MissionLogic.doUpdateMissions();
            }
            catch (Throwable t)
            {
                DebugUtils.trace("Error processing missions", t);
            }
        }
    }
    
    private static void doSleep()
    {
        long lastRun = LongUtils.parseLong(CommonIOLogic.getDataSecondaryValue(MISSION_DATA_URI, MISSION_LAST_RUN));
        long now = System.currentTimeMillis();
        long timeLeft = now - lastRun;
        if (timeLeft > UPDATE_FREQUENCY)
            return;
        try
        {
            long timeToSleep = UPDATE_FREQUENCY - timeLeft;
            DebugUtils.trace("Missions lastRun="+lastRun+", now="+now+", time left "+timeLeft+", Sleeping for "+timeToSleep);
            Thread.sleep(timeToSleep);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private static void doUpdateMissions()
    {
        DebugUtils.trace("Updating Missions Logic");
        List<CompEditModuleBean> modules = CompEditIOLogic.getAllModules();
        boolean anyChangeAtAll = false;
        for (CompEditModuleBean module : modules)
        {
            boolean anyChange = false;
            for (PFeatureBean feature : module.getFeatures().toArray(new PFeatureBean[0]))
                if (updateFeature(module, feature))
                    anyChange = true;
            if (anyChange)
            {
                CompEditIOLogic.saveModule(module);
                anyChangeAtAll = true;
            }
        }
        if (anyChangeAtAll)
            FeatureLogic.readDynamicModules(true); // flush cache
        long now = System.currentTimeMillis();
        CommonIOLogic.setDataSecondaryValue(MISSION_DATA_URI, MISSION_LAST_RUN, String.valueOf(now));
        try
        {
            Thread.sleep(15000L); // latency to let data get written
        }
        catch (InterruptedException e)
        {
        } 
        DebugUtils.trace("Done Mission Thread");
    }
    
    private static boolean updateFeature(CompEditModuleBean module, PFeatureBean feature)
    {
        if (feature.getParams() == null)
            return false;
        if (!feature.getParams().containsKey("expiryDate"))
            return false;
        long expiryDate = LongUtils.parseLong(feature.getParams().get("expiryDate"));
        if (expiryDate > System.currentTimeMillis())
            return false;
        module.getFeatures().remove(feature);
        return true;
    }
    
    public static List<MissionBean> getMissions(CompUserBean user)
    {
        List<MissionBean> missions = new ArrayList<>();
        if (user.getMetadata() == null)
            return missions;
        if (!user.getMetadata().containsKey("MISSION"))
            return missions;
        Object ms = user.getMetadata().get("MISSION");
        if (ms instanceof JSONObject)
        {
            JSONObject json = (JSONObject)user.getMetadata().get("MISSION");
            MissionBean m = new MissionBean(json);
            if (!m.isExpired())
                missions.add(m);
        }
        else if (ms instanceof JSONArray)
        {
            JSONArray arr = (JSONArray)ms;
            for (int i = 0; i < arr.size(); i++)
                if (arr.get(i) instanceof JSONObject)
                {
                    JSONObject json = (JSONObject)arr.get(i);
                    MissionBean m = new MissionBean(json);
                    if (!m.isExpired())
                        missions.add(m);
                }
        }
        return missions;
    }
    
    @SuppressWarnings("unchecked")
    public static void setMissions(CompUserBean user, List<MissionBean> missions)
    {
        JSONArray arr = new JSONArray();
        for (MissionBean mission : missions)
            arr.add(mission.toJSON());
        if (user.getMetadata() == null)
            user.setMetadata(new JSONObject());
        user.getMetadata().put("MISSIONS", arr);
    }
    
    public static MissionBean findMission(List<MissionBean> missions, String id)
    {
        if (id == null)
            return null;
        // check by id
        for (MissionBean m : missions)
            if (id.equalsIgnoreCase(m.getID()))
                return m;
        // check by exact type
        for (MissionBean m : missions)
            if (id.equalsIgnoreCase(m.getType()))
                return m;
        // check by inexact type
        for (MissionBean m : missions)
            if ((m.getType() != null) && m.getType().startsWith(id))
                return m;
        return null;
    }
    
    public static MissionBean getMission(CompUserBean user, String id)
    {
        List<MissionBean> missions = getMissions(user);
        return findMission(missions, id);
    }
    
    public static void deleteMission(CompUserBean user, String id)
    {
        List<MissionBean> missions = getMissions(user);
        MissionBean m = findMission(missions, id);
        if (m != null)
        {
            missions.remove(m);
            setMissions(user, missions);
        }
    }
    
    public static void setMission(CompUserBean user, MissionBean m)
    {
        List<MissionBean> missions = getMissions(user);
        if (!StringUtils.isTrivial(m.getID()))
        {
            for (Iterator<MissionBean> i = missions.iterator(); i.hasNext(); )
                if (i.next().getID().equalsIgnoreCase(m.getID()));
        }
        else
            m.setID(m.getType()+System.currentTimeMillis());
        missions.add(m);
        setMissions(user, missions);
    }
}
