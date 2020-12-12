package jo.audio.loci.core.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jo.audio.loci.core.data.ExecuteContext;
import jo.audio.loci.core.data.InvocationContext;
import jo.audio.loci.core.data.LociBase;
import jo.audio.loci.core.data.LociObject;
import jo.audio.loci.core.data.TypeAheadContext;
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
    
    public static TypeAheadContext typeAhead(LociBase invoker)
    {
        TypeAheadContext context = new TypeAheadContext();
        context.setInvoker(invoker);
        populateVisible(context);
        typeAheadVerbs(context);
        Set<String> all = new HashSet<>();
        all.addAll(context.getCommands());
        context.getCommands().clear();
        context.getCommands().addAll(all);
        Collections.sort(context.getCommands());
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
            DebugUtils.trace("Searching verbs on "+obj+", "+obj.getDataProfile());
            List<Verb> verbs = VerbLogic.getVerbs(obj.getClass());
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
            context.setVerbMatcher(vm);
            cmd = cmd.substring(vm.end());
            if ((cmd.length() > 0) && !Character.isWhitespace(cmd.charAt(0)))
                return null;
            cmd = cmd.trim();
            return cmd;
        }
        else if (verb.getVerbType() == Verb.ARG_TYPE_THIS)
        {
            Matcher m = obj.getNamePattern().matcher(cmd);
            if (!m.find())
                return null;
            if (m.start() > 0)
                return null;
            int end = m.end();
            if ((end < cmd.length()) && !Character.isWhitespace(cmd.charAt(end)))
                return null;
            context.setVerbText(cmd.substring(0, end));
            context.setVerbMatcher(m);
            cmd = cmd.substring(end).trim();
            return cmd;
        }
        else
            throw new IllegalStateException("Unknown verb type: "+verb.getVerbType());
    }

    private static boolean matchAObject(ExecuteContext context, String cmd, LociObject obj,
            Consumer<String> setObjectText, Consumer<LociObject> setMatchedObject, Consumer<Matcher> setMatchedMatcher)
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
        Pattern p = obj.getNamePattern();
        Matcher m = p.matcher(cmd);
        if ((p == null) || !m.matches())
            return false;
        setObjectText.accept(cmd);
        setMatchedObject.accept(obj);
        setMatchedMatcher.accept(m);
        return true;
    }

    private static boolean matchAObject(ExecuteContext context, String cmd, LociObject obj,
            List<Class<? extends LociBase>> objectClasses, Consumer<String> setObjectText, Consumer<LociObject> setMatchedObject, Consumer<Matcher> setMatchedMatcher)
    {
        for (Class<? extends LociBase> objectClass : objectClasses)
            if (objectClass.isAssignableFrom(obj.getClass()))
            {
                Matcher m = obj.getNamePattern().matcher(cmd);
                if (m.matches())
                {
                    setObjectText.accept(cmd);
                    setMatchedObject.accept(obj);
                    setMatchedMatcher.accept(m);
                    return true;
                }
            }
        return false;
    }
    
    private static boolean matchObject(ExecuteContext context, LociObject obj, Verb verb, String cmd, 
            int objectType, Pattern objectPattern, List<Class<? extends LociBase>> objectClasses,
            Consumer<String> setObjectText, Consumer<LociObject> setMatchedObject, Consumer<Matcher> setMatchedMatcher)
    {
        if (objectType == Verb.ARG_TYPE_PATTERN)
        {
            Matcher dom = objectPattern.matcher(cmd);
            if (!dom.matches())
                return false;
            setObjectText.accept(dom.group(0));
            setMatchedMatcher.accept(dom);
            return true;
        }
        else if (objectType == Verb.ARG_TYPE_THIS)
        {
            return matchAObject(context, cmd, obj, setObjectText, setMatchedObject, setMatchedMatcher);
        }
        else if (objectType == Verb.ARG_TYPE_CLASS)
        {
            for (LociObject o : context.getVisibleTo())
            {
                boolean matched = matchAObject(context, cmd, o, objectClasses, setObjectText, setMatchedObject, setMatchedMatcher);
                if (matched)
                    return true;
            }
            return false;
        }
        else if (objectType == Verb.ARG_TYPE_ANY)
        {
            for (LociObject o : context.getVisibleTo())
            {
                boolean matched = matchAObject(context, cmd, o, setObjectText, setMatchedObject, setMatchedMatcher);
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
                verb.getDirectObjectType(), verb.getDirectObjectPattern(), verb.getDirectObjectClasses(),
                (e) -> context.setDirectObjectText(e), 
                (e) -> context.setMatchedDirectObject(e),
                (e) -> context.setDirectObjectMatcher(e));
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
            context.setPrepositionMatcher(pm);
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
                verb.getIndirectObjectType(), verb.getIndirectObjectPattern(), verb.getIndirectObjectClasses(), 
                (e) -> context.setIndirectObjectText(e), 
                (e) -> context.setMatchedIndirectObject(e),
                (e) -> context.setIndirectObjectMatcher(e));
    }
    
    private static void populateVisible(InvocationContext context)
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
    
    private static void typeAheadVerbs(TypeAheadContext context)
    {
        DebugUtils.trace("Finding type aheads.");
        for (LociObject obj : context.getVisibleTo())
        {
            DebugUtils.trace("  Expanding verbs on "+obj+", "+obj.getDataProfile());
            List<Verb> verbs = VerbLogic.getVerbs(obj.getClass());
            for (Verb verb : verbs)
                addTypeAheadsFor(context, obj, verb);
        }
        DebugUtils.trace("Found "+context.getCommands().size()+" type aheads.");
    }
    
    private static void addTypeAheadsFor(TypeAheadContext context, LociObject obj, Verb verb)
    {
        DebugUtils.trace("    Expanding "+verb.getID()+"/"+verb);
        List<String> verbs = getTypeAheadVerbs(obj, verb);
        List<String> dos = getTypeAheadDirectObjects(context, obj, verb);
        List<String> preps = getTypeAheadPreopositions(obj, verb);
        List<String> idos = getTypeAheadInDirectObjects(context, obj, verb);
        DebugUtils.trace("    "+verbs.size()+" verbs, "+dos.size()+" dos, "+preps.size()+" preps, "+idos.size()+" idos.");
        List<List<String>> combos = new ArrayList<>();
        if (verbs.size() > 0)
            combos.add(verbs);
        else if (verb.getVerbType() != Verb.ARG_TYPE_NONE)
        {
            DebugUtils.trace("    no match on verb.");
            return;
        }
        if (dos.size() > 0)
            combos.add(dos);
        else if (verb.getDirectObjectType() != Verb.ARG_TYPE_NONE)
        {
            DebugUtils.trace("    no match on do.");
            return;
        }
        if (preps.size() > 0)
            combos.add(preps);
        else if (verb.getPrepositionType() != Verb.ARG_TYPE_NONE)
        {
            DebugUtils.trace("    no match on prep.");
            return;
        }
        if (idos.size() > 0)
            combos.add(idos);
        else if (verb.getIndirectObjectType() != Verb.ARG_TYPE_NONE)
        {
            DebugUtils.trace("    no match on ido.");
            return;
        }
        addCombos(combos, 0, "", context.getCommands());
    }
    
    private static List<String> getTypeAheadVerbs(LociObject obj, Verb verb)
    {
        List<String> ops = new ArrayList<>();
        switch (verb.getVerbType())
        {
            case Verb.ARG_TYPE_PATTERN:
                addPattern(ops, verb.getVerbPattern());
                break;
            case Verb.ARG_TYPE_THIS:
                addPattern(ops, obj.getNamePattern());
                break;
            default:
                throw new IllegalStateException("Unknown verb type: "+verb.getVerbType());
        }
        prune(ops);
        return ops;
    }

    private static List<String> getTypeAheadDirectObjects(TypeAheadContext context, LociObject obj,
            Verb verb)
    {
        List<String> ops = new ArrayList<>();
        switch (verb.getDirectObjectType())
        {
            case Verb.ARG_TYPE_PATTERN:
                addPattern(ops, verb.getDirectObjectPattern());
                break;
            case Verb.ARG_TYPE_THIS:
                addPattern(ops, obj.getNamePattern());
                break;
            case Verb.ARG_TYPE_CLASS:
                for (LociObject o : context.getVisibleTo())
                    for (Class<? extends LociBase> objectClass : verb.getDirectObjectClasses())
                        if (objectClass.isAssignableFrom(obj.getClass()))
                            addPattern(ops, o.getNamePattern());
                break;
            case Verb.ARG_TYPE_ANY:
                for (LociObject o : context.getVisibleTo())
                    addPattern(ops, o.getNamePattern());
                break;
            case Verb.ARG_TYPE_NONE:
                break;
            default:
                throw new IllegalStateException("Unknown preposition type: "+verb.getVerbType());
        }
        prune(ops);
        return ops;
    }

    private static List<String> getTypeAheadPreopositions(LociObject obj,
            Verb verb)
    {
        List<String> ops = new ArrayList<>();
        switch (verb.getPrepositionType())
        {
            case Verb.ARG_TYPE_PATTERN:
                addPattern(ops, verb.getPrepositionPattern());
                break;
            case Verb.ARG_TYPE_NONE:
                break;
            default:
                throw new IllegalStateException("Unknown preposition type: "+verb.getVerbType());
        }
        prune(ops);
        return ops;
    }

    private static List<String> getTypeAheadInDirectObjects(TypeAheadContext context, LociObject obj,
            Verb verb)
    {
        List<String> ops = new ArrayList<>();
        switch (verb.getIndirectObjectType())
        {
            case Verb.ARG_TYPE_PATTERN:
                addPattern(ops, verb.getIndirectObjectPattern());
                break;
            case Verb.ARG_TYPE_THIS:
                addPattern(ops, obj.getNamePattern());
                break;
            case Verb.ARG_TYPE_CLASS:
                for (LociObject o : context.getVisibleTo())
                    for (Class<? extends LociBase> objectClass : verb.getIndirectObjectClasses())
                        if (objectClass.isAssignableFrom(obj.getClass()))
                            addPattern(ops, o.getNamePattern());
                break;
            case Verb.ARG_TYPE_ANY:
                for (LociObject o : context.getVisibleTo())
                    addPattern(ops, o.getNamePattern());
                break;
            case Verb.ARG_TYPE_NONE:
                break;
            default:
                throw new IllegalStateException("Unknown preposition type: "+verb.getVerbType());
        }
        prune(ops);
        return ops;
    }
    
    private static void prune(List<String> ops)
    {
        for (Iterator<String> i = ops.iterator(); i.hasNext(); )
            if (StringUtils.isTrivial(i.next()))
                i.remove();
    }

    private static void addCombos(List<List<String>> combos, int idx, String cmd, List<String> commands)
    {
        boolean more = idx + 1 < combos.size();
        for (String str : combos.get(idx))
        {
            String c = (cmd + " " + str).trim();
            if (more)
                addCombos(combos, idx + 1, c, commands);
            else
                commands.add(c);
        }
    }

    private static void addPattern(List<String> ops, Pattern pattern)
    {
        String txt = pattern.pattern();
        if (txt.startsWith("(") && txt.endsWith(")"))
        {
            txt = txt.substring(1, txt.length() - 1);
            for (StringTokenizer st = new StringTokenizer(txt, "|"); st.hasMoreTokens(); )
                ops.add(st.nextToken());
        }
        else
            ops.add(txt); // non-list pattern
    }
}
