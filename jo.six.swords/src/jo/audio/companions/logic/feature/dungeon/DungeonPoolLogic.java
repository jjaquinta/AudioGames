package jo.audio.companions.logic.feature.dungeon;

import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompEffectTypeBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.logic.CompIOLogic;
import jo.audio.companions.logic.UserLogic;
import jo.audio.companions.logic.effect.EffectCompanionLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.ResponseUtils;

public class DungeonPoolLogic
{
    static void doYesPool(CompContextBean context)
    {
        CompUserBean user = context.getUser();
        context.addMessage(CompanionsModelConst.TEXT_YOU_DRINK_FROM_THE_POOL);
        context.addMessage(CompanionsModelConst.TEXT_PAUSE);
        
        switch (DiceRollBean.roll(BaseUserState.RND, 2, 6))
        {
            case 4: // double plus good - 3 : 16
                DOUBLE_PLUS_GOOD.action(context);
                break;
            case 6: // plus good - 5 : 16
                PLUS_GOOD.action(context);
                break;
            case 3:
            case 5: // good - 6 : 16
                addMessage(context, CompanionsModelConst.TEXT_DRINK_GOOD);
                break;
            case 2:
            case 7: 
            case 12:    // nothing - 8 : 16
                addMessage(context, CompanionsModelConst.TEXT_DRINK_NOTHING_HAPPENS);
                break;
            case 9:
            case 10:    // bad - 6 : 16
                addMessage(context, CompanionsModelConst.TEXT_DRINK_BAD);
                break;
            case 8:     // plus bad - 5 : 16
                PLUS_BAD.action(context);
                break;
            case 11:    // double plus abd - 3 : 16
                DOUBLE_PLUS_BAD.action(context);
                break;
        }
        
        int roll = BaseUserState.RND.nextInt(4);
        if (roll == 0)
        {
            context.addMessage(CompanionsModelConst.TEXT_THE_POOL_DRIES_UP);
            String list = user.getMetadata().getString(DungeonCommandLogic.LIST_POOL);
            list = ResponseUtils.addToList(list, user.getLocation());
            user.getMetadata().put(DungeonCommandLogic.LIST_POOL, list);
        }
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

    private static void addMessage(CompContextBean context, String msg)
    {
        CompCompanionBean primary = getCompanion(context);
        CompCompanionBean secondary = primary;
        for (CompCompanionBean c : context.getUser().getCompanions())
            if (c != primary)
            {
                secondary = c;
                break;
            }
        context.addMessage(msg, primary, secondary);
    }
    
    private static CompEffectTypeBean TEMP_CTRL_ANIMAL = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"animal\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CTRL_DEMI   = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"demihuman\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CTRL_DEMON  = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"demon\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CTRL_DEVIL  = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"devil\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CTRL_DINO   = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"dinosaur\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CTRL_DRAGON = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"dragon\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CTRL_GIANT  = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"giant\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CTRL_GOLEM  = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"golem\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CTRL_HUMAN  = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"humanoid\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CTRL_LYCAN  = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"lycanthrope\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CTRL_SYLVAN = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"sylvan\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CTRL_UNDEAD = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"undead\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_STR19       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_set\",\"subType\":\"19\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_STR20       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_set\",\"subType\":\"20\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_STR21       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_set\",\"subType\":\"21\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_STR22       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_set\",\"subType\":\"22\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_STR23       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_set\",\"subType\":\"23\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_STR24       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_set\",\"subType\":\"24\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_STR25       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_set\",\"subType\":\"25\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_INT19       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"intelligence_set\",\"subType\":\"19\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_INT20       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"intelligence_set\",\"subType\":\"20\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_INT21       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"intelligence_set\",\"subType\":\"21\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_INT22       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"intelligence_set\",\"subType\":\"22\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_INT23       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"intelligence_set\",\"subType\":\"23\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_INT24       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"intelligence_set\",\"subType\":\"24\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_INT25       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"intelligence_set\",\"subType\":\"25\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_WIS19       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"wisdom_set\",\"subType\":\"19\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_WIS20       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"wisdom_set\",\"subType\":\"20\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_WIS21       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"wisdom_set\",\"subType\":\"21\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_WIS22       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"wisdom_set\",\"subType\":\"22\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_WIS23       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"wisdom_set\",\"subType\":\"23\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_WIS24       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"wisdom_set\",\"subType\":\"24\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_WIS25       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"wisdom_set\",\"subType\":\"25\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CON19       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"constitution_set\",\"subType\":\"19\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CON20       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"constitution_set\",\"subType\":\"20\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CON21       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"constitution_set\",\"subType\":\"21\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CON22       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"constitution_set\",\"subType\":\"22\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CON23       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"constitution_set\",\"subType\":\"23\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CON24       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"constitution_set\",\"subType\":\"24\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CON25       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"constitution_set\",\"subType\":\"25\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_DEX19       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"dexterity_set\",\"subType\":\"19\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_DEX20       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"dexterity_set\",\"subType\":\"20\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_DEX21       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"dexterity_set\",\"subType\":\"21\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_DEX22       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"dexterity_set\",\"subType\":\"22\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_DEX23       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"dexterity_set\",\"subType\":\"23\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_DEX24       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"dexterity_set\",\"subType\":\"24\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_DEX25       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"dexterity_set\",\"subType\":\"25\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CHA19       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"charisma_set\",\"subType\":\"19\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CHA20       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"charisma_set\",\"subType\":\"20\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CHA21       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"charisma_set\",\"subType\":\"21\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CHA22       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"charisma_set\",\"subType\":\"22\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CHA23       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"charisma_set\",\"subType\":\"23\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CHA24       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"charisma_set\",\"subType\":\"24\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CHA25       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"charisma_set\",\"subType\":\"25\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_REST_FIRE   = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"resist\",\"subType\":\"fire\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_REST_FRST   = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"resist\",\"subType\":\"frost\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_REST_ACID   = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"resist\",\"subType\":\"acid\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_REST_POI    = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"resist\",\"subType\":\"poison\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_REST_LITN   = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"resist\",\"subType\":\"lightning\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_INVISIBLE   = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"invisibility\",\"subType\":null,\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_INVULNER    = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"invulnerability\",\"subType\":null,\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_SPEED       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"speed\",\"subType\":null,\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_LEVEL_1     = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"level_drain\",\"subType\":\"1\",\"durationType\":0}"));
    private static CompEffectTypeBean TEMP_LEVEL_2     = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"level_drain\",\"subType\":\"1\",\"durationType\":0}"));
    private static CompEffectTypeBean PERM_CTRL_ANIMAL = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"animal\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_CTRL_DEMI   = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"demihuman\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_CTRL_DEMON  = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"demon\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_CTRL_DEVIL  = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"devil\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_CTRL_DINO   = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"dinosaur\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_CTRL_DRAGON = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"dragon\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_CTRL_GIANT  = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"giant\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_CTRL_GOLEM  = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"golem\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_CTRL_HUMAN  = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"humanoid\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_CTRL_LYCAN  = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"lycanthrope\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_CTRL_SYLVAN = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"sylvan\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_CTRL_UNDEAD = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"control\",\"subType\":\"undead\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_REST_FIRE   = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"resist\",\"subType\":\"frost\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_REST_FRST   = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"resist\",\"subType\":\"acid\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_REST_POI    = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"resist\",\"subType\":\"poison\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_REST_LITN   = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"resist\",\"subType\":\"lightning\",\"durationType\":4}"));
    private static CompEffectTypeBean PERM_INVISIBLE   = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"invisibility\",\"subType\":null,\"durationType\":4}"));
    private static CompEffectTypeBean PERM_INVULNER    = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"invulnerability\",\"subType\":null,\"durationType\":4}"));
    private static CompEffectTypeBean PERM_SPEED       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"speed\",\"subType\":null,\"durationType\":4}"));

    private static final DungeonResults PLUS_GOOD = new DungeonResults(new DungeonResult[] {
        new DungeonResult(6) {            
            @Override
            public void run(CompContextBean context)
            {
                CompCompanionBean active = getCompanion(context);
                if (!UserLogic.healCompanion(context, context.getUser(), active, 20))
                    addMessage(context, CompanionsModelConst.TEXT_XXX_FEELS_FULL_OF_HEALING_ENERGY);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                if (!UserLogic.fullyCureCompanions(context, context.getUser()))
                    addMessage(context, CompanionsModelConst.TEXT_XXX_FEELS_FULL_OF_HEALING_ENERGY);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INVISIBLE);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CTRL_ANIMAL);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CTRL_DEMI);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CTRL_DEMON);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CTRL_DEVIL);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CTRL_DINO);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CTRL_DRAGON);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CTRL_GIANT);
            }
        },
        new DungeonResult(1) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CTRL_GOLEM);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CTRL_HUMAN);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CTRL_LYCAN);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CTRL_SYLVAN);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CTRL_UNDEAD);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_STR19);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_STR20);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_STR21);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INT19);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INT20);
            }
        },
        new DungeonResult(1) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INT21);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_WIS19);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_WIS20);
            }
        },
        new DungeonResult(1) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_WIS21);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CON19);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CON20);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CON21);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_DEX19);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_DEX20);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_DEX21);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CHA19);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CHA20);
            }
        },
        new DungeonResult(1) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CHA21);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_REST_FIRE);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_REST_FRST);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_REST_ACID);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_REST_POI);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_REST_LITN);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INVISIBLE);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INVULNER);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_SPEED);
            }
        },
    });
    private static final DungeonResults DOUBLE_PLUS_GOOD = new DungeonResults(new DungeonResult[] {
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                CompCompanionBean active = getCompanion(context);
                active.setHitPoints(active.getHitPoints() + 1);
                addMessage(context, CompanionsModelConst.TEXT_XXX_GAINS_A_HIT_POINT);
            }
        },
        new DungeonResult(5) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_STR22);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_STR23);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_STR24);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_STR25);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INT22);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INT23);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INT24);
            }
        },
        new DungeonResult(1) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INT25);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_WIS22);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_WIS23);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_WIS24);
            }
        },
        new DungeonResult(1) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_WIS25);
            }
        },
        new DungeonResult(5) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CON22);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CON23);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CON24);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CON25);
            }
        },
        new DungeonResult(5) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_DEX22);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_DEX23);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_DEX24);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_DEX25);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CHA22);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CHA23);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CHA24);
            }
        },
        new DungeonResult(1) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CHA25);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_INVISIBLE);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_CTRL_ANIMAL);
            }
        },
        new DungeonResult(1) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_CTRL_DEMI);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_CTRL_DEMON);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_CTRL_DEVIL);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_CTRL_DINO);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_CTRL_DRAGON);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_CTRL_GIANT);
            }
        },
        new DungeonResult(1) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_CTRL_GOLEM);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_CTRL_HUMAN);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_CTRL_LYCAN);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_CTRL_SYLVAN);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_CTRL_UNDEAD);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_REST_FIRE);
            }
        },
        new DungeonResult(4) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_REST_FRST);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_REST_POI);
            }
        },
        new DungeonResult(2) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_REST_LITN);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_INVISIBLE);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_INVULNER);
            }
        },
        new DungeonResult(3) {            
            @Override
            public void run(CompContextBean context)
            {
                EffectCompanionLogic.effect(context, getCompanion(context), PERM_SPEED);
            }
        },
    });
    
    private static CompEffectTypeBean TEMP_STRM1       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-1\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_STRM2       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-2\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_STRM3       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-3\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_STRM4       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-4\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_STRM5       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-5\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_INTM1       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-1\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_INTM2       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-2\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_INTM3       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-3\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_INTM4       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-4\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_INTM5       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-5\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_WISM1       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-1\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_WISM2       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-2\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_WISM3       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-3\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_WISM4       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-4\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_WISM5       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-5\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CONM1       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-1\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CONM2       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-2\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CONM3       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-3\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CONM4       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-4\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CONM5       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-5\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_DEXM1       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-1\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_DEXM2       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-2\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_DEXM3       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-3\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_DEXM4       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-4\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_DEXM5       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-5\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CHAM1       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-1\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CHAM2       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-2\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CHAM3       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-3\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CHAM4       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-4\",\"durationType\":3,\"durationLength\":\"2d20\"}"));
    private static CompEffectTypeBean TEMP_CHAM5       = new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"strength_plus\",\"subType\":\"-5\",\"durationType\":3,\"durationLength\":\"2d20\"}"));

    private static final DungeonResults PLUS_BAD = new DungeonResults(new DungeonResult[] {
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_LEVEL_1);
                }
            },
            new DungeonResult(3) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_STRM1);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_STRM2);
                }
            },
            new DungeonResult(1) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_STRM3);
                }
            },
            new DungeonResult(4) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INTM1);
                }
            },
            new DungeonResult(3) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INTM2);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INTM3);
                }
            },
            new DungeonResult(4) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_WISM1);
                }
            },
            new DungeonResult(3) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_WISM2);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_WISM3);
                }
            },
            new DungeonResult(3) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CONM1);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CONM2);
                }
            },
            new DungeonResult(1) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CONM3);
                }
            },
            new DungeonResult(3) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_DEXM1);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_DEXM2);
                }
            },
            new DungeonResult(1) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_DEXM3);
                }
            },
            new DungeonResult(4) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CHAM1);
                }
            },
            new DungeonResult(3) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CHAM2);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CHAM3);
                }
            },
    });
    private static final DungeonResults DOUBLE_PLUS_BAD = new DungeonResults(new DungeonResult[] {
            new DungeonResult(1) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_LEVEL_2);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_STRM4);
                }
            },
            new DungeonResult(1) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_STRM5);
                }
            },
            new DungeonResult(3) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INTM4);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_INTM5);
                }
            },
            new DungeonResult(3) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_WISM4);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_WISM5);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CONM4);
                }
            },
            new DungeonResult(1) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CONM5);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_DEXM4);
                }
            },
            new DungeonResult(1) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_DEXM5);
                }
            },
            new DungeonResult(3) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CHAM4);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    EffectCompanionLogic.effect(context, getCompanion(context), TEMP_CHAM5);
                }
            },
    });
}
