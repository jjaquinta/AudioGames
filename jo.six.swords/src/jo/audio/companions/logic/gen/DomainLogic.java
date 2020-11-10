package jo.audio.companions.logic.gen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.DemenseBean;
import jo.audio.companions.data.DomainBean;
import jo.audio.companions.data.RegionGenBean;
import jo.audio.companions.logic.CompConstLogic;
import jo.audio.companions.logic.DemenseLogic;
import jo.audio.companions.logic.NameLogic;
import jo.audio.companions.logic.feature.PantheonLogic;
import jo.audio.util.model.data.AudioMessageBean;

public class DomainLogic
{
    
    public static DomainBean generateDomain(CoordBean ord)
    {
        DomainBean domain = new DomainBean();
        domain.setOrds(new CoordBean(ord));
        Random rnd = CompConstLogic.getRandom(domain.getOrds());
        domain.setPredominantRace(CompConstLogic.roll(CompConstLogic.TABLE_RACE_IN_DOMAIN, rnd));
        domain.setGovernmentStructure(CompConstLogic.roll(CompConstLogic.TABLE_GOVERNMENT_IN_DOMAIN, rnd));
        if (domain.getGovernmentStructure() > CompConstLogic.GOVERNMENT_COUNTY)
            generateDomainFeudal(domain, rnd, domain.getGovernmentStructure());
        return domain;
    }
    
    private static void generateDomainFeudal(DomainBean domain, Random rnd, int max)
    {
        CoordBean center = new CoordBean(domain.getOrds().getX() + rnd.nextInt(CompConstLogic.SQUARES_PER_DOMAIN/2) + CompConstLogic.SQUARES_PER_DOMAIN/4,
                domain.getOrds().getY() + rnd.nextInt(CompConstLogic.SQUARES_PER_DOMAIN/2) + CompConstLogic.SQUARES_PER_DOMAIN/4,
                domain.getOrds().getZ());
        center.roundToNearest(CompConstLogic.SQUARES_PER_REGION);
        setRegionGovernment(domain, rnd, center, max, null);
    }
    
    private static void setRegionGovernment(DomainBean domain, Random rnd, CoordBean ord, int government, RegionGenBean liege)
    {
        RegionGenBean region = new RegionGenBean();
        region.setOrds(new CoordBean(ord));
        domain.setRegion(region.getOrds(), region);
        region.setGovernmentalStructure(government);
        region.setPredominantRace(domain.getPredominantRace());
        region.setName(NameLogic.kingdomName(rnd, region.getPredominantRace()));
        DemenseBean demense = new DemenseBean();
        demense.setID(ord.toString());
        demense.setName(new AudioMessageBean(region.getName()));
        region.setLiege(demense);
        if (liege != null)
        {
            region.setLord(liege);
            liege.getVassals().add(region);
            demense.setLiegeID(liege.getLiege().getID());
        }
        switch (region.getPredominantRace())
        {
            case CompConstLogic.RACE_DWARF:
                demense.setLanguage("DW");
                demense.setPantheon(PantheonLogic.DWARVISH);
                break;
            case CompConstLogic.RACE_ELF:
                demense.setLanguage("ELF");
                demense.setPantheon(PantheonLogic.ELVISH);
                break;
        }
        DemenseLogic.register(demense);
        if (government == CompConstLogic.GOVERNMENT_COUNTY)
            return;
        for (int i = rnd.nextInt(3); i >= 0; i--)
        {
            CoordBean vassalOrd = findAdjacent(domain, ord, rnd);
            if (vassalOrd == null)
                break;
            setRegionGovernment(domain, rnd, vassalOrd, government - 1, region);
        }
    }
    
    private static CoordBean findAdjacent(DomainBean domain, CoordBean center, Random rnd)
    {
        List<CoordBean> candidates = new ArrayList<>();
        candidates.add(new CoordBean(center.getX() - CompConstLogic.SQUARES_PER_REGION, center.getY(), center.getZ()));
        candidates.add(new CoordBean(center.getX() + CompConstLogic.SQUARES_PER_REGION, center.getY(), center.getZ()));
        candidates.add(new CoordBean(center.getX(), center.getY() - CompConstLogic.SQUARES_PER_REGION, center.getZ()));
        candidates.add(new CoordBean(center.getX(), center.getY() + CompConstLogic.SQUARES_PER_REGION, center.getZ()));
        for (Iterator<CoordBean> i = candidates.iterator(); i.hasNext(); )
        {
            CoordBean tst = i.next();
            if (!domain.contains(tst))
                i.remove();
            else if (domain.getRegion(tst) != null)
                i.remove();
        }
        if (candidates.size() == 0)
            return null;
        return candidates.get(rnd.nextInt(candidates.size()));
    }


}
