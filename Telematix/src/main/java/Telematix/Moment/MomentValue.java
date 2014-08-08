package Telematix.Moment;

/**
 * Created with IntelliJ IDEA.
 * User: wedge
 * Date: 6/10/14
 * Time: 5:13 PM
 * To change this template use File | Settings | File Templates.
 */

import Telematix.App;
import org.joda.time.DateTime;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MomentValue {

    private String varName;
    private String fileTimestamp;
    private float value;
    private String unitOfMeasure;
    private DateTime utc;
    private String cachedValue = null;

    public MomentValue(){

    }

    public MomentValue(String cachedValue, String varName){
        this.cachedValue = cachedValue;
        this.varName = varName;
    }

    public void setVarName(String varName){
        this.varName = varName;
    }

    public void setFileTimestamp(String fileTimestamp){
        this.fileTimestamp = fileTimestamp;
    }

    public void setValue(float value){
        this.value = value;
    }

    public void setUnitOfMeasure(String unitOfMeasure){
        this.unitOfMeasure = unitOfMeasure;
    }

    public void setUTC(DateTime utc){
        this.utc = utc;
    }

    public String toString(){

        if (this.cachedValue != null){
            return this.cachedValue;
        }

        return String.format(",%s,%s,%s", String.valueOf(value), unitOfMeasure, fileTimestamp);

        //return String.format(", %s, %s, %s, %s", varName, String.valueOf(value), unitOfMeasure, fileTimestamp);

        //return String.format("... %s | %s | %s %s ...", varName, fileTimestamp, String.valueOf(value), unitOfMeasure);
    }

    public void toProperties() throws IOException{

        String key = MomentValue.getCacheKey(varName, this.utc);

        String momentCacheFilePath = getMomentCacheFilePath();

        Properties properties = new Properties();
        properties.load(new FileInputStream(momentCacheFilePath));
        properties.put(key, this.toString());
        FileOutputStream output = new FileOutputStream(momentCacheFilePath);
        properties.store(output, "caching moment values to avoid netcdf processing next time");

    }

    private static Properties properties = null;

    public static MomentValue fromProperties(String varName, DateTime utc, boolean holdFirstLoadInMemory) throws FileNotFoundException, IOException{

        String key = MomentValue.getCacheKey(varName, utc);

        if ((!holdFirstLoadInMemory)||(null == properties)){
            properties = new Properties();
            properties.load(new FileInputStream(getMomentCacheFilePath()));
        }

        Object value = properties.get(key);

        if (value == null){
            return null;
        }

        MomentValue mv = new MomentValue(value.toString(), varName);

        return mv == null ? null : mv;
    }

    private static String getCacheKey(String varName, DateTime utc){
        String key = String.format("%s_%s", varName, utc.toString());
        return key;
    }

    private static String getMomentCacheFilePath() throws IOException{
        return App.getProperties().getProperty("momentCacheFilePath").toString();
    }

    public String getVarName(){
        return this.varName;
    }

}
