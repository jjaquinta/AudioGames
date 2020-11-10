package jo.audio.common.logic.io;

import jo.audio.common.data.SLDataBean;

public class SLDataDriver extends DataDriver<SLDataBean>
{
    public SLDataDriver()
    {
        super(SLDataBean.class);
        mTableName = "StarLanesData";
        mKeyField = "key";
    }
}
