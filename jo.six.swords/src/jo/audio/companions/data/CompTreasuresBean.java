package jo.audio.companions.data;

import java.util.HashMap;
import java.util.Map;

import jo.audio.util.model.data.AudioMessageBean;

public class CompTreasuresBean
{
    private double  mTotalValue;
    private int     mCopperPieces;
    private int     mSilverPieces;
    private int     mElectrumPieces;
    private int     mGoldPieces;
    private int     mPlatinumPieces;
    private int     mJewelryPieces;
    private double  mJewelryValue;
    private int     mGemPieces;
    private double  mGemValue;
    private Map<CompItemTypeBean,Integer> mItems = new HashMap<>();
    private AudioMessageBean mMessage;
    
    public double getTotalValue()
    {
        return mTotalValue;
    }
    public void setTotalValue(double totalValue)
    {
        mTotalValue = totalValue;
    }
    public int getCopperPieces()
    {
        return mCopperPieces;
    }
    public void setCopperPieces(int copperPieces)
    {
        mCopperPieces = copperPieces;
    }
    public int getSilverPieces()
    {
        return mSilverPieces;
    }
    public void setSilverPieces(int silverPieces)
    {
        mSilverPieces = silverPieces;
    }
    public int getElectrumPieces()
    {
        return mElectrumPieces;
    }
    public void setElectrumPieces(int electrumPieces)
    {
        mElectrumPieces = electrumPieces;
    }
    public int getGoldPieces()
    {
        return mGoldPieces;
    }
    public void setGoldPieces(int goldPieces)
    {
        mGoldPieces = goldPieces;
    }
    public int getPlatinumPieces()
    {
        return mPlatinumPieces;
    }
    public void setPlatinumPieces(int platinumPieces)
    {
        mPlatinumPieces = platinumPieces;
    }
    public int getJewelryPieces()
    {
        return mJewelryPieces;
    }
    public void setJewelryPieces(int jewelryPieces)
    {
        mJewelryPieces = jewelryPieces;
    }
    public double getJewelryValue()
    {
        return mJewelryValue;
    }
    public void setJewelryValue(double jewelryValue)
    {
        mJewelryValue = jewelryValue;
    }
    public int getGemPieces()
    {
        return mGemPieces;
    }
    public void setGemPieces(int gemPieces)
    {
        mGemPieces = gemPieces;
    }
    public double getGemValue()
    {
        return mGemValue;
    }
    public void setGemValue(double gemValue)
    {
        mGemValue = gemValue;
    }
    public Map<CompItemTypeBean, Integer> getItems()
    {
        return mItems;
    }
    public void setItems(Map<CompItemTypeBean, Integer> items)
    {
        mItems = items;
    }
    public AudioMessageBean getMessage()
    {
        return mMessage;
    }
    public void setMessage(AudioMessageBean message)
    {
        mMessage = message;
    }
    
}
