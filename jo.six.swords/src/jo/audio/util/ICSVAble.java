package jo.audio.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public interface ICSVAble
{
    public static DateFormat mDATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String[]   toCSVHeader();
    public String   toCSV();
    public void fromCSV(String csv);
    public File getArchiveFile();
    public File getActiveFile();
}
