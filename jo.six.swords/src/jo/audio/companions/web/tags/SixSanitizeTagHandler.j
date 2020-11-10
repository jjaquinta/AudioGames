package jo.audio.companions.web.tags;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import jo.util.utils.xml.EntityUtils;

public class SixSanitizeTagHandler extends SimpleTagSupport
{
    @Override
    public void doTag() throws JspException, IOException
    {
        StringWriter sw = new StringWriter();
        getJspBody().invoke(sw);
        String txt = EntityUtils.insertEntities(sw.toString(), true);
        JspWriter out = getJspContext().getOut();
        out.append(txt);
    }
}
