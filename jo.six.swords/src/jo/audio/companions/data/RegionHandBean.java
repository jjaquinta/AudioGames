package jo.audio.companions.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.audio.companions.logic.CompConstLogic;
import jo.audio.util.FromJSONLogic;
import jo.audio.util.ToJSONLogic;

public class RegionHandBean extends RegionBean
{
    private CoordBean   mOrds = new CoordBean();
    private String      mTitle;
    private DemenseBean mLiege;
    private int         mPredominantRace;
    private int         mGovernmentalStructure;
    private SquareHandBean[][] mSquares;

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("ords", ToJSONLogic.toJSON(mOrds));
        json.put("title", mTitle);
        if (mLiege != null)
            json.put("liege", mLiege.getID());
        json.put("predominantRace", mPredominantRace);
        json.put("governmentalStructure", mGovernmentalStructure);
        JSONArray squares = new JSONArray();
        json.put("squares", squares);
        for (int x = 0; x < mSquares.length; x++)
            for (int y = 0; y < mSquares[x].length; y++)
                squares.add(ToJSONLogic.toJSON(mSquares[x][y]));
        return json;
    }

    @Override
    public void fromJSON(JSONObject o)
    {
        mOrds.fromJSON((JSONObject)o.get("ords"));
        mTitle = (String)o.getString("title");
        mPredominantRace = FromJSONLogic.toInt(o, "predominantRace", mPredominantRace);
        mGovernmentalStructure = FromJSONLogic.toInt(o, "governmentalStructure", mGovernmentalStructure);
        JSONArray squares = (JSONArray)o.get("squares");
        mSquares = new SquareHandBean[CompConstLogic.SQUARES_PER_REGION][CompConstLogic.SQUARES_PER_REGION];
        for (int x = 0; x < mSquares.length; x++)
            for (int y = 0; y < mSquares[x].length; y++)
            {
                mSquares[x][y] = new SquareHandBean();
                JSONObject json = (JSONObject)squares.get(y*CompConstLogic.SQUARES_PER_REGION + x);
                mSquares[x][y].fromJSON(json);
            }
    }

    public CoordBean getOrds()
    {
        return mOrds;
    }

    public void setOrds(CoordBean ords)
    {
        mOrds = ords;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String title)
    {
        mTitle = title;
    }

    public int getPredominantRace()
    {
        return mPredominantRace;
    }

    public void setPredominantRace(int predominantRace)
    {
        mPredominantRace = predominantRace;
    }

    public int getGovernmentalStructure()
    {
        return mGovernmentalStructure;
    }

    public void setGovernmentalStructure(int governmentalStructure)
    {
        mGovernmentalStructure = governmentalStructure;
    }

    public SquareHandBean[][] getSquares()
    {
        return mSquares;
    }

    public void setSquares(SquareHandBean[][] squares)
    {
        mSquares = squares;
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
