package jo.audio.loci.core.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class ExecuteLogic
{
    public static ExecuteContext execute(LociBase invoker, String command)
    {
        ExecuteContext context = new ExecuteContext();
        context.setInvoker(invoker);
        context.setCommand(command);
        populateVisible(context);
        if (findVerb(context))
        {
            context.getMatchedVerb().execute(context);
            context.setSuccess(true);
        }
        return context;
    }
    
    private static boolean findVerb(ExecuteContext context)
    {
        List<ExecuteContext> matches = findVerbs(context);
        if (matches.size() == 0)
            return false;
        ExecuteContext match = findBestMatch(matches);
        context.set(match);
        return true;
    }
    
    private static ExecuteContext findBestMatch(List<ExecuteContext> matches)
    {
        if (matches.size() == 1)
            return matches.get(0);
        // preference verb on invoker
        for (ExecuteContext match : matches)
            if (match.getInvoker().getURI().equals(match.getMatchedVerbHost().getURI()))
                return match;
        // preference with most matches
        ExecuteContext best = null;
        int bestv = 0;
        for (ExecuteContext match : matches)
        {
            int score = (match.getMatchedVerb() != null ? 1 : 0)
                    + (match.getMatchedDirectObject() != null ? 1 : 0)
                    + (match.getMatchedIndirectObject() != null ? 1 : 0);
            if ((best == null) || (score > bestv))
            {
                best = match;
                bestv = score;
            }
        }
        if (bestv > 0)
            return best;
        return matches.get(0);
    }
    
    private static List<ExecuteContext> findVerbs(ExecuteContext context)
    {
        List<ExecuteContext> matches = new ArrayList<>();
        DebugUtils.trace("Finding verbs for "+context.getCommand());
        for (LociObject obj : context.getVisibleTo())
        {
            DebugUtils.trace("Searching verbs on "+obj+", "+obj.getVerbProfile());
            List<Verb> verbs = VerbProfileLogic.getVerbs(obj.getVerbProfile());
            for (Verb verb : verbs)
                if (isVerbFor(context, obj, verb))
                {
                    ExecuteContext c = new ExecuteContext();
                    c.set(context);
                    matches.add(c);
                    DebugUtils.trace("matched "+c+".");
                }
        }
        DebugUtils.trace("Found "+matches.size()+" verbs.");
        return matches;
    }
    
    private static boolean isVerbFor(ExecuteContext context, LociObject obj, Verb verb)
    {
        DebugUtils.trace("    Testing verb "+verb);
        String cmd = context.getCommand().trim();
        cmd = matchVerb(context, obj, verb, cmd);
        if (cmd == null)
        {
            DebugUtils.trace("      Match failed on verb");
            return false;
        }
        String[] cmds = matchPreposition(context, obj, verb, cmd);
        if (cmds == null)
        {
            DebugUtils.trace("      Match failed on preposition");
            return false;
        }
        if (!matchDirectObject(context, obj, verb, cmds[0]))
        {
            DebugUtils.trace("      Match failed on direct object");
            return false;
        }
        if (!matchIndirectObject(context, obj, verb, cmds[1]))
        {
            DebugUtils.trace("      Match failed on indirect object");
            return false;
        }
        context.setMatchedVerb(verb);
        context.setMatchedVerbHost(obj);
        return true;
    }
    
    private static String matchVerb(ExecuteContext context, LociObject obj, Verb verb, String cmd)
    {
        if (verb.getVerbType() == Verb.ARG_TYPE_PATTERN)
        {
            Matcher vm = verb.getVerbPattern().matcher(cmd);
            if (!vm.find())
                return null;
            if (vm.start() > 0)
                return null;
            context.setVerbText(vm.group(0));
            cmd = cmd.substring(vm.end());
            if ((cmd.length() > 0) && !Character.isWhitespace(cmd.charAt(0)))
                return null;
            cmd = cmd.trim();
            return cmd;
        }
        else if (verb.getVerbType() == Verb.ARG_TYPE_THIS)
        {
            if (!cmd.toLowerCase().startsWith(obj.getName().toLowerCase()))
                return null;
            int end = obj.getName().length();
            if ((end < cmd.length()) && !Character.isWhitespace(cmd.charAt(end)))
                return null;
            context.setVerbText(cmd.substring(0, end));
            cmd = cmd.substring(end).trim();
            return cmd;
        }
        else
            throw new IllegalStateException("Unknown verb type: "+verb.getVerbType());
    }

    private static boolean matchAObject(ExecuteContext context, String cmd, LociObject obj,
            Consumer<String> setObjectText, Consumer<LociObject> setMatchedObject)
    {
        if (cmd.equalsIgnoreCase("me"))
        {
            if (!context.getInvoker().getURI().equals(obj.getURI()))
                return false;
            setObjectText.accept(cmd);
            setMatchedObject.accept((LociObject)context.getInvoker());
            return true;
        }
        if (cmd.equalsIgnoreCase("here"))
        {
            LociObject invoker = (LociObject)context.getInvoker();
            if (!obj.getURI().equals(invoker.getContainedBy()))
                return false;
            setObjectText.accept(cmd);
            LociObject here = (LociObject)DataStoreLogic.load(invoker.getContainedBy());
            setMatchedObject.accept(here);
            return true;
        }
        if (!cmd.toLowerCase().equals(obj.getName().toLowerCase()))
            return false;
        setObjectText.accept(obj.getName());
        cmd = cmd.substring(obj.getName().length());
        setMatchedObject.accept(obj);
        return true;
    }
    
    private static boolean matchObject(ExecuteContext context, LociObject obj, Verb verb, String cmd, int objectType, Pattern objectPattern,
            Consumer<String> setObjectText, Consumer<LociObject> setMatchedObject)
    {
        if (objectType == Verb.ARG_TYPE_PATTERN)
        {
            Matcher dom = objectPattern.matcher(cmd);
            if (!dom.matches())
                return false;
            setObjectText.accept(dom.group(0));
            return true;
        }
        else if (objectType == Verb.ARG_TYPE_THIS)
        {
            return matchAObject(context, cmd, obj, setObjectText, setMatchedObject);
        }
        else if (objectType == Verb.ARG_TYPE_ANY)
        {
            for (LociObject o : context.getVisibleTo())
            {
                boolean matched = matchAObject(context, cmd, o, setObjectText, setMatchedObject);
                if (matched)
                    return true;
            }
            return false;
        }
        else if (objectType == Verb.ARG_TYPE_NONE)
        {
            return cmd.length() == 0;
        }
        else
            throw new IllegalArgumentException("Unknown arg type="+objectType);
    }

    private static boolean matchDirectObject(ExecuteContext context, LociObject obj, Verb verb, String cmd)
    {
        return matchObject(context, obj, verb, cmd, 
                verb.getDirectObjectType(), verb.getDirectObjectPattern(), 
                (e) -> context.setDirectObjectText(e), 
                (e) -> context.setMatchedDirectObject(e));
    }

    private static String[] matchPreposition(ExecuteContext context, LociObject obj, Verb verb, String cmd)
    {
        if (verb.getPrepositionType() == Verb.ARG_TYPE_PATTERN)
        {
            Matcher pm = verb.getPrepositionPattern().matcher(cmd);
            if (!pm.find())
                return null;
            if ((cmd.length() > pm.end()) && !Character.isWhitespace(cmd.charAt(pm.end())))
                return null;
            context.setPrepositionText(pm.group(0));
            String[] cmds = new String[2];
            cmds[0] = cmd.substring(0, pm.start()).trim();
            cmds[1] = cmd.substring(pm.end()).trim();
            return cmds;
        }
        else if (verb.getDirectObjectType() == Verb.ARG_TYPE_NONE)
        {
            return new String[] { "", cmd };
        }
        else
        {
            return new String[] { cmd, "" };
        }
    }

    private static boolean matchIndirectObject(ExecuteContext context, LociObject obj, Verb verb, String cmd)
    {
        return matchObject(context, obj, verb, cmd, 
                verb.getIndirectObjectType(), verb.getIndirectObjectPattern(), 
                (e) -> context.setIndirectObjectText(e), 
                (e) -> context.setMatchedIndirectObject(e));
    }
    
    private static void populateVisible(ExecuteContext context)
    {
        if (context.getInvoker() instanceof LociObject)
        {
            // add what contains the invoker
            LociObject invoker = (LociObject)context.getInvoker();
            if (invoker != null)
                context.getVisibleTo().add(invoker);
            if (!StringUtils.isTrivial(invoker.getContainedBy()))
            {
                LociObject container = (LociObject)DataStoreLogic.load(invoker.getContainedBy());
                if (container != null)
                    context.getVisibleTo().add(container);
                // add what else is in container
                for (String uri : container.getContains())
                    if (!uri.equals(invoker.getURI()))
                    {
                        LociObject child = (LociObject)DataStoreLogic.load(uri);
                        if (child != null)
                            context.getVisibleTo().add(child);
                    }
            }
            // add what invoker contains
            String[] contains = invoker.getContains();
            if (contains != null)
                for (String uri : invoker.getContains())
                {
                    LociObject child = (LociObject)DataStoreLogic.load(uri);
                    if (child != null)
                        context.getVisibleTo().add(child);
                }
        }
    }
}
