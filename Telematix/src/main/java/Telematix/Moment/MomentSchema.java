package Telematix.Moment;

import Telematix.App;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: wedge
 * Date: 8/5/14
 * Time: 3:57 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class MomentSchema {

    public static final String VERISK = "VERISK";
    public static final String PROGRESSIVE = "PROGRESSIVE";
    private static String schema = null;

    public static String getSchema() throws IOException{

        if (schema == null){
            schema = App.getProperties().getProperty("inputSchema");
        }

        return schema;
    }

}