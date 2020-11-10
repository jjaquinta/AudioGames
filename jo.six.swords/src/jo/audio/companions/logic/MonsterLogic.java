package jo.audio.companions.logic;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompMonsterInstanceBean;
import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.data.CompTreasuresBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.HitDiceBean;
import jo.audio.companions.data.IMonsterType;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.util.utils.DebugUtils;
import jo.util.utils.io.ResourceUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.StringUtils;

public class MonsterLogic
{
    private static final List<CompMonsterTypeBean> mMonsterTypes = new ArrayList<>();
    private static final List<CompMonsterTypeBean> mAllMonsterTypes = new ArrayList<>();
    private static final Map<String,CompMonsterTypeBean> mMonsterIndex = new HashMap<>();
    private static final Map<Integer,Map<Integer,List<CompMonsterTypeBean>>> mMonsterTypesByTerrainByChallenge = new HashMap<>();
    private static final Map<Integer,Map<Integer,Integer>> mFrequencyTotalsByTerrainByChallenge = new HashMap<>();
    private static final Map<String,Map<Integer,List<CompMonsterTypeBean>>> mMonsterTypesByTypeByChallenge = new HashMap<>();
    private static final String[] DRAGON_SIZE = {
            "Small", "", "Huge",
    };

    private static final String[] DRAGON_AGE = {
            "Very Young", "Young", "Young Adult", "Adult", "Mature Adult", "Old", "Very Old", "Ancient",
    };
    
    static
    {
        MonsterLogic.readItems();
    }
    
    private static void readItems()
    {
        try
        {
            InputStream is = ResourceUtils.loadSystemResourceStream("monsterTypes.json", CompanionsModelConst.class);
            JSONObject json = (JSONObject)JSONUtils.PARSER.parse(new InputStreamReader(is, "utf-8"));
            is.close();
            JSONArray items = JSONUtils.getArray(json, "monsterTypes");
            indexMonsters(items);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void indexMonsters(JSONArray items)
    {
        List<CompMonsterTypeBean> toDerive = new ArrayList<>();
        for (int i = 0; i < items.size(); i++)
        {
            JSONObject item = (JSONObject)items.get(i);
            CompMonsterTypeBean jitem = new CompMonsterTypeBean();
            jitem.fromJSON(item);
            if (jitem.getTerrain().equals("Any"))
                jitem.setTerrain("Plains,Hills,Mountains,Artic,Forest,Desert,Jungle,Swamp");
            List<CompMonsterTypeBean> monsters;
            if ("Dragon".equals(jitem.getType()))
                monsters = multiplexDragon(jitem);
            else
                monsters = multiplexNormal(jitem);
            for (CompMonsterTypeBean m : monsters)
                indexMonster(m);
            CompMonsterTypeBean m = monsters.get(0);
            toDerive.add(m);
        }
        for (CompMonsterTypeBean m : toDerive)
        {
            createDerivitives(m, "leaders");
            createDerivitives(m, "appearsWith");
        }
    }
    
    private static void createDerivitives(CompMonsterTypeBean m, String detailsKey)
    {
        if ((m.getDetails() == null) || !m.getDetails().containsKey(detailsKey))
            return;
        JSONObject item = m.toJSON();
        JSONArray others = JSONUtils.getArray(m.getDetails(), detailsKey);
        for (int j = 0; j < others.size(); j++)
        {
            JSONObject leader = (JSONObject)others.get(j);
            createDerivitive(item, leader);
        }
    }
    
    private static void createDerivitive(JSONObject baseData, JSONObject overrideData)
    {
        JSONObject leaderItem = (JSONObject)JSONUtils.deepCopy(baseData);
        CompMonsterTypeBean jleaderItem = new CompMonsterTypeBean();
        jleaderItem.fromJSON(leaderItem); // base values
        String derivedID = overrideData.getString("id");
        if ((derivedID != null) && mMonsterIndex.containsKey(derivedID))
        {
            CompMonsterTypeBean derivedCreature = mMonsterIndex.get(derivedID);
            jleaderItem.fromJSON(derivedCreature.toJSON());
        }
        jleaderItem.fromJSON(overrideData); // override
        jleaderItem.setID(jleaderItem.getName());
        mMonsterTypes.add(jleaderItem);
        mMonsterIndex.put(jleaderItem.getID(), jleaderItem);

    }

    private static void indexMonster(CompMonsterTypeBean m)
    {
        mMonsterTypes.add(m);
        mMonsterIndex.put(m.getID(), m);
        Map<Integer,List<CompMonsterTypeBean>> byTypeByChallenge = mMonsterTypesByTypeByChallenge.get(m.getType());
        if (byTypeByChallenge == null)
        {
            byTypeByChallenge = new HashMap<>();
            mMonsterTypesByTypeByChallenge.put(m.getType(), byTypeByChallenge);
        }
        mAllMonsterTypes.add(m);
        int challengeRating = m.getChallenge();
        List<CompMonsterTypeBean> byChallenge = byTypeByChallenge.get(challengeRating);
        if (byChallenge == null)
        {
            byChallenge = new ArrayList<>();
            byTypeByChallenge.put(challengeRating, byChallenge);
        }
        byChallenge.add(m);
        boolean doneAny = false;
        for (StringTokenizer st = new StringTokenizer(m.getTerrain(), ","); st.hasMoreTokens(); )
        {
            String terrain = st.nextToken();
            switch (terrain)
            {
                case "Plains":
                    addToTerrain(CompConstLogic.TERRAIN_PLAINS, m);
                    break;
                case "Hills":
                    addToTerrain(CompConstLogic.TERRAIN_HILLS, m);
                    break;
                case "Mountains":
                    addToTerrain(CompConstLogic.TERRAIN_MOUNTAINS, m);
                    break;
                case "Artic":
                    addToTerrain(CompConstLogic.TERRAIN_ARTIC, m);
                    break;
                case "Forest":
                    addToTerrain(CompConstLogic.TERRAIN_FOREST, m);
                    break;
                case "Desert":
                    addToTerrain(CompConstLogic.TERRAIN_DESERT, m);
                    break;
                case "Jungle":
                    addToTerrain(CompConstLogic.TERRAIN_JUNGLE, m);
                    break;
                case "Swamp":
                    addToTerrain(CompConstLogic.TERRAIN_SWAMP, m);
                    break;
                case "Freshwater":
                    addToTerrain(CompConstLogic.TERRAIN_FRESHWATER, m);
                    break;
                case "Saltwater":
                    addToTerrain(CompConstLogic.TERRAIN_SALTWATER, m);
                    break;
                case "Any":
                    addToTerrain(CompConstLogic.TERRAIN_PLAINS, m);
                    addToTerrain(CompConstLogic.TERRAIN_HILLS, m);
                    addToTerrain(CompConstLogic.TERRAIN_MOUNTAINS, m);
                    addToTerrain(CompConstLogic.TERRAIN_ARTIC, m);
                    addToTerrain(CompConstLogic.TERRAIN_FOREST, m);
                    addToTerrain(CompConstLogic.TERRAIN_DESERT, m);
                    addToTerrain(CompConstLogic.TERRAIN_JUNGLE, m);
                    addToTerrain(CompConstLogic.TERRAIN_SWAMP, m);
                    break;
                default:
                    DebugUtils.trace("Unhandled terrain '"+terrain+"'");
                    break;
            }
            doneAny = true;
        }
        if (!doneAny)
            System.out.println("Did not do any "+m.getName());
    }

    private static List<CompMonsterTypeBean> multiplexDragon(CompMonsterTypeBean monster)
    {
        List<CompMonsterTypeBean> monsters = new ArrayList<>();
        int hd = IntegerUtils.parseInt(monster.getHD());
        float baseXP = monster.getExperienceBase() + monster.getExperiencePerHP()*monster.getHDRoll().average();
        //System.out.println(monster.getName()+", basexp="+baseXP);
        for (int size = 0; size < 3; size++)
            for (int age = 0; age < 8; age++)
            {
                CompMonsterTypeBean mm = new CompMonsterTypeBean();
                mm.fromJSON(monster.toJSON());
                mm.setID(mm.getName()+"$"+size+"_"+age);
                int hits = (hd - 1 + size)*(age + 1);
                String verb = null;
                String damageType = null;
                switch (mm.getName())
                {
                    case "Black Dragon":
                        verb = CompanionsModelConst.TEXT_BREATHES_ACID;
                        damageType = "acid";
                        break;
                    case "Blue Dragon":
                        verb = CompanionsModelConst.TEXT_BREATHES_LIGHTNING;
                        damageType = "lightning";
                        break;
                    case "Brass Dragon":
                        verb = CompanionsModelConst.TEXT_BREATHES_SONIC;
                        damageType = "sonic";
                        break;
                    case "Bronze Dragon":
                        verb = CompanionsModelConst.TEXT_BREATHES_LIGHTNING;
                        damageType = "lightning";
                        break;
                    case "Copper Dragon":
                          verb = CompanionsModelConst.TEXT_BREATHES_ACID;
                          damageType = "acid";
                          break;
                    case "Gold Dragon":
                        verb = CompanionsModelConst.TEXT_BREATHES_FIRE;
                        damageType = "fire";
                        break;
                    case "Green Dragon":
                        verb = CompanionsModelConst.TEXT_BREATHES_POISON;
                        damageType = "poison";
                        break;
                    case "Red Dragon":
                        verb = CompanionsModelConst.TEXT_BREATHES_FIRE;
                        damageType = "fire";
                        break;
                    case "White Dragon":
                        verb = CompanionsModelConst.TEXT_BREATHES_FROST;
                        damageType = "frost";
                        break;
                    case "Silver Dragon":
                        verb = CompanionsModelConst.TEXT_BREATHES_FROST;
                        damageType = "frost";
                        break;
                }
                mm.setHD(hits+"hp");
                mm.setATT(mm.getATT()+"/"+(hits/2)+"{verb="+verb+",damageType="+damageType+",maxTimes=3,chanceUse=20,plusToHit=20,saveForHalf=bw}");
                mm.setNumAtt(String.valueOf(IntegerUtils.parseInt(mm.getNumAtt())+1));
                mm.setName((DRAGON_SIZE[size]+" "+DRAGON_AGE[age]+" "+mm.getName()).trim());
                float thisXP = mm.getExperienceBase() + mm.getExperiencePerHP()*mm.getHDRoll().average();
                float mult = thisXP/baseXP;
                nerfTreasures(mm, mult, true);
                monsters.addAll(multiplexNormal(mm));
            }
        return monsters;
    }

    private static void nerfTreasures(CompMonsterTypeBean mm, float mult, boolean individual)
    {
        JSONObject details = mm.getDetails();
        if (details == null)
        {
            details = new JSONObject();
            details.put("treasures", new JSONArray());
            mm.setDetails(details);
        }
        details = (JSONObject)JSONUtils.deepCopy(details);
        mm.setDetails(details);
        JSONArray treasures = (JSONArray)details.get("treasures");
        for (int i = 0; i < treasures.size(); i++)
        {
            JSONObject treasure = (JSONObject)treasures.get(i);
            nerfTreasure(treasure, mult, individual);
        }
    }
    
    private static void nerfTreasure(JSONObject treasure, float mult, boolean individual)
    {        
        if (!individual && !BooleanUtils.parseBoolean(treasure.get("inLair")))
                return;
        String number = treasure.getString("number");
        int o = number.indexOf('x');
        int m = IntegerUtils.parseInt(number.substring(o + 1));
        if (m >= 1000)
        {
            m = (int)(m*mult);
            number = number.substring(0, o + 1) + m;
            treasure.put("number", number);
        }
        else
        {
            int chance = TreasureLogic.getChance(treasure);
            chance = (int)(chance*mult);
            if (chance <= 0)
                chance = 1;
            else if (chance > 99)
                chance = 99;
            treasure.put("chance", chance);
        }
    }

    private static List<CompMonsterTypeBean> multiplexNormal(CompMonsterTypeBean monster)
    {
        List<CompMonsterTypeBean> monsters = new ArrayList<>();
        DiceRollBean r = monster.getEncRoll();
        float baseEnc = r.average();
        for (int n = 1; n <= r.getNumber(); n++)
        {
            CompMonsterTypeBean mm = new CompMonsterTypeBean();
            mm.fromJSON(monster.toJSON());
            mm.setID(mm.getName()+"$"+n);
            DiceRollBean mmEnc = new DiceRollBean(r);
            mmEnc.setNumber(n);
            mm.setEnc(mmEnc.toString());
            float thisEnc = mmEnc.average();
            float mult = thisEnc/baseEnc;
            nerfTreasures(mm, mult, false);
            int challengeRating = getChallengeRating(mm);
            mm.setChallenge(challengeRating);
            if ((monsters.size() == 0) || (mm.getChallenge() != monsters.get(monsters.size() - 1).getChallenge()))
                monsters.add(mm);
        }
        return monsters;
    }
    
    private static void addToTerrain(int terrain,
            CompMonsterTypeBean mm)
    {
        Map<Integer,List<CompMonsterTypeBean>> monstersByChallenge = mMonsterTypesByTerrainByChallenge.get(terrain);
        if (monstersByChallenge == null)
        {
            monstersByChallenge = new HashMap<>();
            mMonsterTypesByTerrainByChallenge.put(terrain, monstersByChallenge);
        }
        Map<Integer,Integer> frequencyTotalsByChallenge = mFrequencyTotalsByTerrainByChallenge.get(terrain);
        if (frequencyTotalsByChallenge == null)
        {
            frequencyTotalsByChallenge = new HashMap<>();
            mFrequencyTotalsByTerrainByChallenge.put(terrain, frequencyTotalsByChallenge);
        }
        int cr = mm.getChallenge();
        List<CompMonsterTypeBean> monsters = monstersByChallenge.get(cr);
        if (monsters == null)
        {
            monsters = new ArrayList<>();
            monstersByChallenge.put(cr, monsters);
        }
        monsters.add(mm);
        int total = (frequencyTotalsByChallenge.containsKey(cr) ? frequencyTotalsByChallenge.get(cr) : 0);
        total += mm.getFrequency();
        frequencyTotalsByChallenge.put(cr, total);
    }

    public static int getChallengeRating(IMonsterType monst)
    {
        for (int partyLevel = 1; partyLevel <= 20; partyLevel++)
        {
            float playerHP = 33*partyLevel;
            float playerAC = (partyLevel < 4) ? (11 - 2*partyLevel) : (3 - (partyLevel - 4)/2.0f);
            float playerTH = (21 - partyLevel) - Integer.parseInt(monst.getAC());
            float pc = (21 - playerTH)/20.0f;
            if (pc > 1.0f)
                pc = 1.0f;
            else if (pc < 0)
                pc = 0.0f;
            float playerBaseDam = 4.5f*6;
            if (partyLevel > 10)
                playerBaseDam *= 2;
            else if (partyLevel > 5)
                playerBaseDam *= 1.5f;
            float playerDPR = playerBaseDam*pc;
            float margin = monst.defeatMargin(playerHP, playerDPR, (int)playerAC);
            if (margin <= -2)
                return partyLevel;
        }
        return 21;
    }
    
    public static CompMonsterTypeBean getMonsterType(String id)
    {
        if (id == null)
            return null;
        return mMonsterIndex.get(id);
    }
    
    public static CompMonsterTypeBean getAnyMonsterType(String id)
    {
        if (id == null)
            return null;
        if (mMonsterIndex.containsKey(id))
            return mMonsterIndex.get(id);
        int o = id.indexOf('$');
        if (o > 0)
        {
            id = id.substring(0, o);
            if (mMonsterIndex.containsKey(id))
                return mMonsterIndex.get(id);
        }
        if (mMonsterIndex.containsKey(id+"$1"))
            return mMonsterIndex.get(id+"$1");
        for (String key : mMonsterIndex.keySet())
            if (key.startsWith(id+"$"))
                return mMonsterIndex.get(key);
        return null;
    }
    
    public static String getLesserMonster(String id)
    {
        return getAltMonster(id, -1);
    }
    
    public static String getGreaterMonster(String id)
    {
        return getAltMonster(id, 1);
    }
    
    public static String getAltMonster(String id, int delta)
    {
        int o = id.indexOf('$');
        if (o < 0)
            return id;
        int num = Integer.parseInt(id.substring(o + 1));
        String newID = id.substring(0, o+1) + String.valueOf(num + delta);
        if (mMonsterIndex.containsKey(newID))
            return newID;
        return id;
    }
    
    public static CompMonsterTypeBean getFromTerrain(SquareBean sq, int partyRating)
    {
        Map<Integer, Integer> byTerrain = mFrequencyTotalsByTerrainByChallenge.get(sq.getTerrain());
        if (byTerrain == null)
            throw new IllegalStateException("No monsters for terrain #"+sq.getTerrain());
        int challenge = partyRating + sq.getChallenge2() + 1;
        if (challenge <= 0)
            challenge = 1;
        while (!byTerrain.containsKey(challenge))
        {
            challenge--;
            if (challenge == 0)
                throw new IllegalStateException("No monsters for terrain #"+sq.getTerrain()+" challenge=0 to "+sq.getChallenge());
        }
        Integer byChallenge = byTerrain.get(challenge);
        int roll = BaseUserState.RND.nextInt(byChallenge);
        for (CompMonsterTypeBean monst : mMonsterTypesByTerrainByChallenge.get(sq.getTerrain()).get(challenge))
        {
            roll -= monst.getFrequency();
            if (roll < 0)
                return monst;
        }
        throw new IllegalStateException("Fell off table");
    }
    
    public static CompMonsterTypeBean getFromType(int challenge, String type)
    {
        Map<Integer, List<CompMonsterTypeBean>> byType = mMonsterTypesByTypeByChallenge.get(type);
        if (byType == null)
            throw new IllegalStateException("No such type "+type);
        List<CompMonsterTypeBean> monsters = byType.get(challenge);
        while ((monsters == null) && (--challenge > 0))
            monsters = byType.get(challenge);
        while ((monsters == null) && (++challenge < 16))
            monsters = byType.get(challenge);
        if (monsters == null)
            throw new IllegalStateException("No monsters for type "+type);
        return monsters.get(BaseUserState.RND.nextInt(monsters.size()));
    }

    public static CompMonsterTypeBean findMonster(SquareBean sq, int partyRating, Boolean treasure, Boolean populous)
    {
        for (int i = 0; i < 3; i++)
        {
            CompMonsterTypeBean monster = MonsterLogic.getFromTerrain(sq, partyRating);
            if (treasure != null)
                if (treasure.booleanValue() == (((JSONArray)monster.getDetails().get("treasures")).size() > 0))
                    continue;
            if (populous != null)
                if (populous.booleanValue() != (monster.getEncRoll().getNumber() > 1))
                    continue;
            return monster;
        }
        return MonsterLogic.getFromTerrain(sq, 0);
    }

    public static CompMonsterInstanceBean createInstance(CompUserBean user, String id)
    {
        //DebugUtils.trace("Creating monster instance of type '"+id+"'");
        if (!mMonsterIndex.containsKey(id))
            throw new IllegalArgumentException("No such monster '"+id+"'");
        CompMonsterTypeBean monsterType = getMonsterType(id);
        //DebugUtils.trace("Retrieved type '"+monsterType+"'");
        CompMonsterInstanceBean inst = new CompMonsterInstanceBean();
        inst.setID(id);
        HitDiceBean hdRoll = monsterType.getHDRoll();
        inst.setHitPoints(hdRoll.roll(BaseUserState.RND));
        if ("Lycanthrope".equals(monsterType.getType()))
        {
            int phase = CompConstLogic.getMoonPhase(user.getTotalTime());
            if (phase == CompConstLogic.MOON_PHASE_NEW)
                inst.setHitPoints(inst.getHitPoints() - hdRoll.getHitDice().getNumber());
            else if (phase == CompConstLogic.MOON_PHASE_FULL)
                inst.setHitPoints(inst.getHitPoints() + hdRoll.getHitDice().getNumber());
        }
        if (inst.getHitPoints() <= 0)
            inst.setHitPoints(1);
        inst.setFullHitPoints(inst.getHitPoints());
        return inst;
    }
    
    public static List<CompMonsterTypeBean> getAllTypes()
    {
        return mAllMonsterTypes;
    }
    
    public static Collection<CompMonsterTypeBean> getIndexedTypes()
    {
        return mMonsterIndex.values();
    }

    public static void main(String[] argv)
    {
        System.out.println(mMonsterTypes.size()+" items read in.");
        /*
        for (Integer terrain : mMonsterTypesByTerrainByChallenge.keySet())
        {
            System.out.println("  "+CompConstLogic.TERRAIN_NAMES[terrain]+": ");
            Map<Integer,List<CompMonsterTypeBean>> byChallenge = mMonsterTypesByTerrainByChallenge.get(terrain);
            Integer[] keys = byChallenge.keySet().toArray(new Integer[0]);
            Arrays.sort(keys);
            for (Integer cr : keys)
            {
                System.out.println("    CR"+cr+": "+byChallenge.get(cr).size()+" encounters");
                if (cr.intValue() == 1)
                    for (CompMonsterTypeBean enc : byChallenge.get(cr))
                        System.out.println("      "+enc.getName()+" x "+enc.getEnc()+" ("+enc.getEncRoll()+")");
            }
        }
        */
        /*
        for (CompMonsterTypeBean m : mMonsterTypes)
            if (m.getName().indexOf("Bugbear") >= 0)
            {
                System.out.println(m.getName()+" CR:"+m.getChallenge()+", HD:"+m.getHD()+", HDRoll:"+m.getHDRoll()+", Enc:"+m.getEnc()+", EncRoll:"+m.getEncRoll()+", id="+m.getID());
                if (getMonsterType(m.getID()) == null)
                    System.out.println("  !! not indexed");
            }
            */
        /*
            */
        System.out.println("\"Gold\",\"XP\",\"Ratio\",\"Lair\",\"Name\",\"ID\"");
        for (CompMonsterTypeBean m : mMonsterTypes)
        {
            float xp = m.getExperienceBase() + m.getExperiencePerHP()*m.getHDRoll().average();
            float numAvg = 0;
            float gold = 0;
            float lair = 0;
            for (int i = 0; i < 100; i++)
            {
                int num = m.getEnc(BaseUserState.RND);
                numAvg += num;
                CompTreasuresBean loot = new CompTreasuresBean();
                TreasureLogic.rollIndividualTreasures(m, num, BaseUserState.RND, loot);
                gold += loot.getTotalValue()/num;
                loot = new CompTreasuresBean();
                TreasureLogic.rollLairTreasure(m, BaseUserState.RND, loot);
                lair += loot.getTotalValue();
            }
            gold /= 100;
            numAvg /= 100;
            lair /= 100;
            lair *= IntegerUtils.parseInt(StringUtils.digitize(m.getLairProbability()))/100;
            lair /= numAvg;
            float gpx = gold/xp;
            System.out.println(gold+","+xp+","+gpx+","+lair+",\""+m.getName()+"\",\""+m.getID()+"\"");
        }
    }
}
