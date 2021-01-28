package jo.audio.companions.tools.gui.explorer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jo.audio.companions.service.MapLogic;
import jo.util.utils.obj.StringUtils;

public class ExplorerPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = -810592860706850444L;
    
    private ExplorerBean mRuntime;
    private int          mMouseX;
    private int          mMouseY;

    private JButton mNorth;
    private JButton mSouth;
    private JButton mEast;
    private JButton mWest;
    private JButton mUp;
    private JButton mDown;
    private JButton mIn;
    private JButton mOut;
    private JButton mHome;
    private JButton mEnter;
    private JLabel  mClient;

    public ExplorerPanel(ExplorerBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
        doUpdateDisplay();
    }

    private void initInstantiate()
    {
        mClient = new JLabel();
        mNorth = new JButton("^ North");
        mSouth = new JButton("v South");
        mEast = new JButton("East >");
        mWest = new JButton("< West");
        mUp = new JButton("+Dimension");
        mDown = new JButton("-Dimension");
        mIn = new JButton("Zoom In");
        mOut = new JButton("Zoom Out");
        mHome = new JButton("Home");
        mEnter = new JButton("Enter");
    }

    private void initLayout()
    {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new GridLayout(1, 8));
        toolbar.add(mHome);
        toolbar.add(mWest);
        toolbar.add(mNorth);
        toolbar.add(mSouth);
        toolbar.add(mEast);
        toolbar.add(mUp);
        toolbar.add(mDown);
        toolbar.add(mIn);
        toolbar.add(mOut);
        toolbar.add(mEnter);
        setLayout(new BorderLayout());
        add("North", toolbar);
        add("Center", mClient);
    }

    private void initLink()
    {
        mRuntime.addPropertyChangeListener(new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doUpdateDisplay();
            }
        });
        mNorth.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ExplorerLogic.north(mRuntime);
            }
        });
        mSouth.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ExplorerLogic.south(mRuntime);
            }
        });
        mEast.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ExplorerLogic.east(mRuntime);
            }
        });
        mWest.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ExplorerLogic.west(mRuntime);
            }
        });
        mIn.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ExplorerLogic.zoomIn(mRuntime);
            }
        });
        mOut.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ExplorerLogic.zoomOut(mRuntime);
            }
        });
        mUp.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ExplorerLogic.dimUp(mRuntime);
            }
        });
        mDown.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ExplorerLogic.dimDown(mRuntime);
            }
        });
        mHome.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ExplorerLogic.home(mRuntime);
            }
        });
        mEnter.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                ExplorerLogic.enter(mRuntime);
            }
        });
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e)
            {
                doMousePressed(e);
            }
            @Override
            public void mouseReleased(MouseEvent e)
            {
                doMouseReleased(e);
            }
            @Override
            public void mouseClicked(MouseEvent e)
            {
                doMouseClicked(e);
            }
        };
        mClient.addMouseListener(ma);
    }

    private void doUpdateDisplay()
    {
        Dimension d = mClient.getSize();
        if ((d.width == 0) || (d.height == 0))
            d = new Dimension(1024, 768);
        BufferedImage img = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        MapLogic.drawMap(img, mRuntime.getLocation(), mRuntime.getScale());
        mClient.setIcon(new ImageIcon(img));
    }
    private void doMousePressed(MouseEvent e)
    {
        mMouseX = e.getX();
        mMouseY = e.getY();
    }
    private void doMouseReleased(MouseEvent e)
    {
        int dx = e.getX() - mMouseX;
        int dy = e.getY() - mMouseY;
        if (Math.abs(dx) + Math.abs(dy) < 32)
            return;
        if (Math.abs(dx) > Math.abs(dy))
        {
            if (dx < 0)
                ExplorerLogic.west(mRuntime);
            else
                ExplorerLogic.east(mRuntime);
        }
        else
        {
            if (dy < 0)
                ExplorerLogic.north(mRuntime);
            else
                ExplorerLogic.south(mRuntime);
        }
    }
    public void doMouseClicked(MouseEvent e)
    {
        if (e.getClickCount() == 2)
        {
            if (StringUtils.isTrivial(mRuntime.getLocation().getRoomID()) && ExplorerLogic.enter(mRuntime))
                return;
            Dimension d = getSize();
            float x = (float)e.getX()/(float)d.width;
            float y = (float)e.getY()/(float)d.height;
            if (x > y)
                if (1f - x > y)
                    ExplorerLogic.north(mRuntime);
                else
                    ExplorerLogic.east(mRuntime);
            else
                if (1f - x > y)
                    ExplorerLogic.west(mRuntime);
                else
                    ExplorerLogic.south(mRuntime);
        }
    }

}
