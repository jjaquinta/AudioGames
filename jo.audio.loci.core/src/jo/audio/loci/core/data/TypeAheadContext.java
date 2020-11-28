package jo.audio.loci.core.data;

import java.util.ArrayList;
import java.util.List;

public class TypeAheadContext extends InvocationContext
{
    private List<String> mCommands = new ArrayList<>();

    // utils
    
    public void addCommand(String cmd)
    {
        mCommands.add(cmd);
    }
    
    // getters and setters

    public List<String> getCommands()
    {
        return mCommands;
    }

    public void setCommands(List<String> Command)
    {
        mCommands = Command;
    }
}
