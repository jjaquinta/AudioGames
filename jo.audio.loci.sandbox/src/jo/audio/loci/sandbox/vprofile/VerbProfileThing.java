package jo.audio.loci.sandbox.vprofile;

import jo.audio.loci.core.data.VerbProfile;
import jo.audio.loci.sandbox.verb.VerbLookDO;
import jo.audio.loci.sandbox.verb.VerbLookIO;

public class VerbProfileThing extends VerbProfile
{
    public VerbProfileThing()
    {
        addVerbs(VerbLookDO.class, VerbLookIO.class);
    }
}
