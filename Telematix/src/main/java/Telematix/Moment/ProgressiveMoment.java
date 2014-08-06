package Telematix.Moment;

/**
 * Created with IntelliJ IDEA.
 * User: wedge
 * Date: 5/23/14
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */

import java.util.ArrayList;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ProgressiveMoment extends Moment implements MomentInterface {

    public ProgressiveMoment(String csvLine){

        String[] csvLineValues = csvLine.split(",");

        this.csvLine = csvLine;

        this.lat = Float.parseFloat(csvLineValues[0]);
        this.lon = Float.parseFloat(csvLineValues[1]);

        // we'll assume eastern timezone
        //    7/10/13 0:00
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yy HH:mm").withZone(DateTimeZone.forID("America/New_York"));
        datetime = formatter.parseDateTime(csvLineValues[2]);
        utc = datetime.toDateTime(DateTimeZone.forID("UTC"));

        this.momentValues = new ArrayList<MomentValue>();

    }

    public String getHeaderRow(){
        return "Latitude,Longitude,Time,UTC,rain_60min_value,rain_60min_uom,rain_60min_timestamp,snow_60min_value,snow_60min_uom,snow_60min_timestamp,ice_60min_value,ice_60min_uom,ice_60min_timestamp,rain_rate_value,rain_rate_uom,rain_rate_timestamp";
    }

}



