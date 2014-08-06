package Telematix;

import Telematix.Moment.MomentInterface;

import java.io.IOException;
import java.util.List;
import java.io.FileWriter;

public class CSV {

    private String headerRow = null;
    private String csvPath;
    private List<MomentInterface> moments;
    private FileWriter writer;

    public CSV(List<MomentInterface> moments) throws IOException{

        try{

            this.moments = moments;
            this.csvPath = App.getProperties().getProperty("csvPath").toString();

            writer = new FileWriter(csvPath, false);

            for (MomentInterface moment : moments){

                if (headerRow == null){
                    // add the header row (this only happens once, on the first pass)
                    headerRow = moment.getHeaderRow();
                    addRow(headerRow);
                }

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
