package jo.audio.companions.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONUtils;

import jo.audio.common.logic.CommonIOLogic;
import jo.audio.companions.data.CompCompanionBean;
import jo.audio.companions.data.CompUserBean;
import jo.audio.companions.logic.CompIOLogic;
import jo.audio.util.ICSVAble;
import jo.audio.util.ResponseUtils;
import jo.util.logic.CSVLogic;

public class ArchiveSixSwords
{
    public static void archive(File dir) throws IOException
    {
        System.out.println("Archiving SixSwords users");
        File userDir = new File(dir, "users");
        List<CompUserBean> users = CompIOLogic.getAllUsers();
        int todo = users.size();
        for (CompUserBean user : users)
        {
            String fname = toFileName(user);
            File f = new File(userDir, fname);
            JSONUtils.writeJSON(f, user.toJSON());
            todo--;
            if (todo%20 == 0)
                System.out.println(todo);
            else
                System.out.print(".");
        }
        System.out.println();
    }

    private static String toFileName(CompUserBean user)
    {
        String uri = user.getURI();
        if (uri.startsWith("compuser://"))
            uri = uri.substring(11);
        StringBuffer fname = new StringBuffer();
        for (char ch : uri.toCharArray())
            if (Character.isJavaIdentifierPart(ch))
                fname.append(ch);
            else if (!fname.toString().endsWith("_"))
                fname.append('_');
        fname.append(".json");
        return fname.toString();
    }

    private static final String[] mStatsColumns = {
            "URI",
            "Location",
            "Interactions",
            "Last Interaction",
            "Visits",
            "MaxDistance",
            "TotalDistance",
            "TotalEffort",
            "TotalKills",
            "KillTypes",
            "TotalFights",
            "TotalWins",
            "MaxGold",
            "NumCompanions",
            "TotalLevels",
            "AverageLevel",
            "TotalXP",
    };

    private static void doStats(File dir) throws IOException
    {
        System.out.println("Updating SixSwords stats");
        File userStats = new File(dir, "user_stats.csv");
        File userDir = new File(dir, "users");
        File[] userFiles = userDir.listFiles();
        BufferedWriter wtr = new BufferedWriter(new FileWriter(userStats));
        wtr.write(CSVLogic.toCSVHeader(mStatsColumns));
        wtr.newLine();
        int todo = userFiles.length;
        for (File userFile : userFiles)
        {
            JSONObject json = JSONUtils.readJSON(userFile);
            CompUserBean user = new CompUserBean();
            user.fromJSON(json);
            int totalLevels = 0;
            int totalXP = 0;
            for (CompCompanionBean comp : user.getCompanions())
            {
                totalLevels += comp.getLevel();
                totalXP += comp.getExperiencePoints();
            }
            float averageLevel = 0;
            if (user.getCompanions().size() > 0)
                averageLevel = totalLevels/user.getCompanions().size();
            List<Object> line = new ArrayList<>();
            Date lastInteraction = new Date(user.getLastInteraction());
            line.add(user.getURI());
            line.add(user.getLocation());
            line.add(user.getInteractions());
            line.add(ICSVAble.mDATE.format(lastInteraction));
            line.add(ResponseUtils.countList(user.getVisitList()));
            line.add(user.getMaxDistance());
            line.add(user.getTotalDistance());
            line.add(user.getTotalEffort());
            line.add(user.getTotalKills());
            line.add(ResponseUtils.countList(user.getKillList()));
            line.add(user.getTotalFights());
            line.add(user.getTotalWins());
            line.add(user.getMaxGoldPieces());
            line.add(user.getCompanions().size());
            line.add(totalLevels);
            line.add(averageLevel);
            line.add(totalXP);
            String outbuf = CSVLogic.toCSVLine(line);
            wtr.write(outbuf);
            wtr.newLine();
            todo--;
            if (todo%20 == 0)
                System.out.println(todo);
            else
                System.out.print(".");
        }
        wtr.close();
        System.out.println();
    }

    private static void doRanks(File dir) throws IOException
    {
        System.out.println("Updating SixSwords ranks");
        File userDir = new File(dir, "users");
        File[] userFiles = userDir.listFiles();
        int todo = userFiles.length;
        List<CompUserBean> users = new ArrayList<>();
        for (File userFile : userFiles)
        {
            //System.out.println(userFile.getName());
            JSONObject json = JSONUtils.readJSON(userFile);
            CompUserBean user = new CompUserBean();
            user.fromJSON(json);
            if ((user.getTags() == null) || (user.getTags().indexOf("admin") < 0))
                users.add(user);
            todo--;
            if (todo%20 == 0)
                System.out.println(todo);
            else
                System.out.print(".");
        }
        System.out.println();
        // visits
        Collections.sort(users, new Comparator<CompUserBean>() {
            @Override
            public int compare(CompUserBean o1, CompUserBean o2)
            {
                return ResponseUtils.countList(o2.getVisitList()) - ResponseUtils.countList(o1.getVisitList());
            }
        });
        try { Thread.sleep(5000); } catch (Exception e) {}
        CommonIOLogic.setDataSecondaryValue("sixswords://rankings", "visit", toRankings(users));
        // total distance
        Collections.sort(users, new Comparator<CompUserBean>() {
            @Override
            public int compare(CompUserBean o1, CompUserBean o2)
            {
                return (int)Math.signum(o2.getTotalDistance() - o1.getTotalDistance());
            }
        });
        try { Thread.sleep(5000); } catch (Exception e) {}
        CommonIOLogic.setDataSecondaryValue("sixswords://rankings", "traveller", toRankings(users));
        // max distance
        Collections.sort(users, new Comparator<CompUserBean>() {
            @Override
            public int compare(CompUserBean o1, CompUserBean o2)
            {
                return (int)Math.signum(o2.getMaxDistance() - o1.getMaxDistance());
            }
        });
        try { Thread.sleep(5000); } catch (Exception e) {}
        CommonIOLogic.setDataSecondaryValue("sixswords://rankings", "ranger", toRankings(users));
        // gold
        Collections.sort(users, new Comparator<CompUserBean>() {
            @Override
            public int compare(CompUserBean o1, CompUserBean o2)
            {
                return (int)Math.signum(o2.getMaxGoldPieces() - o1.getMaxGoldPieces());
            }
        });
        try { Thread.sleep(5000); } catch (Exception e) {}
        CommonIOLogic.setDataSecondaryValue("sixswords://rankings", "banker", toRankings(users));
    }
    
    private static String toRankings(List<CompUserBean> users)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < users.size(); i++)
        {
            if (i == 100)
                break;
            if (sb.length() > 0)
                sb.append(";");
            sb.append(users.get(i).getURI());
        }
        return sb.toString();
    }

    public static void main(String[] argv) throws IOException
    {
        File dir = new File("D:\\temp\\data\\sixswords\\archive");
        boolean doDownload = true;
        for (int i = 0; i < argv.length; i++)
            if ("-dir".equals(argv[i]))
                dir = new File(argv[++i]);
            else if ("-download".equals(argv[i]))
                doDownload = false;
        if (doDownload)
            archive(dir);
        doStats(dir);
        doRanks(dir);
    }
}
