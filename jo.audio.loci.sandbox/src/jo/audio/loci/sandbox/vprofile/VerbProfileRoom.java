package jo.audio.loci.sandbox.vprofile;

import jo.audio.loci.core.data.VerbProfile;
import jo.audio.loci.core.logic.vprofile.VerbProfileObject;
import jo.audio.loci.sandbox.verb.VerbHelpRoom;
import jo.audio.loci.sandbox.verb.VerbLookRoom;

public class VerbProfileRoom extends VerbProfile
{
    public VerbProfileRoom()
    {
        setExtends(VerbProfileObject.class);
        addVerbs(VerbLookRoom.class, VerbHelpRoom.class);
    }
}
