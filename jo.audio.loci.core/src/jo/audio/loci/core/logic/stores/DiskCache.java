package jo.audio.loci.core.logic.stores;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.function.Function;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.loci.core.data.LociBase;

public class DiskCache
{
    private String mPrefix = "disk://";
    
    private File mBaseDir;
    
    public DiskCache(String prefix)
    {
        mPrefix = prefix;
        String dirName = getDiskLocation(null);
        mBaseDir = new File(dirName);
    }
    
    public DiskCache(String prefix, String subdir)
    {
        mPrefix = prefix;
        String dirName = getDiskLocation(subdir);
        mBaseDir = new File(dirName);
    }
    
    public DiskCache(String prefix, File baseDir)
    {
        mPrefix = prefix;
        mBaseDir = baseDir;
    }

    private String getDiskLocation(String subdir)
    {
        String dirName = System.getProperty("loci.store.disk.dir");
        if (dirName == null)
            dirName = System.getProperty("user.home")+System.getProperty("file.separator")+".loci";
        if (subdir == null)
            dirName += System.getProperty("file.separator")+"data";
        else
            dirName += System.getProperty("file.separator")+subdir;
        return dirName;
    }
    
    public File getBaseDir()
    {
        return mBaseDir;
    }
    
    private File getFile(String uri)
    {
        uri = uri.substring(mPrefix.length());
        int o = uri.lastIndexOf('?');
        if (o >= 0)
            uri = uri.substring(0, o);
        String[]segs = uri.split("/");
        File f = mBaseDir;
        try
        {
            for (int i = 0; i < segs.length - 1; i++)
                f = new File(f, URLEncoder.encode(segs[i], "utf-8"));
            f = new File(f, URLEncoder.encode(segs[segs.length - 1], "utf-8")+".json");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IllegalStateException(e);
        }
        return f;
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

    public void iterate(Function<JSONObject, Boolean> found)
    {
        doFindSome(mBaseDir, found);
    }

    private boolean doFindSome(File dir, Function<JSONObject, Boolean> found)
    {
        File[] files = dir.listFiles();
        if (files == null)
            return false;
        for (File f : files)
        {
            if (f.isDirectory())
            {
                boolean done = doFindSome(f, found);
                if (done)
                    return true;
            }
            else
            {
                JSONObject json = loadJSON(f);
                boolean done = found.apply(json);
                if (done)
                    return true;
            }
        }
        return false;
    }

    public void delete(String uri)
    {
        File f = getFile(uri);
        if (f.exists())
            f.delete();
    }
}
