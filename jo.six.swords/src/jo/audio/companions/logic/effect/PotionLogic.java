package jo.audio.companions.logic.effect;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompEffectTypeBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.logic.CompIOLogic;
import jo.audio.companions.logic.UserLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.util.utils.DebugUtils;

public class PotionLogic
{

    public static void equipPotion(CompContextBean context, String itemID,
            int amnt, CompUserBean user, CompCompanionBean comp,
            CompItemInstanceBean item)
    {
        DebugUtils.trace("PotionLogic.equipPotion(itemID="+itemID+", amnt="+amnt+", comp="+comp.getName());
        for (CompEffectTypeBean e : item.getBaseType().getEffects())
        {
            String effect = e.getID();
            switch (effect.toLowerCase())
            {
                case CompEffectTypeBean.HEALING:
                case CompEffectTypeBean.EXTRA_HEALING:
                    if (comp.getCurrentHitPoints() >= comp.getEffectiveHitPoints())
                    {
                        context.addMessage(CompanionsModelConst.TEXT_XXX_IS_ALREADY_ON_FULL_HIT_POINTS, comp.getName());
                        return;
                    }
                    UserLogic.doRemoveItem(user, itemID, amnt);
                    EffectCompanionLogic.effect(context, comp, e);
                    CompIOLogic.logUse(user, item.getID(), amnt);
                    break;
                case CompEffectTypeBean.CONTROL:
                case CompEffectTypeBean.STRENGTH_SET:
                case CompEffectTypeBean.INVISIBILITY:
                case CompEffectTypeBean.INVULNERABILITY:
                case CompEffectTypeBean.RESIST:
                case CompEffectTypeBean.SPEED:
                    UserLogic.doRemoveItem(user, itemID, amnt);
                    EffectCompanionLogic.effect(context, comp, e);
                    CompIOLogic.logUse(user, item.getID(), amnt);
                    break;
                case CompEffectTypeBean.CLAIRAUDIENCE:
                    UserLogic.doRemoveItem(user, itemID, amnt);
                    doClairaudience(context, user, item);
                    CompIOLogic.logUse(user, item.getID(), amnt);
                    break;
                default:
                    throw new IllegalStateException("Unhandled potion effect '"+effect+"'");
            }
        }
    }

    private static final String[] VOICES = {
            "Raveena",
            "Nicole",
            "Russell",
            "Amy",
            "Brian",
            "Emma",
            "Ivy",
            "Joanna",
            "Joey",
            "Justin",
            "Kendra",
            "Kimberly",
            "Matthew",
            "Salli",
            "Geraint",
    };
    
    private static void doClairaudience(CompContextBean context, CompUserBean user, CompItemInstanceBean item)
    {
        user.setTaleStreamStyle("voice: "+VOICES[BaseUserState.RND.nextInt(VOICES.length)]);
        user.setTaleStreamUntil(user.getInteractions() + 5);
        CompIOLogic.saveUser(user);
        context.addMessage(CompanionsModelConst.TEXT_EVERYTHING_STARTS_TO_SOUND_DIFFERENT);
    }
}
