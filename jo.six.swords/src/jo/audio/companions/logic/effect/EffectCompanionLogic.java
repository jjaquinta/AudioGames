package jo.audio.companions.logic.effect;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompEffectInstanceBean;
import jo.audio.companions.data.CompEffectTypeBean;
import jo.audio.companions.data.CompMonsterInstanceBean;
import jo.audio.companions.data.DamageRollBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.FightDetails;
import jo.audio.companions.logic.UserLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.IntegerUtils;

public class EffectCompanionLogic
{
    private static final Map<String,CompEffectTypeBean> mCannedEffects = new HashMap<>();
    static
    {
        mCannedEffects.put("level_drain", new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"level_drain\",\"subType\":\"1\",\"durationType\":4}")));
        mCannedEffects.put("level_drain2", new CompEffectTypeBean(JSONUtils.readJSONString("{\"ID\":\"level_drain\",\"subType\":\"2\",\"durationType\":4}")));
    }
    
    public static void effect(CompContextBean context, CompCompanionBean comp,
            CompEffectTypeBean effect)
    {
        switch (effect.getID())
        {
            case CompEffectTypeBean.HEALING:
            case CompEffectTypeBean.EXTRA_HEALING:
                performHeal(context, comp, effect);
                break;
            case CompEffectTypeBean.CONTROL:
                performControl(context, comp, effect);
                break;
            case CompEffectTypeBean.RESIST:
                performResist(context, comp, effect);
                break;
            case CompEffectTypeBean.INVULNERABILITY:
                performInvulnerable(context, comp, effect);
                break;
            case CompEffectTypeBean.INVISIBILITY:
                performInvisibility(context, comp, effect);
                break;
            case CompEffectTypeBean.SPEED:
                performSpeed(context, comp, effect);
                break;
            case CompEffectTypeBean.STRENGTH_SET:
            case CompEffectTypeBean.STRENGTH_PLUS:
                performStr(context, comp, effect);
                break;
            case CompEffectTypeBean.INTELLIGENCE_SET:
            case CompEffectTypeBean.INTELLIGENCE_PLUS:
                performInt(context, comp, effect);
                break;
            case CompEffectTypeBean.WISDOM_SET:
            case CompEffectTypeBean.WISDOM_PLUS:
                performWis(context, comp, effect);
                break;
            case CompEffectTypeBean.CONSTITUTION_SET:
            case CompEffectTypeBean.CONSTITUTION_PLUS:
                performCon(context, comp, effect);
                break;
            case CompEffectTypeBean.DEXTERITY_SET:
            case CompEffectTypeBean.DEXTERITY_PLUS:
                performDex(context, comp, effect);
                break;
            case CompEffectTypeBean.CHARISMA_SET:
            case CompEffectTypeBean.CHARISMA_PLUS:
                performCha(context, comp, effect);
                break;
            case CompEffectTypeBean.LEVEL_DRAIN:
                performLevelDrain(context, comp, effect);
                break;
            case CompEffectTypeBean.DAMAGE:
                performDamage(context, comp, effect);
                break;
            default:
                throw new IllegalStateException("Unhandled effect: "+effect.getID());
        }
        if (effect.getDurationType() == CompEffectTypeBean.PERMANENT)
            context.addMessage(CompanionsModelConst.TEXT_IT_FEELS_PERMANENT);
    }

    private static void performDamage(CompContextBean context,
            CompCompanionBean comp, CompEffectTypeBean effect)
    {
        String st = effect.getSubType();
        int o = st.indexOf(':');
        String type = st.substring(0, o);
        DamageRollBean damRoll = new DamageRollBean(st.substring(o + 1));
        damRoll.getParams().put("damageType", type);
        int damage = damRoll.roll(BaseUserState.RND);
        damage = modifyDamage(comp, null, damRoll, damage);
        DebugUtils.trace(comp.getName()+" hit for (" + type + ") " + damage + " damage");
        comp.setCurrentHitPoints(comp.getCurrentHitPoints() - damage);
        context.addMessage(CompanionsModelConst.TEXT_XXX_IS_YYY_FOR_ZZZ_DAMAGE,
                comp.getName(), new AudioMessageBean("VERB_"+type), damage);
        DebugUtils.trace("Left with " + comp.getCurrentHitPoints()+ " hit points");
        if (comp.getCurrentHitPoints() <= -10)
        {
            DebugUtils.trace(comp.getName()+" dies");
            context.addMessage(CompanionsModelConst.TEXT_XXX_DIES, comp.getName());
            FightDetails.doPlayerKilled(context, comp);
        }
        else if (comp.getCurrentHitPoints() <= 0)
        {
            DebugUtils.trace(comp.getName()+" knocked out");
            context.addMessage(CompanionsModelConst.TEXT_XXX_IS_KNOCKED_OUT, comp.getName());
            FightDetails.doPlayerKnockedOut(context, comp);
        }
    }

    private static void performLevelDrain(CompContextBean context,
            CompCompanionBean comp, CompEffectTypeBean effect)
    {
        int levels = IntegerUtils.parseInt(effect.getSubType());
        CompEffectInstanceBean inst = new CompEffectInstanceBean(effect, context.getUser(), BaseUserState.RND);
        comp.getEffects().add(inst);
        if (comp.getCurrentHitPoints() > comp.getEffectiveHitPoints())
            comp.setCurrentHitPoints(comp.getEffectiveHitPoints());
        if (levels == 1)
            context.addMessage(CompanionsModelConst.TEXT_XXX_LOOSES_A_LEVEL, comp.getName());
        else
            context.addMessage(CompanionsModelConst.TEXT_XXX_LOOSES_YYY_LEVELS, comp.getName(), levels);
    }

    private static void performStr(CompContextBean context,
            CompCompanionBean comp, CompEffectTypeBean effect)
    {
        int before = comp.getSTRModified();
        CompEffectInstanceBean inst = new CompEffectInstanceBean(effect, context.getUser(), BaseUserState.RND);
        comp.getEffects().add(inst);
        if (comp.getSTRModified() > before)
            context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_STRONGER, comp.getName());
        else
            context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_WEAKER, comp.getName());
    }

    private static void performInt(CompContextBean context,
            CompCompanionBean comp, CompEffectTypeBean effect)
    {
        int before = comp.getINTModified();
        CompEffectInstanceBean inst = new CompEffectInstanceBean(effect, context.getUser(), BaseUserState.RND);
        comp.getEffects().add(inst);
        if (comp.getINTModified() > before)
            context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_SMARTER, comp.getName());
        else
            context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_DUMBER, comp.getName());
    }

    private static void performWis(CompContextBean context,
            CompCompanionBean comp, CompEffectTypeBean effect)
    {
        int before = comp.getWISModified();
        CompEffectInstanceBean inst = new CompEffectInstanceBean(effect, context.getUser(), BaseUserState.RND);
        comp.getEffects().add(inst);
        if (comp.getWISModified() > before)
            context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_WISER, comp.getName());
        else
            context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_FOOLISH, comp.getName());
    }

    private static void performCon(CompContextBean context,
            CompCompanionBean comp, CompEffectTypeBean effect)
    {
        int before = comp.getCONModified();
        CompEffectInstanceBean inst = new CompEffectInstanceBean(effect, context.getUser(), BaseUserState.RND);
        comp.getEffects().add(inst);
        if (comp.getCONModified() > before)
            context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_MORE_ROBUST, comp.getName());
        else
            context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_INFIRM, comp.getName());
    }

    private static void performDex(CompContextBean context,
            CompCompanionBean comp, CompEffectTypeBean effect)
    {
        int before = comp.getDEXModified();
        CompEffectInstanceBean inst = new CompEffectInstanceBean(effect, context.getUser(), BaseUserState.RND);
        comp.getEffects().add(inst);
        if (comp.getDEXModified() > before)
            context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_MORE_AGILE, comp.getName());
        else
            context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_CLUMSY, comp.getName());
    }

    private static void performCha(CompContextBean context,
            CompCompanionBean comp, CompEffectTypeBean effect)
    {
        int before = comp.getCHAModified();
        CompEffectInstanceBean inst = new CompEffectInstanceBean(effect, context.getUser(), BaseUserState.RND);
        comp.getEffects().add(inst);
        if (comp.getCHAModified() > before)
            context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_MORE_CONFIDENT, comp.getName());
        else
            context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_LESS_CONFIDENT, comp.getName());
    }

    private static void performControl(CompContextBean context,
            CompCompanionBean comp, CompEffectTypeBean effect)
    {
        CompEffectInstanceBean inst = new CompEffectInstanceBean(effect, context.getUser(), BaseUserState.RND);
        comp.getEffects().add(inst);
        context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_IN_CONTROL_OF_YYY,
                comp.getName(), "{{MONSTER_TYPE_PLURAL_"+effect.getSubType().toUpperCase()+"}}");
    }

    private static void performResist(CompContextBean context,
            CompCompanionBean comp, CompEffectTypeBean effect)
    {
        CompEffectInstanceBean inst = new CompEffectInstanceBean(effect, context.getUser(), BaseUserState.RND);
        comp.getEffects().add(inst);
        context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_RESISTANT_TO_YYY,
                comp.getName(), "{{RESISTANCE_TYPE_"+effect.getSubType().toUpperCase()+"}}");
    }

    private static void performInvulnerable(CompContextBean context,
            CompCompanionBean comp, CompEffectTypeBean effect)
    {
        CompEffectInstanceBean inst = new CompEffectInstanceBean(effect, context.getUser(), BaseUserState.RND);
        comp.getEffects().add(inst);
        context.addMessage(CompanionsModelConst.TEXT_XXX_FEELS_INVULNERABLE,
                comp.getName());
    }

    private static void performInvisibility(CompContextBean context,
            CompCompanionBean comp, CompEffectTypeBean effect)
    {
        CompEffectInstanceBean inst = new CompEffectInstanceBean(effect, context.getUser(), BaseUserState.RND);
        comp.getEffects().add(inst);
        context.addMessage(CompanionsModelConst.TEXT_XXX_FADES_FROM_VIEW,
                comp.getName());
    }

    private static void performSpeed(CompContextBean context,
            CompCompanionBean comp, CompEffectTypeBean effect)
    {
        CompEffectInstanceBean inst = new CompEffectInstanceBean(effect, context.getUser(), BaseUserState.RND);
        comp.getEffects().add(inst);
        context.addMessage(CompanionsModelConst.TEXT_XXX_SPEEDS_UP,
                comp.getName());
    }

    public static void performHeal(CompContextBean context,
            CompCompanionBean comp, CompEffectTypeBean effect)
    {
        int heal = 0;
        if (CompEffectTypeBean.HEALING.equals(effect.getID()))
            heal = DiceRollBean.roll(BaseUserState.RND, 2, 4, 2);
        else if (CompEffectTypeBean.EXTRA_HEALING.equals(effect.getID()))
            heal = DiceRollBean.roll(BaseUserState.RND, 2, 4, 2);
        else
            throw new IllegalStateException("Unknown potion effect "+effect.getID());
        UserLogic.healCompanion(context, context.getUser(), comp, heal);
    }

    public static int modifyDamage(CompCompanionBean comp,
            CompMonsterInstanceBean monster, DamageRollBean diceRoll,
            int damage)
    {
        String damageType = diceRoll.getParam("damageType");
        for (CompEffectInstanceBean e : comp.getEffects())
        {
            if (e.getID().equals(CompEffectTypeBean.RESIST) && (e.getSubType().equals(damageType)))
                damage /= 2;
            else if (e.getID().equals(CompEffectTypeBean.INVULNERABILITY) && (monster != null) && (monster.getType().getHDRoll().getHitDice().getNumber() < 4))
                damage = 0;
        }
        String saveType = diceRoll.getParam("saveForHalf");
        if (saveType != null)
        {
            int roll = BaseUserState.RND.nextInt(20) + 1;
            int target = comp.getSave(saveType);
            if (roll >= target)
                damage /= 2;
        }
        return damage;
    }

    public static void attackEffect(FightDetails fight,
            CompCompanionBean comp,
            DamageRollBean diceRoll)
    {
        String effect = diceRoll.getParam("effect");
        if (effect == null)
            return;
        if (!mCannedEffects.containsKey(effect))
        {
            DebugUtils.trace("Unrecognized effect: '"+effect+"'");
            return;
        }
        int mark = fight.context.getMessages().size();
        effect(fight.context, comp, mCannedEffects.get(effect));
        // reel back messages into fight queue
        while (fight.context.getMessages().size() > mark)
        {
            AudioMessageBean msg = fight.context.getMessages().get(mark);
            fight.context.getMessages().remove(mark);
            fight.responses.add(msg);
        }
    }
}
