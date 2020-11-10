package jo.audio.companions.logic.feature.town;

import java.util.List;
import java.util.Random;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.logic.feature.DigOptions;

public class BardLogic
{

    static void addBardCollege(FeatureBean feature, Random rnd, List<DigOptions> sites)
    {
        DigOptions site = sites.get(rnd.nextInt(sites.size()));
        sites.remove(site);
        int north = site.dir;
        int south = TownLogic.opposite(site.dir);
        int west = TownLogic.left(site.dir);
        int east = TownLogic.right(site.dir);
    
        CompRoomBean bardEntrance = TownLogic.extendRoom(feature, site.from, "bardEntrance", north);
        CompRoomBean bardSpeed = TownLogic.extendRoom(feature, bardEntrance, "bardSpeed", west);
        TownLogic.extendRoom(feature, bardSpeed, "bardSpeedReset", west);
        TownLogic.extendRoom(feature, bardSpeed, "bardSpeedFaster", north);
        TownLogic.extendRoom(feature, bardSpeed, "bardSpeedSlower", south);
        CompRoomBean bardPitch = TownLogic.extendRoom(feature, bardEntrance, "bardPitch", east);
        TownLogic.extendRoom(feature, bardPitch, "bardPitchReset", east);
        TownLogic.extendRoom(feature, bardPitch, "bardPitchFaster", north);
        TownLogic.extendRoom(feature, bardPitch, "bardPitchSlower", south);
        CompRoomBean bardVoice = TownLogic.extendRoom(feature, bardEntrance, "bardVoice", north);
        TownLogic.extendRoom(feature, bardVoice, "bardVoiceReset", north);
        CompRoomBean bardVoiceFemale = TownLogic.extendRoom(feature, bardVoice, "bardVoiceFemale", west);
        TownLogic.extendRoom(feature, bardVoiceFemale, "bardVoiceFemaleAU1", south);
        CompRoomBean bardVoiceFemaleGB = TownLogic.extendRoom(feature, bardVoiceFemale, "bardVoiceFemaleGB", west);
        TownLogic.extendRoom(feature, bardVoiceFemaleGB, "bardVoiceFemaleGB1", west);
        TownLogic.extendRoom(feature, bardVoiceFemaleGB, "bardVoiceFemaleGB2", north);
        TownLogic.extendRoom(feature, bardVoiceFemaleGB, "bardVoiceFemaleGB3", south);
        CompRoomBean bardVoiceFemaleUS = TownLogic.extendRoom(feature, bardVoiceFemale, "bardVoiceFemaleUS", north);
        CompRoomBean bardVoiceFemaleUS_MORE = TownLogic.extendRoom(feature, bardVoiceFemaleUS, "bardVoiceFemaleUS_MORE", north);
        TownLogic.extendRoom(feature, bardVoiceFemaleUS, "bardVoiceFemaleUS1", west);
        TownLogic.extendRoom(feature, bardVoiceFemaleUS, "bardVoiceFemaleUS2", east);
        TownLogic.extendRoom(feature, bardVoiceFemaleUS_MORE, "bardVoiceFemaleUS3", west);
        TownLogic.extendRoom(feature, bardVoiceFemaleUS_MORE, "bardVoiceFemaleUS4", east);
        TownLogic.extendRoom(feature, bardVoiceFemaleUS_MORE, "bardVoiceFemaleUS5", north);
        CompRoomBean bardVoiceMale = TownLogic.extendRoom(feature, bardVoice, "bardVoiceMale", east);
        TownLogic.extendRoom(feature, bardVoiceMale, "bardVoiceMaleAU1", south);
        CompRoomBean bardVoiceMaleGB = TownLogic.extendRoom(feature, bardVoiceMale, "bardVoiceMaleGB", east);
        TownLogic.extendRoom(feature, bardVoiceMaleGB, "bardVoiceMaleGB1", north);
        TownLogic.extendRoom(feature, bardVoiceMaleGB, "bardVoiceMaleGB2", south);
        CompRoomBean bardVoiceMaleUS = TownLogic.extendRoom(feature, bardVoiceMale, "bardVoiceMaleUS", north);
        TownLogic.extendRoom(feature, bardVoiceMaleUS, "bardVoiceMaleUS1", north);
        TownLogic.extendRoom(feature, bardVoiceMaleUS, "bardVoiceMaleUS2", east);
        TownLogic.extendRoom(feature, bardVoiceMaleUS, "bardVoiceMaleUS3", west);
    }

}
