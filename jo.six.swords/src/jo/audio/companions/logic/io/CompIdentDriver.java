package jo.audio.companions.logic.io;

import jo.audio.common.logic.io.DataDriver;
import jo.audio.companions.data.CompIdentBean;

public class CompIdentDriver extends DataDriver<CompIdentBean>
{
    public CompIdentDriver()
    {
        super(CompIdentBean.class);
        mTableName = "StarCoreIdents";
        mKeyField = "URI";
    }
}
