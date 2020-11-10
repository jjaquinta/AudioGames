package jo.audio.companions.tools.gui.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jo.audio.util.model.data.AudioResponseBean;
import jo.util.ui.swing.logic.FontUtils;
import jo.util.utils.obj.StringUtils;

public class GUIClient extends JFrame
{
    private AudioResponseBean       mResponse;
    private String                  mLocale = "en_US";
    
    private JTextArea               mDialog;
    private JTextField              mInput;
    
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
        mDialog.setEditable(false);
        mDialog.setLineWrap(true);
        mDialog.setWrapStyleWord(true);
        FontUtils.increaseFontSize(mDialog, 12);
        mInput = new JTextField();
        FontUtils.increaseFontSize(mInput, 8);
    }

    private void initLayout()
    {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add("Center", mDialog);
        getContentPane().add("South", mInput);
    }

    private void initLink()
    {
        ActionListener send = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doSend();
            }
        };
        mInput.addActionListener(send);
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
                mInput.requestFocusInWindow();
            }
        });
    }
    
    private void doHotKey(String txt)
    {
        mInput.setText(txt);
        doSend();
    }
    
    private void doSend()
    {
        String txt = mInput.getText();
        appendToDialog(">"+txt);
        try
        {
            mResponse = RequestLogic.performIntentRequest(txt, mLocale);
            appendResponseToDialog();
            mInput.setText("");
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
        mDialog.setText(txt);
    }
    
    public static void main(String[] args) throws IOException
    {
        GUIClient app = new GUIClient();
        app.setSize(1024, 768);
        app.setVisible(true);
    }
    
    class TextListener implements ActionListener
    {
        private String mText;
        public TextListener(String text)
        {
            mText = text;
        }
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            doHotKey(mText);
        }
    }
}
