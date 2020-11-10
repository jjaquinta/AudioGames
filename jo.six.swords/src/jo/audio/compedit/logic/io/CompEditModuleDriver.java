package jo.audio.compedit.logic.io;

import jo.audio.common.logic.io.DataDriver;
import jo.audio.compedit.data.CompEditModuleBean;

public class CompEditModuleDriver extends DataDriver<CompEditModuleBean>
{
    public CompEditModuleDriver()
    {
        super(CompEditModuleBean.class);
        mTableName = "CompEditModules";
        mKeyField = "id";
    }
}
