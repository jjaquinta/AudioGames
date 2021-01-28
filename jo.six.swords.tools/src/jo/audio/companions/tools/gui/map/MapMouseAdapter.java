package jo.audio.companions.tools.gui.map;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import jo.audio.companions.data.CoordBean;

public class MapMouseAdapter extends MouseAdapter
{
    private MapApp  mApp;
    private MapAssets  mAssets;
    private MapData mData;
    private static final int MAX_SCALE = 48*4;
    private static final int MIN_SCALE = 3;
    
    private int mMouseX;
    private int mMouseY;
    
    public MapMouseAdapter(MapApp app, MapAssets assets, MapData data)
    {
        mApp = app;
        mAssets = assets;
        mData = data;
    }
    
    private CoordBean getCoord(MouseEvent e)
    {
        int px = e.getX() - mData.getDX() - (int)mData.getSize().getWidth()/2;
        int py = e.getY() - mData.getDY() - (int)mData.getSize().getHeight()/2;;
        int x;
        if (px >= 0)
            x = px/mData.getScale();
        else
            x = (px - (mData.getScale() - 1))/mData.getScale();
        int y;
        if (py >= 0)
            y = py/mData.getScale();
        else
            y = (py - (mData.getScale() - 1))/mData.getScale();
        CoordBean ord = new CoordBean(mData.getX() + x, mData.getY() + y, mData.getZ());
        return ord;
    }
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        mouseDragged(e);
        mMouseX = -1;
        mMouseY = -1;
    }            
    @Override
    public void mousePressed(MouseEvent e)
    {
        mMouseX = e.getX();
        mMouseY = e.getY();
    }            
    @Override
    public void mouseExited(MouseEvent e)
    {
        mMouseX = -1;
        mMouseY = -1;
    }
    @Override
    public void mouseDragged(MouseEvent e)
    {
        int dx = mMouseX - e.getX();
        int dy = mMouseY - e.getY();
        mData.setDX(mData.getDX() - dx);
        mData.setDY(mData.getDY() - dy);
        while (mData.getDX() < 0)
        {
            mData.setDX(mData.getDX() + mData.getScale());
            mData.setX(mData.getX() + 1);
        }
        while (mData.getDX() >= mData.getScale())
        {
            mData.setDX(mData.getDX() - mData.getScale());
            mData.setX(mData.getX() - 1);
        }
        while (mData.getDY() < 0)
        {
            mData.setDY(mData.getDY() + mData.getScale());
            mData.setY(mData.getY() + 1);
        }
        while (mData.getDY() >= mData.getScale())
        {
            mData.setDY(mData.getDY() - mData.getScale());
            mData.setY(mData.getY() - 1);
        }
        mousePressed(e);
        mApp.repaint();
    }
    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        int dw = e.getWheelRotation();
        if (dw < 0)
        {
            if (mData.getScale() < MAX_SCALE)
                mData.setScale(mData.getScale()*2);
        }
        if (dw > 0)
        {
            if (mData.getScale() > MIN_SCALE)
                mData.setScale(mData.getScale()/2);
        }
        mApp.repaint();
    }
    @Override
    public void mouseMoved(MouseEvent e)
    {
        CoordBean ord = getCoord(e);
        mApp.updateStatus(ord);
    }
    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getClickCount() == 2)
        {
            CoordBean ord = getCoord(e);
            new SquareApp(mAssets, ord);
        }
    }
}
