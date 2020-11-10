package jo.audio.common.data;

import java.util.HashMap;

@SuppressWarnings("serial")
@MapType(keyType=String.class, valueType=Integer.class)
public class StringIntegerMap extends HashMap<String, Integer>
{
    @Override
    public Integer get(Object key)
    {
        if (containsKey(key))
            return ((Number)super.get(key)).intValue();
        return null;
    }
}
