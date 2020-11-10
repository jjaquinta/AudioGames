package jo.audio.companions.logic.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.DiceRollBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.RegionGenBean;
import jo.audio.companions.data.SquareGenBean;
import jo.audio.companions.logic.CompConstLogic;

public class ChallengeLogic
{
    
    static void generateRegionChallenge(RegionGenBean region, Random rnd)
    {
        if (region.getGovernmentalStructure() == CompConstLogic.GOVERNMENT_ANARCHY)
            generateRegionAnarchyChallenge(region, rnd);
        else
            generateRegionStructuredChallenge(region, rnd);
    }

    
    private static void generateRegionStructuredChallenge(RegionGenBean region, Random rnd)
    {
        int maxChallenge = 16;
        if (region.getGovernmentalStructure() == CompConstLogic.GOVERNMENT_COUNTY)
            maxChallenge = 14;
        else if (region.getGovernmentalStructure() == CompConstLogic.GOVERNMENT_DUCHY)
            maxChallenge = 12;
        else if (region.getGovernmentalStructure() == CompConstLogic.GOVERNMENT_KINGDOM)
            maxChallenge = 10;
        else if (region.getGovernmentalStructure() == CompConstLogic.GOVERNMENT_EMPIRE)
            maxChallenge = 8;
        List<CoordBean> safe = new ArrayList<>();
        List<SquareGenBean> unsafe = new ArrayList<>();
        for (int x = 0; x < CompConstLogic.SQUARES_PER_REGION; x++)
            for (int y = 0; y < CompConstLogic.SQUARES_PER_REGION; y++)
            {
                SquareGenBean sq = (SquareGenBean)region.getSquares()[x][y];
                if (sq.isAnyRoads())
                {
                    sq.setChallenge(1);
                    safe.add(sq.getOrds());
                }
                else
                    unsafe.add(sq);
            }
        for (SquareGenBean sq : unsafe)
        {
            int d = nearestSafe(sq.getOrds(), safe);
            int cr = d/3;
            cr += CompConstLogic.TABLE_TERRAIN_CHALLENGE[sq.getTerrain()];
            if (cr < 1)
                cr = 1;
            else if (cr > maxChallenge)
                cr = maxChallenge;
            sq.setChallenge(cr);
            int c2;
            if (d < 2)
                c2 = 0;
            else
                c2 = ((d-2)/5) + 1;
            if (c2 > 5)
                c2 = 5;
            sq.setChallenge2(c2);
        }
    }

    private static int nearestSafe(CoordBean ords, List<CoordBean> list)
    {
        int best = -1;
        for (CoordBean o : list)
        {
            int d = ords.dist(o);
            if ((best < 0) || (d < best))
                best = d;
        }
        return best;
    }
    
    private static void generateRegionAnarchyChallenge(RegionBean region, Random rnd)
    {
        double period1x = Math.PI/CompConstLogic.SQUARES_PER_REGION*DiceRollBean.roll(rnd, 1, 4);
        double period1y = Math.PI/CompConstLogic.SQUARES_PER_REGION*DiceRollBean.roll(rnd, 1, 4);
        double period2x = Math.PI/CompConstLogic.SQUARES_PER_REGION*DiceRollBean.roll(rnd, 1, 4);
        double period2y = Math.PI/CompConstLogic.SQUARES_PER_REGION*DiceRollBean.roll(rnd, 1, 4);
        double period3x = Math.PI/CompConstLogic.SQUARES_PER_REGION*DiceRollBean.roll(rnd, 1, 4);
        double period3y = Math.PI/CompConstLogic.SQUARES_PER_REGION*DiceRollBean.roll(rnd, 1, 4);
        for (int x = 0; x < CompConstLogic.SQUARES_PER_REGION; x++)
            for (int y = 0; y < CompConstLogic.SQUARES_PER_REGION; y++)
            {
                SquareGenBean sq = (SquareGenBean)region.getSquares()[x][y];
                double f = Math.sin(x*period1x) + Math.sin(y*period1y)
                + Math.sin(x*period2x) + Math.sin(y*period2y)
                + Math.sin(x*period3x) + Math.sin(y*period3y);
                f = Math.abs(f/6);
                int cr = (int)(f*16) + 1;
                cr += CompConstLogic.TABLE_TERRAIN_CHALLENGE[sq.getTerrain()];
                if (cr < 1)
                    cr = 1;
                sq.setChallenge(cr);
                int c2 = (int)(f*6);
                if (c2 > 5)
                    c2 = 5;
                if (c2 < 0)
                    c2 = 0;
                sq.setChallenge2(c2);
            }
    }

}
