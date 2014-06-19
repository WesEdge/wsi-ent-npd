package TelematixWU;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class App
{

    public static void main( String[] args )
    {

        try {

            // get input csv
            String csvPath = App.getProperties().getProperty("csvInputPath").toString();




        }catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void write(String msg){
        System.out.println(msg);
    }

    public static Properties getProperties() throws IOException {
        String resourceName = "config.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        InputStream resourceStream = loader.getResourceAsStream(resourceName);
        props.load(resourceStream);
        return props;
    }


}
