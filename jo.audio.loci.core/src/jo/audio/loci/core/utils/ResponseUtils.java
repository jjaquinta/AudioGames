package jo.audio.loci.core.utils;

import java.util.Collection;
import java.util.function.BiFunction;

public class ResponseUtils
{
    
    public static String wordList(Collection<String> words)
    {
        return wordList(words.toArray(new String[0]), -1, "and ");
    }
    
    public static String wordListOR(Collection<String> words)
    {
        return wordList(words.toArray(), -1, "or ");
    }
    
    public static String wordListOR(Object... words)
    {
        return wordList(words, -1, "or ");
    }
    
    public static String wordList(Object[] words, int limit)
    {
        return wordList(words, limit, "and ");
    }
    
    public static String wordList(Object[] words, int limit, String and)
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
                    resp.append(and);
            }
            resp.append(words[i].toString());
        }
        return resp.toString();
    }

    public static <T> String filterText(String inbuf, T payload, BiFunction<T, String, String> proc)
    {
        StringBuffer outbuf = new StringBuffer();
        for (;;)
        {
            int o = inbuf.indexOf("[[");
            if (o < 0)
            {
                outbuf.append(inbuf);
                break;
            }
            outbuf.append(inbuf.substring(0, o));
            inbuf = inbuf.substring(o + 2);
            o = inbuf.indexOf("]]");
            if (o < 0)
            {
                outbuf.append("[[");
                outbuf.append(inbuf);
                break;
            }
            String arg = inbuf.substring(0, o);
            inbuf = inbuf.substring(o + 2);
            String ret = proc.apply(payload, arg);
            if (ret != null)
                outbuf.append(ret);
        }
        return outbuf.toString();
    }
}
