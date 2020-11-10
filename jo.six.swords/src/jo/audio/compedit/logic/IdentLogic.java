package jo.audio.compedit.logic;

import java.util.HashMap;
import java.util.Map;

import jo.audio.compedit.data.CompEditIdentBean;
import jo.util.utils.obj.StringUtils;

public class IdentLogic
{
    // redundancy with cache in driver
    private static Map<String, CompEditIdentBean> mCache = new HashMap<>();
    
    public static CompEditIdentBean getIdent(String id)
    {
        if (!id.startsWith("scid://"))
            id = "scid://"+id;
        synchronized (mCache)
        {
            if (mCache.containsKey(id))
                return mCache.get(id);
        }
        CompEditIdentBean user = CompEditIOLogic.getIdentFromURI(id);
        synchronized (mCache)
        {
            mCache.put(id, user);
        }
        return user;
    }

    public static CompEditIdentBean newInstance(String id, String linkedName, String linkedEmail)
    {
        if (!id.startsWith("scid://"))
            id = "scid://"+id;
        CompEditIdentBean user = new CompEditIdentBean();
        user.setURI(id);
        user.setUserID(id.substring(6));
        user.setEmail(linkedEmail);
        user.setFamilyName(linkedName);
        CompEditIOLogic.saveIdent(user);
        synchronized (mCache)
        {
            mCache.put(id, user);
        }
        return user;
    }

    public static CompEditIdentBean newInstance(String id, String password)
    {
        if (!id.startsWith("scid://"))
            id = "scid://"+id;
        CompEditIdentBean user = new CompEditIdentBean();
        user.setURI(id);
        user.setUserID(id.substring(6));
        user.setPassword(password);
        CompEditIOLogic.saveIdent(user);
        synchronized (mCache)
        {
            mCache.put(id, user);
        }
        return user;
    }
    
    public static void updateIdent(CompEditIdentBean ident, String email, String displayName, String givenName)
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
            CompEditIOLogic.saveIdent(ident);
    }
}
