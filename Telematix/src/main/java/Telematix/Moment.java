package Telematix;

/**
 * Created with IntelliJ IDEA.
 * User: wedge
 * Date: 5/23/14
 * Time: 4:36 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import ucar.unidata.geoloc.LatLonPointImpl;

public class Moment {

    private String csvLine;
    private String county;
    private DateTime datetime;
    private DateTime utc;
    private float lon;
    private float lat;
    private List<MomentValue> momentValues;

    public Moment(String csvLine){

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

    public LatLonPointImpl getLatLon(){
        return new LatLonPointImpl(lat, lon);
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

    private MomentValue getMomentValue(String name){

        for (MomentValue momentValue : momentValues){

            if (momentValue.getVarName().equals(name)){
                return momentValue;
            }

        }

        return null;

    }

    public static List<Moment> getMoments(String csvPath){

        List moments = new ArrayList<Moment>();

        InputStream stream = null;
        BufferedReader reader = null;

        try {

            stream = App.class.getResourceAsStream(csvPath);
            reader = new BufferedReader(new InputStreamReader(stream));
            String line = "";

            while ((line = reader.readLine()) != null) {

                // skip the header row (its the one with no colon)
                if (!line.contains(":")){
                    continue;
                }

                Moment moment = new Moment(line);
                moments.add(moment);
            }

            // sort by date asc
            Collections.sort(moments,new MomentComparator());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return moments;

    }

    public DateTime getDatetime(){
        return this.datetime;
    }

    public DateTime getDatetimeUTC(){
        return this.utc;
    }

    public void addMomentValue(MomentValue momentValue){
        this.momentValues.add(momentValue);
    }
}


