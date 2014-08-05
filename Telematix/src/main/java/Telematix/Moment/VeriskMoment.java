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

public class VeriskMoment extends Moment implements MomentInterface {


    public VeriskMoment(String csvLine){

        String[] csvLineValues = csvLine.split(",");

        this.csvLine = csvLine;
        this.county = csvLineValues[0];

        // we'll assume eastern timezone
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yy hh:mm:ss aa").withZone(DateTimeZone.forID("America/New_York"));
        datetime = formatter.parseDateTime(csvLineValues[1]);
        utc = datetime.toDateTime(DateTimeZone.forID("UTC"));

        this.lon = Float.parseFloat(csvLineValues[3]);
        this.lat = Float.parseFloat(csvLineValues[4]);

        this.momentValues = new ArrayList<MomentValue>();

    }

    public String toString(){

        // "COUNTYNAME,DATETIME,DATE,LON,LAT,PK,UTC,rain_60min_value,rain_60min_uom, rain_60min_timestamp,snow_60min_value,snow_60min_uom,snow_60min_timestamp,ice_60min_value,ice_60min_uom,ice_60min_timestamp,rain_rate_value,rain_rate_uom,rain_rate_timestamp"
        String result = String.format("%s,%s", csvLine, utc.toString());
        //String result = String.format("%s, %s, %s, %f, %f, %s", county, csvLine.split(",")[1], datetime.toString(), lon, lat, utc.toString());

        // get the MomentValues for each varaible
        MomentValue PrecipRate_radar_rain_Sum_60min = getMomentValue("PrecipRate_radar_rain_Sum_60min");
        MomentValue PrecipRate_radar_snow_Sum_60min = getMomentValue("PrecipRate_radar_snow_Sum_60min");
        MomentValue PrecipRate_radar_ice_Sum_60min = getMomentValue("PrecipRate_radar_ice_Sum_60min");
        MomentValue PrecipRate_radar_rain = getMomentValue("PrecipRate_radar_rain");

        String empty = ",,,";

        // get the csv portion for each variable
        result = String.format("%s%s", result, (PrecipRate_radar_rain_Sum_60min != null) ? PrecipRate_radar_rain_Sum_60min.toString() : empty);
        result = String.format("%s%s", result, (PrecipRate_radar_snow_Sum_60min != null) ? PrecipRate_radar_snow_Sum_60min.toString() : empty);
        result = String.format("%s%s", result, (PrecipRate_radar_ice_Sum_60min != null) ? PrecipRate_radar_ice_Sum_60min.toString() : empty);
        result = String.format("%s%s", result, (PrecipRate_radar_rain != null) ? PrecipRate_radar_rain.toString() : empty);

        return result;
    }

}


