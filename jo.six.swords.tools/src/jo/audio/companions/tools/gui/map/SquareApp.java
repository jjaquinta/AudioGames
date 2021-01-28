package jo.audio.companions.tools.gui.map;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.json.simple.JSONUtils;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.DemenseBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.GenerationLogic;
import jo.util.utils.obj.StringUtils;

public class SquareApp
{
    private MapAssets   mAssets;

    private CoordBean   mCoords;
    private SquareBean  mSquare;
    private FeatureBean mFeature;
    private CompRoomBean mRoom;
    
    private JButton     mNorth;
    private JButton     mSouth;
    private JButton     mEast;
    private JButton     mWest;
    private JTextField  mName;
    private JTextField  mID;
    private JTextField  mType;
    private JTextField  mDescription;
    private JTextArea   mParams;
    
    public SquareApp(MapAssets assets, CoordBean coords)
    {
        mAssets = assets;
        mCoords = coords;
        mSquare = GenerationLogic.getSquare(mCoords);
        if (mSquare.getFeature() != CompConstLogic.FEATURE_NONE)
            mFeature = FeatureLogic.getFeature(null, mSquare, null);
        List<DemenseBean> demenses = new ArrayList<>();
        for (DemenseBean d = mSquare.getDemense(); d != null; d = d.getLiege())
            demenses.add(d);
        StringBuffer ll = new StringBuffer();
        MapApp.toLonLat(ll, mCoords);
        JFrame frame = new JFrame("Square "+mCoords);
        JPanel infoBar = new JPanel();
        infoBar.setLayout(new GridLayout(6+demenses.size(), 2));
        infoBar.add(new JLabel("Ords:"));
        infoBar.add(new JLabel(mCoords.toString()+" / "+ll));
        infoBar.add(new JLabel("Name:"));
        infoBar.add(new JLabel((mFeature != null) ? mFeature.getName().getIdent()+"/"+mAssets.expandInserts(mFeature.getName()) : ""));
        infoBar.add(new JLabel("Terrain:"));
        infoBar.add(new JLabel(CompConstLogic.TERRAIN_NAMES[mSquare.getTerrain()]+" ("+mSquare.getTerrainDepth()+")"));
        infoBar.add(new JLabel("Challenge:"));
        infoBar.add(new JLabel(mSquare.getChallenge()+"/"+mSquare.getChallenge2()));
        infoBar.add(new JLabel("Road:"));
        infoBar.add(new JLabel("N:"+mSquare.getRoadNorth()+" S:"+mSquare.getRoadSouth()+" E:"+mSquare.getRoadEast()+" W:"+mSquare.getRoadWest()
                +" elev "+mSquare.getAltitude()));
        String lang = null;
        String pant = null;
        for (int i = 0; i < demenses.size(); i++)
        {
            DemenseBean d = demenses.get(i);
            if (i == 0)
                infoBar.add(new JLabel("Demense:"));
            else
                infoBar.add(new JLabel(""));
            infoBar.add(new JLabel(d.getID()+"/"+d.getName().getIdent()+"/"+mAssets.expandInserts(d.getName())));
            if (lang == null)
                lang = d.getLanguage();
            if (pant == null)
                pant = d.getPantheon();
        }
        infoBar.add(new JLabel("Ethnicity:"));
        infoBar.add(new JLabel(lang+"/"+pant));
        
        mName = new JTextField();
        mName.setEditable(false);
        mID = new JTextField();
        mID.setEditable(false);
        mType = new JTextField();
        mType.setEditable(false);
        mDescription = new JTextField();
        mDescription.setEditable(false);
        JPanel roomPanelBar = new JPanel();
        roomPanelBar.setLayout(new GridLayout(4, 2));
        roomPanelBar.add(new JLabel("Name:"));
        roomPanelBar.add(mName);
        roomPanelBar.add(new JLabel("ID:"));
        roomPanelBar.add(mID);
        roomPanelBar.add(new JLabel("Type:"));
        roomPanelBar.add(mType);
        roomPanelBar.add(new JLabel("Desc:"));
        roomPanelBar.add(mDescription);
        mParams = new JTextArea();
        mParams.setEditable(false);
        JPanel roomPanel = new JPanel();
        roomPanel.setLayout(new BorderLayout());
        roomPanel.add("North", roomPanelBar);
        roomPanel.add("Center", new JScrollPane(mParams));
        
        mNorth = new JButton("North");
        mSouth = new JButton("South");
        mEast = new JButton("East");
        mWest = new JButton("West");
        JPanel roomBar = new JPanel();
        roomBar.setLayout(new GridLayout(3, 3));
        roomBar.add(new JLabel(""));
        roomBar.add(mNorth);
        roomBar.add(new JLabel(""));
        roomBar.add(mWest);
        roomBar.add(roomPanel);
        roomBar.add(mEast);
        roomBar.add(new JLabel(""));
        roomBar.add(mSouth);
        roomBar.add(new JLabel(""));
        
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add("North", infoBar);
        frame.getContentPane().add("Center", roomBar);
        frame.setSize(768, 1024);
        frame.setVisible(true);
        
        if (mSquare.getFeature() != CompConstLogic.FEATURE_NONE)
        {
            focusRoom(mFeature.getEntranceID());
            for (CompRoomBean room : mFeature.getRooms())
                System.out.println("\t"+room.getID());
        } 
        
        mNorth.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                focusRoom(mRoom.getNorth());
            }
        });
        mSouth.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                focusRoom(mRoom.getSouth());
            }
        });
        mEast.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                focusRoom(mRoom.getEast());
            }
        });
        mWest.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                focusRoom(mRoom.getWest());
            }
        });
    }
    
    private void focusRoom(String id)
    {
        mRoom = FeatureLogic.findRoom(mFeature, id);
        mName.setText(mAssets.expandInserts(mRoom.getName()));
        mName.setToolTipText(mRoom.getName().toJSON().toJSONString());
        mID.setText(mRoom.getID());
        mType.setText(mRoom.getType());
        mDescription.setText(mAssets.expandInserts(mRoom.getDescription()));
        mDescription.setToolTipText(mRoom.getDescription().toJSON().toJSONString());
        mParams.setText(JSONUtils.toFormattedString(mRoom.getParams()));
        fillDirection(mNorth, mRoom.getNorth());
        fillDirection(mSouth, mRoom.getSouth());
        fillDirection(mEast, mRoom.getEast());
        fillDirection(mWest, mRoom.getWest());
    }

    public void fillDirection(JButton button, String id)
    {
        if (StringUtils.isTrivial(id))
        {
            button.setEnabled(false);
            button.setText("");
        }
        else if ("$exit".equals(id))
        {
            button.setEnabled(false);
            button.setText("<exit>");
        }
        else
        {
            CompRoomBean r = FeatureLogic.findRoom(mFeature, id);
            if (r == null)
            {
                System.err.println("Cannot find room '"+id+"'");
                return;
            }
            button.setEnabled(true);
            button.setText(mAssets.expandInserts(r.getName()));
        }
    }
}
