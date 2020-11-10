package jo.audio.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.json.simple.IJSONAble;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.LongUtils;

public class ToJSONLogic
{
    public static JSONObject toJSONFromBean(Object bean)
    {
        if (bean instanceof JSONObject)
            return (JSONObject)bean;
        JSONObject json = new JSONObject();
        json.put("className", bean.getClass().getName());
        try
        {
            BeanInfo beanClassInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] beanProps = beanClassInfo.getPropertyDescriptors();
            for (int i = 0; i < beanProps.length; i++)
            {
                Class<?> propType = beanProps[i].getPropertyType();
                if (propType == null)
                    continue;
                Method read = beanProps[i].getReadMethod();
                Method write = beanProps[i].getWriteMethod();
                if ((read != null) && (write != null))
                    try
                    {
                        Object val = beanProps[i].getReadMethod().invoke(bean);
                        if (val != null)
                            if (val instanceof Object[][])
                                json.put(beanProps[i].getName(), toJSONArray((Object[])val));
                            else if (val instanceof Object[])
                                json.put(beanProps[i].getName(), toJSONArray((Object[])val));
                            else if (val instanceof long[])
                                json.put(beanProps[i].getName(), toJSONArray(LongUtils.toArray((long[])val)));
                            else if (val instanceof int[])
                                json.put(beanProps[i].getName(), toJSONArray(IntegerUtils.toArray((int[])val)));
                            else if (val instanceof String[])
                                json.put(beanProps[i].getName(), toJSONArray((String[])val));
                            else if (val instanceof Collection<?>)
                                json.put(beanProps[i].getName(), toJSONArray(((Collection<?>)val).toArray()));
                            else if (val instanceof JSONObject)
                                json.put(beanProps[i].getName(), val);
                            else if (val instanceof JSONArray)
                                json.put(beanProps[i].getName(), val);
                            else if (val instanceof Map<?,?>)
                                json.put(beanProps[i].getName(), toJSON((Map<?,?>)val));
                            else if (ToJSONLogic.isSimple(val.getClass()))
                                json.put(beanProps[i].getName(), val);
                            else
                                json.put(beanProps[i].getName(), toJSON(val));
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
            }
        }
        catch (IntrospectionException e1)
        {
            e1.printStackTrace();
        }
        return json;
    }
    @SuppressWarnings("unchecked")
    private static JSONArray toJSONArray(Object[] arr)
    {
        JSONArray json = new JSONArray();
        for (Object o : arr)
        {
            if (o == null)
                json.add(null);
            else if (ToJSONLogic.isSimple(o.getClass()))
                json.add(o);
            else
                json.add(toJSON(o));
        }
        return json;
    }
    public static JSONObject toJSON(Object obj)
    {
        if (obj instanceof IJSONAble)
            return ((IJSONAble)obj).toJSON();
        return toJSONFromBean(obj);
    }
    @SuppressWarnings("unchecked")
    public static JSONArray toJSON(long[] beans)
    {
        JSONArray json = new JSONArray();
        for (long bean : beans)
            json.add(bean);
        return json;
    }
    @SuppressWarnings("unchecked")
    public static JSONArray toJSON(Object[] beans)
    {
        JSONArray json = new JSONArray();
        for (Object bean : beans)
            if (bean instanceof Number)
                json.add(bean);
            else if (bean instanceof String)
                json.add(bean);
            else
                json.add(toJSON(bean));
        return json;
    }
    public static JSONObject toJSON(Map<?,?> map)
    {
        JSONObject json = new JSONObject();
        for (Object key : map.keySet())
        {
            Object bean = map.get(key);
            if (bean instanceof Number)
                json.put(key.toString(), bean);
            else if (bean instanceof String)
                json.put(key.toString(), bean);
            else if (bean instanceof Object[])
                json.put(key.toString(), toJSON((Object[])bean));
            else
                json.put(key.toString(), toJSON(bean));
        }
        return json;
    }

    public static boolean isSimple(Class<?> clazz)
    {
        return clazz.isPrimitive() || clazz.getName().startsWith("java.lang.");
    }
}
