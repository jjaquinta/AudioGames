package jo.audio.util.model.data;

public class SlotSegmentBean extends PhraseSegmentBean
{
    private SlotBean    mSlot;
    
    @Override
    public String toString()
    {
        return "{"+mSlot.getName()+"}";
    }
    public SlotBean getSlot()
    {
        return mSlot;
    }
    public void setSlot(SlotBean slot)
    {
        mSlot = slot;
    }
}
