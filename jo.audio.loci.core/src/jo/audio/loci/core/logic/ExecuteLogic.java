package jo.audio.loci.core.logic;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.Verb;
import jo.util.utils.obj.StringUtils;

public class ExecuteLogic
{
    public static boolean DEBUG = false;
    
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
        debug("Finding verb for "+context.getCommand());
        for (LociObject obj : context.getVisibleTo())
        {
            debug("Searching verbs on "+obj);
            List<Verb> verbs = VerbProfileLogic.getVerbs(obj.getVerbProfile());
            for (Verb verb : verbs)
                if (isVerbFor(context, obj, verb))
                    return true;
        }
        return false;
    }
    
    private static boolean isVerbFor(ExecuteContext context, LociObject obj, Verb verb)
    {
        debug("    Testing verb "+verb);
        String cmd = context.getCommand().trim();
        cmd = matchVerb(context, verb, cmd);
        if (cmd == null)
        {
            debug("      Match failed on verb");
            return false;
        }
        String[] cmds = matchPreposition(context, obj, verb, cmd);
        if (cmds == null)
        {
            debug("      Match failed on preposition");
            return false;
        }
        if (!matchDirectObject(context, obj, verb, cmds[0]))
        {
            debug("      Match failed on direct object");
            return false;
        }
        if (!matchIndirectObject(context, obj, verb, cmds[1]))
        {
            debug("      Match failed on indirect object");
            return false;
        }
        context.setMatchedVerb(verb);
        context.setMatchedVerbHost(obj);
        return true;
    }
    
    private static String matchVerb(ExecuteContext context, Verb verb, String cmd)
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
            context.getVisibleTo().add(invoker);
            if (!StringUtils.isTrivial(invoker.getContainedBy()))
            {
                LociObject container = (LociObject)DataStoreLogic.load(invoker.getContainedBy());
                context.getVisibleTo().add(container);
                // add what else is in container
                for (String uri : container.getContains())
                    context.getVisibleTo().add((LociObject)DataStoreLogic.load(uri));
            }
            // add what invoker contains
            String[] contains = invoker.getContains();
            if (contains != null)
                for (String uri : invoker.getContains())
                    context.getVisibleTo().add((LociObject)DataStoreLogic.load(uri));
        }
    }
    
    private static void debug(String msg)
    {
        if (DEBUG)
            System.out.println(msg);
    }
}
