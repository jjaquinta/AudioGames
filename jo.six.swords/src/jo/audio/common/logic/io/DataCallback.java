package jo.audio.common.logic.io;

import jo.audio.util.IIOBean;

public interface DataCallback
{
    public boolean read(IIOBean data);
    public boolean endOfChunk();
}
