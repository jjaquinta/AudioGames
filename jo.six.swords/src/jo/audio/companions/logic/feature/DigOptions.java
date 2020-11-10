package jo.audio.companions.logic.feature;

import jo.audio.companions.data.CompRoomBean;

public class DigOptions
{
    public CompRoomBean from;
    public int dir;
    
    public DigOptions(CompRoomBean _from, int _dir)
    {
        from = _from;
        dir = _dir;
    }
}
