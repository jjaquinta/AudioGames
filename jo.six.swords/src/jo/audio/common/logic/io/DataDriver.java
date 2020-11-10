package jo.audio.common.logic.io;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.util.IIOBean;
import jo.util.beans.URIBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.io.FileUtils;

public abstract class DataDriver<T extends IIOBean>
{
    protected String mTableName;
    protected String mKeyField;
    protected String mIndexName;
    protected String mIndexField;
    private Class<? extends IIOBean> mClass;
    private Map<String, T> mCache = new HashMap<String, T>();
    private Map<String, Long> mCacheFetch = new HashMap<String, Long>();
    private File mTableDir = null;

    public DataDriver(Class<? extends IIOBean> _class)
    {
        mClass = _class;
        mKeyField = "URI";
    }
    
    public String getTableName()
    {
        return mTableName;
    }
    
    public void clearCache()
    {
        mCache.clear();
        mCacheFetch.clear();
    }

    public boolean isDriverFor(IIOBean bean)
    {
        return mClass.isAssignableFrom(bean.getClass());
    }

    public boolean isDriverFor(Class<?> clazz)
    {
        return mClass.isAssignableFrom(clazz);
    }
    
    private File getTableDir()
    {
        if (mTableDir == null)
        {
            File dbdir = new File(System.getProperty("user.home"), ".sixswords");
            if (!dbdir.exists())
                dbdir.mkdir();
            mTableDir = new File(dbdir, mTableName);
            if (!mTableDir.exists())
                mTableDir.mkdir();
        }
        return mTableDir;
    }
    
    private File getRecordFile(IIOBean bean)
    {
        return getRecordFile(bean.getURI());
    }
    
    private File getRecordFile(String uri)
    {
        File tableDir = getTableDir();
        String base;
        try
        {
            base = URLEncoder.encode(uri, "utf-8");
        }
        catch (UnsupportedEncodingException e)
        {
            base = uri;
        }
        File recordDir = new File(tableDir, base+".json");
        return recordDir;
    }
    
    @SuppressWarnings("unchecked")
    public void save(IIOBean bean)
    {
        trace("Put "+bean.getClass().getSimpleName()+" #"+bean.hashCode()+" in cache as "+bean.getURI());
        mCache.put(bean.getURI(), (T)bean);
        mCacheFetch.put(bean.getURI(), System.currentTimeMillis());            
        if (DriverLogic.isSingleThreaded())
        {
            trace("Save immeidate");
            saveImmediate((T)bean);
        }
        else
        {
            trace("addToOutputQueue");
            DriverLogic.addToOutputQueue(bean.getURI(), bean);
        }
    }
    public void saveImmediate(IIOBean bean)
    {
        trace("Saving "+bean.getURI());
        File recordFile = getRecordFile(bean);
        JSONObject json = bean.toJSON();
        try
        {
            JSONUtils.writeJSON(recordFile, json);
        }
        catch (IOException e)
        {
            throw new IllegalStateException(e);
        }
    }
    

    @SuppressWarnings("unchecked")
    public T getFromURI(String uri)
    {
        T star = mCache.get(uri);
        if (star != null)
        {
            trace("Found "+mClass.getSimpleName()+" #"+star.hashCode()+" in cache as "+((URIBean)star).getURI());
            Long lastFetch = mCacheFetch.get(uri);
            if (lastFetch != null)
            {
                long elapsed = System.currentTimeMillis() - lastFetch;
                if (elapsed < DriverLogic.FETCH_TIMEOUT)
                    return star;
            }
        }
        star = (T)DriverLogic.findInOutputQueue(uri);
        if (star != null)
            return star;
        trace("Getting "+uri+" from "+mTableName);
        File recordFile = getRecordFile(uri);
        if (!recordFile.exists())
            return null;
        JSONObject result;
        try
        {
            String data = FileUtils.readFileAsString(recordFile.toString(), "utf-8");
            result = (JSONObject)JSONUtils.readJSONString(data);
        }
        catch (IOException e)
        {
            result = null;
        } 
        if (result == null)
        {
            trace("Could not get "+uri+" from "+mTableName);
            return null;
        }
        if (star == null)
        {
            star = newInstance();
            mCache.put(uri, star);
            trace("Inserting "+mClass.getSimpleName()+" #"+star.hashCode()+" in cache as "+uri);
        }
        trace("Reading values");
        star.fromJSON(result);
        mCacheFetch.put(star.getURI(), System.currentTimeMillis());
        trace("Returning object "+star.hashCode());
        return star;
    }

    public List<T> getFromField(int maxNum, long maxTime, String... fieldNameValues)
    {
        trace("Getting "+fieldNameValues+" from "+mTableName);
        List<T> items = new ArrayList<>();
        File tableDir = getTableDir();
        File[] recordFiles = tableDir.listFiles();
        if (recordFiles != null)
            for (File recordFile : recordFiles)
            {
                try
                {
                    JSONObject json = (JSONObject)JSONUtils.readJSON(recordFile);
                    T star = newInstance();
                    star.fromJSON(json);
                    mCache.put(star.getURI(), star);
                    mCacheFetch.put(star.getURI(), System.currentTimeMillis());
                    trace("Inserting "+mClass.getSimpleName()+" #"+star.hashCode()+" in cache as "+star.getURI());
                    if (match(json, fieldNameValues))
                        items.add(star);
                }
                catch (IOException e)
                {
                }
            }
        trace("Returning "+items.size()+" objects");
        return items;
    }
    
    private boolean match(JSONObject json, String[] fieldNameValues)
    {
        for (int i = 0; i < fieldNameValues.length; i += 2)
        {
            if (!json.containsKey(fieldNameValues[i]))
                return false;
            if (!fieldNameValues[i+1].equals(json.get(fieldNameValues[i])))
                return false;
        }
        return true;
    }

    public List<T> getAll()
    {
        trace("Getting all");
        List<T> ads = new ArrayList<T>();
        File tableDir = getTableDir();
        File[] recordFiles = tableDir.listFiles();
        if (recordFiles != null)
            for (File recordFile : recordFiles)
            {
                try
                {
                    JSONObject json = (JSONObject)JSONUtils.readJSON(recordFile);
                    T star = newInstance();
                    star.fromJSON(json);
                    mCache.put(star.getURI(), star);
                    mCacheFetch.put(star.getURI(), System.currentTimeMillis());
                    trace("Inserting "+mClass.getSimpleName()+" #"+star.hashCode()+" in cache as "+star.getURI());
                    ads.add(star);
                }
                catch (IOException e)
                {
                }
            }
        trace("Returning "+ads.size()+" objects");
        return ads;
    }
    
    public void getAllIncremental(DataCallback cb)
    {
        trace("Getting all");
        File tableDir = getTableDir();
        File[] recordFiles = tableDir.listFiles();
        if (recordFiles != null)
            for (File recordFile : recordFiles)
            {
                try
                {
                    JSONObject json = (JSONObject)JSONUtils.readJSON(recordFile);
                    T star = newInstance();
                    star.fromJSON(json);
                    mCache.put(star.getURI(), star);
                    mCacheFetch.put(star.getURI(), System.currentTimeMillis());
                    cb.read(star);
                }
                catch (IOException e)
                {
                }
            }
    }

    @SuppressWarnings("unchecked")
    private T newInstance()
    {
        T user; 
        try
        {
            user = (T)mClass.newInstance();
        }
        catch (Exception e)
        {
            throw new IllegalStateException("Cant' create a "+mClass.getName(), e);
        }
        return user;
    }
    
    public List<T> getByIndex(String callsign)
    {
        if ((mIndexName == null) || (mIndexField == null))
            throw new IllegalArgumentException("Cannot do indexed lookup for "+mClass.getSimpleName());
        trace("Looking up "+mClass.getSimpleName()+" indexKey="+callsign+" in "+mTableName+":"+mIndexField);
        List<T> users = new ArrayList<T>();
        // TODO: query by index
        return users;
    }

    public void delete(IIOBean bean)
    {
        String uri = bean.getURI();
        File recordFile = getRecordFile(bean);
        if (recordFile.exists())
            recordFile.delete();
        mCache.remove(uri);
        mCacheFetch.remove(uri);            
    }
    
    protected void trace(String msg)
    {
        DebugUtils.trace(msg);
    }
}
