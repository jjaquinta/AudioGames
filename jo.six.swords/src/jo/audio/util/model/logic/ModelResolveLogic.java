package jo.audio.util.model.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import jo.audio.common.data.I8nString;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;
import jo.audio.util.model.data.InteractionModelBean;
import jo.util.utils.BeanUtils;
import jo.util.utils.DebugUtils;

public class ModelResolveLogic
{
    public static final String TEST_NORANDOM = "jo.audio.utils.test.resolve.norandom";
    private static final boolean debug = false;
    
    public static String resolve(InteractionModelBean model, String lang, Random rnd, Object context, String key, Object... args)
    {
        if (debug)
        {
            DebugUtils.trace("ModelResolveLogic.resolve: Resolving '"+key+"'");
            if (args != null)
                for (int i = 0; i < args.length; i++)
                    DebugUtils.trace(" arg"+(i+1)+"='"+args[i]+"'");
        }
        if (args != null)
            for (int i = 0; i < args.length; i++)
                if (args[i] instanceof AudioMessageBean)
                {
                    AudioMessageBean msg = (AudioMessageBean)args[i];
                    args[i] = resolve(model, lang, rnd, context, msg.getIdent(), msg.getArgs());
                    if (debug) DebugUtils.trace(" arg"+(i+1)+"->'"+args[i]+"'");
                }
        if (AudioMessageBean.AND.equals(key))
            return ResponseUtils.wordList(args, -1);
        else if (AudioMessageBean.OR.equals(key))
            return ResponseUtils.wordListOR(args);
        else if (AudioMessageBean.RAW.equals(key))
        {
            StringBuffer ret = new StringBuffer();
            for (int i = 0; i < args.length; i++)
                ret.append(args[i]);
            return ret.toString();
        }
        else if (AudioMessageBean.GROUP.equals(key))
        {
            StringBuffer ret = new StringBuffer();
            for (int i = 0; i < args.length; i++)
                ret.append(expandInserts(model, lang, rnd, context, args[i].toString()));
            return ret.toString();
        }

        int idx = -1;
        int o = key.indexOf("#");
        if (o > 0)
        {
            idx = Integer.parseInt(key.substring(o + 1));
            key = key.substring(0, o);
            if (debug) DebugUtils.trace("  key is a lookup with key='"+key+"', off="+idx);
        }
        List<String> choices = model.getText(lang, key);
        String format;
        if ((choices != null) && (choices.size() > 0))
        {
            if (debug) DebugUtils.trace("  key '"+key+"' has "+choices.size()+" choices");
            if (choices.size() == 1)
            {
                format = choices.get(0);
                if (debug) DebugUtils.trace("  only one, so picking '"+format+"'");
            }
            else if (idx < 0)
            {
                if (System.getProperty(TEST_NORANDOM) != null)
                    idx = 0;
                else
                    idx = rnd.nextInt(choices.size());
                format = choices.get(idx);
                if (debug) DebugUtils.trace("  randomly picking #"+idx+"='"+format+"'");
            }
            else
            {
                format = choices.get(idx%choices.size());
                if (debug) DebugUtils.trace("  index picking #"+idx+"='"+format+"'");
            }
        }
        else
        {
            DebugUtils.error("Failed to look up key '%s' with language '%s'", key, lang);
            if (debug) 
            {
                DebugUtils.trace("  failed to look up key '"+key+"' with language '"+lang+"'");
                try
                {
                    for (String k : model.getText().get(lang).keySet())
                        DebugUtils.trace("    '"+k+"'");
                }
                catch (NullPointerException e)
                {                    
                }
            }
            format = key;
        }
        if (debug) DebugUtils.trace("  key resolves to '"+format+"'");
        for (int i = 0; i < args.length; i++)
        {
            if (args[i] instanceof I8nString)
            {
                if (debug) DebugUtils.trace(" Resolving arg as I8nValue");
                args[i] = ((I8nString)args[i]).getValue(lang);
            }
            if (debug) DebugUtils.trace(" arg"+(i+1)+" resolves to '"+args[i]+"'");
        }
        String txt = "";
        try
        {
            txt = String.format(format, args);
        }
        catch (Exception e)
        {
            DebugUtils.error("Error attempting to format '"+format+"'");
            for (int i = 0; i < args.length; i++)
                DebugUtils.error(" arg"+(i+1)+"='"+args[i]+"'");
            DebugUtils.error("Error", e);
            txt = e.getMessage();
        }
        if (debug) DebugUtils.trace(" before pre-processing '"+txt+"'");
        txt = expandInserts(model, lang, rnd, context, txt);
        if (debug) DebugUtils.trace("ModelResolveLogic.resolve: final formatted string '"+txt+"'");
        return txt;
    }
    
    public static String expandInserts(InteractionModelBean model, String lang, Random rnd, Object context, String txt)
    {
        for (;;)
        {
            if (debug) DebugUtils.trace("ModelResolveLogic.expandInserts, looping with '"+txt+"'");
            int end = txt.indexOf("}}");
            if (end < 0)
            {
                if (debug) DebugUtils.trace("expandInserts, no expansions, done.");
                return txt;
            }
            int start = txt.lastIndexOf("{{", end);
            if (start < 0)
            {
                DebugUtils.warn("Warning, unbalanced {{ }} in '"+txt+"'");
                return txt;
            }
            String prefix = txt.substring(0, start);
            String middle = txt.substring(start + 2, end);
            String suffix = txt.substring(end + 2);
            String newMiddle;
            if (middle.startsWith("$") && middle.endsWith("$"))
            {
                middle = middle.substring(1, middle.length() - 1);
                if (debug) DebugUtils.trace("expandInserts, expression='"+middle+"'");
                Object insert = BeanUtils.get(context, middle);
                if (debug) DebugUtils.trace("expandInserts, insert='"+insert+"'");
                if (insert instanceof AudioMessageBean)
                {
                    AudioMessageBean msg = (AudioMessageBean)insert;
                    insert = resolve(model, lang, rnd, context, msg.getIdent(), msg.getArgs());
                }
                newMiddle = String.valueOf(insert);
                if (model.isText(lang, newMiddle))
                    newMiddle = resolve(model, lang, rnd, context, newMiddle);
                if (debug) DebugUtils.trace("expandInserts, newMiddle='"+newMiddle+"'");
            }
            else
            {
                List<Object> args = new ArrayList<>();
                int aStart = middle.indexOf('(');
                if (aStart >= 0)
                {
                    int aEnd = middle.lastIndexOf(')');
                    if (aEnd >= 0)
                    {
                        for (StringTokenizer st = new StringTokenizer(middle.substring(aStart + 1, aEnd), ","); st.hasMoreTokens(); )
                            args.add(st.nextToken());
                        middle = middle.substring(0, start);
                    }
                }
                if (debug) DebugUtils.trace("expandInserts, prefix='"+prefix+"', suffix='"+suffix+"'");
                newMiddle = resolve(model, lang, rnd, context, middle, args.toArray());
            }
            if (debug) DebugUtils.trace("expandInserts, expanding middle '"+middle+"' to '"+newMiddle+"'");
            txt = prefix + newMiddle + suffix;
        }
    }
}
