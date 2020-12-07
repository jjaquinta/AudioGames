package jo.audio.thieves.logic.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collection;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;
import org.json.simple.parser.ParseException;

import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PTemplate;

public class LibraryLogic
{
    public static PLibrary read(File fname) throws IOException
    {
        InputStream is = new FileInputStream(fname);
        PLibrary lib = read(is);
        is.close();
        return lib;
    }
    
    public static PLibrary read(InputStream is) throws IOException
    {
        JSONObject json;
        try
        {
            json = (JSONObject)JSONUtils.PARSER.parse(new InputStreamReader(is, "utf-8"));
        }
        catch (ParseException e)
        {
            throw new IOException(e);
        }
        PLibrary locations = new PLibrary();
        locations.fromJSON(json);
        return locations;
    }
    
    public static PLibrary read(String uri) throws IOException
    {
        JSONObject json = (JSONObject)JSONUtils.readJSON(uri);
        PLibrary locations = new PLibrary();
        locations.fromJSON(json);
        return locations;
    }
    
    public static void write(OutputStream os, PLibrary locations) throws IOException
    {
        JSONObject json = locations.toJSON();
        byte[] data = json.toJSONString().getBytes("utf-8");
        os.write(data);
    }
    
    public static int[][] getBoundary(PTemplate house)
    {
        int[][] ret = null;
        if (house == null)
            return ret;
        for (PLocationRef loc : house.getLocations().values())
            ret = extendBoundary(loc, ret);
        return ret;
    }
    public static int[][] getBoundary(Collection<PLocationRef> keys)
    {
        int[][] ret = null;
        for (PLocationRef loc : keys)
            ret = extendBoundary(loc, ret);
        return ret;
    }
    private static int[][] extendBoundary(PLocationRef loc, int[][] edges)
    {
        if (edges == null)
            return new int[][] { { loc.getX(), loc.getY(), loc.getZ() }, { loc.getX(), loc.getY(), loc.getZ() } };
        edges[0][0] = Math.min(edges[0][0], loc.getX());
        edges[0][1] = Math.min(edges[0][1], loc.getY());
        edges[0][2] = Math.min(edges[0][2], loc.getZ());
        edges[1][0] = Math.max(edges[1][0], loc.getX());
        edges[1][1] = Math.max(edges[1][1], loc.getY());
        edges[1][2] = Math.max(edges[1][2], loc.getZ());
        return edges;
    }
    public static int[][] getSquareBoundary(int[][] bounds)
    {
        int[][] squareBounds = new int[2][3];
        if (bounds[0][0] % 2 == 0)
            squareBounds[0][0] = bounds[0][0] + 1;
        else
            squareBounds[0][0] = bounds[0][0];
        if (bounds[0][1] % 2 == 0)
            squareBounds[0][1] = bounds[0][1] + 1;
        else
            squareBounds[0][1] = bounds[0][1];
        squareBounds[0][2] = bounds[0][2];
        if (bounds[1][0] % 2 == 0)
            squareBounds[1][0] = bounds[1][0] - 1;
        else
            squareBounds[1][0] = bounds[1][0];
        if (bounds[1][1] % 2 == 0)
            squareBounds[1][1] = bounds[1][1] - 1;
        else
            squareBounds[1][1] = bounds[1][1];
        squareBounds[1][2] = bounds[1][2];
        return squareBounds;
    }

}
