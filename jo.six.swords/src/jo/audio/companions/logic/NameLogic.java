package jo.audio.companions.logic;

import java.util.Random;

import jo.audio.util.BaseUserState;

public class NameLogic
{
    public static String kingdomName(Random rnd, int race)
    {
        String raceSuffix = "HUMAN";
        if (race == CompConstLogic.RACE_DWARF)
            raceSuffix = "DWARF";
        else if (race == CompConstLogic.RACE_ELF)
            raceSuffix = "ELF";
        return "{{KINGDOM_ADJECTIVE_"+raceSuffix+"#"+rnd.nextInt(9973)+"}}" + " " 
                    + "{{KINGDOM_NOUN_"+raceSuffix+"#"+rnd.nextInt(9973)+"}}"; 
    }
		
    public static String getName(int race, boolean isMale)
    {
        String names = null;
        if (race == CompConstLogic.RACE_DWARF)
            names = isMale ? "DWARF_MALE_NAMES" : "DWARF_FEMALE_NAMES";
        else if (race == CompConstLogic.RACE_ELF)
            names = isMale ? "ELF_MALE_NAMES" : "ELF_FEMALE_NAMES";
        else 
            names = isMale ? "HUMAN_MALE_NAMES" : "HUMAN_FEMALE_NAMES";
        return "{{"+names+"#"+BaseUserState.RND.nextInt(9973)+"}}";
    }
}
