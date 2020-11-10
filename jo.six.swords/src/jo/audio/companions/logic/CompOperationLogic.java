package jo.audio.companions.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.json.simple.JSONObject;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompIdentBean;
import jo.audio.companions.data.CompOperationBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.GeoBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.gen.GypsyLogic;
import jo.audio.companions.logic.gen.MissionLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.BaseUserState;
import jo.audio.util.ToJSONLogic;
import jo.audio.util.model.data.AudioMessageBean;
import jo.util.utils.DebugUtils;
import jo.util.utils.obj.StringUtils;

public class CompOperationLogic
{
    private static List<CompUserBean> mMRU = new ArrayList<>();
    // base function
    
    public static CompContextBean operate(CompOperationBean op)
    {
        if (op.getOperation() == CompOperationBean.TEXT)
        {   // functional, no login
            CompContextBean context = new CompContextBean();
            context.setTextModel(TextLogic.getText());
            return context;
        }
        if (op.getOperation() == CompOperationBean.GETNEWS)
        {   // functional, no login
            CompContextBean context = new CompContextBean();
            context.setTextModel(NewsLogic.getNews());
            return context;
        }
        log(ToJSONLogic.toJSON(op).toJSONString());
        final CompContextBean context = new CompContextBean();
        initializeCompContext(op, context);
        switch (op.getOperation())
        {
            case CompOperationBean.QUERY:
                if (op.getStrParam1() != null)
                    if (op.getStrParam1().indexOf(CompOperationBean.RANKS) >= 0)
                        context.setRanks(RanksLogic.getRanks(context.getUser()));
                    else if (op.getStrParam1().indexOf(CompOperationBean.NEARBY) >= 0)
                        FeatureLogic.fillNearbyFeatures(context, (int)op.getNumParam1());
                break;
            case CompOperationBean.MOVE:
                UserLogic.move(context, (int)op.getNumParam1());
                break;
            case CompOperationBean.ACTIVATE:
                UserLogic.activate(context, op.getStrParam1());
                break;
            case CompOperationBean.EQUIP:
                UserLogic.equip(context, op.getStrParam1(), op.getStrParam2(), (int)op.getNumParam1());
                break;
            case CompOperationBean.UNEQUIP:
                UserLogic.unequip(context, op.getStrParam1(), op.getStrParam2(), (int)op.getNumParam1());
                break;
            case CompOperationBean.BUY:
                PurchaseLogic.buy(context, op.getStrParam1(), (int)op.getNumParam1());
                break;
            case CompOperationBean.SELL:
                PurchaseLogic.sell(context, op.getStrParam1(), (int)op.getNumParam1());
                break;
            case CompOperationBean.FIGHT:
                FightLogic.fight(context);
                break;
            case CompOperationBean.ABANDON_COMBAT:
                FightLogic.abandon(context);
                break;
            case CompOperationBean.ENTER:
                UserLogic.enter(context);
                break;
            case CompOperationBean.HIRE:
                UserLogic.hire(context, op.getStrParam1());
                break;
            case CompOperationBean.FIRE:
                UserLogic.fire(context, op.getStrParam1());
                break;
            case CompOperationBean.DEBUG:
                CompOperationLogic.debug(context, op.getStrParam1());
                break;
            case CompOperationBean.ANSWER:
                if (op.getNumParam1() == CompOperationBean.YES)
                    QueryLogic.doYes(context);
                else if (op.getNumParam1() == CompOperationBean.NO)
                    QueryLogic.doNo(context);
                else if (op.getNumParam1() == CompOperationBean.CANCEL)
                    QueryLogic.doCancel(context);
                else if (op.getNumParam2() == CompOperationBean.NORTH)
                    QueryLogic.doDirection(context, (int)op.getNumParam2());
                else if (op.getNumParam2() == CompOperationBean.SOUTH)
                    QueryLogic.doDirection(context, (int)op.getNumParam2());
                else if (op.getNumParam2() == CompOperationBean.EAST)
                    QueryLogic.doDirection(context, (int)op.getNumParam2());
                else if (op.getNumParam2() == CompOperationBean.WEST)
                    QueryLogic.doDirection(context, (int)op.getNumParam2());
                else if (op.getNumParam2() == CompOperationBean.CANCEL)
                    QueryLogic.doCancel(context);
                break;
            case CompOperationBean.CONSUMENEWS:
                UserLogic.consumeNews(context, (int)op.getNumParam1());
                break;
            case CompOperationBean.SLEEP:
                UserLogic.sleep(context);
                break;
            case CompOperationBean.LINK:
                linkAccount(context, op.getStrParam1(), op.getStrParam2(),
                        op.getStrParam3(), op.getStrParam4());
                break;
            default:
                context.setLastOperationError(CompanionsModelConst.TEXT_UNKNOWN_OPERATION_XXX, op.getOperation());
        }
        fillContext(context);
        return context;
    }

    public static boolean initializeCompContext(CompOperationBean op,
            final CompContextBean context)
    {
        context.setLastOperation(op);
        CompIdentBean id = IdentLogic.getIdent(op.getIdentID());
        if (id == null)
        {
            log("Can't find ident="+op.getIdentID());
            if (op.getNumParam1() != 0)
                return false;
            id = IdentLogic.newInstance(op.getIdentID(), op.getStrParam2(), op.getStrParam3());
            log("Creating new ident="+op.getIdentID());
            log("with name="+op.getStrParam2()+", email="+op.getStrParam3());
        }
        else
        {
            IdentLogic.updateIdent(id, op.getStrParam3(), op.getStrParam2(), null);
            log("Updating ident="+op.getIdentID());
            //log("with name="+op.getStrParam2()+", email="+op.getStrParam3());
        }
        CompUserBean acct = UserLogic.getUser(id.getEmail() != null ? id.getEmail() : id.getUserID());
        if (acct == null)
        {
            acct = UserLogic.newInstance(id, op.getFlags());
            log("Creating new account with id="+id.getUserID()+", flags="+op.getFlags()+", in location "+acct.getLocation());
        }
        else
        {
            log("Retrieved account with id="+id.getUserID());
            mMRU.remove(acct);
            mMRU.add(0, acct);
            if (mMRU.size() > 32)
                mMRU.remove(32);
        }
        context.setUser(acct);
        log("user loc="+acct.getLocation());
//            if (!StringUtils.isTrivial(id.getEmail()))
//            {
//                CompUserBean acct = UserLogic.getUser(id.getEmail());
//                if (acct == null)
//                {
//                    acct = UserLogic.newInstance(id);
//                    log("Creating new account with email="+id.getEmail());
//                }
//                else
//                    log("Retrieved account with email="+id.getEmail());
//                context.setUser(acct);
//                log("user loc="+acct.getLocation());
//            }
//        }
        context.setID(id);
        if (StringUtils.isTrivial(acct.getSupportIdent()) || Character.isLowerCase(acct.getSupportIdent().charAt(0)))
        {
            acct.setSupportIdent(makeSupportIdent());
            log("new support ident="+acct.getSupportIdent());
        }
        if (StringUtils.isTrivial(acct.getSupportPassword()))
        {
            acct.setSupportPassword(makeSupportPassword());
            log("new support password="+acct.getSupportPassword());
            CompIOLogic.saveUser(acct);
        }
        // premium entitlement
        String flags = op.getFlags().toLowerCase();
        if (flags.indexOf("google") >= 0)
        {
            context.setCanPremium(false);
            context.setPremium(true);
        }
        else if (flags.indexOf("kids") >= 0)
        {
            context.setCanPremium(false);
            context.setPremium(false);
        }
        else if (flags.indexOf("lite") >= 0)
        {
            context.setCanPremium(false);
            context.setPremium(false);
        }
        else if (flags.indexOf("premium") >= 0)
        {
            if (flags.indexOf("lang=en_us") < 0)
            {
                context.setCanPremium(false);
                context.setPremium(true);
            }
            else
            {
                context.setCanPremium(true);
                if (System.currentTimeMillis() < context.getUser().getSubscribedUnitl())
                    context.setPremium(true);
                else if (flags.indexOf(CompConstLogic.PREMIUM_SUBSCRIPTION_ID1) >= 0)
                    context.setPremium(true);
                else if (flags.indexOf(CompConstLogic.PREMIUM_SUBSCRIPTION_ID2) >= 0)
                    context.setPremium(true);
                else
                    context.setPremium(false);
            }
        }
        else
        {
            context.setCanPremium(false);
            context.setPremium(true);
        }
        return true;
    }
    
    public static void fillContext(CompContextBean context)
    {
        if (context.getUser() == null)
            return;
        GeoBean ords = new GeoBean(context.getUser().getLocation());
        context.setLocation(ords);
        log("context loc="+context.getUser().getLocation()+" -> "+context.getLocation().toString());
        context.setDomain(GenerationLogic.getDomain(ords));
        context.setRegion(GenerationLogic.getRegion(ords));
        context.setSquare(GenerationLogic.getSquare(ords));
        context.setSquareNorth(GenerationLogic.getSquare(ords.north()));
        context.setSquareSouth(GenerationLogic.getSquare(ords.south()));
        context.setSquareEast(GenerationLogic.getSquare(ords.east()));
        context.setSquareWest(GenerationLogic.getSquare(ords.west()));
        for (CompCompanionBean comp : context.getUser().getCompanions())
            if (comp.getID().equals(context.getUser().getActiveCompanion()))
            {
                context.setCompanion(comp);
                break;
            }
        context.setFeature(null);
        context.setRoom(null);
        if (FeatureLogic.isStaticFeature(ords, context))
            context.getSquare().setFeature(CompConstLogic.FEATURE_STATIC);
        if (context.getSquare().getFeature() != CompConstLogic.FEATURE_NONE)
        {
            context.setFeature(FeatureLogic.getFeatureInstance(context));
            if ((context.getFeature() !=  null) && !StringUtils.isTrivial(ords.getRoomID()))
            {
                context.setRoom(FeatureLogic.findRoom(context.getFeature().getFeature(), ords.getRoomID()));
                if (context.getRoom() == null) // no such room
                {
                    DebugUtils.trace("Tactical location evaporated. Resetting to strategic location.");
                    ords.setRoomID(null);
                    context.getUser().setLocation(ords.toString());
                    context.setLocation(ords);
                    CompIOLogic.saveUser(context.getUser());
                }
            }
        }
        try
        {
            context.setCloudCover(GenerationLogic.getCloudCover(context.getLocation(), context.getUser().getTotalTime()));
            context.setPrecipitation(GenerationLogic.getPrecipitation(context.getLocation(), context.getUser().getTotalTime()));
        }
        catch (Exception e)
        {
            DebugUtils.trace("Error processing weather.", e);
        }
        if (context.getSquare().getSignposts().size() > 0)
        {
            for (int i = 0; i < context.getSquare().getSignposts().size(); i++)
            {
                CoordBean loc = context.getSquare().getSignposts().get(i).getDestination();
                SquareBean sq = GenerationLogic.getSquare(loc);
                FeatureBean f = FeatureLogic.getFeature(context.getRegion(), sq, context);
                if (f == null)
                    context.getSignpostNames().add(new AudioMessageBean(CompanionsModelConst.TEXT_UNKNOWN_LOCATION));
                else
                    context.getSignpostNames().add(f.getName());
            }
        }
        MessageLogic.checkMessagesForDelivery(context);
        MessageLogic.checkMessagesForPickup(context);
        if (StringUtils.isTrivial(context.getUser().getActiveCompanion()))
            if (context.getUser().getCompanions().size() > 0)
                context.getUser().setActiveCompanion(context.getUser().getCompanions().get(0).getID());
    }
    
    public static void startBackgroundDaemons()
    {
        GypsyLogic.startBackgroundDaemon();
        MissionLogic.startBackgroundDaemon();
        GenerationLogic.startBackgroundDaemon();
    }
    
    private static void log(String msg)
    {
        DebugUtils.trace(msg);
    }
    
    private static void debug(CompContextBean context, String command)
    {
        if ("encounter".equals(command))
        {
            CompOperationLogic.fillContext(context);
            UserLogic.createStrategicCombat(context.getUser(), context.getSquare());
            CompIOLogic.saveUser(context.getUser());
        }
        else if ("cure".equals(command))
        {
            CompOperationLogic.fillContext(context);
            UserLogic.fullyCureCompanions(context, context.getUser());
            CompIOLogic.saveUser(context.getUser());
        }
        else if ("decimate".equals(command))
        {
            FightLogic.decimate(context);
        }
        else if ("wizard".equals(command))
        {
            context.addMessage(CompanionsModelConst.TEXT_WIZARD_APPEARS);
            AudioMessageBean q = new AudioMessageBean(CompanionsModelConst.TEXT_WIZARD_TELEPORT_QUESTION);
            JSONObject question = new JSONObject();
            question.put(QueryLogic.QUERY_TEXT, q.toJSON());
            question.put(QueryLogic.QUERY_ACTION, "wizard_teleport");
            CompUserBean user = context.getUser();
            if (user.getMetadata() == null)
                user.setMetadata(new JSONObject());
            user.getMetadata().put(CompUserBean.META_QUESTION, question);
            context.setError(false);                
        }
    }
    
    private static void linkAccount(CompContextBean context, String supportID, String supportPass,
            String email, String name)
    {
        log("LinkAccout id="+supportID+" pass="+supportPass);
        StringBuffer normalizedSupportID = new StringBuffer();
        for (StringTokenizer st = new StringTokenizer(supportID, " "); st.hasMoreTokens(); )
        {
            if (normalizedSupportID.length() > 0)
                normalizedSupportID.append(" ");
            String word = st.nextToken().toLowerCase();
            if (word.equalsIgnoreCase("the"))
                normalizedSupportID.append(word);
            else if (word.equalsIgnoreCase("of"))
                normalizedSupportID.append("of");
            else
            {
                normalizedSupportID.append(Character.toUpperCase(word.charAt(0)));
                normalizedSupportID.append(word.substring(1).toLowerCase());
            }
        }
        supportID = normalizedSupportID.toString();
        log("LinkAccout id="+supportID+" (normalized)");
        CompUserBean user = null;
        log("LinkAccout "+mMRU.size()+" in MRU list");
        for (CompUserBean u : mMRU)
            if (supportID.equalsIgnoreCase(u.getSupportIdent()) && supportPass.equalsIgnoreCase(u.getSupportPassword()))
            {
                user = u;
            }
            else
                log("LinkAccout no match to mru with "+u.getSupportIdent()+"/"+u.getSupportPassword());
        if (user == null)
            user = CompIOLogic.getUserFromSupport(supportID, supportPass);
        if (user != null)
        {
            CompIdentBean id = context.getID();
            if (id == null)
            {
                CompOperationBean op = context.getLastOperation();
                id = IdentLogic.newInstance(op.getIdentID(), name, email);
                log("Creating new ident="+op.getIdentID());
                log("with name="+op.getStrParam2()+", email="+op.getStrParam3());
            }
            if (!id.getUserID().equals(user.getURI().substring(11)))
            {
                id.setUserID(user.getURI().substring(11));
                CompIOLogic.saveIdent(id);
                log("Mapped="+id.getURI()+" to "+user.getURI());
            }
            context.setUser(user);
            context.addMessage(CompanionsModelConst.TEXT_LINKED_TO_XXX, user.getSupportIdent());
            return;
        }
        log("None match");
        context.addMessage(CompanionsModelConst.TEXT_COULD_NOT_LINK_TO_XXX, supportID);
        context.setError(true);
    }
    
    private static final String[] ADJECTIVES = {
            "Able",
            "Bad",
            "Best",
            "Big",
            "Black",
            "Certain",
            "Clear",
            "Easy",
            "Federal",
            "Free",
            "Full",
            "Good",
            "Great",
            "Hard",
            "High",
            "Important",
            "Invincible",
            "International",
            "Large",
            "Late",
            "Little",
            "Local",
            "Long",
            "Low",
            "Major",
            "National",
            "New",
            "Old",
            "Only",
            "Other",
            "One",
            "Pretentious",
            "Public",
            "Real",
            "Raging",
            "Right",
            "Small",
            "Social",
            "Special",
            "Strong",
            "Sure",
            "True",
            "White",
            "Whole",
            "Young",
            "Mighty",
            "Dark",
    };
    
    private static final String[] NOUN = {
            "Wolves",
            "Horde",
            "Elephants",
            "Falcons",
            "Hornets",
            "Kittens",
            "Leopards",
            "Lions",
            "Monkeys",
            "Owls",
            "Pandas",
            "Panthers",
            "Boars",
            "Raccoons",
            "Tigers",
            "Fighters",
            "Swords",
            "Roads",
            "Storms",
            "Gentlemen",
            "Ladies",
            "Dragons",
            "Dragoons",
            "Titans",
            "Serpents",
            "Scorpions",
            "Stars",
            "Mountains",
            "Rivers",
    };
    
    private static final String[] COLLECTIVE = {
            "%s %s of Wanders",
            "%s %s of Sojouners",
            "%s %s of Brigands",
            "%s %s of Adveneturers",
            "%s %s of Adventure",
            "%s %s of The Way",
            "Fellowship of the %s %s",
            "Union of %s %s",
            "Alliance of %s %s",
            "Group of %s %s",
            "Band of %s %s",
            "Association of %s %s",
            "Guild of %s %s",
            "Companions of the %s %s",
            "Brotherhood of the %s %s",
            "Legion of %s %s",
            "Army of %s %s",
            "Partnership of %s %s",
            "Council of %s %s",
            "Fraterernity of %s %s",
            "Sorority of %s %s",
            "Sisterhood of the %s %s",
            "%s %s Squadron",
            "%s %s Club",
            "%s %s Troupe",
            "%s %s Syndicate",
            "%s %s Knitting Club",
            "League of %s %s",
            "%s %s Swarm",
            "%s %s Front",
            "%s %s Academy",
            "%s %s Society",
            "%s %s Front",
            "%s %s of Slayers",
            "%s %s Party",
            "%s %s of Hunters",
            "%s %s Collective",
            "%s %s of Despair",
            "%s %s of The Stick",
            "%s %s of The Moon",
    };

    public static final String[] PASSWORD_PREFIX = {
      "red", "orange", "yellow", "green", "blue", "indigo", "violet", "white", "black", "gray",      
    };
    public static final String[] PASSWORD_SUFFIX = {
            "cat", "dog", "pig", "chicken", "cow", "sheep", "duck", "goat", "horse",      
          };
        
    private static String makeSupportIdent()
    {
        String adjective = ADJECTIVES[BaseUserState.RND.nextInt(ADJECTIVES.length)];
        String noun = NOUN[BaseUserState.RND.nextInt(NOUN.length)];
        String collective = COLLECTIVE[BaseUserState.RND.nextInt(COLLECTIVE.length)];
        String ident = String.format(collective, adjective, noun);
        return ident;
    }
    
    private static String makeSupportPassword()
    {
        String prefix = PASSWORD_PREFIX[BaseUserState.RND.nextInt(PASSWORD_PREFIX.length)];
        String suffix = PASSWORD_SUFFIX[BaseUserState.RND.nextInt(PASSWORD_SUFFIX.length)];
        String password = prefix + " " + suffix;
        return password;
    }
}
