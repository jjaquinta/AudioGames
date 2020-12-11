package jo.audio.loci.thieves.logic;

import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONObject;

import jo.audio.loci.core.logic.ContainmentLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.thieves.data.LociContainer;
import jo.audio.loci.thieves.data.LociItemStackable;
import jo.audio.loci.thieves.data.LociSquare;
import jo.audio.loci.thieves.data.LociThing;
import jo.audio.loci.thieves.data.LociTreasure;
import jo.audio.loci.thieves.data.npc.LociBuyer;
import jo.audio.loci.thieves.stores.ApatureStore;
import jo.audio.loci.thieves.stores.ExitStore;
import jo.audio.loci.thieves.stores.SquareStore;
import jo.audio.loci.thieves.stores.SquareStore.SquareURI;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.data.template.PSquare;
import jo.audio.thieves.logic.DiceLogic;
import jo.audio.thieves.logic.JewelryLogic;
import jo.audio.thieves.logic.LocationLogic;
import jo.audio.thieves.logic.ThievesConstLogic;
import jo.util.utils.MathUtils;
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
            addBed(square, uri);
        if (uri.mLocation.getTags().contains(PLocationRef.TAG_CHEST))
            addChest(square, uri);
        if (uri.mLocation.getTags().contains(PLocationRef.TAG_BUYER))
            addBuyer(square, uri);
    }

    private static void addBuyer(LociSquare square, SquareURI uri)
    {
        String u = DiskStore.PREFIX+"buyer/"+square.getURI().substring(SquareStore.PREFIX.length());
        LociBuyer buyer = new LociBuyer(u);
        if (uri.mHouse.getTemplate().getID().startsWith("GUILD"))
        {
            buyer.setName("{{FENCE_NAMES#"+(uri.mStreet.getHighIntersection().getY() > 0 ? 0 : 1)+"}}|fence");
            buyer.setDescription("The guild's fence will buy your stolen goods for a non-negotiable price.");
            buyer.setType("stolen");
        }
        buyer.setPublic(false);
        ContainmentLogic.add(square, buyer);
    }

    private static void addBed(LociSquare square, SquareURI uri)
    {
        String u = DiskStore.PREFIX+"bed/"+square.getURI().substring(SquareStore.PREFIX.length());
        LociThing bed = new LociThing(u);
        bed.setName("Bed");
        bed.setDescription("{{BED_DESCRIPTION#"+uri.mHouse.getPosh()+"}}");
        bed.setPublic(false);
        ContainmentLogic.add(square, bed);
    }

    private static void addChest(LociSquare square, SquareURI uri)
    {
        String u = DiskStore.PREFIX+"chest/"+square.getURI().substring(SquareStore.PREFIX.length());
        LociContainer chest = new LociContainer(u);
        chest.setName("Chest");
        chest.setDescription("{{CHEST_DESCRIPTION#"+uri.mHouse.getPosh()+"}}");
        chest.setPublic(false);
        chest.setLockable(true);
        chest.setLocked(true);
        chest.setOpenLocksMod((int)MathUtils.interpolate(uri.mHouse.getPosh(), 0, 1, 5, -25));
        ContainmentLogic.add(square, chest);
        if (uri.mHouse.getPosh() > .5)
            addJewelry(uri, chest);
        addCoins(uri, chest);
    }
    
    private static void addJewelry(SquareURI uri, LociContainer chest)
    {
        String u = DiskStore.PREFIX+"jewelry/"+LocationLogic.getCity().getRND().nextInt();
        LociTreasure item = new LociTreasure(u);
        StringBuffer name = new StringBuffer();
        int value = JewelryLogic.rollJewelry(null, name);
        item.setName(name.toString());
        item.setDescription("A lovely piece worth "+value+".");
        item.setPublic(true);
        item.setValue(value);
        item.setType("stolen");
        ContainmentLogic.add(chest, item);
    }

    private static final String[] COIN_TYPE = new String[] {
                                               "copper",
                                               "silver",
                                               "gold",
                                               "platinum"
    };
    
    private static void addCoins(SquareURI uri, LociContainer chest)
    {
        String u = DiskStore.PREFIX+"coins/"+LocationLogic.getCity().getRND().nextInt();
        LociItemStackable item = new LociItemStackable(u);
        int num = (int)Math.round(MathUtils.interpolate(uri.mHouse.getPosh(), 0, 1, 1, 8));
        int die = (int)Math.round(MathUtils.interpolate(uri.mHouse.getPosh(), 0, 1, 4, 12));
        int type = (int)Math.round(MathUtils.interpolate(uri.mHouse.getPosh(), 0, 1, 0, 3));
        int count = DiceLogic.d(die, num);
        item.setName(COIN_TYPE[type]);
        item.setDescription("A small bag of "+COIN_TYPE[type]+" coins.");
        item.setPublic(true);
        item.setCount(count);
        item.setClassification(COIN_TYPE[type]);
        ContainmentLogic.add(chest, item);
    }
}
