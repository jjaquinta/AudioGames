package jo.audio.companions.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.audio.companions.app.CompApplicationHandler;
import jo.audio.companions.data.CompEffectTypeBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompOperationLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.GenerationLogic;
import jo.audio.companions.logic.ItemLogic;
import jo.audio.companions.logic.MonsterLogic;
import jo.audio.util.BaseServlet;
import jo.util.utils.obj.BooleanUtils;

public class CompServlet extends BaseServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 6998188851979224629L;

    private static CompServlet mInstance = null;
    
    public CompServlet()
    {
        mInstance = this;
//      DebugUtils.mLoggers.add(new LogEngine() {            
//      @Override
//      public void log(int severity, String msg, Throwable exception)
//      {
//          if (exception != null)
//              Logger.getAnonymousLogger().log(Level.FINEST, exception);
//          if (msg != null)
//              Logger.getAnonymousLogger().log(Level.FINEST, msg);
//      }
//  });
        //new CompTelnet(22, true, "utf-8");
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
        this.setHandler(CompApplicationHandler.getInstance());
        CompOperationLogic.startBackgroundDaemons();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        String dump = req.getParameter("dump");
        if ("monsters".equals(dump))
            dumpMonsters(req, resp, false);
        else if ("monsterIndex".equals(dump))
            dumpMonsters(req, resp, true);
        else if ("monster".equals(dump))
            dumpMonster(req, resp);
        else if ("features".equals(dump))
            dumpFeatures(req, resp);
        else if ("items".equals(dump))
            dumpItems(req, resp);
        else if ("vorticies".equals(dump) || "vortex".equals(dump) || "vort".equals(dump))
            dumpVorticies(req, resp);
        else if ("system".equals(dump))
            dumpSystem(req, resp);
        else
            super.doGet(req, resp);
    }
    
    private void dumpSystem(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        boolean cleanup = BooleanUtils.parseBoolean(req.getParameter("clean"));
        StringBuffer html = new StringBuffer();
        Thread[] threads = new Thread[Thread.activeCount()];
        int count = Thread.enumerate(threads);
        html.append("<h1>Threads</h1>");
        html.append("<table>");
        html.append("<tr>");
        html.append("<th>Name</th>");
        html.append("<th>Priority</th>");
        html.append("<th>State</th>");
        html.append("</tr>");
        for (int i = 0; i < count; i++)
        {
            html.append("<tr>");
            html.append("<td>"+threads[i].getName()+"</td>");
            html.append("<td>"+threads[i].getPriority()+"</td>");
            html.append("<td>"+threads[i].getState().name()+"</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        Runtime rt = Runtime.getRuntime();
        html.append("<h1>Runtime</h1>");
        html.append("<table>");
        html.append("<tr>");
        html.append("<td>availableProcessors</td>");
        html.append("<td>"+rt.availableProcessors()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td>freeMemory</td>");
        html.append("<td>"+rt.freeMemory()+", "+(rt.freeMemory()*100/rt.totalMemory())+"%</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td>maxMemory</td>");
        html.append("<td>"+rt.maxMemory()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td>totalMemory</td>");
        html.append("<td>"+rt.totalMemory()+"</td>");
        html.append("</tr>");
        html.append("</table>");
        html.append("<h1>Properties</h1>");
        html.append("<table>");
        html.append("<tr>");
        html.append("<th>Prop</th>");
        html.append("<th>Value</th>");
        html.append("</tr>");
        Object[] keys = System.getProperties().keySet().toArray();
        Arrays.sort(keys);
        for (Object key : keys)
        {
            Object val = System.getProperties().get(key);
            html.append("<tr>");
            html.append("<td>"+key+"</td>");
            html.append("<td>"+val+"</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        html.append("<h1>Regions</h1>");
        if (cleanup)
            html.append("<h2>Before Cleanup</h2>");
        html.append(GenerationLogic.dumpRegions());
        if (cleanup)
        {
            html.append("<p>Free memory before cleanup: "+rt.freeMemory()+", "+(rt.freeMemory()*100/rt.totalMemory())+"%.</p>");
            GenerationLogic.cleanup();
            System.gc();
            html.append("<p>Free memory after cleanup: "+rt.freeMemory()+", "+(rt.freeMemory()*100/rt.totalMemory())+"%.</p>");
            html.append("<h1>Regions</h1>");
            html.append("<h2>After Cleanup</h2>");
            html.append(GenerationLogic.dumpRegions());
        }
        dumpHTML(resp, html.toString());
    }
    
    private void dumpMonster(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        String id = req.getParameter("id");
        CompMonsterTypeBean monster = MonsterLogic.getAnyMonsterType(id);
        StringBuffer html = new StringBuffer();
        html.append("<h1>Monster "+id+"</h1>");
        if (monster != null)
        html.append("<table>");
        html.append("<tr>");
        html.append("<th>ID</th>");
        html.append("<td>"+monster.getID()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>Name</th>");
        html.append("<td>"+monster.getName()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>AC</th>");
        html.append("<td>"+monster.getAC()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>ATT</th>");
        html.append("<td>"+monster.getATT()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>Challenge</th>");
        html.append("<td>"+monster.getChallenge()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>Terrain</th>");
        html.append("<td>"+monster.getTerrain()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>Type</th>");
        html.append("<td>"+monster.getType()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>Alignment</th>");
        html.append("<td>"+monster.getAlignment()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>AverageDamage</th>");
        html.append("<td>"+monster.getAverageDamage()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>Details().toJSONString</th>");
        html.append("<td>"+(monster.getDetails() == null ? "" : monster.getDetails().toJSONString())+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>Enc</th>");
        html.append("<td>"+monster.getEnc()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>EquivalentLevel</th>");
        html.append("<td>"+monster.getEquivalentLevel()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>ExperienceBase</th>");
        html.append("<td>"+monster.getExperienceBase()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>ExperiencePerHP</th>");
        html.append("<td>"+monster.getExperiencePerHP()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>Freq</th>");
        html.append("<td>"+monster.getFreq()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>HD</th>");
        html.append("<td>"+monster.getHD()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>Intelligence</th>");
        html.append("<td>"+monster.getIntelligence()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>NumAtt</th>");
        html.append("<td>"+monster.getNumAtt()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>Size</th>");
        html.append("<td>"+monster.getSize()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>Special</th>");
        html.append("<td>"+monster.getSpecial()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>THAC0</th>");
        html.append("<td>"+monster.getTHAC0()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>Treasures</th>");
        html.append("<td>");
        JSONArray treasures = (JSONArray)monster.getDetails().get("treasures");
        for (int i = 0; i < treasures.size(); i++)
        {
            if (i > 0)
                html.append("<br/>");
            html.append(((JSONObject)treasures.get(i)).toJSONString());
        }
        html.append("</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("<th>Width</th>");
        html.append("<td>"+monster.getWidth()+"</td>");
        html.append("</tr>");
        html.append("<tr>");
        html.append("</tr>");
        html.append("</table>");
        dumpHTML(resp, html.toString());
    }
    
    private void dumpMonsters(HttpServletRequest req, HttpServletResponse resp, boolean index) throws IOException
    {
        CompMonsterTypeBean[] monsters;
        if (index)
            monsters = MonsterLogic.getIndexedTypes().toArray(new CompMonsterTypeBean[0]);
        else
            monsters = MonsterLogic.getAllTypes().toArray(new CompMonsterTypeBean[0]);
        Arrays.sort(monsters, new Comparator<CompMonsterTypeBean>(){
            @Override
            public int compare(CompMonsterTypeBean o1, CompMonsterTypeBean o2)
            {
                int diff = o1.getTerrain().compareTo(o2.getTerrain());
                if (diff == 0)
                {
                    String id1 = o1.getID();
                    int o = id1.indexOf('$');
                    if (o > 0)
                        id1 = id1.substring(0,  o);
                    String id2 = o2.getID();
                    o = id2.indexOf('$');
                    if (o > 0)
                        id2 = id2.substring(0,  o);
                    diff = id1.compareTo(id2);
                }
                if (diff == 0)
                    diff = o1.getID().compareTo(o2.getID());
                return 0;
            }
        });
        StringBuffer html = new StringBuffer();
        html.append("<h1>Monsters</h1>");
        html.append("<table>");
        html.append("<tr>");
        html.append("<th>ID</th>");
        html.append("<th>Name</th>");
        html.append("<th>AC</th>");
        html.append("<th>ATT</th>");
        html.append("<th>Challenge</th>");
        html.append("<th>Terrain</th>");
        html.append("<th>Type</th>");
        html.append("<th>Alignment</th>");
        html.append("<th>AverageDamage</th>");
        html.append("<th>Details().toJSONString</th>");
        html.append("<th>Enc</th>");
        html.append("<th>EquivalentLevel</th>");
        html.append("<th>ExperienceBase</th>");
        html.append("<th>ExperiencePerHP</th>");
        html.append("<th>Freq</th>");
        html.append("<th>HD</th>");
        html.append("<th>Intelligence</th>");
        html.append("<th>NumAtt</th>");
        html.append("<th>Size</th>");
        html.append("<th>Special</th>");
        html.append("<th>THAC0</th>");
        html.append("<th>Treasure</th>");
        html.append("<th>Width</th>");
        html.append("</tr>");
        for (CompMonsterTypeBean monster : monsters)
        {
            html.append("<tr>");
            try
            {
                html.append("<td>"+monster.getID()+"</td>");
                html.append("<td>"+monster.getName()+"</td>");
                html.append("<td>"+monster.getAC()+"</td>");
                html.append("<td>"+monster.getATT()+"</td>");
                html.append("<td>"+monster.getChallenge()+"</td>");
                html.append("<td>"+monster.getTerrain()+"</td>");
                html.append("<td>"+monster.getType()+"</td>");
                html.append("<td>"+monster.getAlignment()+"</td>");
                html.append("<td>"+monster.getAverageDamage()+"</td>");
                html.append("<td>"+(monster.getDetails() == null ? "" : monster.getDetails().toJSONString())+"</td>");
                html.append("<td>"+monster.getEnc()+"</td>");
                html.append("<td>"+monster.getEquivalentLevel()+"</td>");
                html.append("<td>"+monster.getExperienceBase()+"</td>");
                html.append("<td>"+monster.getExperiencePerHP()+"</td>");
                html.append("<td>"+monster.getFreq()+"</td>");
                html.append("<td>"+monster.getHD()+"</td>");
                html.append("<td>"+monster.getIntelligence()+"</td>");
                html.append("<td>"+monster.getNumAtt()+"</td>");
                html.append("<td>"+monster.getSize()+"</td>");
                html.append("<td>"+monster.getSpecial()+"</td>");
                html.append("<td>"+monster.getTHAC0()+"</td>");
                html.append("<td>");
                JSONArray treasures = (JSONArray)monster.getDetails().get("treasures");
                for (int i = 0; i < treasures.size(); i++)
                {
                    if (i > 0)
                        html.append("<br/>");
                    html.append(((JSONObject)treasures.get(i)).toJSONString());
                }
                html.append("</td>");
                html.append("<td>"+monster.getWidth()+"</td>");
            }
            catch (Exception e)
            {
                html.append("<td>"+e.getLocalizedMessage()+"</td>");
            }
            html.append("</tr>");
        }
        html.append("</table>");
        dumpHTML(resp, html.toString());
    }
    
    private void dumpFeatures(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        final Map<String,FeatureBean> featureIndex = FeatureLogic.getStaticFeatureIndex();
        String[] loc = featureIndex.keySet().toArray(new String[0]);
        Arrays.sort(loc);
        StringBuffer html = new StringBuffer();
        html.append("<h1>Features</h1>");
        html.append("<table>");
        html.append("<tr>");
        html.append("<th>Location</th>");
        html.append("<th>Name</th>");
        html.append("<th>Account</th>");
        html.append("</tr>");
        for (String l : loc)
        {
            html.append("<tr>");
            try
            {
                FeatureBean f = featureIndex.get(l);
                html.append("<td>"+l+"</td>");
                html.append("<td>"+f.getName()+"</td>");
                html.append("<td>"+f.getAccount()+"</td>");
            }
            catch (Exception e)
            {
                html.append("<td>"+e.getLocalizedMessage()+"</td>");
            }
            html.append("</tr>");
        }
        html.append("</table>");
        dumpHTML(resp, html.toString());
    }
    
    private void dumpVorticies(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        List<CoordBean> vorticies = GenerationLogic.getVorticies();
        StringBuffer html = new StringBuffer();
        html.append("<h1>Vorticies</h1>");
        html.append("<table>");
        html.append("<tr>");
        html.append("<th>Location</th>");
        html.append("<th>Description</th>");
        html.append("</tr>");
        for (CoordBean v : vorticies)
        {
            SquareBean sq = GenerationLogic.getSquare(v);
            html.append("<tr>");
            html.append("<td>"+v.toString()+"</td>");
            html.append("<td>"+sq.getDescription()+"</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        dumpHTML(resp, html.toString());
    }
    
    private void dumpHTML(HttpServletResponse resp, String html) throws IOException
    {
        resp.setContentType("text/html");
        if (!html.startsWith("<html>"))
            html = "<html>"+html;
        if (!html.endsWith("</html>"))
            html = html+"</html>";
        resp.getWriter().write(html);
    }
    
    private void dumpItems(HttpServletRequest req, HttpServletResponse resp) throws IOException
    {
        List<CompItemTypeBean> items = ItemLogic.getAllItemTypes(-1);
        Collections.sort(items, new Comparator<CompItemTypeBean>(){
            @Override
            public int compare(CompItemTypeBean o1, CompItemTypeBean o2)
            {
                int diff = o1.getType() - o2.getType();
                if (diff == 0)
                    diff = o1.getBaseID().compareTo(o2.getBaseID());
                if (diff == 0)
                    diff = o1.getID().compareTo(o2.getID());
                return 0;
            }
        });
        StringBuffer html = new StringBuffer();
        html.append("<h1>Items</h1>");
        html.append("<table>");
        html.append("<tr>");
        html.append("<th>Type</th>");
        html.append("<th>ID</th>");
        html.append("<th>Name</th>");
        html.append("<th>Ammo</th>");
        html.append("<th>DamageL</th>");
        html.append("<th>DamageSM</th>");
        html.append("<th>Special</th>");
        html.append("<th>ACMod</th>");
        html.append("<th>Cost</th>");
        html.append("<th>Count</th>");
        html.append("<th>Encumbrance</th>");
        html.append("<th>HandsNeeded</th>");
        html.append("<th>Magic</th>");
        html.append("<th>RateOfFire</th>");
        html.append("<th>Effect</th>");
        html.append("</tr>");
        for (CompItemTypeBean item : items)
        {
            html.append("<tr>");
            try
            {
                html.append("<td>"+item.getType()+"</td>");
                html.append("<td>"+item.getID()+"</td>");
                html.append("<td>"+item.getName()+"</td>");
                html.append("<td>"+item.getAmmo()+"</td>");
                html.append("<td>"+item.getDamageL()+"</td>");
                html.append("<td>"+item.getDamageSM()+"</td>");
                html.append("<td>"+item.getSpecial()+"</td>");
                html.append("<td>"+item.getACMod()+"</td>");
                html.append("<td>"+item.getCost()+"</td>");
                html.append("<td>"+item.getCount()+"</td>");
                html.append("<td>"+item.getEncumbrance()+"</td>");
                html.append("<td>"+item.getHandsNeeded()+"</td>");
                html.append("<td>"+item.getMagic()+"</td>");
                html.append("<td>"+item.getRateOfFire()+"</td>");
                html.append("<td>");
                for (CompEffectTypeBean e : item.getEffects())
                    html.append(e.getID()+" ");
                html.append("</td>");
            }
            catch (Exception e)
            {
                html.append("<td>"+e.getLocalizedMessage()+"</td>");
            }
            html.append("</tr>");
        }
        html.append("</table>");
        dumpHTML(resp, html.toString());
    }
    
    public static void debug(String string)
    {
        if (mInstance == null)
            System.out.println(string);
        else
            mInstance.log(string);
    }       
}
