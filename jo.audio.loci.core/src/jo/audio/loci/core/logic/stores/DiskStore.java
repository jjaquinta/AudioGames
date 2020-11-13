package jo.audio.loci.core.logic.stores;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.function.Function;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.logic.IDataStore;

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

    @Override
    public LociBase load(String uri)
    {
        File f = getFile(uri);
        return load(f);
    }
    private LociBase load(File f)
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
        LociBase obj = new LociBase(json);
        return obj;
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

    @Override
    public LociBase findFirst(String dataProfile,
            Function<LociBase, Boolean> matcher)
    {
        return doFindFirst(mBaseDir, dataProfile, matcher);
    }

    private LociBase doFindFirst(File dir, String dataProfile,
            Function<LociBase, Boolean> matcher)
    {
        File[] files = dir.listFiles();
        for (File f : files)
        {
            if (f.isDirectory())
            {
                LociBase ret = doFindFirst(f, dataProfile, matcher);
                if (ret != null)
                    return ret;
            }
            else
            {
                LociBase ret = load(f);
                if (ret.getDataProfile().equals(dataProfile) && matcher.apply(ret))
                    return ret;
            }
        }
        return null;
    }

    @Override
    public void delete(String uri)
    {
        File f = getFile(uri);
        if (f.exists())
            f.delete();
    }
}
