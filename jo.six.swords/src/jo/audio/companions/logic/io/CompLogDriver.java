package jo.audio.companions.logic.io;

import jo.audio.common.logic.io.DataDriver;
import jo.audio.companions.data.CompLogBean;

public class CompLogDriver extends DataDriver<CompLogBean>
{
    public CompLogDriver()
    {
        super(CompLogBean.class);
        mTableName = "CompanionLog";
        mKeyField = "URI";
    }
}
