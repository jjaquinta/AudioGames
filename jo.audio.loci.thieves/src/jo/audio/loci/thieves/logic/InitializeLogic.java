package jo.audio.loci.thieves.logic;

import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.logic.DataProfileLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.VerbLogic;
import jo.audio.loci.core.logic.stores.MemoryStore;
import jo.audio.loci.thieves.data.LociApature;
import jo.audio.loci.thieves.data.LociContainer;
import jo.audio.loci.thieves.data.LociExit;
import jo.audio.loci.thieves.data.LociFoyeur;
import jo.audio.loci.thieves.data.LociIntersection;
import jo.audio.loci.thieves.data.LociItem;
import jo.audio.loci.thieves.data.LociLocality;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociPlayerAdmin;
import jo.audio.loci.thieves.data.LociPlayerGhost;
import jo.audio.loci.thieves.data.LociSquare;
import jo.audio.loci.thieves.data.LociStreet;
import jo.audio.loci.thieves.data.LociThing;
import jo.audio.loci.thieves.data.LociTreasure;
import jo.audio.loci.thieves.data.npc.LociBuyer;
import jo.audio.loci.thieves.data.npc.LociNPC;
import jo.audio.loci.thieves.stores.ApatureStore;
import jo.audio.loci.thieves.stores.ExitStore;
import jo.audio.loci.thieves.stores.IntersectionStore;
import jo.audio.loci.thieves.stores.SquareStore;
import jo.audio.loci.thieves.stores.StreetStore;
import jo.audio.loci.thieves.verbs.VerbHelp;
import jo.audio.loci.thieves.verbs.VerbInventory;
import jo.audio.loci.thieves.verbs.VerbLogin;
import jo.audio.loci.thieves.verbs.VerbLogin2;
import jo.audio.loci.thieves.verbs.VerbLogout;
import jo.audio.loci.thieves.verbs.VerbLookDO;
import jo.audio.loci.thieves.verbs.VerbLookFoyeur;
import jo.audio.loci.thieves.verbs.VerbLookHouse;
import jo.audio.loci.thieves.verbs.VerbMore;
import jo.audio.loci.thieves.verbs.VerbRegister;
import jo.audio.loci.thieves.verbs.VerbRegister2;
import jo.audio.loci.thieves.verbs.admin.VerbAddXP;
import jo.audio.loci.thieves.verbs.admin.VerbDump;
import jo.audio.loci.thieves.verbs.item.VerbAppraise;
import jo.audio.loci.thieves.verbs.item.VerbDrop;
import jo.audio.loci.thieves.verbs.item.VerbPickUp;
import jo.audio.loci.thieves.verbs.item.VerbPutIn;
import jo.audio.loci.thieves.verbs.item.VerbSell;
import jo.audio.loci.thieves.verbs.item.VerbTakeOut;
import jo.audio.loci.thieves.verbs.move.VerbEnter;
import jo.audio.loci.thieves.verbs.move.VerbGoDown;
import jo.audio.loci.thieves.verbs.move.VerbGoEast;
import jo.audio.loci.thieves.verbs.move.VerbGoImplicit;
import jo.audio.loci.thieves.verbs.move.VerbGoNorth;
import jo.audio.loci.thieves.verbs.move.VerbGoNorthEast;
import jo.audio.loci.thieves.verbs.move.VerbGoNorthWest;
import jo.audio.loci.thieves.verbs.move.VerbGoSouth;
import jo.audio.loci.thieves.verbs.move.VerbGoSouthEast;
import jo.audio.loci.thieves.verbs.move.VerbGoSouthWest;
import jo.audio.loci.thieves.verbs.move.VerbGoUp;
import jo.audio.loci.thieves.verbs.move.VerbGoWest;
import jo.audio.loci.thieves.verbs.room.VerbClose;
import jo.audio.loci.thieves.verbs.room.VerbLock;
import jo.audio.loci.thieves.verbs.room.VerbLookHere;
import jo.audio.loci.thieves.verbs.room.VerbLookThrough;
import jo.audio.loci.thieves.verbs.room.VerbOpen;
import jo.audio.loci.thieves.verbs.room.VerbUnlock;
import jo.audio.thieves.logic.ThievesConstLogic;

public class InitializeLogic
{
    public static final String ADMIN_URI= MemoryStore.PREFIX+"player/admin";
    private static final String FOYER_URI= MemoryStore.PREFIX+"room/foyeur";
    public static final String ENTRANCE_URI= ThievesConstLogic.INITIAL_LOCATION;

    public static void initialize()
    {
        LociObject.NAME_DELIM = "|";
        VerbLogic.registerVerbs(LociThing.class
                );
        VerbLogic.registerVerbs(LociItem.class,
                new VerbDrop(), new VerbPickUp());
        DataProfileLogic.registerDataProfile(LociContainer.class);
        DataProfileLogic.registerDataProfile(LociTreasure.class);
        DataProfileLogic.registerDataProfile(LociNPC.class);
        VerbLogic.registerVerbs(LociBuyer.class,
                new VerbSell(), new VerbAppraise()); 
        VerbLogic.registerVerbs(LociContainer.class,
                new VerbPutIn(), new VerbTakeOut(), 
                new jo.audio.loci.thieves.verbs.item.VerbClose(), new jo.audio.loci.thieves.verbs.item.VerbOpen(),
                new jo.audio.loci.thieves.verbs.item.VerbLock(), new jo.audio.loci.thieves.verbs.item.VerbUnlock()
                );
        VerbLogic.registerVerbs(LociExit.class,
                new VerbGoImplicit());
        VerbLogic.registerVerbs(LociApature.class,
                new VerbGoImplicit());
        VerbLogic.registerVerbs(LociLocality.class,
                new VerbLookDO());
        VerbLogic.registerVerbs(LociSquare.class,                
                        new VerbLookThrough(),
                        new VerbOpen(),
                        new VerbClose(),
                        new VerbLock(),
                        new VerbUnlock()
                        );
        VerbLogic.registerVerbs(LociIntersection.class,                
                        new VerbGoNorth(), 
                        new VerbGoNorthWest(), 
                        new VerbGoNorthEast(), 
                        new VerbGoSouth(), 
                        new VerbGoSouthEast(), 
                        new VerbGoSouthWest(), 
                        new VerbGoEast(), 
                        new VerbGoWest(), 
                        new VerbGoUp(), 
                        new VerbGoDown()
                        );
        VerbLogic.registerVerbs(LociStreet.class,                
                        new VerbGoNorth(), 
                        new VerbGoNorthWest(), 
                        new VerbGoNorthEast(), 
                        new VerbGoSouth(), 
                        new VerbGoSouthEast(), 
                        new VerbGoSouthWest(), 
                        new VerbGoEast(), 
                        new VerbGoWest(), 
                        new VerbGoUp(), 
                        new VerbGoDown(),
                        new VerbEnter(),
                        new VerbLookHouse());
        VerbLogic.registerVerbs(LociFoyeur.class,
                new VerbRegister(), new VerbRegister2(), new VerbLogin(), new VerbLogin2(), new VerbLookFoyeur());
        VerbLogic.registerVerbs(LociPlayer.class,
                new VerbLogout(), new VerbLookHere(), new VerbMore(), new VerbInventory(), new VerbHelp());
        VerbLogic.registerVerbs(LociPlayerAdmin.class,
                new VerbDump(), new VerbAddXP());
        VerbLogic.registerVerbs(LociPlayerGhost.class);
        DataStoreLogic.registerDataStore(new IntersectionStore());
        DataStoreLogic.registerDataStore(new ExitStore());
        DataStoreLogic.registerDataStore(new StreetStore());
        DataStoreLogic.registerDataStore(new ApatureStore());
        DataStoreLogic.registerDataStore(new SquareStore());
        // create mandatory objects
        createAdmin();
        createFoyeur();
    }

    private static LociFoyeur createFoyeur()
    {
        LociFoyeur foyeur = (LociFoyeur)DataStoreLogic.load(InitializeLogic.FOYER_URI);
        if (foyeur == null)
        {
            foyeur = new LociFoyeur(InitializeLogic.FOYER_URI);
            foyeur.setName("City Gates");
            foyeur.setDescription("You are on a lonely froad, outside of the city. You can enter by saying register <username> <password> or login <username> <password>.");
            foyeur.setOwner(ADMIN_URI);
            DataStoreLogic.save(foyeur);
        }
        return foyeur;
    }
    
    public static LociFoyeur getFoyeur()
    {
        return createFoyeur();
    }

    private static void createAdmin()
    {
        LociPlayerAdmin admin = (LociPlayerAdmin)DataStoreLogic.load(InitializeLogic.ADMIN_URI);
        if (admin == null)
        {
            admin = new LociPlayerAdmin(InitializeLogic.ADMIN_URI);
            admin.setName("admin");
            admin.setDescription("A mysterious figure in blue wizard robes.");
            admin.setPassword("lollipop");
            admin.setOwner(ADMIN_URI);
            DataStoreLogic.save(admin);
        }
    }
}
