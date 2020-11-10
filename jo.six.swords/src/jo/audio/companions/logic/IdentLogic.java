package jo.audio.companions.logic;

import java.util.HashMap;
import java.util.Map;

import jo.audio.companions.data.CompIdentBean;
import jo.util.utils.obj.StringUtils;

public class IdentLogic
{
    // redundancy with cache in driver
    private static Map<String, CompIdentBean> mCache = new HashMap<>();
    
    public static CompIdentBean getIdent(String id)
    {
        if (!id.startsWith("scid://"))
            id = "scid://"+id;
        synchronized (mCache)
        {
            if (mCache.containsKey(id))
                return mCache.get(id);
        }
        CompIdentBean user = CompIOLogic.getIdentFromURI(id);
        synchronized (mCache)
        {
            mCache.put(id, user);
        }
        return user;
    }

    public static CompIdentBean newInstance(String id, String linkedName, String linkedEmail)
    {
        if (!id.startsWith("scid://"))
            id = "scid://"+id;
        CompIdentBean user = new CompIdentBean();
        user.setURI(id);
        user.setUserID(id.substring(6));
        user.setEmail(linkedEmail);
        user.setFamilyName(linkedName);
        CompIOLogic.saveIdent(user);
        synchronized (mCache)
        {
            mCache.put(id, user);
        }
        return user;
    }
    
    public static void updateIdent(CompIdentBean ident, String email, String displayName, String givenName)
    {
        boolean save = false;
        if ((email != null) && !StringUtils.equals(email, ident.getEmail()))
        {
            save = true;
            ident.setEmail(email);
        }
        if ((displayName != null) && !StringUtils.equals(displayName, ident.getDisplayName()))
        {
            save = true;
            ident.setDisplayName(displayName);
        }
        if ((givenName != null) && !StringUtils.equals(givenName, ident.getGivenName()))
        {
            save = true;
            ident.setGivenName(givenName);
        }
        if (save)
            CompIOLogic.saveIdent(ident);
    }
}
