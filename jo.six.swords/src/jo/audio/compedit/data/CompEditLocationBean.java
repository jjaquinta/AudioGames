package jo.audio.compedit.data;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.StringTokenizer;

import jo.util.utils.obj.StringUtils;

public class CompEditLocationBean
{
    private String  mURI;
    private String  mModuleID;
    private String  mFeatureID;
    private String  mRoomID;
    
    public CompEditLocationBean()
    {
        deconstructURI();
    }
    
    public CompEditLocationBean(String uri)
    {
        mURI = uri;
        deconstructURI();
    }
    
    // utils
    private void deconstructURI()
    {
        mModuleID = null;
        mFeatureID = null;
        mRoomID = null;
        if (StringUtils.isTrivial(mURI))
            return;
        int o = mURI.indexOf('?');
        String params = null;
        if (o >= 0)
        {
            mModuleID = mURI.substring(0, o);
            params = mURI.substring(o + 1);
            for (StringTokenizer st = new StringTokenizer(params, "&"); st.hasMoreTokens(); )
            {
                String kv = st.nextToken();
                int o2 = kv.indexOf('=');
                String key = kv.substring(0, o2);
                String val;
                try
                {
                    val = URLDecoder.decode(kv.substring(o2 + 1), "utf-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    val = kv.substring(o2 + 1);
                }
                if (key.equals("feature"))
                    mFeatureID = val;
                else if (key.equals("room"))
                    mRoomID = val;
            }
        }
        else
            mModuleID = mURI;
    }
    
    private void constructURI()
    {
        if (StringUtils.isTrivial(mModuleID))
        {
            mURI = "";
            return;
        }
        mURI = mModuleID;
        try
        {
        if (!StringUtils.isTrivial(mFeatureID))
        {
            mURI += "?feature="+URLEncoder.encode(mFeatureID, "utf-8");
            if (!StringUtils.isTrivial(mRoomID))
            {
                mURI += "&room="+URLEncoder.encode(mRoomID, "utf-8");
            }
        }
        }
        catch (UnsupportedEncodingException e)
        {            
        }
    }
    
    // getters and setters
    public String getURI()
    {
        return mURI;
    }
    public void setURI(String uRI)
    {
        mURI = uRI;
        deconstructURI();
    }
    public String getModuleID()
    {
        return mModuleID;
    }
    public void setModuleID(String moduleID)
    {
        mModuleID = moduleID;
        constructURI();
    }
    public String getFeatureID()
    {
        return mFeatureID;
    }
    public void setFeatureID(String featureID)
    {
        mFeatureID = featureID;
        constructURI();
    }
    public String getRoomID()
    {
        return mRoomID;
    }
    public void setRoomID(String roomID)
    {
        mRoomID = roomID;
        constructURI();
    }
}
