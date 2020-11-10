package jo.audio.companions.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import jo.audio.companions.data.CompRoomBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.LocationBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.SquareBean;
import jo.audio.companions.logic.FeatureLogic;
import jo.audio.companions.logic.GenerationLogic;
import jo.util.utils.obj.StringUtils;

public class MapTacticalLogic
{
    public static void drawMapTactial(BufferedImage img, LocationBean loc, int scale)
    {
        int width = img.getWidth();
        int height = img.getHeight();
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        int x1 = width/3;
        int x2 = width*2/3;
        int y1 = height/3;
        int y2 = height*2/3;
        FeatureBean feature = getFeature(loc);
        CompRoomBean room = getRoom(feature, loc.getRoomID());
        if (room != null)
        {
            drawRoom(g, x1, 0, x2, y1, room.getNorth(), getRoom(feature, room.getNorth()), room, "\u2191", 12);
            drawRoom(g, x1, y2, x2, height, room.getSouth(), getRoom(feature, room.getSouth()), room, "\u2193", 12);
            drawRoom(g, x2, y1, width, y2, room.getEast(), getRoom(feature, room.getEast()), room, "\u2192", 3);
            drawRoom(g, 0, y1, x1, y2, room.getWest(), getRoom(feature, room.getWest()), room, "\u2190", 3);
            int mask = (StringUtils.isTrivial(room.getNorth()) ? 1 : 0)
                    + (StringUtils.isTrivial(room.getSouth()) ? 2 : 0)
                    + (StringUtils.isTrivial(room.getEast()) ? 4 : 0)
                    + (StringUtils.isTrivial(room.getWest()) ? 8 : 0);
            drawRoom(g, x1,y1, x2, y2, room.getID(), room, null, null, mask);
        }
        g.dispose();
    }

    public static FeatureBean getFeature(LocationBean loc)
    {
        RegionBean region = GenerationLogic.getRegion(loc);
        if (region == null)
            return null;
        SquareBean sq = GenerationLogic.getSquare(loc);
        if (sq == null)
            return null;
        FeatureBean feature = FeatureLogic.getFeature(region, sq, null);
        return feature;
    }

    public static CompRoomBean getRoom(FeatureBean feature, String roomID)
    {
        if (feature == null)
            return null;
        CompRoomBean room = FeatureLogic.findRoom(feature, roomID);
        return room;
    }
    
    private static int drawRoom(Graphics g, int x1, int y1, int x2, int y2, String id, CompRoomBean room, CompRoomBean wrt, String alt, int mask)
    {
        BufferedImage img = MapLogic.mAssets.getRoomImage(id);
        if (img == null)
            return 0;
        int width = x2 - x1;
        int height = y2 - y1;
        g.drawImage(img, x1, y1, width, height, null);
        img = MapLogic.mAssets.getRoomMask(mask);
        if (img != null)
            g.drawImage(img, x1, y1, x2, y2, 0, 0, img.getWidth(), img.getHeight(), null);
        if (room == null)
            return 1;
        String txt;
        if ((wrt == null) || !room.getName().equals(wrt.getName()))
            txt = MapLogic.getText(room.getName());
        else
            txt = alt;
        Font f = g.getFont();
        f = new Font(f.getName(), f.getStyle(), 12);
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        int h = fm.getHeight();
        int w = stringWidth(fm, txt);
        if (w < width/2)
            for (;;)
            {
                Font ftry = new Font(f.getName(), f.getStyle(), f.getSize() + 1);
                g.setFont(ftry);
                FontMetrics fmtry = g.getFontMetrics();
                int htry = fm.getHeight();
                int wtry = stringWidth(fm, txt);
                if ((wtry >= width/2) || (htry > height/2))
                    break;
                f = ftry;
                fm = fmtry;
                h = htry;
                w = wtry;
            }
        //System.out.println("txt="+txt+", font="+f.getSize()+", size="+w+","+h);
        int x = (int)(width - w)/2;
        int y = (int)(height - h)/2;
        g.setColor(new Color(0, 0, 0, 128));
        g.fillRoundRect(x1 + x, y1 + y, w, h, h/4, h/4);
        g.setColor(Color.WHITE);
        g.drawString(txt, x1 + x, y1 + y + fm.getAscent());
        return 1;
    }

    private static int stringWidth(FontMetrics fm, String txt)
    {
        int w = fm.stringWidth(txt);
        for (;;)
        {
            int o = txt.indexOf(' ');
            if (o < 0)
                break;
            w += fm.charWidth('M');
            txt = txt.substring(o + 1);
        }
        return w;
    }
}
