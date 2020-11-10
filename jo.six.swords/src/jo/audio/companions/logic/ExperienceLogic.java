package jo.audio.companions.logic;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompMonsterInstanceBean;
import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.ICombatant;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.obj.IntegerUtils;

public class ExperienceLogic
{

    public static int addXP(List<Object> responses, CompCompanionBean player,
            CompMonsterInstanceBean target, int damage)
    {
        int xp = target.getType().getExperiencePerHP()*damage;
        if (target.getHitPoints() <= 0)
            xp += target.getType().getExperienceBase();
        return addXP(responses, player, xp);
    }

    public static int addXP(List<Object> responses, CompCompanionBean player,
            int xp)
    {
        if (CompanionLogic.isXPBonus(player))
            xp += xp/10;
        int xpBefore = player.getExperiencePoints();
        int xpAfter = xpBefore+xp;
        player.setExperiencePoints(xpAfter);
        if (CompanionLogic.levelForXP(player, -1) > player.getLevel())
            goUpLevel(responses, player);
        else
        {
            float levelBefore = CompanionLogic.analogLevelForXP(player, xpBefore);
            float levelAfter = CompanionLogic.analogLevelForXP(player, xpAfter);
            int quarterBefore = (int)(levelBefore*4);
            int quarterAfter = (int)(levelAfter*4);
            //DebugUtils.trace("XP Increase from "+xpBefore+" to "+xpAfter+", level from "+levelBefore+" to "+levelAfter
            //        +", quarter from "+quarterBefore+" to "+quarterAfter);
            if (quarterBefore != quarterAfter)
            {
                responses.add(new AudioMessageBean(CompanionsModelConst.TEXT_LEVEL_QUARTER));
                if ((quarterAfter%4) == 1)
                    responses.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_NOW_ONE_QUARTER_THE_WAY_TO_LEVEL_YYY,
                        player.getName(), player.getLevel()+1));
                else if ((quarterAfter%4) == 2)
                    responses.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_NOW_HALFWAY_TO_LEVEL_YYY,
                        player.getName(), player.getLevel()+1));
                else if ((quarterAfter%4) == 3)
                    responses.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_IS_NOW_THREE_QUARTERS_THE_WAY_TO_LEVEL_YYY,
                        player.getName(), player.getLevel()+1));
            }
        }
        return xp;
    }

    public static int addXP(CompContextBean context, int amount)
    {
        int xpPerCompanion = amount/context.getUser().getCompanions().size();
        for (CompCompanionBean comp : context.getUser().getCompanions())
        {
            List<Object> msgsTmp = new ArrayList<>();
            addXP(msgsTmp, comp, xpPerCompanion);
            for (Object msg : msgsTmp)
                context.getMessages().add((AudioMessageBean)msg);
        }
        return amount;
    }
    
    private static void goUpLevel(List<Object> responses, CompCompanionBean player)
    {
        responses.add(new AudioMessageBean(CompanionsModelConst.TEXT_LEVEL_UP));
        responses.add(new AudioMessageBean(CompanionsModelConst.TEXT_XXX_GOES_UP_A_LEVEL, player.getName()));
        player.setLevel(player.getLevel() + 1);
        DiceRollBean levelhp = CompanionLogic.getHPForLevel(player);
        int hp = levelhp.roll(BaseUserState.RND);
        player.setHitPoints(player.getHitPoints() + hp);
        player.setCurrentHitPoints(player.getCurrentHitPoints() + hp);
    }

    public static void addGold(CompUserBean user, float gold)
    {
        user.setGoldPieces(user.getGoldPieces() + gold);
        if (user.getGoldPieces() > user.getMaxGoldPieces())
            user.setMaxGoldPieces(user.getGoldPieces());
    }

    public static void registerKill(CompContextBean context, ICombatant target)
    {
        if (target instanceof CompMonsterInstanceBean)
        {
            CompMonsterTypeBean monster = ((CompMonsterInstanceBean)target).getType();
            String id = monster.getID();
            int o = id.indexOf('$');
            if (o > 0)
                id = id.substring(0, o);
            JSONObject kills = (JSONObject)context.getUser().getMetadata().get("kills");
            if (kills == null)
            {
                kills = new JSONObject();
                context.getUser().getMetadata().put("kills", kills);
            }
            registerTypeKill(kills, "monster", id);
            registerTypeKill(kills, "alignment", monster.getAlignment());
            registerTypeKill(kills, "type", monster.getType());
        }
    }
    
    private static void registerTypeKill(JSONObject kills, String type, String id)
    {
        JSONObject types = (JSONObject)kills.get(type);
        if (types == null)
        {
            types = new JSONObject();
            kills.put(type, types);
        }
        int num = IntegerUtils.parseInt(types.get(id));
        num++;
        types.put(id, num);
    }

}
