package gov.zndev.reviewlogclient.configs;

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

                FileReader reader = new FileReader(configFile);
                Properties props = new Properties();
                props.load(reader);

                String base_url = props.getProperty("base_url");

                log.info("Base URL: " + base_url);
                ResourceHelper.BASE_URL = base_url;

                String editor_url = props.getProperty("editor_url");

                log.info("Editor URL: " + editor_url);
                ResourceHelper.EDITOR_URL = editor_url;
                reader.close();
            } else {
                // Create Default Configurations

                Properties props = new Properties();
                props.setProperty("base_url", "http://10.10.100.2:84/");
                props.setProperty("editor_url", "http://localhost/editor/ckeditor/samples/index.html");
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
