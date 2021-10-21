package gov.zndev.reviewlogclient.controller.components;

import gov.zndev.reviewlogclient.helpers.AlertDialogHelper;
import gov.zndev.reviewlogclient.helpers.ConfigProperties;
import gov.zndev.reviewlogclient.helpers.Helper;
import gov.zndev.reviewlogclient.helpers.ResourceHelper;
import gov.zndev.reviewlogclient.models.other.RepoInterface;
import gov.zndev.reviewlogclient.repositories.system.SystemDataRepository;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

@Component
@FxmlView("configuration_settings_layout.fxml")
public class ConfigSettingsCtrl {

    private SystemDataRepository systemRepo;

    @FXML
    private TextField settings_address;

    @FXML
    public void initialize() {
        systemRepo = new SystemDataRepository();
        this.settings_address.setText(ConfigProperties.BASE_URL);
    }

    @FXML
    void onSaveConfigurations(ActionEvent event) {
        if (!settings_address.getText().equals("")) {
            String address = settings_address.getText();
            if (!address.endsWith("/")) {
                address = address + "/";
            }
            ConfigProperties.BASE_URL = address;
            try {
                ConfigProperties.UpdateConfigurations();
                AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Info", "Configuration Saved", null);
            } catch (IOException e) {
                e.printStackTrace();
                AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "Exception Occurred", e.getMessage());
            }
        } else {
            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "Field cannot be empty", "Default: "+ConfigProperties.DEFAULT_BASE_URL);
            settings_address.setText(ConfigProperties.BASE_URL);
        }
    }

    @FXML
    void onTestConnection(ActionEvent event) {
        if (!settings_address.getText().equals("")) {
            String address = settings_address.getText();
            if (!address.endsWith("/")) {
                address = address + "/";
            }
            systemRepo.testConnection(address, new RepoInterface() {
                @Override
                public void activityDone(Boolean success, String message, Object object) {
                    if (success) {
                        Platform.runLater(() -> {
                            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.INFORMATION, "System Info", message, null);
                        });

                    } else {
                        Platform.runLater(() -> {
                            AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Info", "Can't Connect to Server", message);
                        });
                    }
                }
            });

        } else {
            Platform.runLater(() -> {
                AlertDialogHelper.ShowSimpleAlertDialog(Alert.AlertType.ERROR, "System Error", "Field cannot be empty", "Default: http://localhost:8080/");
            });
        }
    }
}
