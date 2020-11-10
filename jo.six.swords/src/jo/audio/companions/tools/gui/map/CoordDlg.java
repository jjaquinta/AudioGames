package jo.audio.companions.tools.gui.map;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jo.audio.companions.data.CoordBean;
import jo.util.utils.obj.IntegerUtils;

@SuppressWarnings("serial")
public class CoordDlg extends JDialog
{
    private JTextField      mX;
    private JTextField      mY;
    private JTextField      mZ;
    private JButton         mOK;
    
    private CoordBean       mCoord;

    public CoordDlg(Frame f, CoordBean coord)
    {
        super(f, "Pick Location", true);
        mCoord = coord;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mX = new JTextField(String.valueOf(mCoord.getX()));
        mY = new JTextField(String.valueOf(mCoord.getY()));
        mZ = new JTextField(String.valueOf(mCoord.getZ()));
        mOK = new JButton("OK");
    }

    private void initLayout()
    {
        JPanel client = new JPanel();
        client.setLayout(new GridLayout(4, 2));
        client.add(new JLabel("X:"));
        client.add(mX);
        client.add(new JLabel("Y:"));
        client.add(mY);
        client.add(new JLabel("Z:"));
        client.add(mZ);
        client.add(new JLabel(""));
        client.add(mOK);
        setContentPane(client);
        pack();
    }

    private void initLink()
    {
        FocusListener fl = new FocusAdapter(){
            @Override
            public void focusLost(FocusEvent e)
            {
                updateData();
            }
        };
        mX.addFocusListener(fl);
        mY.addFocusListener(fl);
        mZ.addFocusListener(fl);
        mOK.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });
    }
    
    private void updateData()
    {
        mCoord.setX(IntegerUtils.parseInt(mX.getText()));
        mCoord.setY(IntegerUtils.parseInt(mY.getText()));
        mCoord.setZ(IntegerUtils.parseInt(mZ.getText()));
    }

    public CoordBean getCoord()
    {
        return mCoord;
    }
}
