package jo.audio.loci.sandbox.vprofile;

import jo.audio.loci.core.data.VerbProfile;
import jo.audio.loci.sandbox.verb.VerbCreateThing;
import jo.audio.loci.sandbox.verb.VerbDescribe;
import jo.audio.loci.sandbox.verb.VerbDrop;
import jo.audio.loci.sandbox.verb.VerbInventory;
import jo.audio.loci.sandbox.verb.VerbName;
import jo.audio.loci.sandbox.verb.VerbPickUp;

public class VerbProfilePlayer extends VerbProfile
{
    public VerbProfilePlayer()
    {
        setExtends(VerbProfileThing.class);
        addVerbs(VerbDescribe.class, VerbName.class, VerbInventory.class, VerbCreateThing.class,
                VerbPickUp.class, VerbDrop.class);
    }
}
