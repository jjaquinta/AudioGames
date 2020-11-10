package jo.audio.companions.logic.gen;

import java.util.ArrayList;
import java.util.List;

import jo.audio.companions.data.SquareBean;
import jo.audio.companions.data.SquareGenBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.util.astar.AStarNode;

class SquareNode extends AStarNode
{
    private static SquareNode[][] mCache = new SquareNode[CompConstLogic.SQUARES_PER_REGION][CompConstLogic.SQUARES_PER_REGION];

    public static void init()
    {
        mCache = new SquareNode[CompConstLogic.SQUARES_PER_REGION][CompConstLogic.SQUARES_PER_REGION];
    }

    public static void term()
    {
        mCache = new SquareNode[CompConstLogic.SQUARES_PER_REGION][CompConstLogic.SQUARES_PER_REGION];
    }
    
    public static SquareNode getInstance(SquareBean[][] squares, int x, int y)
    {
        if (mCache[x][y] == null)
            mCache[x][y] = new SquareNode(squares, x, y);
        return mCache[x][y];
    }
    
    private SquareBean[][] mSquares;
    public int mX;
    public int mY;
    public SquareGenBean mS;
    
    public SquareNode(SquareBean[][] squares, int x, int y)
    {
        mSquares = squares;
        mX = x;
        mY = y;
        mS = (SquareGenBean)mSquares[mX][mY];
    }

    @Override
    public String toString()
    {
        return "("+mX+","+mY+")";
    }
    
    @Override
    public float getCost(AStarNode n2)
    {
        SquareNode s2 = (SquareNode)n2;
        return getCost(mX, mY, s2.mX, s2.mY);
    }

    private float getCost(int x1, int y1, int x2, int y2)
    {
        int d = Math.abs(x1 - x2) + Math.abs(y1 - y2);
        float dist = d*CompConstLogic.TABLE_TERRAIN_COST[mSquares[x1][y1].getTerrain()]*CompConstLogic.TABLE_TERRAIN_COST[mSquares[x2][y2].getTerrain()];
        if (mSquares[x1][y1].isAnyRoads() || mSquares[x2][y2].isAnyRoads())
            dist /= 8;
        return dist;
    }

    @Override
    public float getEstimatedCost(AStarNode n2)
    {
        SquareNode s2 = (SquareNode)n2;
        int x = mX;
        int y = mY;
        float cost = 0;
        while ((x != s2.mX) && (y != s2.mY))
        {
            int dx = s2.mX - x;
            int dy = s2.mY - y;
            int nx = x;
            int ny = y;
            if (Math.abs(dx) > Math.abs(dy))
                nx += (int)Math.signum(dx);
            else
                ny += (int)Math.signum(dy);
            cost += getCost(x, y, nx, ny);
            x = nx;
            y = ny;
        }
        return cost;
    }
    
    @Override
    public List<AStarNode> getNeighbors()
    {
        List<AStarNode> neighbors = new ArrayList<AStarNode>();
        addNeighbor(neighbors, -1, 0);
        addNeighbor(neighbors, 1, 0);
        addNeighbor(neighbors, 0, -1);
        addNeighbor(neighbors, 0, 1);
        return neighbors;
    }

    private void addNeighbor(List<AStarNode> neighbors, int dx, int dy)
    {
        int x = mX + dx;
        int y = mY + dy;
        if ((x < 0) || (x >= CompConstLogic.SQUARES_PER_REGION))
            return;
        if ((y < 0) || (y >= CompConstLogic.SQUARES_PER_REGION))
            return;
        SquareNode node = getInstance(mSquares, mX + dx, mY + dy);
        neighbors.add(node);
    }

    public void addBorderRoad()
    {
        if (mY == 0)
            mS.setRoadNorth(SquareBean.T_ROAD);
        else if (mY == CompConstLogic.SQUARES_PER_REGION - 1)
            mS.setRoadSouth(SquareBean.T_ROAD);
        if (mX == 0)
            mS.setRoadWest(SquareBean.T_ROAD);
        else if (mX == CompConstLogic.SQUARES_PER_REGION - 1)
            mS.setRoadEast(SquareBean.T_ROAD);
    }

    public boolean isBorder()
    {
        return ((mX == 0) || (mY == 0) || (mX == CompConstLogic.SQUARES_PER_REGION-1) || (mY == CompConstLogic.SQUARES_PER_REGION-1));
    }
    
}