package jo.audio.companions.logic.feature.dungeon;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompEffectTypeBean;
import jo.audio.companions.data.CompTreasuresBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.logic.CompIOLogic;
import jo.audio.companions.logic.TreasureLogic;
import jo.audio.companions.logic.effect.EffectCompanionLogic;
import jo.audio.companions.logic.feature.LibraryLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;

public class DungeonChestLogic
{
    private static JSONObject TREASURE = null;
    
    static void doYesChest(CompContextBean context)
    {
        if (TREASURE == null)
        {
            try
            {
                TREASURE = JSONUtils.readJSON("resource://jo/audio/companions/logic/feature/dungeon/chestTreasure.json");
            }
            catch (IOException e)
            {
                DebugUtils.trace("Error loading chest treasure", e);
                return;
            }
        }
        
        CompUserBean user = context.getUser();
        context.addMessage(CompanionsModelConst.TEXT_YOU_OPEN_THE_CHEST);
        context.addMessage(CompanionsModelConst.TEXT_PAUSE);
        
        int roll = DiceRollBean.roll(BaseUserState.RND, 2, 6);
        DebugUtils.trace("DungeonChestLogic, roll="+roll);
        switch (roll)
        {
            case 4: // double plus good - 3 : 16
                DOUBLE_PLUS_GOOD.action(context);
                break;
            case 6: // plus good - 5 : 16
                PLUS_GOOD.action(context);
                break;
            case 3:
            case 5: // good - 6 : 16
                GOOD.action(context);
                break;
            case 2:
            case 7: 
            case 12:    // nothing - 8 : 16
                context.addMessage(CompanionsModelConst.TEXT_THE_CHEST_IS_EMPTY);
                break;
            case 9:
            case 10:    // bad - 6 : 16
                BAD.action(context);
                break;
            case 8:     // plus bad - 5 : 16
                PLUS_BAD.action(context);
                break;
            case 11:    // double plus abd - 3 : 16
                DOUBLE_PLUS_BAD.action(context);
                break;
        }

        String list = user.getMetadata().getString(DungeonCommandLogic.LIST_CHEST);
        list = ResponseUtils.addToList(list, user.getLocation());
        user.getMetadata().put(DungeonCommandLogic.LIST_CHEST, list);
        CompIOLogic.saveUser(user);
    }
    
    private static CompCompanionBean getCompanion(CompContextBean context)    
    {
        CompCompanionBean comp = context.getCompanion();
        if (comp == null)
        {
            String id = context.getUser().getActiveCompanion();
            comp = context.getUser().getCompanion(id);
        }
        if (comp == null)
            comp = context.getUser().getCompanions().get(0);
        return comp;
    }

    private static void rollTreasures(CompContextBean context, String key)
    {
        CompTreasuresBean loot = new CompTreasuresBean();
        JSONArray choices = (JSONArray)TREASURE.get(key);
        JSONArray choice = (JSONArray)choices.get(BaseUserState.RND.nextInt(choices.size()));
        TreasureLogic.rollTreasures(1, BaseUserState.RND, loot, choice);
        TreasureLogic.addMessages(loot);
        TreasureLogic.addTreasure(context, loot);
        CompIOLogic.saveUser(context.getUser());
        context.addMessage(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_FIND_XXX_, loot.getMessage()));
    }
    
    private static final DungeonResults GOOD = new DungeonResults(new DungeonResult[] {
            new DungeonResult(90) {            
                @Override
                public void run(CompContextBean context)
                {
                    if (!LibraryLogic.discoverKey(context))
                    {
                        rollTreasures(context, "good");
                    }
                }
            },
    });
    
    private static final DungeonResults PLUS_GOOD = new DungeonResults(new DungeonResult[] {
            new DungeonResult(90) {            
                @Override
                public void run(CompContextBean context)
                {
                    rollTreasures(context, "plusgood");
                }
            },
    });
    
    private static final DungeonResults DOUBLE_PLUS_GOOD = new DungeonResults(new DungeonResult[] {
            new DungeonResult(90) {            
                @Override
                public void run(CompContextBean context)
                {
                    rollTreasures(context, "doubleplusgood");
                }
            },
    });
    
    private static final DungeonResults BAD = new DungeonResults(new DungeonResult[] {
            new DungeonResult(90) {            
                @Override
                public void run(CompContextBean context)
                {
                    rollTreasures(context, "bad");
                }
            },
    });
    
    private static final DungeonResults PLUS_BAD = new DungeonResults(new DungeonResult[] {
            new DungeonResult(5) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean comp = getCompanion(context);
                    context.addMessage(CompanionsModelConst.TEXT_CHEST_FIRE);
                    EffectCompanionLogic.effect(context, comp, 
                            new CompEffectTypeBean(CompEffectTypeBean.DAMAGE, "fire:2d6", CompEffectTypeBean.INSTANTANEOUS, null));
                }
            },
            new DungeonResult(5) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean comp = getCompanion(context);
                    context.addMessage(CompanionsModelConst.TEXT_CHEST_COLD);
                    EffectCompanionLogic.effect(context, comp, 
                            new CompEffectTypeBean(CompEffectTypeBean.DAMAGE, "cold:2d6", CompEffectTypeBean.INSTANTANEOUS, null));
                }
            },
            new DungeonResult(5) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean comp = getCompanion(context);
                    context.addMessage(CompanionsModelConst.TEXT_CHEST_LIGHTNING);
                    EffectCompanionLogic.effect(context, comp, 
                            new CompEffectTypeBean(CompEffectTypeBean.DAMAGE, "electricity:2d6", CompEffectTypeBean.INSTANTANEOUS, null));
                }
            },
            new DungeonResult(5) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean comp = getCompanion(context);
                    context.addMessage(CompanionsModelConst.TEXT_CHEST_POISON);
                    EffectCompanionLogic.effect(context, comp, 
                            new CompEffectTypeBean(CompEffectTypeBean.DAMAGE, "poison:2d6", CompEffectTypeBean.INSTANTANEOUS, null));
                }
            },
            new DungeonResult(5) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean comp = getCompanion(context);
                    context.addMessage(CompanionsModelConst.TEXT_CHEST_ACID);
                    EffectCompanionLogic.effect(context, comp, 
                            new CompEffectTypeBean(CompEffectTypeBean.DAMAGE, "acid:2d6", CompEffectTypeBean.INSTANTANEOUS, null));
                }
            },
    });
    
    private static final DungeonResults DOUBLE_PLUS_BAD = new DungeonResults(new DungeonResult[] {
            new DungeonResult(5) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean comp = getCompanion(context);
                    context.addMessage(CompanionsModelConst.TEXT_CHEST_FIRE);
                    EffectCompanionLogic.effect(context, comp, 
                            new CompEffectTypeBean(CompEffectTypeBean.DAMAGE, "fire:4d6", CompEffectTypeBean.INSTANTANEOUS, null));
                }
            },
            new DungeonResult(5) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean comp = getCompanion(context);
                    context.addMessage(CompanionsModelConst.TEXT_CHEST_COLD);
                    EffectCompanionLogic.effect(context, comp, 
                            new CompEffectTypeBean(CompEffectTypeBean.DAMAGE, "cold:4d6", CompEffectTypeBean.INSTANTANEOUS, null));
                }
            },
            new DungeonResult(5) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean comp = getCompanion(context);
                    context.addMessage(CompanionsModelConst.TEXT_CHEST_LIGHTNING);
                    EffectCompanionLogic.effect(context, comp, 
                            new CompEffectTypeBean(CompEffectTypeBean.DAMAGE, "electricity:4d6", CompEffectTypeBean.INSTANTANEOUS, null));
                }
            },
            new DungeonResult(5) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean comp = getCompanion(context);
                    context.addMessage(CompanionsModelConst.TEXT_CHEST_POISON);
                    EffectCompanionLogic.effect(context, comp, 
                            new CompEffectTypeBean(CompEffectTypeBean.DAMAGE, "poison:4d6", CompEffectTypeBean.INSTANTANEOUS, null));
                }
            },
            new DungeonResult(5) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean comp = getCompanion(context);
                    context.addMessage(CompanionsModelConst.TEXT_CHEST_ACID);
                    EffectCompanionLogic.effect(context, comp, 
                            new CompEffectTypeBean(CompEffectTypeBean.DAMAGE, "acid:4d6", CompEffectTypeBean.INSTANTANEOUS, null));
                }
            },
    });
}
