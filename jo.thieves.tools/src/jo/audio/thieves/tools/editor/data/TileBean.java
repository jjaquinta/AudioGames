package jo.audio.thieves.tools.editor.data;

import java.awt.Color;

public class TileBean
{
    public static final int LOCATION = 1;
    public static final int APATURE = 2;
    
    private String  mChar;
    private String  mID;
    private String  mName;
    private String  mDescription;
    private Color   mColor;
    private int     mType;
    
    public String getChar()
    {
        return mChar;
    }
    public void setChar(String c)
    {
        mChar = c;
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
    public Color getColor()
    {
        return mColor;
    }
    public void setColor(Color color)
    {
        mColor = color;
    }
    public int getType()
    {
        return mType;
    }
    public void setType(int type)
    {
        mType = type;
    }
    public String getID()
    {
        return mID;
    }
    public void setID(String iD)
    {
        mID = iD;
    }
}
