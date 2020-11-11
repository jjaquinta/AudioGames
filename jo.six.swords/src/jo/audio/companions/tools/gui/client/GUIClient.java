package jo.audio.companions.tools.gui.client;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JTextArea;

import jo.audio.util.model.data.AudioResponseBean;
import jo.util.ui.swing.logic.FontUtils;
import jo.util.utils.obj.StringUtils;

public class GUIClient extends Frame
{
    private AudioResponseBean       mResponse;
    private String                  mLocale = "en_US";
    
    private JTextArea                mDialog;
    
    public GUIClient()
    {
        super("Six Swords");
        initInstantiate();
        initLayout();
        initLink();

        try
        {
            mResponse = RequestLogic.performLaunchRequest(mLocale);
            appendResponseToDialog();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void initInstantiate()
    {
        mDialog = new JTextArea(12, 40);
        mDialog.setEditable(true);
        //mDialog.setLineWrap(true);
        //mDialog.setWrapStyleWord(true);
        FontUtils.increaseFontSize(mDialog, 12);
        //mInput = new TextField();
        //FontUtils.increaseFontSize(mInput, 8);
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", mDialog);
        //add("South", mInput);
    }

    private void initLink()
    {
        mDialog.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e)
            {
                if (e.getKeyChar() == '\n')
                    doSend();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosed(e);
                try
                {
                    RequestLogic.performSessionEndedRequest();
                }
                catch (IOException e1)
                {
                    e1.printStackTrace();
                }
                System.exit(0);
            }
            @Override
            public void windowActivated(WindowEvent e)
            {
                super.windowActivated(e);
                mDialog.requestFocusInWindow();
            }
        });
    }
    
    private void doSend()
    {
        String txt = mDialog.getText().trim();
        int o = txt.lastIndexOf('\n');
        txt = txt.substring(o + 1);
        try
        {
            mResponse = RequestLogic.performIntentRequest(txt, mLocale);
            appendResponseToDialog();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private void appendResponseToDialog()
    {
        String card = mResponse.getCardContent();
        if (!StringUtils.isTrivial(card))
        {
            appendToDialog(card);
            return;
        }
        String ssml = mResponse.getOutputSpeechText();
        String txt = StringUtils.exciseAllTags(ssml);
        appendToDialog(txt);
    }
    
    private void appendToDialog(String txt)
    {
        //mDialog.setText("<html><body><p>"+txt+"</p></body></html>");
        mDialog.setText(txt+"\n");
        mDialog.setCaretPosition(mDialog.getText().length());
        mDialog.requestFocusInWindow();
    }
    
    public static void main(String[] args) throws IOException
    {
        GUIClient app = new GUIClient();
        app.setSize(1024, 768);
        app.setVisible(true);
    }
}
