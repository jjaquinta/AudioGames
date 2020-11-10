package jo.audio.companions.logic.feature;

/*
ADDING PANTHEON

in Companions.model
Add to A_GOD the names of each god
Add to CULTIC_FUNCTIONARY "Priest" or "Priestess" for each god

in rooms/temple.json
Find the last room with id "templeXXX". Add a new one of the form:
      {
         "name":{
            "ident":"TEMPLE_OF_GODNAME"
         },
         "description":{
            "ident":"TEMPLE_OF_GODNAME_DESCRIPTION"
         },
         "ID":"templeXXX + 1",
         "type":"scenic",
         "params":{
            "effect":"message",
            "message":"{{GODNAME_ORACLES}}"
         }
      },
Add to text->enUS values for TEMPLE_OF_GODNAME, and TEMPLE_OF_GODNAME_DESCRIPTION

in effects.model
Add to text->enUS values for GODNAME_ORACLES

in PantheonLogic
Add constant for pantheon, Add range for gods.
 */
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompContextBean;
import jo.audio.companions.data.CompItemInstanceBean;
import jo.audio.companions.data.CompItemTypeBean;
import jo.audio.companions.data.DemenseBean;
import jo.audio.companions.logic.ItemLogic;
import jo.audio.companions.slu.CompanionsModelConst;
import jo.audio.util.ResponseUtils;
import jo.util.utils.obj.StringUtils;

public class PantheonLogic
{
    public static final String ROMAN = "ROMAN";
    public static final String LIZARD = "LIZARD";
    public static final String AZTEC = "AZTEC";
    public static final String INDIAN = "INDIAN";
    public static final String BABYLONIAN = "BABYLONIAN";
    public static final String GNOME = "GNOME";
    public static final String DWARVISH = "DWARVISH";
    public static final String HALFLING = "HALFLING";
    public static final String AMERINDIAN = "AMERINDIAN";
    public static final String ELVISH = "ELVISH";
    public static final String CELTIC = "CELTIC";
    public static final String JAPANESE = "JAPANESE";
    public static final String EGYPTIAN = "EGYPTIAN";
    public static final String NORSE = "NORSE";
    public static final String FINNISH = "FINNISH";
    public static final String ROMITU = "ROMITU";
    
    public static final String DEFAULT = ROMAN;
    
    public static final Map<String, int[]> GOD_RANGE = new HashMap<String, int[]>();
    static
    {
        GOD_RANGE.put(ROMAN, new int[] { 0, 12 });
        GOD_RANGE.put(LIZARD, new int[] { 12, 16 });
        GOD_RANGE.put(DWARVISH, new int[] { 16, 21 });
        GOD_RANGE.put(ROMITU, new int[] { 21, 28 });
        GOD_RANGE.put(ELVISH, new int[] { 28, 36 });
        GOD_RANGE.put(AZTEC, new int[] { 36, 41 });
        GOD_RANGE.put(BABYLONIAN, new int[] { 41, 46 });
        GOD_RANGE.put(JAPANESE, new int[] { 46, 50 });
        GOD_RANGE.put(EGYPTIAN, new int[] { 50, 59 });
        GOD_RANGE.put(INDIAN, new int[] { 59, 65 });
        //GOD_RANGE.put(GNOME, new int[] { -, - });
        //GOD_RANGE.put(HALFLING, new int[] { -, - });
        //GOD_RANGE.put(AMERINDIAN, new int[] { -, - });
        //GOD_RANGE.put(CELTIC, new int[] { -, - });
        //GOD_RANGE.put(NORSE, new int[] { -, - });
        //GOD_RANGE.put(FINNISH, new int[] { -, - });
    }
    public static final String[] GOD_WEAPONS = {
            "trident", // Neptune",
            "spear", // Jupiter",
            "large_shield", // Minerva",
            "heavy_flail", // Pluto",
            "long_bow", // Apollo",
            "bastard_sword", // Mars",
            "comp_short_bow", // Diana",
            "war_hammer", // Vulcan",
            "short_bow", // Venus",
            "potion_speed", // Mercury",
            "banded_armor", // Juno",
            "scimatar", // Ceres",
            "pole_arm", // Sechchest",
            "heavy_pick", // Seta",
            "staff", // Lachkaka",
            "war_hammer", // Oknadeta",
            "war_hammer", // <phoneme alphabet=\"ipa\" ph=\"zoːʀʌk\">Zôruk</phoneme>",
            "battle_axe", // <phoneme alphabet=\"ipa\" ph=\"beːnad\">Bênad</phoneme>",
            "light_pick", // <phoneme alphabet=\"ipa\" ph=\"kʰəzud\">Khirzud</phoneme>",
            "light_mace", // <phoneme alphabet=\"ipa\" ph=\"dom\">Dom</phoneme>",
            "light_xbow", // <phoneme alphabet=\"ipa\" ph=\"siːmak\">Shîmak</phoneme>",
            "heavy_flail", // Sky Father",
            "spear", // Hearth Mother",
            "long_bow", // Water Bearer",
            "scimatar", // Grave Keeper",
            "bastard_sword", // Martius",
            "club", // Harper",
            "chain_mail", // Weaver",
            "long_sword", // <phoneme alphabet=\"ipa\" ph=\"ɑrɑʊ̯r\">Araur</phoneme>",
            "medium_shield", // <phoneme alphabet=\"ipa\" ph=\"ɔrɛ\">Ore</phoneme>",
            "scimatar", // <phoneme alphabet=\"ipa\" ph=\"mɔsɛnɔɑ\">Mosenoa</phoneme>",
            "lance", // <phoneme alphabet=\"ipa\" ph=\"nɛrkkɑnɡɑ\">Nerccanga</phoneme>",
            "dagger", // <phoneme alphabet=\"ipa\" ph=\"sinɑˑnɛtɑ\">Sináneta</phoneme>",
            "heavy_mace", // <phoneme alphabet=\"ipa\" ph=\"yɑkuilɑ\">Yacuila</phoneme>",
            "elfin_chain", // <phoneme alphabet=\"ipa\" ph=\"lilɑɪ̯vɔ\">Lilaivo</phoneme>",
            "trident", // <phoneme alphabet=\"ipa\" ph=\"nkwɑnnɑˑtɑ\">Nquannáta</phoneme>",
            "heavy_mace", // <phoneme alphabet=\"ipa\" ph=\"ˌtɛzˌkætliˈpoʊkə\">Tezcatlipoca</phoneme>",
            "heavy_mace", // <phoneme alphabet=\"ipa\" ph=\"ˌkɛtsɑːlˈkoʊɑːtəl\">Quetzalcoatl</phoneme>",
            "heavy_mace", // <phoneme alphabet=\"ipa\" ph=\"ˈtɬaːlok\">Tlaloc</phoneme>",
            "heavy_mace", // <phoneme alphabet=\"ipa\" ph=\"miʃˈkoːwaːt͡ɬ\">Mixcoatl</phoneme>",
            "heavy_mace", // <phoneme alphabet=\"ipa\" ph=\"wiːt͡siloːˈpoːt͡ʃt͡ɬi\">Huitzilopochtli</phoneme>",
            "light_mace", // <phoneme alphabet=\"ipa\" ph=\"ˈɛŋki\">Enki</phoneme>",
            "light_mace", // Ashur",
            "light_mace", // Enlil",
            "light_mace", // <phoneme alphabet=\"ipa\" ph=\" (ˈɪʃtɑːr\">Ishtar</phoneme>",
            "light_mace", // Marduk",
            "long_sword", // <phoneme alphabet=\"ipa\" ph=\"tomauko\">Tomauko</phoneme>",
            "short_sword", // <phoneme alphabet=\"ipa\" ph=\"kamateko\">Kamateko</phoneme>",
            "halberd", // <phoneme alphabet=\"ipa\" ph=\"shinshimi\">Shinshimi</phoneme>",
            "comp_long_bow", // <phoneme alphabet=\"ipa\" ph=\"omira\">Omira</phoneme>",
            "scimatar", // <phoneme alphabet=\"ipa\" ph=\"ɑ.tum\">Atum</phoneme>",
            "morning_star", // Shu",
            "scimatar", // Tefnut",
            "staff", // Geb",
            "hand_axe", // <phoneme alphabet=\"ipa\" ph=\"nuːt\">Nut</phoneme>",
            "spear", // <phoneme alphabet=\"ipa\" ph=\"(oʊˈsaɪrɪs\">Osiris</phoneme>",
            "javelin", // <phoneme alphabet=\"ipa\" ph=\"ˈaɪsɪs\">Isis</phoneme>",
            "two_handed_sword", // <phoneme alphabet=\"ipa\" ph=\"sɛθ\">Seth</phoneme>",
            "medium_shield", // <phoneme alphabet=\"ipa\" ph=\"ˈnɛfθɪs\">Nephthys</phoneme>",
            "long_sword", // Tilwarna",
            "battle_axe", // Jamano",
            "broad_sword", // Bhima",
            "long_bow", // Devrakoot",
            "morning_star", // Nindarthata",
            "bastard_sword", // Jarama"
    };

    // 0 = none, 100 = head of pantheon
    public static final int[] GOD_POWER = {
            // ROMAN
            90, // Neptune",
            100, // Jupiter",
            70, // Minerva",
            80, // Pluto",
            60, // Apollo",
            50, // Mars",
            60, // Diana",
            50, // Vulcan",
            40, // Venus",
            40, // Mercury",
            30, // Juno",
            20, // Ceres",
            // LIZARD
            100, // Sechchest",
            80, // Seta",
            50, // Lachkaka",
            50, // Oknadeta",
            // DWARVISH
            100, // <phoneme alphabet=\"ipa\" ph=\"zoːʀʌk\">ZôrukR</phoneme>",
            80, // <phoneme alphabet=\"ipa\" ph=\"beːnad\">Bênad</phoneme>",
            70, // <phoneme alphabet=\"ipa\" ph=\"kʰəzud\">Khirzud</phoneme>",
            60, // <phoneme alphabet=\"ipa\" ph=\"dom\">Dom</phoneme>",
            50, // <phoneme alphabet=\"ipa\" ph=\"siːmak\">Shîmak</phoneme>",
            // ROMITU
            100, // Sky Father",
            70, // Hearth Mother",
            90, // Water Bearer",
            80, // Grave Keeper",
            60, // Martius",
            50, // Harper",
            40, // Weaver",
            // ELVISH
            100, // <phoneme alphabet=\"ipa\" ph=\"ɑrɑʊ̯r\">Araur</phoneme>",
            90, // <phoneme alphabet=\"ipa\" ph=\"ɔrɛ\">Ore</phoneme>",
            80, // <phoneme alphabet=\"ipa\" ph=\"mɔsɛnɔɑ\">Mosenoa</phoneme>",
            70, // <phoneme alphabet=\"ipa\" ph=\"nɛrkkɑnɡɑ\">Nerccanga</phoneme>",
            60, // <phoneme alphabet=\"ipa\" ph=\"sinɑˑnɛtɑ\">Sináneta</phoneme>",
            50, // <phoneme alphabet=\"ipa\" ph=\"yɑkuilɑ\">Yacuila</phoneme>",
            40, // <phoneme alphabet=\"ipa\" ph=\"lilɑɪ̯vɔ\">Lilaivo</phoneme>",
            30, // <phoneme alphabet=\"ipa\" ph=\"nkwɑnnɑˑtɑ\">Nquannáta</phoneme>",
            // AZTEC
            100, // <phoneme alphabet=\"ipa\" ph=\"ˌtɛzˌkætliˈpoʊkə\">Tezcatlipoca</phoneme>",
            90, // <phoneme alphabet=\"ipa\" ph=\"ˌkɛtsɑːlˈkoʊɑːtəl\">Quetzalcoatl</phoneme>",
            80, // <phoneme alphabet=\"ipa\" ph=\"ˈtɬaːlok\">Tlaloc</phoneme>",
            70, // <phoneme alphabet=\"ipa\" ph=\"miʃˈkoːwaːt͡ɬ\">Mixcoatl</phoneme>",
            60, // <phoneme alphabet=\"ipa\" ph=\"wiːt͡siloːˈpoːt͡ʃt͡ɬi\">Huitzilopochtli</phoneme>",
            // BABYLONIAN
            100, // <phoneme alphabet=\"ipa\" ph=\"ˈɛŋki\">Enki</phoneme>",
            90, // Ashur",
            80, // Enlil",
            70, // <phoneme alphabet=\"ipa\" ph=\" (ˈɪʃtɑːr\">Ishtar</phoneme>",
            60, // Marduk",
            // JAPANESE
            100, // "<phoneme alphabet=\"ipa\" ph=\"tomauko\">Tomauko</phoneme>",
            90, // "<phoneme alphabet=\"ipa\" ph=\"kamateko\">Kamateko</phoneme>",
            80, // "<phoneme alphabet=\"ipa\" ph=\"shinshimi\">Shinshimi</phoneme>",
            70, // "<phoneme alphabet=\"ipa\" ph=\"omira\">Omira</phoneme>",
            // EGYPTIAN
            100, // <phoneme alphabet=\"ipa\" ph=\"ɑ.tum\">Atum</phoneme>",
            90, // Shu",
            90, // Tefnut",
            80, // Geb",
            80, // <phoneme alphabet=\"ipa\" ph=\"nuːt\">Nut</phoneme>",
            70, // <phoneme alphabet=\"ipa\" ph=\"(oʊˈsaɪrɪs\">Osiris</phoneme>",
            70, // <phoneme alphabet=\"ipa\" ph=\"ˈaɪsɪs\">Isis</phoneme>",
            60, // <phoneme alphabet=\"ipa\" ph=\"sɛθ\">Seth</phoneme>",
            60, // <phoneme alphabet=\"ipa\" ph=\"ˈnɛfθɪs\">Nephthys</phoneme>",
            // INDIAN
            100, // Tilwarna",
            90, // Jamano",
            80, // Bhima",
            70, // Devrakoot",
            60, // Nindarthata",
            50, // Jarama"
            // GNOME
            // HALFLING
            // AMERINDIAN
            // CELTIC
            /// NORSE
            // FINNISH
    };

    public static String getPantheon(DemenseBean dem)
    {
        while ((dem != null) && StringUtils.isTrivial(dem.getPantheon()))
            dem = dem.getLiege();
        if (dem == null)
            return DEFAULT;
        return dem.getPantheon();
    }
    
    public static int randomGod(Random rnd, DemenseBean dem, float max)
    {
        int[] hilow = getGods(dem);
        int range = (int)((hilow[1] - hilow[0])*max);
        if (range == 0)
            range = 1;
        return rnd.nextInt(range) + hilow[0];
    }
    
    public static int numberOfGods(DemenseBean dem)
    {
        int[] hilow = getGods(dem);
        int range = hilow[1] - hilow[0];
        return range;
    }
    
    public static int[] getGods(DemenseBean dem)
    {
        String pantheon = getPantheon(dem);
        int[] hilow = GOD_RANGE.get(pantheon);
        if (hilow == null)
            hilow = GOD_RANGE.get(DEFAULT);
        return hilow;
    }

    /*
        JSONArray postCombat = new JSONArray();
        postCombat.add("respond(GOD_SMILES)");
        postCombat.add("increment(GOD_"+expansions.get(0)+")");
        postCombat.add("increment(BLESSINGS_NUM)");
        postCombat.add("tag(BLESSINGS_WHO, GOD_"+expansions.get(0)+")");
        JSONArray doGive = new JSONArray();
        doGive.add("give("+PantheonLogic.GOD_WEAPONS[IntegerUtils.parseInt(expansions.get(0))]+", 1, {{A_GOD_WEAPON#"+expansions.get(0)+"}})");
        doGive.add("respond(GOD_GIVES, {{A_GOD_WEAPON#"+expansions.get(0)+"}})");
        JSONObject giveWeapon = new JSONObject();
        giveWeapon.put("if", "getvalue(GOD_"+expansions.get(0)+") == 1");
        giveWeapon.put("then", doGive);
        postCombat.add(giveWeapon);

     */
    public static void divineBlessing(CompContextBean context, int god)
    {
        //DebugUtils.trace("PantheonLogic.divineBlessing(god="+god+")");
        context.addMessage(CompanionsModelConst.TEXT_GOD_SMILES);
        int godFavor = JSONUtils.getInt(context.getUser().getMetadata(), "GOD_"+god);
        godFavor++;
        context.getUser().getMetadata().put("GOD_"+god, godFavor);
        //DebugUtils.trace("PantheonLogic.divineBlessing favor="+godFavor);
        int totBlessings = JSONUtils.getInt(context.getUser().getMetadata(), "BLESSINGS_NUM");
        totBlessings++;
        context.getUser().getMetadata().put("BLESSINGS_NUM", totBlessings);
        //DebugUtils.trace("PantheonLogic.divineBlessing favor="+totBlessings);
        String list = JSONUtils.getString(context.getUser().getMetadata(), "BLESSINGS_WHO");
        list = ResponseUtils.addToList(list, "GOD_"+god);
        context.getUser().getMetadata().put("BLESSINGS_WHO", list);
        int averageLevel = 0;
        for (CompCompanionBean comp : context.getUser().getCompanions())
            averageLevel += comp.getLevel();
        averageLevel /= 6; // assume six members of party
        //DebugUtils.trace("PantheonLogic.divineBlessing averageLevel="+averageLevel);
        if ((godFavor <= averageLevel/2) && (godFavor < 5))
        {   // bless with item
            String id = GOD_WEAPONS[god];
            CompItemTypeBean type = ItemLogic.getItemType(id);
            int quan = 1;
            if ((type.getType() == CompItemTypeBean.TYPE_POTION) || (type.getType() == CompItemTypeBean.TYPE_AMMO))
                quan = godFavor;
            if ((type.getType() == CompItemTypeBean.TYPE_HAND) || (type.getType() == CompItemTypeBean.TYPE_AMMO)
                    || (type.getType() == CompItemTypeBean.TYPE_HURLED) || (type.getType() == CompItemTypeBean.TYPE_ARMOR)
                    || (type.getType() == CompItemTypeBean.TYPE_LAUNCHER)|| (type.getType() == CompItemTypeBean.TYPE_SHIELD))
            {
                id += "+"+godFavor;
            }
            CompItemInstanceBean item = ItemLogic.createInstance(id, quan);
            item.setName("{{A_GOD_WEAPON#"+god+"}}");
            context.getUser().getItems().add(0, item);
            context.addMessage(CompanionsModelConst.TEXT_GOD_GIVES, item.getName());
            //DebugUtils.trace("PantheonLogic.divineBlessing giving "+item.getID()+" called "+item.getName());
        }
    }
}
