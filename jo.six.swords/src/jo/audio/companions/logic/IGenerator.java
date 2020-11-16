package jo.audio.companions.logic;

import jo.audio.companions.data.CoordBean;
import jo.audio.companions.data.DomainBean;
import jo.audio.companions.data.FeatureBean;
import jo.audio.companions.data.RegionBean;
import jo.audio.companions.data.SquareBean;

public interface IGenerator
{
    public static long CACHE_TIMEOUT = 15*60*1000L;
    
    public DomainBean getDomain(CoordBean oord);
    public RegionBean getRegion(CoordBean oord);
    public SquareBean getSquare(CoordBean ord);
    public FeatureBean getFeature(RegionBean region, SquareBean square, boolean athiest);
    public int getCloudCover(CoordBean ord, int time);
    public int getPrecipitation(CoordBean ord, int time);
    public String dumpCache();
    public void cleanup();
    public void clearCache();
}
