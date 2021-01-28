package jo.audio.companions.tools.gui.edit.rich;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jo.audio.companions.data.CompMonsterTypeBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.MonsterLogic;
import jo.audio.companions.tools.ParseMonsters;
import jo.util.utils.obj.StringUtils;

@SuppressWarnings("serial")
public class MonsterPickerDlg extends JDialog
{
    private JComboBox<String> mTerrain;
    private JComboBox<String> mChallenge;
    private JComboBox<String> mType;
    private JTextField        mName;
    private MonstDetailPanel mDetails;
    private JList<CompMonsterTypeBean>     mMonsters;
    private JOptionPane mClient;
    
    private List<CompMonsterTypeBean> mFiltered = new ArrayList<>();
    
    private CompMonsterTypeBean mMonster;

    public MonsterPickerDlg(Frame f)
    {
        super(f, "Pick Monster", true);
        initInstantiate();
        initLayout();
        initLink();
        updateMonsters();
    }

    private void initInstantiate()
    {
        mTerrain = new JComboBox<>();
        mTerrain.addItem("");
        for (String t : CompConstLogic.TERRAIN_NAMES)
            mTerrain.addItem(t);
        mChallenge = new JComboBox<>();
        mChallenge.addItem("");
        for (int i = 1; i < 24; i++)
            mChallenge.addItem("C"+i);
        mType = new JComboBox<>();
        mType.addItem("");
        for (String t : ParseMonsters.TYPEs)
            mType.addItem(t);
        mName = new JTextField();
        mDetails = new MonstDetailPanel();
        mMonsters = new JList<>();
        mMonsters.setModel(new DefaultListModel<>());
        mMonsters.setCellRenderer(new DefaultListCellRenderer(){
            private static final long serialVersionUID = -8701586005193853156L;
            
            @SuppressWarnings("rawtypes")
            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus)
            {
                CompMonsterTypeBean monst = (CompMonsterTypeBean)value;
                String name = monst.getName()+" "+monst.getNumAtt()+" (CR "+MonsterLogic.getChallengeRating(monst)+")";
                return super.getListCellRendererComponent(list, name, index, isSelected,
                        cellHasFocus);
            }
        });
        mClient = new JOptionPane(
                null,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                null,
                null
                );
    }

    private void initLayout()
    {
        JPanel filter1 = new JPanel();
        filter1.setLayout(new BorderLayout());
        filter1.add("West", new JLabel("Terrain:"));
        filter1.add("Center", mTerrain);
        JPanel filter2 = new JPanel();
        filter2.setLayout(new BorderLayout());
        filter2.add("West", new JLabel("Challenge:"));
        filter2.add("Center", mChallenge);
        JPanel filter3 = new JPanel();
        filter3.setLayout(new BorderLayout());
        filter3.add("West", new JLabel("Type:"));
        filter3.add("Center", mType);
        JPanel filter4 = new JPanel();
        filter4.setLayout(new BorderLayout());
        filter4.add("West", new JLabel("Name:"));
        filter4.add("Center", mName);
        JPanel filters = new JPanel();
        filters.setLayout(new GridLayout(2, 2));
        filters.add(filter1);
        filters.add(filter2);
        filters.add(filter3);
        filters.add(filter4);
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(1, 2));
        center.add(new JScrollPane(mMonsters));
        center.add(mDetails);
        JPanel client = new JPanel();
        client.setLayout(new BorderLayout());
        client.add("North", filters);
        client.add("Center", center);
        mClient.setMessage(client);
        setContentPane(mClient);
        pack();
    }

    private void initLink()
    {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e)
            {
                mTerrain.requestFocusInWindow();
            }
        });
        mClient.addPropertyChangeListener("value", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doClick((Integer)evt.getNewValue());
            }
        });
        mTerrain.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                updateMonsters();
            }
        });
        mChallenge.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                updateMonsters();
            }
        });
        mType.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                updateMonsters();
            }
        });
        mName.addKeyListener(new KeyAdapter() {            
            @Override
            public void keyTyped(KeyEvent e)
            {
                updateMonsters();
            }
        });
        mMonsters.addListSelectionListener(new ListSelectionListener() {            
            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                mDetails.setMonster(mMonsters.getSelectedValue());
            }
        });
    }
    
    private void doClick(int value)
    {
        if (value == 0)
        {   // OK
            mMonster = mMonsters.getSelectedValue();
        }
        else
            mMonster = null;
        setVisible(false);
        dispose();
    }
    
    private void updateMonsters()
    {
        mFiltered.clear();
        Set<String> done = new HashSet<>();
        String terrain = ((String)mTerrain.getSelectedItem()).toLowerCase();
        String type = ((String)mType.getSelectedItem()).toLowerCase();
        String name = mName.getText().toLowerCase();
        int challenge = mChallenge.getSelectedIndex();
        for (CompMonsterTypeBean monster : MonsterLogic.getAllTypes())
        {
            if (done.contains(monster.getID()))
                continue;
            done.add(monster.getID());
            if (!StringUtils.isTrivial(terrain))
                if (!"any".equalsIgnoreCase(monster.getTerrain()) && monster.getTerrain().toLowerCase().indexOf(terrain) < 0)
                    continue;
            if (!StringUtils.isTrivial(type))
                if (!monster.getType().equalsIgnoreCase(type))
                    continue;
            if (!StringUtils.isTrivial(name))
                if (monster.getName().toLowerCase().indexOf(name) < 0)
                    continue;
            if (challenge > 0)
                if (MonsterLogic.getChallengeRating(monster) != challenge)
                    continue;
            mFiltered.add(monster);
        }
        Collections.sort(mFiltered, new Comparator<CompMonsterTypeBean>() {
            @Override
            public int compare(CompMonsterTypeBean o1, CompMonsterTypeBean o2)
            {
                return o1.getName().compareTo(o2.getName());
            }
        });
        ((DefaultListModel<CompMonsterTypeBean>)mMonsters.getModel()).clear();
        for (CompMonsterTypeBean monster : mFiltered)
            ((DefaultListModel<CompMonsterTypeBean>)mMonsters.getModel()).addElement(monster);
    }
    
    public static CompMonsterTypeBean pickMonster(Component comp)
    {
        MonsterPickerDlg dlg = new MonsterPickerDlg((Frame)SwingUtilities.getAncestorOfClass(Frame.class, comp));
        dlg.setVisible(true);
        return dlg.getMonster();
    }

    public CompMonsterTypeBean getMonster()
    {
        return mMonster;
    }

    public void setMonster(CompMonsterTypeBean monster)
    {
        mMonster = monster;
    }
}
