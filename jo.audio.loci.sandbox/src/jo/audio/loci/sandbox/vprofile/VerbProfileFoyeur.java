package jo.audio.loci.sandbox.vprofile;

import jo.audio.loci.core.data.VerbProfile;
import jo.audio.loci.sandbox.verb.VerbLogin;
import jo.audio.loci.sandbox.verb.VerbRegister;

public class VerbProfileFoyeur extends VerbProfile
{
    public VerbProfileFoyeur()
    {
        setExtends(VerbProfileRoom.class);
        addVerbs(VerbRegister.class, VerbLogin.class);
    }
}
