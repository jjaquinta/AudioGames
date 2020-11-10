package jo.ipa.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import jo.util.utils.io.ResourceUtils;
import jo.util.utils.obj.BooleanUtils;

public class CMULogic
{
    public static boolean    mExternalLookup = false;
    private static Map<String,String[]> mCache = null;
    private static boolean mPrintUnknowns = BooleanUtils.parseBoolean(System.getProperty("cmu.map.debug"));
    
    private static void loadCache() throws IOException
    {
        if (mCache != null)
            return;
        mCache = new HashMap<>();
        BufferedReader rdr = new BufferedReader(new InputStreamReader(ResourceUtils.loadSystemResourceStream("cmudict-0.7b", CMULogic.class)));
        for (;;)
        {
            String inbuf = rdr.readLine();
            if (inbuf == null)
                break;
            if (inbuf.startsWith(";"))
                continue;
            if (inbuf.length() == 0)
                continue;
            int o = inbuf.indexOf("  ");
            if (o < 0)
            {
                System.err.println("Can't find split in '"+inbuf+"'");
                continue;
            }
            String word = inbuf.substring(0, o).toLowerCase();
            String pron = inbuf.substring(o + 2);
            o = word.indexOf('(');
            if (o > 0)
                word = word.substring(0, o);
            StringBuffer ipa = new StringBuffer();
            for (StringTokenizer st = new StringTokenizer(pron, " "); st.hasMoreTokens(); )
            {
                String arpa = st.nextToken();
                if (Character.isDigit(arpa.charAt(arpa.length() - 1))) // remove stress
                    arpa = arpa.substring(0, arpa.length() - 1);
                switch (arpa)
                {
                    case "AA":
                        ipa.append("ɑ");
                        break;
                    case "AE":
                        ipa.append("æ");
                        break;
                    case "AH":
                        ipa.append("ʌ");
                        break;
                    case "AO":
                        ipa.append("ɔ");
                        break;
                    case "AW":
                        ipa.append("aʊ");
                        break;
                    case "AY":
                        ipa.append("aɪ");
                        break;
                    case "EH":
                        ipa.append("ɛ");
                        break;
                    case "ER":
                        ipa.append("ɝ");
                        break;
                    case "EY":
                        ipa.append("eɪ");
                        break;
                    case "IH":
                        ipa.append("ɪ");
                        break;
                    case "IY":
                        ipa.append("i");
                        break;
                    case "OW":
                        ipa.append("oʊ");
                        break;
                    case "OY":
                        ipa.append("ɔɪ");
                        break;
                    case "UH":
                        ipa.append("ʊ");
                        break;
                    case "UW":
                        ipa.append("u");
                        break;
                    case "B":
                        ipa.append("b");
                        break;
                    case "CH":
                        ipa.append("tʃ");
                        break;
                    case "D":
                        ipa.append("d");
                        break;
                    case "DH":
                        ipa.append("ð");
                        break;
                    case "F":
                        ipa.append("f");
                        break;
                    case "G":
                        ipa.append("g");
                        break;
                    case "JH":
                        ipa.append("dʒ");
                        break;
                    case "K":
                        ipa.append("k");
                        break;
                    case "HH":
                        ipa.append("h");
                        break;
                    case "L":
                        ipa.append("l");
                        break;
                    case "M":
                        ipa.append("m̩");
                        break;
                    case "N":
                        ipa.append("n");
                        break;
                    case "NG":
                        ipa.append("ŋ");
                        break;
                    case "P":
                        ipa.append("p");
                        break;
                    case "R":
                        ipa.append("ɹ");
                        break;
                    case "S":
                        ipa.append("s");
                        break;
                    case "SH":
                        ipa.append("ʃ");
                        break;
                    case "T":
                        ipa.append("t");
                        break;
                    case "TH":
                        ipa.append("θ");
                        break;
                    case "V":
                        ipa.append("v");
                        break;
                    case "W":
                        ipa.append("w");
                        break;
                    case "Y":
                        ipa.append("j");
                        break;
                    case "Z":
                        ipa.append("z");
                        break;
                    case "ZH":
                        ipa.append("ʒ");
                        break;
                    default:
                        System.err.println("Unknown ARPA: '"+arpa+"'");
                        System.exit(1);
                        break;
                }
            }
            addWordToCache(word, ipa.toString());
        }
        rdr.close();
        
        InputStream fis = null;
        try
        {
            fis = ResourceUtils.loadSystemResourceStream("cmu_extras.properties", CMULogic.class);
            if (fis != null)
            {
                Properties p = new Properties();
                p.load(fis);
                fis.close();
                for (Object word : p.keySet())
                {
                    String ipa = (String)p.get(word);
                    for (StringTokenizer st = new StringTokenizer(ipa, ","); st.hasMoreTokens(); )
                            addWordToCache((String)word, st.nextToken());
                }
            }
        }
        catch (IOException e)
        {
        }
        
        System.out.println(mCache.size()+" words");
    }

    private static void addWordToCache(String word, String ipa)
    {
        String[] words = mCache.get(word);
        if (words == null)
            words = new String[1];
        else
        {
            String[] w = new String[words.length + 1];
            System.arraycopy(words, 0, w, 0, words.length);
            words = w;
        }
        words[words.length - 1] = ipa;
        mCache.put(word, words);
    }
    
    public static String[] lookupAll(String word)
    {
        word = word.toLowerCase();
        if (word.endsWith("."))
            word = word.substring(0, word.length() - 1);
        boolean posessive = false;
        if (word.endsWith("'s"))
        {
            posessive = true;
            word = word.substring(0, word.length() - 2);
        }
        if (mCache == null)
            try
            {
                loadCache();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        String[] ipas = mCache.get(word);
        if (ipas == null)
            return null;
        if (posessive && (ipas != null))
            for (int i = 0; i < ipas.length; i++)
                ipas[i] += "s";
        return ipas;
    }
    
    public static String lookup(String word)
    {
        String[] ipas = lookupAll(word);
        if (ipas == null)
            return null;
        return ipas[0];
    }
    
    public static IPAWord toIPA(String word)
    {
        IPAWord ret = null;
        for (StringTokenizer st = new StringTokenizer(word, " -!?()"); st.hasMoreTokens(); )
        {
            String w = st.nextToken();
            String[] i = lookupAll(w);
            IPAWord ww = new IPAWord();
            if (i == null)
            {
                if (mPrintUnknowns)
                    System.out.println("Cannot find '"+w+"'");
                i = new String[] { w };
                ww.setSynthIPA(true);
            }
            ww.setWord(w);
            ww.setIPA(i[0]);
            ww.setAllVariants(i);
            if (ret == null)
                ret = ww;
            else
                ret.append(ww);
        }
        if (ret != null)
            ret.setSource(word);
        return ret;
    }
    
    public static void main(String[] argv) throws IOException
    {
        String ipa = lookup("to");
        System.out.println(ipa);
    }
}
