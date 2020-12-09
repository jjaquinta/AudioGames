package jo.audio.loci.thieves.logic;

import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONObject;

import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.thieves.data.LociSquare;
import jo.audio.loci.thieves.data.LociThing;
import jo.audio.loci.thieves.stores.ApatureStore;
import jo.audio.loci.thieves.stores.ExitStore;
import jo.audio.loci.thieves.stores.SquareStore;
import jo.audio.loci.thieves.stores.SquareStore.SquareURI;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.logic.ThievesConstLogic;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.StringUtils;

public class PopulateSquareLogic
{
    public static void populate(LociSquare square)
    {
        String u = square.getURI();
        SquareURI uri = ((SquareStore)DataStoreLogic.getStore(SquareStore.PREFIX)).new SquareURI(u);
        square.setURIObject(uri);
        PSquare sq = uri.getThis();
        JSONObject properties = square.getProperties();
        properties.put(LociSquare.ID_NAME, sq.getName());
        if (StringUtils.isTrivial(sq.getDescription()))
            properties.put(LociSquare.ID_DECRIPTION, "");
        else
            properties.put(LociSquare.ID_DECRIPTION, sq.getDescription());
        Set<String> contains = new HashSet<>();
        String[] cs = square.getContains();
        if (cs != null)
            for (String c : cs)
                if (!c.startsWith(ExitStore.PREFIX) && !c.startsWith(ApatureStore.PREFIX))
                    contains.add(c);
        for (int dir : ThievesConstLogic.ORTHOGONAL_DIRS)
        {
            PLocationRef exit = uri.getApatureRef(dir);
            if (exit != null)
            {
                if ("EXIT".equals(exit.getID()))
                {
                    String streetID = StringUtils.stripAfterLast(u.substring(SquareStore.PREFIX.length()), ":");
                    String sURI = ExitStore.PREFIX+streetID+"/"+streetID+"/"+dir;
                    contains.add(sURI);
                }
                else
                {    
                    ApatureStore.ApatureURI aURI = ((ApatureStore)DataStoreLogic.getStore(ApatureStore.PREFIX)).new ApatureURI(
                            uri.mStreet, uri.mHouseNum, exit, dir);
                    contains.add(aURI.toURI());
                }
            }
        }
        square.setContains(contains.toArray(new String[0]));
        if (BooleanUtils.parseBoolean(properties.get("$firstTime")))
            firstTimeSetup(square);
    }
    
    private static void firstTimeSetup(LociSquare square)
    {
        //Random rnd = new Random(square.getURI().hashCode());
        SquareURI uri = square.getURIObject();
        if (uri.mLocation.getTags().contains(PLocationRef.TAG_BED))
        {
            String u = DiskStore.PREFIX+"bed/"+square.getURI().substring(SquareStore.PREFIX.length());
            LociThing bed = new LociThing(u);
            bed.setName("Bed");
            bed.setDescription("Homespun linen on a pine frame.");
            bed.setPublic(false);
            ContainmentLogic.add(square, bed);
        }
    }
}
