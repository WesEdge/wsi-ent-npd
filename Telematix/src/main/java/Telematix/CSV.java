package Telematix;

import Telematix.Moment.MomentInterface;

import java.io.IOException;
import java.util.List;
import java.io.FileWriter;

public class CSV {

    private String headerRow = "COUNTYNAME,DATETIME,DATE,LON,LAT,PK,UTC,rain_60min_value,rain_60min_uom, rain_60min_timestamp,snow_60min_value,snow_60min_uom,snow_60min_timestamp,ice_60min_value,ice_60min_uom,ice_60min_timestamp,rain_rate_value,rain_rate_uom,rain_rate_timestamp";
    private String csvPath;
    private List<MomentInterface> moments;
    private FileWriter writer;

    public CSV(List<MomentInterface> moments) throws IOException{

        try{

            this.moments = moments;
            this.csvPath = App.getProperties().getProperty("csvPath").toString();

            writer = new FileWriter(csvPath, false);

            // add the header row
            addRow(headerRow);

            for (MomentInterface moment : moments){
                String csvLine = moment.toString();
                addRow(csvLine);
            }

        }
        finally{
            if (null != writer){
                writer.flush();
                writer.close();
            }

        }

    }

    private void addRow(String row) throws IOException{
        writer.append(row);
        writer.append("\n");
    }


}
