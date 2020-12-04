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
import jo.audio.thieves.data.template.PLocationRef;
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
    
    public static void append(PLibrary library, TLocations locs, TLocations base, String category)
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
            papp.setTransition(tapp.getTransition());
            papp.setOpenable(tapp.getOpenable());
            papp.setLockable(tapp.getLockable());
            papp.setTransparent(tapp.getTransparent());
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
            ptemp.setCategory(category);
            Map<String,PLocationRef> sqs = new HashMap<>();
            Map<String,PLocationRef> aps = new HashMap<>();
            char[][][] floors = ttemp.getFloors();
            for (int z = 0; z < floors.length; z++)
                for (int y = 0; y < floors[z].length; y++)
                    for (int x = 0; x < floors[z][y].length; x++)
                    {
                        int type = PTemplate.getType(x, y, z);
                        if (type == PTemplate.NOTHING)
                            continue;
                        char ch = floors[z][y][x];
                        String id = locs.getIDMap().getProperty(String.valueOf(ch));
                        if (id == null)
                            continue;
                        if (type == PTemplate.SQUARE)
                        {
                            if (!squares.containsKey(id))
                                continue;
                            PLocationRef sq = new PLocationRef();
                            sq.setID(id);
                            sq.setX(x);
                            sq.setY(y);
                            sq.setZ(z);
                            sqs.put(x+","+y+","+z, sq);
                        }
                        else
                        {
                            if (!apatures.containsKey(id))
                                continue;
                            PLocationRef ap = new PLocationRef();
                            ap.setID(id);
                            ap.setX(x);
                            ap.setY(y);
                            ap.setZ(z);
                            aps.put(x+","+y+","+z, ap);
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
        append(library, base, null, "core");
        for (String sub : new String[] { "shotgun", "standalone", "terrace", "warehouse" })
        {
            TLocations lib = read(baseDir + "\\locations\\"+sub+".json");
            append(library, lib, base, sub);
        }
        JSONObject json = library.toJSON();
        File libFile = new File(baseDir+"locationLibrary.json");
        JSONUtils.writeJSON(libFile, json);
        
    }
}
