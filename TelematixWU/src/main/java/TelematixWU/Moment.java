package TelematixWU;


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Moment {

    private String csvLine;
    private DateTime datetime;
    private DateTime utc;
    private float lon;
    private float lat;

    public Moment(String csvLine){

        String[] csvLineValues = csvLine.split(",");

        this.csvLine = csvLine;

        // we'll assume eastern timezone
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yy hh:mm:ss aa").withZone(DateTimeZone.forID("America/New_York"));
        datetime = formatter.parseDateTime(csvLineValues[1]);
        utc = datetime.toDateTime(DateTimeZone.forID("UTC"));

        this.lon = Float.parseFloat(csvLineValues[3]);
        this.lat = Float.parseFloat(csvLineValues[4]);

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
            Collections.sort(moments, new MomentComparator());

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

}