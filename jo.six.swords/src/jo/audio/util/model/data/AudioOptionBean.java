package jo.audio.util.model.data;

import java.util.ArrayList;
import java.util.List;

public class AudioOptionBean
{
    private List<String> mKeys = new ArrayList<>();
    private String mTitle;
    private String mDescription;
    private String mImage;
    
    public List<String> getKeys()
    {
        return mKeys;
    }
    public void setKeys(List<String> keys)
    {
        mKeys = keys;
    }
    public String getTitle()
    {
        return mTitle;
    }
    public void setTitle(String title)
    {
        mTitle = title;
    }
    public String getDescription()
    {
        return mDescription;
    }
    public void setDescription(String description)
    {
        mDescription = description;
    }
    public String getImage()
    {
        return mImage;
    }
    public void setImage(String image)
    {
        mImage = image;
    }
}
