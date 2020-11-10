package jo.audio.companions.logic.feature.ruin;

import java.util.ArrayList;
import java.util.List;

public class TempleTemplate
{
    public String[][] template;
    public String[][] ids;
    public List<int[]> entries = new ArrayList<>();
    public int        minRooms;
    public int        maxRooms;
    
    public TempleTemplate(String[][] base, String entry)
    {
        template = new String[base.length + 2][base[0].length + 2];
        ids = new String[base.length + 2][base[0].length + 2];
        boolean[][] filled = new boolean[base.length + 2][base[0].length + 2];
        for (int y = 0; y < base.length; y++)
            for (int x = 0; x < base[y].length; x++)
                if (base[y][x] != null)
                {
                    template[y+1][x+1] = base[y][x];
                    filled[y+1][x+1] = true;
                    if (entry != null)
                        if (base[y][x].indexOf(entry) >= 0)
                            entries.add(new int[] { x + 1, y + 1, 3 });
                    minRooms++;
                    String id = template[y+1][x+1];
                    id = id.replace("<", "");
                    id = id.replace(">", "");
                    id = id.replace("^", "");
                    id = id.replace(".", "");
                    ids[y+1][x+1] = id;
               }
        maxRooms = minRooms;
        for (int y = 0; y < template.length; y++)
            for (int x = 0; x < template[y].length; x++)
            {
                if (template[y][x] != null)
                {
                    testFill(template[y][x], "<", x-1, y, filled);
                    testFill(template[y][x], ">", x+1, y, filled);
                    testFill(template[y][x], "^", x, y-1, filled);
                    testFill(template[y][x], ".", x, y+1, filled);
                }
            }
    }
    
    public TempleTemplate(String[][] base, int[][] _entries)
    {
        this(base, (String)null);
        for (int[] entry : _entries)
            entries.add(new int[] { entry[0] + 1, entry[1] + 1, entry[2]});
    }
    
    private void testFill(String template, String dir, int x, int y, boolean[][] filled)
    {
        if (template.indexOf(dir) >= 0)
        {
            if (!filled[y][x])
            {
                filled[y][x] = true;
                maxRooms++;
            }
        }
    }
    
    public boolean isEntry(int x, int y)
    {
        for (int[] xy : entries)
            if ((x == xy[0]) && (y == xy[1]))
                return true;
        return false;
    }
    
    public int getEntryDir(int x, int y)
    {
        for (int[] xy : entries)
            if ((x == xy[0]) && (y == xy[1]))
                return xy[2];
        return 3;
    }
    
    public int getExtensionsNum()
    {
        return maxRooms - minRooms;
    }
}