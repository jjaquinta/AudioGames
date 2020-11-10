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
import org.json.simple.JSONUtils;

import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.DoubleUtils;
import jo.util.utils.obj.FloatUtils;
import jo.util.utils.obj.IntegerUtils;
import jo.util.utils.obj.LongUtils;

public class FromJSONLogic
{
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void fromJSONFromBean(Object bean, JSONObject json)
    {
        if (bean instanceof Map<?, ?>)
        {
            JSONUtils.fromJSON((Map<?, ?>)bean, json);
            return;
        }
        try
        {
            BeanInfo beanClassInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] beanProps = beanClassInfo.getPropertyDescriptors();
            for (int i = 0; i < beanProps.length; i++)
            {
                String propName = beanProps[i].getName();
                String jsonPropName = findJSONPropName(json, propName);
                if (jsonPropName == null)
                    continue;
                Object jsonPropValue = json.get(jsonPropName);
                Class<?> propType = beanProps[i].getPropertyType();
                if (propType == null)
                    continue;
                Method read = beanProps[i].getReadMethod();
                Method write = beanProps[i].getWriteMethod();
                if ((read != null) && (write != null))
                    try
                    {
                        if (propType == String.class)
                            write.invoke(bean, FromJSONLogic.toString(json, jsonPropName, null));
                        else if ((propType == int.class) || (propType == Integer.class))
                            write.invoke(bean, FromJSONLogic.toInt(json, jsonPropName, 0));
                        else if ((propType == long.class) || (propType == Long.class))
                            write.invoke(bean, FromJSONLogic.toLong(json, jsonPropName, 0));
                        else if ((propType == float.class) || (propType == Float.class))
                            write.invoke(bean, FromJSONLogic.toFloat(json, jsonPropName, 0));
                        else if ((propType == double.class) || (propType == Double.class))
                            write.invoke(bean, FromJSONLogic.toDouble(json, jsonPropName, 0));
                        else if ((propType == boolean.class) || (propType == Boolean.class))
                            write.invoke(bean, FromJSONLogic.toBoolean(json, jsonPropName, false));
                        else if ((propType == int[].class) && (jsonPropValue instanceof JSONArray))
                            write.invoke(bean, toIntArray((JSONArray)jsonPropValue));
                        else if ((propType == long[].class) && (jsonPropValue instanceof JSONArray))
                            write.invoke(bean, toLongArray((JSONArray)jsonPropValue));
                        else if ((propType == float[].class) && (jsonPropValue instanceof JSONArray))
                            write.invoke(bean, toFloatArray((JSONArray)jsonPropValue));
                        else if ((propType == String[].class) && (jsonPropValue instanceof JSONArray))
                            write.invoke(bean, new Object[] { toStringArray((JSONArray)jsonPropValue)});
                        else if ((propType == Object[].class) && (jsonPropValue instanceof JSONArray))
                            write.invoke(bean, new Object[] { toStringArray((JSONArray)jsonPropValue)});
                        else if ((propType == JSONObject.class) && (jsonPropValue instanceof JSONObject))
                            write.invoke(bean, jsonPropValue);
                        else if (Collection.class.isAssignableFrom(propType) && (jsonPropValue instanceof JSONArray))
                        {
                            JSONArray arr = (JSONArray)jsonPropValue;
                            Collection c = (Collection)beanProps[i].getReadMethod().invoke(bean);
                            ArrayType at = beanProps[i].getReadMethod().getAnnotation(ArrayType.class);
                            for (int idx = 0; idx < arr.size(); idx++)
                            {
                                Object j = arr.get(idx);
                                if (at == null)
                                    c.add(fromJSON(j));
                                else
                                {
                                    Object o = at.type().newInstance();
                                    fromJSON(o, (JSONObject)j);
                                    c.add(o);
                                }
                            }
                        }
                        else if (json.get(propName) instanceof JSONObject)
                        {
                            Object val = beanProps[i].getReadMethod().invoke(bean);
                            if (val != null)
                                if (val instanceof IJSONAble)
                                    ((IJSONAble)val).fromJSON((JSONObject)jsonPropValue);
                                else
                                    fromJSON(val, (JSONObject)jsonPropValue);
                        }
                        else if (jsonPropValue != null)
                            throw new IllegalArgumentException("Cannot assign "+jsonPropName+":"+jsonPropValue
                                +" to "+propName+":"+propType.getName());
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
    }
    private static String findJSONPropName(JSONObject json, String propName)
    {
        if (json.containsKey(propName))
            return propName;
        for (String key : json.keySet())
            if (key.equalsIgnoreCase(propName))
                return key;
        return null;
    }
    private static String[] toStringArray(JSONArray jsonArray)
    {
        String[] ret = new String[jsonArray.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = jsonArray.get(i).toString();
        return ret;
    }
    private static int[] toIntArray(JSONArray jsonArray)
    {
        int[] ret = new int[jsonArray.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = IntegerUtils.parseInt(jsonArray.get(i));
        return ret;
    }
    private static long[] toLongArray(JSONArray jsonArray)
    {
        long[] ret = new long[jsonArray.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = LongUtils.parseLong(jsonArray.get(i));
        return ret;
    }
    private static float[] toFloatArray(JSONArray jsonArray)
    {
        float[] ret = new float[jsonArray.size()];
        for (int i = 0; i < ret.length; i++)
            ret[i] = FloatUtils.parseFloat(jsonArray.get(i));
        return ret;
    }
    public static void fromJSON(Object obj, JSONObject json)
    {
        fromJSONFromBean(obj, json);
    }
    
    @SuppressWarnings("rawtypes")
    public static Object fromJSON(Object jsonVal)
    {
        if (jsonVal instanceof JSONObject)
        {
            JSONObject json = (JSONObject)jsonVal;
            String className = (String)(json.get("className"));
            if (className == null)
                throw new IllegalArgumentException("Can't work out how to convert '"+json.toJSONString()+"'");
            try
            {
                Class c = Class.forName(className);
                Object o = c.newInstance();
                fromJSON(o, json);
                return o;
            }
            catch (Exception e)
            {
                throw new IllegalArgumentException("Cannot create '"+className+"'");
            }
        }
        else
            return jsonVal;
    }

    public static boolean toBoolean(JSONObject json, String key, boolean def)
    {
        if (json.containsKey(key))
            return BooleanUtils.parseBoolean(json.get(key));
        else
            return def;
    }

    public static int toInt(JSONObject json, String key, int def)
    {
        if (json.containsKey(key))
            return IntegerUtils.parseInt(json.get(key));
        else
            return def;
    }

    public static long toLong(JSONObject json, String key, long def)
    {
        if (json.containsKey(key))
            return LongUtils.parseLong(json.get(key));
        else
            return def;
    }

    public static float toFloat(JSONObject json, String key, float def)
    {
        if (json.containsKey(key))
            return FloatUtils.parseFloat(json.get(key));
        else
            return def;
    }

    public static double toDouble(JSONObject json, String key, float def)
    {
        if (json.containsKey(key))
            return DoubleUtils.parseDouble(json.get(key));
        else
            return def;
    }

    public static String toString(JSONObject json, String key, String def)
    {
        if (json.containsKey(key))
        {
            Object val = json.get(key);
            if (val == null)
                return def;
            return val.toString();
        }
        else
            return def;
    }
}
