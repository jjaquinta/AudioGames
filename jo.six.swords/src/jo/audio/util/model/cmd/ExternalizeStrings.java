package jo.audio.util.model.cmd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.util.model.data.InteractionModelBean;
import jo.audio.util.model.logic.ParseModelLogic;
import jo.util.utils.ZipUtils;
import jo.util.utils.io.FileUtils;

public class ExternalizeStrings
{
    private String[] mArgs;
    
    private File mModelInput;
    private File mSourceDir;
    private String mBaseName;
    private boolean mReadOnly;
    private boolean mVerbose;
    
    private InteractionModelBean    mModel;
    
    public ExternalizeStrings(String[] args)
    {
        mArgs = args;
        mReadOnly = true;
        mVerbose = false;
    }
    
    public void run()
    {
        parseArgs();
        try
        {
            readModel();
            archiveSource();
            boolean anyChange = scanSourceDir(mSourceDir);
            if (anyChange)
                if (!mReadOnly)
                    writeModel();
                else if (mVerbose)
                    System.out.println(mModel.getRawModel().toJSONString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private boolean scanSourceDir(File dir) throws IOException
    {
        File[] files = dir.listFiles();
        if (files == null)
            return false;
        boolean anyChange = false;
        for (File file : files)
            if (file.isDirectory())
                anyChange |= scanSourceDir(file);
            else if (file.getName().endsWith(".java"))
                anyChange |= scanSourceFile(file);
        return anyChange;
    }
    
    private boolean scanSourceFile(File java) throws IOException
    {
        String inbuf = FileUtils.readFileAsString(java.toString());
        String outbuf = inbuf;
        outbuf = extractText(java, outbuf, "respond");
        outbuf = extractText(java, outbuf, "card");
        outbuf = extractText(java, outbuf, "speak");
        outbuf = extractText(java, outbuf, "reprompt");
        if (inbuf.equals(outbuf))
            return false;
        if (!mReadOnly)
            FileUtils.writeFile(outbuf, java);
        else if (mVerbose)
            System.out.println(outbuf);
        return true;
    }

    private String extractText(File java, String outbuf, String func)
    {
        int o = 0;
        for (;;)
        {
            int s = outbuf.indexOf("."+func+"(", o);
            if (s < 0)
                break;
            s += 2 + func.length();
            String line = extractLine(outbuf, s);
            if (line.indexOf("$NON-NLS-1$") >= 0)
            {
                o = s;
                continue;
            }
            int e = s;
            if (outbuf.charAt(e) != '\"')
            {
                if (outbuf.substring(e).startsWith(mBaseName+"ModelConst"))
                    continue;
                lineError(java, outbuf, s, "Unexpected argument for respond");
                o = e;
                continue;
            }
            while (++e < outbuf.length())
            {
                int ch = outbuf.charAt(e);
                if (ch == '\\')
                    e++;
                else if (ch == '\"')
                    break;
            }
            if (e == outbuf.length())
                break;
            e++;
            char term = outbuf.charAt(e);
            if ((term != ',') && (term != ')'))
            {
                lineError(java, outbuf, e, "Expected standalone string, not a calculation");
                o = e;
                continue;
            }
            String text = outbuf.substring(s+1, e-1);
            String name = makeConst(text, false);
            addText(text);
            String sub = mBaseName+"ModelConst.TEXT_"+name;
            outbuf = outbuf.substring(0, s) + sub + outbuf.substring(e);
            o = s + sub.length();
            o = e;
        }
        return outbuf;
    }
    
    @SuppressWarnings("unchecked")
    private void addText(String key)
    {
        JSONObject enus = JSONUtils.getObject(mModel.getRawModel(), "text.en_US");
        if (enus == null)
        {
            enus = new JSONObject();
            JSONUtils.getObject(mModel.getRawModel(), "text").put("en_US", enus);
        }
        JSONArray words = JSONUtils.getArray(enus, key);
        if (words == null)
        {
            words = new JSONArray();
            enus.put(key, words);
        }
        words.add(key);
    }
    
    public static String makeConst(String text, boolean numbersOK)
    {
        StringBuffer sb = new StringBuffer();
        for (char ch : text.toCharArray())
            if (Character.isAlphabetic(ch))
                sb.append(Character.toUpperCase(ch));
            else if (Character.isDigit(ch) && ((sb.length() > 0) || numbersOK))
                sb.append(ch);
            else
                sb.append("_");
        return sb.toString();
    }

    private String extractLine(String inbuf, int o)
    {
        int s = inbuf.lastIndexOf('\n', o);
        int e = inbuf.indexOf('\n', o);
        return inbuf.substring(s+1, e-1);
    }
    
    private void lineError(File java, String inbuf, int o, String err)
    {
        int s = inbuf.lastIndexOf('\n', o);
        int e = inbuf.indexOf('\n', o);
        System.out.println(java.getName()+": "+inbuf.substring(s+1, e-1));
        o -= s;
        o += java.getName().length() + 2;
        for (int i = 0; i < o; i++)
            System.out.print(" ");
        System.out.println("^ "+err);
    }
    
    private void writeModel() throws IOException
    {
        JSONUtils.writeJSON(mModelInput, mModel.getRawModel());        
    }
    
    private void archiveSource() throws IOException
    {
        File zipFile = File.createTempFile("voiceSourceBackup", ".zip");
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipUtils.zip(mSourceDir, fos);
        fos.close();
        System.out.println("Source backed up to "+zipFile.getAbsolutePath());
    }

    private void readModel() throws IOException
    {
        mModel = ParseModelLogic.parse(mModelInput.toURI().toString());
        String base = mModelInput.getName();
        int o = base.lastIndexOf('.');
        mBaseName = base.substring(0, o);
    }
    
    private void parseArgs()
    {
        for (int i = 0; i < mArgs.length; i++)
            if ("-model".equalsIgnoreCase(mArgs[i]) || "-m".equalsIgnoreCase(mArgs[i]))
                mModelInput = new File(mArgs[++i]);
            else if ("-source".equalsIgnoreCase(mArgs[i]) || "-s".equalsIgnoreCase(mArgs[i]))
                mSourceDir = new File(mArgs[++i]);
            else if ("-rw".equalsIgnoreCase(mArgs[i]))
                mReadOnly = false;
            else if ("-ro".equalsIgnoreCase(mArgs[i]))
                mReadOnly = true;
        if (mModelInput == null)
        {
            System.err.println("Specify model input file with -m");
            System.exit(1);
        }
        if (mSourceDir == null)
            mSourceDir = mModelInput.getParentFile().getParentFile();
    }
    
    public static void main(String[] argv)
    {
        ExternalizeStrings app = new ExternalizeStrings(argv);
        app.run();
    }
}
