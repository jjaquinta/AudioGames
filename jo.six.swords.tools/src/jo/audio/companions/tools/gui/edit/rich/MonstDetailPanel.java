package jo.audio.companions.tools.gui.edit.rich;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jo.audio.companions.data.CompMonsterTypeBean;
import jo.util.ui.swing.TableLayout;

public class MonstDetailPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
    
    private JTextField mID;
    private JTextField mATT;
    private JTextField mMove;
    private JTextField mAC;
    private JTextField mNumAtt;
    private JTextField mSize;
    private JTextField mFreq;
    private JTextField mName;
    private JTextField mEnc;
    private JTextField mType;
    private JTextField mHD;
    private JTextField mTerrain;
    private JTextField mSpecial;
 
    private CompMonsterTypeBean mMonster;
    
    public MonstDetailPanel()
    {
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mID = new JTextField();
        mATT = new JTextField();
        mMove = new JTextField();
        mAC = new JTextField();
        mNumAtt = new JTextField();
        mSize = new JTextField();
        mFreq = new JTextField();
        mName = new JTextField();
        mEnc = new JTextField();
        mType = new JTextField();
        mHD = new JTextField();
        mTerrain = new JTextField();
        mSpecial = new JTextField();
        mID.setEditable(false);
        mATT.setEditable(false);
        mMove.setEditable(false);
        mAC.setEditable(false);
        mNumAtt.setEditable(false);
        mSize.setEditable(false);
        mFreq.setEditable(false);
        mName.setEditable(false);
        mEnc.setEditable(false);
        mType.setEditable(false);
        mHD.setEditable(false);
        mTerrain.setEditable(false);
        mSpecial.setEditable(false);
    }

    private void initLayout()
    {
        setLayout(new TableLayout());
        add("1,1", new JLabel("ID:")); add("+,.,1,1,3 fill=h", mID);
        add("1,+", new JLabel("ATT:")); add("+,.,1,1,3 fill=h", mATT);
        add("1,+", new JLabel("Move:")); add("+,.,1,1,3 fill=h", mMove);
        add("1,+", new JLabel("AC:")); add("+,.,1,1,3 fill=h", mAC);
        add("1,+", new JLabel("NumAtt:")); add("+,.,1,1,3 fill=h", mNumAtt);
        add("1,+", new JLabel("Size:")); add("+,.,1,1,3 fill=h", mSize);
        add("1,+", new JLabel("Freq:")); add("+,.,1,1,3 fill=h", mFreq);
        add("1,+", new JLabel("Name:")); add("+,.,1,1,3 fill=h", mName);
        add("1,+", new JLabel("Enc:")); add("+,.,1,1,3 fill=h", mEnc);
        add("1,+", new JLabel("Type:")); add("+,.,1,1,3 fill=h", mType);
        add("1,+", new JLabel("HD:")); add("+,.,1,1,3 fill=h", mHD);
        add("1,+", new JLabel("Terrain:")); add("+,.,1,1,3 fill=h", mTerrain);
        add("1,+", new JLabel("Special:")); add("+,.,1,1,3 fill=h", mSpecial);
    }

    private void initLink()
    {
    }

    public CompMonsterTypeBean getMonster()
    {
        return mMonster;
    }

    public void setMonster(CompMonsterTypeBean monster)
    {
        mMonster = monster;
        mID.setText(mMonster.getID());
        mATT.setText(mMonster.getATT());
        mMove.setText(mMonster.getMove());
        mAC.setText(mMonster.getAC());
        mNumAtt.setText(mMonster.getNumAtt());
        mSize.setText(mMonster.getSize());
        mFreq.setText(mMonster.getFreq());
        mName.setText(mMonster.getName());
        mEnc.setText(mMonster.getEnc());
        mType.setText(mMonster.getType());
        mHD.setText(mMonster.getHD());
        mTerrain.setText(mMonster.getTerrain());
        mSpecial.setText(mMonster.getSpecial());
    }
}
