package jo.audio.companions.tools.gui.edit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import jo.audio.companions.data.build.PModuleBean;
import jo.audio.companions.tools.gui.edit.data.RuntimeBean;
import jo.audio.companions.tools.gui.edit.logic.RuntimeLogic;
import jo.audio.compedit.data.CompEditModuleBean;

public class LoadPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean mRuntime;
    
    private JTextPane   mClient;
    private JButton     mOpenFile;
    private JButton     mOpenData;

    public LoadPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mClient = new JTextPane();
        mClient.setContentType("text/html");
        mClient.setText(WELCOM);
        mClient.setEditable(false);
        mOpenFile = new JButton("Open File");
        mOpenData = new JButton("Open Data");
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", mClient);
        JPanel bottom = new JPanel();
        bottom.setLayout(new GridLayout(1, 2));
        bottom.add("West", mOpenFile);
        bottom.add("East", mOpenData);
        add("South", bottom);
    }

    private void initLink()
    {
        mOpenFile.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doOpenFile();
            }
        });
        mOpenData.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doOpenData();
            }
        });
    }
    
    private void doOpenFile()
    {
        FileFilter jsonFilter = new FileNameExtensionFilter(
                "JSON files", "json");
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(mRuntime.getLastDirectory());
        jFileChooser.setFileFilter(jsonFilter);
        int result = jFileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jFileChooser.getSelectedFile();
            RuntimeLogic.openFile(mRuntime, selectedFile);
        }
    }

    private void doOpenData()
    {
        RuntimeLogic.updateDynamoModules(mRuntime);
        boolean[] opts = new boolean[1];
        PModuleBean module = ModulePickerDlg.pickModule(this, mRuntime, opts);
        if (opts[0])
            RuntimeLogic.newDynamoModule(mRuntime);
        else if (module != null)
            RuntimeLogic.openDynamoModule(mRuntime, (CompEditModuleBean)module);
    }

    private static final String WELCOM = "<HTML>"
            + "<BODY>"
            + "<H1>TsaTsaTzu 6 Swords Location Editor</H1>"
            + "<P>This is for editing locations in 6 Swords. "
            + "It may affect live data and game play. "
            + "So be careful! </P>"
            + "<P>Select the data type to open below.</P>"            
            + "</BODY>"
            + "</HTML>";
}
