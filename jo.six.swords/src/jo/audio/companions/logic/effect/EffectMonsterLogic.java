package jo.audio.companions.logic.effect;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompEffectInstanceBean;
import jo.audio.companions.data.CompEffectTypeBean;
import jo.audio.companions.data.CompMonsterInstanceBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FightDetails;
import jo.audio.companions.logic.ExperienceLogic;
import jo.audio.companions.logic.MonsterLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.IntegerUtils;

public class EffectMonsterLogic
{

    public static void effect(FightDetails fight, CompCompanionBean player, CompMonsterInstanceBean target,
            CompEffectTypeBean effect)
    {
        DebugUtils.trace("Applying effect from "+player.getName()+" to "+target.getID()+" effect="+effect.toJSON().toJSONString());
        switch (effect.getID())
        {
            case CompEffectTypeBean.DAMAGE:
                performDamage(fight, player, target, effect);
                break;
            case CompEffectTypeBean.SUMMON:
                performSummon(fight, player, target, effect);
                break;
            case CompEffectTypeBean.SOUND:
                performSound(fight, player, target, effect);
                break;
            default:
                throw new IllegalStateException("Unhandled effect: "+effect.getID());
        }
    }

    private static void performSound(FightDetails fight, CompCompanionBean player, CompMonsterInstanceBean target,
            CompEffectTypeBean effect)
    {
        if (effect.getMetadata().containsKey("chance"))
        {
            int chance = IntegerUtils.parseInt(effect.getMetadata().get("chance"));
            if (BaseUserState.RND.nextInt(100) > chance)
                return;
        }
        AudioMessageBean msg = new AudioMessageBean(effect.getMetadata());
        fight.responses.add(msg);
    }

    private static void performSummon(FightDetails fight, CompCompanionBean player, CompMonsterInstanceBean target,
            CompEffectTypeBean effect)
    {
        String id = effect.getSubType();
        if (effect.getMetadata().containsKey("chance"))
        {
            int chance = IntegerUtils.parseInt(effect.getMetadata().get("chance"));
            if (BaseUserState.RND.nextInt(100) > chance)
                return;
        }
        CompMonsterInstanceBean monster = MonsterLogic.createInstance(fight.context.getUser(), id);
        fight.context.getUser().getEncounter().getAllies().add(monster);
        fight.responses.add(new AudioMessageBean(
                CompanionsModelConst.TEXT_XXX_SUMMONS_A_YYY,
                player.getName(),
                monster.getType().getName()
                ));
    }

    private static void performDamage(FightDetails fight, CompCompanionBean player, CompMonsterInstanceBean target,
            CompEffectTypeBean effect)
    {
        String type = effect.getSubType();
        String dam = "1d6";
        int o = type.indexOf(":");
        if (o >= 0)
        {
            dam = type.substring(o +1);
            type = type.substring(0, o);
        }
        if (target.getType().isSpecial("immune-"+type))
            return;
        if (effect.getDurationType() != CompEffectTypeBean.INSTANTANEOUS)
        {
            CompEffectInstanceBean inst = new CompEffectInstanceBean(effect, fight.context.getUser(), BaseUserState.RND);
            target.getEffects().add(inst);
        }
        else
        {
            DiceRollBean roll = new DiceRollBean(dam);
            int damage = roll.roll(BaseUserState.RND);
            if (target.getType().isSpecial("halfdam-"+type) && (damage > 1))
                damage /= 2;
            target.setHitPoints(target.getHitPoints() - damage);
            if (target.getHitPoints() > 0)
            {
                fight.responses.add(new AudioMessageBean(
                        CompanionsModelConst.TEXT_XXX_IS_YYY_FOR_ZZZ_DAMAGE,
                        target.getType().getName(), "{{VERB_"+type+"}}",
                        damage
                        ));
                if (player != null)
                {
                    int xp = ExperienceLogic.addXP(fight.responses, player, target, damage);
                    fight.context.getUser().getEncounter().setXP(xp + fight.context.getUser().getEncounter().getXP());
                }
            }
            else
            {
                fight.responses.add(new AudioMessageBean(
                        CompanionsModelConst.TEXT_XXX_IS_YYY_AND_DIES,
                        target.getType().getName(), "{{VERB_"+type+"}}"
                        ));
                fight.context.getUser().getEncounter().getMonsters()
                        .remove(target);
                fight.monsterFirstRank.remove(target);
                fight.monsterSecondRank.remove(target);
                if (player != null)
                {
                    int xp = ExperienceLogic.addXP(fight.responses, player, target, damage + target.getHitPoints());
                    fight.context.getUser().getEncounter().setXP(xp + fight.context.getUser().getEncounter().getXP());
                }
                fight.context.getUser().setKillList(ResponseUtils.addToList(fight.context.getUser().getKillList(), target.getType().getName()));
                fight.context.getUser().setTotalKills(fight.context.getUser().getTotalKills() + 1);
            }
        }
    }
}
