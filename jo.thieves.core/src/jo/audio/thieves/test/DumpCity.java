package jo.audio.thieves.test;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import jo.audio.thieves.data.gen.City;
import jo.audio.thieves.data.gen.House;
import jo.audio.thieves.data.gen.Intersection;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.data.template.PLocationRef;
import jo.audio.thieves.logic.LocationLogic;
import jo.audio.thieves.logic.ThievesConstLogic;
import jo.audio.thieves.slu.ThievesModelConst;
import jo.util.utils.MathUtils;

public class DumpCity
{
    public void run()
    {
        City city = LocationLogic.getCity();
        System.out.println("City name: "+ThievesModelConst.expand(city.getName()));
        System.out.println("River name: "+ThievesModelConst.expand(city.getRiverName()));
        System.out.println("Num Streets: "+city.getStreets().size());
        boolean firstStreet = true;
        boolean firstHouse = true;
        int numHouses = 0;
        int numRooms = 0;
        for (Street street : city.getStreets().values())
        {
            System.out.println("  Street "+ThievesModelConst.expand(street.getName())
                    +", posh="+(int)(100*street.getPosh())
                    +", houses="+street.getHouses()
                    +", type="+street.getType()
                    +", id="+street.getID());
            numHouses += street.getHouses();
            for (int i = 1; i <= street.getHouses(); i++)
            {
                //DebugUtils.mDebugLevel = DebugUtils.TRACE;
                House house = LocationLogic.getHouse(street.getID()+":"+i);
                numRooms += house.getTemplate().getLocations().size();
                if (firstStreet)
                {
                    firstStreet = false;
                    System.out.println("    House: #"+house.getHouseNumber()+", squares=#"+house.getTemplate().getNumSquares()+", apateurs=#"+house.getTemplate().getNumApatures());
                    if (firstHouse)
                    {
                        firstHouse = false;
                        for (PLocationRef l : house.getTemplate().getLocations().values())
                        {
                            System.out.println("      "+l.getID()+", "+l.getX()+","+l.getY()+","+l.getZ());
                        }
                    }
                }
            }
        }
        int maxx = 0;
        int maxy = 0;
        System.out.println("Num Intersections: "+city.getIntersections().size());
        for (Intersection i : city.getIntersections().values())
        {
            System.out.println("  Intersection: id="+i.getID()
                +", name="+ThievesModelConst.expand(i.getName())
                +", desc="+ThievesModelConst.expand(i.getDescription()));
            maxx = Math.max(maxx, i.getX());
            maxy = Math.max(maxy, i.getY());
        }
        System.out.println("Total houses: "+numHouses);
        System.out.println("Total rooms : "+numRooms);
        dumpImage(city, maxx, maxy);
    }
    
    private void dumpImage(City c, int maxx, int maxy)
    {
        // image
        BufferedImage img = new BufferedImage(maxx+100, (maxy+100)*2, BufferedImage.TYPE_INT_ARGB);
        int midy = img.getHeight()/2;
        Graphics2D g = (Graphics2D)img.getGraphics();
        // draw posh
        for (int x = 0; x < img.getWidth(); x += 4)
            for (int y = 0; y < img.getHeight(); y += 4)
            {
                Color posh = new Color(0.0f, (float)(getPosh(c, x, y - midy)/2) + 0.5f, 0.0f);
                g.setColor(posh);
                g.fillRect(x, y, 4, 4);
            }
        g.setColor(Color.BLUE);
        g.fillRect(0, midy-10, img.getWidth(), 20);
        // draw streets
        FontMetrics fm = g.getFontMetrics();
        for (Street s : c.getStreets().values())
        {
            if (s.getName().indexOf("STREET") >= 0)
                g.setColor(Color.BLACK);
            else if (s.getName().indexOf("QUAY") >= 0)
                g.setColor(Color.DARK_GRAY);
            else if (s.getName().indexOf("BRIDGE") >= 0)
                g.setColor(Color.RED);
            int x1 = s.getHighIntersection().getX();
            int y1 = s.getHighIntersection().getY() + midy;
            int x2 = s.getLowIntersection().getX();
            int y2 = s.getLowIntersection().getY() + midy;
            g.drawLine(x1, y1, x2, y2);
            String name = ThievesModelConst.expand(s.getName());
            g.setColor(Color.RED);
            System.out.println(name);
            int o = name.lastIndexOf(' ');
            if (o < 0)
                o = name.length();
            String name1 = name.substring(0, o);
            String name2 = name.substring(o).trim();
            double w1 = fm.getStringBounds(name1, g).getWidth();
            double w2 = fm.getStringBounds(name2, g).getWidth();
            double d = MathUtils.dist(x1, y1, x2, y2);
            double a = Math.atan2(y2 - y1, x2 - x1);
            AffineTransform oldTrans = g.getTransform();
            AffineTransform newTrans = AffineTransform.getTranslateInstance(x1, y1);
            newTrans.concatenate(AffineTransform.getRotateInstance(a));
            //newTrans.concatenate(AffineTransform.getTranslateInstance(x1, y1));
            g.setTransform(newTrans);
            g.drawString(name1, (int)(d - w1)/2, -fm.getDescent() - fm.getLeading());
            g.drawString(name2, (int)(d - w2)/2, fm.getAscent() + fm.getLeading());
            g.setTransform(oldTrans);
        }
        // draw intersections
        for (Intersection i : c.getIntersections().values())
        {
            if (i.isRiverside())
                g.setColor(Color.GREEN);
            else
                g.setColor(Color.BLACK);
            int x = i.getX();
            int y = i.getY()+midy;
            g.fillOval(x-4, y-4, 8, 8);
            String name = ThievesModelConst.expand(i.getName());
            int o = name.lastIndexOf(' ');
            String name1 = name.substring(0, o);
            String name2 = name.substring(o + 1);
            double w1 = fm.getStringBounds(name1, g).getWidth();
            double w2 = fm.getStringBounds(name2, g).getWidth();
            g.setColor(Color.RED);
            g.drawString(name1, x - (int)(w1/2), y - fm.getDescent() - fm.getLeading());
            g.drawString(name2, x - (int)(w2/2), y + fm.getAscent() + fm.getLeading());
        }
        g.dispose();
        try
        {
            ImageIO.write(img, "PNG", new File("c:\\temp\\city.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static float getPosh(City c, int x, int y)
    {
        List<Intersection> bestI = new ArrayList<>();
        List<Double> bestD = new ArrayList<>();
        for (Intersection i : c.getIntersections().values())
        {
            double d = dist(i, x, y);
            if ((bestI.size() == 0) || (d < bestD.get(0)))
            {
                bestI.add(0, i);
                bestD.add(0, d);
            }
            else if ((bestI.size() == 1) || (d < bestD.get(1)))
            {
                bestI.add(1, i);
                bestD.add(1, d);
            }
        }
        double p1 = bestI.get(0).getPosh()*bestD.get(0);
        double p2 = bestI.get(1).getPosh()*bestD.get(1);
        double p = (p1 + p2)/(bestD.get(0) + bestD.get(1));
        return (float)p;
    }
    
    private static double dist(Intersection i1, int x, int y)
    {
        int dx = i1.getX() - x;
        int dy = i1.getY() - y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    public static void main(String[] argv)
    {
        DumpCity app = new DumpCity();
        app.run();
    }
}
