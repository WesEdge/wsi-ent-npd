package Telematix.Moment;

import org.joda.time.DateTime;
import ucar.unidata.geoloc.LatLonPointImpl;

public interface MomentInterface {

    public LatLonPointImpl getLatLon();
    public String toString();
    public DateTime getDatetime();
    public DateTime getDatetimeUTC();
    public void addMomentValue(MomentValue momentValue);

}


