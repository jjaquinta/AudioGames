package jo.audio.companions.logic.io;

import jo.audio.common.logic.io.DataDriver;
import jo.audio.companions.data.CompUserBean;

public class CompUserDriver extends DataDriver<CompUserBean>
{
    public CompUserDriver()
    {
        super(CompUserBean.class);
        mTableName = "CompanionUsers";
        mKeyField = "URI";
    }
}
