package jo.audio.companions.logic.feature.dungeon;

import java.util.ArrayList;
import java.util.List;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.logic.CompIOLogic;
import jo.audio.companions.logic.MonsterLogic;
import jo.audio.companions.logic.UserLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;

public class DungeonThroneLogic
{
    static void doYesThrone(CompContextBean context)
    {
        CompUserBean user = context.getUser();
        context.addMessage(CompanionsModelConst.TEXT_YOU_SIT_IN_THE_THRONE);
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
                addMessage(context, CompanionsModelConst.TEXT_THRONE_GOOD);
                break;
            case 2:
            case 7: 
            case 12:    // nothing - 8 : 16
                addMessage(context, CompanionsModelConst.TEXT_THRONE_NOTHING_HAPPENS);
                break;
            case 9:
            case 10:    // bad - 6 : 16
                addMessage(context, CompanionsModelConst.TEXT_THRONE_BAD);
                break;
            case 8:     // plus bad - 5 : 16
                PLUS_BAD.action(context);
                break;
            case 11:    // double plus abd - 3 : 16
                DOUBLE_PLUS_BAD.action(context);
                break;
        }
        
        if (BaseUserState.RND.nextInt(3) == 0)
        {
            context.addMessage(CompanionsModelConst.TEXT_THE_THRONE_CRACKS);
            String list = user.getMetadata().getString(DungeonCommandLogic.LIST_THRONE);
            list = ResponseUtils.addToList(list, user.getLocation());
            user.getMetadata().put(DungeonCommandLogic.LIST_THRONE, list);
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

    private static final DungeonResults PLUS_GOOD = new DungeonResults(new DungeonResult[] {
            new DungeonResult(3) {            
                @Override
                public void run(CompContextBean context)
                {
                    if (!UserLogic.fullyCureCompanions(context, context.getUser()))
                        addMessage(context, CompanionsModelConst.TEXT_HEALING_ENERGY_RADIATES_FROM_XXX);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean active = getCompanion(context);
                    active.setINT(active.getINT() + 1);
                    addMessage(context, CompanionsModelConst.TEXT_XXX_FEELS_SMARTER);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean active = getCompanion(context);
                    active.setWIS(active.getWIS() + 1);
                    addMessage(context, CompanionsModelConst.TEXT_XXX_FEELS_WISER);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean active = getCompanion(context);
                    active.setCHA(active.getCHA() + 1);
                    addMessage(context, CompanionsModelConst.TEXT_XXX_FEELS_MORE_CONFIDENT);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    UserLogic.summonMonsterTactical(context,
                            MonsterLogic.getMonsterType("Goblin$1"), 4, "1000", null,
                            CompanionsModelConst.TEXT_A_GOBLIN_FALLS_FROM_THE_CEILING_CARRYING_A_SACK_OF_GOLD);
                }
            },
        });
    private static final DungeonResults DOUBLE_PLUS_GOOD = new DungeonResults(new DungeonResult[] {
            new DungeonResult(3) {            
                @Override
                public void run(CompContextBean context)
                {
                    for (CompCompanionBean comp : context.getUser().getCompanions())
                        comp.setHitPoints(comp.getHitPoints() + 1);
                    addMessage(context, CompanionsModelConst.TEXT_ALL_OF_YOUR_COMPANIONS_GAIN_A_HIT_POINT);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean active = getCompanion(context);
                    active.setSTR(active.getSTR() + 1);
                    addMessage(context, CompanionsModelConst.TEXT_XXX_FEELS_STRONGER);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean active = getCompanion(context);
                    active.setCON(active.getCON() + 1);
                    addMessage(context, CompanionsModelConst.TEXT_XXX_FEELS_MORE_ROBUST);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean active = getCompanion(context);
                    active.setDEX(active.getDEX() + 1);
                    addMessage(context, CompanionsModelConst.TEXT_XXX_FEELS_MORE_AGILE);
                }
            },
        });
    private static final DungeonResults PLUS_BAD = new DungeonResults(new DungeonResult[] {
            new DungeonResult(3) {            
                @Override
                public void run(CompContextBean context)
                {
                    List<String> hurt = new ArrayList<>();
                    for (CompCompanionBean comp : context.getUser().getCompanions())
                        if (comp.getCurrentHitPoints() > 10)
                        {
                            comp.setCurrentHitPoints(comp.getHitPoints() - 10);
                            hurt.add(comp.getName());
                        }
                    if (hurt.size() == 0)
                        addMessage(context, CompanionsModelConst.TEXT_THE_WHOLE_PARTY_FEELS_CHILL_FOR_A_MOMENT);
                    else if (hurt.size() == 1)
                        context.addMessage(CompanionsModelConst.TEXT_XXX_LOSES_TEN_HIT_POINTS, hurt.get(0));
                    else if (hurt.size() == 1)
                        context.addMessage(CompanionsModelConst.TEXT_XXX_LOSE_TEN_HIT_POINTS, new AudioMessageBean(AudioMessageBean.AND, hurt.toArray()));
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean active = getCompanion(context);
                    active.setINT(active.getINT() - 1);
                    addMessage(context, CompanionsModelConst.TEXT_XXX_FEELS_DUMBER);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean active = getCompanion(context);
                    active.setWIS(active.getWIS() - 1);
                    addMessage(context, CompanionsModelConst.TEXT_XXX_FEELS_FOOLISH);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean active = getCompanion(context);
                    active.setCHA(active.getCHA() - 1);
                    addMessage(context, CompanionsModelConst.TEXT_XXX_FEELS_LESS_CONFIDENT);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    UserLogic.summonMonsterTactical(context,
                            MonsterLogic.getMonsterType("Ghost$1"), 1, null, null,
                            CompanionsModelConst.TEXT_THE_GHOST_OF_A_LONG_DEAD_RULER_RISES_FROM_THE_THRONE);
                }
            },
        });
    private static final DungeonResults DOUBLE_PLUS_BAD = new DungeonResults(new DungeonResult[] {
            new DungeonResult(3) {            
                @Override
                public void run(CompContextBean context)
                {
                    List<String> hurt = new ArrayList<>();
                    for (CompCompanionBean comp : context.getUser().getCompanions())
                        if (comp.getHitPoints() > 10)
                        {
                            comp.setHitPoints(comp.getHitPoints() - 1);
                            hurt.add(comp.getName());
                        }
                    if (hurt.size() == 0)
                        addMessage(context, CompanionsModelConst.TEXT_THE_WHOLE_PARTY_FEELS_VERY_CHILL_FOR_A_MOMENT);
                    else if (hurt.size() == 1)
                        context.addMessage(CompanionsModelConst.TEXT_XXX_LOSES_ONE_HIT_POINT_PERMANENTLY, hurt.get(0));
                    else if (hurt.size() == 1)
                        context.addMessage(CompanionsModelConst.TEXT_XXX_LOSE_ONE_HIT_POINTS_PERMANENTLY, new AudioMessageBean(AudioMessageBean.AND, hurt.toArray()));
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean active = getCompanion(context);
                    active.setSTR(active.getSTR() - 1);
                    addMessage(context, CompanionsModelConst.TEXT_XXX_FEELS_WEAKER);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean active = getCompanion(context);
                    active.setCON(active.getCON() - 1);
                    addMessage(context, CompanionsModelConst.TEXT_XXX_FEELS_INFIRM);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    CompCompanionBean active = getCompanion(context);
                    active.setDEX(active.getDEX() - 1);
                    addMessage(context, CompanionsModelConst.TEXT_XXX_FEELS_CLUMSY);
                }
            },
            new DungeonResult(2) {            
                @Override
                public void run(CompContextBean context)
                {
                    UserLogic.summonMonsterTactical(context,
                            MonsterLogic.getMonsterType("Wraith$1"), 4, null, null,
                            CompanionsModelConst.TEXT_THE_GHOST_KINGS_COURT_IS_SUMMONED);
                }
            },
        });
}
