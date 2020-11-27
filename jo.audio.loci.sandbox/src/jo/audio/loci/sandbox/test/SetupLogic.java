package jo.audio.loci.sandbox.test;

import java.io.File;

import jo.audio.loci.core.logic.DataStoreLogic;
import jo.util.utils.io.FileUtils;

public class SetupLogic
{
    public static void cleanup()
    {
        String dirName = System.getProperty("loci.store.disk.dir");
        if (dirName == null)
            dirName = System.getProperty("user.home")+System.getProperty("file.separator")+".loci";
        File data = new File(dirName);
        FileUtils.rmdir(data);
        DataStoreLogic.clearCache();
    }
}
