package gov.zndev.reviewlogclient.configs;

import gov.zndev.reviewlogclient.helpers.ConfigProperties;
import gov.zndev.reviewlogclient.helpers.Helper;
import gov.zndev.reviewlogclient.helpers.ResourceHelper;
import gov.zndev.reviewlogclient.models.User;
import gov.zndev.reviewlogclient.models.other.RepoInterface;
import gov.zndev.reviewlogclient.repositories.users.UsersRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Properties;


@Configuration
public class AppConfiguration {
    private static final Logger log = LoggerFactory.getLogger(AppConfiguration.class);
    private UsersRepo usersRepo;

    /*
        Automatically runs thanks to Spring boot
     */
    @PostConstruct
    private void init() {
        log.info("Running Configurations");

        // Initialize vars
        usersRepo = new UsersRepo();

        runConfiguration();
    }

    private void runConfiguration() {
        File configFile = new File("config.properties");
        try {
            if (configFile.exists()) {
                /*
                    Initializes system server, table row, etc.
                 */
                FileReader reader = new FileReader(configFile);
                Properties props = new Properties();
                props.load(reader);

                // Server Base URL
                String base_url = props.getProperty(ConfigProperties.BASE_URL_KEY);

                log.info("Base URL: " + base_url);
                ConfigProperties.BASE_URL = base_url;

                // Table Row Size

                int table_row_size = 0;
                try {
                    if (props.getProperty(ConfigProperties.TABLE_ROW_SIZE_KEY) != null) {
                        table_row_size = Integer.parseInt(props.getProperty(ConfigProperties.TABLE_ROW_SIZE_KEY));
                    } else {
                        table_row_size = ConfigProperties.DEFAULT_TABLE_ROW_SIZE;
                    }
                } catch (NumberFormatException ex) {
                    log.error("config.properties " + ConfigProperties.TABLE_ROW_SIZE_KEY + " cant be int.", ex);
                    table_row_size = ConfigProperties.DEFAULT_TABLE_ROW_SIZE;
                }
                ConfigProperties.TABLE_ROW_SIZE = table_row_size;


                reader.close();
            } else {
                // Create Default Configurations
                Properties props = new Properties();

                // Write Default Configs Here
                props.setProperty(ConfigProperties.BASE_URL_KEY, ConfigProperties.DEFAULT_BASE_URL);
                props.setProperty(ConfigProperties.TABLE_ROW_SIZE_KEY, "" + ConfigProperties.DEFAULT_TABLE_ROW_SIZE);

                // Saves creates config.properties file
                FileWriter writer = new FileWriter(configFile);
                props.store(writer, "Server Settings");
                writer.close();
            }
        } catch (FileNotFoundException ex) {
            // file does not exist
        } catch (IOException ex) {
            // I/O error
        }

    }
}
