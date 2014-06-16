
package NetCDF;

import ucar.nc2.NetcdfFile;
import Telematix.App;
import java.io.IOException;
import ucar.nc2.Variable;
import ucar.ma2.Array;
import ucar.unidata.geoloc.LatLonPoint;
import java.io.File;

public class Wrapper {

    private NetcdfFile netcdf = null;
    private String varName;
    private float[] grid;
    private int gridHeight;
    private int gridWidth;
    private float cornerLat;
    private float cornerLon;
    private float gridSpacingLat;
    private float gridSpacingLon;
    private Number missingData;
    private String unitOfMeasure;
    private File file;

    private Wrapper(File file, String varName){

        try {

            this.file = file;
            this.varName = varName;
            this.netcdf = NetcdfFile.open(file.getPath());

            this.init();
            this.buildGrid();

        } catch (IOException ioe) {
            App.write("IOException trying to open " + file.getPath());
            App.write(ioe.toString());
        }

    }

    private void init(){

        gridHeight = this.netcdf.findDimension("Lat").getLength();
        gridWidth = this.netcdf.findDimension("Lon").getLength();
        cornerLat = this.netcdf.findGlobalAttribute("Latitude").getNumericValue().floatValue();
        cornerLon = this.netcdf.findGlobalAttribute("Longitude").getNumericValue().floatValue();
        gridSpacingLat = this.netcdf.findGlobalAttribute("LatGridSpacing").getNumericValue().floatValue();
        gridSpacingLon = this.netcdf.findGlobalAttribute("LonGridSpacing").getNumericValue().floatValue();
        missingData = this.netcdf.findGlobalAttribute("MissingData").getNumericValue();
        unitOfMeasure = this.netcdf.findGlobalAttribute("Unit-value").getStringValue();

    }

    private void buildGrid() throws IOException{

        Array values = this.getData(this.varName);
        Array runLengths = this.getData("pixel_count");

        boolean w2Compressed = null != runLengths;

        // w2 compressed?
        if (w2Compressed){

            int gridLength = this.gridHeight * this.gridWidth;
            this.grid = new float[gridLength];

            int gridIndex = 0;
            int runLength;
            float value;

            for (int x = 0; x < values.getSize(); x++){

                runLength = runLengths.getInt(x);
                value = values.getFloat(x);

                for (int y = 0; y < runLength; y++){
                    this.grid[gridIndex++] = value;
                }

            }

        }
        else{

            this.grid = new float[(int) values.getSize()];

            for (int x = 0; x < values.getSize(); x++){
                this.grid[x] = values.getFloat(x);
            }

        }

    }

    public float getGridValue(LatLonPoint latlon) throws Exception{

        int xGridIndex = (int) Math.round(Math.abs(cornerLon - latlon.getLongitude()) / gridSpacingLon);
        int yGridIndex = (int) Math.round((cornerLat - latlon.getLatitude()) / gridSpacingLat);

        // get the first index of the correct row
        int arrayIndex = yGridIndex * this.gridWidth;

        // find the correct index in the row
        arrayIndex += xGridIndex;

        float value = this.grid[arrayIndex];
        return value;

    }

    private Array getData(String varName) throws IOException {

        Variable gridVar = this.netcdf.findVariable(varName);

        if (gridVar == null) {
            //App.write(String.format("Could not find grid variable '%s'", varName));
            return null;
        }

        Array data = gridVar.read().reduce();

        return data;
    }

    public float[] getGrid(){
        return this.grid;
    }

    public static Wrapper open(File file, String var){
        Wrapper wrapper = new Wrapper(file, var);
        return wrapper;
    }

    public void close() throws IOException{
        if (null != this.netcdf)
            this.netcdf.close();
    }

    public int getGridHeight(){
        return this.gridHeight;
    }

    public int getGridWidth(){
        return this.gridWidth;
    }

    public String getUnitOfMeasure(){
        return this.unitOfMeasure;
    }

    public float getMinValue(){

        float min = Float.MAX_VALUE;

        for (int x = 0; x < this.grid.length; x++){
            if (this.grid[x] < min){
                min = this.grid[x];
            }
        }

        return min;

    }

    public float getMaxValue(){

        float max = Float.MIN_VALUE;

        for (int x = 0; x < this.grid.length; x++){
            if (this.grid[x] > max){
                max = this.grid[x];
            }
        }

        return max;

    }

    public File getFile(){
        return this.file;
    }

}

