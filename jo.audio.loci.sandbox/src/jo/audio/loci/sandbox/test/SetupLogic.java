package jo.audio.loci.sandbox.test;

import java.io.File;

import jo.audio.loci.core.logic.stores.DiskStore;
import jo.util.utils.io.FileUtils;

public class SetupLogic
{
    public static void cleanup()
    {
        File data = new File(DiskStore.getDiskLocation());
        FileUtils.rmdir(data);
    }
}
