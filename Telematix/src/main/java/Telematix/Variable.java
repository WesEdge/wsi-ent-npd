package Telematix;

import NetCDF.Wrapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.util.Properties;

public class Variable {

    private String rootDir;
    private String name;
    private int maxMinuteGap;
    private Wrapper lastNetCDF;
    private String secondaryNetCdfRootDir;

    public Variable(String name, String rootDir, int maxMinuteGap) throws IOException{
        this.name = name;
        this.rootDir = rootDir;
        this.maxMinuteGap = maxMinuteGap;

        if (null != App.getProperties().getProperty("secondaryNetCdfRootDir")){
            this.secondaryNetCdfRootDir = App.getProperties().getProperty("secondaryNetCdfRootDir").toString();
        }

    }

    public static List<Variable> getVariables() throws IOException{

        List<Variable> variables = new ArrayList<Variable>();

        Properties props = App.getProperties();

        int counter = 1;
        while (true){
            String propKey = String.format("var%01d", counter++);
            String propValue = props.getProperty(propKey);

            // are we done here?
            if (null == propValue){
                break;
            }

            String csvLine = propValue;
            String[] csvValues = csvLine.split(",");
            String name = csvValues[0];
            String rootDir = csvValues[1];
            int maxMinuteGap = Integer.valueOf(csvValues[2]);
            Variable var = new Variable(name, rootDir, maxMinuteGap);
            variables.add(var);

        }

        return variables;
    }

    public Wrapper getNetCDF(DateTime utc) throws Exception{

        File dateDir = this.getTargetDir(utc);

        // is this a valid directory?
        if ((null == dateDir) || (!dateDir.isDirectory())){

            if (null == this.secondaryNetCdfRootDir){
                throw new Exception(String.format("Directory does not exist..'%s'", dateDir));
            }

            // try the secondary root location, is this a valid directory?
            dateDir = this.getTargetDir(utc, secondaryNetCdfRootDir);
            if ((null == dateDir) || (!dateDir.isDirectory())){
                throw new Exception(String.format("Directory does not exist..'%s'", dateDir));
            }


        }

        // do any files exist in this directory?
        File[] files = dateDir.listFiles();
        if((files==null) || (files.length < 1)){
            throw new Exception(String.format("No files exist here..'%s'", dateDir));
        }

        // search the directory for the netcdf file
        File targetFile = this.getTargetFile(files, utc);

        if ((null == targetFile) || (!targetFile.exists())){
            throw new Exception(String.format("Target file not found, %s, '%s'", utc.toString(), dateDir));
        }

        // is this netcdf file opened and cached already?
        if ((null != lastNetCDF) && (targetFile.getPath().equals(lastNetCDF.getFile().getPath()))) {
            return lastNetCDF;
        }

        // open the netcdf file
        Wrapper targetNetcdf = Wrapper.open(targetFile, this.getName());

        // cache this to (potentially) eliminate processing on the next call
        lastNetCDF = targetNetcdf;

        return targetNetcdf;
    }

    private DateTime getFileDate(File file){

        // get the timestamp from the file name
        String fileTimestamp = this.getFileTimestamp(file);

        // get the file date
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd-HHmmss").withZone(DateTimeZone.UTC);
        DateTime fileTime = formatter.parseDateTime(fileTimestamp);

        return fileTime;
    }

    private File getTargetFile(File[] files, DateTime momentUTC){
        // find the netcdf file that is closest to the moment

        File targetFile = null;
        long targetFileMilliDiff = Long.MAX_VALUE;

        // for each file in the directory
        for (File file : files)
        {
            if (file.isDirectory())
            {
                // TODO: maybe search nested child directories here later if needed
            }
            else{
                String fileName = file.getName();

                if (!fileName.endsWith(".netcdf")){
                    continue;
                }

                DateTime fileDateTime = getFileDate(file);

                long milliDiff = Math.abs(fileDateTime.getMillis() - momentUTC.getMillis());

                // the duration in-between must be less than maxMinuteGap
                long maxMillis = maxMinuteGap * 60 * 1000;
                if (milliDiff > maxMillis){
                    continue;
                }

                // have we found a potential target file?
                if (milliDiff < targetFileMilliDiff){
                    targetFile = file;
                    targetFileMilliDiff = milliDiff;
                }

            }

        }

        return targetFile;

    }

    private File getTargetDir(DateTime utc, String rootDir){

        String formattedDate = this.getFormattedDate(utc);
        String targetDirPath = String.format("%s%s/%s", rootDir, formattedDate, this.name);

        File targetDir = new File(targetDirPath);

        return targetDir;

    }

    private File getTargetDir(DateTime utc){

        String formattedDate = this.getFormattedDate(utc);
        String targetDirPath = String.format("%s%s/%s", rootDir, formattedDate, this.name);

        File targetDir = new File(targetDirPath);

        return targetDir;

    }


    private String getFormattedDate(DateTime datetime){

        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
        String result = formatter.print(datetime);
        return result;

    }

    public String getFileTimestamp(File file){

        String [] parts = file.getName().split("\\|");
        String fileTimestamp = parts[parts.length-1];
        fileTimestamp = fileTimestamp.split("\\.")[0];

        return fileTimestamp;

    }

    public String getName(){
        return this.name;
    }

}
