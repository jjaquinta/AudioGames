package jo.audio.thieves.logic.template;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;
import org.json.simple.parser.ParseException;

import jo.audio.thieves.data.template.PApature;
import jo.audio.thieves.data.template.PLibrary;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.data.template.PTemplate;
import jo.audio.thieves.data.template.TApature;
import jo.audio.thieves.data.template.TLocation;
import jo.audio.thieves.data.template.TLocations;
import jo.audio.thieves.data.template.TTemplate;
import jo.util.utils.MapUtils;

public class OldLogic
{
    public static TLocations read(String fname) throws IOException
    {
        InputStream is = new FileInputStream(fname);
        JSONObject json;
        try
        {
            json = (JSONObject)JSONUtils.PARSER.parse(new InputStreamReader(is, "utf-8"));
        }
        catch (ParseException e)
        {
            throw new IOException(e);
        }
        is.close();
        TLocations locations = new TLocations();
        locations.fromJSON(json);
        return locations;
    }
    
    public static void append(PLibrary library, TLocations locs, TLocations base)
    {
        Map<String,PSquare> squares = library.getSquares();
        for (TLocation loc : locs.getLocations())
        {
            String id = (String)MapUtils.getKey(locs.getIDMap(), loc.getID());
            String color = locs.getColorMap().getProperty(id);
            PSquare sq = new PSquare();
            sq.setBedroom(loc.getBedroom());
            sq.setClimbWallsMod(loc.getClimbWallsMod());
            sq.setColor(color);
            sq.setDescription(loc.getDescription());
            sq.setFindTrapsMod(loc.getFindTrapsMod());
            sq.setHideInShadowsMod(loc.getHideInShadowsMod());
            sq.setID(loc.getID());
            sq.setInside(loc.getInside());
            sq.setMoveSilentlyMod(loc.getMoveSilentlyMod());
            sq.setName(loc.getName());
            sq.setOpenLocksMod(loc.getOpenLocksMod());
            squares.put(sq.getID(), sq);
        }
        library.setSquares(squares);
        Map<String,PApature> apatures = library.getApatures();
        for (TApature tapp : locs.getApatures())
        {
            String id = (String)MapUtils.getKey(locs.getIDMap(), tapp.getID());
            String color = locs.getColorMap().getProperty(id);
            PApature papp = new PApature();
            //papp.setClimbWallsMod(tapp.getClimbWallsMod());
            papp.setColor(color);
            papp.setDescription(tapp.getDescription());
            //papp.setFindTrapsMod(tapp.getFindTrapsMod());
            //papp.setHideInShadowsMod(tapp.getHideInShadowsMod());
            papp.setID(tapp.getID());
            //papp.setMoveSilentlyMod(tapp.getMoveSilentlyMod());
            papp.setName(tapp.getName());
            //papp.setOpenLocksMod(tapp.getOpenLocksMod());
            apatures.put(papp.getID(), papp);
        }
        library.setApatures(apatures);
        Map<String,PTemplate> templates = library.getTemplates();
        for (TTemplate ttemp : locs.getTemplates())
        {
            PTemplate ptemp = new PTemplate();
            ptemp.setID(locs.getPrefix()+ttemp.getID());
            ptemp.setName(ttemp.getID());
            ptemp.setDescription(ttemp.getDescription());
            Map<String,PSquare> sqs = new HashMap<>();
            Map<String,PApature> aps = new HashMap<>();
            char[][][] floors = ttemp.getFloors();
            for (int z = 0; z < floors.length; z++)
                for (int y = 0; y < floors[z].length; y++)
                    for (int x = 0; x < floors[z][y].length; x++)
                    {
                        if ((x%2 == 1) && (y%2 == 1) && (z%2 == 0))
                        {   // square
                            char ch = floors[z][y][x];
                            if ((ch == ' ') || (ch == '.') || (ch == '_'))
                                continue;
                            String id = locs.getIDMap().getProperty(String.valueOf(ch));
                            if ((id == null) && (base != null))
                                id = base.getIDMap().getProperty(String.valueOf(ch));
                            if (id == null)
                                throw new IllegalStateException(ptemp.getID()+", "+x+","+y+","+z+" ch="+ch+", has no ID");
                            if (!squares.containsKey(id))
                                throw new IllegalStateException(ptemp.getID()+", "+x+","+y+","+z+" ch="+ch+", id="+id+", not found in square library");
                            PSquare sq = new PSquare();
                            sq.setID(id);
                            sqs.put(x+","+y+","+z, sq);
                        }
                        else
                        {   // apature
                            boolean doit = false;
                            if (z%2 == 0)
                            {
                                if ((x%2 == 1) && (y%2 == 1))
                                    doit = true;
                            }
                            else
                            {
                                if (y%2 == 0)
                                    doit = x%2 == 1;
                                else
                                    doit = x%2 == 0;
                            }
                            if (doit)
                            {
                                char ch = floors[z][y][x];
                                if ((ch == ' ') || (ch == '.') || (ch == '_') || (ch == '|'))
                                    continue;
                                String id = locs.getIDMap().getProperty(String.valueOf(ch));
                                if ((id == null) && (base != null))
                                    id = base.getIDMap().getProperty(String.valueOf(ch));
                                if (id == null)
                                    throw new IllegalStateException(ptemp.getID()+", "+x+","+y+","+z+" ch="+ch+", has no ID");
                                if (!apatures.containsKey(id))
                                    throw new IllegalStateException(ptemp.getID()+", "+x+","+y+","+z+" ch="+ch+", id="+id+", not found in apature library");
                                PApature ap = new PApature();
                                ap.setID(id);
                                aps.put(x+","+y+","+z, ap);
                            }
                        }
                    }
            ptemp.setApatures(aps);
            ptemp.setSquares(sqs);
            templates.put(ptemp.getID(), ptemp);
        }
        library.setTemplates(templates);
    }
    
    public static void main(String[] argv) throws IOException
    {
        String baseDir = "C:\\Users\\JoJaquinta\\git\\AudioGames\\jo.thieves.core\\src\\jo\\audio\\thieves\\slu\\";
        PLibrary library = new PLibrary();
        TLocations base = read(baseDir + "locationTypes.json");
        append(library, base, null);
        for (String sub : new String[] { "shotgun", "standalone", "terrace", "warehouse" })
        {
            TLocations lib = read(baseDir + "\\locations\\"+sub+".json");
            append(library, lib, base);
        }
        JSONObject json = library.toJSON();
        File libFile = new File(baseDir+"locationLibrary.json");
        JSONUtils.writeJSON(libFile, json);
        
    }
}
