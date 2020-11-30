package jo.audio.thieves.tools.editor.data;

import java.awt.Color;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

public class PTile
{
    public static final int LOCATION = 1;
    public static final int APATURE  = 2;

    private int             mType;
    private String          mID;
    private String          mName;
    private String          mDescription;
    private int             mClimbWallsMod;
    private int             mFindTrapsMod;
    private int             mOpenLocksMod;
    private int             mMoveSilentlyMod;
    private int             mHideInShadowsMod;
    private String          mChar;
    private Color           mColor   = Color.LIGHT_GRAY;
    private boolean         mPlaceholder;
    private boolean         mInside;
    private boolean         mBedroom;

    // utilities
    @Override
    public String toString()
    {
        return mID;
    }

    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("ID", mID);
        if (mClimbWallsMod != 0)
            json.put("ClimbWallsMod", mClimbWallsMod);
        if (mFindTrapsMod != 0)
            json.put("FindTrapsMod", mFindTrapsMod);
        if (mOpenLocksMod != 0)
            json.put("OpenLocksMod", mOpenLocksMod);
        if (mMoveSilentlyMod != 0)
            json.put("MoveSilentlyMod", mMoveSilentlyMod);
        if (mHideInShadowsMod != 0)
            json.put("HideInShadowsMod", mHideInShadowsMod);
        if (mType == LOCATION)
        {
            if (mInside)
                json.put("Inside", mInside);
            if (mBedroom)
                json.put("Bedroom", mBedroom);
        }
        return json;
    }

    public void fromJSON(JSONObject json)
    {
        mID = json.getString("ID");
        mClimbWallsMod = json.getInt("ClimbWallsMod");
        mFindTrapsMod = json.getInt("FindTrapsMod");
        mOpenLocksMod = json.getInt("OpenLocksMod");
        mMoveSilentlyMod = json.getInt("MoveSilentlyMod");
        mHideInShadowsMod = json.getInt("HideInShadowsMod");
        mInside = JSONUtils.getBoolean(json, "Inside");
        mBedroom = JSONUtils.getBoolean(json, "Bedroom");
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

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public void setDescription(String description)
    {
        mDescription = description;
    }

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

    public int getType()
    {
        return mType;
    }

    public void setType(int type)
    {
        mType = type;
    }

    public String getChar()
    {
        return mChar;
    }

    public void setChar(String c)
    {
        mChar = c;
    }

    public Color getColor()
    {
        return mColor;
    }

    public void setColor(Color color)
    {
        mColor = color;
    }

    public boolean isPlaceholder()
    {
        return mPlaceholder;
    }

    public void setPlaceholder(boolean placeholder)
    {
        mPlaceholder = placeholder;
    }

    public boolean isInside()
    {
        return mInside;
    }

    public void setInside(boolean inside)
    {
        mInside = inside;
    }

    public boolean isBedroom()
    {
        return mBedroom;
    }

    public void setBedroom(boolean bedroom)
    {
        mBedroom = bedroom;
    }

}
