package jo.audio.companions.data;

import org.json.simple.JSONObject;

public interface IMonsterType
{
    public float defeatMargin(float playerHP, float playerDPR, int playerAC);
    public JSONObject   toJSON();
    public void fromJSON(JSONObject o);
    public String getATT();
    public void setATT(String aTT);
    public String getMove();
    public void setMove(String move);
    public String getAC();
    public void setAC(String aC);
    public String getNumAtt();
    public void setNumAtt(String numAtt);
    public String getSize();
    public void setSize(String size);
    public String getFreq();
    public void setFreq(String freq);
    public String getName();
    public void setName(String name);
    public String getEnc();
    public void setEnc(String enc);
    public String getType();
    public void setType(String type);
    public String getHD();
    public void setHD(String hD);
    public String getTerrain();
    public void setTerrain(String terrain);
    public String getID();
    public void setID(String iD);
    public String getSpecial();
    public void setSpecial(String special);
    public JSONObject getDetails();
    public void setDetails(JSONObject details);
}
