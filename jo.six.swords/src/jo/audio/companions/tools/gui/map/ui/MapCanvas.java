package jo.audio.companions.tools.gui.map.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.tools.gui.map.MapData;
import jo.audio.companions.tools.gui.map.logic.MapTileLogic;

@SuppressWarnings("serial")
public class MapCanvas extends JComponent
{
    private MapData mData;
    private Point   mMouseDown;
    private long    mLastScale;

    public MapCanvas(MapData data)
    {
        mData = data;
        addMouseListener(new MouseListener() {            
            @Override
            public void mouseReleased(MouseEvent e)
            {
                doMouseReleased(e);
            }
            @Override
            public void mousePressed(MouseEvent e)
            {
                doMousePressed(e);
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                doMouseExited(e);
            }
            @Override
            public void mouseEntered(MouseEvent e)
            {
            }
            @Override
            public void mouseClicked(MouseEvent e)
            {
            }
        });
        addMouseMotionListener(new MouseMotionListener() {            
            @Override
            public void mouseMoved(MouseEvent e)
            {
            }
            @Override
            public void mouseDragged(MouseEvent e)
            {
                doMouseDragged(e);
            }
        });
        addMouseWheelListener(new MouseWheelListener() {            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                doMouseWheelMoved(e);
            }
        });
    }
    
    private void doMousePressed(MouseEvent e)
    {
        mMouseDown = e.getPoint();
    }
    
    private void doMouseReleased(MouseEvent e)
    {
        scrollTo(e.getPoint());
        mMouseDown = null;
    }
    
    private void doMouseExited(MouseEvent e)
    {
        mMouseDown = null;
    }
    
    private void doMouseDragged(MouseEvent e)
    {
        scrollTo(e.getPoint());
    }
    
    private void doMouseWheelMoved(MouseWheelEvent e)
    {
        long now = System.currentTimeMillis();
        if (mLastScale + 1000L > now)
            return;
        mLastScale = now;
        if (e.getWheelRotation() > 0)
        {
            if (mData.getScale() < -3)
                return;
            else if (mData.getScale() < -1)
                mData.setScale(mData.getScale() + 1);
            else if (mData.getScale() < 4)
                mData.setScale(4);
            else
                mData.setScale(mData.getScale()*3/2);
            MapTileLogic.clearCache();
            repaint();
        }
        else if (e.getWheelRotation() < 0)
        {
            if (mData.getScale() > 4)
                mData.setScale(mData.getScale()*2/3);
            else if (mData.getScale() > -1)
                mData.setScale(-1);
            else if (mData.getScale() > -3)
                mData.setScale(mData.getScale() - 1);
            else
                return;
            MapTileLogic.clearCache();
            repaint();
        }
    }
    
    private void scrollTo(Point p)
    {
        int dx = 0;
        int scale = mData.getPixelScale();
        dx = (p.x - mMouseDown.x)/scale;
        int dy = 0;
        dy = (p.y - mMouseDown.y)/scale;
        mMouseDown.x += dx*scale;
        mMouseDown.y += dy*scale;
        mData.setX(mData.getX() - dx);
        mData.setY(mData.getY() - dy);
        repaint();
    }
    
    @Override
    public void paint(Graphics g1)
    {
        Graphics2D g = (Graphics2D)g1;
        Dimension s = getSize();
        Rectangle screen = new Rectangle(0, 0, s.width, s.height);
        int scale = mData.getPixelScale();
        Rectangle tileBase  = new Rectangle(0, 0, scale*MapTileLogic.TILE_WIDTH, scale*MapTileLogic.TILE_HEIGHT);
        for (int x = 0; x < CompConstLogic.MAX_ENUMA_LOCATION_X; x += MapTileLogic.TILE_WIDTH)
            for (int y = 0; y < CompConstLogic.MAX_ENUMA_LOCATION_Y; y += MapTileLogic.TILE_HEIGHT)
            {
                Rectangle r  = new Rectangle(tileBase);
                r.translate(x*scale, y*scale);
                r.translate(-mData.getX()*scale, -mData.getY()*scale);
                if (!r.intersects(screen))
                    continue;
                BufferedImage tile = MapTileLogic.getFromCache(x, y, mData);
                g.drawImage(tile, r.x, r.y, null);
//                g.setColor(Color.RED);
//                g.draw(r);
//                g.drawLine(r.x, r.y, r.x+r.width, r.y+r.height);
//                g.drawLine(r.x, r.y+r.height, r.x+r.width, r.y);
            }
    }
}
