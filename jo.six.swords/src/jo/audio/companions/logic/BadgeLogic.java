package jo.audio.companions.logic;

import org.json.simple.JSONObject;

import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.logic.feature.dungeon.DungeonCommandLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.ResponseUtils;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.obj.IntegerUtils;

public class BadgeLogic
{

    public static void updateBadges(CompContextBean context, CompUserBean user)
    {
        BadgeLogic.addProgressiveTags(context, user, user.getVisitList(), "visit");
        BadgeLogic.addProgressiveTags(context, user, (int)user.getMaxDistance()/5, "ranger");
        BadgeLogic.addProgressiveTags(context, user, (int)(user.getTotalDistance()/40), "traveller");
        BadgeLogic.addProgressiveTags(context, user, (int)(user.getMaxGoldPieces()/6000), "banker");
        if (user.getMetadata() != null)
        {
            for (String key : user.getMetadata().keySet())
                if (key.startsWith("PRESTIGE_"))
                {
                    String god = key.substring(9);
                    BadgeLogic.addProgressiveTags(context, user, IntegerUtils.parseInt(user.getMetadata().get(key)), "prestige", "{{A_GOD#"+god+"}}");
                }
                else if (key.startsWith("GOD_"))
                {
                    String god = key.substring(4);
                    BadgeLogic.addProgressiveTags(context, user, IntegerUtils.parseInt(user.getMetadata().get(key)), "blessing", "{{A_GOD#"+god+"}}");
                }
                else if (key.equals(DungeonCommandLogic.LIST_CHEST))
                    BadgeLogic.addProgressiveTags(context, user, user.getMetadata().getString(key), "chest");
                else if (key.equals(DungeonCommandLogic.LIST_POOL))
                    BadgeLogic.addProgressiveTags(context, user, user.getMetadata().getString(key), "pool");
                else if (key.equals(DungeonCommandLogic.LIST_THRONE))
                    BadgeLogic.addProgressiveTags(context, user, user.getMetadata().getString(key), "throne");
            JSONObject kills = (JSONObject)user.getMetadata().get("kills");
            if (kills != null)
            {
                JSONObject monsters = (JSONObject)kills.get("monster");
                if (monsters != null)
                    for (String id : monsters.keySet())
                    {
                        CompMonsterTypeBean monster = MonsterLogic.getMonsterType(id);
                        if (monster != null)
                        {
                            int killed = IntegerUtils.parseInt(monsters.get(id));
                            BadgeLogic.addProgressiveTags(context, user, killed/5, "slayer", monster.getName());
                        }
                    }
                JSONObject alignment = (JSONObject)kills.get("alignment");
                if (alignment != null)
                    for (String id : alignment.keySet())
                    {
                        if (!"neutral".equalsIgnoreCase(id))
                        {
                            int killed = IntegerUtils.parseInt(alignment.get(id));
                            BadgeLogic.addProgressiveTags(context, user, killed/5/5, "slayer", id);
                        }
                    }
                JSONObject type = (JSONObject)kills.get("type");
                if (type != null)
                    for (String id : type.keySet())
                    {
                        if (!"other".equalsIgnoreCase(id))
                        {
                            int killed = IntegerUtils.parseInt(type.get(id));
                            BadgeLogic.addProgressiveTags(context, user, killed/5/5, "slayer", id);
                        }
                    }
            }
        }
        if ((context.getRoom() != null) && (context.getRoomParam("z") != null))
            BadgeLogic.addItemizedProgressiveTags(context, user, IntegerUtils.parseInt(context.getRoomParam("z"))/5, "delver",
                    1, 4, 8, 12);
    }

    private static void addProgressiveTags(CompContextBean context, CompUserBean user, String list, String badge)
    {
        int count = ResponseUtils.countList(list);
        addProgressiveTags(context, user, count, badge);
    }

    private static void addProgressiveTags(CompContextBean context, CompUserBean user, int count, String badge, Object... args)
    {
        addItemizedProgressiveTags(context, user, count, badge, 5, 25, 125, 575, args);
    }

    private static void addItemizedProgressiveTags(CompContextBean context, CompUserBean user, int count, String badge, int level1, int level2, int level3, int level4, Object... args)
    {
        if (count >= level1)
        {
            if (addTag(user, badge+"Bronze"))
                context.addMessage(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_ACHIEVED_A_XXX, new AudioMessageBean(badge+"_BRONZE", args)));
            if (count >= level2)
            {
                if (addTag(user, badge+"Silver"))
                    context.addMessage(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_ACHIEVED_A_XXX, new AudioMessageBean(badge+"_SILVER", args)));
                if (count >= level3)
                {
                    if (addTag(user, badge+"Gold"))
                        context.addMessage(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_ACHIEVED_A_XXX, new AudioMessageBean(badge+"_GOLD", args)));
                    if (count >= level4)
                    {
                        if (addTag(user, badge+"Platinum"))
                            context.addMessage(new AudioMessageBean(CompanionsModelConst.TEXT_YOU_HAVE_ACHIEVED_A_XXX, new AudioMessageBean(badge+"_PLATINUM", args)));
                    }        
                }        
            }        
        }        
    }
    
    private static boolean addTag(CompUserBean user, String badge)
    {
        if (user.getTags() == null)
        {
            user.setTags(badge);
            return true;
        }
        else if (user.getTags().indexOf(badge) < 0)
        {
            user.setTags(user.getTags() + " " + badge);
            return true;
        }
        return false;
    }
    
//    private static boolean removeTag(CompUserBean user, String badge)
//    {
//        if (user.getTags() == null)
//            return false;
//        int o = user.getTags().indexOf(badge);
//        if (o < 0)
//            return false;
//        user.setTags(user.getTags().substring(0, o).trim() + " " + user.getTags().substring(o + badge.length()).trim());
//        return true;
//    }

}
