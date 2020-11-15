package jo.audio.util.model.cmd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.audio.util.model.data.IntentDefBean;
import jo.audio.util.model.data.InteractionModelBean;
import jo.audio.util.model.data.SlotBean;
import jo.audio.util.model.data.UtteranceBean;
import jo.audio.util.model.logic.ParseModelLogic;

public class GenerateAudioModel
{
    private String[] mArgs;
    
    private File mModelInput;
    private File mModelOutputDir;
    private boolean mNumbersOK = false;
    
    private InteractionModelBean    mModel;
    private String mBaseName;
    
    public GenerateAudioModel(String[] args)
    {
        mArgs = args;
    }
    
    public void run()
    {
        parseArgs();
        readModel();
        try
        {
            writeJavaConstants();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void readModel()
    {
        try
        {
            mModel = ParseModelLogic.parse(mModelInput.toURI().toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
        String base = mModelInput.getName();
        int o = base.lastIndexOf('.');
        mBaseName = base.substring(0, o);
    }

    private void writeJavaConstants() throws IOException
    {
        StringBuffer sb = null;
        for (StringTokenizer st = new StringTokenizer(mModelOutputDir.toString(), "\\/"); st.hasMoreTokens(); )
        {
            String dir = st.nextToken();
            if (dir.equals("src"))
                sb = new StringBuffer();
            else if (sb != null)
            {
                if (sb.length() > 0)
                    sb.append(".");
                sb.append(dir);
            }
        }
        File intentFile = new File(mModelOutputDir, mBaseName+"ModelConst.java");
        BufferedWriter wtr = new BufferedWriter(new FileWriter(intentFile));
        wtr.write("package "+sb+";");wtr.newLine();
        wtr.write("");wtr.newLine();
        wtr.write("public class "+mBaseName+"ModelConst");wtr.newLine();
        wtr.write("{");wtr.newLine();
        wtr.write("\t// intent constants");wtr.newLine();
        for (String intentName : mModel.getIntents().keySet())
        {
            String constName = intentName.toUpperCase();
            if (constName.startsWith("AUDIOPLAYER."))
                constName = constName.substring(12);
            if (constName.startsWith("PLAYBACKCONTROLLER."))
                constName = constName.substring(19);
            if (constName.startsWith("SYSTEM."))
                constName = constName.substring(7);
            if (constName.endsWith("INTENT"))
                constName = constName.substring(0, constName.length() - 6);
            wtr.write("\tpublic static final String INTENT_"+constName+" = \""+intentName+"\";");wtr.newLine();
        }
        wtr.write("\t// text constants");wtr.newLine();
        String[] textKeys = mModel.getText().get("en_US").keySet().toArray(new String[0]);
        Arrays.sort(textKeys);
        Set<String> done = new HashSet<>();
        for (String keyName : textKeys)
        {
            String constName = ExternalizeStrings.makeConst(keyName, mNumbersOK);
            if (done.contains(constName))
            {
                for (int i = 1; i < 99; i++)
                {
                    String txt = constName+i;
                    if (!done.contains(txt))
                    {
                        constName = txt;
                        break;
                    }
                }
            }
            wtr.write("\tpublic static final String TEXT_"+constName+" = \""+toQuotedString(keyName)+"\";");wtr.newLine();
            done.add(constName);
        }
        wtr.write("}");wtr.newLine();

        wtr.close();
    }
    
    private String toQuotedString(String txt)
    {
        StringBuffer sb = new StringBuffer();
        for (char c : txt.toCharArray())
        {
            if ((c == '\"') || (c == '\\'))
                sb.append("\\");
            sb.append(c);
        }
            
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public void addCombinedTypes(String lang, JSONObject lm)
    {
        if (mModel.getCustomSlots().size() > 0)
        {
            JSONArray types = new JSONArray();
            lm.put("types", types);
            for (String table : mModel.getCustomSlots())
            {
                if (table.toUpperCase().startsWith("AMAZON."))
                    continue;
                if (table.equalsIgnoreCase("ANYTHING"))
                    continue;
                Map<String, List<String>> dict = mModel.getDictionaries().get(table).get(lang);
                if (dict == null)
                {
                    dict = mModel.getDictionaries().get(table).get(lang.substring(0, 2));
                    if (dict == null)
                    {
                        dict = mModel.getDictionaries().get(table).get("en_US");
                    }
                }
                if (dict.size() == 0)
                    continue;
                JSONObject type = new JSONObject();
                types.add(type);
                type.put("name", table.toUpperCase());
                JSONArray values = new JSONArray();
                type.put("values", values);
                for (String primaryWord : dict.keySet())
                {
                    JSONObject value = new JSONObject();
                    values.add(value);
                    JSONObject name = new JSONObject();
                    value.put("name", name);
                    name.put("value", primaryWord);
                    JSONArray synonyms = new JSONArray();
                    for (String word : dict.get(primaryWord))
                        if (!word.equalsIgnoreCase(primaryWord))
                            synonyms.add(word);
                    if (synonyms.size() > 0)
                        value.put("synonyms", synonyms);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void addCombinedIntents(String lang, JSONObject lm)
    {
        JSONArray intents = new JSONArray();
        lm.put("intents", intents);
        for (String intentName : mModel.getIntents().keySet())
        {
            if (intentName.toLowerCase().startsWith("audioplayer."))
                continue;
            IntentDefBean i = mModel.getIntent(intentName);
            if ((i.getTarget() != null) && (i.getTarget().indexOf("alexa") < 0))
                continue;
            if ((i.getTargetLang() != null) && (i.getTargetLang().indexOf(lang) < 0))
                continue;
            JSONObject intent = new JSONObject();
            intents.add(intent);
            if (i.getIntent().endsWith("Intent") && !i.getIntent().startsWith("AMAZON."))
                intent.put("name", "AMAZON."+i.getIntent());
            else if (i.getIntent().startsWith("AMAZON."))
                intent.put("name", i.getIntent());
            else
                intent.put("name", intentName.toUpperCase());
            if (i.getSlots().size() > 0)
            {
                JSONArray slots = new JSONArray();
                intent.put("slots", slots);
                for (SlotBean s : i.getSlots())
                {
                    JSONObject slot = new JSONObject();
                    slots.add(slot);
                    String slotName = s.getName();
                    String type = s.getType();
                    slot.put("name", slotName);
                    if ("CUSTOM".equals(type))
                    {
                        String table = s.getDictionary();
                        if ("anything".equalsIgnoreCase(table))
                            slot.put("type", "AMAZON.SearchQuery");
                        else
                            slot.put("type", table.toUpperCase());
                    }
                    else if ("US_FIRST_NAME".equals(type))
                        slot.put("type", "AMAZON.US_FIRST_NAME");
                    else if ("FirstName".equals(type))
                    {
                        if ("en_US".equals(lang))
                            slot.put("type", "AMAZON.US_FIRST_NAME");
                        else if ("en_UK".equals(lang))
                            slot.put("type", "AMAZON.GB_FIRST_NAME");
                        else if ("de_DE".equals(lang))
                            slot.put("type", "AMAZON.DE_FIRST_NAME");
                        else
                            slot.put("type", "AMAZON.FirstName");
                    }
                    else if ("FirstName".equals(type))
                    {
                        slot.put("type", "AMAZON.FirstName");
                    }
                    else
                        slot.put("type", type);
                }
            }
            List<UtteranceBean> list = i.getUtterances().get(lang);
            if (list == null)
            {
                list = i.getUtterances().get(lang.substring(0, 2));
                if (list == null)
                {
                    list = i.getUtterances().get("en_US");
                }
            }
            if ((list != null) && !i.getIntent().startsWith("AMAZON."))
            {
                JSONArray samples = new JSONArray();
                intent.put("samples", samples);
                for (UtteranceBean utterance : list)
                {
                    if (utterance.getTags().containsKey("only") && !"alexa".equalsIgnoreCase(utterance.getTags().getProperty("only")))
                        continue;
                    String raw = utterance.getRawUtterance();
                    int o = raw.indexOf('[');
                    if (o >= 0)
                        raw = raw.substring(0, o);
                    samples.add(raw);
                }
            }
        }
    }
    
    private void parseArgs()
    {
        for (int i = 0; i < mArgs.length; i++)
            if ("-model".equalsIgnoreCase(mArgs[i]) || "-m".equalsIgnoreCase(mArgs[i]))
                mModelInput = new File(mArgs[++i]);
            else if ("-output".equalsIgnoreCase(mArgs[i]) || "-o".equalsIgnoreCase(mArgs[i]))
                mModelOutputDir = new File(mArgs[++i]);
            else if ("-numbersOk".equalsIgnoreCase(mArgs[i]))
                mNumbersOK = true;
        if (mModelInput == null)
        {
            System.err.println("Specify model input file with -m");
            System.exit(1);
        }
        if (mModelOutputDir == null)
            mModelOutputDir = mModelInput.getParentFile();
    }
    
    public static void main(String[] argv)
    {
        GenerateAudioModel app = new GenerateAudioModel(argv);
        app.run();
    }
}
