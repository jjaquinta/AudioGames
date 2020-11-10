package jo.audio.companions.app.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jo.audio.companions.app.data.AboutInfo;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.data.CompState;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.model.data.AudioMessageBean;
import jo.ipa.logic.IPAComp;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class WhatLogic
{
    public static void doWhat(CompState state, String thing)
    {
        if (StringUtils.isTrivial(thing))
        {
            state.respond(CompanionsModelConst.TEXT_WTF_WHATIS);
            return;
        }
        List<AudioMessageBean> msgs = resolveWhat(state, thing);
        if (msgs.size() > 0)
        {
            state.respond(msgs.get(0));
            msgs.remove(0);
            state.setMore(msgs.toArray(new AudioMessageBean[0]));
        }
        else
        {
            state.respond(CompanionsModelConst.TEXT_I_DONT_KNOW_WHAT_OR_WHO_XXX_IS, thing);
        }
    }
    
    public static List<AudioMessageBean> resolveWhat(CompState state, String thing)
    {
        DebugUtils.trace("Resolve what="+thing);
        List<AudioMessageBean> msgs = new ArrayList<>();
        List<IPAComp> matches = MatchLogic.findMatches(state, thing, MatchLogic.STATIC_ITEMS, MatchLogic.STATIC_MONSTERS, MatchLogic.MISC);
        Set<Object> done = new HashSet<>();
        for (IPAComp match : matches)
        {
            Object matched = match.getWord2().getMetadata();
            DebugUtils.trace("Matched "+match.getWord2().getWord()+" / "+matched+", dist="+match.getDistance());
            if (done.contains(matched))
                continue;
            done.add(matched);
            if (matched instanceof CompMonsterTypeBean)
                whatMonsterType(msgs, (CompMonsterTypeBean)matched);
            else if (matched instanceof CompItemTypeBean)
                whatItemType(msgs, (CompItemTypeBean)matched);
            else if (matched instanceof AboutInfo)
                whatAboutType(msgs, (AboutInfo)matched, state);
        }
        return msgs;
    }

    private static void whatAboutType(List<AudioMessageBean> msgs,
            AboutInfo info, CompState state)
    {
        msgs.addAll(info.response(state));
    }

    private static void whatMonsterType(List<AudioMessageBean> msgs,
            CompMonsterTypeBean monster)
    {
        if ((monster.getDetails() != null) && monster.getDetails().containsValue("description"))
            msgs.add(new AudioMessageBean(AudioMessageBean.RAW, monster.getDetails().getString("description")));
        msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_HAS_ARMOR_CLASS_YYY__AN_AVERAGE_OF_ZZZ_HIT_POINTS__AND_DOES_WWW_DAMAGE_ON_AVERAGE, 
                monster.getName(), monster.getAC(), (int)monster.getHDRoll().average(),
                (int)monster.getAverageDamage()));
    }

    public static void whatItemType(List<AudioMessageBean> msgs,
            CompItemTypeBean item)
    {
        switch (item.getType())
        {
            case CompItemTypeBean.TYPE_AMMO:
                // detail 1
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_DOES_ABOUT_YYY_DAMAGE,  
                        item.getName(), (int)item.getDamageSMRoll().average()));
                // detail == 2)
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_SOLD_IN_BUNDLES_OF_YYY,  
                        item.getName(), item.getCount()));
                // detail == 3
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_WEIGHS_YYY, 
                        item.getName(), (int)item.getEncumbrance()));
                break;
            case CompItemTypeBean.TYPE_ARMOR:
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IMPROVES_YOUR_ARMOR_BY_YYY,  
                        item.getName(), item.getACMod()));
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_WEIGHS_YYY, 
                        item.getName(), (int)item.getEncumbrance()));
                break;
            case CompItemTypeBean.TYPE_HAND:
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_DOES_ABOUT_YYY_DAMAGE,  
                        item.getName(), (int)item.getDamageSMRoll().average()));
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_REQUIRES_YYY_HANDS_TO_WIELD,  
                        item.getName(), item.getHandsNeeded()));
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_WEIGHS_YYY, 
                        item.getName(), (int)item.getEncumbrance()));
                break;
            case CompItemTypeBean.TYPE_HURLED:
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_DOES_ABOUT_YYY_DAMAGE,  
                        item.getName(), (int)item.getDamageSMRoll().average()));
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_SOLD_IN_BUNDLES_OF_YYY,  
                        item.getName(), item.getCount()));
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_WEIGHS_YYY, 
                        item.getName(), (int)item.getEncumbrance()));
                break;
            case CompItemTypeBean.TYPE_LAUNCHER:
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_DOES_ABOUT_YYY_DAMAGE,  
                        item.getName(), (int)item.getDamageSMRoll().average()));
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_REQUIRES_YYY_HANDS_TO_WIELD,  
                        item.getName(), item.getHandsNeeded()));
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_WEIGHS_YYY, 
                        item.getName(), (int)item.getEncumbrance()));
                break;
            case CompItemTypeBean.TYPE_SHIELD:
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IMPROVES_YOUR_ARMOR_BY_YYY,  
                        item.getName(), item.getACMod()));
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_WEIGHS_YYY, 
                        item.getName(), (int)item.getEncumbrance()));
                break;
            default:
                msgs.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_WEIGHS_YYY, 
                        item.getName(), (int)item.getEncumbrance()));
                break;
        }
    }

}
