package jo.audio.companions.tools.gui.map;

import java.awt.Dimension;

import jo.util.beans.PCSBean;

public class MapData extends PCSBean
{
    private int mX = 1018;//CompConstLogic.INITIAL_LOCATION_X;
    private int mY = 941;//CompConstLogic.INITIAL_LOCATION_Y;
    private int mZ = 2;//0;
    private int mDX;
    private int mDY;
    private int mScale = 48;
    private Dimension  mSize;
    private boolean mDrawRivers = true;
    private boolean mDrawRoads = true;
    private boolean mDrawBorders = true;
    private boolean mDrawTownBorders = false;
    private boolean mTownNames = true;
    private boolean mFeatureNames = true;
    private boolean mCountryNames = true;
    private boolean mBorderNames = true;
    
    private boolean mNoTerrainIcons;
    private boolean mNoRuinIcons;

    // utilities
    public int getPixelScale()
    {
        if (mScale > 4)
            return mScale;
        else if (mScale > 0)
            return 4;
        else
            return 1;
    }
    
    public int getWidth()
    {
        return mSize.width;
    }
    
    public void setWidth(int w)
    {
        mSize.width = w;
    }
    
    public int getHeight()
    {
        return mSize.height;
    }
    
    public void setHeight(int h)
    {
        mSize.height = h;
    }
    
    // getters and setters
    
    public int getX()
    {
        return mX;
    }
    public void setX(int x)
    {
        if (x == mX)
            return;
        queuePropertyChange("X", mX, x);
        mX = x;
        firePropertyChange();
    }
    public int getY()
    {
        return mY;
    }
    public void setY(int y)
    {
        if (y == mY)
            return;
        queuePropertyChange("Y", mY, y);
        mY = y;
        firePropertyChange();
    }
    public int getDX()
    {
        return mDX;
    }
    public void setDX(int dX)
    {
        mDX = dX;
    }
    public int getDY()
    {
        return mDY;
    }
    public void setDY(int dY)
    {
        mDY = dY;
    }
    public int getScale()
    {
        return mScale;
    }
    public void setScale(int scale)
    {
        if (scale == mScale)
            return;
        queuePropertyChange("scale", mScale, scale);
        mScale = scale;
        firePropertyChange();
    }
    public Dimension getSize()
    {
        return mSize;
    }
    public void setSize(Dimension size)
    {
        mSize = size;
    }
    public int getZ()
    {
        return mZ;
    }
    public void setZ(int z)
    {
        mZ = z;
    }
    public boolean isNoTerrainIcons()
    {
        return mNoTerrainIcons;
    }
    public void setNoTerrainIcons(boolean noTerrainIcons)
    {
        mNoTerrainIcons = noTerrainIcons;
    }
    public boolean isNoRuinIcons()
    {
        return mNoRuinIcons;
    }
    public void setNoRuinIcons(boolean noRuinIcons)
    {
        mNoRuinIcons = noRuinIcons;
    }

    public boolean isDrawRivers()
    {
        return mDrawRivers;
    }

    public void setDrawRivers(boolean drawRivers)
    {
        mDrawRivers = drawRivers;
    }

    public boolean isDrawRoads()
    {
        return mDrawRoads;
    }

    public void setDrawRoads(boolean drawRoads)
    {
        mDrawRoads = drawRoads;
    }

    public boolean isDrawBorders()
    {
        return mDrawBorders;
    }

    public void setDrawBorders(boolean drawBoarders)
    {
        mDrawBorders = drawBoarders;
    }

    public boolean isFeatureNames()
    {
        return mFeatureNames;
    }

    public void setFeatureNames(boolean featureNames)
    {
        mFeatureNames = featureNames;
    }

    public boolean isCountryNames()
    {
        return mCountryNames;
    }

    public void setCountryNames(boolean countryNames)
    {
        mCountryNames = countryNames;
    }

    public boolean isDrawTownBorders()
    {
        return mDrawTownBorders;
    }

    public void setDrawTownBorders(boolean drawTownBorders)
    {
        mDrawTownBorders = drawTownBorders;
    }

    public boolean isBorderNames()
    {
        return mBorderNames;
    }

    public void setBorderNames(boolean borderNames)
    {
        mBorderNames = borderNames;
    }

    public boolean isTownNames()
    {
        return mTownNames;
    }

    public void setTownNames(boolean townNames)
    {
        mTownNames = townNames;
    }
}
