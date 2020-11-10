package jo.audio.companions.tools.gui.edit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.companions.tools.gui.edit.json.JSONEditorPanel;
import jo.audio.companions.tools.gui.edit.logic.RuntimeLogic;
import jo.audio.companions.tools.gui.edit.rich.RichEditorPanel;
import jo.audio.companions.tools.gui.edit.text.TextEditorPanel;

public class MainPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean mRuntime;
 
    private JTabbedPane mTabs;
    private int         mCurrentTab;
    // tabs
    private TextEditorPanel       mTextEdit;
    private JSONEditorPanel       mJSONEdit;
    private RichEditorPanel       mRichEdit;
    private int mTextEditIndex;
    private int mJSONEditIndex;
    private int mRichEditIndex;
    
    private JButton mSave;
    private JButton mSaveAs;
    private JButton mClose;
    
    public MainPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mTextEdit = new TextEditorPanel(mRuntime);
        mJSONEdit = new JSONEditorPanel(mRuntime);
        mRichEdit = new RichEditorPanel(mRuntime);
        mSave = new JButton("Save");
        mSaveAs = new JButton("Save As");
        mClose = new JButton("Close");
    }

    private void initLayout()
    {
        mTabs = new JTabbedPane();
        mTextEditIndex = mTabs.getTabCount();
        mTabs.add("Text", mTextEdit);
        mJSONEditIndex = mTabs.getTabCount();
        mTabs.add("JSON", mJSONEdit);
        mRichEditIndex = mTabs.getTabCount();
        mTabs.add("Rich", mRichEdit);
        
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new GridLayout(1, 3));
        toolbar.add(mSave);
        toolbar.add(mSaveAs);
        toolbar.add(mClose);
        
        setLayout(new BorderLayout());
        add("Center", mTabs);
        add("South", toolbar);
        mCurrentTab = mTabs.getSelectedIndex();
        doShowTab();
    }

    private void initLink()
    {
        mTabs.addChangeListener(new ChangeListener() {            
            @Override
            public void stateChanged(ChangeEvent e)
            {
                doHideTab();
                mCurrentTab = mTabs.getSelectedIndex();
                doShowTab();
            }
        });
        mRuntime.addPropertyChangeListener("loaded", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doUpdateDisplay();
            }
        });
        mSave.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doSave();
            }
        });
        mSaveAs.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doSaveAs();
            }
        });
        mClose.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doClose();
            }
        });
    }
    
    private void doSave()
    {
        RuntimeLogic.save(mRuntime);
    }
    
    private void doSaveAs()
    {
        FileFilter jsonFilter = new FileNameExtensionFilter(
                "JSON files", "json");
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(mRuntime.getLastDirectory());
        jFileChooser.setFileFilter(jsonFilter);
        int result = jFileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jFileChooser.getSelectedFile();
            RuntimeLogic.saveAs(mRuntime, selectedFile);
        }
    }
    
    private void doClose()
    {
        RuntimeLogic.close(mRuntime);
    }

    private void doUpdateDisplay()
    {
        if (mRuntime.isLoaded())
            mTabs.setSelectedIndex(mRichEditIndex);
    }
    
    private void doHideTab()
    {
        if (mCurrentTab == mTextEditIndex)
            mTextEdit.beingHidden();
        else if (mCurrentTab == mJSONEditIndex)
            mJSONEdit.beingHidden();
        else if (mCurrentTab == mRichEditIndex)
            mRichEdit.beingHidden();
    }
    
    private void doShowTab()
    {
        if (mCurrentTab == mTextEditIndex)
            mTextEdit.beingShown();
        else if (mCurrentTab == mJSONEditIndex)
            mJSONEdit.beingShown();
        else if (mCurrentTab == mRichEditIndex)
            mRichEdit.beingShown();
    }
}
