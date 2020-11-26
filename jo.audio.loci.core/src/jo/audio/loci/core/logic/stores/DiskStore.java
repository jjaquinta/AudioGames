package jo.audio.loci.core.logic.stores;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.logic.DataProfileLogic;
import jo.audio.loci.core.logic.IDataStore;
import jo.util.beans.WeakCache;

public class DiskStore implements IDataStore
{
    public static final String PREFIX = "disk://";
    
    private File mBaseDir;
    
    public DiskStore()
    {
        String dirName = getDiskLocation();
        mBaseDir = new File(dirName);
    }

    public static String getDiskLocation()
    {
        String dirName = System.getProperty("loci.store.disk.dir", System.getProperty("user.home")
                +System.getProperty("file.separator")+".loci"
                +System.getProperty("file.separator")+"data");
        return dirName;
    }

    @Override
    public boolean isStoreFor(String uri)
    {
        return uri.startsWith(PREFIX);
    }
    
    private File getFile(String uri)
    {
        uri = uri.substring(PREFIX.length());
        int o = uri.lastIndexOf('?');
        if (o >= 0)
            uri = uri.substring(0, o);
        String[]segs = uri.split("/");
        File f = mBaseDir;
        try
        {
            for (int i = 0; i < segs.length - 1; i++)
                f = new File(f, URLDecoder.decode(segs[i], "utf-8"));
            f = new File(f, URLDecoder.decode(segs[segs.length - 1], "utf-8")+".json");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IllegalStateException(e);
        }
        return f;
    }
    
    private String getURI(File f)
    {
        StringBuffer uri = new StringBuffer();
        while (!f.equals(mBaseDir))
        {
            String fname = f.getName();
            if (uri.length() == 0)
                uri.append(fname.substring(0, fname.length() - 5));
            else
                uri.insert(0, fname+"/");
            f = f.getParentFile();
        }
        uri.insert(0, PREFIX);
        return uri.toString();
    }

    @Override
    public LociBase load(String uri)
    {
        File f = getFile(uri);
        return load(f);
    }
    private LociBase load(File f)
    {
        JSONObject json = loadJSON(f);
        if (json == null)
            return null;
        LociBase obj = new LociBase(json);
        return obj;
    }
    public JSONObject loadJSON(String uri)
    {
        File f = getFile(uri);
        return loadJSON(f);
    }
    private JSONObject loadJSON(File f)
    {
        if (!f.exists())
            return null;
        JSONObject json;
        try
        {
            json = JSONUtils.readJSON(f);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void save(LociBase obj)
    {
        File f = getFile(obj.getURI());
        f.getParentFile().mkdirs();
        JSONObject json = obj.toJSON();
        try
        {
            JSONUtils.writeJSON(f, json);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public void saveJSON(JSONObject json)
    {
        File f = getFile(json.getString(LociBase.ID_URI));
        f.getParentFile().mkdirs();
        try
        {
            JSONUtils.writeJSON(f, json);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public <T> List<T> findSome(String dataProfile,
            Function<T, Boolean> matcher, int limit, WeakCache<String, LociBase> cache)
    {
        return doFindSome(mBaseDir, dataProfile, matcher, limit, cache);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> doFindSome(File dir, String dataProfile,
            Function<T, Boolean> matcher, int limit, WeakCache<String, LociBase> cache)
    {
        List<T> found = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files == null)
            return null;
        for (File f : files)
        {
            if (f.isDirectory())
            {
                List<T> ret = doFindSome(f, dataProfile, matcher, limit, cache);
                if (ret != null)
                    found.addAll(ret);
            }
            else
            {
                String uri = getURI(f);
                LociBase ret = cache.get(uri);
                if (ret == null)
                    ret = load(f);
                if (!ret.getDataProfile().equals(dataProfile))
                    continue;
                T item = (T)DataProfileLogic.cast(ret);
                cache.put(uri, (LociBase)item);
                if (matcher.apply(item))
                    found.add(item);
            }
            if ((limit > 0) && (found.size() >= limit))
                break;
        }
        return found;
    }

    @Override
    public void delete(String uri)
    {
        File f = getFile(uri);
        if (f.exists())
            f.delete();
    }

    @Override
    public void clearCache()
    {
    }
}
