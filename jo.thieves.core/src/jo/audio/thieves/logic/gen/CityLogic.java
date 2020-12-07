package jo.audio.thieves.logic.gen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jo.audio.thieves.data.gen.City;
import jo.audio.thieves.data.gen.Intersection;
import jo.audio.thieves.data.gen.Street;
import jo.audio.thieves.logic.ThievesConstLogic;
import jo.audio.thieves.slu.ThievesModelConst;
import jo.util.utils.MathUtils;

public class CityLogic
{
    public static City generateCity(long seed)
    {
        Random rnd = new Random(seed);
        City city = new City();
        city.setRND(rnd);
        city.setID("CITY"+seed);
        city.setName("{{CITY_NAME#"+seed+"}}");
        city.setRiverName("{{RIVER_NAME#"+seed+"}}");
        generateIntersections(rnd, city);
        generateStreets(rnd, city);
        computeCardinality(city);
        return city;
    }
    
    private static void computeCardinality(City city)
    {
        for (Intersection i : city.getIntersections().values())
        {
            List<Street> skipped = new ArrayList<>();
            for (Street s : i.getStreets())
            {
                int dir = i.bearing(s);
                if (i.getCardinalStreets()[dir] != null)
                    skipped.add(s);
                else
                    i.getCardinalStreets()[dir] = s;
            }
            for (Street s1 : skipped)
            {
                int dir = i.bearing(s1);
                Street s2 = i.getCardinalStreets()[dir];
                double dir1 = ThievesConstLogic.bearingDouble(i, s1);
                double dir2 = ThievesConstLogic.bearingDouble(i, s2);
                double delta1 = Math.abs(dir1 - dir);
                double delta2 = Math.abs(dir2 - dir);
                if (delta1 < delta2)
                {
                    i.getCardinalStreets()[dir] = s1;
                    s1 = s2;
                    dir1 = dir2;
                }
                int target;
                if (dir1 < dir)
                    target = (dir + 7)%8;
                else
                    target = (dir + 1)%8;
                if (i.getCardinalStreets()[target] == null)
                    i.getCardinalStreets()[target] = s1;
                else
                {
                    if (dir1 < dir)
                        target = (dir + 1)%8;
                    else
                        target = (dir + 7)%8;
                    if (i.getCardinalStreets()[target] == null)
                    {
                        s2 = i.getCardinalStreets()[dir];
                        i.getCardinalStreets()[dir] = s1;
                        i.getCardinalStreets()[target] = s2;
                    }
                    else
                        System.out.println("Can't squeeze in "+s1.getName());
                }
            }
        }
        for (Street s : city.getStreets().values())
        {
            int high2low = ThievesConstLogic.bearing(s.getHighIntersection(), s.getLowIntersection());
            s.setLowDir(high2low);
            s.setHighDir((high2low+4)%8);
        }
    }

    private static void generateStreets(Random rnd, City city)
    {
        List<Intersection> nearest = new ArrayList<>();
        int streets = 0;
        int bridges = 0;
        int quays = 0;
        for (Intersection i : city.getIntersections().values())
        {
            if (i.getStreets().size() >= 3)
                continue;
            nearest.clear();
            nearest.addAll(city.getIntersections().values());
            nearest.remove(i);
            for (Street s : i.getStreets())
            {
                nearest.remove(s.getHighIntersection());
                nearest.remove(s.getLowIntersection());
            }
            sortIntersections(i, nearest);
            while ((i.getStreets().size() < 3) && (nearest.size() > 0))
            {
                Intersection j = nearest.get(0);
                nearest.remove(0);
                Street s = new Street();
                s.setHighIntersection((i.getElevation() > j.getElevation()) ? i : j);
                s.setLowIntersection((i.getElevation() < j.getElevation()) ? i : j);
                if (intersection(s, city))
                    continue;
                s.setSeed(rnd.nextLong());
                s.setID("STR"+city.getStreets().size());
                int houses = (int)dist(i, j);
                if (i.isRiverside() && j.isRiverside())
                {
                    if (Math.signum(i.getY()) == Math.signum(j.getY()))
                    {
                        s.setType(Street.QUAY);
                        s.setName("{{QUAY_NAME#"+quays+"}}");
                        quays++;
                        houses /= 2;
                    }
                    else
                    {
                        s.setType(Street.BRIDGE);
                        s.setName("{{BRIDGE_NAME#"+bridges+"}}");
                        bridges++;
                        houses = 0;
                    }
                }
                else
                {
                    s.setType(Street.STREET);
                    s.setName("{{STREET_NAME#"+streets+"}}");
                    streets++;
                }
                s.setHouses(houses);
                i.getStreets().add(s);
                j.getStreets().add(s);
                city.getStreets().put(s.getID(), s);
            }
        }
    }
    
    private static boolean intersection(Street s, City city)
    {
        for (Street s2 : city.getStreets().values())
        {
            if (intersectStreets(s, s2))
                return true;
        }
        return false;
    }

    private static void sortIntersections(final Intersection i,
            List<Intersection> nearest)
    {
        Collections.sort(nearest, new Comparator<Intersection>(){
            @Override
            public int compare(Intersection o1, Intersection o2)
            {
                double d1 = dist(i, o1);
                double d2 = dist(i, o2);
                if (d1 < d2)
                    return -1;
                else if (d1 > d2)
                    return 1;
                return 0;
            }
        });
    }
    
    private static double dist(Intersection i1, Intersection i2)
    {
        return dist(i1, i2.getX(), i2.getY());
    }
    
    private static double dist(Intersection i1, int x, int y)
    {
        int dx = i1.getX() - x;
        int dy = i1.getY() - y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    private static void generateIntersections(Random rnd, City city)
    {
        int width = (int)Math.sqrt(ThievesConstLogic.CITY_SIZE_INTERSECTIONS);
        int height = ThievesConstLogic.CITY_SIZE_INTERSECTIONS/width/2 + 1;
        Set<Integer> usedElevations = new HashSet<>();
        Set<Integer> usedX = new HashSet<>();
        Set<Integer> usedY = new HashSet<>();
        for (int x = 0; x < width; x++)
        {
            for (int y = 1; y <= height; y++)
            {
                generateIntersection(rnd, city, usedElevations, usedX, usedY, x+2,
                        y);
                generateIntersection(rnd, city, usedElevations, usedX, usedY, x+1,
                        -y);
            }
        }
        Intersection[] intersections = city.getIntersections().values().toArray(new Intersection[0]);
        Arrays.sort(intersections, new Comparator<Intersection>() {
            @Override
            public int compare(Intersection o1, Intersection o2)
            {
                return o1.getElevation() - o2.getElevation();
            }
        });
        int maxNames = ThievesModelConst.getTexts("INTERSECTION_NAME").length;
        int maxTypes = ThievesModelConst.getTexts("INTERSECTION_TYPE").length;
        int maxDescriptions = ThievesModelConst.getTexts("LANDMARK_NAME").length;
        for (int i = 0; i < intersections.length; i++)
        {
            intersections[i].setPosh(MathUtils.interpolate(i, 0, intersections.length - 1, 0.0, 1.0));
            if (intersections.length - i < maxDescriptions)
                intersections[i].setDescription("{{LANDMARK_NAME#"+(intersections.length - i)+"}}");
            int nameIdx = (int)MathUtils.interpolate(i, 0, intersections.length, 0, maxNames);
            intersections[i].setName("{{INTERSECTION_NAME#"+nameIdx+"}} {{INTERSECTION_TYPE#"+rnd.nextInt(maxTypes)+"}}");
        }
    }

    public static void generateIntersection(Random rnd, City city,
            Set<Integer> usedElevations, Set<Integer> usedX, Set<Integer> usedY,
            int x, int y)
    {
        Intersection i = new Intersection();
        i.setSeed(rnd.nextLong());
        i.setID("INT"+rnd.nextInt());
        i.setElevation(uniqueValue(Math.abs(y), rnd, usedElevations));
        i.setX(uniqueValue(x, rnd, usedX));
        i.setY(uniqueValue(y, rnd, usedY));
        i.setRiverside(Math.abs(y) == 1);
        city.getIntersections().put(i.getID(), i);
    }

    private static int uniqueValue(int v, Random rnd,
            Set<Integer> usedValues)
    {
        int e;
        do
        {
            e = v*100;
            if (e > 0)
                e -= 50;
            else
                e += 50;
            e += rnd.nextInt(70) - 35;
        } while (usedValues.contains(e));
        usedValues.add(e);
        return e;
    }
    

    private static boolean intersectStreets(Street line1, Street line2)
    {
        return intersectSegment(line1.getHighIntersection().getX(), line1.getHighIntersection().getY(), 
                line1.getLowIntersection().getX(), line1.getLowIntersection().getY(),
                line2.getHighIntersection().getX(), line2.getHighIntersection().getY(), 
                line2.getLowIntersection().getX(), line2.getLowIntersection().getY());
    }
    
    // http://stackoverflow.com/questions/563198/how-do-you-detect-where-two-line-segments-intersect
     // Returns 1 if the lines intersect, otherwise 0. In addition, if the lines 
     // intersect the intersection point may be stored in the floats i_x and i_y.
     public static boolean intersectSegment(double p0_x, double p0_y, double p1_x, double p1_y, 
         double p2_x, double p2_y, double p3_x, double p3_y)
     {
         double s1_x, s1_y, s2_x, s2_y;
         s1_x = p1_x - p0_x;     s1_y = p1_y - p0_y;
         s2_x = p3_x - p2_x;     s2_y = p3_y - p2_y;
    
         double s, t;
         s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
         t = ( s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);
    
         if (s > 0 && s < 1 && t > 0 && t < 1) // Collision detected
             return true;
         return false; // No collision
     }

    
     /*
    public static void main(String[] argv) throws IOException
    {
        City c = generateCity(0);
        int streets = 0;
        int quays = 0;
        int bridges = 0;
        int houses = 0;
        for (Street s : c.getStreets().values())
        {
            if (s.isStreet())
                streets++;
            else if (s.isQuay())
                quays++;
            else if (s.isBridge())
                bridges++;
            houses += s.getHouses();
        }
        System.out.println("Intersections: "+c.getIntersections().size());
        System.out.println("Total Streets: "+c.getStreets().size());
        System.out.println("Streets: "+streets);
        System.out.println("Quays: "+quays);
        System.out.println("Bridges: "+bridges);
        System.out.println("Total Houses: "+houses);
        int totConn = 0;
        int maxx = 0;
        int maxy = 0;
        for (Intersection i : c.getIntersections().values())
        {
            totConn += i.getStreets().size();
            maxx = Math.max(maxx, i.getX());
            maxy = Math.max(maxy, i.getY());
        }
        System.out.println("Avg conn: "+((double)totConn)/c.getIntersections().size());
        System.out.println("Intersection: "+c.getIntersections().values().iterator().next().getID());
        // names
        JSONObject json = JSONUtils.readJSON(new File("C:\\Users\\IBM_ADMIN\\git\\TsaTsaTzuAlexa\\jo.audio.thieves\\src\\jo\\audio\\thieves\\slu\\place_names.model"));
        JSONObject text = JSONUtils.getObject(json, "text.en_US");
        Set<String> fullName = new HashSet<>();
        Set<String> partName = new HashSet<>();
        for (String key : text.keySet())
        {
            JSONArray a = (JSONArray)text.get(key);
            for (int i = 0; i < a.size(); i++)
            {
                String name = (String)a.get(i);
                if (fullName.contains(name))
                    System.out.println("Duplicate name: "+name);
                fullName.add(name);
                System.out.print("          [ \""+name+"\"");
                StringTokenizer st = new StringTokenizer(name, " ");
                for (int j = st.countTokens(); j > 1; j--)
                {
                    String part = st.nextToken();
                    if (partName.contains(part))
                        System.out.println("Duplicate part: "+name+", "+part);
                    partName.add(part);
                    System.out.print(", \""+part+"\"");
                }
                System.out.println(" ],");
            }
        }
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
            int xm = (x1 + x2)/2;
            int ym = (y1 + y2)/2;
            int o = s.getName().indexOf("#");
            String key = s.getName().substring(2,  o);
            int idx = IntegerUtils.parseInt(s.getName().substring(o + 1, s.getName().length() - 2));
            String name = (String)JSONUtils.getArray(text, key).get(idx);
            int w = g.getFontMetrics().stringWidth(name);
            g.setColor(Color.BLACK);
            g.drawString(name, xm - w/2, ym);
        }
        // draw intersections
        for (Intersection i : c.getIntersections().values())
        {
            if (i.isRiverside())
                g.setColor(Color.GREEN);
            else
                g.setColor(Color.BLACK);
            g.fillOval(i.getX()-4, i.getY()-4+midy, 8, 8);
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
        // house creation test
        Street s = c.getStreets().get("STR2");
        House h = null;
        for (int i = 1; i <= s.getHouses(); i++)
        {
            h = HouseLogic.getHouse(s, i);
            if (h.getEntry().startsWith("TERRACE"))
                break;
        }
        System.out.println(s.getName()+" #"+h.getHouseNumber()+" - "+h.getLocations().size()+" locations, "+h.getApatures().size()+" apatures");
        System.out.println("  entry="+h.getEntry());
        Location l = h.getLocations().get(h.getEntry());
        System.out.println("  name="+l.getName()+", desc="+l.getDescription());
        for (int dir : new int[] {ThievesConstLogic.NORTH,ThievesConstLogic.SOUTH,ThievesConstLogic.EAST,ThievesConstLogic.WEST,ThievesConstLogic.UP,ThievesConstLogic.DOWN})
        {
            String apID = l.getApature(dir);
            System.out.print("  dir="+dir+", apID="+apID);
            Apature ap = h.getApatures().get(apID);
            if (ap != null)
            {
                System.out.print(", apName="+ap.getName());
                String locID = ap.getLocation(dir);
                System.out.print(", locID="+locID);
                Location l2 = h.getLocations().get(locID);
                if (l2 != null)
                    System.out.println(", locName="+l2.getName()+", locDesc="+l2.getDescription());
            }
            System.out.println();
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
    */
}
