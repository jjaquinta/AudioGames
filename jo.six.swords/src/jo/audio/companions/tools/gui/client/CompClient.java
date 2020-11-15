package jo.audio.companions.tools.gui.client;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jo.audio.util.model.data.AudioResponseBean;
import jo.util.utils.obj.StringUtils;

public class CompClient extends JFrame
{
    private AudioResponseBean       mResponse;
    
    private JComboBox<String>       mLanguage;
    private JScrollPane             mScroller;
    private JTextArea               mDialog;
    private JTextField              mInput;
    private JButton                 mSend;
    private JButton                 mFontEnlarge;
    private JButton                 mFontReduce;
    private JButton                 mClear;
    
    private JButton                 mNorth;
    private JButton                 mSouth;
    private JButton                 mEast;
    private JButton                 mWest;
    private JButton                 mEnter;
    private JButton                 mFight;
    private JButton                 mRunaway;
    private JButton                 mSleep;
    private JButton                 mWho;
    private JButton                 mMore;

    public CompClient()
    {
        super("Six Swords");
        initInstantiate();
        initLayout();
        initLink();

        try
        {
            mResponse = RequestLogic.performLaunchRequest("amadan", "lollipop", (String)mLanguage.getSelectedItem());
            appendResponseToDialog();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void initInstantiate()
    {
        mLanguage = new JComboBox<>(new String[] { "en_US", "en_GB", "en_IN" });
        mLanguage.setSelectedIndex(0);
        mDialog = new JTextArea(12, 40);
        mDialog.setEditable(false);
        mDialog.setLineWrap(true);
        mDialog.setWrapStyleWord(true);
        mScroller = new JScrollPane(mDialog);
        mInput = new JTextField();
        mSend = new JButton("Send");
        mSend.setDefaultCapable(true);
        
        mFontEnlarge = new JButton("a>A");
        mFontReduce = new JButton("A>a");
        mClear = new JButton("Clear");
        
        mNorth = new JButton("North");
        mSouth = new JButton("South");
        mEast = new JButton("East");
        mWest = new JButton("West");
        mEnter = new JButton("Enter");
        mFight = new JButton("Fight");
        mRunaway = new JButton("Runaway");
        mSleep = new JButton("Sleep");
        mWho = new JButton("Who");
        mMore = new JButton("More");
    }

    private void initLayout()
    {
        JPanel enterBar = new JPanel();
        enterBar.setLayout(new BorderLayout());
        enterBar.add("West", mLanguage);
        enterBar.add("Center", mInput);
        enterBar.add("East", mSend);

        JPanel buttonBar = new JPanel();
        buttonBar.setLayout(new GridLayout(1, 5));
        buttonBar.add(mFontEnlarge);
        buttonBar.add(mFontReduce);
        buttonBar.add(mClear);

        JPanel hotkeyBar = new JPanel();
        hotkeyBar.setLayout(new GridLayout(3, 5));
        hotkeyBar.add(mMore);
        hotkeyBar.add(mNorth);
        hotkeyBar.add(new JLabel(""));
        hotkeyBar.add(new JLabel(""));
        hotkeyBar.add(mSleep);
        hotkeyBar.add(mWest);
        hotkeyBar.add(mEnter);
        hotkeyBar.add(mEast);
        hotkeyBar.add(mFight);
        hotkeyBar.add(mRunaway);
        hotkeyBar.add(new JLabel(""));
        hotkeyBar.add(mSouth);
        hotkeyBar.add(new JLabel(""));
        hotkeyBar.add(new JLabel(""));
        hotkeyBar.add(mWho);
        
        JPanel southBar = new JPanel();
        southBar.setLayout(new GridLayout(2, 1));
        southBar.add(enterBar);
        southBar.add(hotkeyBar);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add("North", buttonBar);
        getContentPane().add("Center", mScroller);
        getContentPane().add("South", southBar);
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
        mSend.addActionListener(send);
        mFontEnlarge.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                doFontEnlarge();
            }
        });
        mFontReduce.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                doFontReduce();
            }
        });
        mClear.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent arg0)
            {
                doClear();
            }
        });
        mNorth.addActionListener(new TextListener("north"));
        mSouth.addActionListener(new TextListener("south"));
        mEast.addActionListener(new TextListener("east"));
        mWest.addActionListener(new TextListener("west"));
        mEnter.addActionListener(new TextListener("enter"));
        mFight.addActionListener(new TextListener("fight"));
        mRunaway.addActionListener(new TextListener("run away"));
        mSleep.addActionListener(new TextListener("sleep"));
        mWho.addActionListener(new TextListener("who"));
        mMore.addActionListener(new TextListener("more"));
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
            mResponse = RequestLogic.performIntentRequest(txt, "amadan", "lollipop", (String)mLanguage.getSelectedItem());
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
        String oldText = mDialog.getText();
        oldText += "\n"+txt;
        mDialog.setText(oldText);
    }
    
    private void doFontEnlarge()
    {
        Font oldFont = mDialog.getFont();
        Font newFont = new Font(oldFont.getName(), oldFont.getStyle(), oldFont.getSize() + 1);
        mDialog.setFont(newFont);
    }
    
    private void doFontReduce()
    {
        Font oldFont = mDialog.getFont();
        Font newFont = new Font(oldFont.getName(), oldFont.getStyle(), oldFont.getSize() - 1);
        mDialog.setFont(newFont);
    }
    
    private void doClear()
    {
        mDialog.setText("");
    }
    
    public static void main(String[] args) throws IOException
    {
        CompClient app = new CompClient();
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
