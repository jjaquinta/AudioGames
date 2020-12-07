package jo.audio.loci.core.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import jo.audio.loci.core.logic.DataProfileLogic;
import jo.util.utils.obj.StringUtils;

public abstract class Verb
{
    public static final int ARG_TYPE_NONE = 0;
    public static final int ARG_TYPE_THIS = 1;
    public static final int ARG_TYPE_ANY = 2;
    public static final int ARG_TYPE_PATTERN = 3;
    public static final int ARG_TYPE_CLASS = 4;
    
    private String  mID;
    private String  mVerbText;
    private int     mVerbType;
    private String  mDirectObjectText;
    private String  mPrepositionText;
    private String  mIndirectObjectText;
    private int     mDirectObjectType;
    private int     mPrepositionType;
    private int     mIndirectObjectType;
    private Pattern mVerbPattern;
    private Pattern mDirectObjectPattern;
    private Pattern mPrepositionPattern;
    private Pattern mIndirectObjectPattern;
    private List<Class<? extends LociBase>> mDirectObjectClasses;
    private List<Class<? extends LociBase>> mIndirectObjectClasses;
    
    public Verb(String id, String verbText, String directObjectText, String prepositionText, String indirectObjectText)
    {
        if (id == null)
            id = getClass().getSimpleName();
        mID = id;
        mVerbText = verbText;
        mDirectObjectText = directObjectText;
        mPrepositionText = prepositionText;
        mIndirectObjectText = indirectObjectText;
        parseTypes();
    }
    
    public Verb(String verbText, String directObjectText, String prepositionText, String indirectObjectText)
    {
        this(null, verbText, directObjectText, prepositionText, indirectObjectText);
    }
    
    private void parseTypes()
    {
        parseVerb();
        parseDirectObject();
        parsePreposition();
        parseIndirectObject();
    }
    
    private void parseVerb()
    {
        if (mVerbText.equals("self") || mVerbText.equals("this"))
        {
            mVerbType = ARG_TYPE_THIS;
            return;
        }
        mVerbType = ARG_TYPE_PATTERN;
        mVerbPattern = commaListToPattern(mVerbText);
    }
    
    private void parseDirectObject()
    {
        if ((mDirectObjectText == null) || mDirectObjectText.equals("none") || mDirectObjectText.equals("null"))
        {
            mDirectObjectType = ARG_TYPE_NONE;
            return;
        }
        if (mDirectObjectText.equals("self") || mDirectObjectText.equals("this"))
        {
            mDirectObjectType = ARG_TYPE_THIS;
            return;
        }
        if (mDirectObjectText.equals("any"))
        {
            mDirectObjectType = ARG_TYPE_ANY;
            return;
        }
        if (mDirectObjectText.startsWith("$"))
        {
            mDirectObjectType = ARG_TYPE_CLASS;
            mDirectObjectClasses = parseClassList(mDirectObjectText);
            return;
        }
        mDirectObjectType = ARG_TYPE_PATTERN;
        mDirectObjectPattern = commaListToPattern(mDirectObjectText);
    }
    
    private List<Class<? extends LociBase>> parseClassList(String directObjectText)
    {
        List<Class<? extends LociBase>> classes = new ArrayList<>();
        for (StringTokenizer st = new StringTokenizer(directObjectText, ","); st.hasMoreTokens(); )
        {
            String name = st.nextToken();
            if (name.startsWith("$"))
                name = name.substring(1);
            int o = name.lastIndexOf('.');
            if (o > 0)
                name = name.substring(o + 1);
            Class<? extends LociBase> type = DataProfileLogic.getArchetype(name);
            if (type == null)
                throw new IllegalArgumentException("Unknown type '"+name+"'");
            classes.add(type);
        }
        return classes;
    }

    private void parsePreposition()
    {
        if ((mPrepositionText == null) || mPrepositionText.equals("none") || mPrepositionText.equals("null"))
        {
            mPrepositionType = ARG_TYPE_NONE;
            return;
        }
        mPrepositionType = ARG_TYPE_PATTERN;
        mPrepositionPattern = commaListToPattern(mPrepositionText);
    }
    
    private void parseIndirectObject()
    {
        if ((mIndirectObjectText == null) || mIndirectObjectText.equals("none") || mIndirectObjectText.equals("null"))
        {
            mIndirectObjectType = ARG_TYPE_NONE;
            return;
        }
        if (mIndirectObjectText.equals("self") || mIndirectObjectText.equals("this"))
        {
            mIndirectObjectType = ARG_TYPE_THIS;
            return;
        }
        if (mIndirectObjectText.equals("any"))
        {
            mIndirectObjectType = ARG_TYPE_ANY;
            return;
        }
        if (mIndirectObjectText.startsWith("$"))
        {
            mIndirectObjectType = ARG_TYPE_CLASS;
            mIndirectObjectClasses = parseClassList(mIndirectObjectText);
            return;
        }
        mIndirectObjectType = ARG_TYPE_PATTERN;
        mIndirectObjectPattern = commaListToPattern(mIndirectObjectText);
    }
    
    public abstract void execute(ExecuteContext context);
    
    // utils
    @Override
    public String toString()
    {
        return mVerbText+":"+mDirectObjectText+":"+mPrepositionText+":"+mIndirectObjectText;
    }

    public static Pattern commaListToPattern(String text)
    {
        return listToPattern(text, ",|");
    }

    public static Pattern listToPattern(String text, String delim)
    {
        if (text == null)
            return Pattern.compile("^$");
        StringBuffer regex = new StringBuffer("(");
        List<String> chunks = new ArrayList<>();
        for (StringTokenizer st = new StringTokenizer(text, delim); st.hasMoreTokens(); )
            chunks.add(st.nextToken().trim());
        Collections.sort(chunks, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2)
            {
                return o2.length() - o1.length();
            }
        });
        regex.append(StringUtils.listize(chunks, "|"));
        regex.append(")");
        return Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE);
    }
    
    // getters and setters

    public String getID()
    {
        return mID;
    }

    public void setID(String iD)
    {
        mID = iD;
    }

    public String getVerbText()
    {
        return mVerbText;
    }

    public void setVerbText(String verbText)
    {
        mVerbText = verbText;
    }

    public String getDirectObjectText()
    {
        return mDirectObjectText;
    }

    public void setDirectObjectText(String directObjectText)
    {
        mDirectObjectText = directObjectText;
    }

    public String getPrepositionText()
    {
        return mPrepositionText;
    }

    public void setPrepositionText(String prepositionText)
    {
        mPrepositionText = prepositionText;
    }

    public String getIndirectObjectText()
    {
        return mIndirectObjectText;
    }

    public void setIndirectObjectText(String indirectObjectText)
    {
        mIndirectObjectText = indirectObjectText;
    }

    public int getDirectObjectType()
    {
        return mDirectObjectType;
    }

    public void setDirectObjectType(int directObjectType)
    {
        mDirectObjectType = directObjectType;
    }

    public int getIndirectObjectType()
    {
        return mIndirectObjectType;
    }

    public void setIndirectObjectType(int indirectObjectType)
    {
        mIndirectObjectType = indirectObjectType;
    }

    public Pattern getVerbPattern()
    {
        return mVerbPattern;
    }

    public void setVerbPattern(Pattern verbPattern)
    {
        mVerbPattern = verbPattern;
    }

    public Pattern getDirectObjectPattern()
    {
        return mDirectObjectPattern;
    }

    public void setDirectObjectPattern(Pattern directObjectPattern)
    {
        mDirectObjectPattern = directObjectPattern;
    }

    public Pattern getPrepositionPattern()
    {
        return mPrepositionPattern;
    }

    public void setPrepositionPattern(Pattern prepositionPattern)
    {
        mPrepositionPattern = prepositionPattern;
    }

    public Pattern getIndirectObjectPattern()
    {
        return mIndirectObjectPattern;
    }

    public void setIndirectObjectPattern(Pattern indirectObjectPattern)
    {
        mIndirectObjectPattern = indirectObjectPattern;
    }

    public int getPrepositionType()
    {
        return mPrepositionType;
    }

    public void setPrepositionType(int prepositionType)
    {
        mPrepositionType = prepositionType;
    }

    public int getVerbType()
    {
        return mVerbType;
    }

    public void setVerbType(int verbType)
    {
        mVerbType = verbType;
    }

    public List<Class<? extends LociBase>> getDirectObjectClasses()
    {
        return mDirectObjectClasses;
    }

    public void setDirectObjectClasses(
            List<Class<? extends LociBase>> directObjectClasses)
    {
        mDirectObjectClasses = directObjectClasses;
    }

    public List<Class<? extends LociBase>> getIndirectObjectClasses()
    {
        return mIndirectObjectClasses;
    }

    public void setIndirectObjectClasses(
            List<Class<? extends LociBase>> indirectObjectClasses)
    {
        mIndirectObjectClasses = indirectObjectClasses;
    }
}
