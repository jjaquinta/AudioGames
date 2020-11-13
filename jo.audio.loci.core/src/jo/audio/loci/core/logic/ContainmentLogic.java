package jo.audio.loci.core.logic;

import java.util.ArrayList;
import java.util.List;

import jo.audio.loci.core.data.LociObject;
import jo.util.utils.obj.StringUtils;

public class ContainmentLogic
{
    public static boolean isContainedIn(LociObject parent, LociObject child)
    {
        synchronized (ContainmentLogic.class)
        {
            if (!child.getContainedBy().equals(parent.getURI()))
                return false;
            String[] contains = parent.getContains();
            if (contains == null)
                return false;
            for (String contain : contains)
                if (contain.equals(child.getURI()))
                    return true;
            return false;
        }
    }
    
    public static void remove(LociObject parent, LociObject child)
    {
        synchronized (ContainmentLogic.class)
        {
            if (!isContainedIn(parent, child))
                throw new IllegalStateException("Parent '"+parent.getURI()+"' does not contain child '"+child.getURI()+"'");
            child.setContainedBy(null);
            String[] oldContains = parent.getContains();
            if (oldContains == null)
                oldContains = new String[0];
            List<String> newContains = new ArrayList<>();
            for (String oldContain : oldContains)
                if (!oldContain.equals(child.getURI()))
                    newContains.add(oldContain);
            parent.setContains(newContains.toArray(new String[0]));
        }
    }
    
    public static void add(LociObject parent, LociObject child)
    {
        synchronized (ContainmentLogic.class)
        {
            if (!StringUtils.isTrivial(child.getContainedBy()))
                throw new IllegalStateException("Child '"+child.getURI()+"' is already contained by '"+child.getContainedBy()+"'");
            child.setContainedBy(parent.getURI());
            String[] oldContains = parent.getContains();
            if (oldContains == null)
                oldContains = new String[0];
            String[] newContains = new String[oldContains.length + 1];
            System.arraycopy(oldContains, 0, newContains, 0, oldContains.length);
            newContains[newContains.length - 1] = child.getURI();
            parent.setContains(newContains);
        }
    }
}
