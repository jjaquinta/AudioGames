package jo.audio.companions.logic;

import org.json.simple.JSONObject;

import jo.audio.common.data.SLDataBean;
import jo.audio.common.logic.CommonIOLogic;

public class NewsLogic
{
    private static final String NEWS_URI = "sixswords://news";

    public static JSONObject getNews()
    {
        JSONObject news = new JSONObject();
        SLDataBean data = CommonIOLogic.getDataFromURI(NEWS_URI);
        if (data != null)
            for (String key : data.getSecondaryValues().keySet())
            {
                String val = data.getSecondaryValues().get(key);
                news.put(key, val);
            }
        return news;
    }

    public static void setNews(int id, String news)
    {
        CommonIOLogic.setDataSecondaryValue(NEWS_URI, String.valueOf(id), news);
    }
}
