package jo.audio.companions.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import jo.audio.util.PhoneticMatchLogic;

public class NamePicker
{
    private static final int NUM = 200;
    
    private Map<String, String> mHumanMale = new HashMap<>();
    private Map<String, String> mHumanFemale = new HashMap<>();
    private Map<String, String> mDwarfMale = new HashMap<>();
    private Map<String, String> mDwarfFemale = new HashMap<>();
    private Map<String, String> mElfMale = new HashMap<>();
    private Map<String, String> mElfFemale = new HashMap<>();

    public NamePicker()
    {        
    }
    
    public void run() throws Exception
    {
        File nameDir = new File("C:\\Users\\IBM_ADMIN\\git\\TsaTsaTzuAlexa\\jo.echo.tweet.poll\\src\\jo\\namenerd\\slu");
        for (int yob = 2014; yob >= 1880; yob--)
        {
            File nameFile = new File(nameDir, "yob"+yob+".txt");
            BufferedReader rdr = new BufferedReader(new FileReader(nameFile));
            for (int i = 0; i < 2000; i++)
            {
                String inbuf = rdr.readLine();
                if (inbuf == null)
                    break;
                processLine(inbuf);
                if (isDone())
                    break;
            }
            rdr.close();
            if (isDone())
                break;
        }
        dumpNames(mHumanMale, "HUMAN_MALE_NAMES");
        dumpNames(mHumanFemale, "HUMAN_FEMALE_NAMES");
        dumpNames(mDwarfMale, "DWARF_MALE_NAMES");
        dumpNames(mDwarfFemale, "DWARF_FEMALE_NAMES");
        dumpNames(mElfMale, "ELF_MALE_NAMES");
        dumpNames(mElfFemale, "ELF_FEMALE_NAMES");
        if (!isDone())
            System.out.println("Did not reach quota!");
    }
    
    private void dumpNames(Map<String, String> index, String title)
    {
        String[] names = index.values().toArray(new String[0]);
        Arrays.sort(names);
        System.out.println("\t\t// "+names.length);
        System.out.println("\t\tpublic static final String[] "+title+" = {");
        for (String name : names)
            System.out.println("\t\t\t\""+name+"\",");
        System.out.println("\t\t\t};");
    }

    private void processLine(String inbuf)
    {
        StringTokenizer st = new StringTokenizer(inbuf, ",");
        String name = st.nextToken();
        String gender = st.nextToken();
        String firstLetter = name.substring(0, 1);
        if ("M".equals(gender))
        {
            if ("CDKQTVWXZ".indexOf(firstLetter) >= 0)
                processName(name, mDwarfMale);
            else if ("AEILOSUY".indexOf(firstLetter) >= 0)
                processName(name, mElfMale);
            else
                processName(name, mHumanMale);
        }
        else
        {
            if ("CDKQTVWXZ".indexOf(firstLetter) >= 0)
                processName(name, mDwarfFemale);
            else if ("AEILOSUY".indexOf(firstLetter) >= 0)
                processName(name, mElfFemale);
            else
                processName(name, mHumanFemale);
        }
    }
    
    private void processName(String name, Map<String, String> index)
    {
        if (index.size() >= NUM)
            return;
        String phonetic = PhoneticMatchLogic.mDoubleMetaphone.doubleMetaphone(name);
        if (index.containsKey(phonetic))
            return;
        index.put(phonetic, name);
    }

    private boolean isDone()
    {
        if (mHumanFemale.size() < NUM)
            return false;
        if (mHumanMale.size() < NUM)
            return false;
        if (mDwarfFemale.size() < NUM)
            return false;
        if (mDwarfMale.size() < NUM)
            return false;
        if (mElfFemale.size() < NUM)
            return false;
        if (mElfMale.size() < NUM)
            return false;
        return true;
    }
    
    public static void main(String[] argv)
    {
        NamePicker app = new NamePicker();
        try
        {
            app.run();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
