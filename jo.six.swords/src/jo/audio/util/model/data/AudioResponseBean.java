package jo.audio.util.model.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;

public class AudioResponseBean
{
    public static final int OPTION_LIST = 1;
    public static final int OPTION_CAROUSEL = 2;
    
    private SpeechBuffer mOutputSpeech = new SpeechBuffer();
    private String  mCardImageSmall;
    private String  mCardImageLarge;
    private String  mCardImageHero;
    private String  mCardTitle;
    private String  mCardSubTitle;
    private SpeechBuffer mCardSpeech = new SpeechBuffer();
    private SpeechBuffer mRepromptSpeech = new SpeechBuffer();
    private boolean mShouldEndSession;
    private boolean mRequestAuthentication;
    private JSONObject          mTransactionState;
    private Set<String> mExpectedContexts = new HashSet<>();
    private String mLinkOutName;
    private String mLinkOutURL;
    private List<String> mSuggestions = new ArrayList<String>();
    private int mOptionMode;
    private String mOptionTitle;
    private List<AudioOptionBean> mOptions = new ArrayList<>();
    
    public String getOutputSpeechText()
    {
        return mOutputSpeech.toString();
    }
    public void setOutputSpeechText(String outputSpeechText)
    {
        mOutputSpeech = new SpeechBuffer();
        mOutputSpeech.append(outputSpeechText);
    }
    public String getCardTitle()
    {
        return mCardTitle;
    }
    public void setCardTitle(String cardTitle)
    {
        mCardTitle = cardTitle;
    }
    public String getCardContent()
    {
        return mCardSpeech.toString();
    }
    public void setCardContent(String cardContent)
    {
        mCardSpeech = new SpeechBuffer();
        mCardSpeech.append(cardContent);
    }
    public String getRepromptText()
    {
        return mRepromptSpeech.toString();
    }
    public void setRepromptText(String repromptText)
    {
        mRepromptSpeech = new SpeechBuffer();
        mRepromptSpeech.append(repromptText);
    }
    public boolean isShouldEndSession()
    {
        return mShouldEndSession;
    }
    public void setShouldEndSession(boolean shouldEndSession)
    {
        mShouldEndSession = shouldEndSession;
    }
    public JSONObject getTransactionState()
    {
        return mTransactionState;
    }
    public void setTransactionState(JSONObject transactionState)
    {
        mTransactionState = transactionState;
    }
    public Set<String> getExpectedContexts()
    {
        return mExpectedContexts;
    }
    public void setExpectedContexts(Set<String> expectedContexts)
    {
        mExpectedContexts = expectedContexts;
    }
    public boolean isRequestAuthentication()
    {
        return mRequestAuthentication;
    }
    public void setRequestAuthentication(boolean requestAuthentication)
    {
        mRequestAuthentication = requestAuthentication;
    }
    public String getCardImageSmall()
    {
        return mCardImageSmall;
    }
    public void setCardImageSmall(String cardImageSmall)
    {
        mCardImageSmall = cardImageSmall;
    }
    public String getCardImageLarge()
    {
        return mCardImageLarge;
    }
    public void setCardImageLarge(String cardImageLarge)
    {
        mCardImageLarge = cardImageLarge;
    }
    public String getLinkOutName()
    {
        return mLinkOutName;
    }
    public void setLinkOutName(String linkOutName)
    {
        mLinkOutName = linkOutName;
    }
    public String getLinkOutURL()
    {
        return mLinkOutURL;
    }
    public void setLinkOutURL(String linkOutURL)
    {
        mLinkOutURL = linkOutURL;
    }
    public List<String> getSuggestions()
    {
        return mSuggestions;
    }
    public void setSuggestions(List<String> suggestions)
    {
        mSuggestions = suggestions;
    }
    public int getOptionMode()
    {
        return mOptionMode;
    }
    public void setOptionMode(int optionMode)
    {
        mOptionMode = optionMode;
    }
    public List<AudioOptionBean> getOptions()
    {
        return mOptions;
    }
    public void setOptions(List<AudioOptionBean> options)
    {
        mOptions = options;
    }
    public String getOptionTitle()
    {
        return mOptionTitle;
    }
    public void setOptionTitle(String optionTitle)
    {
        mOptionTitle = optionTitle;
    }
    public SpeechBuffer getOutputSpeech()
    {
        return mOutputSpeech;
    }
    public void setOutputSpeech(SpeechBuffer outputSpeech)
    {
        mOutputSpeech = outputSpeech;
    }
    public SpeechBuffer getCardSpeech()
    {
        return mCardSpeech;
    }
    public void setCardSpeech(SpeechBuffer cardSpeech)
    {
        mCardSpeech = cardSpeech;
    }
    public SpeechBuffer getRepromptSpeech()
    {
        return mRepromptSpeech;
    }
    public void setRepromptSpeech(SpeechBuffer repromptSpeech)
    {
        mRepromptSpeech = repromptSpeech;
    }
    public String getCardImageHero()
    {
        return mCardImageHero;
    }
    public void setCardImageHero(String cardImageHero)
    {
        mCardImageHero = cardImageHero;
    }
    public String getCardSubTitle()
    {
        return mCardSubTitle;
    }
    public void setCardSubTitle(String cardSubTitle)
    {
        mCardSubTitle = cardSubTitle;
    }
}
