package jo.audio.companions.logic.gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.RegionGenBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.data.SquareGenBean;
import jo.audio.companions.logic.CompConstLogic;

public class TerrainLogic
{

    static void generateRegionTerrain(Random rnd, RegionGenBean region,
            List<SquareGenBean> squares)
    {
        Collections.sort(squares, new Comparator<SquareGenBean>() {
            @Override
            public int compare(SquareGenBean o1, SquareGenBean o2)
            {
                return (int)Math.signum(o2.getAltitude() - o1.getAltitude());
            }
        });
        Object[][] terrainMap = CompConstLogic.TABLE_TERRAIN_IN_TERRAIN[region.getPredominantTerrain()];
        int[] terrainSwap = new int[terrainMap.length];
        int tot = 0;
        for (int i = 0; i < terrainMap.length; i++)
            tot += ((Number)terrainMap[i][0]).intValue();
        int top = 0;
        for (int i = 0; i < terrainMap.length; i++)
        {
            int pc = ((Number)terrainMap[i][0]).intValue();
            int terrain = ((Number)terrainMap[i][1]).intValue();
            int incr = squares.size()*pc/tot;
            if (i == terrainMap.length)
                incr = squares.size() - top; // fix round off
            setTerrain(squares, top, top+incr, terrain);
            top += incr;
            terrainSwap[i] = terrain;
        }
        // swap chunks for varied effect
        for (int i = 0; i < terrainSwap.length - 1; i++)
        {
            int t1 = terrainSwap[i];
            int t2 = terrainSwap[i+1];
            for (int j = 0; j < CompConstLogic.TABLE_SWAP_MASKS.length; j++)
            {
                List<int[]> t1chunks = getTerrainChunks(region.getSquares(), t1, CompConstLogic.TABLE_SWAP_MASKS[j]);
                List<int[]> t2chunks = getTerrainChunks(region.getSquares(), t2, CompConstLogic.TABLE_SWAP_MASKS[j]);
                int todo = Math.min(t1chunks.size(), t2chunks.size())/50;
                if (todo > 10)
                    todo = 10;
                while (todo-- > 0)
                {
                    int t1idx = rnd.nextInt(t1chunks.size());
                    int[] t1ords = t1chunks.get(t1idx);
                    int t2idx = rnd.nextInt(t2chunks.size());
                    int[] t2ords = t2chunks.get(t2idx);
                    boolean doit = true;
                    if (!isTerrainChunk(region.getSquares(), t1ords[0], t1ords[1], t1, CompConstLogic.TABLE_SWAP_MASKS[j]))
                    {
                        t1chunks.remove(t1idx);
                        doit = false;
                    }
                    if (!isTerrainChunk(region.getSquares(), t2ords[0], t2ords[1], t2, CompConstLogic.TABLE_SWAP_MASKS[j]))
                    {
                        t2chunks.remove(t2idx);
                        doit = false;
                    }
                    if (doit)
                    {
                        t1chunks.remove(t1idx);
                        setTerrainChunk(region.getSquares(), t1ords[0], t1ords[1], t2, CompConstLogic.TABLE_SWAP_MASKS[j]);
                        t2chunks.remove(t2idx);
                        setTerrainChunk(region.getSquares(), t2ords[0], t2ords[1], t1, CompConstLogic.TABLE_SWAP_MASKS[j]);
                    }
                    else
                        todo++;
                }
            }
        }
    }
    

    private static void setTerrainChunk(SquareBean[][] squares, int x, int y,
            int terrain, int[][] mask)
    {
        for (int i = 0; i < mask.length; i++)
        {
            int dx = x + mask[i][0];
            int dy = y + mask[i][1];
            ((SquareGenBean)squares[dx][dy]).setTerrain(terrain);
        }
    }

    private static List<int[]> getTerrainChunks(SquareBean[][] squares, int terrain,
            int[][] mask)
    {
        List<int[]> chunks = new ArrayList<>();
        for (int dx = 0; dx < CompConstLogic.SQUARES_PER_REGION; dx++)
            for (int dy = 0; dy < CompConstLogic.SQUARES_PER_REGION; dy++)
                if (isTerrainChunk(squares, dx, dy, terrain, mask))
                    chunks.add(new int[] { dx, dy });
        return chunks;
    }

    private static boolean isTerrainChunk(SquareBean[][] squares, int x,
            int y, int terrain, int[][] mask)
    {
        for (int i = 0; i < mask.length; i++)
        {
            int dx = x + mask[i][0];
            int dy = y + mask[i][1];
            if ((dx < 0) || (dx >= CompConstLogic.SQUARES_PER_REGION))
                return false;
            if ((dy < 0) || (dy >= CompConstLogic.SQUARES_PER_REGION))
                return false;
            if (squares[dx][dy].getTerrain() != terrain)
                return false;
        }
        return true;
    }
    
    private static void setTerrain(List<SquareGenBean> squares, int from,
            int to, int terrain)
    {
        for (int i = from; i < to; i++)
            squares.get(i).setTerrain(terrain);
    }


    static void generateRegionTerrainDepth(RegionBean region)
    {
        // pass 1
        for (int x = 1; x < CompConstLogic.SQUARES_PER_REGION - 2; x++)
            for (int y = 1; y < CompConstLogic.SQUARES_PER_REGION - 2; y++)
            {
                if (region.getSquares()[x][y].getTerrain() != region.getSquares()[x+1][y].getTerrain())
                    continue;
                if (region.getSquares()[x][y].getTerrain() != region.getSquares()[x-1][y].getTerrain())
                    continue;
                if (region.getSquares()[x][y].getTerrain() != region.getSquares()[x][y+1].getTerrain())
                    continue;
                if (region.getSquares()[x][y].getTerrain() != region.getSquares()[x][y-1].getTerrain())
                    continue;
                ((SquareGenBean)region.getSquares()[x][y]).setTerrainDepth(1);
            }
        // pass 2
        for (int x = 1; x < CompConstLogic.SQUARES_PER_REGION - 2; x++)
            for (int y = 1; y < CompConstLogic.SQUARES_PER_REGION - 2; y++)
            {
                if (region.getSquares()[x][y].getTerrainDepth() != 1)
                    continue;
                if (1 > region.getSquares()[x+1][y].getTerrainDepth())
                    continue;
                if (1 > region.getSquares()[x-1][y].getTerrainDepth())
                    continue;
                if (1 > region.getSquares()[x][y+1].getTerrainDepth())
                    continue;
                if (1 > region.getSquares()[x][y-1].getTerrainDepth())
                    continue;
                ((SquareGenBean)region.getSquares()[x][y]).setTerrainDepth(2);
            }
    }

}
