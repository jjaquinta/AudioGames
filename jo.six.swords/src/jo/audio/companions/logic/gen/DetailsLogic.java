package jo.audio.companions.logic.gen;

import java.util.List;
import java.util.Random;

import jo.audio.companions.data.DemenseBean;
import jo.audio.companions.data.RegionGenBean;
import jo.audio.companions.data.SquareGenBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.DemenseLogic;

public class DetailsLogic
{
    public static void generateRegionDetails(RegionGenBean region, Long seed)
    {
        Random rnd;
        if (seed == null)
            rnd = CompConstLogic.getRandom(region.getOrds(), 1);
        else
            rnd = CompConstLogic.getRandom(region.getOrds(), seed);
        region.setPredominantTerrain(CompConstLogic.roll(CompConstLogic.TABLE_TERRAIN_IN_REGION[region.getPredominantRace()], rnd));
        if ((region.getGovernmentalStructure() != CompConstLogic.GOVERNMENT_ANARCHY)
                && ((region.getPredominantTerrain() == CompConstLogic.TERRAIN_ARTIC)
                        || (region.getPredominantTerrain() == CompConstLogic.TERRAIN_FRESHWATER)
                        || (region.getPredominantTerrain() == CompConstLogic.TERRAIN_SALTWATER)))
            region.setPredominantTerrain(CompConstLogic.TERRAIN_PLAINS);
        List<SquareGenBean> squares = HeightLogic.generateRegionHeights(region, rnd);
        TerrainLogic.generateRegionTerrain(rnd, region, squares);
        TerrainLogic.generateRegionTerrainDepth(region);
        FeaturesLogic.generateRegionFeatures(region, squares, rnd);
        RoadsLogic.generateRegionRoads(region, rnd);
        RoadsLogic.generateRegionSigns(region);
        ChallengeLogic.generateRegionChallenge(region, rnd);
        DemenseBean d = DemenseLogic.get(region.getOrds().toString());
        for (SquareGenBean sq : squares)
            sq.setDemense(d);
        region.setDetails(true);
    }
}
