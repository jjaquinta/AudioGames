package jo.audio.companions.tools.gui.edit.rich;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import jo.audio.companions.tools.gui.edit.data.PFeatureBean;
import jo.audio.companions.tools.gui.edit.data.PModuleBean;
import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.companions.tools.gui.edit.logic.SelectionLogic;
import jo.audio.companions.tools.gui.edit.ui.PCSComboBox;
import jo.audio.companions.tools.gui.edit.ui.PCSTextField;

public class RichClientPanel extends JPanel implements PropertyChangeListener
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean             mRuntime;

    private JButton                 mModuleInfo;
    private PCSTextField            mModuleName;
    private PCSComboBox<PFeatureBean> mFeature;
    private JButton                 mFeatureAdd;
    private JButton                 mFeatureDel;
    private FeatureEditPanel        mFeatureClient;
    
    public RichClientPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mModuleInfo = new JButton("(i)");
        mModuleInfo.setToolTipText("Update module info");
        mModuleName = new PCSTextField(mRuntime, "locationRich.name", null);
        mModuleName.setEditable(false);
        Font oldFont = mModuleName.getFont();
        mModuleName.setFont(new Font(oldFont.getName(), oldFont.getStyle(), oldFont.getSize() + 4));
        mFeature = new PCSComboBox<PFeatureBean>(mRuntime, "locationRich.features", mRuntime, "selectedFeature", 
                (feature) -> SelectionLogic.selectFeature(mRuntime, (feature == null) ? -1 : feature.getOID()));
        mFeatureAdd = new JButton("+");
        mFeatureAdd.setToolTipText("Add a new feature");
        mFeatureDel = new JButton("-");
        mFeatureDel.setToolTipText("Delete selected feature");
        mFeatureClient = new FeatureEditPanel(mRuntime);
    }

    private void initLayout()
    {
        JPanel moduleInfo = new JPanel();
        moduleInfo.setLayout(new BorderLayout());
        moduleInfo.add("West", mModuleInfo);
        moduleInfo.add("Center", mModuleName);

        JPanel featureTools = new JPanel();
        featureTools.setLayout(new GridLayout(1, 2));
        featureTools.add(mFeatureAdd);
        featureTools.add(mFeatureDel);
        
        JPanel featureInfo = new JPanel();
        featureInfo.setLayout(new BorderLayout());
        featureInfo.add("Center", mFeature);
        featureInfo.add("East", featureTools);
        
        JPanel northBar = new JPanel();
        northBar.setLayout(new GridLayout(1, 2));
        northBar.add(moduleInfo);
        northBar.add(featureInfo);
        
        setLayout(new BorderLayout());
        add("North", northBar);
        add("Center", mFeatureClient);
    }

    private void initLink()
    {
        mModuleInfo.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doModuleInfo();
            }
        });
        mFeatureAdd.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doFeatureAdd();
            }
        });
        mFeatureDel.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doFeatureDel();
            }
        });
        mRuntime.addPropertyChangeListener("locationRich", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateModule((PModuleBean)evt.getOldValue(), (PModuleBean)evt.getNewValue());
            }
        });
    }
    
    private void doModuleInfo()
    {
        ModuleDetailPanel client = new ModuleDetailPanel(mRuntime);
        JOptionPane.showConfirmDialog((Frame)SwingUtilities.getWindowAncestor(this), 
                client, "Module Details", JOptionPane.OK_CANCEL_OPTION);
    }
    
    private void doFeatureAdd()
    {
        
    }
    
    private void doFeatureDel()
    {
        
    }

    public void updateModule(PModuleBean oldData, PModuleBean newData)
    {
        if (oldData != null)
            oldData.removePropertyChangeListener(this);
        if (newData != null)
            newData.addPropertyChangeListener(this);
        else
            return;
//        updateName();
        updateFeatures();
    }

    public void updateFeatures()
    {
        mFeature.setModel(new DefaultComboBoxModel<PFeatureBean>(mRuntime.getLocationRich().getFeatures().toArray(new PFeatureBean[0])));
    }

//    public void updateName()
//    {
//        mModuleName.setText(mRuntime.getLocationRich().getName());
//    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        switch (evt.getPropertyName())
        {
            case "name":
                //updateName();
                break;
            case "features":
                updateFeatures();
                break;
        }
    }
}
