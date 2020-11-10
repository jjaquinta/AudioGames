package jo.audio.companions.service;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jo.audio.companions.data.LocationBean;
import jo.util.utils.obj.IntegerUtils;

public class CompMapServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 6998188851979224629L;

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.addHeader("Access-Control-Allow-Methods","GET, POST, OPTIONS, HEAD, PUT, DELETE, PATCH");
        resp.addHeader("Access-Control-Expose-Headers","Origin, X-Requested-With, Content-Type, Accept");
        resp.addHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.addHeader("Access-Control-Allow-Methods","GET, POST, OPTIONS, HEAD, PUT, DELETE, PATCH");
        resp.addHeader("Access-Control-Expose-Headers","Origin, X-Requested-With, Content-Type, Accept");
        resp.addHeader("Access-Control-Allow-Headers","Origin, X-Requested-With, Content-Type, Accept");
        Dimension d = getSize(req);
        int scale = getScale(req);
        LocationBean ords = getOrds(req);
        
        BufferedImage img = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        MapLogic.drawMap(img, ords, scale);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", baos);
        baos.close();
        byte[] data = baos.toByteArray();
                
        resp.setContentType("image/png");
        resp.setContentLength(data.length);
        resp.getOutputStream().write(data);
    }

    public Dimension getSize(HttpServletRequest req)
    {
        int width = 720;
        int height = 480;
        String s = (String)req.getParameter("size");
        if ("s".equalsIgnoreCase(s) || "small".equalsIgnoreCase(s))
        {
            width = 720;
            height = 480;
        }
        else if ("l".equalsIgnoreCase(s) || "large".equalsIgnoreCase(s))
        {
            width = 1200;
            height = 800;
        }
        else if ("h".equalsIgnoreCase(s) || "hero".equalsIgnoreCase(s))
        {
            width = 288;
            height = 192;
        }
        else if (s != null)
        {
            int o = s.indexOf('x');
            if (o > 0)
            {
                int ww = IntegerUtils.parseInt(s.substring(0, o));
                int hh = IntegerUtils.parseInt(s.substring(o+1));
                if ((ww > 0) && (hh > 0))
                {
                    width = ww;
                    height = hh;
                }
            }
        }
        Dimension d = new Dimension(width, height);
        return d;
    }

    public int getScale(HttpServletRequest req)
    {
        int scale = 48;
        String s = (String)req.getParameter("scale");
        if (s != null)
            scale = IntegerUtils.parseInt(s);
        return scale;
    }

    public LocationBean getOrds(HttpServletRequest req)
    {
        LocationBean ords;
        String o = (String)req.getParameter("ords");
        if (o != null)
            ords = new LocationBean(o);
        else
            ords = new LocationBean(1018, 941, 2);
        return ords;
    }
}
