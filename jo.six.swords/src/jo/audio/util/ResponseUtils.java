package jo.audio.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import jo.audio.util.model.data.AudioMessageBean;
import jo.audio.util.model.data.AudioResponseBean;

public class ResponseUtils
{
    private static String parseInsert(String cmd, String arg, boolean forCard)
    {
        switch (cmd)
        {
            case "forCard":
            case "card":
                if (forCard)
                    return arg;
                else
                    return "";
            case "speech":
            case "sp":
            case "forSpeech":
                if (!forCard)
                    return arg;
                else
                    return "";
            case "ph":
            case "phoneme":
            {
                String[] args = arg.split(":");
                if (args.length != 2)
                    throw new IllegalArgumentException("Expected two arguments for '"+cmd+"', not '"+args+"'");
                if (forCard)
                    return args[0];
                else
                    return "<phoneme alphabet=\"ipa\" ph=\""+args[1]+"\">"+args[0]+"</phoneme>";
            }
            default:
                throw new IllegalArgumentException("Unknown parse command '"+cmd+"("+arg+")");
        }
    }
    
    private static String parseInserts(String inbuf, boolean forCard)
    {
        StringBuffer outbuf = new StringBuffer();
        for (;;)
        {
            int o = inbuf.indexOf("<<");
            if (o < 0)
            {
                outbuf.append(inbuf);
                break;
            }
            outbuf.append(inbuf.substring(0, o));
            inbuf = inbuf.substring(o + 2);
            o = inbuf.indexOf(':');
            if (o < 0)
                throw new IllegalStateException("Expected ':', got '"+inbuf+"'");
            String cmd = inbuf.substring(0, o);
            inbuf = inbuf.substring(o + 1);
            o = inbuf.indexOf(">>");
            if (o < 0)
                throw new IllegalStateException("Expected '>>', got '"+inbuf+"'");
            String arg = inbuf.substring(0, o);
            inbuf = inbuf.substring(o + 2);
            outbuf.append(parseInsert(cmd, arg, forCard));
        }
        return outbuf.toString();
    }
    
    private static String toOutputSpeech(String txt)
    {
        txt = parseInserts(txt, false);
        if (txt.indexOf('<') < 0)
            return txt;
        else
        {
            txt = txt.trim();
            if (!txt.startsWith("<speak"))
                txt = "<speak>"+txt;
            if (!txt.endsWith("</speak>"))
                txt = txt+"</speak>";
            return txt;
        }
    }
    
    public static List<String> expand(String base, String[] inserts)
    {
        List<String> alts = new ArrayList<String>();
        for (String insert : inserts)
            alts.add(base.replace("[[insert]]", insert));
        return alts;
    }
    
    public static String expandCallsign(String callsign)
    {
        if (callsign == null)
            return "null";
        return "<say-as interpret-as=\"spell-out\">"+callsign.toUpperCase()+"</say-as>";
    }
    
    public static String expandCallsign(char cs1, char cs2, char cs3)
    {
        return "<say-as interpret-as=\"spell-out\">"+String.valueOf(cs1)+String.valueOf(cs2)+String.valueOf(cs3)+"</say-as>";
    }
    
    public static String callsignList(String[] callsigns)
    {
        List<String> active = new ArrayList<>();
        for (int i = 0; i < Math.min(callsigns.length, 5); i++)
            active.add(expandCallsign(callsigns[i]));
        return ResponseUtils.wordList(active);
    }
    
    public static String wordList(Collection<String> words)
    {
        return wordList(words.toArray(new String[0]), -1, true);
    }
    
    public static String wordListOR(Collection<String> words)
    {
        return wordList(words.toArray(), -1, false);
    }
    
    public static String wordListOR(Object... words)
    {
        return wordList(words, -1, false);
    }
    
    public static String wordList(Object[] words, int limit)
    {
        return wordList(words, limit, true);
    }
    
    public static String wordList(Object[] words, int limit, boolean and)
    {
        StringBuffer resp = new StringBuffer();
        if (limit < 0)
            limit = words.length;
        if (limit > words.length)
            limit = words.length;
        for (int i = 0; i < limit; i++)
        {
            if (i > 0)
            {
                if (i < limit - 1)
                    resp.append(",");
                resp.append(" ");
                if (i == limit - 1)
                    if (and)
                        resp.append("and ");
                    else
                        resp.append("or ");
            }
            resp.append(words[i].toString());
        }
        return resp.toString();
    }
    public static AudioMessageBean wordListMessage(Collection<String> words)
    {
        return wordListMessage(words.toArray(new String[0]), -1, true);
    }
    
    public static AudioMessageBean wordListORMessage(Collection<String> words)
    {
        return wordListMessage(words.toArray(), -1, false);
    }
    
    public static AudioMessageBean wordListORMessage(Object... words)
    {
        return wordListMessage(words, -1, false);
    }
    
    public static AudioMessageBean wordListMessage(Object[] words, int limit)
    {
        return wordListMessage(words, limit, true);
    }
    
    public static AudioMessageBean wordListMessage(Object[] words, int limit, boolean and)
    {
        if (limit < 0)
            limit = words.length;
        Object[] args = new Object[limit];
        for (int i = 0; i < limit; i++)
            args[i] = words[i].toString();
        if (and)
            return new AudioMessageBean(AudioMessageBean.AND, args);
        else
            return new AudioMessageBean(AudioMessageBean.OR, args);
    }

    public static AudioResponseBean buildSpeechletResponse(Throwable e) {
        AudioResponseBean response = new AudioResponseBean();
        response.setCardTitle(e.getLocalizedMessage());
        String cardText = toCardText(e);
        response.setCardContent(cardText);
        response.setShouldEndSession(true);
        response.setOutputSpeechText(toOutputSpeech(e));
        return response;
    }
    
    private static String toCardText(Throwable e)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(e.toString()+":\n");
        int max = Math.min(20, e.getStackTrace().length);
        for (int i = 0; i < max; i++)
        {
            StackTraceElement ste = e.getStackTrace()[i];
            sb.append("  "+ste+"\n");
        }
        if (e.getCause() != null)
        {
            sb.append("Caused by ");
            sb.append(toCardText(e.getCause()));
        }
        return sb.toString();
    }
    private static String toOutputSpeech(Throwable e)
    {
        return e.toString();
    }

    public static int countList(String list)
    {
        if (list == null)
            return 0;
        StringTokenizer st = new StringTokenizer(list, "{}");
        return st.countTokens();
    }

    public static String addToList(String list, String name)
    {
        String tag = "{"+name+"}";
        if (list == null)
            list = tag;
        else if (list.indexOf(tag) < 0)
            list += tag;
        return list;
    }

    public static boolean isInList(String list, String name)
    {
        String tag = "{"+name+"}";
        if (list == null)
            return false;
        else if (list.indexOf(tag) >= 0)
            return true;
        return false;
    }

    public static AudioResponseBean buildLinkRequestSpeechletResponse()
    {
        AudioResponseBean response = new AudioResponseBean();
        response.setShouldEndSession(true);
        response.setRequestAuthentication(true);
        response.setOutputSpeechText(toOutputSpeech("Please use the companion app to authenticate and start using this skill"));
        return response;
    }

    public static String elapsedTime(long elapsed)
    {
        if (elapsed < 2*60*1000L)
            return (elapsed/1000L)+" seconds";
        if (elapsed < 2*60*60*1000L)
            return (elapsed/(60*1000L))+" minutes";
        if (elapsed < 2*24*60*60*1000L)
            return (elapsed/(60*60*1000L))+" hours";
        //if (elapsed < 31*24*60*60*1000L)
            return (elapsed/(24*60*60*1000L))+" days";
    }
}
