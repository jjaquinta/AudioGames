package jo.audio.companions.service;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jo.util.utils.io.ResourceUtils;
import jo.util.utils.obj.IntegerUtils;

public class CompMonsterServlet extends HttpServlet
{
    /**
     * 
     */
    private static final long serialVersionUID = 6998188851979224629L;
    
    private static final Map<String,String> mAssets = new HashMap<>();
    
    public CompMonsterServlet()
    {
        super();
        try
        {
            String index = ResourceUtils.loadSystemResourceString("images/monsters.idx", CompMonsterServlet.class);
            for (StringTokenizer st = new StringTokenizer(index, "\r\n"); st.hasMoreTokens(); )
            {
                String line = st.nextToken();
                int o = line.indexOf(',');
                if (o < 0)
                    continue;
                mAssets.put(line.substring(0, o), line.substring(o + 1));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

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
        String monster = req.getParameter("m");
        String image;
        if (mAssets.containsKey(monster))
            image = mAssets.get(monster);
        else
            image = "cbt_combat.png";
        
        InputStream is = ResourceUtils.loadSystemResourceStream("images/"+image, CompMonsterServlet.class);
        BufferedImage img1 = ImageIO.read(is);
        is.close();
        BufferedImage img2 = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img2.getGraphics();
        g.drawImage(img1, 0, 0, d.width, d.height, 0, 0, img1.getWidth(), img1.getHeight(), null);
        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img2, "PNG", baos);
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
}
