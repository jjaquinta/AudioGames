package jo.audio.loci.sandbox.logic;

import jo.audio.loci.core.logic.DataProfileLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.VerbLogic;
import jo.audio.loci.core.logic.VerbProfileLogic;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.core.logic.stores.MemoryStore;
import jo.audio.loci.sandbox.data.LociCookie;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociRoom;
import jo.audio.loci.sandbox.data.LociThing;
import jo.audio.loci.sandbox.verb.VerbCreateThing;
import jo.audio.loci.sandbox.verb.VerbDescribe;
import jo.audio.loci.sandbox.verb.VerbDrop;
import jo.audio.loci.sandbox.verb.VerbHelpRoom;
import jo.audio.loci.sandbox.verb.VerbInventory;
import jo.audio.loci.sandbox.verb.VerbLogin;
import jo.audio.loci.sandbox.verb.VerbLookDO;
import jo.audio.loci.sandbox.verb.VerbLookIO;
import jo.audio.loci.sandbox.verb.VerbLookRoom;
import jo.audio.loci.sandbox.verb.VerbName;
import jo.audio.loci.sandbox.verb.VerbPickUp;
import jo.audio.loci.sandbox.verb.VerbRegister;
import jo.audio.loci.sandbox.vprofile.VerbProfileFoyeur;
import jo.audio.loci.sandbox.vprofile.VerbProfilePlayer;
import jo.audio.loci.sandbox.vprofile.VerbProfilePlayerAdmin;
import jo.audio.loci.sandbox.vprofile.VerbProfileRoom;
import jo.audio.loci.sandbox.vprofile.VerbProfileThing;

public class InitializeLogic
{
    public static final String FOYER_URI= MemoryStore.PREFIX+"room/foyeur";
    public static final String ENTRANCE_URI= DiskStore.PREFIX+"room/entrance";

    public static void initialize()
    {
        DataProfileLogic.registerDataProfile(LociCookie.PROFILE, LociCookie.class);
        DataProfileLogic.registerDataProfile(LociThing.PROFILE, LociThing.class);
        DataProfileLogic.registerDataProfile(LociPlayer.PROFILE, LociPlayer.class);
        DataProfileLogic.registerDataProfile(LociRoom.PROFILE, LociRoom.class);
        VerbLogic.registerVerbs(new VerbLookRoom(), 
                new VerbLookDO(), 
                new VerbLookIO(), 
                new VerbHelpRoom(), 
                new VerbRegister(), 
                new VerbLogin(), 
                new VerbDescribe(), 
                new VerbName(), 
                new VerbCreateThing(), 
                new VerbInventory(), 
                new VerbPickUp(), 
                new VerbDrop());
        VerbProfileLogic.registerVerbProfile(new VerbProfileThing());
        VerbProfileLogic.registerVerbProfile(new VerbProfileRoom());
        VerbProfileLogic.registerVerbProfile(new VerbProfileFoyeur());
        VerbProfileLogic.registerVerbProfile(new VerbProfilePlayer());
        VerbProfileLogic.registerVerbProfile(new VerbProfilePlayerAdmin());
        // create mandatory objects
        LociRoom foyeur = (LociRoom)DataStoreLogic.load(InitializeLogic.FOYER_URI);
        if (foyeur == null)
        {
            foyeur = new LociRoom(InitializeLogic.FOYER_URI);
            foyeur.setVerbProfile(VerbProfileFoyeur.class.getSimpleName());
            foyeur.setName("Foyeur");
            foyeur.setDescription("You are in a nebulous grey area, outside of reality. For a list of commands, type help.");
            DataStoreLogic.save(foyeur);
        }
        LociRoom entrance = (LociRoom)DataStoreLogic.load(InitializeLogic.ENTRANCE_URI);
        if (entrance == null)
        {
            entrance = new LociRoom(InitializeLogic.ENTRANCE_URI);
            entrance.setName("Entrance Hall");
            entrance.setDescription("This is the wonderful, welcoming, first room of the sandbox.");
            DataStoreLogic.save(entrance);
        }
    }
}
