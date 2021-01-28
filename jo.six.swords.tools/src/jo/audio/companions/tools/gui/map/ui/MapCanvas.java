package jo.audio.companions.tools.gui.map.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.DemenseBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.tools.gui.map.MapData;
import jo.audio.companions.tools.gui.map.logic.BorderDrawLogic;
import jo.audio.companions.tools.gui.map.logic.MapDrawLogic;
import jo.audio.companions.tools.gui.map.logic.MapTileLogic;
import jo.util.utils.obj.StringUtils;

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
                doMouseMoved(e);
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
        if (e.getButton() == MouseEvent.BUTTON3)
        {
            doPopUpMenu(e);
            return;
        }
        mMouseDown = e.getPoint();
    }
    
    private void doMouseReleased(MouseEvent e)
    {
        if (mMouseDown != null)
            scrollTo(e.getPoint());
        mMouseDown = null;
    }
    
    private void doMouseExited(MouseEvent e)
    {
        mMouseDown = null;
    }
    
    private void doMouseDragged(MouseEvent e)
    {
        if (mMouseDown != null)
            scrollTo(e.getPoint());
    }
    
    private void doMouseMoved(MouseEvent e)
    {
        mData.setHover(getSquareAt(e));
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
        //System.out.println("Screen: "+screen);
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
                //System.out.println("draw "+x+","+y+" -> "+r.getX()+","+r.getY());
                BufferedImage tile = MapTileLogic.getFromCache(x, y, mData);
                g.drawImage(tile, r.x, r.y, null);
                g.setColor(Color.RED);
                g.draw(r);
//                g.drawLine(r.x, r.y, r.x+r.width, r.y+r.height);
//                g.drawLine(r.x, r.y+r.height, r.x+r.width, r.y);
            }
        if (mData.getHover() != null)
        {
            Point p = getPointAt(mData.getHover().getOrds());
            Rectangle r = new Rectangle(p.x, p.y, scale, scale);
            g.setColor(Color.RED);
            g.draw(r);
        }
    }
    
    private void doPopUpMenu(MouseEvent ev)
    {
        SquareBean sq = getSquareAt(ev);
        if (sq == null)
            return;
        //System.out.println("map "+ev.getX()+","+ev.getY()+" -> "+sq.getOrds()+" terrain="+sq.getTerrain()+" feature="+sq.getFeature());
        JPopupMenu menu = new JPopupMenu();
        ActionListener showOnly = new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String id = e.getActionCommand();
                if (StringUtils.isTrivial(id))
                    mData.setDrawOnlyDemense(null);
                else
                    mData.setDrawOnlyDemense(id);
                MapTileLogic.clearCache();
                mData.clearDrawBounds();
                repaint();
            }
        };
        for (DemenseBean d = sq.getDemense(); d != null; d = d.getLiege())
        {
            String name = BorderDrawLogic.findName(d);
            JMenuItem mi = new JMenuItem("Show only "+name);
            mi.setActionCommand(d.getID());
            mi.addActionListener(showOnly);
            menu.add(mi);
        }
        //menu.add(JSeparator());
        JMenuItem mi = new JMenuItem("Show all");
        mi.setActionCommand("");
        mi.addActionListener(showOnly);
        menu.add(mi);
        menu.show(this, ev.getX(), ev.getY());
    }
    
    public Point getPointAt(CoordBean ords)
    {
        return getPointAt(ords.getX(), ords.getY());
    }
    public Point getPointAt(int x, int y)
    {
        x += MapTileLogic.TILE_WIDTH/2;
        y += MapTileLogic.TILE_HEIGHT/2;
        int scale = mData.getPixelScale();
        Point p  = new Point(0, 0);
        p.translate(x*scale, y*scale);
        p.translate(-mData.getX()*scale, -mData.getY()*scale);
        return p;
    }

    public SquareBean getSquareAt(MouseEvent ev)
    {
        int scale = mData.getPixelScale();
        int x = ev.getX()/scale;
        int y = ev.getY()/scale;
        x -= MapTileLogic.TILE_WIDTH/2;
        y -= MapTileLogic.TILE_HEIGHT/2;
        SquareBean sq = MapDrawLogic.getSquareD(x, y);
        //if (sq != null)
        //    System.out.println("map "+ev.getX()+","+ev.getY()+" -> "+sq.getOrds());
        return sq;
    }
}
