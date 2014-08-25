package Telematix;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import Telematix.Moment.MomentInterface;
import Telematix.Moment.Moment;
import Telematix.Moment.MomentValue;
import ucar.unidata.geoloc.LatLonPoint;
import org.joda.time.DateTime;
import java.util.Properties;
import java.text.DecimalFormat;
import java.io.InputStream;

public class App 
{

    public static void main( String[] args )
    {

        List<String> missingTimes = new ArrayList<String>();

        try {

            // these are the netcdf variables we need
            List<Variable> variables = Variable.getVariables();

            // load the records from the sample trip into a list, sorted by date
            List<MomentInterface> moments = Moment.getMoments();

            int counter = 0;

            boolean allDataIsCached = "true".equals(getProperties().getProperty("allDataIsCached"));

            for (MomentInterface moment : moments){

                String percentComplete = (new DecimalFormat("#.##")).format((counter++ / (double) moments.size()) * 100);

                write(String.format("%s%% complete - begin processing moment.. %s", percentComplete, moment.toString()));

                DateTime utc = moment.getDatetimeUTC();

                if(missingTimes.contains(utc.toString())){
                    write(String.format("Date Directory does not exist.. %s", utc.toString()));
                    continue;  // skip this date if we've already found that it's missing
                }

                // for each netcdf variable
                for (Variable variable : variables){

                    try{

                        LatLonPoint latlon = moment.getLatLon();

                        // is this value already saved? (to avoid netcdf processing time again later)
                        //MomentValue momentValue = MomentValue.fromProperties(variable.getName(), moment.getDatetimeUTC(), allDataIsCached);
                        MomentValue momentValue = null;

                        if (null != momentValue){
                            moment.addMomentValue(momentValue);
                            continue;
                        }

                        // get the netcdf file from the system (if it exists)
                        NetCDF.Wrapper netcdf = variable.getNetCDF(utc);

                        // a visual test of the netcdf grid
                        //PNG.netCDFtoPNG(netcdf, "/Users/wedge/NetCDF/netcdf.png");

                        // make a point request
                        float val = netcdf.getGridValue(latlon);

                        // update the moment with the new value
                        momentValue = new MomentValue();
                        momentValue.setVarName(variable.getName());
                        momentValue.setFileTimestamp(variable.getFileTimestamp(netcdf.getFile()));
                        momentValue.setValue(val);
                        momentValue.setUnitOfMeasure(netcdf.getUnitOfMeasure());
                        momentValue.setUTC(utc);

                        moment.addMomentValue(momentValue);

                        // save this value (to avoid netcdf processing time again later)
                        //momentValue.toProperties();

                    }catch (Exception e) {
                        //e.printStackTrace();
                        App.write(e.getMessage());
                        missingTimes.add(moment.getDatetimeUTC().toString());
                        break;  //don't bother checking for the other variables if the date doesn't exist
                    }

                }

            }

            new CSV(moments);   // write to csv file
            writeMoments(moments);  // write to console

        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void write(String msg){
        System.out.println(msg);
    }

    public static void writeMoments(List<MomentInterface> moments){
        // writes moments to console

        for(Object moment: moments){
            App.write(moment.toString());
        }

    }

    public static Properties props = null;
    public static Properties getProperties() throws IOException{
        if (null != props) { return props; }
        String resourceName = "config.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        props = new Properties();
        InputStream resourceStream = loader.getResourceAsStream(resourceName);
        props.load(resourceStream);
        return props;
    }

}
