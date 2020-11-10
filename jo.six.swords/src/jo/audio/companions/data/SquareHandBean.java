package jo.audio.companions.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.IJSONAble;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.audio.util.FromJSONLogic;
import jo.audio.util.model.data.AudioMessageBean;

public class SquareHandBean extends SquareBean implements IJSONAble
{
    private CoordBean   mOrds = new CoordBean();
    private List<AudioMessageBean> mDescription;
    private int         mTerrain;
    private int         mTerrainDepth;
    private int         mFeature;
    private int         mRoadNorth;
    private int         mRoadSouth;
    private int         mRoadEast;
    private int         mRoadWest;
    private int         mRiverNorth;
    private int         mRiverSouth;
    private int         mRiverEast;
    private int         mRiverWest;
    private float       mAltitude;
    private int         mChallenge;
    private int         mChallenge2;
    private List<SignPost> mSignposts = new ArrayList<>();
    private String      mLiege;
    private DemenseBean mDemense;
    
    // utilities
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("o", mOrds.toJSON());
        if (mTerrain != 0)
            json.put("t", mTerrain);
        if (mTerrainDepth != 0)
            json.put("td", mTerrainDepth);
        if (mFeature != 0)
            json.put("f", mFeature);
        if (mDescription != null)
        {
            JSONArray descriptions = new JSONArray();
            for (AudioMessageBean description : mDescription)
                descriptions.add(description.toJSON());
            json.put("d", descriptions);
        }
        StringBuffer road = new StringBuffer();
        road.append(String.valueOf(mRoadNorth));
        road.append(String.valueOf(mRoadSouth));
        road.append(String.valueOf(mRoadEast));
        road.append(String.valueOf(mRoadWest));
        road.append(String.valueOf(mRiverNorth));
        road.append(String.valueOf(mRiverSouth));
        road.append(String.valueOf(mRiverEast));
        road.append(String.valueOf(mRiverWest));
        if (!"00000000".equals(road.toString()))
            json.put("r", road.toString());
        json.put("a", mAltitude);
        json.put("c", mChallenge);
        json.put("c2", mChallenge2);
        if (mLiege != null)
            json.put("l", mLiege);
        JSONArray signs = new JSONArray();
        json.put("sp", signs);
        if (mSignposts != null)
            for (SignPost s : mSignposts)
                signs.add(s.toJSON());
        return json;
    }
    @Override
    public void fromJSON(JSONObject o)
    {
        mOrds.fromJSON((JSONObject)o.get("o"));
        if (o.containsKey("t"))
            mTerrain = FromJSONLogic.toInt(o, "t", mTerrain);
        if (o.containsKey("td"))
            mTerrainDepth = FromJSONLogic.toInt(o, "td", mTerrainDepth);
        if (o.containsKey("f"))
            mFeature = FromJSONLogic.toInt(o, "f", mFeature);
        if (o.containsKey("d"))
        {
            mDescription = new ArrayList<>();
            JSONArray descriptions = (JSONArray)o.get("d");
            for (int i = 0; i < descriptions.size(); i++)
                mDescription.add(new AudioMessageBean((JSONObject)descriptions.get(i)));
        }
        String road = (String)o.get("r");
        if (road != null)
        {
            mRoadNorth = road.charAt(0) - '0';
            mRoadSouth = road.charAt(1) - '0';
            mRoadEast = road.charAt(2) - '0';
            mRoadWest = road.charAt(3) - '0';
            mRiverNorth = road.charAt(4) - '0';
            mRiverSouth = road.charAt(5) - '0';
            mRiverEast = road.charAt(6) - '0';
            mRiverWest = road.charAt(7) - '0';
        }
        mAltitude = FromJSONLogic.toFloat(o, "a", mAltitude);
        mChallenge = FromJSONLogic.toInt(o, "c", mChallenge);
        mChallenge2 = FromJSONLogic.toInt(o, "c2", mChallenge2);
        mLiege = o.getString("l");
        mSignposts = new ArrayList<>();
        JSONArray signs = (JSONArray)o.get("sp");
        for (int i = 0; i < signs.size(); i++)
        {
            JSONObject s = (JSONObject)signs.get(i);
            SignPost sp = new SignPost();
            sp.fromJSON(s);
            mSignposts.add(sp);
        }
    }

    public void setDescription(AudioMessageBean description)
    {
        if (mDescription == null)
            mDescription = new ArrayList<>();
        mDescription.add(description);
    }

    // getters and setters
    
    public CoordBean getOrds()
    {
        return mOrds;
    }
    public void setOrds(CoordBean ords)
    {
        mOrds = ords;
    }
    public int getTerrain()
    {
        return mTerrain;
    }
    public void setTerrain(int terrain)
    {
        mTerrain = terrain;
    }
    public int getTerrainDepth()
    {
        return mTerrainDepth;
    }
    public void setTerrainDepth(int terrainDepth)
    {
        mTerrainDepth = terrainDepth;
    }
    public int getFeature()
    {
        return mFeature;
    }
    public void setFeature(int feature)
    {
        mFeature = feature;
    }
    public int getRoadNorth()
    {
        return mRoadNorth;
    }
    public void setRoadNorth(int roadNorth)
    {
        mRoadNorth = roadNorth;
    }
    public int getRoadSouth()
    {
        return mRoadSouth;
    }
    public void setRoadSouth(int roadSouth)
    {
        mRoadSouth = roadSouth;
    }
    public int getRoadEast()
    {
        return mRoadEast;
    }
    public void setRoadEast(int roadEast)
    {
        mRoadEast = roadEast;
    }
    public int getRoadWest()
    {
        return mRoadWest;
    }
    public void setRoadWest(int roadWest)
    {
        mRoadWest = roadWest;
    }
    public float getAltitude()
    {
        return mAltitude;
    }
    public void setAltitude(float altitude)
    {
        mAltitude = altitude;
    }
    public int getChallenge()
    {
        return mChallenge;
    }
    public void setChallenge(int challenge)
    {
        mChallenge = challenge;
    }
    public int getChallenge2()
    {
        return mChallenge2;
    }
    public void setChallenge2(int challenge2)
    {
        mChallenge2 = challenge2;
    }
    public List<SignPost> getSignposts()
    {
        return mSignposts;
    }
    public void setSignposts(List<SignPost> signposts)
    {
        mSignposts = signposts;
    }
    public int getRiverNorth()
    {
        return mRiverNorth;
    }
    public void setRiverNorth(int riverNorth)
    {
        mRiverNorth = riverNorth;
    }
    public int getRiverSouth()
    {
        return mRiverSouth;
    }
    public void setRiverSouth(int riverSouth)
    {
        mRiverSouth = riverSouth;
    }
    public int getRiverEast()
    {
        return mRiverEast;
    }
    public void setRiverEast(int riverEast)
    {
        mRiverEast = riverEast;
    }
    public int getRiverWest()
    {
        return mRiverWest;
    }
    public void setRiverWest(int riverWest)
    {
        mRiverWest = riverWest;
    }
    public String getLiege()
    {
        return mLiege;
    }
    public void setLiege(String liege)
    {
        mLiege = liege;
    }
    public DemenseBean getDemense()
    {
        return mDemense;
    }
    public void setDemense(DemenseBean demense)
    {
        mDemense = demense;
    }
    public List<AudioMessageBean> getDescription()
    {
        return mDescription;
    }
    public void setDescription(List<AudioMessageBean> description)
    {
        mDescription = description;
    }
}
