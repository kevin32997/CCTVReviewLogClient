package gov.zndev.reviewlogclient.helpers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigProperties {



    // SERVER URL (Rest Api Connections)
    public static final String BASE_URL_KEY="base_url";
    public static String BASE_URL = "http://10.10.100.2:84/";
    public static String DEFAULT_BASE_URL = "http://10.10.100.2:84/";

    // TABLE ROW SIZE
    public static final String TABLE_ROW_SIZE_KEY="table_row_size";
    public static int TABLE_ROW_SIZE = 30;
    public static int DEFAULT_TABLE_ROW_SIZE = 30;


    /*
        Updates config.properties file data to ResourceHelper variables data
     */
    public static final void UpdateConfigurations() throws IOException {
        File configFile = new File("config.properties");
        Properties props = new Properties();

        // Update Here
        props.setProperty(BASE_URL_KEY, BASE_URL);
        props.setProperty(TABLE_ROW_SIZE_KEY,""+TABLE_ROW_SIZE);


        FileWriter writer = new FileWriter(configFile);
        props.store(writer, "Server Settings");
        writer.close();
    }


}
