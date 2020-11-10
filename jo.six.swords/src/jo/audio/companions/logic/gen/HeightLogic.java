package jo.audio.companions.logic.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.RegionGenBean;
import jo.audio.companions.data.SquareGenBean;
import jo.audio.companions.logic.CompConstLogic;

public class HeightLogic
{

    static List<SquareGenBean> generateRegionHeights(RegionGenBean region,
            Random rnd)
    {
        float roughness = CompConstLogic.TABLE_ROUGHNESS_TERRAIN[region.getPredominantTerrain()];
        int[][] heightmap = generateHeightmap(rnd, region.getOrds(), roughness);
        List<SquareGenBean> squares = new ArrayList<>();
        for (int dx = 0; dx < CompConstLogic.SQUARES_PER_REGION; dx++)
            for (int dy = 0; dy < CompConstLogic.SQUARES_PER_REGION; dy++)
            {
                CoordBean ords = new CoordBean(region.getOrds(), dx, dy);
                SquareGenBean square = new SquareGenBean();
                square.setOrds(ords);
                square.setAltitude(heightmap[dx][dy]);
                region.setSquare(square.getOrds(), square);
                squares.add(square);
            }
        return squares;
    }

    private static final int H = 192;
    private static final int L = 64;
    private static final int M = (H + L)/2;
    
    private static int[][] generateHeightmap(Random rnd, CoordBean ords, float roughness)
    {
        int[][] hm = new int[CompConstLogic.SQUARES_PER_REGION+1][CompConstLogic.SQUARES_PER_REGION+1];
        hm[0][0] = H;
        hm[0][CompConstLogic.SQUARES_PER_REGION] = H;
        hm[CompConstLogic.SQUARES_PER_REGION][0] = H;
        hm[CompConstLogic.SQUARES_PER_REGION][CompConstLogic.SQUARES_PER_REGION] = H;
        hm[0][CompConstLogic.SQUARES_PER_REGION/2] = M;
        hm[CompConstLogic.SQUARES_PER_REGION/2][0] = M;
        hm[CompConstLogic.SQUARES_PER_REGION][CompConstLogic.SQUARES_PER_REGION/2] = M;
        hm[CompConstLogic.SQUARES_PER_REGION/2][CompConstLogic.SQUARES_PER_REGION] = M;
        hm[CompConstLogic.SQUARES_PER_REGION/2][CompConstLogic.SQUARES_PER_REGION/2] = L;
        subDivide(rnd, hm, ords, 0, 0, CompConstLogic.SQUARES_PER_REGION/2, CompConstLogic.SQUARES_PER_REGION/2, roughness);
        subDivide(rnd, hm, ords, CompConstLogic.SQUARES_PER_REGION/2, 0, CompConstLogic.SQUARES_PER_REGION, CompConstLogic.SQUARES_PER_REGION/2, roughness);
        subDivide(rnd, hm, ords, 0, CompConstLogic.SQUARES_PER_REGION/2, CompConstLogic.SQUARES_PER_REGION/2, CompConstLogic.SQUARES_PER_REGION, roughness);
        subDivide(rnd, hm, ords, CompConstLogic.SQUARES_PER_REGION/2, CompConstLogic.SQUARES_PER_REGION/2, CompConstLogic.SQUARES_PER_REGION, CompConstLogic.SQUARES_PER_REGION, roughness);
        return hm;
   }

    private static void subDivide(Random rnd, int[][] hm, CoordBean ords, int left, int top, int right, int bottom, float roughness)
    {
        if ((left + 1 >= right) && (top + 1 >= bottom))
            return;
        int w = left + (right - left)/2;
        int h = top + (bottom - top)/2;
        tween(rnd, hm, ords, left, top, right, top, w, top, roughness);
        tween(rnd, hm, ords, left, top, left, bottom, left, h, roughness);
        tween(rnd, hm, ords, right, top, right, bottom, right, h, roughness);
        tween(rnd, hm, ords, left, bottom, right, bottom, w, bottom, roughness);
        //TODO: 4 way tween
        tween(rnd, hm, ords, left, top, right, bottom, w, h, roughness);
        //TODO: recurse
        subDivide(rnd, hm, ords, left, top, w, h, roughness);
        subDivide(rnd, hm, ords, w, top, right, h, roughness);
        subDivide(rnd, hm, ords, left, h, w, bottom, roughness);
        subDivide(rnd, hm, ords, w, h, right, bottom, roughness);
    }

    private static void tween(Random rnd, int[][] hm, CoordBean ords, int x1, int y1, int x2, int y2, int x, int y, float roughness)
    {
        //int x = (x1 + x2)/2;
        //int y = (y1 + y2)/2;
        if (hm[x][y] > 0)
            return;
        int h1 = hm[x1][y1];
        int h2 = hm[x2][y2];
        int h = (h1 + h2)/2;
        int range = Math.abs(h1 - h2);
        Random r = rnd;
        if (((x1%CompConstLogic.SQUARES_PER_REGION) == 0) || ((y1%CompConstLogic.SQUARES_PER_REGION) == 0)
                || ((x2%CompConstLogic.SQUARES_PER_REGION) == 0) || ((y2%CompConstLogic.SQUARES_PER_REGION) == 0))
            r = CompConstLogic.getRandom(new CoordBean(ords.getX() + x1, ords.getY() + y1, ords.getZ()), new CoordBean(ords.getX() + x2, ords.getY() + y2, ords.getZ()));
        h += (int)((r.nextFloat() - 0.5f)*range*roughness);
        hm[x][y] = h;
        //System.out.println(x+","+y+"="+h1+"|"+h2+"->"+h);
    }

}
