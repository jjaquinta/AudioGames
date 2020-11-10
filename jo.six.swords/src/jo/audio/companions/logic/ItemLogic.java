package jo.audio.companions.logic;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompEffectTypeBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.DamageRollBean;
import jo.audio.companions.data.ItemSelectBean;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.util.utils.DebugUtils;
import jo.util.utils.io.ResourceUtils;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.StringUtils;

public class ItemLogic
{
    private static final List<CompItemTypeBean> mItemTypes = new ArrayList<>();
    private static final Map<Integer, List<CompItemTypeBean>> mItemTypeIndex = new HashMap<>();
    private static final Map<String, CompItemTypeBean> mItemIndex = new HashMap<>();
    
    private static final CompEffectTypeBean EXT_EFFECTS[] = {
        new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"damage\",\"subType\":\"fire:d0+3\",\"durationType\":0}")),
        new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"damage\",\"subType\":\"cold:d0+3\",\"durationType\":0}")),
        new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"damage\",\"subType\":\"electricity:d6\",\"durationType\":0}")),      
        new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"damage\",\"subType\":\"poison:d6\",\"durationType\":0}")),      
        null, null, null, null, null, null, null,
    };
    private static final int EXT_MAGIC[] = {
        3, 3, 3, 3,
        2, 2, 2, 2, 2, 2, 2,
    };
    private static final String EXT_ID[] = {
            "fire", "cold", "electricity", "poison",
            "bane-Demon",
            "bane-Devil",
            "bane-Dragon",
            "bane-Giant",
            "bane-Lycanthrope",
            "bane-Sylvan-or-Faerie",
            "bane-Undead",
        };
    private static final String EXT_SPECIAL[] = {
            null, null, null, null,
            "bane-Demon",
            "bane-Devil",
            "bane-Dragon",
            "bane-Giant",
            "bane-Lycanthrope",
            "bane-Sylvan-or-Faerie",
            "bane-Undead",
        };
    private static final String EXT_NAME[] = {
            "Flaming %s", "Frost %s", "%s of Thunder", "%s of Venom",
            "Demonbane %s",
            "Anti-Devil %s",
            "%s Dragonslayer",
            "%s Giantslayer",
            "Werebane %s",
            "Cold Iron %s",
            "Undead Slaying %s",
        };

    static
    {
        ItemLogic.readItems();
    }
    
    private static void readItems()
    {
        boolean debug = BooleanUtils.parseBoolean(System.getProperty("tsatsatzu.debug"));
        try
        {
            InputStream is = ResourceUtils.loadSystemResourceStream("itemTypes.json", CompanionsModelConst.class);
            JSONObject json = (JSONObject)JSONUtils.PARSER.parse(new InputStreamReader(is, "utf-8"));
            is.close();
            JSONArray items = JSONUtils.getArray(json, "itemTypes");
            for (int i = 0; i < items.size(); i++)
            {
                JSONObject item = (JSONObject)items.get(i);
                CompItemTypeBean jitem = new CompItemTypeBean();
                jitem.fromJSON(item);
                if (!debug && jitem.isTest())
                    continue;
                addItem(jitem);
                if (jitem.getMagic() == 0)
                {   // create magical variants
                    createPlusVariants(item);
                    if (jitem.isHand())
                        createEffectVariants(item);
                }
            }
            sortByCost(mItemTypes);
            for (Integer type : mItemTypeIndex.keySet())
                sortByCost(mItemTypeIndex.get(type));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void createPlusVariants(JSONObject item)
    {
        for (int j = 1; j <= 5; j++)
        {
            CompItemTypeBean mitem = new CompItemTypeBean();
            mitem.fromJSON(item);
            mitem.setMagic(j);
            mitem.setID(mitem.getID()+"+"+j);
            mitem.setName("+"+j+" "+mitem.getName());
            if (mitem.getCost() > 1)
                mitem.setCost(mitem.getCost()*(float)Math.pow(5, j));
            else
                mitem.setCost(1*(float)Math.pow(5, j));
            if (mitem.getACMod() != 0)
                mitem.setACMod(mitem.getACMod() - j);
            if (!"0".equals(mitem.getDamageSM()))
            {
                DamageRollBean r = mitem.getDamageSMRoll();
                r.setMod(r.getMod() + j);
            }
            if (!"0".equals(mitem.getDamageL()))
            {
                DamageRollBean r = mitem.getDamageLRoll();
                r.setMod(r.getMod() + j);
            }
            addItem(mitem);
        }
    }

    private static void createEffectVariants(JSONObject item)
    {
        for (int j = 0; j < EXT_EFFECTS.length; j++)
        {
            CompItemTypeBean mitem = new CompItemTypeBean();
            mitem.fromJSON(item);
            mitem.setMagic(EXT_MAGIC[j]);
            mitem.setID(mitem.getID()+"+"+EXT_ID[j]);
            mitem.setName(String.format(EXT_NAME[j], mitem.getName()));
            if (EXT_EFFECTS[j] != null)
                mitem.getEffects().add(EXT_EFFECTS[j]);
            if (mitem.getSpecial() == null)
                mitem.setSpecial(EXT_SPECIAL[j]);
            else
                mitem.setSpecial(mitem.getSpecial()+","+EXT_SPECIAL[j]);
            int mult = EXT_MAGIC[j];
            mult += mitem.getEffects().size();
            if (mitem.isSpecial("bane"))
                mult++;
            if (mitem.getCost() > 1)
                mitem.setCost(mitem.getCost()*(float)Math.pow(5, mult));
            else
                mitem.setCost(1*(float)Math.pow(5, mult));
            if (!"0".equals(mitem.getDamageSM()))
            {
                DamageRollBean r = mitem.getDamageSMRoll();
                r.setMod(r.getMod() + EXT_MAGIC[j]);
            }
            if (!"0".equals(mitem.getDamageL()))
            {
                DamageRollBean r = mitem.getDamageLRoll();
                r.setMod(r.getMod() + EXT_MAGIC[j]);
            }
            addItem(mitem);
        }
    }

    public static void sortByCost(List<CompItemTypeBean> items)
    {
        Collections.sort(items, new Comparator<CompItemTypeBean>() {
            @Override
            public int compare(CompItemTypeBean o1, CompItemTypeBean o2)
            {
                if (o1.getCost() != o2.getCost())
                    return (int)Math.signum(Math.abs(o1.getCost()) - Math.abs(o2.getCost()));
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    public static void addItem(CompItemTypeBean item)
    {
        mItemTypes.add(item);
        List<CompItemTypeBean> cat = mItemTypeIndex.get(item.getType());
        if (cat == null)
        {
            cat = new ArrayList<>();
            mItemTypeIndex.put(item.getType(), cat);
        }
        cat.add(item);
        mItemIndex.put(item.getID(), item);
    }
    
    public static CompItemInstanceBean createInstance(String id, int count)
    {
        if (!mItemIndex.containsKey(id))
            throw new IllegalArgumentException("No such item '"+id+"'");
        CompItemInstanceBean inst = new CompItemInstanceBean();
        inst.setID(id);
        inst.setQuantity(count);
        return inst;
    }

    public static boolean contains(List<CompItemInstanceBean> list, String id, int amnt)
    {
        for (Iterator<CompItemInstanceBean> i = list.iterator(); i.hasNext(); )
        {
            CompItemInstanceBean item = i.next();
            if (id.equals(item.getID()))
                return item.getQuantity() >= amnt;
        }
        return false;
    }

    public static boolean isAny(List<CompItemInstanceBean> list, String id)
    {
        for (Iterator<CompItemInstanceBean> i = list.iterator(); i.hasNext(); )
        {
            CompItemInstanceBean item = i.next();
            if (id.equals(item.getID()))
                return true;
        }
        return false;
    }

    public static CompItemInstanceBean getMostMagical(List<CompItemInstanceBean> list, String id)
    {
        //DebugUtils.trace("Searching for best item of type '"+id+"'");
        CompItemInstanceBean best = null;
        for (Iterator<CompItemInstanceBean> i = list.iterator(); i.hasNext(); )
        {
            CompItemInstanceBean item = i.next();
            //DebugUtils.trace("Looking at '"+item.getID()+"' +"+item.getType().getMagic());
            if (item.getID().equals(id) || item.getID().startsWith(id+"+"))
                if (best == null)
                {
                    //DebugUtils.trace("Best item of type '"+id+"' is "+item.getID());
                    best = item;
                }
                else if (item.getType().getMagic() > best.getType().getMagic())
                {
                    //DebugUtils.trace("Better item of type '"+id+"' is "+item.getID());
                    best = item;
                }
        }
        return best;
    }

    public static void removeItem(List<CompItemInstanceBean> list, String id, int amnt)
    {
        for (Iterator<CompItemInstanceBean> i = list.iterator(); i.hasNext(); )
        {
            CompItemInstanceBean item = i.next();
            if (id.equals(item.getID()) && StringUtils.isTrivial(item.getName()))
            {
                item.setQuantity(item.getQuantity() - amnt);
                if (item.getQuantity() <= 0)
                    i.remove();
                return;
            }
        }
    }

    public static void addItem(List<CompItemInstanceBean> list, String id, int amnt)
    {
        for (Iterator<CompItemInstanceBean> i = list.iterator(); i.hasNext(); )
        {
            CompItemInstanceBean item = i.next();
            if (id.equals(item.getID()) && StringUtils.isTrivial(item.getName()))
            {
                item.setQuantity(item.getQuantity() + amnt);
                i.remove();
                list.add(0, item);
                return;
            }
        }
        CompItemInstanceBean item = new CompItemInstanceBean();
        item.setID(id);
        item.setQuantity(amnt);
        list.add(0, item);
    }

    public static void main(String[] argv)
    {
        System.out.println(mItemTypes.size()+" items read in "+mItemTypeIndex.size()+" categories.");
        for (CompItemTypeBean item : mItemTypes)
        {
            System.out.print("\t"+item.getName()+" $"+item.getCost());
            if (!"0".equals(item.getDamageSM()))
                System.out.print(" "+item.getDamageSMRoll()+"/"+item.getDamageLRoll());
            if (item.getACMod() != 0)
                System.out.print(" AC"+item.getACMod());
            System.out.print("\t("+item.getID()+")");
            for (CompEffectTypeBean effect : item.getEffects())
                System.out.print("\t"+effect.toJSON());
            System.out.println();
        }
    }

    public static CompItemTypeBean getItemType(String id)
    {
        if (!mItemIndex.containsKey(id))
            throw new IllegalArgumentException("No such item '"+id+"'");
        return mItemIndex.get(id);
    }

    public static List<CompItemTypeBean> getAllItemTypes(int max)
    {
        List<CompItemTypeBean> types = new ArrayList<>();
        for (List<CompItemTypeBean> i : mItemTypeIndex.values())
            types.addAll(i);
        if (max > 0)
            while (types.size() > max)
                types.remove(BaseUserState.RND.nextInt(types.size()));
        return types;
    }

    public static List<CompItemTypeBean> getItemTypes(int type, int status)
    {
        if (!mItemTypeIndex.containsKey(type))
        {
            DebugUtils.trace("Unable to find type "+type+" in index. Index contains:");
            for (Integer i : mItemTypeIndex.keySet())
                DebugUtils.trace("  "+i);
            throw new IllegalStateException("Unable to find type "+type+" in index. Index contains:");
        }
        List<CompItemTypeBean> types = new ArrayList<>();
        types.addAll(mItemTypeIndex.get(type));
        int max = (types.size()*(status + 1))/9;
        while (types.size() > max)
            types.remove(max);
        return types;
    }

    public static CompItemInstanceBean selectItem(ItemSelectBean select)
    {
        List<CompItemTypeBean> types = new ArrayList<>();
        types.addAll(mItemTypes);
        int selectType = -1;
        if (select.getType() != null)
            selectType = CompItemTypeBean.convType2Int(select.getType());
        for (Iterator<CompItemTypeBean> i = types.iterator(); i.hasNext(); )
        {
            CompItemTypeBean it = i.next();
            if (select.getID() != null)
                if (!select.getID().equals(it.getID()))
                {
                    i.remove();
                    continue;
                }
            if (selectType != -1)
                if (selectType != it.getType())
                {
                    i.remove();
                    continue;
                }
            if (select.getMagic() != -1)
                if (select.getMagic() != it.getMagic())
                {
                    i.remove();
                    continue;
                }
        }
        if (types.size() == 0)
            return null;
        CompItemTypeBean it = types.get(BaseUserState.RND.nextInt(types.size()));
        CompItemInstanceBean item = ItemLogic.createInstance(it.getID(), (select.getCount() == 0) ? 1 : select.getCount());
        if (select.getName() != null)
            item.setName(select.getName());
        return item;
    }

    public static CompItemTypeBean getAmmoTypeFor(CompItemInstanceBean weapon)
    {
        CompItemTypeBean ammo = null;
        if (CompItemTypeBean.TYPE_LAUNCHER == weapon.getType().getType())
            ammo = getItemType(weapon.getType().getAmmo());
        else if (CompItemTypeBean.TYPE_LAUNCHER == weapon.getType().getType())
            ammo = weapon.getType();
        return ammo;
    }

    public static CompItemInstanceBean getAmmoInstanceFor(CompUserBean user, CompCompanionBean player, CompItemInstanceBean weapon)
    {
        CompItemTypeBean ammoType = getAmmoTypeFor(weapon);
        if (ammoType == null)
            return null;
        CompItemInstanceBean ammo = getMostMagical(user.getItems(), ammoType.getID());
        if (ammo == null)
            ammo =getMostMagical(player.getItems(), ammoType.getID());
        return ammo;
    }
    
    public static List<CompItemTypeBean> getItemsByTypes(Boolean magic, int... types)
    {
        if (types.length == 0)
            return mItemTypes;
        List<CompItemTypeBean> items = new ArrayList<>();
        Set<Integer> valid = new HashSet<>();
        for (int t : types)
            valid.add(t);
        for (CompItemTypeBean item : mItemTypes)
        {
            if ((magic != null) && ((item.getMagic() > 0) != magic))
                continue;
            if (valid.contains(item.getType()))
                items.add(item);
        }
        return items;
    }
    
    public static CompItemTypeBean getRandomItemByTypes(Random rnd, Boolean magic, int... types)
    {
        List<CompItemTypeBean> items = getItemsByTypes(magic, types);
        if (items.size() == 0)
            throw new IllegalStateException("No items with magic="+magic+", and types="+types);
        double total = 0;
        for (CompItemTypeBean item : items)
            total += 1/(item.getCost() + 1);
        double roll = rnd.nextDouble()*total;
        for (CompItemTypeBean item : items)
        {
            roll -= 1/(item.getCost() + 1);
            if (roll < 0)
                return item;
        }
        return items.get(0);
    }
}
