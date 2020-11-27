package jo.audio.loci.thieves.logic;

import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.VerbProfile;
import jo.audio.loci.core.logic.DataProfileLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.VerbLogic;
import jo.audio.loci.core.logic.VerbProfileLogic;
import jo.audio.loci.core.logic.stores.MemoryStore;
import jo.audio.loci.thieves.data.LociContainer;
import jo.audio.loci.thieves.data.LociExit;
import jo.audio.loci.thieves.data.LociFoyeur;
import jo.audio.loci.thieves.data.LociIntersection;
import jo.audio.loci.thieves.data.LociItem;
import jo.audio.loci.thieves.data.LociPlayer;
import jo.audio.loci.thieves.data.LociPlayerAdmin;
import jo.audio.loci.thieves.data.LociPlayerGhost;
import jo.audio.loci.thieves.data.LociRoom;
import jo.audio.loci.thieves.data.LociStreet;
import jo.audio.loci.thieves.data.LociThing;
import jo.audio.loci.thieves.stores.ExitStore;
import jo.audio.loci.thieves.stores.HouseStore;
import jo.audio.loci.thieves.stores.SquareStore;
import jo.audio.loci.thieves.stores.StreetStore;
import jo.audio.loci.thieves.verbs.VerbDump;
import jo.audio.loci.thieves.verbs.VerbLogin;
import jo.audio.loci.thieves.verbs.VerbLogin2;
import jo.audio.loci.thieves.verbs.VerbLogout;
import jo.audio.loci.thieves.verbs.VerbLookDO;
import jo.audio.loci.thieves.verbs.VerbLookFoyeur;
import jo.audio.loci.thieves.verbs.VerbRegister;
import jo.audio.loci.thieves.verbs.VerbRegister2;
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
import jo.audio.loci.thieves.verbs.room.VerbLookHere;
import jo.audio.loci.thieves.verbs.room.VerbLookThrough;
import jo.audio.thieves.logic.ThievesConstLogic;

public class InitializeLogic
{
    public static final String ADMIN_URI= MemoryStore.PREFIX+"player/admin";
    private static final String FOYER_URI= MemoryStore.PREFIX+"room/foyeur";
    public static final String ENTRANCE_URI= ThievesConstLogic.INITIAL_LOCATION;

    public static void initialize()
    {
        LociObject.NAME_DELIM = "|";
        DataProfileLogic.registerDataProfile(LociThing.class);
        DataProfileLogic.registerDataProfile(LociPlayer.class);
        DataProfileLogic.registerDataProfile(LociPlayerAdmin.class);
        DataProfileLogic.registerDataProfile(LociPlayerGhost.class);
        DataProfileLogic.registerDataProfile(LociIntersection.class);
        DataProfileLogic.registerDataProfile(LociStreet.class);
        DataProfileLogic.registerDataProfile(LociRoom.class);
        DataProfileLogic.registerDataProfile(LociExit.class);
        DataProfileLogic.registerDataProfile(LociFoyeur.class);
        DataProfileLogic.registerDataProfile(LociItem.class);
        DataProfileLogic.registerDataProfile(LociContainer.class);
        VerbLogic.registerVerbs(
                new VerbLookHere(), 
                new VerbLookDO(), 
                //new VerbHelpRoom(), 
                //new VerbHelpDO(), 
                //new VerbDescribe(), 
                //new VerbName(), 
                //new VerbCreateItem(), 
                //new VerbCreateContainer(), 
                //new VerbInventory(), 
                //new VerbPickUp(), 
                //new VerbDrop(),
                //new VerbPutIn(),
                //new VerbTakeOut(),
                //new VerbOpen(),
                //new VerbShut(),
                //new VerbSet(),
                //new VerbDig(),
                //new VerbDigTo(),
                new VerbGoImplicit(),
                //new VerbDelete(),
                //new VerbHome(),
                //new VerbSetHelp(),
                //new VerbSay(),
                new VerbLogout(),
                new VerbDump(),
                new VerbEnter(), 
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
                new VerbLookFoyeur(), 
                new VerbRegister(), 
                new VerbRegister2(), 
                new VerbLogin(), 
                new VerbLogin2(),
                new VerbLookThrough()
                );
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfileThing").setExtendsName("VerbProfileObject")
                .addVerbs());
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfileItem").setExtendsName("VerbProfileThing")
                .addVerbs());
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfileContainer").setExtendsName("VerbProfileItem")
                .addVerbs());
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfileLocality").setExtendsName("VerbProfileThing")
                .addVerbs(VerbLookDO.class));
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfileRoom").setExtendsName("VerbProfileLocality")
                .addVerbs(
                        VerbLookThrough.class
                        ));
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfileIntersection").setExtendsName("VerbProfileLocality")
                .addVerbs(
                        VerbGoNorth.class, 
                        VerbGoNorthWest.class, 
                        VerbGoNorthEast.class, 
                        VerbGoSouth.class, 
                        VerbGoSouthEast.class, 
                        VerbGoSouthWest.class, 
                        VerbGoEast.class, 
                        VerbGoWest.class, 
                        VerbGoUp.class, 
                        VerbGoDown.class
                        ));
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfileStreet").setExtendsName("VerbProfileLocality")
                .addVerbs(
                        VerbGoNorth.class, 
                        VerbGoNorthWest.class, 
                        VerbGoNorthEast.class, 
                        VerbGoSouth.class, 
                        VerbGoSouthEast.class, 
                        VerbGoSouthWest.class, 
                        VerbGoEast.class, 
                        VerbGoWest.class, 
                        VerbGoUp.class, 
                        VerbGoDown.class,
                        VerbEnter.class));
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfileFoyeur").setExtendsName("VerbProfileLocality")
                .addVerbs(VerbRegister.class, VerbRegister2.class, VerbLogin.class, VerbLogin2.class, VerbLookFoyeur.class));
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfileExit").setExtendsName("VerbProfileThing")
                .addVerbs(VerbGoImplicit.class));
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfilePlayer").setExtendsName("VerbProfileThing")
                .addVerbs(VerbLogout.class, VerbDump.class, VerbLookHere.class));
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfilePlayerAdmin").setExtendsName("VerbProfilePlayer")
                .addVerbs());
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfilePlayerGhost").setExtendsName("VerbProfilePlayer")
                .addVerbs());
        DataStoreLogic.registerDataStore(new SquareStore());
        DataStoreLogic.registerDataStore(new ExitStore());
        DataStoreLogic.registerDataStore(new StreetStore());
        DataStoreLogic.registerDataStore(new HouseStore());
        // create mandatory objects
        createAdmin();
        createFoyeur();
    }

    private static void createFoyeur()
    {
        LociFoyeur foyeur = (LociFoyeur)DataStoreLogic.load(InitializeLogic.FOYER_URI);
        if (foyeur == null)
        {
            foyeur = new LociFoyeur(InitializeLogic.FOYER_URI);

            foyeur.setName("Foyeur");
            foyeur.setDescription("You are in a nebulous grey area, outside of reality. You can enter reality by saying register <username> <password> or login <username> <password>.");
            foyeur.setOwner(ADMIN_URI);
            DataStoreLogic.save(foyeur);
        }
    }
    
    public static LociFoyeur getFoyeur()
    {
        createFoyeur();
        LociFoyeur foyeur = (LociFoyeur)DataStoreLogic.load(InitializeLogic.FOYER_URI);
        return foyeur;
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
