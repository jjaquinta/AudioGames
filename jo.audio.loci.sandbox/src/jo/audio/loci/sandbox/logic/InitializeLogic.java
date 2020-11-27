package jo.audio.loci.sandbox.logic;

import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.VerbLogic;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.core.logic.stores.MemoryStore;
import jo.audio.loci.sandbox.data.LociContainer;
import jo.audio.loci.sandbox.data.LociExit;
import jo.audio.loci.sandbox.data.LociItem;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociPlayerAdmin;
import jo.audio.loci.sandbox.data.LociPlayerGhost;
import jo.audio.loci.sandbox.data.LociRoom;
import jo.audio.loci.sandbox.data.LociRoomFoyeur;
import jo.audio.loci.sandbox.data.LociThing;
import jo.audio.loci.sandbox.verb.VerbCreateContainer;
import jo.audio.loci.sandbox.verb.VerbCreateItem;
import jo.audio.loci.sandbox.verb.VerbDelete;
import jo.audio.loci.sandbox.verb.VerbDescribe;
import jo.audio.loci.sandbox.verb.VerbDig;
import jo.audio.loci.sandbox.verb.VerbDigTo;
import jo.audio.loci.sandbox.verb.VerbDrop;
import jo.audio.loci.sandbox.verb.VerbDump;
import jo.audio.loci.sandbox.verb.VerbGoImplicit;
import jo.audio.loci.sandbox.verb.VerbHelpDO;
import jo.audio.loci.sandbox.verb.VerbHelpRoom;
import jo.audio.loci.sandbox.verb.VerbHome;
import jo.audio.loci.sandbox.verb.VerbInventory;
import jo.audio.loci.sandbox.verb.VerbLogin;
import jo.audio.loci.sandbox.verb.VerbLogin2;
import jo.audio.loci.sandbox.verb.VerbLogout;
import jo.audio.loci.sandbox.verb.VerbLookDO;
import jo.audio.loci.sandbox.verb.VerbLookIO;
import jo.audio.loci.sandbox.verb.VerbLookRoom;
import jo.audio.loci.sandbox.verb.VerbName;
import jo.audio.loci.sandbox.verb.VerbOpen;
import jo.audio.loci.sandbox.verb.VerbPickUp;
import jo.audio.loci.sandbox.verb.VerbPutIn;
import jo.audio.loci.sandbox.verb.VerbRegister;
import jo.audio.loci.sandbox.verb.VerbRegister2;
import jo.audio.loci.sandbox.verb.VerbSay;
import jo.audio.loci.sandbox.verb.VerbSet;
import jo.audio.loci.sandbox.verb.VerbSetHelp;
import jo.audio.loci.sandbox.verb.VerbShut;
import jo.audio.loci.sandbox.verb.VerbTakeOut;

public class InitializeLogic
{
    private static final String ADMIN_URI= MemoryStore.PREFIX+"player/admin";
    public static final String FOYER_URI= MemoryStore.PREFIX+"room/foyeur";
    private static final String ENTRANCE_URI= DiskStore.PREFIX+"room/entrance";

    public static void initialize()
    {
        VerbLogic.registerVerbs(LociThing.class,
                new VerbLookDO(), new VerbLookIO(), new VerbSet(), new VerbDelete(), new VerbSetHelp(),
                        new VerbHelpDO());
        VerbLogic.registerVerbs(LociItem.class,
                new VerbPickUp(), new VerbDrop());
        VerbLogic.registerVerbs(LociContainer.class,
                new VerbPutIn(), new VerbTakeOut(), new VerbOpen(), new VerbShut());
        VerbLogic.registerVerbs(LociRoom.class,
                new VerbLookRoom(), new VerbHelpRoom());
        VerbLogic.registerVerbs(LociRoomFoyeur.class,
                new VerbRegister(), new VerbRegister2(), new VerbLogin(), new VerbLogin2());
        VerbLogic.registerVerbs(LociExit.class,
                new VerbGoImplicit());
        VerbLogic.registerVerbs(LociPlayer.class,
                new VerbDescribe(), new VerbName(), new VerbInventory(), new VerbCreateItem(),
                        new VerbCreateContainer(), new VerbDigTo(), new VerbDig(), new VerbSay(),
                        new VerbHome(), new VerbLogout(), new VerbDump());
        VerbLogic.registerVerbs(LociPlayerAdmin.class);
        VerbLogic.registerVerbs(LociPlayerGhost.class);
        // create mandatory objects
        createAdmin();
        createFoyeur();
        createEntrance();
    }

    private static LociRoom createEntrance()
    {
        LociRoom entrance = (LociRoom)DataStoreLogic.load(InitializeLogic.ENTRANCE_URI);
        if (entrance == null)
        {
            entrance = new LociRoom(InitializeLogic.ENTRANCE_URI);
            entrance.setName("Entrance Hall");
            entrance.setDescription("Coats, hats, and other outerwear hang on hooks along the walls of this comfortable room.");
            entrance.setPublic(true);
            entrance.setOwner(ADMIN_URI);
            DataStoreLogic.save(entrance);
        }
        return entrance;
    }

    private static LociRoom createFoyeur()
    {
        LociRoomFoyeur foyeur = (LociRoomFoyeur)DataStoreLogic.load(InitializeLogic.FOYER_URI);
        if (foyeur == null)
        {
            foyeur = new LociRoomFoyeur(InitializeLogic.FOYER_URI);
            foyeur.setName("Foyeur");
            foyeur.setDescription("You are in a nebulous grey area, outside of reality. You can enter reality by saying register <username> <password> or login <username> <password>.");
            foyeur.setOwner(ADMIN_URI);
            DataStoreLogic.save(foyeur);
        }
        return foyeur;
    }

    private static LociPlayerAdmin createAdmin()
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
        return admin;
    }
    
    public static LociRoom getFoyeur()
    {
        return createFoyeur();
    }

    public static LociRoom geEntrance()
    {
        return createEntrance();
    }
}
