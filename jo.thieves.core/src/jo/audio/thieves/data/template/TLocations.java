package jo.audio.thieves.data.template;

import java.util.List;
import java.util.Properties;

import jo.util.beans.JSONBean;
import jo.util.utils.obj.StringUtils;

public class TLocations extends JSONBean implements Comparable<TLocations>
{
    public static final String ID_APATURES = "apatures";
    public static final String ID_COLORMAP = "colorMap";
    public static final String ID_IDMAP = "idMap";
    public static final String ID_LOCATIONS = "locations";
    public static final String ID_PREFIX = "prefix";
    public static final String ID_TEMPLATES = "templates";
    
    // transient
    private String  mPath;
    
    // utilities
    
    @Override
    public String toString()
    {
        String str = getPrefix();
        if (!StringUtils.isTrivial(str))
            return str;
        return "<root>";
    }
    
    public TLocation findLocation(String id)
    {
        for (TLocation l : getLocations())
            if (l.getID().equals(id))
                return l;
        return null;
    }

    @Override
    public int compareTo(TLocations o)
    {
        return StringUtils.compareTo(getPrefix(), o.getPrefix());
    }
    
    // getters and setters
    
    public List<TApature> getApatures()
    {
        return getArray(ID_APATURES, TApature.class);
    }
    
    public void setLocations(List<TLocation> value)
    {
        setArray(ID_LOCATIONS, value);
    }
    
    public List<TTemplate> getTemplates()
    {
        return getArray(ID_TEMPLATES, TTemplate.class);
    }
    
    public void setTemplates(List<TTemplate> value)
    {
        setArray(ID_TEMPLATES, value);
    }
    
    public List<TLocation> getLocations()
    {
        return getArray(ID_LOCATIONS, TLocation.class);

    }
    
    public void setApatures(List<TApature> value)
    {
        setArray(ID_APATURES, value);
    }
    
    public Properties getColorMap()
    {
        return getProperties(ID_COLORMAP);
    }
    
    public void setColorMap(Properties value)
    {
        setProperties(ID_COLORMAP, value);
    }
    
    public Properties getIDMap()
    {
        return getProperties(ID_IDMAP);

    }
    
    public void setIDMap(Properties value)
    {
        setProperties(ID_IDMAP, value);
    }

    public String getPrefix()
    {
        return getString(ID_PREFIX);
    }
    
    public void setPrefix(String value)
    {
        setString(ID_PREFIX, value);
    }

    public String getPath()
    {
        return mPath;
    }

    public void setPath(String path)
    {
        mPath = path;
    }
}
