package jo.audio.compedit.logic.io;

import jo.audio.common.logic.io.DataDriver;
import jo.audio.compedit.data.CompEditUserBean;

public class CompEditUserDriver extends DataDriver<CompEditUserBean>
{
    public CompEditUserDriver()
    {
        super(CompEditUserBean.class);
        mTableName = "CompEditUsers";
        mKeyField = "URI";
    }
}
