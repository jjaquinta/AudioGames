package jo.audio.thieves.tools.editor.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

public class PHouse implements Comparable<PHouse>
{
    private String     mID;
    private char[][][] mFloors;
    
    // utilities
    @SuppressWarnings("unchecked")
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("ID", mID);
        JSONArray jfloors = new JSONArray();
        json.put("floors", jfloors);
        for (char[][] floor : mFloors)
        {
            JSONArray jfloor = new JSONArray();
            jfloors.add(jfloor);
            for (char[] row : floor)
                jfloor.add(new String(row));
        }
        return json;
    }
    
    public void fromJSON(JSONObject json)
    {
        mID = json.getString("ID");
        JSONArray floors = JSONUtils.getArray(json, "floors");
        mFloors = new char[floors.size()][][];
        for (int i = 0; i < floors.size(); i++)
        {
            JSONArray floor = (JSONArray)floors.get(i);
            mFloors[i] = new char[floor.size()][];
            for (int j = 0; j < floor.size(); j++)
            {
                String row = (String)floor.get(j);
                mFloors[i][j] = row.toCharArray();
            }
        }
    }

    @Override
    public int compareTo(PHouse o)
    {
        return mID.compareTo(o.getID());
    }
    
    @Override
    public String toString()
    {
        return mID;
    }

    // getters and setters


    public String getID()
    {
        return mID;
    }

    public void setID(String iD)
    {
        mID = iD;
    }

    public char[][][] getFloors()
    {
        return mFloors;
    }

    public void setFloors(char[][][] floors)
    {
        mFloors = floors;
    }
}
