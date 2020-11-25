package jo.audio.thieves.data.gen;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

public class Actionable
{
    private int mClimbWallsMod;
    private int mFindTrapsMod;
    private int mOpenLocksMod;
    private int mMoveSilentlyMod;
    private int mHideInShadowsMod;
    
    // utilities
    public void fromJSON(JSONObject o)
    {
        if (o.containsKey("ClimbWallsMod"))
            setClimbWallsMod(JSONUtils.getInt(o, "ClimbWallsMod"));
        if (o.containsKey("FindTrapsMod"))
            setFindTrapsMod(JSONUtils.getInt(o, "FindTrapsMod"));
        if (o.containsKey("OpenLocksMod"))
            setOpenLocksMod(JSONUtils.getInt(o, "OpenLocksMod"));
        if (o.containsKey("MoveSilentlyMod"))
            setMoveSilentlyMod(JSONUtils.getInt(o, "MoveSilentlyMod"));
        if (o.containsKey("HideInShadowsMod"))
            setHideInShadowsMod(JSONUtils.getInt(o, "HideInShadowsMod"));
    }

    // getters and setters
    public int getClimbWallsMod()
    {
        return mClimbWallsMod;
    }
    public void setClimbWallsMod(int climbWallsMod)
    {
        mClimbWallsMod = climbWallsMod;
    }
    public int getFindTrapsMod()
    {
        return mFindTrapsMod;
    }
    public void setFindTrapsMod(int findTrapsMod)
    {
        mFindTrapsMod = findTrapsMod;
    }
    public int getOpenLocksMod()
    {
        return mOpenLocksMod;
    }
    public void setOpenLocksMod(int openLocksMod)
    {
        mOpenLocksMod = openLocksMod;
    }
    public int getMoveSilentlyMod()
    {
        return mMoveSilentlyMod;
    }
    public void setMoveSilentlyMod(int moveSilentlyMod)
    {
        mMoveSilentlyMod = moveSilentlyMod;
    }
    public int getHideInShadowsMod()
    {
        return mHideInShadowsMod;
    }
    public void setHideInShadowsMod(int hideInShadowsMod)
    {
        mHideInShadowsMod = hideInShadowsMod;
    }
}
