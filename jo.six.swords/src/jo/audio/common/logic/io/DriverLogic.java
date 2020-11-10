package jo.audio.common.logic.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jo.audio.util.IIOBean;
import jo.util.utils.DebugUtils;

public class DriverLogic
{
    public static int FETCH_TIMEOUT = 1000; // latency for almost-continuous
    
    private static List<DataDriver<?>> mDrivers = new ArrayList<>();
    private static Set<String> mOutputQueueIndex = new HashSet<String>();
    private static List<IIOBean> mOutputQueue = new LinkedList<IIOBean>();
    private static Thread mOutputQueueThread = null;
    private static boolean mSingleThreaded = false;
    public static int mOperations = 0;

    public static void setSingleThreaded(boolean singleThreaded)
    {
        mSingleThreaded = singleThreaded;
    }
    
    public static boolean isSingleThreaded()
    {
        return mSingleThreaded;
    }
    
    public static void addDriver(DataDriver<?> driver)
    {
        mDrivers.add(driver);
    }

    public static void clearCaches()
    {
        for (DataDriver<?> driver : mDrivers)
            driver.clearCache();
    }
    
    private static DataDriver<?> findDriver(IIOBean bean)
    {
        for (DataDriver<?> driver : mDrivers)
            if (driver.isDriverFor(bean))
                return driver;
        throw new IllegalArgumentException("No driver for "+bean.getClass().getName());
    }
    
    public static DataDriver<?> findDriver(Class<?> clazz)
    {
        for (DataDriver<?> driver : mDrivers)
            if (driver.isDriverFor(clazz))
                return driver;
        throw new IllegalArgumentException("No driver for "+clazz.getName());
    }
    
    public static void save(IIOBean bean)
    {
        DataDriver<?> driver = findDriver(bean);
        driver.save(bean);
    }
    
    public static IIOBean getFromURI(String uri, Class<?> type)
    {
        return findDriver(type).getFromURI(uri);
    }
    
    public static List<?> getByIndex(String pattern, Class<?> type)
    {
        return findDriver(type).getByIndex(pattern);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void getByIndex(String pattern, Class<?> type, List list)
    {
        list.addAll(getByIndex(pattern, type));
    }
    
    public static List<?> getFromField(Class<?> type, int maxNum, long maxTime, String... fieldNameValues)
    {
        return findDriver(type).getFromField(maxNum, maxTime, fieldNameValues);
    }
    
    public static List<?> getAll(Class<?> type)
    {
        return findDriver(type).getAll();
    }
    
    public static void getAllIncremental(Class<?> type, DataCallback cb)
    {
        findDriver(type).getAllIncremental(cb);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void getAll(Class<?> type, List list)
    {
        list.addAll(getAll(type));
    }
    
    public static void delete(IIOBean bean)
    {
        String uri = bean.getURI();
        synchronized (mOutputQueueIndex)
        {
            mOutputQueue.remove(bean);
            mOutputQueueIndex.remove(uri);
        }        
        DataDriver<?> driver = findDriver(bean);
        driver.delete(bean);        
    }
    
    public static void addToOutputQueue(String idx, IIOBean obj)
    {
        DebugUtils.trace("Adding to output queue: "+idx);
        synchronized (mOutputQueueIndex)
        {
            if (mOutputQueueIndex.contains(idx))
            {
                DebugUtils.trace("Already in output queue: "+idx);
                return; // assume objects the same
            }
            mOutputQueueIndex.add(idx);
            mOutputQueue.add(obj);
            if ((mOutputQueueThread == null) || !mOutputQueueThread.isAlive())
            {
                DebugUtils.trace("Kicking off thread");
                mOutputQueueThread = new Thread("OutputWriter") { public void run() { runOutput(); } };
                mOutputQueueThread.start();
            }
        }
    }
    
    private static void runOutput()
    {
        DebugUtils.trace("OUTPUT THREAD: Starting");
        for (;;)
        {
            IIOBean bean = null;
            synchronized (mOutputQueueIndex)
            {
                bean = getFromOutputQueue();
                if (bean == null)
                {
                    mOutputQueueThread = null;
                    DebugUtils.trace("OUTPUT THREAD: Terminating");
                    return;
                }
            }
            DataDriver<?> driver = findDriver(bean);
            DebugUtils.trace("OUTPUT THREAD: "+driver.getTableName()+" Saving "+bean.getURI());
            try
            {
                driver.saveImmediate(bean);
                DebugUtils.trace("OUTPUT THREAD: Saved "+bean.getURI());
            }
            catch (Throwable t)
            {
                DebugUtils.trace("OUTPUT THREAD: Exception while saving "+bean.getURI(), t);
                DebugUtils.trace("OUTPUT THREAD: Table="+driver.getTableName()+", KeyField="+driver.mKeyField);
                DebugUtils.trace("OUTPUT THREAD: Bean="+bean.toJSON().toJSONString());
            }
        }
    }

    private static IIOBean getFromOutputQueue()
    {
        if (mOutputQueue.size() == 0)
            return null;
        IIOBean bean = mOutputQueue.get(0);
        mOutputQueue.remove(0);
        mOutputQueueIndex.remove(bean.getURI());
        return bean;
    }

    @SuppressWarnings("unchecked")
    public static <T> Map<String,T> indexOutputQueue(Class<?> type)
    {
        Map<String,T> index = new HashMap<String, T>();
        synchronized (mOutputQueueIndex)
        {
            for (IIOBean o : mOutputQueue)
                if (type.isAssignableFrom(o.getClass()))
                    index.put(o.getURI(), (T)o);
        }
        return index;
    }
    
    public static IIOBean findInOutputQueue(String uri)
    {
        synchronized (mOutputQueueIndex)
        {
            if (mOutputQueueIndex.contains(uri))
                for (IIOBean o : mOutputQueue)
                    if (uri.equals(o.getURI()))
                        return o;
        }
        return null;
    }

    public static void join()
    {
        if (mOutputQueueThread != null)
            try
            {
                mOutputQueueThread.join();
            }
            catch (InterruptedException e)
            {
            }
    }
}
