package jo.audio.companions.tools.gui.edit.rich;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import jo.audio.companions.data.build.PFeatureBean;
import jo.audio.companions.data.build.PRoomBean;
import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.companions.tools.gui.edit.logic.SelectionLogic;
import jo.util.utils.ArrayUtils;
import jo.util.utils.obj.StringUtils;

public class MapPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
    
    private static final int NULL = 0;
    private static final int FLAT = 1;
    private static final int GRAPH = 2;
 
    private RuntimeBean         mRuntime;
    
    private PFeatureBean        mFeature;
    private int                 mMode;
    private PRoomBean[][]       mGrid;
    private Map<Shape, PRoomBean>   mRoomLocations = new HashMap<>();
    
    public MapPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
    }

    private void initLayout()
    {
    }

    private void initLink()
    {
        mRuntime.addPropertyChangeListener("selectedFeature", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateFeature();
            }
        });
        mRuntime.addPropertyChangeListener("selectedRoom", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateRoom();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                doMouseClicked(e.getX(), e.getY());
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e)
            {
                doMouseMove(e.getX(), e.getY());
            }
        });
    }
    
    private PRoomBean findRoom(int x, int y)
    {
        for (Shape s : mRoomLocations.keySet())
            if (s.contains(x, y))
                return mRoomLocations.get(s);
        return null;
    }
    
    private void doMouseClicked(int x, int y)
    {
        PRoomBean room = findRoom(x, y);
        if (room != null)
            SelectionLogic.selectRoom(mRuntime, room.getID());
    }
    
    private void doMouseMove(int x, int y)
    {
        PRoomBean room = findRoom(x, y);
        if (room != null)
            setToolTipText(room.getName());
        else
            setToolTipText(null);
    }
    
    private void updateFeature()
    {
        mFeature = mRuntime.getSelectedFeature();
        if (mFeature == null)
        {
            mMode = NULL;
        }
        else
        {
            mGrid = FeatureLogic.getGrid(mFeature);
            if (mGrid != null)
                mMode = FLAT;
            else
                mMode = GRAPH;
        }
        repaint();
    }
    
    private void updateRoom()
    {
        repaint();
    }
    
    @Override
    public void paint(Graphics g)
    {
        mRoomLocations.clear();
        if (mMode == NULL)
            super.paint(g);
        else if (mMode == GRAPH)
            super.paint(g);
        else if (mMode == FLAT)
            paintFlat(g);
    }
    
    private void paintFlat(Graphics g)
    {
        super.paint(g);
        Dimension size = getSize();
        int dx = size.width/mGrid[0].length;
        int dy = size.height/mGrid.length;
        int mx = dx/8;
        int my = dy/8;
        for (int y = 0; y < mGrid.length; y++)
            for (int x = 0; x < mGrid[y].length; x++)
            {
                if (mGrid[y][x] == null)
                    continue;
                int px = x*dx;
                int py = y*dy;
                List<Integer> ix = new ArrayList<>();
                List<Integer> iy = new ArrayList<>();
                ix.add(px + mx); iy.add(py + my);
                if (!StringUtils.isTrivial(mGrid[y][x].getNorth()))
                {
                    ix.add(px + mx*2); iy.add(py + my);
                    ix.add(px + mx*2); iy.add(py);
                    ix.add(px + dx - mx*2); iy.add(py);
                    ix.add(px + dx - mx*2); iy.add(py + my);
                }
                ix.add(px + dx - mx); iy.add(py + my);
                if (!StringUtils.isTrivial(mGrid[y][x].getEast()))
                {
                    ix.add(px + dx - mx); iy.add(py + my*2);
                    ix.add(px + dx); iy.add(py + my*2);
                    ix.add(px + dx); iy.add(py + dy - my*2);
                    ix.add(px + dx - mx); iy.add(py + dy - my*2);
                }
                ix.add(px + dx - mx); iy.add(py + dy - my);
                if (!StringUtils.isTrivial(mGrid[y][x].getSouth()))
                {
                    ix.add(px + dx - mx*2); iy.add(py + dy - my);
                    ix.add(px + dx - mx*2); iy.add(py + dy);
                    ix.add(px + mx*2); iy.add(py + dy);
                    ix.add(px + mx*2); iy.add(py + dy - my);
                }
                ix.add(px + mx); iy.add(py + dy - my);
                if (!StringUtils.isTrivial(mGrid[y][x].getWest()))
                {
                    ix.add(px + mx); iy.add(py + dy - my*2);
                    ix.add(px); iy.add(py + dy - my*2);
                    ix.add(px); iy.add(py + my*2);
                    ix.add(px + mx); iy.add(py + my*2);
                }
                int[] xPoints = ArrayUtils.toIntArray(ix);
                int[] yPoints = ArrayUtils.toIntArray(iy);
                Polygon p = new Polygon(xPoints, yPoints, xPoints.length);
                if (mGrid[y][x] == null)
                    g.setColor(Color.GRAY);
                else if (mGrid[y][x] == mRuntime.getSelectedRoom())
                    g.setColor(Color.BLUE);
                else
                    g.setColor(Color.WHITE);
                g.fillPolygon(p);
                g.setColor(Color.BLACK);
                g.drawPolygon(p);
                mRoomLocations.put(p, mGrid[y][x]);
            }
    }
}
