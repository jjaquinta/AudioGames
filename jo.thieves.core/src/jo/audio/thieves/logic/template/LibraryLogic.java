package jo.audio.thieves.logic.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;
import org.json.simple.parser.ParseException;

import jo.audio.thieves.data.template.PLibrary;

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
    
    public static void write(OutputStream os, PLibrary locations) throws IOException
    {
        JSONObject json = locations.toJSON();
        byte[] data = json.toJSONString().getBytes("utf-8");
        os.write(data);
    }
}
