package Telematix.Moment;

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

import Telematix.App;
import org.joda.time.DateTime;
import ucar.unidata.geoloc.LatLonPointImpl;

public abstract class Moment implements MomentInterface {

    protected String csvLine;
    protected String county;
    protected DateTime datetime;
    protected DateTime utc;
    protected float lon;
    protected float lat;
    protected List<MomentValue> momentValues;

    public static List<MomentInterface> getMoments() throws IOException{

        String csvPath = App.getProperties().getProperty("inputPath").toString();

        List moments = new ArrayList<MomentInterface>();

        InputStream stream = null;
        BufferedReader reader = null;

        try {

            stream = App.class.getResourceAsStream(csvPath);
            reader = new BufferedReader(new InputStreamReader(stream));
            String line = "";

            while ((line = reader.readLine()) != null) {

                MomentInterface moment = Moment.getMoment(line);

                if (moment == null){
                    continue;
                }

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

    public static MomentInterface getMoment(String csvLine) throws IOException{

        // skip the header row (its the one with no colon)
        if (!csvLine.contains(":")){
            return null;
        }

        MomentInterface moment = null;

        if (MomentSchema.getSchema().equals(MomentSchema.VERISK)){
            moment = new VeriskMoment(csvLine);
        }
        else if (MomentSchema.getSchema().equals(MomentSchema.PROGRESSIVE)){
            moment = new ProgressiveMoment(csvLine);
        }

        return moment;
    }

    public String toString(){

        // "Latitude, Longitude, Time, UTC, rain_60min_value,rain_60min_uom, rain_60min_timestamp,snow_60min_value,snow_60min_uom,snow_60min_timestamp,ice_60min_value,ice_60min_uom,ice_60min_timestamp,rain_rate_value,rain_rate_uom,rain_rate_timestamp"
        String result = String.format("%s,%s", csvLine, utc.toString());
        //String result = String.format("%s, %s, %s, %f, %f, %s", county, csvLine.split(",")[1], datetime.toString(), lon, lat, utc.toString());

        // get the MomentValues for each variable
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

    protected MomentValue getMomentValue(String name){

        for (MomentValue momentValue : momentValues){

            if (momentValue.getVarName().equals(name)){
                return momentValue;
            }

        }

        return null;

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

    public LatLonPointImpl getLatLon(){
        return new LatLonPointImpl(lat, lon);
    }

}


