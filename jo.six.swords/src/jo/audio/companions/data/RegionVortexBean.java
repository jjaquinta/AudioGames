package jo.audio.companions.data;

import java.util.Random;

import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;

public class RegionVortexBean extends RegionHandBean
{
    private static final AudioMessageBean mNorth = new AudioMessageBean(CompanionsModelConst.TEXT_YOU_SEE_A_HUGE_MAGICAL_VORTEX_SWIRLING_XXX_OF_YOU,
            new AudioMessageBean(CompanionsModelConst.TEXT_NORTH));
    private static final AudioMessageBean mSouth = new AudioMessageBean(CompanionsModelConst.TEXT_YOU_SEE_A_HUGE_MAGICAL_VORTEX_SWIRLING_XXX_OF_YOU,
            new AudioMessageBean(CompanionsModelConst.TEXT_SOUTH));
    private static final AudioMessageBean mEast = new AudioMessageBean(CompanionsModelConst.TEXT_YOU_SEE_A_HUGE_MAGICAL_VORTEX_SWIRLING_XXX_OF_YOU,
            new AudioMessageBean(CompanionsModelConst.TEXT_EAST));
    private static final AudioMessageBean mWest = new AudioMessageBean(CompanionsModelConst.TEXT_YOU_SEE_A_HUGE_MAGICAL_VORTEX_SWIRLING_XXX_OF_YOU,
            new AudioMessageBean(CompanionsModelConst.TEXT_WEST));
    //private static final AudioMessageBean mHere = new AudioMessageBean(CompanionsModelConst.TEXT_RIGHT_HERE_IS_A_GIANT_MAGICAL_VORTEX);

    private int mVortexX = -1;
    private int mVortexY = -1;
    
    // utilities
    private void removeVortexMessages()
    {
        if ((mVortexX < 0) || (mVortexY < 0))
            return;
        if (getSquare(mVortexX, mVortexY) == null)
            return;
        //getSquare(mVortexX, mVortexY).removeDescription(mHere);
        if (getSquare(mVortexX, mVortexY).getFeature() == CompConstLogic.FEATURE_VORTEX)
            getSquare(mVortexX, mVortexY).setFeature(CompConstLogic.FEATURE_NONE);
        getSquare(mVortexX-1, mVortexY).removeDescription(mEast);
        getSquare(mVortexX+1, mVortexY).removeDescription(mWest);
        getSquare(mVortexX, mVortexY-1).removeDescription(mSouth);
        getSquare(mVortexX, mVortexY+1).removeDescription(mNorth);
    }
    
    private void addVortexMessages()
    {
        if ((mVortexX < 0) || (mVortexY < 0))
            return;
        //((SquareHandBean)getSquare(mVortexX, mVortexY)).setDescription(mHere);
        if (getSquare(mVortexX, mVortexY) == null)
            return;
        if (getSquare(mVortexX, mVortexY).getFeature() == CompConstLogic.FEATURE_NONE)
            getSquare(mVortexX, mVortexY).setFeature(CompConstLogic.FEATURE_VORTEX);
        ((SquareHandBean)getSquare(mVortexX-1, mVortexY)).setDescription(mEast);
        ((SquareHandBean)getSquare(mVortexX+1, mVortexY)).setDescription(mWest);
        ((SquareHandBean)getSquare(mVortexX, mVortexY-1)).setDescription(mSouth);
        ((SquareHandBean)getSquare(mVortexX, mVortexY+1)).setDescription(mNorth);
    }
    
    public void moveVortex(Random rnd)
    {
        removeVortexMessages();
        if (mVortexX < 0)
            mVortexX = rnd.nextInt(CompConstLogic.SQUARES_PER_REGION - 2) + 1;
        else
            mVortexX += rnd.nextInt(3) - 2;
        if (mVortexY < 0)
            mVortexY = rnd.nextInt(CompConstLogic.SQUARES_PER_REGION - 2) + 1;
        else
            mVortexY += rnd.nextInt(3) - 2;
        if ((mVortexX <= 0) || (mVortexX >= CompConstLogic.SQUARES_PER_REGION - 1)
                || (mVortexY <= 0) || (mVortexY >= CompConstLogic.SQUARES_PER_REGION - 1))
        {
            mVortexX = -1;
            mVortexY = -1;
            DebugUtils.trace("Vortex for "+getOrds()+" dissapates");
        }
        else
        {
            addVortexMessages();
            DebugUtils.trace("Vortex for "+getOrds()+" at "+mVortexX+","+mVortexY);
        }
    }
    
    // getters and setters
    
    public int getVortexX()
    {
        return mVortexX;
    }
    public void setVortexX(int vortexX)
    {
        mVortexX = vortexX;
    }
    public int getVortexY()
    {
        return mVortexY;
    }
    public void setVortexY(int vortexY)
    {
        mVortexY = vortexY;
    }
}
