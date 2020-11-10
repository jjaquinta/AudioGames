package jo.audio.companions.data;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.GenerationLogic;
import jo.audio.util.FromJSONLogic;
import jo.audio.util.ToJSONLogic;

public class RegionGenBean extends RegionBean
{
    private CoordBean mOrds;
    private int mPredominantRace;
    private int mPredominantTerrain;
    private int mGovernmentalStructure;
    private String mName;
    private DemenseBean mLiege;
    private RegionBean  mLord;
    private List<RegionBean> mVassals = new ArrayList<>();
    private SquareBean[][] mSquares = new SquareBean[CompConstLogic.SQUARES_PER_REGION][CompConstLogic.SQUARES_PER_REGION];
    private boolean mDetails = false;

    // utilities
    
    public String getTitle()
    {
        if ((mLord == null) && (mVassals.size() == 0))
            return mName;
        switch (mGovernmentalStructure)
        {
            case CompConstLogic.GOVERNMENT_EMPIRE:
                return "Empire of "+mName;
            case CompConstLogic.GOVERNMENT_KINGDOM:
                return "Kingdom of "+mName;
            case CompConstLogic.GOVERNMENT_DUCHY:
                return "Duchy of "+mName;
            case CompConstLogic.GOVERNMENT_COUNTY:
                return mName+" County";
        }
        return mName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject toJSON()
    {
        RegionBean  lord = mLord;
        List<RegionBean> vassals = mVassals;
        SquareBean[][] squares = mSquares;
        mLord = null;
        mVassals = null;
        mSquares = squares;
        JSONObject ret = ToJSONLogic.toJSONFromBean(this);
        if (lord != null)
            ret.put("lordOrds", ToJSONLogic.toJSONFromBean(mLord.getOrds()));
        JSONArray v = new JSONArray();
        ret.put("vassalsOrds", v);
        if ((vassals != null) && (vassals.size() > 0))
            for (RegionBean vassal : vassals)
                v.add(ToJSONLogic.toJSONFromBean(vassal.getOrds()));
        JSONArray s1 = new JSONArray();
        ret.put("squares", s1);
        for (int x = 0; x < squares.length; x++)
        {
            JSONArray s2 = new JSONArray();
            s1.add(s2);
            for (int y = 0; y < squares[x].length; y++)
                s2.add(ToJSONLogic.toJSONFromBean(squares[x][y]));
        }
        mLord = lord;
        mVassals = vassals;
        mSquares = squares;
        return ret;
    }

    @Override
    public void fromJSON(JSONObject o)
    {
        FromJSONLogic.fromJSON(this, o);
        JSONObject lordOrds = (JSONObject)o.get("lordOrds");
        if (lordOrds != null)
        {
            CoordBean lo = new CoordBean();
            FromJSONLogic.fromJSON(lo, lordOrds);
            mLord = GenerationLogic.getRegion(lo);
        }
        JSONArray vassalsOrds = (JSONArray)o.get("vassalsOrds");
        for (int i = 0; i < vassalsOrds.size(); i++)
        {
            CoordBean vo = new CoordBean();
            FromJSONLogic.fromJSON(vo, (JSONObject)vassalsOrds.get(i));
            mVassals.add(GenerationLogic.getRegion(vo));
        }
    }
    
    @Override
    public int hashCode()
    {
        return mOrds.hashCode();
    }
    
    public void setSquare(int dx, int dy, SquareBean square)
    {
        mSquares[dx][dy] = square;
    }
    
    public void setSquare(CoordBean ords, SquareBean square)
    {
        int dx = (ords.getX() - mOrds.getX());
        int dy = (ords.getY() - mOrds.getY());
        setSquare(dx, dy, square);
    }

    // getters and setters

    public int getPredominantRace()
    {
        return mPredominantRace;
    }
    public void setPredominantRace(int predominantRace)
    {
        mPredominantRace = predominantRace;
    }
    public CoordBean getOrds()
    {
        return mOrds;
    }
    public void setOrds(CoordBean ords)
    {
        mOrds = ords;
    }
    public int getPredominantTerrain()
    {
        return mPredominantTerrain;
    }
    public void setPredominantTerrain(int predominantTerrain)
    {
        mPredominantTerrain = predominantTerrain;
    }

    public SquareBean[][] getSquares()
    {
        return mSquares;
    }

    public void setSquares(SquareBean[][] squares)
    {
        mSquares = squares;
    }

    public int getGovernmentalStructure()
    {
        return mGovernmentalStructure;
    }

    public void setGovernmentalStructure(int governmentalStructure)
    {
        mGovernmentalStructure = governmentalStructure;
    }

    public RegionBean getLord()
    {
        return mLord;
    }

    public void setLord(RegionBean lord)
    {
        mLord = lord;
    }

    public List<RegionBean> getVassals()
    {
        return mVassals;
    }

    public void setVassals(List<RegionBean> vassals)
    {
        mVassals = vassals;
    }

    public boolean isDetails()
    {
        return mDetails;
    }

    public void setDetails(boolean details)
    {
        mDetails = details;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public DemenseBean getLiege()
    {
        return mLiege;
    }

    public void setLiege(DemenseBean liege)
    {
        mLiege = liege;
    }
}
