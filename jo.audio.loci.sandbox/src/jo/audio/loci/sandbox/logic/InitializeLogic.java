package jo.audio.loci.sandbox.logic;

import jo.audio.loci.core.data.VerbProfile;
import jo.audio.loci.core.logic.DataProfileLogic;
import jo.audio.loci.core.logic.DataStoreLogic;
import jo.audio.loci.core.logic.VerbLogic;
import jo.audio.loci.core.logic.VerbProfileLogic;
import jo.audio.loci.core.logic.stores.DiskStore;
import jo.audio.loci.core.logic.stores.MemoryStore;
import jo.audio.loci.sandbox.data.LociContainer;
import jo.audio.loci.sandbox.data.LociCookie;
import jo.audio.loci.sandbox.data.LociItem;
import jo.audio.loci.sandbox.data.LociPlayer;
import jo.audio.loci.sandbox.data.LociRoom;
import jo.audio.loci.sandbox.data.LociThing;
import jo.audio.loci.sandbox.verb.VerbShut;
import jo.audio.loci.sandbox.verb.VerbCreateContainer;
import jo.audio.loci.sandbox.verb.VerbCreateItem;
import jo.audio.loci.sandbox.verb.VerbDescribe;
import jo.audio.loci.sandbox.verb.VerbDrop;
import jo.audio.loci.sandbox.verb.VerbHelpRoom;
import jo.audio.loci.sandbox.verb.VerbInventory;
import jo.audio.loci.sandbox.verb.VerbLogin;
import jo.audio.loci.sandbox.verb.VerbLookDO;
import jo.audio.loci.sandbox.verb.VerbLookIO;
import jo.audio.loci.sandbox.verb.VerbLookRoom;
import jo.audio.loci.sandbox.verb.VerbName;
import jo.audio.loci.sandbox.verb.VerbOpen;
import jo.audio.loci.sandbox.verb.VerbPickUp;
import jo.audio.loci.sandbox.verb.VerbPutIn;
import jo.audio.loci.sandbox.verb.VerbRegister;
import jo.audio.loci.sandbox.verb.VerbSet;
import jo.audio.loci.sandbox.verb.VerbTakeOut;

public class InitializeLogic
{
    public static final String FOYER_URI= MemoryStore.PREFIX+"room/foyeur";
    public static final String ENTRANCE_URI= DiskStore.PREFIX+"room/entrance";

    public static void initialize()
    {
        DataProfileLogic.registerDataProfile(LociCookie.class);
        DataProfileLogic.registerDataProfile(LociThing.class);
        DataProfileLogic.registerDataProfile(LociPlayer.class);
        DataProfileLogic.registerDataProfile(LociRoom.class);
        DataProfileLogic.registerDataProfile(LociItem.class);
        DataProfileLogic.registerDataProfile(LociContainer.class);
        VerbLogic.registerVerbs(new VerbLookRoom(), 
                new VerbLookDO(), 
                new VerbLookIO(), 
                new VerbHelpRoom(), 
                new VerbRegister(), 
                new VerbLogin(), 
                new VerbDescribe(), 
                new VerbName(), 
                new VerbCreateItem(), 
                new VerbCreateContainer(), 
                new VerbInventory(), 
                new VerbPickUp(), 
                new VerbDrop(),
                new VerbPutIn(),
                new VerbTakeOut(),
                new VerbOpen(),
                new VerbShut(),
                new VerbSet());
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfileThing").setExtendsName("VerbProfileObject")
                .addVerbs(VerbLookDO.class, VerbLookIO.class, VerbSet.class));
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfileItem").setExtendsName("VerbProfileThing")
                .addVerbs(VerbPickUp.class, VerbDrop.class));
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfileContainer").setExtendsName("VerbProfileItem")
                .addVerbs(VerbPutIn.class, VerbTakeOut.class, VerbOpen.class, VerbShut.class));
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfileRoom").setExtendsName("VerbProfileObject")
                .addVerbs(VerbLookRoom.class, VerbHelpRoom.class));
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfileFoyeur").setExtendsName("VerbProfileRoom")
                .addVerbs(VerbRegister.class, VerbLogin.class));
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfilePlayer").setExtendsName("VerbProfileThing")
                .addVerbs(VerbDescribe.class, VerbName.class, VerbInventory.class, VerbCreateItem.class,
                        VerbCreateContainer.class, VerbPickUp.class, VerbDrop.class));
        VerbProfileLogic.registerVerbProfile(VerbProfile.build("VerbProfilePlayerAdmin").setExtendsName("VerbProfilePlayer")
                .addVerbs());
        // create mandatory objects
        LociRoom foyeur = (LociRoom)DataStoreLogic.load(InitializeLogic.FOYER_URI);
        if (foyeur == null)
        {
            foyeur = new LociRoom(InitializeLogic.FOYER_URI);
            foyeur.setVerbProfile("VerbProfileFoyeur");
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
