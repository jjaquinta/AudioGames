package jo.audio.compedit.logic.io;

import jo.audio.common.logic.io.DataDriver;
import jo.audio.compedit.data.CompEditIdentBean;

public class CompEditIdentDriver extends DataDriver<CompEditIdentBean>
{
    public CompEditIdentDriver()
    {
        super(CompEditIdentBean.class);
        mTableName = "StarCoreIdents";
        mKeyField = "URI";
    }
}
